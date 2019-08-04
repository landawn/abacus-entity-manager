/*
 * Copyright (c) 2015, Haiyang Li.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.core.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.core.EntityManagerUtil;
import com.landawn.abacus.core.RowDataSet;
import com.landawn.abacus.core.sql.command.SQLCommand;
import com.landawn.abacus.core.sql.command.SQLOperationCommand;
import com.landawn.abacus.dataSource.SQLDataSource;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.type.ObjectType;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.WD;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
public class SQLResult {
    private static final Logger logger = LoggerFactory.getLogger(SQLResult.class);

    private static final Map<String, Map<String, Map<List<String>, ResultInfo>>> resultInfoPool = new HashMap<>();

    protected final Executant executant;
    protected final SQLOperationCommand sqlCmd;
    protected final Map<String, Object> options;

    protected final SQLDataSource ds;
    protected final Statement stmt;
    protected final ResultSet rs;

    protected final ResultInfo resultInfo;

    private final long executionTime;
    private final int updateCount;
    private final List<Object> generatedKeys;

    private int size = -1;

    private boolean isClosed = false;

    public SQLResult(Executant executant, SQLOperationCommand sqlCmd, Map<String, Object> options, SQLDataSource ds, Statement stmt, ResultSet rs,
            long executionTime) {
        this(executant, sqlCmd, options, ds, stmt, rs, executionTime, 0, null);
    }

    public SQLResult(Executant executant, SQLOperationCommand sqlCmd, long executionTime, int updateCount) {
        this(executant, sqlCmd, null, null, null, null, executionTime, updateCount, null);
    }

    public SQLResult(Executant executant, SQLOperationCommand sqlCmd, long executionTime, int updateCount, List<Object> generatedKeys) {
        this(executant, sqlCmd, null, null, null, null, executionTime, updateCount, generatedKeys);
    }

    SQLResult(Executant executant, SQLOperationCommand sqlCmd, Map<String, Object> options, SQLDataSource ds, Statement stmt, ResultSet rs, long executionTime,
            int updateCount, List<Object> generatedKeys) {
        this.executant = executant;
        this.sqlCmd = sqlCmd;
        this.options = options;

        this.ds = ds;
        this.stmt = stmt;
        this.rs = rs;

        this.executionTime = executionTime;
        this.updateCount = updateCount;
        this.generatedKeys = generatedKeys == null ? N.emptyList() : generatedKeys;

        if (rs == null) {
            this.resultInfo = null;
        } else {
            this.resultInfo = getResultInfo(sqlCmd, rs);
        }
    }

    public Executant getExecutant() {
        return executant;
    }

    public SQLCommand getSQLCommand() {
        return sqlCmd;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getUpateCount() {
        return updateCount;
    }

    public List<Object> getGeneratedKeys() {
        return generatedKeys;
    }

    public List<String> getPropNameList() {
        return resultInfo.propNames;
    }

    public String getPropName(int propIndex) {
        return resultInfo.propNames.get(propIndex);
    }

    public int getPropIndex(String propName) {
        Integer index = resultInfo.propIndexes.get(propName);

        if (index == null) {
            throw new IllegalArgumentException("The result set " + getPropNameList() + " doesn't contain property[" + propName + "]. ");
        }

        return index;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int propIndex) throws SQLException {
        return (T) resultInfo.propTypes[propIndex].get(rs, propIndex + 1);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String propName) throws SQLException {
        return (T) get(getPropIndex(propName));
    }

    public boolean absolute(int row) throws SQLException {
        boolean result = rs.absolute(row + 1);

        if (result) {
        } else {
            rs.previous();
        }

        return result;
    }

    public boolean next() throws SQLException {
        return rs.next();
    }

    public int getCurrentRowNum() throws SQLException {
        return rs.getRow();
    }

    public DataSet getResultSet(Collection<String> selectPropNames, Map<String, Object> options) throws UncheckedSQLException {
        assertNotClosed();

        try {
            return createResultSet(selectPropNames, options);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public int size() {
        assertNotClosed();

        synchronized (this) {
            if (size < 0) {
                size = getExecutant().executeCount(sqlCmd, options);
            }

            return size;
        }
    }

    public void close() {
        if (isClosed) {
            return;
        }

        synchronized (this) {
            try {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("Failed to close ResultSet", e);
                    }
                }
            } finally {
                if (stmt != null) {
                    executant.closeStatement(ds, stmt, getOptions());
                }
            }
        }

        isClosed = true;
    }

    public boolean isClosed() {
        return isClosed;
    }

    private void assertNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("SQL result has been closed");
        }
    }

    private DataSet createResultSet(Collection<String> selectPropNames, Map<String, Object> options) throws SQLException {
        if (selectPropNames == null) {
            selectPropNames = resultInfo.propNames;
        } else {
            if (!resultInfo.propNames.containsAll(selectPropNames)) {
                List<String> temp = new ArrayList<>(selectPropNames);
                temp.removeAll(resultInfo.propNames);

                for (String propName : temp) {
                    if (!(propName.trim().endsWith(WD.ASTERISK))) {
                        throw new IllegalArgumentException("The resultSet doesn't include all request property: " + selectPropNames
                                + ". The property in the result is: " + resultInfo.propNames);
                    }
                }
            }
        }

        final int propCount = selectPropNames.size();
        final List<List<Object>> columnList = new ArrayList<>(propCount);

        for (int i = 0; i < propCount; i++) {
            columnList.add(new ArrayList<Object>());
        }

        int offset = EntityManagerUtil.getOffset(options);
        int count = EntityManagerUtil.getCount(options);

        if (count > 0) {
            int[] selectPropIndexTable = new int[propCount];

            int arrayIndex = 0;

            for (String propName : selectPropNames) {
                selectPropIndexTable[arrayIndex++] = getPropIndex(propName);
            }

            synchronized (this) {
                if (absolute(offset)) {
                    do {
                        for (int i = 0; i < propCount; i++) {
                            columnList.get(i).add(get(selectPropIndexTable[i]));
                        }

                        count--;
                    } while ((count > 0) && rs.next());
                }
            }
        }

        return new RowDataSet(new ArrayList<>(selectPropNames), columnList);
    }

    private ResultInfo getResultInfo(SQLOperationCommand sqlCmd, ResultSet rs) throws UncheckedSQLException {
        final EntityDefinition entityDef = sqlCmd.getEntityDef();
        final String domainName = (entityDef.getFactory() == null) ? N.EMPTY_STRING : entityDef.getFactory().domainName();
        final String entityName = entityDef.getName();
        final Collection<String> selectedPropNames = sqlCmd.getTargetPropNames();

        ResultInfo resultInfo = null;

        synchronized (resultInfoPool) {
            Map<String, Map<List<String>, ResultInfo>> entityResultInfoMap = resultInfoPool.get(domainName);

            if (entityResultInfoMap == null) {
                entityResultInfoMap = new HashMap<>();
                resultInfoPool.put(domainName, entityResultInfoMap);
            }

            Map<List<String>, ResultInfo> resultInfoMap = entityResultInfoMap.get(entityName);

            if (resultInfoMap == null) {
                resultInfoMap = new HashMap<>();
                entityResultInfoMap.put(entityName, resultInfoMap);
            } else {
                resultInfo = resultInfoMap.get(selectedPropNames);
            }

            if (resultInfo == null) {
                int columnCount = 0;
                List<String> columnLabelList = null;

                try {
                    final ResultSetMetaData rsmd = rs.getMetaData();

                    columnCount = rsmd.getColumnCount();
                    columnLabelList = new ArrayList<>(columnCount);

                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        columnLabelList.add(JdbcUtil.getColumnLabel(rsmd, columnIndex));
                    }
                } catch (SQLException e) {
                    throw new UncheckedSQLException(e);
                }

                final Collection<String> resultPropNames = selectedPropNames == null || selectedPropNames.size() != columnCount ? columnLabelList
                        : selectedPropNames;

                final List<String> propNames = new ArrayList<>(columnCount);
                final Map<String, Integer> propIndexes = new HashMap<>(columnCount * 2);
                final Type<Object>[] propTypes = new Type[columnCount];

                final Iterator<String> it = resultPropNames.iterator();
                Property prop = null;
                String propName = null;

                for (int index = 0; index < columnCount; index++) {
                    propName = it.next();
                    prop = entityDef.getProperty(propName);

                    if ((prop == null) && (resultPropNames != selectedPropNames)) {
                        for (Property e : entityDef.getPropertyList()) {
                            if (propName.equalsIgnoreCase(e.getColumnName())) {
                                propName = e.getName();
                                prop = e;

                                break;
                            }
                        }
                    }

                    propNames.add(propName);

                    propIndexes.put(propName, index);

                    if (prop == null) {
                        propTypes[index] = TypeFactory.getType(ObjectType.OBJECT);
                    } else {
                        propTypes[index] = prop.getType();
                    }
                }

                resultInfo = new ResultInfo(propNames, propIndexes, propTypes);

                if (selectedPropNames == null) {
                    resultInfoPool.get(domainName).get(entityName).put(null, resultInfo);
                } else {
                    resultInfoPool.get(domainName).get(entityName).put(new ArrayList<>(selectedPropNames), resultInfo);
                }
            }
        }

        return resultInfo;
    }

    private static class ResultInfo {
        final List<String> propNames;
        final Map<String, Integer> propIndexes;
        final Type<Object>[] propTypes;

        ResultInfo(final List<String> propNames, final Map<String, Integer> propIndexes, final Type<Object>[] propTypes) {
            this.propNames = propNames;
            this.propIndexes = propIndexes;
            this.propTypes = propTypes;
        }
    }
}

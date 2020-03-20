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

package com.landawn.abacus.core;

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
import com.landawn.abacus.core.command.SQLCommand;
import com.landawn.abacus.core.command.SQLOperationCommand;
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

// TODO: Auto-generated Javadoc
/**
 * The Class SQLResult.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
public class SQLResult {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SQLResult.class);

    /** The Constant resultInfoPool. */
    private static final Map<String, Map<String, Map<List<String>, ResultInfo>>> resultInfoPool = new HashMap<>();

    /** The executant. */
    protected final Executant executant;

    /** The sql cmd. */
    protected final SQLOperationCommand sqlCmd;

    /** The options. */
    protected final Map<String, Object> options;

    /** The ds. */
    protected final SQLDataSource ds;

    /** The stmt. */
    protected final Statement stmt;

    /** The rs. */
    protected final ResultSet rs;

    /** The result info. */
    protected final ResultInfo resultInfo;

    /** The execution time. */
    private final long executionTime;

    /** The update count. */
    private final int updateCount;

    /** The generated keys. */
    private final List<Object> generatedKeys;

    /** The size. */
    private int size = -1;

    /** The is closed. */
    private boolean isClosed = false;

    /**
     * Instantiates a new SQL result.
     *
     * @param executant
     * @param sqlCmd
     * @param options
     * @param ds
     * @param stmt
     * @param rs
     * @param executionTime
     */
    public SQLResult(Executant executant, SQLOperationCommand sqlCmd, Map<String, Object> options, SQLDataSource ds, Statement stmt, ResultSet rs,
            long executionTime) {
        this(executant, sqlCmd, options, ds, stmt, rs, executionTime, 0, null);
    }

    /**
     * Instantiates a new SQL result.
     *
     * @param executant
     * @param sqlCmd
     * @param executionTime
     * @param updateCount
     */
    public SQLResult(Executant executant, SQLOperationCommand sqlCmd, long executionTime, int updateCount) {
        this(executant, sqlCmd, null, null, null, null, executionTime, updateCount, null);
    }

    /**
     * Instantiates a new SQL result.
     *
     * @param executant
     * @param sqlCmd
     * @param executionTime
     * @param updateCount
     * @param generatedKeys
     */
    public SQLResult(Executant executant, SQLOperationCommand sqlCmd, long executionTime, int updateCount, List<Object> generatedKeys) {
        this(executant, sqlCmd, null, null, null, null, executionTime, updateCount, generatedKeys);
    }

    /**
     * Instantiates a new SQL result.
     *
     * @param executant
     * @param sqlCmd
     * @param options
     * @param ds
     * @param stmt
     * @param rs
     * @param executionTime
     * @param updateCount
     * @param generatedKeys
     */
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

    /**
     * Gets the executant.
     *
     * @return
     */
    public Executant getExecutant() {
        return executant;
    }

    /**
     * Gets the SQL command.
     *
     * @return
     */
    public SQLCommand getSQLCommand() {
        return sqlCmd;
    }

    /**
     * Gets the options.
     *
     * @return
     */
    public Map<String, Object> getOptions() {
        return options;
    }

    /**
     * Gets the execution time.
     *
     * @return
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Gets the upate count.
     *
     * @return
     */
    public int getUpateCount() {
        return updateCount;
    }

    /**
     * Gets the generated keys.
     *
     * @return
     */
    public List<Object> getGeneratedKeys() {
        return generatedKeys;
    }

    /**
     * Gets the prop name list.
     *
     * @return
     */
    public List<String> getPropNameList() {
        return resultInfo.propNames;
    }

    /**
     * Gets the prop name.
     *
     * @param propIndex
     * @return
     */
    public String getPropName(int propIndex) {
        return resultInfo.propNames.get(propIndex);
    }

    /**
     * Gets the prop index.
     *
     * @param propName
     * @return
     */
    public int getPropIndex(String propName) {
        Integer index = resultInfo.propIndexes.get(propName);

        if (index == null) {
            throw new IllegalArgumentException("The result set " + getPropNameList() + " doesn't contain property[" + propName + "]. ");
        }

        return index;
    }

    /**
     *
     * @param <T>
     * @param propIndex
     * @return
     * @throws SQLException the SQL exception
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int propIndex) throws SQLException {
        return (T) resultInfo.propTypes[propIndex].get(rs, propIndex + 1);
    }

    /**
     *
     * @param <T>
     * @param propName
     * @return
     * @throws SQLException the SQL exception
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String propName) throws SQLException {
        return (T) get(getPropIndex(propName));
    }

    /**
     *
     * @param row
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    public boolean absolute(int row) throws SQLException {
        boolean result = rs.absolute(row + 1);

        if (result) {
        } else {
            rs.previous();
        }

        return result;
    }

    /**
     *
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    public boolean next() throws SQLException {
        return rs.next();
    }

    /**
     * Gets the current row num.
     *
     * @return
     * @throws SQLException the SQL exception
     */
    public int getCurrentRowNum() throws SQLException {
        return rs.getRow();
    }

    /**
     * Gets the result set.
     *
     * @param selectPropNames
     * @param options
     * @return
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    public DataSet getResultSet(Collection<String> selectPropNames, Map<String, Object> options) throws UncheckedSQLException {
        assertNotClosed();

        try {
            return createResultSet(selectPropNames, options);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    /**
     *
     * @return
     */
    public int size() {
        assertNotClosed();

        synchronized (this) {
            if (size < 0) {
                size = getExecutant().executeCount(sqlCmd, options);
            }

            return size;
        }
    }

    /**
     * Close.
     */
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

    /**
     * Checks if is closed.
     *
     * @return true, if is closed
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Assert not closed.
     */
    private void assertNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("SQL result has been closed");
        }
    }

    /**
     * Creates the result set.
     *
     * @param selectPropNames
     * @param options
     * @return
     * @throws SQLException the SQL exception
     */
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

    /**
     * Gets the result info.
     *
     * @param sqlCmd
     * @param rs
     * @return
     * @throws UncheckedSQLException the unchecked SQL exception
     */
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

    /**
     * The Class ResultInfo.
     */
    private static class ResultInfo {

        /** The prop names. */
        final List<String> propNames;

        /** The prop indexes. */
        final Map<String, Integer> propIndexes;

        /** The prop types. */
        final Type<Object>[] propTypes;

        /**
         * Instantiates a new result info.
         *
         * @param propNames
         * @param propIndexes
         * @param propTypes
         */
        ResultInfo(final List<String> propNames, final Map<String, Integer> propIndexes, final Type<Object>[] propTypes) {
            this.propNames = propNames;
            this.propIndexes = propIndexes;
            this.propTypes = propTypes;
        }
    }
}

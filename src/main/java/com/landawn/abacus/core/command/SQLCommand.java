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

package com.landawn.abacus.core.command;

import static com.landawn.abacus.util.WD.COMMA_SPACE;
import static com.landawn.abacus.util.WD._BRACE_L;
import static com.landawn.abacus.util.WD._BRACE_R;
import static com.landawn.abacus.util.WD._EQUAL;
import static com.landawn.abacus.util.WD._SPACE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLCommand extends AbstractCommand {

    private static final int INIT_SIZE = 9;

    private Set<String> targetTables = N.newHashSet();

    private Set<String> subQueryTables = N.newHashSet();

    protected Object[] parameterValues = new Object[INIT_SIZE];

    protected Type<Object>[] parameterTypes = new Type[INIT_SIZE];

    protected int parameterCount;

    protected String sql = N.EMPTY_STRING;

    /**
     * Gets the parameters.
     *
     * @return
     */
    @Override
    public Object[] getParameters() {
        return parameterValues;
    }

    /**
     * Gets the parameter types.
     *
     * @return
     */
    @Override
    public Type<Object>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Gets the parameter.
     *
     * @param index
     * @return
     */
    @Override
    public Object getParameter(int index) {
        return parameterValues[index];
    }

    /**
     * Gets the parameter type.
     *
     * @param index
     * @return
     */
    @Override
    public Type<Object> getParameterType(int index) {
        return parameterTypes[index];
    }

    /**
     * Sets the parameter.
     *
     * @param index
     * @param value
     * @param type
     */
    @Override
    public void setParameter(int index, Object value, Type<Object> type) {
        if ((index < 0) || (index > (parameterCount))) {
            throw new IllegalArgumentException("Parameter index[" + index + "] must not less than zero and can't skip. ");
        }

        internalSetParameter(index, value);
        internalSetParameterType(index, type);
    }

    /**
     * Gets the parameter.
     *
     * @param parameterName
     * @return
     */
    @Override
    public Object getParameter(String parameterName) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Gets the parameter type.
     *
     * @param parameterName
     * @return
     */
    @Override
    public Type<Object> getParameterType(String parameterName) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Sets the parameter.
     *
     * @param parameterName
     * @param value
     * @param type
     */
    @Override
    public void setParameter(String parameterName, Object value, Type<Object> type) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     *
     * @param sqlCmd
     */
    public void appendParameters(SQLCommand sqlCmd) {
        appendParameters(sqlCmd, 0, sqlCmd.parameterCount);
    }

    /**
     *
     * @param sqlCmd
     * @param fromIndex
     * @param toIndex
     */
    public void appendParameters(SQLCommand sqlCmd, int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            internalSetParameter(parameterCount, sqlCmd.parameterValues[i], sqlCmd.parameterTypes[i]);
        }
    }

    /**
     * Internal set parameter.
     *
     * @param index
     * @param value
     * @param type
     */
    protected void internalSetParameter(int index, Object value, Type<Object> type) {
        internalSetParameter(index, value);
        internalSetParameterType(index, type);
    }

    /**
     * Internal set parameter.
     *
     * @param index
     * @param value
     */
    protected void internalSetParameter(int index, Object value) {
        if (index >= parameterValues.length) {
            parameterValues = N.copyOf(parameterValues, (int) (index * (1.25)));
        }

        parameterValues[index] = value;
        parameterCount++;
    }

    /**
     * Internal set parameter type.
     *
     * @param index
     * @param type
     */
    protected void internalSetParameterType(int index, Type<Object> type) {
        if (index >= parameterTypes.length) {
            parameterTypes = N.copyOf(parameterTypes, (int) (index * (1.25)));
        }

        parameterTypes[index] = type;
    }

    /**
     * Clear parameters.
     */
    @Override
    public void clearParameters() {
        Arrays.fill(parameterValues, null);
        // Arrays.fill(parameterTypes, TypeFactory.NULL);
        parameterCount = 0;
    }

    /**
     * Gets the parameter count.
     *
     * @return
     */
    @Override
    public int getParameterCount() {
        return parameterCount;
    }

    /**
     * Gets the sql.
     *
     * @return
     */
    public String getSql() {
        return sql;
    }

    /**
     * Sets the sql.
     *
     * @param sql the new sql
     */
    public void setSql(String sql) {
        this.sql = (sql != null) ? sql : N.EMPTY_STRING;
    }

    /**
     * Gets the target tables.
     *
     * @return
     */
    public Set<String> getTargetTables() {
        return targetTables;
    }

    /**
     * Adds the target table.
     *
     * @param tableName
     */
    public void addTargetTable(String tableName) {
        targetTables.add(tableName);
    }

    /**
     * Adds the target tables.
     *
     * @param tableNames
     */
    public void addTargetTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            targetTables.addAll(tableNames);
        }
    }

    /**
     * Removes the target table.
     *
     * @param tableName
     */
    public void removeTargetTable(String tableName) {
        targetTables.remove(tableName);
    }

    /**
     * Removes the target tables.
     *
     * @param tableNames
     */
    public void removeTargetTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            N.removeAll(targetTables, tableNames);
        }
    }

    /**
     * Clear target table.
     */
    public void clearTargetTable() {
        targetTables.clear();
    }

    /**
     * Gets the sub query tables.
     *
     * @return
     */
    public Set<String> getSubQueryTables() {
        return subQueryTables;
    }

    /**
     * Adds the sub query table.
     *
     * @param tableName
     */
    public void addSubQueryTable(String tableName) {
        subQueryTables.add(tableName);
    }

    /**
     * Adds the sub query tables.
     *
     * @param tableNames
     */
    public void addSubQueryTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            subQueryTables.addAll(tableNames);
        }
    }

    /**
     * Removes the sub query table.
     *
     * @param tableName
     */
    public void removeSubQueryTable(String tableName) {
        subQueryTables.remove(tableName);
    }

    /**
     * Removes the sub query tables.
     *
     * @param tableNames
     */
    public void removeSubQueryTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            N.removeAll(subQueryTables, tableNames);
        }
    }

    /**
     * Clear sub query table.
     */
    public void clearSubQueryTable() {
        subQueryTables.clear();
    }

    /**
     *
     * @param sqlCmd
     */
    public void combine(SQLCommand sqlCmd) {
        sql += (_SPACE + sqlCmd.sql);
        addTargetTables(sqlCmd.targetTables);
        addSubQueryTables(sqlCmd.subQueryTables);
        appendParameters(sqlCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        SQLCommand copy = (SQLCommand) super.clone();

        copy.targetTables = N.newHashSet(targetTables);
        copy.subQueryTables = N.newHashSet(subQueryTables);
        copy.parameterValues = N.copyOfRange(parameterValues, 0, parameterValues.length);
        copy.parameterTypes = N.copyOfRange(parameterTypes, 0, parameterTypes.length);

        return copy;
    }

    /**
     * Clear.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        sql = N.EMPTY_STRING;

        targetTables.clear();
        subQueryTables.clear();

        if (parameterValues.length > INIT_SIZE) {
            parameterValues = new Object[INIT_SIZE];
        } else {
            Arrays.fill(parameterValues, null);
        }

        if (parameterTypes.length > INIT_SIZE) {
            parameterTypes = new Type[INIT_SIZE];
        } else {
            Arrays.fill(parameterTypes, null);
        }

        parameterCount = 0;
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = (h * 31) + sql.hashCode();
        h = (h * 31) + N.hashCode(parameterValues);

        return h;
    }

    /**
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SQLCommand) {
            SQLCommand other = (SQLCommand) obj;

            return N.equals(sql, other.sql) && N.equals(parameterCount, other.parameterCount) && N.equals(parameterValues, other.parameterValues);
        }

        return false;
    }

    @Override
    public String toString() {
        if (parameterCount == 0) {
            return sql;
        } else {
            final StringBuilder sb = Objectory.createStringBuilder();

            try {
                sb.append(sql);
                sb.append(_SPACE);
                sb.append(_BRACE_L);

                for (int i = 0; i < parameterCount; i++) {
                    if (i > 0) {
                        sb.append(COMMA_SPACE);
                    }

                    sb.append(i + 1);
                    sb.append(_EQUAL);
                    sb.append(parameterValues[i]);
                }

                sb.append(_BRACE_R);

                return sb.toString();

            } finally {
                Objectory.recycle(sb);
            }
        }
    }

    /**
     *
     * @throws Throwable the throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        /*
         * clear(); SQLCommandFactory.reback(this);
         */
    }
}

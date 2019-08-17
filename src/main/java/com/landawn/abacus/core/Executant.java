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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.landawn.abacus.DataSourceManager;
import com.landawn.abacus.DataSourceSelector;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.core.SQLTransaction.CreatedBy;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.core.command.SQLOperationCommand;
import com.landawn.abacus.core.interpreter.Interpreter;
import com.landawn.abacus.core.interpreter.InterpreterProxy;
import com.landawn.abacus.core.interpreter.SQLInterpreterFactory;
import com.landawn.abacus.dataSource.SQLDataSource;
import com.landawn.abacus.exception.InvalidTransactionIdException;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.util.ExceptionUtil;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Jdbc;
import com.landawn.abacus.util.SQLParser;
import com.landawn.abacus.util.WD;

// TODO: Auto-generated Javadoc
/**
 * The Class Executant.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
public class Executant {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Executant.class);

    /** The Constant SELECT_COUNT. */
    private static final String SELECT_COUNT = "SELECT count(*) ";

    /** The dsm. */
    private final DataSourceManager _dsm;

    /** The dss. */
    private final DataSourceSelector _dss;

    private final SQLDataSource _ds;

    /** The interpreter. */
    private final Interpreter _interpreter;

    /** The default isolation level. */
    private final IsolationLevel _defaultIsolationLevel;

    /**
     * Instantiates a new executant.
     *
     * @param dsm the dsm
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    public Executant(DataSourceManager dsm) throws UncheckedSQLException {
        this._dsm = dsm;
        this._dss = dsm.getDataSourceSelector();
        this._ds = (SQLDataSource) dsm.getPrimaryDataSource();

        Connection conn = null;

        try {
            conn = _ds.getConnection();

            DatabaseMetaData dmd = conn.getMetaData();
            String proudctName = dmd.getDatabaseProductName();
            String productVersion = dmd.getDatabaseProductVersion();

            _interpreter = new InterpreterProxy(SQLInterpreterFactory.getInterpreter(proudctName, productVersion));

            final IsolationLevel tmp = _ds instanceof SQLDataSource ? this._ds.getDefaultIsolationLevel() : IsolationLevel.DEFAULT;
            _defaultIsolationLevel = tmp == IsolationLevel.DEFAULT ? IsolationLevel.valueOf(conn.getTransactionIsolation()) : tmp;

        } catch (SQLException e) {
            throw new UncheckedSQLException(ExceptionUtil.getMessage(e), e);
        } finally {
            closeConnection(_ds, conn, null);
        }
    }

    /**
     * Execute update.
     *
     * @param command the command
     * @param isAutoGeneratedKeys the is auto generated keys
     * @param options the options
     * @return the SQL result
     */
    public SQLResult executeUpdate(Command command, boolean isAutoGeneratedKeys, Map<String, Object> options) {
        return internalExecuteUpdate(checkCmd(command), isAutoGeneratedKeys, options);
    }

    /**
     * Execute count.
     *
     * @param command the command
     * @param options the options
     * @return the int
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    public int executeCount(Command command, Map<String, Object> options) throws UncheckedSQLException {
        SQLOperationCommand sqlCommand = checkCmd(command);

        String querySQL = sqlCommand.getSql();
        int fromIndex = SQLParser.indexWord(querySQL, WD.FROM, 6, false);
        int toIndex = SQLParser.indexWord(querySQL, WD.OFFSET, fromIndex, false);

        if (toIndex < 0) {
            toIndex = SQLParser.indexWord(querySQL, WD.FOR_UPDATE, fromIndex, false);
        }

        toIndex = (toIndex < 0) ? querySQL.length() : toIndex;

        String countQuerySQL = SELECT_COUNT + querySQL.substring(fromIndex, toIndex);

        int resultSize = 0;
        SQLResult queryResult = null;

        try {
            sqlCommand.setSql(countQuerySQL);
            queryResult = internalExecuteQuery(sqlCommand, false, false, options);

            if (queryResult.next()) {
                resultSize = ((Number) queryResult.get(0)).intValue();
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            sqlCommand.setSql(querySQL);

            if (queryResult != null) {
                queryResult.close();
            }
        }

        return resultSize;
    }

    /**
     * Execute query.
     *
     * @param command the command
     * @param options the options
     * @return the SQL result
     */
    public SQLResult executeQuery(Command command, Map<String, Object> options) {
        return internalExecuteQuery(checkCmd(command), false, false, options);
    }

    /**
     * Execute query with handle.
     *
     * @param command the command
     * @param options the options
     * @return the SQL result
     */
    public SQLResult executeQueryWithHandle(Command command, Map<String, Object> options) {
        return internalExecuteQuery(checkCmd(command), true, false, options);
    }

    /**
     * Begin transaction.
     *
     * @param isolationLevel the isolation level
     * @param options the options
     * @return the string
     */
    public String beginTransaction(IsolationLevel isolationLevel, Map<String, Object> options) {
        N.checkArgNotNull(isolationLevel, "isolationLevel");

        final IsolationLevel isolation = isolationLevel == IsolationLevel.DEFAULT ? _defaultIsolationLevel : isolationLevel;
        final boolean forUpdateOnly = EntityManagerUtil.isTransactionForUpdateOnly(options);
        final String queryWithDataSource = EntityManagerUtil.getQueryWithDataSource(options);
        final DataSource ds = N.isNullOrEmpty(queryWithDataSource) ? _ds : _dsm.getActiveDataSource(queryWithDataSource);

        if (ds == null) {
            throw new IllegalArgumentException("No active data source found by name: " + queryWithDataSource);
        }

        SQLTransaction tran = SQLTransaction.getTransaction(ds, CreatedBy.NEW_ENTITY_MANAGER);

        if (tran == null) {
            Connection conn = null;
            boolean noException = false;

            try {
                conn = JdbcUtil.getConnection(ds);
                tran = new SQLTransaction(ds, conn, isolation, CreatedBy.NEW_ENTITY_MANAGER, true);
                tran.incrementAndGetRef(isolation, forUpdateOnly);

                noException = true;
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            } finally {
                if (noException == false) {
                    JdbcUtil.releaseConnection(conn, ds);
                }
            }

            logger.info("Create a new SQLTransaction(id={})", tran.id());
            SQLTransaction.putTransaction(tran);
        } else {
            logger.info("Reusing the existing SQLTransaction(id={})", tran.id());
            tran.incrementAndGetRef(isolation, forUpdateOnly);
        }

        return tran.id();
    }

    /**
     * Gets the transaction.
     *
     * @param transactionId the transaction id
     * @return the transaction
     */
    public SQLTransaction getTransaction(String transactionId) {
        SQLTransaction tran = SQLTransaction.getTransaction(transactionId);

        if (tran == null) {
            throw new InvalidTransactionIdException("No transaction found with transaction id: " + transactionId);
        }

        return tran;
    }

    /**
     * End transaction.
     *
     * @param transactionId the transaction id
     * @param transactionAction the transaction action
     * @param options the options
     */
    @SuppressWarnings("deprecation")
    public void endTransaction(String transactionId, Action transactionAction, Map<String, Object> options) {
        final SQLTransaction tran = getTransaction(transactionId);

        // Should never happen.
        if (!(transactionAction == Action.COMMIT || transactionAction == Action.ROLLBACK)) {
            throw new IllegalArgumentException("Unsupported transaction action[" + transactionAction + "]. ");
        }

        if (transactionAction == Action.COMMIT) {
            tran.commit();
        } else {
            tran.rollback();
        }
    }

    /**
     * Gets the interpreter.
     *
     * @return the interpreter
     */
    public Interpreter getInterpreter() {
        return _interpreter;
    }

    /**
     * Internal execute update.
     *
     * @param sqlCommand the sql command
     * @param isAutoGeneratedKeys the is auto generated keys
     * @param options the options
     * @return the SQL result
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    SQLResult internalExecuteUpdate(SQLOperationCommand sqlCommand, boolean isAutoGeneratedKeys, Map<String, Object> options) throws UncheckedSQLException {
        SQLDataSource ds = getDataSource(sqlCommand, options);

        if (ds.isSqlLogEnable() && logger.isInfoEnabled()) {
            logger.info(sqlCommand.getSql());
        }

        long startTime = System.currentTimeMillis();

        long executionTime = 0;
        int udpatedCount = 0;
        List<Object> autoGeneratedKeys = null;

        final PreparedStatement stmt = prepareStatement(ds, sqlCommand, isAutoGeneratedKeys, false, false, options);
        ResultSet rs = null;

        try {
            if (sqlCommand.isBatch()) {
                int[] results = stmt.executeBatch();

                for (int result : results) {
                    udpatedCount += result;
                }
            } else {
                udpatedCount = stmt.executeUpdate();
            }

            if (isAutoGeneratedKeys) {
                try {
                    rs = stmt.getGeneratedKeys();
                    autoGeneratedKeys = new ArrayList<Object>(udpatedCount);

                    while (rs.next()) {
                        autoGeneratedKeys.add(JdbcUtil.getColumnValue(rs, 1));
                    }
                } catch (SQLException e) {
                    logger.error("Failed to retrieve the auto-generated Ids", e);
                }
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(ExceptionUtil.getMessage(e) + ". [SQL]:" + sqlCommand.getSql(), e);
        } finally {
            executionTime = System.currentTimeMillis() - startTime;

            if (ds.isPerfLog() && (executionTime > ds.getPerfLog()) && logger.isWarnEnabled()) {
                logger.warn("[PERF] [" + (System.currentTimeMillis() - startTime) + "] " + sqlCommand.toString());
            }

            if (sqlCommand.isBatch()) {
                try {
                    stmt.clearBatch();
                } catch (SQLException e) {
                    logger.error("Failed to clear batch parameters", e);
                }
            }

            closeResultSet(rs);
            closeStatement(ds, stmt, options);
        }

        if (isAutoGeneratedKeys) {
            return new SQLResult(this, sqlCommand, executionTime, udpatedCount, autoGeneratedKeys);
        } else {
            return new SQLResult(this, sqlCommand, executionTime, udpatedCount);
        }
    }

    /**
     * Internal execute query.
     *
     * @param sqlCommand the sql command
     * @param queryWithHandle the query with handle
     * @param queryInUpdate the query in update
     * @param options the options
     * @return the SQL result
     */
    SQLResult internalExecuteQuery(SQLOperationCommand sqlCommand, boolean queryWithHandle, boolean queryInUpdate, Map<String, Object> options) {
        SQLDataSource ds = getDataSource(sqlCommand, options);

        if (ds.isSqlLogEnable() && logger.isInfoEnabled()) {
            logger.info(sqlCommand.getSql());
        }

        options = EntityManagerUtil.copyOptions(options);

        if (options.get(Jdbc.RESULT_SET_TYPE) == null) {
            options.put(Jdbc.RESULT_SET_TYPE, ResultSet.TYPE_SCROLL_INSENSITIVE);
        }

        if (options.get(Jdbc.RESULT_SET_CONCURRENCY) == null) {
            options.put(Jdbc.RESULT_SET_CONCURRENCY, ResultSet.CONCUR_READ_ONLY);
        }

        long startTime = System.currentTimeMillis();

        long executionTime = 0;

        final PreparedStatement stmt = prepareStatement(ds, sqlCommand, false, queryWithHandle, queryInUpdate, options);
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            closeResultSet(rs);
            closeStatement(ds, stmt, options);

            throw new UncheckedSQLException(ExceptionUtil.getMessage(e) + ". [SQL]:" + sqlCommand.getSql(), e);
        } finally {
            executionTime = System.currentTimeMillis() - startTime;

            if (ds.isPerfLog() && (executionTime > ds.getPerfLog()) && logger.isWarnEnabled()) {
                logger.warn("[PERF] [" + (System.currentTimeMillis() - startTime) + "] " + sqlCommand.toString());
            }
        }

        if (queryWithHandle) {
            return new HandledSQLResult(this, sqlCommand, options, ds, stmt, rs, executionTime);
        } else {
            return new SQLResult(this, sqlCommand, options, ds, stmt, rs, executionTime);
        }
    }

    /**
     * Gets the transaction.
     *
     * @param transactionId the transaction id
     * @param ds the ds
     * @return the transaction
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    private SQLTransaction getTransaction(String transactionId, SQLDataSource ds) throws UncheckedSQLException {
        SQLTransaction tran = SQLTransaction.getTransaction(transactionId);

        if (tran == null) {
            throw new InvalidTransactionIdException("No transaction found with id: " + transactionId);
        }

        if (!ds.equals(tran.dataSource())) {
            throw new IllegalStateException(
                    "Can't started mult-transaction between different data sources with transaction id: " + transactionId + "it's not supported yet");
        }

        return tran;
    }

    /**
     * Select data source.
     *
     * @param sqlCommand the sql command
     * @param options the options
     * @return the SQL data source
     */
    private SQLDataSource getDataSource(SQLOperationCommand sqlCommand, Map<String, Object> options) {
        String entityName = sqlCommand.getEntityDef() == null ? null : sqlCommand.getEntityDef().getName();

        SQLDataSource ds = null;
        if (sqlCommand.isBatch()) {
            ds = (SQLDataSource) _dss.select(_dsm, entityName, sqlCommand.getSql(), sqlCommand.getBatchParameters(), options);
        } else {
            ds = (SQLDataSource) _dss.select(_dsm, entityName, sqlCommand.getSql(), sqlCommand.getParameters(), options);
        }

        if (ds == null) {
            throw new IllegalArgumentException("No data source selected for sql command: " + sqlCommand.toString());
        }

        return ds;
    }

    /**
     * Gets the connection.
     *
     * @param ds the ds
     * @param sqlCommand the sql command
     * @param queryWithHandle the query with handle
     * @param queryInUpdate the query in update
     * @param options the options
     * @return the connection
     */
    private Connection getConnection(SQLDataSource ds, SQLOperationCommand sqlCommand, boolean queryWithHandle, boolean queryInUpdate,
            Map<String, Object> options) {
        boolean isQuery = (sqlCommand != null) && (sqlCommand.getOperationType() == OperationType.QUERY);
        boolean isInTransaction = EntityManagerUtil.isInTransaction(options);

        if (queryWithHandle) {
            if (isInTransaction) {
                throw new IllegalArgumentException("Can't query with 'resultHandle' in transaction.");
            }

            return ds.getPersistentConnection();
        } else if (isInTransaction) {
            final SQLTransaction tran = getTransaction(EntityManagerUtil.getTransactionId(options), ds);

            if (isQuery == false || tran.isForUpdateOnly() == false) {
                return getTransaction(options.get(Options.TRANSACTION_ID).toString(), ds).connection();
            }
        }

        if (isQuery && (ds.isQueryWithReadOnlyConnectionByDefault() || EntityManagerUtil.isQueryWithReadOnlyConection(options))
                && !(EntityManagerUtil.notQueryWithReadOnlyConection(options) || queryInUpdate)) {
            return ds.getReadOnlyConnection();
        } else {
            return JdbcUtil.getConnection(ds);
        }
    }

    /**
     * Prepare statement.
     *
     * @param ds the ds
     * @param sqlCommand the sql command
     * @param isAutoGeneratedKeys the is auto generated keys
     * @param queryWithHandle the query with handle
     * @param queryInUpdate the query in update
     * @param options the options
     * @return the prepared statement
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    private PreparedStatement prepareStatement(SQLDataSource ds, SQLOperationCommand sqlCommand, boolean isAutoGeneratedKeys, boolean queryWithHandle,
            boolean queryInUpdate, Map<String, Object> options) throws UncheckedSQLException {
        String entityName = sqlCommand.getEntityDef() == null ? null : sqlCommand.getEntityDef().getName();
        String sql = sqlCommand.getSql();

        if (sqlCommand.isBatch()) {
            sql = ds.getSliceSelector().select(entityName, sql, sqlCommand.getBatchParameters(), options);
        } else {
            sql = ds.getSliceSelector().select(entityName, sql, sqlCommand.getParameters(), options);
        }

        int timeout = -1;

        if ((options != null) && (options.get(Jdbc.QUERY_TIMEOUT) != null)) {
            timeout = ((Number) options.get(Jdbc.QUERY_TIMEOUT)).intValue();

            if (timeout < 0) {
                throw new IllegalArgumentException("Jdbc timeout[" + timeout + "] must not less than zero.");
            }
        }

        int maxRows = -1;

        if ((options != null) && (options.get(Jdbc.MAX_ROWS) != null)) {
            maxRows = (Integer) options.get(Jdbc.MAX_ROWS);

            if (maxRows < 0) {
                throw new IllegalArgumentException("'maxRows[" + maxRows + "] must not less than zero.");
            }
        }

        int maxFieldSize = -1;

        if ((options != null) && (options.get(Jdbc.MAX_FIELD_SIZE) != null)) {
            maxFieldSize = (Integer) options.get(Jdbc.MAX_FIELD_SIZE);

            if (maxFieldSize < 0) {
                throw new IllegalArgumentException("maxFieldSize[" + maxFieldSize + "] must not less than zero.");
            }
        }

        int fetchSize = -1;

        if ((options != null) && (options.get(Jdbc.FETCH_SIZE) != null)) {
            fetchSize = (Integer) options.get(Jdbc.FETCH_SIZE);

            if (fetchSize < 0) {
                throw new IllegalArgumentException("fetchSize[" + fetchSize + "] must not less than zero.");
            }
        }

        int fetchDirection = -1;

        if ((options != null) && (options.get(Jdbc.FETCH_DIRECTION) != null)) {
            fetchDirection = (Integer) options.get(Jdbc.FETCH_DIRECTION);

            if (fetchDirection < 0) {
                throw new IllegalArgumentException("fetchDirection[" + fetchDirection + "] must not less than zero.");
            }
        }

        int resultSetType = -1;

        if ((options != null) && (options.get(Jdbc.RESULT_SET_TYPE) != null)) {
            resultSetType = (Integer) options.get(Jdbc.RESULT_SET_TYPE);

            if (resultSetType < 0) {
                throw new IllegalArgumentException("resultSetType[" + resultSetType + "] must not less than zero.");
            }
        }

        int resultSetConcurrency = -1;

        if ((options != null) && (options.get(Jdbc.RESULT_SET_CONCURRENCY) != null)) {
            resultSetConcurrency = (Integer) options.get(Jdbc.RESULT_SET_CONCURRENCY);

            if (resultSetConcurrency < 0) {
                throw new IllegalArgumentException("resultSetConcurrency[" + resultSetConcurrency + "] must not less than zero. ");
            }
        }

        int resultSetHoldability = -1;

        if ((options != null) && (options.get(Jdbc.RESULT_SET_HOLDABILITY) != null)) {
            resultSetHoldability = (Integer) options.get(Jdbc.RESULT_SET_HOLDABILITY);

            if (resultSetHoldability < 0) {
                throw new IllegalArgumentException("resultSetHoldability[" + resultSetHoldability + "] must not less than zero.");
            }
        }

        if (!(((resultSetType > -1) && (resultSetConcurrency > -1) && (resultSetHoldability > -1))
                || ((resultSetType > -1) && (resultSetConcurrency > -1) && (resultSetHoldability < 0))
                || ((resultSetType < 0) && (resultSetConcurrency < 0) && (resultSetHoldability < 0)))) {
            throw new IllegalArgumentException("resultSetHoldability=" + resultSetHoldability + ". resultSetType=" + resultSetType + ". resultSetConcurrency="
                    + resultSetConcurrency + ".");
        }

        final Connection conn = getConnection(ds, sqlCommand, queryWithHandle, queryInUpdate, options);
        PreparedStatement stmt = null;
        boolean noException = false;

        try {
            if ((resultSetType != -1) || (resultSetConcurrency != -1) || (resultSetHoldability != -1)) {
                resultSetType = (resultSetType == -1) ? ResultSet.TYPE_FORWARD_ONLY : resultSetType;

                resultSetConcurrency = (resultSetConcurrency == -1) ? ResultSet.CONCUR_READ_ONLY : resultSetConcurrency;

                if (resultSetHoldability != -1) {
                    stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
                } else {
                    stmt = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
                }
            } else {
                if (isAutoGeneratedKeys) {
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                } else {
                    stmt = conn.prepareStatement(sql);
                }
            }

            if (timeout != -1) {
                stmt.setQueryTimeout(timeout);
            }

            if (maxRows != -1) {
                stmt.setMaxRows(maxRows);
            }

            if (maxFieldSize != -1) {
                stmt.setMaxFieldSize(maxFieldSize);
            }

            if (fetchSize != -1) {
                stmt.setFetchSize(fetchSize);
            }

            if (fetchDirection != -1) {
                stmt.setFetchDirection(fetchDirection);
            }

            setParameterValue(sqlCommand, stmt);
            noException = true;
        } catch (SQLException e) {
            throw new UncheckedSQLException(ExceptionUtil.getMessage(e) + ". [SQL]:" + sqlCommand.getSql(), e);
        } finally {
            if (!noException) {
                closeStatement(ds, stmt, options);
            }
        }

        return stmt;
    }

    /**
     * Sets the parameter value.
     *
     * @param sqlCommand the sql command
     * @param stmt the stmt
     * @throws SQLException the SQL exception
     */
    private void setParameterValue(SQLOperationCommand sqlCommand, PreparedStatement stmt) throws SQLException {
        Type<Object> type = null;
        Object parameterValue = null;

        if (sqlCommand.isBatch()) {
            List<Object[]> parametersList = sqlCommand.getBatchParameters();

            for (Object[] parameters : parametersList) {
                for (int parameterIndex = 0, len = parameters.length; parameterIndex < len; parameterIndex++) {
                    type = sqlCommand.getParameterType(parameterIndex);
                    parameterValue = parameters[parameterIndex];

                    if (type == null) {
                        stmt.setObject(parameterIndex + 1, parameterValue);
                    } else {
                        type.set(stmt, parameterIndex + 1, parameterValue);
                    }
                }

                stmt.addBatch();
            }
        } else {
            for (int parameterIndex = 0, len = sqlCommand.getParameterCount(); parameterIndex < len; parameterIndex++) {
                type = sqlCommand.getParameterType(parameterIndex);
                parameterValue = sqlCommand.getParameter(parameterIndex);

                if (type == null) {
                    stmt.setObject(parameterIndex + 1, parameterValue);
                } else {
                    try {
                        type.set(stmt, parameterIndex + 1, parameterValue);
                    } catch (ClassCastException e) {
                        // ignore

                        if (logger.isWarnEnabled()) {
                            logger.warn("Failed to set parameter value(" + parameterValue + ") due to error: " + ExceptionUtil.getMessage(e)
                                    + ". Try to convert value and reset.");
                        }

                        Type<Object> srcType = TypeFactory.getType(parameterValue.getClass());
                        type.set(stmt, parameterIndex + 1, type.valueOf(srcType.stringOf(parameterValue)));
                    } catch (Exception e) {
                        // ignore

                        if (logger.isWarnEnabled()) {
                            logger.warn("Failed to set parameter value(" + parameterValue + ") due to error: " + ExceptionUtil.getMessage(e)
                                    + ". Try to reset it by setObject(...).");
                        }

                        stmt.setObject(parameterIndex + 1, parameterValue);
                    }
                }
            }
        }
    }

    /**
     * Close result set.
     *
     * @param rs the rs
     */
    void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore;
                logger.error("Failed to close ResultSet", e);
            }
        }
    }

    /**
     * Close statement.
     *
     * @param ds the ds
     * @param stmt the stmt
     * @param options the options
     */
    void closeStatement(SQLDataSource ds, Statement stmt, Map<String, Object> options) {
        if (stmt != null) {
            Connection conn = null;

            try {
                conn = stmt.getConnection();
            } catch (SQLException e) {
                // ignore;
                logger.error("Failed to stmt.getConnection() in close action", e);
            }

            try {
                stmt.close();
            } catch (SQLException e) {
                // ignore;
                logger.error("Failed to close Statement", e);
            }

            if (conn != null) {
                closeConnection(ds, conn, options);
            }
        }
    }

    /**
     * Close connection.
     *
     * @param ds the ds
     * @param conn the conn
     * @param options the options
     * @return true, if successful
     */
    boolean closeConnection(SQLDataSource ds, Connection conn, Map<String, Object> options) {
        if ((conn != null) && !ds.isPersistentConnection(conn) && !EntityManagerUtil.isInTransaction(options)) {
            JdbcUtil.releaseConnection(conn, ds);

            return true;
        }

        return false;
    }

    /**
     * Check cmd.
     *
     * @param command the command
     * @return the SQL operation command
     */
    private static SQLOperationCommand checkCmd(Command command) {
        if (command instanceof SQLOperationCommand) {
            return (SQLOperationCommand) command;
        } else {
            throw new IllegalArgumentException("The sql executant only recognize sql operation command, can't recognize cmd [" + command.toString() + "]. ");
        }
    }

    /**
     * The Class HandledSQLResult.
     */
    private static class HandledSQLResult extends SQLResult {

        /**
         * Instantiates a new handled SQL result.
         *
         * @param executant the executant
         * @param sqlCommand the sql command
         * @param options the options
         * @param ds the ds
         * @param stmt the stmt
         * @param rs the rs
         * @param executionTime the execution time
         */
        public HandledSQLResult(Executant executant, SQLOperationCommand sqlCommand, Map<String, Object> options, SQLDataSource ds, Statement stmt,
                ResultSet rs, long executionTime) {
            super(executant, sqlCommand, options, ds, stmt, rs, executionTime);
        }

        /**
         * Close.
         */
        @Override
        public void close() {
            if (isClosed()) {
                return;
            }

            synchronized (this) {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        // ignore
                        logger.error("Failed to close statement when close ResultHandle", e);
                    }
                }
            }
        }
    }
}

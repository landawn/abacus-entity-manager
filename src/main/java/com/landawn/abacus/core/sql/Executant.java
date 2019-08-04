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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.DataSourceManager;
import com.landawn.abacus.DataSourceSelector;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.Transaction.Status;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.core.EntityManagerUtil;
import com.landawn.abacus.core.sql.command.Command;
import com.landawn.abacus.core.sql.command.SQLOperationCommand;
import com.landawn.abacus.core.sql.interpreter.Interpreter;
import com.landawn.abacus.core.sql.interpreter.InterpreterProxy;
import com.landawn.abacus.core.sql.interpreter.SQLInterpreterFactory;
import com.landawn.abacus.dataSource.SQLDataSource;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.exception.InvalidTransactionIdException;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Jdbc;
import com.landawn.abacus.util.SQLParser;
import com.landawn.abacus.util.WD;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
public class Executant {
    private static final Logger logger = LoggerFactory.getLogger(Executant.class);

    private static final String SELECT_COUNT = "SELECT count(*) ";

    private final Map<String, SQLTransaction> transactionPool = new ConcurrentHashMap<>();

    private final DataSourceManager dsm;
    private final DataSourceSelector dss;
    private final Interpreter interpreter;

    public Executant(DataSourceManager dsm) throws UncheckedSQLException {
        this.dsm = dsm;
        this.dss = dsm.getDataSourceSelector();

        SQLDataSource ds = (SQLDataSource) dsm.getPrimaryDataSource();
        Connection conn = null;

        try {
            conn = ds.getConnection();

            DatabaseMetaData dmd = conn.getMetaData();
            String proudctName = dmd.getDatabaseProductName();
            String productVersion = dmd.getDatabaseProductVersion();

            interpreter = new InterpreterProxy(SQLInterpreterFactory.getInterpreter(proudctName, productVersion));
        } catch (SQLException e) {
            throw new UncheckedSQLException(AbacusException.getErrorMsg(e), e);
        } finally {
            closeConnection(ds, conn, null);
        }
    }

    public SQLResult executeUpdate(Command command, boolean isAutoGeneratedKeys, Map<String, Object> options) {
        return internalExecuteUpdate(checkCmd(command), isAutoGeneratedKeys, options);
    }

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

    public SQLResult executeQuery(Command command, Map<String, Object> options) {
        return internalExecuteQuery(checkCmd(command), false, false, options);
    }

    public SQLResult executeQueryWithHandle(Command command, Map<String, Object> options) {
        return internalExecuteQuery(checkCmd(command), true, false, options);
    }

    public String beginTransaction(IsolationLevel isolationLevel, Map<String, Object> options) {
        //
        //        Connection conn = getConnection(null, options);
        //
        //        try {
        //            conn.setAutoCommit(false);
        //
        //            if (isolationLevel.intValue() >= 0) {
        //                conn.setTransactionIsolation(isolationLevel.intValue());
        //            }
        //        } catch (SQLException e) { 
        //            throw new UncheckedSQLException(AbacusException.getErrorMsg(e), e);
        //        }
        //
        //        String id = N.uuid();
        //        SQLTransaction tran = new SQLTransaction(conn);
        //        transactionPool.put(id, tran);
        //

        if (isolationLevel == null) {
            throw new IllegalArgumentException("the specified IsolationLevel is null. ");
        }

        String id = N.uuid();
        transactionPool.put(id, new SQLTransaction(id, isolationLevel));

        return id;
    }

    public SQLTransaction getTransaction(String transactionId) {
        SQLTransaction tran = transactionPool.get(transactionId);

        if (tran == null) {
            throw new InvalidTransactionIdException("No transaction found with transaction id: " + transactionId);
        }

        return tran;
    }

    public void endTransaction(String transactionId, Action transactionAction, Map<String, Object> options) {
        SQLTransaction tran = getTransaction(transactionId);

        transactionPool.remove(transactionId);

        // Should never happen.
        if (!(transactionAction == Action.COMMIT || transactionAction == Action.ROLLBACK)) {
            throw new IllegalArgumentException("Unsupported transaction action[" + transactionAction + "]. ");
        }

        // Should never happen.
        if (tran.status() != Status.ACTIVE) {
            throw new IllegalStateException("Transaction with id " + transactionId + " is already " + tran.status());
        }

        if (tran.getConnection() != null) {
            try {
                if (Action.COMMIT == transactionAction) {
                    tran.commit(EntityManagerUtil.isAutoRollbackTransaction(options));
                } else {
                    tran.rollback();
                }
            } finally {
                resetConnection(tran.getDataSource(), tran.getConnection());

                closeConnection(tran.getDataSource(), tran.getConnection(), null);
            }
        }
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    SQLResult internalExecuteUpdate(SQLOperationCommand sqlCommand, boolean isAutoGeneratedKeys, Map<String, Object> options) throws UncheckedSQLException {
        SQLDataSource ds = selectDataSource(sqlCommand, options);

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
            throw new UncheckedSQLException(AbacusException.getErrorMsg(e) + ". [SQL]:" + sqlCommand.getSql(), e);
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

    SQLResult internalExecuteQuery(SQLOperationCommand sqlCommand, boolean queryWithHandle, boolean queryInUpdate, Map<String, Object> options) {
        SQLDataSource ds = selectDataSource(sqlCommand, options);

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

            throw new UncheckedSQLException(AbacusException.getErrorMsg(e) + ". [SQL]:" + sqlCommand.getSql(), e);
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

    private SQLDataSource selectDataSource(SQLOperationCommand sqlCommand, Map<String, Object> options) {
        String entityName = sqlCommand.getEntityDef() == null ? null : sqlCommand.getEntityDef().getName();

        SQLDataSource ds = null;
        if (sqlCommand.isBatch()) {
            ds = (SQLDataSource) dss.select(dsm, entityName, sqlCommand.getSql(), sqlCommand.getBatchParameters(), options);
        } else {
            ds = (SQLDataSource) dss.select(dsm, entityName, sqlCommand.getSql(), sqlCommand.getParameters(), options);
        }

        if (ds == null) {
            throw new IllegalArgumentException("No data source selected for sql command: " + sqlCommand.toString());
        }

        return ds;
    }

    private Connection getConnection(SQLDataSource ds, SQLOperationCommand sqlCommand, boolean queryWithHandle, boolean queryInUpdate,
            Map<String, Object> options) {
        Connection conn = null;
        boolean isQuery = (sqlCommand != null) && (sqlCommand.getOperationType() == OperationType.QUERY);
        boolean isInTransaction = EntityManagerUtil.isInTransaction(options);

        if (queryWithHandle) {
            if (isInTransaction) {
                throw new IllegalArgumentException("Can't query with 'resultHandle' in transaction.");
            }

            conn = ds.getPersistentConnection();
        } else {
            if (isInTransaction) {
                SQLTransaction tran = getTransaction(options.get(Options.TRANSACTION_ID).toString(), ds);
                conn = tran.getConnection();
            } else {
                if (isQuery && (ds.isQueryWithReadOnlyConnectionByDefault() || EntityManagerUtil.isQueryWithReadOnlyConection(options))
                        && !(EntityManagerUtil.notQueryWithReadOnlyConection(options) || queryInUpdate)) {
                    conn = ds.getReadOnlyConnection();
                } else {
                    conn = ds.getConnection();
                }
            }
        }

        return conn;
    }

    private SQLTransaction getTransaction(String transactionId, SQLDataSource ds) throws UncheckedSQLException {
        SQLTransaction tran = transactionPool.get(transactionId);

        if (tran == null) {
            throw new InvalidTransactionIdException("No transaction found with id: " + transactionId);
        }

        if (tran.getDataSource() == null) {
            tran.setDataSource(ds);
        } else if (!ds.equals(tran.getDataSource())) {
            throw new IllegalStateException(
                    "Can't started mult-transaction between different data sources with transaction id: " + transactionId + "it's not supported yet");
        }

        if (tran.getConnection() == null) {
            Connection conn = ds.getConnection();

            try {
                conn.setAutoCommit(false);

                if (tran.isolationLevel() == IsolationLevel.DEFAULT) {
                    if (ds.getDefaultIsolationLevel() == IsolationLevel.DEFAULT) {
                        // ignore. by default.
                    } else {
                        conn.setTransactionIsolation(ds.getDefaultIsolationLevel().intValue());
                    }
                } else {
                    conn.setTransactionIsolation(tran.isolationLevel().intValue());
                }
            } catch (SQLException e) {
                resetConnection(ds, conn);
                closeConnection(ds, conn, null);

                throw new UncheckedSQLException(AbacusException.getErrorMsg(e), e);
            }

            tran.setConnection(conn);
        }

        return tran;
    }

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

        boolean noException = false;

        final Connection conn = getConnection(ds, sqlCommand, queryWithHandle, queryInUpdate, options);

        PreparedStatement stmt = null;

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
            throw new UncheckedSQLException(AbacusException.getErrorMsg(e) + ". [SQL]:" + sqlCommand.getSql(), e);
        } finally {
            if (!noException) {
                closeStatement(ds, stmt, options);
            }
        }

        return stmt;
    }

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
                            logger.warn("Failed to set parameter value(" + parameterValue + ") due to error: " + AbacusException.getErrorMsg(e)
                                    + ". Try to convert value and reset.");
                        }

                        Type<Object> srcType = TypeFactory.getType(parameterValue.getClass());
                        type.set(stmt, parameterIndex + 1, type.valueOf(srcType.stringOf(parameterValue)));
                    } catch (Exception e) {
                        // ignore

                        if (logger.isWarnEnabled()) {
                            logger.warn("Failed to set parameter value(" + parameterValue + ") due to error: " + AbacusException.getErrorMsg(e)
                                    + ". Try to reset it by setObject(...).");
                        }

                        stmt.setObject(parameterIndex + 1, parameterValue);
                    }
                }
            }
        }
    }

    private void resetConnection(SQLDataSource ds, Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.setTransactionIsolation(ds.getDefaultConnectionIsolation());
            } catch (SQLException e) {
                // ignore.
                logger.error("Failed to reset AutoCommit", e);
            }
        }
    }

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

    boolean closeConnection(SQLDataSource ds, Connection conn, Map<String, Object> options) {
        try {
            if ((conn != null) && !ds.isPersistentConnection(conn) && !EntityManagerUtil.isInTransaction(options)) {
                conn.close();

                return true;
            }
        } catch (SQLException e) {
            logger.error("Failed to close Connection", e);
        }

        return false;
    }

    private static SQLOperationCommand checkCmd(Command command) {
        if (command instanceof SQLOperationCommand) {
            return (SQLOperationCommand) command;
        } else {
            throw new IllegalArgumentException("The sql executant only recognize sql operation command, can't recognize cmd [" + command.toString() + "]. ");
        }
    }

    private static class HandledSQLResult extends SQLResult {
        public HandledSQLResult(Executant executant, SQLOperationCommand sqlCommand, Map<String, Object> options, SQLDataSource ds, Statement stmt,
                ResultSet rs, long executionTime) {
            super(executant, sqlCommand, options, ds, stmt, rs, executionTime);
        }

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

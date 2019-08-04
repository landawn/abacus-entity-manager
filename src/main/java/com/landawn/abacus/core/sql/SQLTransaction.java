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
import java.sql.SQLException;

import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.Transaction;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.dataSource.SQLDataSource;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLTransaction.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class SQLTransaction implements Transaction {
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SQLTransaction.class);

    /** The id. */
    private final String id;
    
    /** The isolation level. */
    private final IsolationLevel isolationLevel;
    
    /** The ds. */
    private SQLDataSource ds;
    
    /** The conn. */
    private Connection conn;
    
    /** The status. */
    private Status status;

    /**
     * Instantiates a new SQL transaction.
     *
     * @param id the id
     * @param isolationLevel the isolation level
     */
    public SQLTransaction(String id, IsolationLevel isolationLevel) {
        this.id = id;
        this.isolationLevel = isolationLevel;
        status = Status.ACTIVE;
    }

    /**
     * Id.
     *
     * @return the string
     */
    @Override
    public String id() {
        return id;
    }

    /**
     * Isolation level.
     *
     * @return the isolation level
     */
    @Override
    public IsolationLevel isolationLevel() {
        return isolationLevel;
    }

    /**
     * Status.
     *
     * @return the status
     */
    @Override
    public Status status() {
        return status;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    @Override
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    /**
     * Gets the data source.
     *
     * @return the data source
     */
    SQLDataSource getDataSource() {
        return ds;
    }

    /**
     * Sets the data source.
     *
     * @param ds the new data source
     */
    void setDataSource(SQLDataSource ds) {
        this.ds = ds;
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    Connection getConnection() {
        return conn;
    }

    /**
     * Sets the connection.
     *
     * @param conn the new connection
     */
    void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * Commit.
     *
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    @Override
    public void commit() throws UncheckedSQLException {
        commit(true);
    }

    /**
     * Commit.
     *
     * @param autoRollback the auto rollback
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    public void commit(boolean autoRollback) throws UncheckedSQLException {
        if (!status.equals(Status.ACTIVE)) {
            throw new IllegalStateException("transaction is already " + status);
        }

        status = Status.FAILED_COMMIT;

        try {
            conn.commit();

            status = Status.COMMITTED;
        } catch (SQLException e) {
            boolean rollback = false;

            if (autoRollback) {
                try {
                    rollback();

                    rollback = true;
                } catch (Exception e2) {
                    // ignore;
                    logger.error("Failed to roll back after error happened during committing", e2);
                }
            }

            throw new UncheckedSQLException(
                    "Failed to commit transaction with id: " + id + ". and " + (rollback ? "rollback sucessfully" : "failed to roll back"), e);
        }
    }

    /**
     * Rollback.
     *
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    @Override
    public void rollback() throws UncheckedSQLException {
        if (!(status.equals(Status.ACTIVE) || status == Status.FAILED_COMMIT)) {
            throw new IllegalStateException("transaction is already " + status);
        }

        status = Status.FAILED_ROLLBACK;

        try {
            conn.rollback();

            status = Status.ROLLED_BACK;
        } catch (SQLException e) {
            throw new UncheckedSQLException("Failed to roll back transaction with id: " + id, e);
        }
    }
}

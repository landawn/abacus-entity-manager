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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
final class SQLTransaction implements Transaction {
    private static final Logger logger = LoggerFactory.getLogger(SQLTransaction.class);

    private final String id;
    private final IsolationLevel isolationLevel;
    private SQLDataSource ds;
    private Connection conn;
    private Status status;

    public SQLTransaction(String id, IsolationLevel isolationLevel) {
        this.id = id;
        this.isolationLevel = isolationLevel;
        status = Status.ACTIVE;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public IsolationLevel isolationLevel() {
        return isolationLevel;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    SQLDataSource getDataSource() {
        return ds;
    }

    void setDataSource(SQLDataSource ds) {
        this.ds = ds;
    }

    Connection getConnection() {
        return conn;
    }

    void setConnection(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void commit() throws UncheckedSQLException {
        commit(true);
    }

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

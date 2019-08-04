/*
 * Copyright (C) 2015 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus.idGenerator;

import java.sql.SQLException;

import com.landawn.abacus.core.sql.Executant;
import com.landawn.abacus.core.sql.SQLResult;
import com.landawn.abacus.core.sql.command.Command;
import com.landawn.abacus.core.sql.command.SQLCommandFactory;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.OperationType;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalIdGenerator.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class LocalIdGenerator extends AbstractNumberIdGenerator<Number> {
    
    /** The init value. */
    private final long initValue;
    
    /** The delta. */
    private final int delta;
    
    /** The max id value. */
    private volatile long maxIdValue;
    
    /** The sql. */
    private final String sql;
    
    /** The executor. */
    private Executant executor;
    
    /** The is initialized. */
    private boolean isInitialized = false;

    /**
     * Instantiates a new local id generator.
     *
     * @param prop the prop
     */
    public LocalIdGenerator(Property prop) {
        this(prop, "0");
    }

    /**
     * Instantiates a new local id generator.
     *
     * @param prop the prop
     * @param initValue the init value
     */
    public LocalIdGenerator(Property prop, String initValue) {
        this(prop, initValue, "1");
    }

    /**
     * Instantiates a new local id generator.
     *
     * @param prop the prop
     * @param initValue the init value
     * @param delta the delta
     */
    public LocalIdGenerator(Property prop, String initValue, String delta) {
        super(prop);
        this.initValue = Long.valueOf(initValue);
        this.delta = Integer.valueOf(delta);
        this.maxIdValue = this.initValue;

        sql = "select max(" + prop.getColumnName() + ") from " + prop.getEntityDefinition().getTableName();
    }

    /**
     * Initialize.
     *
     * @param executor the executor
     */
    @Override
    public void initialize(Executant executor) {
        this.executor = executor;
    }

    /**
     * Allocate.
     *
     * @return the number
     */
    @Override
    public synchronized Number allocate() {
        if (!isInitialized) {
            initialize();
        }

        maxIdValue += delta;

        return valueOf(maxIdValue);
    }

    /**
     * Reserve.
     *
     * @param value the value
     */
    @Override
    public synchronized void reserve(Number value) {
        if (!isInitialized) {
            initialize();
        }

        if (value.longValue() > maxIdValue) {
            maxIdValue = value.longValue();
        }
    }

    /**
     * Initialize.
     */
    private void initialize() {
        EntityDefinition entityDef = prop.getEntityDefinition();
        Command queryCmd = SQLCommandFactory.createSqlCommand(OperationType.QUERY, entityDef, sql, null);

        SQLResult queryResult = executor.executeQuery(queryCmd, null);

        try {
            if (queryResult.next() && (queryResult.get(0) != null)) {
                maxIdValue = ((Number) queryResult.get(0)).longValue();
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        maxIdValue = (maxIdValue < initValue) ? initValue : maxIdValue;
        isInitialized = true;
    }
}

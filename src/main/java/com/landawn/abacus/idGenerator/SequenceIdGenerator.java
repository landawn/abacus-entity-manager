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

import com.landawn.abacus.core.Executant;
import com.landawn.abacus.core.SQLResult;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.core.command.SQLCommandFactory;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.OperationType;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceIdGenerator.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class SequenceIdGenerator extends AbstractNumberIdGenerator<Number> {

    /** The next val sql. */
    private final String nextValSql;

    /** The executant. */
    private Executant executant;

    /** The query cmd. */
    private Command queryCmd;

    /**
     * Instantiates a new sequence id generator.
     *
     * @param prop the prop
     * @param sequenceName the sequence name
     */
    public SequenceIdGenerator(Property prop, String sequenceName) {
        super(prop);

        String tmp = sequenceName.trim().toLowerCase();
        if (tmp.startsWith("select ")) {
            nextValSql = sequenceName;
        } else {
            if (tmp.indexOf(".nextval") < 0) {
                sequenceName += ".nextval";
            }

            nextValSql = "select " + sequenceName + " from dual";
        }
    }

    /**
     * Initialize.
     *
     * @param executor the executor
     */
    @Override
    public void initialize(Executant executor) {
        this.executant = executor;

        EntityDefinition entityDef = getProperty().getEntityDefinition();
        queryCmd = SQLCommandFactory.createSqlCommand(OperationType.QUERY, entityDef, nextValSql, null);
    }

    /**
     * Allocate.
     *
     * @return the number
     */
    @Override
    public Number allocate() {
        SQLResult queryResult = executant.executeQuery(queryCmd, null);

        try {
            if (queryResult.next()) {
                return valueOf((Number) queryResult.get(0));
            } else {
                throw new AbacusException("Failed to allocate value for id property: " + prop.getName());
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }
}

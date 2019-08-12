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

import java.util.Map;

import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.OperationType;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating SQLCommand objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class SQLCommandFactory {
    
    /**
     * Instantiates a new SQL command factory.
     */
    private SQLCommandFactory() {
        // No instance;
    }

    /**
     * Creates a new SQLCommand object.
     *
     * @return the SQL cond command
     */
    public static SQLCondCommand createSqlCondCmd() {
        return new SQLCondCommand();
    }

    /**
     * Creates a new SQLCommand object.
     *
     * @param sql the sql
     * @return the SQL cond command
     */
    public static SQLCondCommand createSqlCondCmd(String sql) {
        SQLCondCommand sqlCondCmd = new SQLCondCommand();
        sqlCondCmd.setSql(sql);

        return sqlCondCmd;
    }

    /**
     * Creates a new SQLCommand object.
     *
     * @param operationType the operation type
     * @param entityDef the entity def
     * @param options the options
     * @return the SQL operation command
     */
    public static SQLOperationCommand createSqlCommand(OperationType operationType, EntityDefinition entityDef, Map<String, Object> options) {
        return new SQLOperationCommand(operationType, entityDef, options);
    }

    /**
     * Creates a new SQLCommand object.
     *
     * @param operationType the operation type
     * @param entityDef the entity def
     * @param sql the sql
     * @param options the options
     * @return the SQL operation command
     */
    public static SQLOperationCommand createSqlCommand(OperationType operationType, EntityDefinition entityDef, String sql, Map<String, Object> options) {
        SQLOperationCommand sqlCommand = new SQLOperationCommand(operationType, entityDef, options);

        sqlCommand.setSql(sql);

        return sqlCommand;
    }
}

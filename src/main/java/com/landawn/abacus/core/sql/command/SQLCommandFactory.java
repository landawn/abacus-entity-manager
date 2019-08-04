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

package com.landawn.abacus.core.sql.command;

import java.util.Map;

import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.OperationType;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public final class SQLCommandFactory {
    private SQLCommandFactory() {
        // No instance;
    }

    public static SQLCondCommand createSqlCondCmd() {
        return new SQLCondCommand();
    }

    public static SQLCondCommand createSqlCondCmd(String sql) {
        SQLCondCommand sqlCondCmd = new SQLCondCommand();
        sqlCondCmd.setSql(sql);

        return sqlCondCmd;
    }

    public static SQLOperationCommand createSqlCommand(OperationType operationType, EntityDefinition entityDef, Map<String, Object> options) {
        return new SQLOperationCommand(operationType, entityDef, options);
    }

    public static SQLOperationCommand createSqlCommand(OperationType operationType, EntityDefinition entityDef, String sql, Map<String, Object> options) {
        SQLOperationCommand sqlCommand = new SQLOperationCommand(operationType, entityDef, options);

        sqlCommand.setSql(sql);

        return sqlCommand;
    }
}

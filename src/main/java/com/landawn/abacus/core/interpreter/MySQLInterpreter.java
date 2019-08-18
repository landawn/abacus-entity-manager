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

package com.landawn.abacus.core.interpreter;

import static com.landawn.abacus.util.WD._PARENTHESES_L;
import static com.landawn.abacus.util.WD._PARENTHESES_R;
import static com.landawn.abacus.util.WD._SPACE;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.core.EntityManagerUtil;
import com.landawn.abacus.core.command.SQLCommandFactory;
import com.landawn.abacus.core.command.SQLOperationCommand;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.Objectory;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.WD;

// TODO: Auto-generated Javadoc
/**
 * The Class MySQLInterpreter.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class MySQLInterpreter extends SQLInterpreter {
    
    /**
     * Instantiates a new my SQL interpreter.
     *
     * @param productName the product name
     * @param productVersion the product version
     */
    public MySQLInterpreter(String productName, String productVersion) {
        super(productName, productVersion);
    }

    /**
     * Interpret add.
     *
     * @param entityDef the entity def
     * @param propsList the props list
     * @param options the options
     * @return
     */
    @Override
    public SQLOperationCommand interpretAdd(EntityDefinition entityDef, List<Map<String, Object>> propsList, Map<String, Object> options) {
        Collection<String> propNames = propsList.get(0).keySet();

        final SQLOperationCommand sqlCommand = SQLCommandFactory.createSqlCommand(OperationType.ADD, entityDef, options);
        final StringBuilder sql = Objectory.createStringBuilder();

        try {
            sql.append(_INSERT);
            sql.append(WD._SPACE);
            sql.append(_INTO);

            buildTargetTable(entityDef, sqlCommand, sql);

            buildInsertPropNames(entityDef, propNames, sqlCommand, sql);

            if (propsList.size() > 1 && EntityManagerUtil.isEnableMySQLBatchAdd(options)) {
                buildInsertPropsListWithOneSql(entityDef, propNames, propsList, sqlCommand, sql);
            } else {
                buildInsertPropsList(entityDef, propNames, propsList, sqlCommand, sql);
            }

            sqlCommand.setSql(sql.toString());

        } finally {
            Objectory.recycle(sql);
        }

        return sqlCommand;
    }

    /**
     * Builds the insert props list with one sql.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param propsList the props list
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void buildInsertPropsListWithOneSql(EntityDefinition entityDef, Collection<String> propNames, List<Map<String, Object>> propsList,
            SQLOperationCommand sqlCommand, StringBuilder sql) {
        sql.append(_SPACE);
        sql.append(_VALUES);
        sql.append(_SPACE);

        int i = 0;
        for (Map<String, Object> props : propsList) {
            if (i++ > 0) {
                sql.append(_COMMA_SPACE);
            }

            sql.append(_PARENTHESES_L);

            int j = 0;
            for (String propName : propNames) {
                if (j++ > 0) {
                    sql.append(_COMMA_SPACE);
                }

                setPropValue(entityDef, propName, props.get(propName), sqlCommand, sql);
            }

            sql.append(_PARENTHESES_R);
        }
    }
}

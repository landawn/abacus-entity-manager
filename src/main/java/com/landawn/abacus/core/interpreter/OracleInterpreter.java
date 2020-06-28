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

import java.util.Collection;
import java.util.List;

import com.landawn.abacus.condition.Cell;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.condition.Join;
import com.landawn.abacus.condition.Limit;
import com.landawn.abacus.core.command.SQLCondCommand;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.WD;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class OracleInterpreter extends SQLInterpreter {

    public OracleInterpreter(String productName, String productVersion) {
        super(productName, productVersion);
    }

    /**
     * Builds the criteria.
     *
     * @param entityDef
     * @param criteria
     * @param sqlCondCmd
     * @param sql
     */
    @Override
    protected void buildCriteria(EntityDefinition entityDef, Criteria criteria, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        Collection<Join> joins = criteria.getJoins();
        if (N.notNullOrEmpty(joins)) {
            for (Join join : joins) {
                interpretJoin(entityDef, join, sqlCondCmd, sql);
            }
        }

        Cell where = criteria.getWhere();
        if ((where != null)) {
            int whereBeginIndex = sql.length();
            sqlCondCmd.setWhereBeginIndex(whereBeginIndex);

            interpretCell(entityDef, where, sqlCondCmd, sql);

            int whereEndIndex = sql.length();
            sqlCondCmd.setWhereEndIndex(whereEndIndex);
        }

        final Limit limit = criteria.getLimit();

        if (limit != null) {
            if (limit.getOffset() != 0) {
                throw new IllegalArgumentException("Oracle doesn't support offset. ");
            }

            int whereBeginIndex = (where == null) ? sql.length() : sqlCondCmd.getWhereBeginIndex();

            if (where == null) {
                sqlCondCmd.setWhereBeginIndex(whereBeginIndex);
                sql.append(WD._SPACE);
                sql.append(_WHERE);
            } else {
                sql.insert(whereBeginIndex + 7, WD._PARENTHESES_L);
                sql.append(WD._PARENTHESES_R);
                sql.append(WD._SPACE);
                sql.append(_AND);
            }

            if (N.notNullOrEmpty(limit.getExpr())) {
                sql.append(WD._SPACE).append(limit.getExpr());
            } else {
                sql.append(WD._SPACE);
                sql.append(WD.ROWNUM);
                sql.append(WD._SPACE);
                sql.append(WD._LESS_THAN);
                sql.append(WD._SPACE);
                sql.append(limit.getCount() + 1);
            }

            sqlCondCmd.setWhereEndIndex(sql.length());
        }

        Cell groupBy = criteria.getGroupBy();
        if (groupBy != null) {
            interpretCell(entityDef, groupBy, sqlCondCmd, sql);
        }

        Cell having = criteria.getHaving();
        if (having != null) {
            interpretCell(entityDef, having, sqlCondCmd, sql);
        }

        List<Cell> conditionList = criteria.getAggregation();
        if (N.notNullOrEmpty(conditionList)) {
            for (Cell cond : conditionList) {
                interpretCondition(entityDef, cond, sqlCondCmd, sql);
            }
        }

        Cell orderBy = criteria.getOrderBy();
        if (orderBy != null) {
            interpretCell(entityDef, orderBy, sqlCondCmd, sql);
        }
    }
}

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

package com.landawn.abacus.core.sql.interpreter;

import java.util.Collection;
import java.util.List;

import com.landawn.abacus.condition.Cell;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.condition.Join;
import com.landawn.abacus.condition.Limit;
import com.landawn.abacus.core.sql.command.SQLCondCommand;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.WD;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLServerInterpreter extends SQLInterpreter {
    private static final int VERSION_2012 = 11;
    private final boolean isVersion2012OrAbove;

    public SQLServerInterpreter(String productName, String productVersion) {
        super(productName, productVersion);

        String[] sts = productVersion.split("\\.");
        isVersion2012OrAbove = (sts.length > 0) && (Integer.valueOf(sts[0]) >= VERSION_2012);
    }

    @Override
    protected void interpretQuery(EntityDefinition entityDef, Collection<String> propNames, Criteria criteria, SQLCondCommand sqlCommand, StringBuilder sql) {
        if (isVersion2012OrAbove || criteria.getLimit() == null) {
            super.interpretQuery(entityDef, propNames, criteria, sqlCommand, sql);
        } else {
            final Limit limit = criteria.getLimit();

            if (limit != null && limit.getOffset() != 0) {
                throw new IllegalArgumentException("SQLServer(" + productVersion + ") doesn't support offset. ");
            }

            sql.append(_SELECT);

            if (criteria.isDistinct()) {
                sql.append(WD._SPACE);
                sql.append(_DISTINCT);
            }

            if (limit != null) {
                sql.append(WD._SPACE);
                sql.append(WD.TOP);
                sql.append(WD._SPACE);
                sql.append(WD._PARENTHESES_L);
                sql.append(limit.getCount());
                sql.append(WD.PARENTHESES_R);
            }

            buildSelectPropNames(entityDef, propNames, sqlCommand, sql);

            sql.append(WD._SPACE);
            sql.append(_FROM);

            final StringBuilder condSql = Objectory.createStringBuilder();

            buildCriteria(entityDef, criteria, sqlCommand, condSql);

            buildTargetTable(entityDef, sqlCommand, sql);

            sqlCommand.setWhereBeginIndex(sql.length() + sqlCommand.getWhereBeginIndex());
            sqlCommand.setWhereEndIndex(sql.length() + sqlCommand.getWhereEndIndex());

            sql.append(condSql);

            Objectory.recycle(condSql);
        }
    }

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

        Limit limit = criteria.getLimit();
        if (limit != null) {
            if (isVersion2012OrAbove) {
                sql.append(WD._SPACE);
                sql.append(_OFFSET);
                sql.append(WD._SPACE);
                sql.append(limit.getOffset());
                sql.append(WD._SPACE);
                sql.append(WD.ROWS);

                sql.append(WD._SPACE);
                sql.append(WD.FETCH_NEXT);
                sql.append(WD._SPACE);
                sql.append(limit.getCount());
                sql.append(WD._SPACE);
                sql.append(WD.ROWS_ONLY);
            } else {
                // do nothing.
            }
        }
    }
}

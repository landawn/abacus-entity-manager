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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.landawn.abacus.condition.Between;
import com.landawn.abacus.condition.Binary;
import com.landawn.abacus.condition.Cell;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.condition.Expression;
import com.landawn.abacus.condition.In;
import com.landawn.abacus.condition.Join;
import com.landawn.abacus.condition.Junction;
import com.landawn.abacus.condition.Limit;
import com.landawn.abacus.condition.SubQuery;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.core.command.SQLCommandFactory;
import com.landawn.abacus.core.command.SQLCondCommand;
import com.landawn.abacus.core.command.SQLOperationCommand;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.type.ObjectType;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.util.Iterables;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.SQLParser;
import com.landawn.abacus.util.WD;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLInterpreter.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLInterpreter extends AbstractInterpreter {
    
    /** The Constant operatorChars. */
    static final char[] operatorChars = new char[128];
    static {
        operatorChars[' '] = ' ';
        operatorChars[','] = ',';
        operatorChars['\''] = '\'';
        operatorChars['\"'] = '\"';
        operatorChars['('] = '(';
        operatorChars[')'] = ')';
        operatorChars['['] = '[';
        operatorChars[']'] = ']';
        operatorChars['{'] = '{';
        operatorChars['}'] = '}';
        operatorChars['='] = '=';
        operatorChars['<'] = '<';
        operatorChars['>'] = '>';
        operatorChars['+'] = '+';
        operatorChars['-'] = '-';
        operatorChars['*'] = '*';
        operatorChars['/'] = '/';
        operatorChars['~'] = '~';
        operatorChars['^'] = '^';
        operatorChars['%'] = '%';
        operatorChars['&'] = '&';
        operatorChars['!'] = '!';
    }

    /** The Constant EMPTY_CRITERIA. */
    static final Criteria EMPTY_CRITERIA = CF.criteria();

    /** The product name. */
    protected final String productName;
    
    /** The product version. */
    protected final String productVersion;

    /**
     * Instantiates a new SQL interpreter.
     *
     * @param productName the product name
     * @param productVersion the product version
     */
    public SQLInterpreter(String productName, String productVersion) {
        this.productName = productName;
        this.productVersion = productVersion;
    }

    //
    //    @Override
    //    public Command interpretAdd(EntityDefinition entityDef, Map<String, Object> props, Map<String, Object> options) {
    //        return interpretAdd(entityDef, N.asList(props), options);
    //    }
    //

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

            buildInsertPropsList(entityDef, propNames, propsList, sqlCommand, sql);

            sqlCommand.setSql(sql.toString());
        } finally {
            Objectory.recycle(sql);
        }

        return sqlCommand;
    }

    /**
     * Builds the target table.
     *
     * @param entityDef the entity def
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void buildTargetTable(EntityDefinition entityDef, SQLCondCommand sqlCommand, StringBuilder sql) {
        String targetTableName = entityDef.getTableName();
        sqlCommand.addTargetTable(targetTableName);

        Set<String> tableNameSet = sqlCommand.getTargetTables();
        Iterables.removeAll(tableNameSet, sqlCommand.getJoinTables());

        if (tableNameSet.size() > 0) {
            sql.append(WD._SPACE);
        }

        int i = 0;
        for (String tableName : tableNameSet) {
            if (i++ > 0) {
                sql.append(_COMMA_SPACE);
            }

            sql.append(tableName);
        }
    }

    /**
     * Builds the insert prop names.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void buildInsertPropNames(EntityDefinition entityDef, Collection<String> propNames, SQLOperationCommand sqlCommand, StringBuilder sql) {
        sql.append(_SPACE_PARENTHESES_L);

        int i = 0;
        for (String propName : propNames) {
            if (i++ > 0) {
                sql.append(_COMMA_SPACE);
            }

            sql.append(getWriteColumnName(entityDef, propName));
        }

        sql.append(WD._PARENTHESES_R);

        sqlCommand.setTargetPropNames(new ArrayList<>(propNames));
    }

    /**
     * Builds the insert props list.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param propsList the props list
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void buildInsertPropsList(EntityDefinition entityDef, Collection<String> propNames, List<Map<String, Object>> propsList,
            SQLOperationCommand sqlCommand, StringBuilder sql) {
        sql.append(WD._SPACE);
        sql.append(_VALUES);

        sql.append(_SPACE_PARENTHESES_L);

        for (int i = 0; i < propNames.size(); i++) {
            if (i > 0) {
                sql.append(_COMMA_SPACE);
            }

            sql.append(WD._QUESTION_MARK);
        }

        sql.append(WD._PARENTHESES_R);

        for (Map<String, Object> props : propsList) {
            for (String propName : propNames) {
                setPropValue(entityDef, propName, props.get(propName), sqlCommand, null);
            }

            if (propsList.size() > 1) {
                sqlCommand.addBatch();
            }
        }
    }

    /**
     * Interpret update.
     *
     * @param entityDef the entity def
     * @param props the props
     * @param condition the condition
     * @param options the options
     * @return
     */
    @Override
    public SQLOperationCommand interpretUpdate(EntityDefinition entityDef, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        Criteria criteria = condition2Criteria(condition);

        final SQLOperationCommand sqlCommand = SQLCommandFactory.createSqlCommand(OperationType.UPDATE, entityDef, options);
        final StringBuilder sql = Objectory.createStringBuilder();

        try {
            sql.append(_UPDATE);

            buildTargetTable(entityDef, sqlCommand, sql);

            sql.append(WD._SPACE);
            sql.append(_SET);

            buildUpdateProps(entityDef, props, sqlCommand, sql);

            buildCriteria(entityDef, criteria, sqlCommand, sql);

            sqlCommand.setSql(sql.toString());
        } finally {
            Objectory.recycle(sql);
        }

        return sqlCommand;
    }

    /**
     * Builds the update props.
     *
     * @param entityDef the entity def
     * @param props the props
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void buildUpdateProps(EntityDefinition entityDef, Map<String, Object> props, SQLOperationCommand sqlCommand, StringBuilder sql) {
        sql.append(WD._SPACE);

        int i = 0;
        for (String propName : props.keySet()) {
            if (i++ > 0) {
                sql.append(_COMMA_SPACE);
            }

            sql.append(getUpdateColumnName(entityDef, propName));

            sql.append(WD._SPACE);
            sql.append(WD._EQUAL);

            setPropValue(entityDef, propName, props.get(propName), sqlCommand, sql);
        }

        sqlCommand.setTargetPropNames(new ArrayList<>(props.keySet()));
    }

    /**
     * Builds the criteria.
     *
     * @param entityDef the entity def
     * @param criteria the criteria
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected void buildCriteria(EntityDefinition entityDef, Criteria criteria, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        interpretCriteria(entityDef, criteria, sqlCondCmd, sql);
    }

    /**
     * Interpret delete.
     *
     * @param entityDef the entity def
     * @param condition the condition
     * @param options the options
     * @return
     */
    @Override
    public SQLOperationCommand interpretDelete(EntityDefinition entityDef, Condition condition, Map<String, Object> options) {
        final Criteria criteria = condition2Criteria(condition);
        final SQLOperationCommand sqlCommand = SQLCommandFactory.createSqlCommand(OperationType.DELETE, entityDef, options);
        final StringBuilder sql = Objectory.createStringBuilder();

        try {
            sql.append(_DELETE);

            sql.append(WD._SPACE);
            sql.append(_FROM);

            buildTargetTable(entityDef, sqlCommand, sql);

            buildCriteria(entityDef, criteria, sqlCommand, sql);

            sqlCommand.setTargetPropNames(new ArrayList<>(entityDef.getPropertyNameList()));

            sqlCommand.setSql(sql.toString());
        } finally {
            Objectory.recycle(sql);
        }

        return sqlCommand;
    }

    /**
     * Interpret query.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param condition the condition
     * @param options the options
     * @return
     */
    @Override
    public Command interpretQuery(EntityDefinition entityDef, Collection<String> propNames, Condition condition, Map<String, Object> options) {
        final Criteria criteria = condition2Criteria(condition);
        final SQLOperationCommand sqlCommand = SQLCommandFactory.createSqlCommand(OperationType.QUERY, entityDef, options);
        final StringBuilder sql = Objectory.createStringBuilder();

        try {
            interpretQuery(entityDef, propNames, criteria, sqlCommand, sql);

            sqlCommand.setSql(sql.toString());
        } finally {
            Objectory.recycle(sql);
        }

        return sqlCommand;
    }

    /**
     * Interpret query.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param criteria the criteria
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void interpretQuery(EntityDefinition entityDef, Collection<String> propNames, Criteria criteria, SQLCondCommand sqlCommand, StringBuilder sql) {
        sql.append(_SELECT);

        if (criteria.isDistinct()) {
            sql.append(WD._SPACE);
            sql.append(_DISTINCT);
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

    /**
     * Builds the select prop names.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param sqlCommand the sql command
     * @param sql the sql
     */
    protected void buildSelectPropNames(EntityDefinition entityDef, Collection<String> propNames, SQLCondCommand sqlCommand, StringBuilder sql) {
        int i = 0;
        for (String propName : propNames) {
            if (i > 0) {
                sql.append(_COMMA_SPACE);
            }

            boolean hasAS = false;
            Property prop = entityDef.getProperty(propName);

            if (prop != null) {
                if (!prop.isReadable()) {
                    throw new IllegalArgumentException(
                            "Can't read the property[" + propName + "] which is not readable in the entity[" + prop.getEntityDefinition().getName() + "]. ");
                }

                sql.append(WD._SPACE);
                sql.append(prop.getCanonicalColumnName());

                sqlCommand.addTargetTable(prop.getEntityDefinition().getTableName());
            } else {
                List<String> words = SQLParser.parse(propName);
                String word = null;

                if (i == 0) {
                    sql.append(WD._SPACE);
                }

                for (int j = 0; j < words.size(); j++) {
                    word = words.get(j);

                    if ((j > 2) && WD.AS.equalsIgnoreCase(words.get(j - 2))) {
                        sql.append(word);

                        hasAS = true;
                    } else {
                        prop = entityDef.getProperty(word);

                        if (prop != null) {
                            setPropName(prop, sqlCommand, sql);
                        } else {
                            sql.append(word);
                        }
                    }
                }
            }

            if (hasAS == false && (propName.length() > 1 || propName.charAt(0) != '*')) {
                sql.append(WD._SPACE);

                sql.append(WD._QUOTATION_D);
                sql.append(propName);
                sql.append(WD._QUOTATION_D);
            }

            i++;
        }

        if (sqlCommand instanceof SQLOperationCommand) {
            ((SQLOperationCommand) sqlCommand).setTargetPropNames(new ArrayList<>(propNames));
        }
    }

    /**
     * Interpret query.
     *
     * @param entityDef the entity def
     * @param querySql the query sql
     * @param parameters the parameters
     * @param options the options
     * @return
     */
    @Override
    public SQLOperationCommand interpretQuery(EntityDefinition entityDef, String querySql, List<?> parameters, Map<String, Object> options) {
        final SQLOperationCommand sqlCommand = SQLCommandFactory.createSqlCommand(OperationType.QUERY, entityDef, options);
        final StringBuilder sql = Objectory.createStringBuilder();

        try {
            interpretExpression(entityDef, querySql, sqlCommand, sql);
            sqlCommand.setSql(sql.toString());

            if (N.notNullOrEmpty(parameters)) {
                for (int i = 0, count = parameters.size(); i < count; i++) {
                    sqlCommand.setParameter(i, parameters.get(i), null);
                }
            }

        } finally {
            Objectory.recycle(sql);
        }

        return sqlCommand;
    }

    /**
     * Interpret condition.
     *
     * @param entityDef the entity def
     * @param condition the condition
     * @return
     */
    @Override
    public SQLCondCommand interpretCondition(EntityDefinition entityDef, Condition condition) {
        SQLCondCommand sqlCondCmd = null;

        if (condition instanceof SubQuery) {
            sqlCondCmd = SQLCommandFactory.createSqlCommand(OperationType.QUERY, entityDef, null);
        } else {
            sqlCondCmd = SQLCommandFactory.createSqlCondCmd();
        }

        final StringBuilder sql = Objectory.createStringBuilder();

        try {
            interpretCondition(entityDef, condition, sqlCondCmd, sql);
            sqlCondCmd.setSql(sql.toString());

        } finally {
            Objectory.recycle(sql);
        }

        return sqlCondCmd;
    }

    /**
     * Interpret condition.
     *
     * @param entityDef the entity def
     * @param condition the condition
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     * @return
     */
    protected SQLCondCommand interpretCondition(EntityDefinition entityDef, Condition condition, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        // TODO [performance improvement]. how to improve the performance here?
        // cache?.
        if (condition instanceof Binary) {
            interpretBinary(entityDef, (Binary) condition, sqlCondCmd, sql);
        } else if (condition instanceof Join) {
            interpretJoin(entityDef, (Join) condition, sqlCondCmd, sql);
        } else if (condition instanceof Cell) {
            interpretCell(entityDef, (Cell) condition, sqlCondCmd, sql);
        } else if (condition instanceof Between) {
            interpretBetween(entityDef, (Between) condition, sqlCondCmd, sql);
        } else if (condition instanceof In) {
            interpretIn(entityDef, (In) condition, sqlCondCmd, sql);
        } else if (condition instanceof Junction) {
            interpretJunction(entityDef, (Junction) condition, sqlCondCmd, sql);
        } else if (condition instanceof Limit) {
            interpretLimit((Limit) condition, sql);
        } else if (condition instanceof SubQuery) {
            interpretSubQuery(entityDef, (SubQuery) condition, sqlCondCmd, sql);
        } else if (condition instanceof Criteria) {
            interpretCriteria(entityDef, (Criteria) condition, sqlCondCmd, sql);
        } else if (condition instanceof Expression) {
            interpretExpression(entityDef, ((Expression) condition).getLiteral(), sqlCondCmd, sql);
        } else {
            inerpretCustomizedCondition(entityDef, condition, sqlCondCmd, sql);
        }

        return sqlCondCmd;
    }

    /**
     * Inerpret customized condition.
     *
     * @param entityDef the entity def
     * @param condition the condition
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected void inerpretCustomizedCondition(EntityDefinition entityDef, Condition condition, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        throw new IllegalArgumentException("unsupported condition[" + condition + "]. ");
    }

    //    protected final void interpretEntityId(EntityDefinition entityDef, EntityId entityId, SQLCondCommand sqlCondCmd, StringBuilder sql) {
    //        Collection<String> propNames = entityId.getPropNames();
    //        Object propValue = null;
    //
    //        if (propNames.size() == 1) {
    //            String propName = propNames.iterator().next();
    //            setPropName(entityDef, propName, sqlCondCmd, sql);
    //
    //            propValue = entityId.get(propName);
    //
    //            if (propValue != null) {
    //                sql.append(D._SPACE);
    //                sql.append(D.EQUAL);
    //                setPropValue(entityDef, propName, propValue, sqlCondCmd, sql);
    //            } else {
    //                sql.append(D._SPACE);
    //                sql.append(D.IS_NULL);
    //            }
    //        } else {
    //            int i = 0;
    //            for (String propName : propNames) {
    //                propValue = entityId.get(propName);
    //
    //                if (i++ > 0) {
    //                    sql.append(D._SPACE);
    //                    sql.append(D.AND);
    //                }
    //
    //                setPropName(entityDef, propName, sqlCondCmd, sql);
    //
    //                if (propValue != null) {
    //                    sql.append(D._SPACE);
    //                    sql.append(D.EQUAL);
    //                    setPropValue(entityDef, propName, propValue, sqlCondCmd, sql);
    //                } else {
    //                    sql.append(D._SPACE);
    //                    sql.append(D.IS_NULL);
    //                }
    //            }
    //        }
    //    }

    /**
     * Interpret binary.
     *
     * @param entityDef the entity def
     * @param binary the binary
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretBinary(EntityDefinition entityDef, Binary binary, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        interpretExpression(entityDef, binary.getPropName(), sqlCondCmd, sql);
        sql.append(WD._SPACE);
        sql.append(binary.getOperator().toString());

        setPropValue(entityDef, binary.getPropName(), binary.getPropValue(), sqlCondCmd, sql);
    }

    /**
     * Interpret join.
     *
     * @param entityDef the entity def
     * @param join the join
     * @param criteriaCmd the criteria cmd
     * @param sql the sql
     */
    protected final void interpretJoin(EntityDefinition entityDef, Join join, SQLCondCommand criteriaCmd, StringBuilder sql) {
        sql.append(WD._SPACE);
        sql.append(join.getOperator().toString());
        sql.append(WD._SPACE);

        List<String> joinTableNames = getJoinTableNames(entityDef, join);

        if (joinTableNames.size() == 1) {
            sql.append(joinTableNames.get(0));
        } else {
            sql.append(WD._PARENTHESES_L);

            for (int i = 0; i < joinTableNames.size(); i++) {
                if (i > 0) {
                    sql.append(_COMMA_SPACE);
                }

                sql.append(joinTableNames.get(i));
            }

            sql.append(WD._PARENTHESES_R);
        }

        criteriaCmd.addJoinTables(joinTableNames);

        if (join.getCondition() != null) {
            interpretCondition(entityDef, join.getCondition(), criteriaCmd, sql);
        }
    }

    /**
     * Interpret cell.
     *
     * @param entityDef the entity def
     * @param cell the cell
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretCell(EntityDefinition entityDef, Cell cell, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        sql.append(WD._SPACE);
        sql.append(cell.getOperator().toString());

        interpretCondition(entityDef, cell.getCondition(), sqlCondCmd, sql);
    }

    /**
     * Interpret between.
     *
     * @param entityDef the entity def
     * @param between the between
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretBetween(EntityDefinition entityDef, Between between, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        interpretExpression(entityDef, between.getPropName(), sqlCondCmd, sql);

        sql.append(WD._SPACE + between.getOperator().toString());

        setPropValue(entityDef, between.getPropName(), between.getMinValue(), sqlCondCmd, sql);
        sql.append(WD._SPACE);
        sql.append(_AND);

        setPropValue(entityDef, between.getPropName(), between.getMaxValue(), sqlCondCmd, sql);
    }

    /**
     * Interpret in.
     *
     * @param entityDef the entity def
     * @param in the in
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretIn(EntityDefinition entityDef, In in, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        interpretExpression(entityDef, in.getPropName(), sqlCondCmd, sql);

        sql.append(WD._SPACE).append(in.getOperator().toString()).append(WD.SPACE_PARENTHESES_L);

        final String propName = in.getPropName();
        final List<Object> parameters = in.getParameters();

        for (int i = 0, len = parameters.size(); i < len; i++) {
            if (i > 0) {
                sql.append(WD.COMMA_SPACE);
            }

            setPropValue(entityDef, propName, parameters.get(i), sqlCondCmd, sql);
        }

        sql.append(WD._PARENTHESES_R);

    }

    /**
     * Interpret junction.
     *
     * @param entityDef the entity def
     * @param junction the junction
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretJunction(EntityDefinition entityDef, Junction junction, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        List<Condition> conditionList = junction.getConditions();

        if (N.isNullOrEmpty(conditionList)) {
            throw new IllegalArgumentException("The junction condition(" + junction.getOperator().toString() + ") doesn't include any element.");
        }

        if (conditionList.size() == 1) {
            interpretCondition(entityDef, conditionList.get(0), sqlCondCmd, sql);
        } else {
            // sql.append(PARENTHESES_L);
            for (int i = 0; i < conditionList.size(); i++) {
                if (i > 0) {
                    sql.append(WD._SPACE);
                    sql.append(junction.getOperator().toString());
                    sql.append(WD._SPACE);
                } else if ((sql.length() > 0) && (sql.charAt(sql.length() - 1) != WD._PARENTHESES_L)) {
                    sql.append(WD._SPACE);
                }

                sql.append(WD._PARENTHESES_L);
                interpretCondition(entityDef, conditionList.get(i), sqlCondCmd, sql);
                sql.append(WD._PARENTHESES_R);
            }

            // sql.append(PARENTHESES_R);
            // sqlCondCmd.setSql(sql.toString());
        }
    }

    /**
     * Interpret limit.
     *
     * @param limit the limit
     * @param sql the sql
     */
    protected final void interpretLimit(Limit limit, StringBuilder sql) {
        sql.append(WD._SPACE);
        sql.append(_LIMIT);

        // setParameterValue(limit.getCount(), TypeFactory.getType(IntType.INT),
        // sqlCondCmd, sql);
        sql.append(WD._SPACE);
        sql.append(limit.getCount());

        sql.append(WD._SPACE);
        sql.append(_OFFSET);

        // setParameterValue(limit.getOffset(),
        // TypeFactory.getType(IntType.INT), sqlCondCmd, sql);
        sql.append(WD._SPACE);
        sql.append(limit.getOffset());
    }

    /**
     * Interpret sub query.
     *
     * @param entityDef the entity def
     * @param subQuery the sub query
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretSubQuery(EntityDefinition entityDef, SubQuery subQuery, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        entityDef = entityDef.getFactory().getDefinition(subQuery.getEntityName());

        final SQLOperationCommand sqlCommand = SQLCommandFactory.createSqlCommand(OperationType.QUERY, entityDef, null);

        if (subQuery.getSql() != null) {
            sql.append(WD._SPACE);
            sql.append(WD._PARENTHESES_L);
            interpretExpression(entityDef, subQuery.getSql(), sqlCommand, sql);
            sql.append(WD._PARENTHESES_R);
        } else {
            Collection<String> selectPropNames = subQuery.getSelectPropNames();
            Criteria criteria = condition2Criteria(subQuery.getCondition());
            sql.append(WD._SPACE);
            sql.append(WD._PARENTHESES_L);
            interpretQuery(entityDef, selectPropNames, criteria, sqlCommand, sql);
            sql.append(WD._PARENTHESES_R);
        }

        sqlCondCmd.addSubQueryTables(sqlCommand.getTargetTables());
        sqlCondCmd.appendParameters(sqlCommand);
    }

    /**
     * Interpret criteria.
     *
     * @param entityDef the entity def
     * @param criteria the criteria
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretCriteria(EntityDefinition entityDef, Criteria criteria, SQLCondCommand sqlCondCmd, StringBuilder sql) {
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
            interpretLimit(limit, sql);
        }
    }

    /**
     * Interpret expression.
     *
     * @param entityDef the entity def
     * @param literal the literal
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void interpretExpression(EntityDefinition entityDef, String literal, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        Property prop = entityDef.getProperty(literal);

        if (prop != null) {
            setPropName(prop, sqlCondCmd, sql);
        } else {
            List<String> words = SQLParser.parse(literal);

            if ((sql.length() == 0) || !((sql.charAt(sql.length() - 1) == WD._SPACE) || (sql.charAt(sql.length() - 1) == WD._PARENTHESES_L))) {
                if (!words.get(0).equals(WD.SPACE)) {
                    sql.append(WD._SPACE);
                }
            }

            String word = null;

            for (int i = 0; i < words.size(); i++) {
                word = words.get(i);

                if ((i > 2) && WD.AS.equalsIgnoreCase(words.get(i - 2))) {
                    sql.append(word);
                } else {
                    prop = entityDef.getProperty(word);

                    if (prop != null) {
                        setPropName(prop, sqlCondCmd, sql);
                    } else {
                        sql.append(word);
                    }
                }
            }
        }
    }

    /**
     * Condition 2 criteria.
     *
     * @param condition the condition
     * @return
     */
    protected final Criteria condition2Criteria(Condition condition) {
        if (condition == null) {
            return EMPTY_CRITERIA;
        } else if (condition instanceof Criteria) {
            return (Criteria) condition;
        } else {
            return CF.criteria().where(condition);
        }
    }

    /**
     * Gets the write column name.
     *
     * @param entityDef the entity def
     * @param propName the prop name
     * @return
     */
    protected final String getWriteColumnName(EntityDefinition entityDef, String propName) {
        Property prop = entityDef.getProperty(propName);

        if (prop == null) {
            throw new IllegalArgumentException("Can't write the property[" + propName + "] which is not found in the entity[" + entityDef.getName() + "]. ");
        }

        if (prop.getColumnType().isEntity()) {
            throw new IllegalArgumentException("Can't write this property[" + propName + "]. the column type is entity");
        }

        return prop.getColumnName();
    }

    /**
     * Gets the update column name.
     *
     * @param entityDef the entity def
     * @param propName the prop name
     * @return
     */
    protected final String getUpdateColumnName(EntityDefinition entityDef, String propName) {
        Property prop = entityDef.getProperty(propName);

        if (prop == null) {
            throw new IllegalArgumentException("Can't update this property[" + propName + "] which is not found in the entity[" + entityDef.getName() + "]. ");
        }

        if (prop.getColumnType().isEntity()) {
            throw new IllegalArgumentException("Can't update this property[" + propName + "]. the column type is entity");
        }

        return prop.getColumnName();
    }

    /**
     * Gets the join table names.
     *
     * @param entityDef the entity def
     * @param join the join
     * @return
     */
    protected final List<String> getJoinTableNames(EntityDefinition entityDef, Join join) {
        List<String> joinTableNames = new ArrayList<>();
        EntityDefinition joinEntityDef = null;

        for (String joinEntityName : join.getJoinEntities()) {
            joinEntityDef = entityDef.getFactory().getDefinition(joinEntityName);

            if (joinEntityDef != null) {
                joinTableNames.add(joinEntityDef.getTableName());
            } else {
                joinTableNames.add(joinEntityName);
            }
        }

        return joinTableNames;
    }

    /**
     * Sets the prop name.
     *
     * @param prop the prop
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void setPropName(Property prop, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        //            if ((sql.length() == 0) || !((sql.charAt(sql.length() - 1) == D._SPACE) || (sql.charAt(sql.length() - 1) == D._PARENTHESES_L))) {
        //                sql.append(D._SPACE);
        //            }

        if (sql.length() > 0) {
            char ch = sql.charAt(sql.length() - 1);
            if (ch >= 128 || operatorChars[ch] == 0) {
                sql.append(WD._SPACE);
            }
        }

        if (sqlCondCmd instanceof SQLOperationCommand && sqlCondCmd.getOperationType() == OperationType.QUERY) {
            sql.append(prop.getCanonicalColumnName());
        } else {
            sql.append(prop.getColumnName());
        }

        sqlCondCmd.addTargetTable(prop.getEntityDefinition().getTableName());
    }

    /**
     * Sets the prop value.
     *
     * @param entityDef the entity def
     * @param propName the prop name
     * @param propValue the prop value
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void setPropValue(EntityDefinition entityDef, String propName, Object propValue, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        //        if ((propValue == null) || propValue instanceof String || propValue instanceof Number || propValue instanceof Date || propValue instanceof Boolean) {
        //            setParameterValue(entityDef, propName, propValue, sqlCondCmd, sql);
        //        } else if (propValue instanceof Condition) {
        //            interpretCondition(entityDef, (Condition) propValue, sqlCondCmd, sql);
        //        } else {
        //            setParameterValue(entityDef, propName, propValue, sqlCondCmd, sql);
        //        }

        if ((propValue != null) && propValue instanceof Condition) {
            interpretCondition(entityDef, (Condition) propValue, sqlCondCmd, sql);
        } else {
            setParameterValue(entityDef, propName, propValue, sqlCondCmd, sql);
        }
    }

    /**
     * Sets the parameter value.
     *
     * @param entityDef the entity def
     * @param propName the prop name
     * @param propValue the prop value
     * @param sqlCondCmd the sql cond cmd
     * @param sql the sql
     */
    protected final void setParameterValue(EntityDefinition entityDef, String propName, Object propValue, SQLCondCommand sqlCondCmd, StringBuilder sql) {
        Property prop = entityDef.getProperty(propName);
        Type<Object> type = (prop == null) ? TypeFactory.getType(ObjectType.OBJECT) : prop.getType();

        if (sql != null) {
            sql.append(WD._SPACE);
            sql.append(WD._QUESTION_MARK);
        }

        sqlCondCmd.setParameter(sqlCondCmd.getParameterCount(), propValue, type);
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.Expression;
import com.landawn.abacus.core.EntityManagerUtil;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.core.command.SQLCommand;
import com.landawn.abacus.core.command.SQLOperationCommand;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class InterpreterProxy extends AbstractInterpreter {

    private static final int MAX_POOL_SIZE = 100;

    private final Map<String, Map<String, Command>> conditionCmdPool = new HashMap<>();

    private final Map<String, Map<List<String>, Command>> addCmdPool = new HashMap<>();

    private final Map<String, Map<String, Map<List<String>, Command>>> updateCmdPool = new HashMap<>();

    private final Map<String, Map<String, Command>> deleteCmdPool = new HashMap<>();

    private final Map<String, Map<String, Map<List<String>, Command>>> queryCmdPool = new HashMap<>();

    private final Map<String, Map<String, Command>> sqlQueryCmdPool = new HashMap<>();

    //    private final Map<String, Map<Set<String>, SQLOperationCommand>> batchUpdateCmdPool = new HashMap<>();
    //    private final Map<String, SQLOperationCommand> batchDeleteCmdPool = new HashMap<>();

    private final Interpreter interpreter;

    public InterpreterProxy(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    //    @Override
    //    public Command interpretAdd(EntityDefinition entityDef, Map<String, Object> props, Map<String, Object> options) {
    //        Command command = null;
    //
    //        boolean isCachable = isCachable(props);
    //
    //        if (isCachable) {
    //            command = getCachedAddCmd(entityDef, props);
    //        }
    //
    //        if (command == null) {
    //            command = interpreter.interpretAdd(entityDef, props, options);
    //
    //            if (isCachable) {
    //                cacheAddCmd(entityDef, props, command);
    //            }
    //        }
    //
    //        return command;
    //    }

    /**
     *
     * @param entityDef
     * @param propsList
     * @param options
     * @return
     */
    @Override
    public Command interpretAdd(EntityDefinition entityDef, List<Map<String, Object>> propsList, Map<String, Object> options) {
        Command command = null;

        boolean isCachable = isCachable(propsList);
        boolean isEableMySQLBatchAdd = (propsList.size() > 1) && EntityManagerUtil.isEnableMySQLBatchAdd(options);

        if (isCachable && isEableMySQLBatchAdd == false) {
            command = getCachedAddCmd(entityDef, propsList);
        }

        if (command == null) {
            command = interpreter.interpretAdd(entityDef, propsList, options);

            if (isCachable && isEableMySQLBatchAdd == false) {
                cacheAddCmd(entityDef, propsList, command);
            }
        }

        return command;
    }

    /**
     *
     * @param entityDef
     * @param props
     * @param condition
     * @param options
     * @return
     */
    @Override
    public Command interpretUpdate(EntityDefinition entityDef, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        Command command = null;

        // boolean isBatchable = isBatchable(condition);
        //
        // if (isBatchable) {
        // command = getCachedBatchUpdateCmd(entityDef, props, condition);
        //
        // if (command == null) {
        // SQLOperationCommand sqlCommand = (SQLOperationCommand)
        // interpreter.interpretUpdate(entityDef, props,
        // condition, options);
        //
        // if (sqlCommand.isBatch() && isCachable(props, condition)) {
        // cacheBatchUpdateCmd(entityDef, props, sqlCommand);
        // }
        //
        // command = sqlCommand;
        // }
        // } else {
        String conditionKey = null;
        boolean isCachable = isCachable(props, condition);

        if (isCachable) {
            conditionKey = createConditionCacheKey(entityDef, condition);
            command = getCachedUpdateCmd(entityDef, props, condition, conditionKey);
        }

        if (command == null) {
            command = interpreter.interpretUpdate(entityDef, props, condition, options);

            if (isCachable) {
                cacheUpdateCmd(entityDef, props, conditionKey, command);
            }
        }

        // }
        return command;
    }

    /**
     *
     * @param entityDef
     * @param condition
     * @param options
     * @return
     */
    @Override
    public Command interpretDelete(EntityDefinition entityDef, Condition condition, Map<String, Object> options) {
        Command command = null;

        // boolean isBatchable = isBatchable(condition);
        //
        // if (isBatchable) {
        // command = getCachedBatchDeleleteCmd(entityDef, condition);
        //
        // if (command == null) {
        // SQLOperationCommand sqlCommand = (SQLOperationCommand)
        // interpreter.interpretDelete(entityDef,
        // condition, options);
        //
        // if (sqlCommand.isBatch() && isCachable(condition)) {
        // cacheBatchDeleteCmd(entityDef, sqlCommand);
        // }
        //
        // command = sqlCommand;
        // }
        // } else {
        String conditionKey = null;
        boolean isCachable = isCachable(condition);

        if (isCachable) {
            conditionKey = createConditionCacheKey(entityDef, condition);
            command = getCachedDeleteCmd(entityDef, condition, conditionKey);
        }

        if (command == null) {
            command = interpreter.interpretDelete(entityDef, condition, options);

            if (isCachable) {
                cacheDeleteCmd(entityDef, conditionKey, command);
            }
        }

        // }
        return command;
    }

    /**
     *
     * @param entityDef
     * @param propNames
     * @param condition
     * @param options
     * @return
     */
    @Override
    public Command interpretQuery(EntityDefinition entityDef, Collection<String> propNames, Condition condition, Map<String, Object> options) {
        Command command = null;
        String conditionKey = null;
        boolean isCachable = isCachable(condition);

        if (isCachable) {
            conditionKey = createConditionCacheKey(entityDef, condition);
            command = getCachedQueryCmd(entityDef, propNames, condition, conditionKey);
        }

        if (command == null) {
            command = interpreter.interpretQuery(entityDef, propNames, condition, options);

            if (isCachable) {
                cacheQueryCmd(entityDef, propNames, conditionKey, command);
            }
        }

        return command;
    }

    /**
     *
     * @param entityDef
     * @param query
     * @param parameters
     * @param options
     * @return
     */
    @Override
    public Command interpretQuery(EntityDefinition entityDef, String query, List<?> parameters, Map<String, Object> options) {
        Command command = null;

        Map<String, Command> entitySqlQueryMap = null;

        synchronized (sqlQueryCmdPool) {
            entitySqlQueryMap = sqlQueryCmdPool.get(entityDef.getName());

            if (entitySqlQueryMap == null) {
                entitySqlQueryMap = new HashMap<>();
                sqlQueryCmdPool.put(entityDef.getName(), entitySqlQueryMap);
            } else {
                command = entitySqlQueryMap.get(query);
            }
        }

        if (command == null) {
            command = interpreter.interpretQuery(entityDef, query, parameters, options);

            synchronized (sqlQueryCmdPool) {
                Command copy = copy(command);
                entitySqlQueryMap.put(query, copy);
            }
        } else {
            command = command.copy();

            if (parameters != null) {
                for (int i = 0, count = parameters.size(); i < count; i++) {
                    command.setParameter(i, parameters.get(i), null);
                }
            }
        }

        return command;
    }

    /**
     *
     * @param entityDef
     * @param condition
     * @return
     */
    @Override
    public Command interpretCondition(EntityDefinition entityDef, Condition condition) {
        Command command = null;
        String conditionKey = null;
        boolean isCachable = isCachable(condition);

        if (isCachable) {
            conditionKey = createConditionCacheKey(entityDef, condition);
            command = getCachedCondtionCmd(entityDef, condition, conditionKey);
        }

        if (command == null) {
            command = interpreter.interpretCondition(entityDef, condition);

            if (isCachable) {
                cacheCondtionCmd(entityDef, conditionKey, command);
            }
        }

        return command;
    }

    //    private boolean isBatchable(Condition condition) {
    //        if ((condition != null) && (condition.getOperator() == Operator.OR)) {
    //            Junction or = (Junction) condition;
    //            List<Condition> condList = or.getConditions();
    //
    //            if (condList.size() < 2) {
    //                return false;
    //            } else {
    //                for (Condition cond : condList) {
    //                    if (!(cond instanceof EntityId)) {
    //                        return false;
    //                    }
    //                }
    //
    //                return true;
    //            }
    //        } else {
    //            return false;
    //        }
    //    }

    /**
     * Checks if is cachable.
     *
     * @param condition
     * @return true, if is cachable
     */
    private boolean isCachable(Condition condition) {
        if (condition != null) {
            for (Object propValue : condition.getParameters()) {
                if ((propValue != null) && (propValue instanceof Condition)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if is cachable.
     *
     * @param props
     * @return true, if is cachable
     */
    private boolean isCachable(Map<String, Object> props) {
        for (Object propValue : props.values()) {
            if ((propValue != null) && (propValue instanceof Condition)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if is cachable.
     *
     * @param propsList
     * @return true, if is cachable
     */
    private boolean isCachable(List<Map<String, Object>> propsList) {
        for (Map<String, Object> props : propsList) {
            if (!isCachable(props)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if is cachable.
     *
     * @param props
     * @param condition
     * @return true, if is cachable
     */
    private boolean isCachable(Map<String, Object> props, Condition condition) {
        return isCachable(condition) && isCachable(props);
    }

    /**
     * Creates the condition cache key.
     *
     * @param entityDef
     * @param condition
     * @return
     */
    private String createConditionCacheKey(EntityDefinition entityDef, Condition condition) {
        if (condition == null) {
            return entityDef.getName();
        } else {
            // if (condition instanceof EntityId) {
            // EntityId entityId = (EntityId) condition;
            //
            // return (entityId.getPropNames().size() == 1) ?
            // entityId.getPropNames().iterator().next()
            // : entityId.getPropNames().toString();
            // } else if (condition instanceof Expression) {
            if (condition instanceof Expression) {
                return ((Expression) condition).getLiteral();
            } else {
                SQLCommand cmd = (SQLCommand) interpreter.interpretCondition(entityDef, condition);

                return cmd.getSql();
            }
        }
    }

    //    private void cacheAddCmd(EntityDefinition entityDef, Map<String, Object> props, Command command) {
    //        synchronized (addCmdPool) {
    //            Map<List<String>, Map<Integer, Command>> entityAddCmdMap = addCmdPool.get(entityDef.getName());
    //
    //            if (entityAddCmdMap == null) {
    //                entityAddCmdMap = new HashMap<>();
    //                addCmdPool.put(entityDef.getName(), entityAddCmdMap);
    //            }
    //
    //            List<String> propNames = N.asList(props.keySet());
    //            Map<Integer, Command> propNamesCmdMap = entityAddCmdMap.get(propNames);
    //
    //            if (propNamesCmdMap == null) {
    //                propNamesCmdMap = new HashMap<>();
    //                entityAddCmdMap.put(propNames, propNamesCmdMap);
    //            }
    //
    //            propNamesCmdMap.put(1, copy(command));
    //        }
    //    }

    /**
     * Cache add cmd.
     *
     * @param entityDef
     * @param propsList
     * @param command
     */
    private void cacheAddCmd(EntityDefinition entityDef, List<Map<String, Object>> propsList, Command command) {
        synchronized (addCmdPool) {
            final String entityName = entityDef.getName();
            Map<List<String>, Command> entityAddCmdMap = addCmdPool.get(entityName);

            if (entityAddCmdMap == null) {
                entityAddCmdMap = new HashMap<>();
                addCmdPool.put(entityName, entityAddCmdMap);
            }

            if (entityAddCmdMap.size() >= MAX_POOL_SIZE) {
                final List<List<String>> tmp = new ArrayList<>(entityAddCmdMap.keySet());

                for (int i = 0, size = (int) (entityAddCmdMap.size() * 0.2); i < size; i++) {
                    entityAddCmdMap.remove(tmp.get(i));
                }
            }

            entityAddCmdMap.put(new ArrayList<>(propsList.get(0).keySet()), copy(command));
        }
    }

    //    private Command getCachedAddCmd(EntityDefinition entityDef, Map<String, Object> props) {
    //        List<String> propNames = N.asList(props.keySet());
    //        Command cachedcommand = null;
    //
    //        synchronized (addCmdPool) {
    //            Map<List<String>, Map<Integer, Command>> entityAddCmdMap = addCmdPool.get(entityDef.getName());
    //
    //            if (entityAddCmdMap != null) {
    //                Map<Integer, Command> propNamesCmdMap = entityAddCmdMap.get(propNames);
    //
    //                if (propNamesCmdMap != null) {
    //                    cachedcommand = propNamesCmdMap.get(1);
    //                }
    //            }
    //        }
    //
    //        if (cachedcommand != null) {
    //            Command command = cachedcommand.copy();
    //
    //            for (String propName : propNames) {
    //                setParameter(command, props.get(propName));
    //            }
    //
    //            return command;
    //        } else {
    //            return null;
    //        }
    //    }

    /**
     * Gets the cached add cmd.
     *
     * @param entityDef
     * @param propsList
     * @return
     */
    private Command getCachedAddCmd(EntityDefinition entityDef, List<Map<String, Object>> propsList) {
        final List<String> propNames = new ArrayList<>(propsList.get(0).keySet());

        SQLOperationCommand cachedcommand = null;

        synchronized (addCmdPool) {
            String entityName = entityDef.getName();
            Map<List<String>, Command> entityAddCmdMap = addCmdPool.get(entityName);

            if (entityAddCmdMap != null) {
                cachedcommand = (SQLOperationCommand) entityAddCmdMap.get(propNames);
            }
        }

        if (cachedcommand != null) {
            SQLOperationCommand command = cachedcommand.copy();

            for (Map<String, Object> props : propsList) {
                for (String propName : propNames) {
                    setParameter(command, props.get(propName));
                }

                if (propsList.size() > 1) {
                    command.addBatch();
                }
            }

            return command;
        } else {
            return null;
        }
    }

    /**
     * Cache update cmd.
     *
     * @param entityDef
     * @param props
     * @param conditionKey
     * @param command
     */
    private void cacheUpdateCmd(EntityDefinition entityDef, Map<String, Object> props, String conditionKey, Command command) {
        synchronized (updateCmdPool) {
            Map<String, Map<List<String>, Command>> entityUpdateCmdMap = updateCmdPool.get(entityDef.getName());

            if (entityUpdateCmdMap == null) {
                entityUpdateCmdMap = new HashMap<>();
                updateCmdPool.put(entityDef.getName(), entityUpdateCmdMap);
            }

            Map<List<String>, Command> condUpdateCmdMap = entityUpdateCmdMap.get(conditionKey);

            if (condUpdateCmdMap == null) {
                condUpdateCmdMap = new HashMap<>();
                entityUpdateCmdMap.put(conditionKey, condUpdateCmdMap);
            }

            if (condUpdateCmdMap.size() >= MAX_POOL_SIZE) {
                final List<List<String>> tmp = new ArrayList<>(condUpdateCmdMap.keySet());

                for (int i = 0, size = (int) (condUpdateCmdMap.size() * 0.2); i < size; i++) {
                    condUpdateCmdMap.remove(tmp.get(i));
                }
            }

            condUpdateCmdMap.put(new ArrayList<>(props.keySet()), copy(command));
        }
    }

    /**
     * Gets the cached update cmd.
     *
     * @param entityDef
     * @param props
     * @param condition
     * @param conditionKey
     * @return
     */
    private Command getCachedUpdateCmd(EntityDefinition entityDef, Map<String, Object> props, Condition condition, String conditionKey) {
        final List<String> propNames = new ArrayList<>(props.keySet());

        Command cachedCommand = null;

        synchronized (updateCmdPool) {
            Map<String, Map<List<String>, Command>> entityUpdateCmdMap = updateCmdPool.get(entityDef.getName());

            if (entityUpdateCmdMap != null) {
                Map<List<String>, Command> cachedCondUpdateCmdMap = entityUpdateCmdMap.get(conditionKey);

                if (cachedCondUpdateCmdMap != null) {
                    cachedCommand = cachedCondUpdateCmdMap.get(new ArrayList<>(props.keySet()));
                }
            }
        }

        if (cachedCommand != null) {
            Command command = cachedCommand.copy();

            for (String propName : propNames) {
                setParameter(command, props.get(propName));
            }

            if ((condition != null) && N.notNullOrEmpty(condition.getParameters())) {
                for (Object propValue : condition.getParameters()) {
                    setParameter(command, propValue);
                }
            }

            return command;
        } else {
            return null;
        }
    }

    /**
     * Cache delete cmd.
     *
     * @param entityDef
     * @param conditionKey
     * @param command
     */
    private void cacheDeleteCmd(EntityDefinition entityDef, String conditionKey, Command command) {
        synchronized (deleteCmdPool) {
            Map<String, Command> entityDeleteCmdMap = deleteCmdPool.get(entityDef.getName());

            if (entityDeleteCmdMap == null) {
                entityDeleteCmdMap = new HashMap<>();
                deleteCmdPool.put(entityDef.getName(), entityDeleteCmdMap);
            }

            if (entityDeleteCmdMap.size() >= MAX_POOL_SIZE) {
                final List<String> tmp = new ArrayList<>(entityDeleteCmdMap.keySet());

                for (int i = 0, size = (int) (entityDeleteCmdMap.size() * 0.2); i < size; i++) {
                    entityDeleteCmdMap.remove(tmp.get(i));
                }
            }

            entityDeleteCmdMap.put(conditionKey, copy(command));
        }
    }

    /**
     * Gets the cached delete cmd.
     *
     * @param entityDef
     * @param condition
     * @param conditionKey
     * @return
     */
    private Command getCachedDeleteCmd(EntityDefinition entityDef, Condition condition, String conditionKey) {
        Command command = null;

        synchronized (deleteCmdPool) {
            Map<String, Command> entityDeleteCmdMap = deleteCmdPool.get(entityDef.getName());

            if (entityDeleteCmdMap != null) {
                command = entityDeleteCmdMap.get(conditionKey);
            }
        }

        if (command != null) {
            command = command.copy();

            if ((condition != null) && N.notNullOrEmpty(condition.getParameters())) {
                for (Object propValue : condition.getParameters()) {
                    setParameter(command, propValue);
                }
            }
        }

        return command;
    }

    /**
     * Cache query cmd.
     *
     * @param entityDef
     * @param propNames
     * @param conditionKey
     * @param command
     */
    private void cacheQueryCmd(EntityDefinition entityDef, Collection<String> propNames, String conditionKey, Command command) {
        synchronized (queryCmdPool) {
            Map<String, Map<List<String>, Command>> entityQueryCmdMap = queryCmdPool.get(entityDef.getName());

            if (entityQueryCmdMap == null) {
                entityQueryCmdMap = new HashMap<>();
                queryCmdPool.put(entityDef.getName(), entityQueryCmdMap);
            }

            Map<List<String>, Command> condQueryCmdMap = entityQueryCmdMap.get(conditionKey);

            if (condQueryCmdMap == null) {
                condQueryCmdMap = new HashMap<>();
                entityQueryCmdMap.put(conditionKey, condQueryCmdMap);
            }

            if (condQueryCmdMap.size() >= MAX_POOL_SIZE) {
                final List<List<String>> tmp = new ArrayList<>(condQueryCmdMap.keySet());

                for (int i = 0, size = (int) (condQueryCmdMap.size() * 0.2); i < size; i++) {
                    condQueryCmdMap.remove(tmp.get(i));
                }
            }

            condQueryCmdMap.put(new ArrayList<>(propNames), copy(command));
        }
    }

    /**
     * Gets the cached query cmd.
     *
     * @param entityDef
     * @param propNames
     * @param condition
     * @param conditionKey
     * @return
     */
    private Command getCachedQueryCmd(EntityDefinition entityDef, Collection<String> propNames, Condition condition, String conditionKey) {
        Command command = null;

        synchronized (queryCmdPool) {
            Map<String, Map<List<String>, Command>> entityQueryCmdMap = queryCmdPool.get(entityDef.getName());

            if (entityQueryCmdMap != null) {
                Map<List<String>, Command> condQueryCmdMap = entityQueryCmdMap.get(conditionKey);

                if (condQueryCmdMap != null) {
                    command = condQueryCmdMap.get((propNames instanceof List) ? ((List<?>) propNames) : new ArrayList<>(propNames));
                }
            }
        }

        if (command != null) {
            command = command.copy();

            if ((condition != null) && N.notNullOrEmpty(condition.getParameters())) {
                for (Object propValue : condition.getParameters()) {
                    setParameter(command, propValue);
                }
            }
        }

        return command;
    }

    /**
     * Cache condtion cmd.
     *
     * @param entityDef
     * @param conditionKey
     * @param command
     */
    private void cacheCondtionCmd(EntityDefinition entityDef, String conditionKey, Command command) {
        synchronized (conditionCmdPool) {
            Map<String, Command> entityConditionCmdMap = conditionCmdPool.get(entityDef.getName());

            if (entityConditionCmdMap == null) {
                entityConditionCmdMap = new HashMap<>();
                conditionCmdPool.put(entityDef.getName(), entityConditionCmdMap);
            }

            if (entityConditionCmdMap.size() >= MAX_POOL_SIZE) {
                final List<String> tmp = new ArrayList<>(entityConditionCmdMap.keySet());

                for (int i = 0, size = (int) (entityConditionCmdMap.size() * 0.2); i < size; i++) {
                    entityConditionCmdMap.remove(tmp.get(i));
                }
            }

            Command copy = copy(command);
            entityConditionCmdMap.put(conditionKey, copy);
        }
    }

    /**
     * Gets the cached condtion cmd.
     *
     * @param entityDef
     * @param condition
     * @param conditionKey
     * @return
     */
    private Command getCachedCondtionCmd(EntityDefinition entityDef, Condition condition, String conditionKey) {
        Command command = null;

        synchronized (conditionCmdPool) {
            Map<String, Command> entityConditionCmdMap = conditionCmdPool.get(entityDef.getName());

            if (entityConditionCmdMap != null) {
                command = entityConditionCmdMap.get(conditionKey);
            }
        }

        if (command != null) {
            command = command.copy();

            if ((condition != null) && N.notNullOrEmpty(condition.getParameters())) {
                for (Object propValue : condition.getParameters()) {
                    setParameter(command, propValue);
                }
            }
        }

        return command;
    }

    //    protected void cacheBatchUpdateCmd(EntityDefinition entityDef, Map<String, Object> props, SQLOperationCommand sqlCommand) {
    //        synchronized (batchUpdateCmdPool) {
    //            Map<Set<String>, SQLOperationCommand> entityBatchUpdateMap = batchUpdateCmdPool.get(entityDef.getName());
    //
    //            if (entityBatchUpdateMap == null) {
    //                entityBatchUpdateMap = new HashMap<>();
    //                batchUpdateCmdPool.put(entityDef.getName(), entityBatchUpdateMap);
    //            }
    //
    //            SQLOperationCommand copy = sqlCommand.copy();
    //            copy.clearBatchParameters();
    //            copy.clearParameters();
    //            entityBatchUpdateMap.put(N.asSet(props.keySet()), copy);
    //        }
    //    }
    //
    //    protected SQLOperationCommand getCachedBatchUpdateCmd(EntityDefinition entityDef, Map<String, Object> props, Condition condition) {
    //        SQLOperationCommand sqlCommand = null;
    //
    //        synchronized (batchUpdateCmdPool) {
    //            Map<Set<String>, SQLOperationCommand> entityBatchUpdateMap = batchUpdateCmdPool.get(entityDef.getName());
    //
    //            if (entityBatchUpdateMap != null) {
    //                sqlCommand = entityBatchUpdateMap.get(props.keySet());
    //
    //                if (sqlCommand != null) {
    //                    sqlCommand = sqlCommand.copy();
    //
    //                    List<Condition> entityIdCondList = ((Junction) condition).getConditions();
    //
    //                    for (Condition cond : entityIdCondList) {
    //                        for (Object propValue : props.values()) {
    //                            setParameter(sqlCommand, propValue);
    //                        }
    //
    //                        for (Object parameterValue : cond.getParameters()) {
    //                            setParameter(sqlCommand, parameterValue);
    //                        }
    //
    //                        sqlCommand.addBatch();
    //                    }
    //                }
    //            }
    //        }
    //
    //        return sqlCommand;
    //    }
    //
    //    protected void cacheBatchDeleteCmd(EntityDefinition entityDef, SQLOperationCommand sqlCommand) {
    //        synchronized (batchDeleteCmdPool) {
    //            SQLOperationCommand copy = sqlCommand.copy();
    //            copy.clearBatchParameters();
    //            copy.clearParameters();
    //            batchDeleteCmdPool.put(entityDef.getName(), copy);
    //        }
    //    }
    //
    //    protected SQLOperationCommand getCachedBatchDeleleteCmd(EntityDefinition entityDef, Condition condition) {
    //        SQLOperationCommand sqlCommand = null;
    //
    //        synchronized (batchDeleteCmdPool) {
    //            sqlCommand = batchDeleteCmdPool.get(entityDef.getName());
    //
    //            if (sqlCommand != null) {
    //                sqlCommand = sqlCommand.copy();
    //
    //                List<Condition> entityIdCondList = ((Junction) condition).getConditions();
    //
    //                for (Condition cond : entityIdCondList) {
    //                    for (Object parameterValue : cond.getParameters()) {
    //                        setParameter(sqlCommand, parameterValue);
    //                    }
    //
    //                    sqlCommand.addBatch();
    //                }
    //            }
    //        }
    //
    //        return sqlCommand;
    //    }

    /**
     * Sets the parameter.
     *
     * @param command
     * @param parameterValue
     */
    private void setParameter(Command command, Object parameterValue) {
        command.setParameter(command.getParameterCount(), parameterValue, command.getParameterType(command.getParameterCount()));
    }

    /**
     *
     * @param command
     * @return
     */
    private Command copy(Command command) {
        Command copy = command.copy();

        if (copy instanceof SQLOperationCommand) {
            SQLOperationCommand sqlCmd = (SQLOperationCommand) copy;

            if (sqlCmd.isBatch()) {
                sqlCmd.clearBatchParameters();
            }
        }

        copy.clearParameters();

        return copy;
    }
}

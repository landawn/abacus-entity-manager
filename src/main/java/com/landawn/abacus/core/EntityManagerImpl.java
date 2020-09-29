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

package com.landawn.abacus.core;

import static com.landawn.abacus.core.EntityManagerUtil.checkPropsList;
import static com.landawn.abacus.core.EntityManagerUtil.checkResultHandle;
import static com.landawn.abacus.core.EntityManagerUtil.checkSelectPropNamesForGet;
import static com.landawn.abacus.core.EntityManagerUtil.cleanUpDirtyPropNames;
import static com.landawn.abacus.core.EntityManagerUtil.entity2Map;
import static com.landawn.abacus.core.EntityManagerUtil.entityId2Condition;
import static com.landawn.abacus.core.EntityManagerUtil.getBatchSize;
import static com.landawn.abacus.core.EntityManagerUtil.getEntityIdByEntity;
import static com.landawn.abacus.core.EntityManagerUtil.getPropValue;
import static com.landawn.abacus.core.EntityManagerUtil.getUpdatedProps;
import static com.landawn.abacus.core.EntityManagerUtil.hasOffsetCount;
import static com.landawn.abacus.core.EntityManagerUtil.isCacheResult;
import static com.landawn.abacus.core.EntityManagerUtil.isGetByResultHandle;
import static com.landawn.abacus.core.EntityManagerUtil.isGetFromCache;
import static com.landawn.abacus.core.EntityManagerUtil.isInTransaction;
import static com.landawn.abacus.core.EntityManagerUtil.isResultCombined;
import static com.landawn.abacus.core.EntityManagerUtil.parseInsertPropsList;
import static com.landawn.abacus.core.EntityManagerUtil.parseSelectPropNamesInGet;
import static com.landawn.abacus.core.EntityManagerUtil.parseSelectPropNamesInQuery;
import static com.landawn.abacus.core.EntityManagerUtil.parseUpdateProps;
import static com.landawn.abacus.core.EntityManagerUtil.removeOffsetCount;
import static com.landawn.abacus.core.EntityManagerUtil.resultSet2EntityId;
import static com.landawn.abacus.core.EntityManagerUtil.setIdValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.condition.CriteriaUtil;
import com.landawn.abacus.condition.Expression;
import com.landawn.abacus.condition.Or;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.core.command.SQLCommand;
import com.landawn.abacus.exception.DuplicatedResultException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.metadata.Association;
import com.landawn.abacus.metadata.Association.JoinType;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.OnDeleteAction;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.ExceptionUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Query;
import com.landawn.abacus.util.u.Holder;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @param <E>
 * @since 0.8
 */
class EntityManagerImpl<E> extends AbstractEntityManager<E> {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerImpl.class);

    private static final Condition NON_EXISTED_CONDITION = CF.eq("1", "2");

    private final Map<String, Map<Collection<String>, SelectPropNameView>> getSelectPropNameViewPool = new ConcurrentHashMap<>();

    private final Map<String, Map<Collection<String>, SelectPropNameView>> querySelectPropNameViewPool = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Map<Collection<String>, CachedQueryCmd>>> queryCmdPool = new HashMap<>();

    protected final DBAccessImpl dbAccess;

    protected EntityManagerImpl(EntityManagerConfiguration entityManagerConfig, DBAccessImpl dbAccess) {
        super(dbAccess.getEntityDefinitionFactory().domainName(), entityManagerConfig);
        this.dbAccess = dbAccess;
    }

    /**
     * Internal get entity definition factory.
     *
     * @return
     */
    @Override
    protected EntityDefinitionFactory internalGetEntityDefinitionFactory() {
        return dbAccess.getEntityDefinitionFactory();
    }

    /**
     *
     * @param <T>
     * @param targetClass
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    protected <T> T internalGet(Class<T> targetClass, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) {
        List<T> entities = internalGet(targetClass, N.asList(entityId), selectPropNames, options);

        if (entities.size() > 1) {
            throw new DuplicatedResultException("More than one records are found by [entityId]: " + entityId + ". [entities]: " + N.toString(entities));
        }

        return (entities.size() == 0) ? null : entities.get(0);
    }

    /**
     *
     * @param <T>
     * @param targetClass
     * @param entityIds
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    protected <T> List<T> internalGet(Class<T> targetClass, List<? extends EntityId> entityIds, Collection<String> selectPropNames,
            Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityId(entityIds.get(0));

        if (targetClass == null) {
            targetClass = entityDef.getTypeClass();
        }

        if (hasOffsetCount(options)) {
            throw new IllegalArgumentException("Can not set offset[" + options.get(Query.OFFSET) + "] or count[" + options.get(Query.COUNT)
                    + "] options when get entity by EntityId(s). ");
        }

        List<T> entities = getEntities(targetClass, entityDef, entityIds, selectPropNames, options);

        if (entities.size() > entityIds.size()) {
            throw new DuplicatedResultException(
                    "More than one records are found by [entityIds]: " + N.toString(entityIds) + ". [entities]: " + N.toString(entities));
        }

        return entities;
    }

    /**
     * Gets the entities.
     *
     * @param <T>
     * @param targetClass
     * @param entityDef
     * @param entityIds
     * @param selectPropNames
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getEntities(Class<T> targetClass, final EntityDefinition entityDef, List<? extends EntityId> entityIds,
            Collection<String> selectPropNames, Map<String, Object> options) {
        if (targetClass == null) {
            targetClass = entityDef.getTypeClass();
        }

        if (entityIds.size() == 0) {
            return new ArrayList<>();
        }

        final int batchSize = getBatchSize(options);

        if (entityIds.size() <= batchSize) {
            return getEntities(targetClass, entityDef, selectPropNames, entityId2Condition(entityIds), options);
        } else {
            List<T> entityList = new ArrayList<>(entityIds.size());

            for (int size = entityIds.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = N.min(from + batchSize, size)) {
                entityList.addAll(getEntities(targetClass, entityDef, selectPropNames, entityId2Condition(entityIds, from, to), options));
            }

            return entityList;
        }
    }

    /**
     * Gets the entities.
     *
     * @param <T>
     * @param targetClass
     * @param entityDef
     * @param selectPropNames
     * @param condition
     * @param options
     * @return
     */
    protected <T> List<T> getEntities(Class<T> targetClass, final EntityDefinition entityDef, Collection<String> selectPropNames, Condition condition,
            Map<String, Object> options) {
        SelectPropNameView xa = checkSelectPropNamesInGet(entityDef, selectPropNames);

        DataSet rs = dbAccess.query(entityDef.getName(), xa.simplePropNames, condition, null, options);

        if (rs.size() > 0) {
            if (xa.entityPropNames.size() > 0) {
                options = removeOffsetCount(options);

                for (String propName : xa.entityPropNames) {
                    Property entityProp = entityDef.getProperty(propName);
                    Map<Object, Object> propValueEntityMap = getPropEntity(entityProp, rs, options);

                    final List<Object> column = new ArrayList<>(rs.size());
                    final Property srcProp = entityProp.getAssociation().getSrcProperty();
                    final int srcPropIndex = rs.getColumnIndex(srcProp.getName());

                    for (int i = 0; i < rs.size(); i++) {
                        rs.absolute(i);
                        column.add(propValueEntityMap.get(rs.get(srcPropIndex)));
                    }

                    rs.addColumn(propName, column);
                }
            }
        }

        return resultList2Entities(targetClass, entityDef, rs);
    }

    /**
     * Check select prop names in get.
     *
     * @param entityDef
     * @param selectPropNames
     * @return
     */
    private SelectPropNameView checkSelectPropNamesInGet(final EntityDefinition entityDef, Collection<String> selectPropNames) {
        SelectPropNameView xa = null;
        Map<Collection<String>, SelectPropNameView> entitySelectPropNamesView = getSelectPropNameViewPool.get(entityDef.getName());

        if (entitySelectPropNamesView == null) {
            entitySelectPropNamesView = new HashMap<>();
            getSelectPropNameViewPool.put(entityDef.getName(), entitySelectPropNamesView);
        } else {
            xa = entitySelectPropNamesView.get(selectPropNames);
        }

        if (xa == null) {
            checkSelectPropNamesForGet(entityDef, selectPropNames);

            xa = parseSelectPropNamesInGet(entityDef, selectPropNames);

            synchronized (getSelectPropNameViewPool) {
                entitySelectPropNamesView.put(selectPropNames, xa);
            }
        }

        return xa;
    }

    /**
     *
     * @param cls
     * @param prop
     * @param dataSet
     * @param isResultCombined
     */
    protected void composeProperty(Class<?> cls, Property prop, DataSet dataSet, boolean isResultCombined) {
        EntityDefinition columnEntityDef = prop.getColumnEntityDef();

        Class<?> propClass = null;

        if ((cls == null) || MapEntity.class.isAssignableFrom(cls)) {
            propClass = columnEntityDef.getTypeClass();
        } else {
            propClass = prop.getGetMethod(cls).getReturnType();
        }

        final List<?> entities = DataSetUtil.row2Entity(propClass, (RowDataSet) dataSet, columnEntityDef, 0, dataSet.size());

        dataSet.addColumn(prop.getName(), entities);

        if (isResultCombined && prop.isCollection()) {
            final EntityDefinition entityDef = prop.getEntityDefinition();
            final String[] idPropNames = new String[entityDef.getIdPropertyList().size()];

            for (int i = 0, size = entityDef.getIdPropertyList().size(); i < size; i++) {
                idPropNames[i] = entityDef.getIdPropertyList().get(i).getName();
            }

            DataSetUtil.combine((RowDataSet) dataSet, prop, idPropNames);
        }
    }

    /**
     * Gets the prop entity.
     *
     * @param entityProp
     * @param rs
     * @param options
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    protected Map<Object, Object> getPropEntity(Property entityProp, DataSet rs, Map<String, Object> options) {
        if (N.isNullOrEmpty(rs)) {
            return new HashMap<>();
        }

        final EntityDefinition columnEntityDef = entityProp.getColumnEntityDef();
        final String propEntityName = columnEntityDef.getName();
        final Association association = entityProp.getAssociation();
        final Property srcProp = association.getSrcProperty();
        final int srcPropIndex = rs.getColumnIndex(srcProp.getName());

        final Property targetProp = association.getTargetProperty();
        final String targetPropName = targetProp.getName();

        final EntityDefinition biEntityDef = association.getBiEntityDef();

        boolean isIdReference = targetProp.isId();

        final int batchSize = getBatchSize(options);
        final Class<?> propEntityClass = columnEntityDef.getTypeClass();
        final Collection<String> propEntitySelectPropNames = entityProp.getSubPropertyNameList();

        final Map<Object, Object> propValueEntityMap = new HashMap<>();
        final List<Object> propEntityList = new ArrayList<>();

        if (biEntityDef == null) {
            if (isIdReference) {
                final List<EntityId> entityIds = new ArrayList<>(rs.size());

                for (int i = 0; i < rs.size(); i++) {
                    rs.absolute(i);
                    entityIds.add(EntityId.of(propEntityName, targetPropName, rs.get(srcPropIndex)));
                }

                if (N.notNullOrEmpty(entityIds)) {
                    List<?> entities = internalGet(propEntityClass, entityIds, propEntitySelectPropNames, options);

                    if (N.notNullOrEmpty(entities)) {
                        propEntityList.addAll(entities);
                    }
                }
            } else {
                for (int size = rs.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = Math.min(from + batchSize, size)) {
                    Or propEntityCond = CF.or();

                    for (int i = from; i < to; i++) {
                        rs.absolute(i);

                        propEntityCond.add(CF.eq(targetPropName, rs.get(srcPropIndex)));
                    }

                    Condition cond = N.isNullOrEmpty(entityProp.getOrderBy()) ? propEntityCond
                            : CF.criteria().where(propEntityCond).orderBy(CF.expr(entityProp.getOrderBy()));

                    List<?> entities = internalList(propEntityClass, propEntityName, propEntitySelectPropNames, cond, options);

                    if (N.notNullOrEmpty(entities)) {
                        for (Object entity : entities) {
                            propEntityList.add(entity);
                        }
                    }
                }
            }

            Object foreignPropValue = null;
            for (Object entity : propEntityList) {
                foreignPropValue = getPropValue(entity, targetProp);

                if (entityProp.isCollection()) {
                    Collection<Object> c = (Collection<Object>) propValueEntityMap.get(foreignPropValue);

                    if (c == null) {
                        c = entityProp.asCollection();
                        propValueEntityMap.put(foreignPropValue, c);
                    }

                    c.add(entity);
                } else {
                    propValueEntityMap.put(foreignPropValue, entity);
                }
            }

        } else {
            final Property leftProp = association.getBiEntityProperties()[0];
            final Property rightProp = association.getBiEntityProperties()[1];

            final List<String> biSelectPropNames = N.asList(leftProp.getName(), rightProp.getName());
            final Map<Object, Collection<Object>> rightLeftPropValueMap = new HashMap<>();

            for (int size = rs.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = Math.min(from + batchSize, size)) {
                Or or = CF.or();

                for (int i = from; i < to; i++) {
                    rs.absolute(i);

                    or.add(CF.eq(leftProp.getName(), rs.get(srcPropIndex)));
                }

                DataSet biResultSet = dbAccess.query(biEntityDef.getName(), biSelectPropNames, or, null, options);

                if (N.isNullOrEmpty(biResultSet)) {
                    continue;
                }

                if (isIdReference) {
                    final List<EntityId> entityIds = new ArrayList<>(biResultSet.size());

                    for (int i = 0; i < biResultSet.size(); i++) {
                        biResultSet.absolute(i);

                        entityIds.add(EntityId.of(propEntityName, targetPropName, biResultSet.get(1)));
                    }

                    List<?> entities = internalGet(propEntityClass, entityIds, propEntitySelectPropNames, options);

                    if (N.notNullOrEmpty(entities)) {
                        propEntityList.addAll(entities);
                    }
                } else {
                    Or propEntityCond = CF.or();

                    for (int i = 0; i < biResultSet.size(); i++) {
                        biResultSet.absolute(i);

                        propEntityCond.add(CF.eq(targetPropName, biResultSet.get(1)));
                    }

                    Condition cond = N.isNullOrEmpty(entityProp.getOrderBy()) ? propEntityCond
                            : CF.criteria().where(propEntityCond).orderBy(CF.expr(entityProp.getOrderBy()));

                    List<?> entities = internalList(propEntityClass, propEntityName, propEntitySelectPropNames, cond, options);

                    if (N.notNullOrEmpty(entities)) {
                        for (Object entity : entities) {
                            propEntityList.add(entity);
                        }
                    }
                }

                Object rightPropValue = null;
                for (int i = 0; i < biResultSet.size(); i++) {
                    rightPropValue = biResultSet.absolute(i).get(1);

                    Collection<Object> c = rightLeftPropValueMap.get(rightPropValue);
                    if (c == null) {
                        c = N.newHashSet();

                        rightLeftPropValueMap.put(rightPropValue, c);
                    }

                    c.add(biResultSet.get(0));
                }
            }

            // ============
            if (N.notNullOrEmpty(propEntityList)) {
                Object foreignPropValue = null;

                for (Object entity : propEntityList) {
                    foreignPropValue = getPropValue(entity, targetProp);

                    for (Object leftPropValue : rightLeftPropValueMap.get(foreignPropValue)) {
                        if (entityProp.isCollection()) {
                            Collection<Object> c = (Collection<Object>) propValueEntityMap.get(leftPropValue);

                            if (c == null) {
                                c = entityProp.asCollection();
                                propValueEntityMap.put(leftPropValue, c);
                            }

                            c.add(entity);
                        } else {
                            propValueEntityMap.put(leftPropValue, entity);
                        }
                    }
                }
            }
            // ============
        }

        return propValueEntityMap;
    }

    //    protected Class getPropEntityClass(Class clazz, Property entityProp, EntityDefinition propEntityDef) {
    //        Class propClass = null;
    //
    //        if ((clazz == null) || MapEntity.class.isAssignableFrom(clazz)) {
    //            propClass = propEntityDef.getTypeClass();
    //        } else {
    //            if (entityProp.isCollection()) {
    //                // try to find the same package POJO class.
    //                try {
    //                    propClass = N.forClass(clazz.getPackage().getName() + D.PERIOD + propEntityDef.getTypeClass().getSimpleName());
    //                } catch (ClassNotFoundException e) {
    //                    propClass = propEntityDef.getTypeClass();
    //                }
    //            } else {
    //                propClass = entityProp.getGetMethod(clazz).getReturnType();
    //            }
    //        }
    //
    //        return propClass;
    //
    //        return propEntityDef.getTypeClass();
    //    }

    /**
     *
     * @param <T>
     * @param targetClass
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     * @return
     */
    @Override
    protected <T> List<T> internalList(Class<T> targetClass, String entityName, Collection<String> selectPropNames, Condition condition,
            Map<String, Object> options) {
        return getEntities(targetClass, checkEntityName(entityName), selectPropNames, condition, options);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    protected EntityId internalAdd(E entity, Map<String, Object> options) {
        return internalAdd(N.asList(entity), options).get(0);
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    protected List<EntityId> internalAdd(Collection<? extends E> entities, Map<String, Object> options) {
        return addEntities(checkEntity(entities.iterator().next()), entities, options);
    }

    /**
     * Adds the entities.
     *
     * @param entityDef
     * @param entities
     * @param options
     * @return
     */
    protected List<EntityId> addEntities(final EntityDefinition entityDef, Collection<? extends E> entities, Map<String, Object> options) {
        List<EntityId> entityIds = addEntities(entityDef, entity2Map(entityDef, entities), options);

        int idx = 0;

        for (Object entity : entities) {
            setIdValue(entityDef, entity, entityIds.get(idx++));
        }

        cleanUpDirtyPropNames(entities);

        return entityIds;
    }

    /**
     *
     * @param entityName
     * @param props
     * @param options
     * @return
     */
    @Override
    protected EntityId internalAdd(String entityName, Map<String, Object> props, Map<String, Object> options) {
        return internalAdd(entityName, ParametersUtil.asList(props), options).get(0);
    }

    /**
     *
     * @param entityName
     * @param propsList
     * @param options
     * @return
     */
    @Override
    protected List<EntityId> internalAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options) {
        checkPropsList(propsList);

        return addEntities(checkEntityName(entityName), propsList, options);
    }

    /**
     * Adds the entities.
     *
     * @param entityDef
     * @param propsList
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<EntityId> addEntities(final EntityDefinition entityDef, List<Map<String, Object>> propsList, Map<String, Object> options) {
        final String entityName = entityDef.getName();
        List<EntityId> entityIds = null;
        Map<Property, List<Object>> entityProps = EntityManagerUtil.getPropEntity(entityDef, propsList);

        if (entityProps.size() == 0) {
            entityIds = dbAccess.addAll(entityName, propsList, options);
        } else {
            String transactionId = null;

            if (!isInTransaction(options)) {
                transactionId = dbAccess.startDefaultTransactionForUpdate(options);
                options = ParametersUtil.copy(options);
                options.put(Options.TRANSACTION_ID, transactionId);
            }

            boolean noException = false;

            try {
                for (Property prop : entityProps.keySet()) {
                    if (prop.getAssociation().getJoinType() == JoinType.OUTER) {
                        List<Object> entityList = entityProps.get(prop);

                        if (N.notNullOrEmpty(entityList)) {
                            internalAdd((List<E>) entityList, options);
                        }
                    }
                }

                InsertPropsListView insertPropsListView = parseInsertPropsList(entityDef, propsList, true);
                propsList = insertPropsListView.writePropsList;

                entityIds = dbAccess.addAll(entityName, propsList, true, options);

                Map<Property, List<PropEntityProps>> propEntitiesList = insertPropsListView.propEntityPropsList;
                Map<Property, List<BiEntityProps>> propBiEntityPropsList = insertPropsListView.propBiEntityPropsList;

                List<PropEntityProps> propEntities = null;
                List<BiEntityProps> biEntiyPropsList = null;

                for (Property prop : propEntitiesList.keySet()) {
                    if (prop.getAssociation().getJoinType() == JoinType.INNER) {
                        propEntities = propEntitiesList.get(prop);

                        if (propEntities.size() > 0) {
                            final List<Object> entities = new ArrayList<>(propEntities.size());

                            for (PropEntityProps propEntity : propEntities) {
                                entities.add(propEntity.getPropEntity());
                            }

                            internalAdd((List<E>) entities, options);
                        }

                        if (N.notNullOrEmpty(propBiEntityPropsList)) {
                            biEntiyPropsList = propBiEntityPropsList.get(prop);

                            if (N.notNullOrEmpty(biEntiyPropsList)) {
                                List<Map<String, Object>> biPropsList = new ArrayList<>(biEntiyPropsList.size());

                                for (BiEntityProps biEntityProps : biEntiyPropsList) {
                                    biPropsList.add(biEntityProps.createProps());
                                }

                                internalAdd(prop.getAssociation().getBiEntityDef().getName(), biPropsList, options);
                            }
                        }
                    }
                }

                noException = true;
            } finally {
                if (noException) {
                    commitTransaction(transactionId, options);
                } else {
                    rollbackTransaction(transactionId, options);
                }
            }
        }

        return entityIds;
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    protected int internalUpdate(E entity, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntity(entity);
        final EntityId entityId = getEntityIdByEntity(entityDef, entity);
        final Map<String, Object> props = getUpdatedProps(entityDef, entity);

        int result = internalUpdate(entityId, props, options);

        cleanUpDirtyPropNames(entity);

        if (!isInTransaction(options)) {
            // can not reback for cache and version.
            // RecycableEntityId.reback(entityId);
        }

        return result;
    }

    /**
     *
     * @param entityId
     * @param props
     * @param options
     * @return
     */
    @Override
    protected int internalUpdate(EntityId entityId, Map<String, Object> props, Map<String, Object> options) {
        return internalUpdate(N.asList(entityId), props, options);
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    protected int internalUpdate(Collection<? extends E> entities, Map<String, Object> options) {
        if (entities.size() == 1) {
            return internalUpdate(entities.iterator().next(), options);
        } else {
            String transactionId = null;

            if (!isInTransaction(options) && (entities.size() > 1)) {
                transactionId = dbAccess.startDefaultTransactionForUpdate(options);
                options = ParametersUtil.copy(options);
                options.put(Options.TRANSACTION_ID, transactionId);
            }

            int result = 0;
            boolean noException = false;

            try {
                for (E entity : entities) {
                    result += internalUpdate(entity, options);
                }

                noException = true;
            } finally {
                if (noException) {
                    commitTransaction(transactionId, options);
                } else {
                    rollbackTransaction(transactionId, options);
                }
            }

            return result;
        }
    }

    /**
     *
     * @param entityIds
     * @param props
     * @param options
     * @return
     */
    @Override
    protected int internalUpdate(List<? extends EntityId> entityIds, Map<String, Object> props, Map<String, Object> options) {
        return updateEntities(checkEntityId(entityIds.get(0)), entityIds, props, options);
    }

    /**
     *
     * @param entityDef
     * @param entityIds
     * @param props
     * @param options
     * @return
     */
    protected int updateEntities(final EntityDefinition entityDef, List<? extends EntityId> entityIds, Map<String, Object> props, Map<String, Object> options) {
        int batchSize = getBatchSize(options);

        if (entityIds.size() <= batchSize) {
            return updateEntities(entityDef, props, entityId2Condition(entityIds), options);
        } else {
            String transactionId = null;

            if (!isInTransaction(options)) {
                transactionId = dbAccess.startDefaultTransactionForUpdate(options);
                options = ParametersUtil.copy(options);
                options.put(Options.TRANSACTION_ID, transactionId);
            }

            int result = 0;
            boolean noException = false;

            try {
                for (int size = entityIds.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = Math.min(from + batchSize, size)) {
                    result += updateEntities(entityDef, props, entityId2Condition(entityIds, from, to), options);
                }

                noException = true;
            } finally {
                if (noException) {
                    commitTransaction(transactionId, options);
                } else {
                    rollbackTransaction(transactionId, options);
                }
            }

            return result;
        }
    }

    /**
     *
     * @param entityName
     * @param props
     * @param condition
     * @param options
     * @return
     */
    @Override
    protected int internalUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        return updateEntities(checkEntityName(entityName), props, condition, options);
    }

    /**
     *
     * @param entityDef
     * @param props
     * @param condition
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    protected int updateEntities(final EntityDefinition entityDef, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        if (N.isNullOrEmpty(props)) {
            throw new IllegalArgumentException("The parameter props can not be null or empty");
        }

        final String entityName = entityDef.getName();
        UpdatePropsView updatePropsView = parseUpdateProps(entityDef, props, true);
        props = updatePropsView.updateProps;

        Map<Property, List<Object>> propEntiyList = updatePropsView.propEntityList;

        int result = 0;

        if (N.isNullOrEmpty(propEntiyList)) {
            result = dbAccess.update(entityName, props, condition, true, options);
        } else {
            String transactionId = null;

            if (!isInTransaction(options)) {
                transactionId = dbAccess.startDefaultTransactionForUpdate(options);
                options = ParametersUtil.copy(options);
                options.put(Options.TRANSACTION_ID, transactionId);
            }

            boolean noException = false;

            try {
                result = dbAccess.update(entityName, props, condition, true, options);

                for (Property prop : propEntiyList.keySet()) {
                    List<Object> propEntities = propEntiyList.get(prop);

                    for (Object propEntity : propEntities) {
                        internalUpdate((E) propEntity, options);
                    }
                }

                noException = true;
            } finally {
                if (noException) {
                    commitTransaction(transactionId, options);
                } else {
                    rollbackTransaction(transactionId, options);
                }
            }
        }

        return result;
    }

    /**
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    protected int internalDelete(EntityId entityId, Map<String, Object> options) {
        return internalDelete(N.asList(entityId), options);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    protected int internalDelete(E entity, Map<String, Object> options) {
        return internalDelete(N.asList(entity), options);
    }

    /**
     *
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    protected int internalDelete(List<? extends EntityId> entityIds, Map<String, Object> options) {
        return deleteEntities(checkEntityId(entityIds.get(0)), entityIds, options);
    }

    /**
     *
     * @param entityDef
     * @param entityIds
     * @param options
     * @return
     */
    protected int deleteEntities(final EntityDefinition entityDef, List<? extends EntityId> entityIds, Map<String, Object> options) {
        int result = 0;

        final int batchSize = getBatchSize(options);
        final boolean isInTransaction = isInTransaction(options);
        String transactionId = null;

        if ((entityIds.size() > batchSize) && !isInTransaction) {
            transactionId = dbAccess.startDefaultTransactionForUpdate(options);
            options = ParametersUtil.copy(options);
            options.put(Options.TRANSACTION_ID, transactionId);
        }

        boolean noException = false;

        try {
            for (int size = entityIds.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = N.min(from + batchSize, size)) {
                List<? extends EntityId> batchEntityIds = entityIds.subList(from, to);
                Condition condition = entityId2Condition(batchEntityIds);

                CascadeDeleteResult cascadeDeleteResult = deleteCascade(entityDef, batchEntityIds, condition, options);

                if (cascadeDeleteResult != null) {
                    if ((cascadeDeleteResult.transactionId != null) && (transactionId == null)) {
                        transactionId = cascadeDeleteResult.transactionId;
                        options = cascadeDeleteResult.options;
                    }

                    condition = cascadeDeleteResult.cond;
                }

                if (condition != NON_EXISTED_CONDITION) {
                    result += dbAccess.delete(entityDef.getName(), condition, options);
                }
            }

            noException = true;
        } finally {
            if (noException) {
                commitTransaction(transactionId, options);
            } else {
                // it's OK to roll back again if it has been rolled back.
                rollbackTransaction(transactionId, options);
            }
        }

        return result;
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    protected int internalDelete(Collection<? extends E> entities, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntity(entities.iterator().next());
        final List<EntityId> entityIds = getEntityIdByEntity(entityDef, entities);

        final int result = internalDelete(entityIds, options);

        cleanUpDirtyPropNames(entities);

        return result;
    }

    /**
     *
     * @param entityName
     * @param cond
     * @param options
     * @return
     */
    @Override
    protected int internalDelete(String entityName, Condition cond, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        int result = 0;
        String transactionId = null;
        boolean noException = false;

        try {
            CascadeDeleteResult cascadeDeleteResult = deleteCascade(entityDef, null, cond, options);

            if (cascadeDeleteResult != null) {
                if ((cascadeDeleteResult.transactionId != null) && (transactionId == null)) {
                    transactionId = cascadeDeleteResult.transactionId;
                    options = cascadeDeleteResult.options;
                }

                cond = cascadeDeleteResult.cond;
            }

            if (cond != NON_EXISTED_CONDITION) {
                result = dbAccess.delete(entityName, cond, options);
            }

            noException = true;
        } finally {
            if (noException) {
                commitTransaction(transactionId, options);
            } else {
                rollbackTransaction(transactionId, options);
            }
        }

        return result;
    }

    /**
     *
     * @param entityDef
     * @param entityIds
     * @param cond
     * @param options
     * @return
     */
    protected CascadeDeleteResult deleteCascade(final EntityDefinition entityDef, List<? extends EntityId> entityIds, Condition cond,
            Map<String, Object> options) {
        Set<String> casadePropNames = null;

        for (Property prop : entityDef.getEntiyPropertyList()) {
            OnDeleteAction ct = prop.getOnDeleteAction();

            if (ct != OnDeleteAction.NO_ACTION) {
                if (casadePropNames == null) {
                    casadePropNames = ParametersUtil.asSet();
                }

                casadePropNames.add(prop.getAssociation().getSrcProperty().getName());
            }
        }

        if (N.isNullOrEmpty(casadePropNames)) {
            return null;
        }

        final int batchSize = getBatchSize(options);

        DataSet rs = null;

        if ((entityIds != null) && entityDef.getIdPropertyNameList().containsAll(casadePropNames)) {
            List<String> columnNameList = new ArrayList<>(entityDef.getIdPropertyNameList());
            List<List<Object>> columnList = new ArrayList<>();

            for (String propName : columnNameList) {
                List<Object> column = new ArrayList<>(entityIds.size());

                for (EntityId entityId : entityIds) {
                    column.add(entityId.get(propName));
                }

                columnList.add(column);
            }

            rs = new RowDataSet(columnNameList, columnList);
        } else {
            if (entityIds == null) {
                casadePropNames.addAll(entityDef.getIdPropertyNameList());
            }

            rs = dbAccess.query(entityDef.getName(), casadePropNames, cond, null, options);

            // can not convert the original condition to the condition composed by entity ids if size is too big.
            if ((rs.size() > 0 && rs.size() <= batchSize) && (entityDef.getIdPropertyList().size() > 0)
                    && ((entityIds == null) || (entityIds.size() > rs.size()))) {
                cond = entityId2Condition(resultSet2EntityId(entityDef, rs));
            }
        }

        if (rs.size() == 0) {
            return new CascadeDeleteResult(null, NON_EXISTED_CONDITION, options);
        }

        String transactionId = null;

        if (!isInTransaction(options)) {
            transactionId = dbAccess.startDefaultTransactionForUpdate(options);
            options = ParametersUtil.copy(options);
            options.put(Options.TRANSACTION_ID, transactionId);
        }

        CascadeDeleteResult cascadeDeleteResult = new CascadeDeleteResult(transactionId, cond, options);

        for (Property prop : entityDef.getEntiyPropertyList()) {
            OnDeleteAction ct = prop.getOnDeleteAction();

            if ((ct == null) || (ct == OnDeleteAction.NO_ACTION)) {
                continue;
            }

            EntityDefinition columnEntityDef = prop.getColumnEntityDef();
            String propEntityName = columnEntityDef.getName();

            Association association = prop.getAssociation();
            Property srcProp = association.getSrcProperty();
            int srcPropIndex = rs.getColumnIndex(srcProp.getName());

            Property targetProp = association.getTargetProperty();
            String targetPropName = targetProp.getName();
            boolean isIdTargetProp = targetProp.isId();

            EntityDefinition biEntityDef = association.getBiEntityDef();

            if (biEntityDef == null) {
                if (isIdTargetProp) {
                    final List<EntityId> propEntityEntityIds = new ArrayList<>(rs.size());

                    for (int i = 0; i < rs.size(); i++) {
                        rs.absolute(i);
                        propEntityEntityIds.add(EntityId.of(propEntityName, targetPropName, rs.get(srcPropIndex)));
                    }

                    boolean noException = false;

                    try {
                        if (ct == OnDeleteAction.CASCADE) {
                            internalDelete(propEntityEntityIds, options);
                        } else if (ct == OnDeleteAction.SET_NULL) {
                            Map<String, Object> props = new HashMap<>();
                            props.put(targetPropName, N.defaultValueOf(targetProp.getType().clazz()));
                            internalUpdate(propEntityEntityIds, props, options);
                        }

                        noException = true;
                    } finally {
                        if (noException) {
                            // commitTransaction(transactionId, options);
                        } else {
                            rollbackTransaction(transactionId, options);
                        }
                    }
                } else {
                    boolean noException = false;

                    try {
                        for (int size = rs.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = N.min(from + batchSize, size)) {
                            final Or targetPropCond = CF.or();

                            for (int i = from; i < to; i++) {
                                rs.absolute(i);
                                targetPropCond.add(CF.eq(targetPropName, rs.get(srcPropIndex)));
                            }

                            if (ct == OnDeleteAction.CASCADE) {
                                internalDelete(propEntityName, targetPropCond, options);
                            } else if (ct == OnDeleteAction.SET_NULL) {
                                Map<String, Object> props = new HashMap<>();
                                props.put(targetPropName, N.defaultValueOf(targetProp.getType().clazz()));
                                internalUpdate(propEntityName, props, targetPropCond, options);
                            }
                        }

                        noException = true;
                    } finally {
                        if (noException) {
                            // commitTransaction(transactionId, options);
                        } else {
                            rollbackTransaction(transactionId, options);
                        }
                    }
                }
            } else {
                final Property leftProp = association.getBiEntityProperties()[0];
                final Property rightProp = association.getBiEntityProperties()[1];

                final Set<String> selectPropNames = N.newHashSet(biEntityDef.getIdPropertyNameList());
                selectPropNames.add(leftProp.getName());
                selectPropNames.add(rightProp.getName());

                boolean noException = false;

                try {
                    for (int size = rs.size(), from = 0, to = N.min(from + batchSize, size); from < size; from = to, to = N.min(from + batchSize, size)) {
                        Or leftPropCond = CF.or();

                        for (int i = from; i < to; i++) {
                            rs.absolute(i);
                            leftPropCond.add(CF.eq(leftProp.getName(), rs.get(srcPropIndex)));
                        }

                        final DataSet biEntityResultSet = dbAccess.query(biEntityDef.getName(), selectPropNames, leftPropCond, null, options);

                        if (biEntityResultSet.size() > 0) {
                            if ((ct == OnDeleteAction.SET_NULL) || (ct == OnDeleteAction.CASCADE)) {
                                if (biEntityDef.getIdPropertyList().size() > 0) {
                                    List<EntityId> biEntityEntityIds = resultSet2EntityId(biEntityDef, biEntityResultSet);
                                    internalDelete(biEntityEntityIds, options);
                                } else {
                                    leftPropCond = CF.or();
                                    int leftPropIndex = biEntityResultSet.getColumnIndex(leftProp.getName());

                                    for (int i = 0; i < biEntityResultSet.size(); i++) {
                                        biEntityResultSet.absolute(i);
                                        leftPropCond.add(CF.eq(leftProp.getName(), biEntityResultSet.get(leftPropIndex)));
                                    }

                                    internalDelete(biEntityDef.getName(), leftPropCond, options);
                                }
                            }

                            int rightPropIndex = biEntityResultSet.getColumnIndex(rightProp.getName());

                            if (ct == OnDeleteAction.CASCADE) {
                                if (isIdTargetProp) {
                                    final List<EntityId> propEntityEntityIds = new ArrayList<>(biEntityResultSet.size());

                                    for (int i = 0; i < biEntityResultSet.size(); i++) {
                                        biEntityResultSet.absolute(i);

                                        propEntityEntityIds.add(EntityId.of(propEntityName, targetPropName, biEntityResultSet.get(rightPropIndex)));
                                    }

                                    internalDelete(propEntityEntityIds, options);
                                } else {
                                    Or targetPropCond = CF.or();

                                    for (int i = 0; i < biEntityResultSet.size(); i++) {
                                        biEntityResultSet.absolute(i);
                                        targetPropCond.add(CF.eq(targetPropName, biEntityResultSet.get(rightPropIndex)));
                                    }

                                    internalDelete(propEntityName, targetPropCond, options);
                                }
                            }
                        }
                    }

                    noException = true;
                } finally {
                    if (noException) {
                        // commitTransaction(transactionId, options);
                    } else {
                        rollbackTransaction(transactionId, options);
                    }
                }
            }
        }

        return cascadeDeleteResult;
    }

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     * @return
     */
    @Override
    protected DataSet internalQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);
        SelectPropNameView xa = checkSelectPropNamesInQuery(entityDef, selectPropNames, resultHandle, options);

        DataSet dataSet = null;
        boolean isNullSelectPropNames = (selectPropNames == null) ? true : false;
        boolean isCachableCondition = false;
        String conditionKey = null;

        if (!(isGetFromCache(options) || isCacheResult(options) || (resultHandle != null))) {
            isCachableCondition = isCachable(condition);

            if (isCachableCondition) {
                conditionKey = createConditionCacheKey(entityDef, condition);

                CachedQueryCmd cachedQueryCmd = getCachedQueryCmd(conditionKey, entityDef, selectPropNames);

                if (cachedQueryCmd != null) {
                    xa = cachedQueryCmd.xa;

                    Command queryCmd = cachedQueryCmd.getCommand(condition);

                    SQLResult queryResult = dbAccess.executeQuery(queryCmd, options);

                    try {
                        dataSet = dbAccess.getResultListBySQLResult(queryResult, xa.simplePropNames, options);
                    } finally {
                        queryResult.close();
                    }
                }
            }
        }

        if (dataSet == null) {
            Criteria criteria = condition2Criteria(condition);

            if (xa.entityPropNames.size() > 0) {
                if (criteria == condition) {
                    criteria = criteria.copy();
                }

                Property prop = null;

                for (String propName : xa.entityPropNames) {
                    prop = entityDef.getProperty(propName);

                    criteria.join(prop.getAssociation().getJoinCondition());
                }
            }

            dataSet = dbAccess.query(entityName, xa.simplePropNames, criteria, resultHandle, options);

            // replace the selection name with the original input selection names.
            if ((resultHandle != null) && N.notNullOrEmpty(resultHandle.value())) {
                dbAccess.getHandleResult(resultHandle.value()).setSelectPropNames(xa.selectPropNames);
            }

            if (isCachableCondition && (resultHandle == null)) {
                cacheQueryCmd(conditionKey, entityDef, isNullSelectPropNames ? null : selectPropNames, xa, criteria, options);
            }
        }

        composeResultSet(entityDef, xa.selectPropNames, xa.entityPropNames, dataSet, options);

        return dataSet;
    }

    /**
     * Check select prop names in query.
     *
     * @param entityDef
     * @param selectPropNames
     * @param resultHandle
     * @param options
     * @return
     */
    SelectPropNameView checkSelectPropNamesInQuery(final EntityDefinition entityDef, Collection<String> selectPropNames, Holder<String> resultHandle,
            Map<String, Object> options) {
        if ((selectPropNames == null) && isGetByResultHandle(resultHandle)) {
            selectPropNames = dbAccess.getHandleResult(resultHandle.value()).getSelectPropNames();
        }

        SelectPropNameView xa = null;
        Map<Collection<String>, SelectPropNameView> entitySelectPropNamesView = querySelectPropNameViewPool.get(entityDef.getName());

        if (entitySelectPropNamesView == null) {
            entitySelectPropNamesView = new HashMap<>();
            querySelectPropNameViewPool.put(entityDef.getName(), entitySelectPropNamesView);
        } else {
            xa = entitySelectPropNamesView.get(selectPropNames);
        }

        if (xa == null) {
            Collection<String> newSelectPropNames = selectPropNames;

            if (newSelectPropNames == null) {
                if (isGetByResultHandle(resultHandle)) {
                    newSelectPropNames = dbAccess.getHandleResult(resultHandle.value()).getSelectPropNames();
                } else {
                    newSelectPropNames = entityDef.getDefaultLoadPropertyNameList();
                }
            }

            xa = parseSelectPropNamesInQuery(entityDef, newSelectPropNames, options);

            synchronized (querySelectPropNameViewPool) {
                entitySelectPropNamesView.put(selectPropNames, xa);
            }
        }

        return xa;
    }

    /**
     * Compose result set.
     *
     * @param entityDef
     * @param selectPropNames
     * @param entityPropNames
     * @param result
     * @param options
     */
    protected void composeResultSet(final EntityDefinition entityDef, Collection<String> selectPropNames, Collection<String> entityPropNames, DataSet result,
            Map<String, Object> options) {
        if ((result.size() > 0) && (entityPropNames.size() > 0)) {
            boolean isResultCombined = isResultCombined(options);

            for (String compositePropName : entityPropNames) {
                composeProperty(null, entityDef.getProperty(compositePropName), result, isResultCombined);
            }

            final int[] indecs = new int[result.columnNameList().size()];

            for (String propName : selectPropNames) {
                indecs[result.getColumnIndex(propName)] = 1;
            }

            final List<String> columnNameList = new ArrayList<>(result.columnNameList());

            for (int i = 0, len = indecs.length; i < len; i++) {
                if (indecs[i] == 0) {
                    result.removeColumn(columnNameList.get(i));
                }
            }
        }
    }

    /**
     * Internal get result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    protected DataSet internalGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options) {
        checkResultHandle(resultHandle);

        HandleResult handleResult = dbAccess.getHandleResult(resultHandle);

        final EntityDefinition entityDef = handleResult.getEntityDef();

        return internalQuery(entityDef.getName(), selectPropNames, null, Holder.of(resultHandle), options);
    }

    /**
     * Internal release result handle.
     *
     * @param resultHandle
     */
    @Override
    protected void internalReleaseResultHandle(String resultHandle) {
        dbAccess.releaseResultHandle(resultHandle);
    }

    /**
     * Internal begin transaction.
     *
     * @param isolationLevel
     * @param options
     * @return
     */
    @Override
    protected String internalBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options) {
        return dbAccess.beginTransaction(isolationLevel, options);
    }

    /**
     * Internal end transaction.
     *
     * @param transactionId
     * @param transactionAction
     * @param options
     */
    @Override
    protected void internalEndTransaction(String transactionId, Action transactionAction, Map<String, Object> options) {
        dbAccess.endTransaction(transactionId, transactionAction, options);
    }

    /**
     * Internal get record version.
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    protected long internalGetRecordVersion(EntityId entityId, Map<String, Object> options) {
        throw new UnsupportedOperationException();
    }

    /**
     * Internal lock record.
     *
     * @param entityId
     * @param lockMode
     * @param options
     * @return
     */
    @Override
    protected String internalLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options) {
        throw new UnsupportedOperationException();
    }

    /**
     * Internal unlock record.
     *
     * @param entityId
     * @param lockCode
     * @param options
     * @return true, if successful
     */
    @Override
    protected boolean internalUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options) {
        throw new UnsupportedOperationException();
    }

    /**
     * Condition 2 criteria.
     *
     * @param condition
     * @return
     */
    private Criteria condition2Criteria(Condition condition) {
        if (condition == null) {
            return CF.criteria();
        } else if (condition instanceof Criteria) {
            return (Criteria) condition;
        } else {
            if (!CriteriaUtil.isClause(condition)) {
                condition = CF.where(condition);
            }

            Criteria criteria = CF.criteria();
            CriteriaUtil.add(criteria, condition);

            return criteria;
        }
    }

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
     * Gets the cached query cmd.
     *
     * @param conditionKey
     * @param entityDef
     * @param selectPropNames
     * @return
     */
    private CachedQueryCmd getCachedQueryCmd(String conditionKey, final EntityDefinition entityDef, Collection<String> selectPropNames) {
        CachedQueryCmd cachedQueryCmd = null;

        synchronized (queryCmdPool) {
            Map<String, Map<Collection<String>, CachedQueryCmd>> entityQueryCmdPool = queryCmdPool.get(entityDef.getName());

            if (N.notNullOrEmpty(entityQueryCmdPool)) {
                Map<Collection<String>, CachedQueryCmd> condCmdMap = entityQueryCmdPool.get(conditionKey);

                if (N.notNullOrEmpty(condCmdMap)) {
                    cachedQueryCmd = condCmdMap.get(selectPropNames);
                }
            }
        }

        return cachedQueryCmd;
    }

    /**
     * Creates the condition cache key.
     *
     * @param entityDef
     * @param condition
     * @return
     */
    private String createConditionCacheKey(final EntityDefinition entityDef, Condition condition) {
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
                SQLCommand cmd = (SQLCommand) dbAccess.getExecutant().getInterpreter().interpretCondition(entityDef, condition);

                return cmd.getSql();
            }
        }
    }

    /**
     * Cache query cmd.
     *
     * @param conditionKey
     * @param entityDef
     * @param selectPropNames
     * @param xa
     * @param condition
     * @param options
     */
    private void cacheQueryCmd(String conditionKey, final EntityDefinition entityDef, Collection<String> selectPropNames, SelectPropNameView xa,
            Condition condition, Map<String, Object> options) {
        Command queryCmd = dbAccess.getExecutant().getInterpreter().interpretQuery(entityDef, xa.simplePropNames, condition, options);
        queryCmd.clearParameters();

        CachedQueryCmd cachedQueryCmd = new CachedQueryCmd(queryCmd, xa);

        synchronized (queryCmdPool) {
            Map<String, Map<Collection<String>, CachedQueryCmd>> entityQueryCmdPool = queryCmdPool.get(entityDef.getName());

            if (entityQueryCmdPool == null) {
                entityQueryCmdPool = new HashMap<>();
                queryCmdPool.put(entityDef.getName(), entityQueryCmdPool);
            }

            Map<Collection<String>, CachedQueryCmd> condCmdMap = entityQueryCmdPool.get(conditionKey);

            if (condCmdMap == null) {
                condCmdMap = new HashMap<>();
                entityQueryCmdPool.put(conditionKey, condCmdMap);
            }

            condCmdMap.put(selectPropNames, cachedQueryCmd);
        }
    }

    /**
     *
     * @param transactionId
     * @param options
     */
    void commitTransaction(String transactionId, Map<String, Object> options) {
        if (transactionId != null) {
            try {
                endTransaction(transactionId, Action.COMMIT, null);
            } finally {
                options.remove(Options.TRANSACTION_ID);
            }
        }
    }

    /**
     *
     * @param transactionId
     * @param options
     */
    void rollbackTransaction(String transactionId, Map<String, Object> options) {
        if (transactionId != null) {
            try {
                endTransaction(transactionId, Action.ROLLBACK, null);
            } catch (Exception e) {
                // ignore
                logger.error("Failed to roll back with transaction id: " + transactionId + ". " + ExceptionUtil.getMessage(e), e);
            } finally {
                options.remove(Options.TRANSACTION_ID);
            }
        }
    }

    /**
     * Result list 2 entities.
     *
     * @param <T>
     * @param targetClass
     * @param entityDef
     * @param result
     * @return
     */
    protected <T> List<T> resultList2Entities(Class<T> targetClass, final EntityDefinition entityDef, DataSet result) {
        if (targetClass == null) {
            targetClass = entityDef.getTypeClass();
        }

        return DataSetUtil.row2Entity(targetClass, (RowDataSet) result, entityDef, 0, result.size());
    }

    /**
     * The Class CachedQueryCmd.
     */
    static class CachedQueryCmd {

        /** The query cmd. */
        private final Command queryCmd;

        /** The xa. */
        private final SelectPropNameView xa;

        /**
         * Instantiates a new cached query cmd.
         *
         * @param queryCmd
         * @param xa
         */
        CachedQueryCmd(Command queryCmd, SelectPropNameView xa) {
            this.queryCmd = queryCmd;
            this.xa = xa;
        }

        /**
         * Gets the command.
         *
         * @param condition
         * @return
         */
        Command getCommand(Condition condition) {
            if (queryCmd != null) {
                Command copy = queryCmd.copy();

                if (condition != null) {
                    List<Object> parameters = condition.getParameters();

                    for (int index = 0; index < parameters.size(); index++) {
                        copy.setParameter(index, parameters.get(index), queryCmd.getParameterType(index));
                    }
                }

                return copy;
            } else {
                return null;
            }
        }
    }

    /**
     * The Class CascadeDeleteResult.
     */
    static class CascadeDeleteResult {

        /** The transaction id. */
        String transactionId = null;

        /** The options. */
        Map<String, Object> options = null;

        /** The cond. */
        Condition cond = null;

        /**
         * Instantiates a new cascade delete result.
         *
         * @param transactionId
         * @param cond
         * @param options
         */
        CascadeDeleteResult(String transactionId, Condition cond, Map<String, Object> options) {
            this.transactionId = transactionId;
            this.cond = cond;
            this.options = options;
        }
    }

    /**
     * The Class EntityIdMemo.
     */
    static final class EntityIdMemo {

        /** The entity ids. */
        final List<? extends EntityId> entityIds;

        /** The operation type. */
        final OperationType operationType;

        /**
         * Instantiates a new entity id memo.
         *
         * @param entityIds
         * @param op
         */
        EntityIdMemo(List<? extends EntityId> entityIds, OperationType op) {
            this.entityIds = entityIds;
            operationType = op;
        }
    }
}

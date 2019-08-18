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

import static com.landawn.abacus.core.EntityManagerUtil.notInTransaction;
import static com.landawn.abacus.core.EntityManagerUtil.resultSet2EntityId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.condition.Binary;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Operator;
import com.landawn.abacus.condition.Or;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration.CustomizedEntityCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.LockConfiguration;
import com.landawn.abacus.lock.RWLock;
import com.landawn.abacus.lock.RWLockFactory;
import com.landawn.abacus.metadata.Association;
import com.landawn.abacus.metadata.ColumnType;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options.Query;
import com.landawn.abacus.util.Seq;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityManagerLVC.
 *
 * @author Haiyang Li
 * @param <E>
 * @since 0.8
 */
class EntityManagerLVC<E> extends EntityManagerLV<E> {

    /** The entity cache decorator. */
    private final EntityCacheDecorator entityCacheDecorator;

    /** The rw entity id lock. */
    private final RWLock<EntityId> rwEntityIdLock;

    /**
     * Instantiates a new entity manager LVC.
     *
     * @param entityManagerConfig
     * @param dbAccess
     */
    protected EntityManagerLVC(EntityManagerConfiguration entityManagerConfig, DBAccessImpl dbAccess) {
        super(entityManagerConfig, dbAccess);
        this.entityCacheDecorator = new EntityCacheDecorator(entityManagerConfig.getEntityCacheConfiguration());

        LockConfiguration lockConfiguration = entityManagerConfig.getLockConfiguration();

        if ((lockConfiguration == null) || (lockConfiguration.getRWLockProvider() == null)) {
            rwEntityIdLock = RWLockFactory.createLocalRWLock();
        } else {
            rwEntityIdLock = RWLockFactory.createLock(lockConfiguration.getRWLockProvider());
        }
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
    @Override
    protected <T> List<T> getEntities(Class<T> targetClass, EntityDefinition entityDef, List<? extends EntityId> entityIds, Collection<String> selectPropNames,
            Map<String, Object> options) {
        if (targetClass == null) {
            targetClass = entityDef.getTypeClass();
        }

        if (entityIds.size() == 0) {
            return new ArrayList<>();
        }

        checkLock(entityIds, LockMode.R, options);

        boolean isRefreshCache = EntityManagerUtil.isRefreshCache(options);
        boolean isGetFromCache = !EntityManagerUtil.notGetFromCache(options);
        boolean isCacheResult = !EntityManagerUtil.notCacheResult(options);

        List<T> entityList = new ArrayList<>(entityIds.size());
        List<EntityId> notCachedEntityIdList = null;

        if (isRefreshCache) {
            for (EntityId entityId : entityIds) {
                remvoeFromCache(entityDef, entityId);
            }

            notCachedEntityIdList = (List<EntityId>) entityIds;
        } else if (isGetFromCache) {
            for (EntityId entityId : entityIds) {
                T cachedEntity = getEntityFromCache(targetClass, entityDef, entityId, selectPropNames, options);

                if (cachedEntity == null) {
                    if (notCachedEntityIdList == null) {
                        notCachedEntityIdList = new ArrayList<>();
                    }

                    notCachedEntityIdList.add(entityId);
                } else {
                    entityList.add(cachedEntity);
                }
            }
        } else {
            notCachedEntityIdList = (List<EntityId>) entityIds;
        }

        if (N.notNullOrEmpty(notCachedEntityIdList)) {
            if (N.notNullOrEmpty(options)) {
                if ((options.get(Query.QUERY_FROM_CACHE) != null) || (options.get(Query.CACHE_RESULT) != null)) {
                    options = ParametersUtil.copy(options);

                    options.remove(Query.QUERY_FROM_CACHE);
                    options.remove(Query.CACHE_RESULT);
                }
            }

            List<T> notCachedEntities = super.getEntities(targetClass, entityDef, notCachedEntityIdList, selectPropNames, options);

            if (isCacheResult && notInTransaction(options)) {
                for (T notCachedEntity : notCachedEntities) {
                    cacheEntity(entityDef, notCachedEntity, selectPropNames, null, options);
                }
            }

            for (T notCachedEntity : notCachedEntities) {
                entityList.add(notCachedEntity);
            }
        }

        // sort result by input entity id order.
        if (N.notNullOrEmpty(notCachedEntityIdList) && (notCachedEntityIdList.size() != entityIds.size())) {
            Map<EntityId, T> idEntityMap = new LinkedHashMap<>();

            for (EntityId entityId : entityIds) {
                idEntityMap.put(entityId, null);
            }

            for (T entity : entityList) {
                idEntityMap.put(EntityManagerUtil.getEntityIdByEntity(entityDef, entity), entity);
            }

            entityList.clear();

            for (T entity : idEntityMap.values()) {
                if (entity != null) {
                    entityList.add(entity);
                }
            }
        }

        return entityList;
    }

    /**
     * Update entities.
     *
     * @param entityDef
     * @param entityIds
     * @param props
     * @param options
     * @return
     */
    @Override
    protected int updateEntities(EntityDefinition entityDef, List<? extends EntityId> entityIds, Map<String, Object> props, Map<String, Object> options) {
        int result = super.updateEntities(entityDef, entityIds, props, options);

        if (result > 0) {
            if (EntityManagerUtil.isInTransaction(options)) {
                for (EntityId entityId : entityIds) {
                    remvoeFromCache(entityDef, entityId);
                }
            } else {
                for (EntityId entityId : entityIds) {
                    updateCachedEntity(entityId, props, options);
                }
            }
        }

        return result;
    }

    /**
     * Delete entities.
     *
     * @param entityDef
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    protected int deleteEntities(EntityDefinition entityDef, List<? extends EntityId> entityIds, Map<String, Object> options) {
        int result = super.deleteEntities(entityDef, entityIds, options);

        if (result > 0) {
            for (EntityId entityId : entityIds) {
                remvoeFromCache(entityDef, entityId);
            }
        }

        return result;
    }

    /**
     * Cache entity.
     *
     * @param entityDef
     * @param entity
     * @param selectPropNames
     * @param alwrite
     * @param options
     */
    protected void cacheEntity(EntityDefinition entityDef, Object entity, Collection<String> selectPropNames, Set<EntityId> alwrite,
            Map<String, Object> options) {
        if ((entity == null) || (entityDef.getIdPropertyList().size() == 0)) {
            return;
        }

        if (alwrite == null) {
            alwrite = new HashSet<>();
        }

        EntityId entityId = EntityManagerUtil.getEntityIdByEntity(entityDef, entity);
        // TODO what to do for distribution
        rwEntityIdLock.lockWriteOn(entityId);

        try {
            MapEntity cachingEntity = MapEntity.valueOf(entityDef.getName());
            Collection<String> signedPropNames = getSignedPropNames(entityDef, entity, selectPropNames);

            Property prop = null;
            Object propValue = null;

            if (entity instanceof MapEntity) {
                MapEntity anMapEntity = (MapEntity) entity;

                for (String propName : signedPropNames) {
                    prop = entityDef.getProperty(propName);
                    propValue = anMapEntity.get(propName);

                    setCachingPropValue(entity, cachingEntity, prop, propValue, alwrite, options);
                }
            } else {
                for (String propName : signedPropNames) {
                    prop = entityDef.getProperty(propName);
                    propValue = EntityManagerUtil.getPropValueByMethod(entity, prop);

                    setCachingPropValue(entity, cachingEntity, prop, propValue, alwrite, options);
                }
            }

            if (entity instanceof DirtyMarker) {
                cachingEntity.version = ((DirtyMarker) entity).version();
            }

            DirtyMarkerUtil.markDirty(cachingEntity, false);

            addToCache(entityDef, entityId, cachingEntity, options);
        } finally {
            rwEntityIdLock.unlockWriteOn(entityId);
        }
    }

    /**
     * Sets the caching prop value.
     *
     * @param entity
     * @param cachingEntity
     * @param prop
     * @param propValue
     * @param alCached
     * @param options
     */
    private void setCachingPropValue(Object entity, MapEntity cachingEntity, Property prop, Object propValue, Set<EntityId> alCached,
            Map<String, Object> options) {
        if (prop.getColumnType().isEntity()) {
            EntityDefinition propEntityDef = prop.getColumnEntityDef();

            if (propValue != null) {
                EntityId propEntityEntityId = null;

                if (prop.isCollection()) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> propEntities = (Collection<Object>) propValue;

                    for (Object entityPropValue : propEntities) {
                        propEntityEntityId = EntityManagerUtil.getEntityIdByEntity(propEntityDef, entityPropValue);

                        if (!alCached.contains(propEntityEntityId)) {
                            cacheEntity(propEntityDef, entityPropValue, alCached, options);

                            alCached.add(propEntityEntityId);
                        }
                    }
                } else {
                    propEntityEntityId = EntityManagerUtil.getEntityIdByEntity(propEntityDef, propValue);

                    if (!alCached.contains(propEntityEntityId)) {
                        cacheEntity(propEntityDef, propValue, alCached, options);

                        alCached.add(propEntityEntityId);
                    }
                }
            }

            Association association = prop.getAssociation();
            Property srcProp = association.getSrcProperty();
            Property targetProp = association.getTargetProperty();

            boolean isIdTargetProp = targetProp.isId();

            EntityDefinition biEntityDef = association.getBiEntityDef();

            if (biEntityDef == null) {
                if (isIdTargetProp) {
                    // do nothing.
                } else {
                    Object srcPropValue = EntityManagerUtil.getPropValue(entity, srcProp);
                    propValue = CF.eq(targetProp.getName(), srcPropValue);
                }
            } else {
                Object srcPropValue = EntityManagerUtil.getPropValue(entity, srcProp);
                Property left = association.getBiEntityProperties()[0];
                propValue = CF.eq(left.getName(), srcPropValue);
            }
        }

        cachingEntity.set(prop.getName(), propValue);
    }

    /**
     * Cache entity.
     *
     * @param entityDef
     * @param entity
     * @param alwrite
     * @param options
     */
    private void cacheEntity(EntityDefinition entityDef, Object entity, Set<EntityId> alwrite, Map<String, Object> options) {
        if (entity instanceof DirtyMarker) {
            cacheEntity(entityDef, entity, DirtyMarkerUtil.signedPropNames((DirtyMarker) entity), alwrite, options);
        }
    }

    /**
     * Adds the to cache.
     *
     * @param entityDef
     * @param entityId
     * @param cachedEntity
     * @param options
     */
    private void addToCache(EntityDefinition entityDef, EntityId entityId, MapEntity cachedEntity, Map<String, Object> options) {
        if ((cachedEntity == null) || (cachedEntity.version() != getRecordVersion(entityId, options))) {
            remvoeFromCache(entityDef, entityId);

            return;
        }

        String entityName = cachedEntity.entityName();
        long liveTime = EntityManagerUtil.getEntityCacheLiveTime(entityName, entityManagerConfig.getEntityCacheConfiguration(), options);
        long maxIdleTime = EntityManagerUtil.getEntityCacheMaxIdleTime(entityName, entityManagerConfig.getEntityCacheConfiguration(), options);
        entityCacheDecorator.put(entityId, cachedEntity, liveTime, maxIdleTime);

        // add EntityId cache for unique id query.
        List<String> uidPropNameList = entityDef.getUIDPropertyNameList();

        if (N.notNullOrEmpty(uidPropNameList)) {
            EntityCacheConfiguration ec = entityManagerConfig.getEntityCacheConfiguration();
            CustomizedEntityCacheConfiguration ece = (ec == null) ? null : ec.getCustomizedEntityCacheConfiguration(entityName);

            MapEntity idMapEntity = null;
            Object uidPropValue = null;

            for (String uidPropName : uidPropNameList) {
                if ((ece != null) && ece.isExcludedProperty(uidPropName)) {
                    continue;
                }

                uidPropValue = cachedEntity.get(uidPropName);

                if (uidPropValue != null) {
                    if (idMapEntity == null) {
                        idMapEntity = MapEntity.valueOf(entityName);

                        for (String idPropName : entityId.keySet()) {
                            idMapEntity.set(idPropName, entityId.get(idPropName));
                        }

                        idMapEntity.version = cachedEntity.version;
                    }

                    final EntityId uidEntityId = EntityId.of(entityName, uidPropName, uidPropValue);

                    entityCacheDecorator.put(uidEntityId, idMapEntity, liveTime, maxIdleTime);
                }
            }
        }
    }

    /**
     * Gets the entity from cache.
     *
     * @param <T>
     * @param targetClass
     * @param entityDef
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    protected <T> T getEntityFromCache(Class<T> targetClass, EntityDefinition entityDef, EntityId entityId, Collection<String> selectPropNames,
            Map<String, Object> options) {
        final String entityName = entityDef.getName();
        Object result = null;
        // TODO what to do for distribution
        rwEntityIdLock.lockReadOn(entityId);

        try {
            MapEntity cachedEntity = getFromCache(entityDef, entityId, options);

            if (cachedEntity == null) {
                return null;
            }

            result = N.newEntity(targetClass, entityName);

            if (selectPropNames == null) {
                selectPropNames = entityDef.getDefaultLoadPropertyNameList();
            }

            final Collection<String> signedPropNames = DirtyMarkerUtil.signedPropNames(cachedEntity);
            Collection<String> uncachedPropNames = null;
            Property prop = null;
            Object propValue = null;

            if (result instanceof MapEntity) {
                MapEntity anMapEntity = (MapEntity) result;

                for (String propName : selectPropNames) {
                    if (signedPropNames.contains(propName)) {
                        prop = entityDef.getProperty(propName);
                        propValue = getCachedPropValue(prop, cachedEntity, options);
                        anMapEntity.set(propName, propValue);
                    } else {
                        if (uncachedPropNames == null) {
                            uncachedPropNames = new ArrayList<>();
                        }

                        uncachedPropNames.add(propName);
                    }
                }
            } else {
                for (String propName : selectPropNames) {
                    if (cachedEntity.containsKey(propName)) {
                        prop = entityDef.getProperty(propName);
                        propValue = getCachedPropValue(prop, cachedEntity, options);
                        EntityManagerUtil.setPropValueByMethod(result, prop, propValue);
                    } else {
                        if (uncachedPropNames == null) {
                            uncachedPropNames = new ArrayList<>();
                        }

                        uncachedPropNames.add(propName);
                    }
                }
            }

            if (N.notNullOrEmpty(uncachedPropNames)) {
                final CustomizedEntityCacheConfiguration entityCacheConfig = entityManagerConfig.getEntityCacheConfiguration() == null ? null
                        : entityManagerConfig.getEntityCacheConfiguration().getCustomizedEntityCacheConfiguration(entityName);
                final boolean areAllExecludedPropNames = entityCacheConfig != null
                        && Seq.of(uncachedPropNames).allMatch(propName -> entityCacheConfig.isExcludedProperty(propName));
                final boolean needToUpdateEntityCache = !areAllExecludedPropNames;
                final MapEntity tmpEntity = getUncachedPropValues(entityId, uncachedPropNames, cachedEntity, options);

                if (tmpEntity == null) {
                    remvoeFromCache(entityDef, entityId);

                    return null;
                }

                Set<EntityId> alwrite = new HashSet<>();

                if (result instanceof MapEntity) {
                    MapEntity anMapEntity = (MapEntity) result;

                    for (String uncachedPropName : uncachedPropNames) {
                        prop = entityDef.getProperty(uncachedPropName);
                        propValue = tmpEntity.get(uncachedPropName);
                        anMapEntity.set(uncachedPropName, propValue);

                        if (needToUpdateEntityCache) {
                            setCachingPropValue(tmpEntity, cachedEntity, prop, propValue, alwrite, options);
                        }
                    }
                } else {
                    for (String uncachedPropName : uncachedPropNames) {
                        prop = entityDef.getProperty(uncachedPropName);
                        propValue = tmpEntity.get(uncachedPropName);
                        EntityManagerUtil.setPropValueByMethod(result, prop, propValue);

                        if (needToUpdateEntityCache) {
                            setCachingPropValue(tmpEntity, cachedEntity, prop, propValue, alwrite, options);
                        }
                    }
                }

                if (needToUpdateEntityCache) {
                    synchronized (cachedEntity) {
                        DirtyMarkerUtil.markDirty(cachedEntity, false);
                        addToCache(entityDef, entityId, cachedEntity, options);
                    }
                }
            }

            EntityManagerUtil.setIdValue(entityDef, result, entityId);

            if (result instanceof DirtyMarker) {
                DirtyMarkerUtil.setVersion((DirtyMarker) result, cachedEntity.version());
                DirtyMarkerUtil.markDirty((DirtyMarker) result, false);
            }
        } finally {
            rwEntityIdLock.unlockReadOn(entityId);
        }

        return (T) result;
    }

    /**
     * Gets the from cache.
     *
     * @param entityDef
     * @param entityId
     * @param options
     * @return
     */
    private MapEntity getFromCache(EntityDefinition entityDef, EntityId entityId, Map<String, Object> options) {
        MapEntity cachedEntity = entityCacheDecorator.get(entityId).orElse(null);

        if ((cachedEntity != null) && (cachedEntity.version() != getRecordVersion(entityId, options))) {
            remvoeFromCache(entityDef, entityId);
            cachedEntity = null;
        }

        return cachedEntity;
    }

    /**
     * Gets the cached prop value.
     *
     * @param prop
     * @param cachedEntity
     * @param options
     * @return
     */
    private Object getCachedPropValue(Property prop, MapEntity cachedEntity, Map<String, Object> options) {
        Object propValue = cachedEntity.get(prop.getName());

        if (propValue != null) {
            if (prop.getColumnType().isEntity()) {
                EntityDefinition columnEntityDef = prop.getColumnEntityDef();
                Class<?> propEntityClass = columnEntityDef.getTypeClass();
                String propEntityName = columnEntityDef.getName();
                Collection<String> propEntitySelectPropNames = prop.getSubPropertyNameList();

                Association association = prop.getAssociation();

                Property srcProp = association.getSrcProperty();
                String srcPropName = srcProp.getName();

                // TODO MUST have foreign property value in the cached entity?
                Object srcPropValue = cachedEntity.get(srcPropName);

                Property targetProp = association.getTargetProperty();
                String targetPropName = targetProp.getName();
                boolean isIdTargetProp = targetProp.isId();

                EntityDefinition biEntityDef = association.getBiEntityDef();

                if (biEntityDef == null) {
                    if (isIdTargetProp) {
                        final EntityId propEntityEntityId = EntityId.of(propEntityName, targetPropName, srcPropValue);
                        propValue = internalGet(propEntityClass, propEntityEntityId, propEntitySelectPropNames, options);
                    } else {
                        Condition cond = cachedEntity.get(prop.getName());
                        List<Object> entities = internalList(null, columnEntityDef.getName(), propEntitySelectPropNames, cond, options);
                        propValue = entities2PropValue(prop, entities);
                    }
                } else {
                    Condition cond = cachedEntity.get(prop.getName());
                    DataSet biResultSet = dbAccess.query(biEntityDef.getName(), N.asList(prop.getAssociation().getBiEntityProperties()[1].getName()), cond);

                    if (biResultSet.size() > 0) {
                        List<?> entities = null;

                        if (isIdTargetProp) {
                            List<EntityId> propEntityEntityIds = new ArrayList<>(biResultSet.size());

                            for (int i = 0; i < biResultSet.size(); i++) {
                                biResultSet.absolute(i);

                                propEntityEntityIds.add(EntityId.of(propEntityName, targetPropName, biResultSet.get(0)));
                            }

                            entities = internalGet(propEntityClass, propEntityEntityIds, propEntitySelectPropNames, options);
                        } else {
                            Or or = CF.or();

                            for (int i = 0; i < biResultSet.size(); i++) {
                                biResultSet.absolute(i);

                                or.add(CF.eq(targetPropName, biResultSet.get(0)));
                            }

                            entities = internalList(columnEntityDef.getTypeClass(), propEntityName, propEntitySelectPropNames, or, options);
                        }

                        propValue = entities2PropValue(prop, entities);
                    } else {
                        propValue = null;
                    }
                }
            }
        }

        return propValue;
    }

    /**
     * Gets the uncached prop values.
     *
     * @param entityId
     * @param uncachedPropNames
     * @param cachedEntity
     * @param options
     * @return
     */
    private MapEntity getUncachedPropValues(EntityId entityId, Collection<String> uncachedPropNames, MapEntity cachedEntity, Map<String, Object> options) {
        EntityDefinition entityDef = checkEntityName(entityId.entityName());

        Property prop = null;
        Object propValue = null;
        Set<String> gotPropNames = null;

        for (String propName : uncachedPropNames) {
            prop = entityDef.getProperty(propName);

            if (prop.getColumnType().isEntity()) {
                Association association = prop.getAssociation();
                Property srcProp = association.getSrcProperty();

                if (DirtyMarkerUtil.signedPropNames(cachedEntity).contains(srcProp.getName())) {
                    EntityDefinition columnEntityDef = prop.getColumnEntityDef();
                    Class<?> propEntityClass = columnEntityDef.getTypeClass();
                    String propEntityName = columnEntityDef.getName();
                    Collection<String> propEntitySelectPropNames = prop.getSubPropertyNameList();

                    Property targetProp = association.getTargetProperty();
                    String targetPropName = targetProp.getName();

                    boolean isIdTargetProp = targetProp.isId();

                    EntityDefinition biEntityDef = association.getBiEntityDef();

                    if (biEntityDef == null) {
                        if (isIdTargetProp) {
                            final EntityId propEntityEntityId = EntityId.of(propEntityName, targetPropName, cachedEntity.get(srcProp.getName()));
                            propValue = internalGet(null, propEntityEntityId, propEntitySelectPropNames, options);
                        } else {
                            Condition cond = CF.eq(targetPropName, cachedEntity.get(srcProp.getName()));
                            List<?> entities = internalList(propEntityClass, columnEntityDef.getName(), propEntitySelectPropNames, cond, options);

                            propValue = entities2PropValue(prop, entities);
                        }
                    } else {
                        Property left = association.getBiEntityProperties()[0];
                        Property right = association.getBiEntityProperties()[1];
                        Condition cond = CF.eq(left.getName(), cachedEntity.get(srcProp.getName()));

                        DataSet biResultSet = dbAccess.query(biEntityDef.getName(), ParametersUtil.asList(right.getName()), cond, null, options);

                        if (biResultSet.size() > 0) {
                            List<?> entities = null;

                            if (isIdTargetProp) {
                                final List<EntityId> propEntityEntityIds = new ArrayList<>(biResultSet.size());

                                for (int i = 0; i < biResultSet.size(); i++) {
                                    biResultSet.absolute(i);
                                    propEntityEntityIds.add(EntityId.of(propEntityName, targetPropName, biResultSet.get(0)));
                                }

                                entities = internalGet(propEntityClass, propEntityEntityIds, propEntitySelectPropNames, options);
                            } else {
                                Or or = CF.or();

                                for (int i = 0; i < biResultSet.size(); i++) {
                                    biResultSet.absolute(i);
                                    or.add(CF.eq(targetPropName, biResultSet.get(0)));
                                }

                                entities = internalList(propEntityClass, propEntityName, propEntitySelectPropNames, or, options);
                            }

                            propValue = entities2PropValue(prop, entities);
                        } else {
                            propValue = null;
                        }
                    }

                    if (gotPropNames == null) {
                        gotPropNames = new HashSet<>();
                    }

                    gotPropNames.add(propName);

                    cachedEntity.set(propName, propValue);
                }
            }
        }

        if (gotPropNames != null) {
            if (gotPropNames.size() == uncachedPropNames.size()) {
                return cachedEntity;
            } else {
                uncachedPropNames.removeAll(gotPropNames);
            }
        }

        final List<MapEntity> tempEntities = super.getEntities(MapEntity.class, entityDef, N.asList(entityId), uncachedPropNames, options);

        return (tempEntities.size() == 0) ? null : tempEntities.get(0);
    }

    /**
     * Update cached entity.
     *
     * @param entityId
     * @param props
     * @param options
     */
    protected void updateCachedEntity(EntityId entityId, Map<String, Object> props, Map<String, Object> options) {
        // TODO what to do for distribution
        rwEntityIdLock.lockWriteOn(entityId);

        try {
            MapEntity cachedEntity = entityCacheDecorator.get(entityId).orElse(null);

            if ((cachedEntity != null) && ((cachedEntity.version() + VERSION_DELTA_FOR_UPDATE) == getRecordVersion(entityId, options))) {
                EntityDefinition entityDef = checkEntityName(entityId.entityName());
                Property prop = null;
                Object propValue = null;

                for (String propName : props.keySet()) {
                    prop = entityDef.getProperty(propName);

                    if (prop.getColumnType() != ColumnType.ENTITY) {
                        propValue = props.get(propName);
                        setCachingPropValue(null, cachedEntity, prop, propValue, null, options);
                    }
                }

                for (String idPropName : entityDef.getIdPropertyNameList()) {
                    if (props.containsKey(idPropName)) {
                        remvoeFromCache(entityDef, entityId);

                        entityId = EntityManagerUtil.getEntityIdByEntity(entityDef, cachedEntity);
                        remvoeFromCache(entityDef, entityId);

                        return;
                    }
                }

                // ((com.landawn.abacus.core.MapEntity) cachedEntity).version =
                // getRecordVersion(entityId, options);
                cachedEntity.version += VERSION_DELTA_FOR_UPDATE;
                DirtyMarkerUtil.markDirty(cachedEntity, false);

                addToCache(entityDef, entityId, cachedEntity, options);
            }
        } finally {
            rwEntityIdLock.unlockWriteOn(entityId);
        }
    }

    /**
     * Remvoe from cache.
     *
     * @param entityDef
     * @param entityId
     */
    @SuppressWarnings("deprecation")
    protected void remvoeFromCache(EntityDefinition entityDef, EntityId entityId) {
        List<String> uidPropNameList = entityDef.getUIDPropertyNameList();

        if (N.isNullOrEmpty(uidPropNameList)) {
            entityCacheDecorator.remove(entityId);
        } else {
            MapEntity mapEntity = entityCacheDecorator.get(entityId).orElse(null);

            if (mapEntity != null) {
                entityCacheDecorator.remove(entityId);

                Seid uidEntityId = null;
                Object propValue = null;
                for (String uidPropName : uidPropNameList) {
                    propValue = mapEntity.get(uidPropName);

                    if (propValue != null) {
                        if (uidEntityId == null) {
                            uidEntityId = Seid.of(entityDef.getName());
                        } else {
                            uidEntityId.clear();
                        }

                        uidEntityId.set(uidPropName, propValue);

                        entityCacheDecorator.remove(uidEntityId);
                    }
                }

            }
        }
    }

    /**
     * Entities 2 prop value.
     *
     * @param prop
     * @param entities
     * @return
     */
    private Object entities2PropValue(Property prop, List<?> entities) {
        return prop.isCollection() ? prop.asCollection(entities) : (N.notNullOrEmpty(entities) ? entities.get(0) : null);
    }

    /**
     * Gets the signed prop names.
     *
     * @param entityDef
     * @param entity
     * @param selectPropNames
     * @return
     */
    private Collection<String> getSignedPropNames(EntityDefinition entityDef, Object entity, Collection<String> selectPropNames) {
        if (entity instanceof DirtyMarker) {
            return DirtyMarkerUtil.signedPropNames((DirtyMarker) entity);
        } else {
            return (selectPropNames == null) ? entityDef.getDefaultLoadPropertyNameList() : selectPropNames;
        }
    }

    /**
     * Gets the entity id by condition.
     *
     * @param entityName
     * @param cond
     * @param options
     * @return
     */
    @SuppressWarnings("deprecation")
    @Override
    protected List<EntityId> getEntityIdByCondition(String entityName, Condition cond, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);
        Collection<String> idPropNames = entityDef.getIdPropertyNameList();
        List<EntityId> entityIds = null;

        if ((cond != null) && (cond.getOperator() == Operator.EQUAL)) {
            Binary b = (Binary) cond;
            Property prop = entityDef.getProperty(b.getPropName());
            String propName = prop == null ? b.getPropName() : prop.getName();

            EntityCacheConfiguration ec = entityManagerConfig.getEntityCacheConfiguration();
            CustomizedEntityCacheConfiguration ece = (ec == null) ? null : ec.getCustomizedEntityCacheConfiguration(entityName);

            if ((prop != null) && prop.isUID() && ((ece == null) || !ece.isExcludedProperty(propName))) {
                EntityId uid = EntityId.of(entityName, propName, b.getPropValue());

                MapEntity mapEntity = entityCacheDecorator.get(uid).orElse(null);

                if ((mapEntity != null) && mapEntity.keySet().containsAll(idPropNames)) {
                    Seid entityId = Seid.of(entityName);

                    for (String idPropName : idPropNames) {
                        entityId.set(idPropName, mapEntity.get(idPropName));
                    }

                    if (mapEntity.version() == getRecordVersion(entityId, options)) {
                        entityIds = N.asList(entityId);
                    } else {
                        remvoeFromCache(entityDef, uid);
                    }
                }
            }
        }

        if (entityIds == null) {
            DataSet result = internalQuery(entityName, idPropNames, cond, null, options);
            entityIds = resultSet2EntityId(entityDef, result);
        }

        return entityIds;
    }
}

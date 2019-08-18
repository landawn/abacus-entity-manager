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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.handler.Handler;
import com.landawn.abacus.handler.HandlerFactory;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.u.Holder;
import com.landawn.abacus.util.u.Optional;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractEntityManager.
 *
 * @author Haiyang Li
 * @param <E> the element type
 * @since 0.8
 */
abstract class AbstractEntityManager<E> implements com.landawn.abacus.EntityManager<E> {

    /** The domain name. */
    protected final String domainName;

    /** The config. */
    protected final EntityManagerConfiguration entityManagerConfig;

    /** The handler list. */
    private final List<Handler<E>> handlerList;

    /**
     * Instantiates a new abstract entity manager.
     *
     * @param domainName
     * @param entityManagerConfig configuration for entity manager.
     */
    protected AbstractEntityManager(final String domainName, final EntityManagerConfiguration entityManagerConfig) {
        this.domainName = domainName;
        this.entityManagerConfig = entityManagerConfig;

        handlerList = new ArrayList<Handler<E>>();

        for (String attr : entityManagerConfig.getHandlerList()) {
            Handler<E> handler = HandlerFactory.create(this, attr);
            handlerList.add(handler);
        }
    }

    /**
     * Gets the.
     *
     * @param <T>
     * @param entityId
     * @return
     */
    @Override
    public <T> Optional<T> get(EntityId entityId) {
        return Optional.ofNullable((T) gett(entityId));
    }

    /**
     * Gets the.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @return
     */
    @Override
    public <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames) {
        return Optional.ofNullable((T) gett(entityId, selectPropNames));
    }

    /**
     * Gets the.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    public <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) {
        return Optional.ofNullable((T) gett(entityId, selectPropNames, options));
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityId
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T gett(final EntityId entityId) {
        return (T) gett(entityId, null, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames) {
        return (T) gett(entityId, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) {
        EntityManagerUtil.checkArgNotNullOrEmpty(entityId, "EntityId");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preGet(entityId, selectPropNames, options);
        }

        T entity = null;

        try {
            entity = (T) internalGet(null, entityId, selectPropNames, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postGet((E) entity, entityId, selectPropNames, options);
            }
        }

        return entity;
    }

    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public <T> List<T> getAll(final List<? extends EntityId> entityIds) {
    //        return getAll(entityIds, null);
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public <T> List<T> getAll(final List<? extends EntityId> entityIds, final Collection<String> selectPropNames) {
    //        return getAll(entityIds, selectPropNames, null);
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public <T> List<T> getAll(final List<? extends EntityId> entityIds, final Collection<String> selectPropNames, final Map<String, Object> options) {
    //        EntityManagerUtil.checkArgNotNullOrEmpty(entityIds, "EntityIds");
    //        EntityManagerUtil.checkArgNotNullOrEmpty(entityIds.get(0), "EntityIds");
    //
    //        for (int index = 0, size = handlerList.size(); index < size; index++) {
    //            handlerList.get(index).preGet(entityIds, selectPropNames, options);
    //        }
    //
    //        List<T> entities = null;
    //
    //        try {
    //            entities = internalGet(null, entityIds, selectPropNames, options);
    //        } finally {
    //            for (int index = 0, size = handlerList.size(); index < size; index++) {
    //                handlerList.get(index).postGet((List<E>) entities, entityIds, selectPropNames, options);
    //            }
    //        }
    //
    //        return entities;
    //    }

    /**
     * List.
     *
     * @param <T>
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @return
     */
    @Override
    public <T> List<T> list(final String entityName, final Collection<String> selectPropNames, final Condition condition) {
        return list(entityName, selectPropNames, condition, null);
    }

    /**
     * List.
     *
     * @param <T>
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(final String entityName, final Collection<String> selectPropNames, final Condition condition, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preList(entityName, selectPropNames, condition, options);
        }

        List<T> entities = null;

        try {
            entities = internalList(null, entityName, selectPropNames, condition, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postList((List<E>) entities, entityName, selectPropNames, condition, options);
            }
        }

        return entities;
    }

    /**
     * Adds the.
     *
     * @param entity
     * @return
     */
    @Override
    public EntityId add(final E entity) {
        return add(entity, null);
    }

    /**
     * Adds the.
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    public EntityId add(final E entity, final Map<String, Object> options) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity can't be null");
        }

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preAdd(entity, options);
        }

        EntityId entityId = null;

        try {
            entityId = internalAdd(entity, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postAdd(entityId, entity, options);
            }
        }

        return entityId;
    }

    /**
     * Adds the all.
     *
     * @param entities
     * @return
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends E> entities) {
        return addAll(entities, null);
    }

    /**
     * Adds the all.
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        N.checkArgNotNullOrEmpty(entities, "Entities");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preAdd(entities, options);
        }

        List<EntityId> entityIds = null;

        try {
            entityIds = internalAdd(entities, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postAdd(entityIds, entities, options);
            }
        }

        return entityIds;
    }

    /**
     * Adds the.
     *
     * @param entityName
     * @param props
     * @param options
     * @return
     */
    @Override
    public EntityId add(final String entityName, final Map<String, Object> props, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preAdd(entityName, props, options);
        }

        EntityId entityId = null;

        try {
            entityId = internalAdd(entityName, props, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postAdd(entityId, entityName, props, options);
            }
        }

        return entityId;
    }

    /**
     * Adds the all.
     *
     * @param entityName
     * @param propsList
     * @param options
     * @return
     */
    @Override
    public List<EntityId> addAll(final String entityName, final List<Map<String, Object>> propsList, final Map<String, Object> options) {
        N.checkArgNotNullOrEmpty(propsList, "PropsList");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preAdd(entityName, propsList, options);
        }

        List<EntityId> entityIds = null;

        try {
            entityIds = internalAdd(entityName, propsList, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postAdd(entityIds, entityName, propsList, options);
            }
        }

        return entityIds;
    }

    /**
     * Update.
     *
     * @param entity
     * @return
     */
    @Override
    public int update(final E entity) {
        return update(entity, null);
    }

    /**
     * Update.
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    public int update(final E entity, final Map<String, Object> options) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity can't be null");
        }

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preUpdate(entity, options);
        }

        int result = -1;

        try {
            // [TODO]
            // if (entity instanceof ActiveRecord) {
            // ((ActiveRecord) entity).update(options);
            // } else {
            result = internalUpdate(entity, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postUpdate(result, entity, options);
            }
        }

        return result;
    }

    /**
     * Update all.
     *
     * @param entities
     * @return
     */
    @Override
    public int updateAll(final Collection<? extends E> entities) {
        return updateAll(entities, null);
    }

    /**
     * Update all.
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    public int updateAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        N.checkArgNotNullOrEmpty(entities, "Entities");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preUpdate(entities, options);
        }

        int result = -1;

        try {
            result = internalUpdate(entities, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postUpdate(result, entities, options);
            }
        }

        return result;
    }

    /**
     * Update.
     * @param props
     * @param entityId
     * @return
     */
    @Override
    public int update(final Map<String, Object> props, final EntityId entityId) {
        return update(props, entityId, null);
    }

    /**
     * Update.
     * @param props
     * @param entityId
     * @param options
     * @return
     */
    @Override
    public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
        EntityManagerUtil.checkArgNotNullOrEmpty(entityId, "EntityId");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preUpdate(props, entityId, options);
        }

        int result = -1;

        try {
            result = internalUpdate(entityId, props, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postUpdate(result, props, entityId, options);
            }
        }

        return result;
    }

    /**
     * Update all.
     * @param props
     * @param entityIds
     * @return
     */
    @Override
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
        return updateAll(props, entityIds, null);
    }

    /**
     * Update all.
     * @param props
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        N.checkArgNotNullOrEmpty(entityIds, "EntityIds");
        EntityManagerUtil.checkArgNotNullOrEmpty(entityIds.get(0), "EntityIds");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preUpdate(props, entityIds, options);
        }

        int result = -1;

        try {
            result = internalUpdate(entityIds, props, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postUpdate(result, props, entityIds, options);
            }
        }

        return result;
    }

    /**
     * Update.
     *
     * @param entityName
     * @param props
     * @param condition
     * @param options
     * @return
     */
    @Override
    public int update(final String entityName, final Map<String, Object> props, final Condition condition, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preUpdate(entityName, props, condition, options);
        }

        int result = -1;

        try {
            result = internalUpdate(entityName, props, condition, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postUpdate(result, entityName, props, condition, options);
            }
        }

        return result;
    }

    /**
     * Delete.
     *
     * @param entityId
     * @return
     */
    @Override
    public int delete(final EntityId entityId) {
        return delete(entityId, null);
    }

    /**
     * Delete.
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    public int delete(final EntityId entityId, final Map<String, Object> options) {
        EntityManagerUtil.checkArgNotNullOrEmpty(entityId, "EntityId");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preDelete(entityId, options);
        }

        int result = -1;

        try {
            result = internalDelete(entityId, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postDelete(result, entityId, options);
            }
        }

        return result;
    }

    /**
     * Delete all.
     *
     * @param entityIds
     * @return
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds) {
        return deleteAll(entityIds, null);
    }

    /**
     * Delete all.
     *
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        N.checkArgNotNullOrEmpty(entityIds, "EntityIds");
        EntityManagerUtil.checkArgNotNullOrEmpty(entityIds.get(0), "EntityIds");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preDelete(entityIds, options);
        }

        int result = -1;

        try {
            result = internalDelete(entityIds, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postDelete(result, entityIds, options);
            }
        }

        return result;
    }

    /**
     * Delete.
     *
     * @param entity
     * @return
     */
    @Override
    public int delete(final E entity) {
        return delete(entity, null);
    }

    /**
     * Delete.
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    public int delete(final E entity, final Map<String, Object> options) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity can't be null");
        }

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preDelete(entity, options);
        }

        int result = -1;

        try {
            result = internalDelete(entity, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postDelete(result, entity, options);
            }
        }

        return result;
    }

    /**
     * Delete all.
     *
     * @param entities
     * @return
     */
    @Override
    public int deleteAll(final Collection<? extends E> entities) {
        return deleteAll(entities, null);
    }

    /**
     * Delete all.
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    public int deleteAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        N.checkArgNotNullOrEmpty(entities, "Entities");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preDelete(entities, options);
        }

        int result = -1;

        try {
            result = internalDelete(entities, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postDelete(result, entities, options);
            }
        }

        return result;
    }

    /**
     * Delete.
     *
     * @param entityName
     * @param condition
     * @param options
     * @return
     */
    @Override
    public int delete(final String entityName, final Condition condition, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preDelete(entityName, condition, options);
        }

        int result = -1;

        try {
            result = internalDelete(entityName, condition, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postDelete(result, entityName, condition, options);
            }
        }

        return result;
    }

    /**
     * Query.
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @return
     */
    @Override
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition condition) {
        return query(entityName, selectPropNames, condition, null, null);
    }

    /**
     * Query.
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     * @return
     */
    @Override
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition condition, final Holder<String> resultHandle,
            final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preQuery(entityName, selectPropNames, condition, resultHandle, options);
        }

        DataSet result = null;

        try {
            result = internalQuery(entityName, selectPropNames, condition, resultHandle, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postQuery(result, entityName, selectPropNames, condition, resultHandle, options);
            }
        }

        return result;
    }

    /**
     * Gets the result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    public DataSet getResultByHandle(final String resultHandle, final Collection<String> selectPropNames, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preGetResultByHandle(resultHandle, selectPropNames, options);
        }

        DataSet result = null;

        try {
            result = internalGetResultByHandle(resultHandle, selectPropNames, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postGetResultByHandle(result, resultHandle, selectPropNames, options);
            }
        }

        return result;
    }

    /**
     * Release result handle.
     *
     * @param resultHandle
     */
    @Override
    public void releaseResultHandle(String resultHandle)

    {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preReleaseResultHandle(resultHandle);
        }

        try {
            internalReleaseResultHandle(resultHandle);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postReleaseResultHandle(resultHandle);
            }
        }
    }

    /**
     * Begin transaction.
     *
     * @param isolationLevel
     * @param options
     * @return
     */
    @Override
    public String beginTransaction(final IsolationLevel isolationLevel, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preBeginTransaction(isolationLevel, options);
        }

        String result = null;

        try {
            result = internalBeginTransaction(isolationLevel, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postBeginTransaction(result, isolationLevel, options);
            }
        }

        return result;
    }

    /**
     * End transaction.
     *
     * @param transactionId
     * @param transactionAction
     * @param options
     */
    @Override
    public void endTransaction(final String transactionId, final Action transactionAction, final Map<String, Object> options) {
        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preEndTransaction(transactionId, transactionAction, options);
        }

        try {
            internalEndTransaction(transactionId, transactionAction, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postEndTransaction(transactionId, transactionAction, options);
            }
        }
    }

    /**
     * Gets the record version.
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    @SuppressWarnings("deprecation")
    public long getRecordVersion(final EntityId entityId, final Map<String, Object> options) {
        EntityManagerUtil.checkArgNotNullOrEmpty(entityId, "EntityId");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preGetRecordVersion(entityId, options);
        }

        long result = -1;

        try {
            result = internalGetRecordVersion(entityId, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postGetRecordVersion(result, entityId, options);
            }
        }

        return result;
    }

    /**
     * Lock record.
     *
     * @param entityId
     * @param lockMode
     * @param options
     * @return
     */
    @Override
    @SuppressWarnings("deprecation")
    public String lockRecord(final EntityId entityId, final LockMode lockMode, final Map<String, Object> options) {
        EntityManagerUtil.checkArgNotNullOrEmpty(entityId, "EntityId");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preLockRecord(entityId, lockMode, options);
        }

        String result = null;

        try {
            result = internalLockRecord(entityId, lockMode, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postLockRecord(result, entityId, lockMode, options);
            }
        }

        return result;
    }

    /**
     * Unlock record.
     *
     * @param entityId
     * @param lockCode
     * @param options
     * @return true, if successful
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean unlockRecord(final EntityId entityId, final String lockCode, final Map<String, Object> options) {
        EntityManagerUtil.checkArgNotNullOrEmpty(entityId, "EntityId");

        for (int index = 0, size = handlerList.size(); index < size; index++) {
            handlerList.get(index).preUnlockRecord(entityId, lockCode, options);
        }

        boolean result = false;

        try {
            result = internalUnlockRecord(entityId, lockCode, options);
        } finally {
            for (int index = 0, size = handlerList.size(); index < size; index++) {
                handlerList.get(index).postUnlockRecord(result, entityId, lockCode, options);
            }
        }

        return result;
    }

    /**
     * Gets the entity definition factory.
     *
     * @return
     */
    @Override
    public EntityDefinitionFactory getEntityDefinitionFactory() {
        return internalGetEntityDefinitionFactory();
    }

    /**
     * Internal get.
     *
     * @param <T>
     * @param targetClass
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     */
    protected abstract <T> T internalGet(Class<T> targetClass, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Internal get.
     *
     * @param <T>
     * @param targetClass
     * @param entityIds
     * @param selectPropNames
     * @param options
     * @return
     */
    protected abstract <T> List<T> internalGet(Class<T> targetClass, List<? extends EntityId> entityIds, Collection<String> selectPropNames,
            Map<String, Object> options);

    /**
     * Internal list.
     *
     * @param <T>
     * @param targetClass
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     * @return
     */
    protected abstract <T> List<T> internalList(Class<T> targetClass, String entityName, Collection<String> selectPropNames, Condition condition,
            Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entity
     * @param options
     * @return
     */
    protected abstract EntityId internalAdd(E entity, Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entities
     * @param options
     * @return
     */
    protected abstract List<EntityId> internalAdd(Collection<? extends E> entities, Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entityName
     * @param props
     * @param options
     * @return
     */
    protected abstract EntityId internalAdd(String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entityName
     * @param propsList
     * @param options
     * @return
     */
    protected abstract List<EntityId> internalAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entity
     * @param options
     * @return
     */
    protected abstract int internalUpdate(E entity, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entities
     * @param options
     * @return
     */
    protected abstract int internalUpdate(Collection<? extends E> entities, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entityId
     * @param props
     * @param options
     * @return
     */
    protected abstract int internalUpdate(EntityId entityId, Map<String, Object> props, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entityIds
     * @param props
     * @param options
     * @return
     */
    protected abstract int internalUpdate(List<? extends EntityId> entityIds, Map<String, Object> props, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entityName
     * @param props
     * @param condition
     * @param options
     * @return
     */
    protected abstract int internalUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entityId
     * @param options
     * @return
     */
    protected abstract int internalDelete(EntityId entityId, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entityIds
     * @param options
     * @return
     */
    protected abstract int internalDelete(List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entity
     * @param options
     * @return
     */
    protected abstract int internalDelete(E entity, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entities
     * @param options
     * @return
     */
    protected abstract int internalDelete(Collection<? extends E> entities, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entityName
     * @param condition
     * @param options
     * @return
     */
    protected abstract int internalDelete(String entityName, Condition condition, Map<String, Object> options);

    /**
     * Internal query.
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     * @return
     */
    protected abstract DataSet internalQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options);

    /**
     * Internal get result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     * @return
     */
    protected abstract DataSet internalGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Internal release result handle.
     *
     * @param resultHandle
     */
    protected abstract void internalReleaseResultHandle(String resultHandle);

    /**
     * Internal begin transaction.
     *
     * @param isolationLevel
     * @param options
     * @return
     */
    protected abstract String internalBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Internal end transaction.
     *
     * @param transactionId
     * @param transactionAction
     * @param options
     */
    protected abstract void internalEndTransaction(String transactionId, Action transactionAction, Map<String, Object> options);

    /**
     * Internal get record version.
     *
     * @param entityId
     * @param options
     * @return
     */
    protected abstract long internalGetRecordVersion(EntityId entityId, Map<String, Object> options);

    /**
     * Internal lock record.
     *
     * @param entityId
     * @param lockMode
     * @param options
     * @return
     */
    protected abstract String internalLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Internal unlock record.
     *
     * @param entityId
     * @param lockCode
     * @param options
     * @return true, if successful
     */
    protected abstract boolean internalUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options);

    /**
     * Internal get entity definition factory.
     *
     * @return
     */
    protected abstract EntityDefinitionFactory internalGetEntityDefinitionFactory();

    /**
     * Check entity name.
     *
     * @param entityName
     * @return
     */
    protected EntityDefinition checkEntityName(final String entityName) {
        return EntityManagerUtil.checkEntityName(getEntityDefinitionFactory(), entityName);
    }

    /**
     * Check entity id.
     *
     * @param entityId
     * @return
     */
    protected EntityDefinition checkEntityId(final EntityId entityId) {
        return EntityManagerUtil.checkEntityId(getEntityDefinitionFactory(), entityId);
    }

    /**
     * Check entity.
     *
     * @param entity
     * @return
     */
    protected EntityDefinition checkEntity(final E entity) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entity);
    }
}

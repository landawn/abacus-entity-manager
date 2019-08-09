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
    protected final EntityManagerConfiguration config;

    /** The handler list. */
    private final List<Handler<E>> handlerList;

    /**
     * Instantiates a new abstract entity manager.
     *
     * @param domainName the domain name
     * @param config the config
     */
    protected AbstractEntityManager(final String domainName, final EntityManagerConfiguration config) {
        this.domainName = domainName;
        this.config = config;

        handlerList = new ArrayList<Handler<E>>();

        for (String attr : config.getHandlerList()) {
            Handler<E> handler = HandlerFactory.create(this, attr);
            handlerList.add(handler);
        }
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @return the optional
     */
    @Override
    public <T> Optional<T> get(EntityId entityId) {
        return Optional.ofNullable((T) gett(entityId));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return the optional
     */
    @Override
    public <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames) {
        return Optional.ofNullable((T) gett(entityId, selectPropNames));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the optional
     */
    @Override
    public <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) {
        return Optional.ofNullable((T) gett(entityId, selectPropNames, options));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @return the t
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T gett(final EntityId entityId) {
        return (T) gett(entityId, null, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return the t
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames) {
        return (T) gett(entityId, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
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
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @return the list
     */
    @Override
    public <T> List<T> list(final String entityName, final Collection<String> selectPropNames, final Condition condition) {
        return list(entityName, selectPropNames, condition, null);
    }

    /**
     * List.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param options the options
     * @return the list
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
     * @param entity the entity
     * @return the entity id
     */
    @Override
    public EntityId add(final E entity) {
        return add(entity, null);
    }

    /**
     * Adds the.
     *
     * @param entity the entity
     * @param options the options
     * @return the entity id
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
     * @param entities the entities
     * @return the list
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends E> entities) {
        return addAll(entities, null);
    }

    /**
     * Adds the all.
     *
     * @param entities the entities
     * @param options the options
     * @return the list
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
     * @param entityName the entity name
     * @param props the props
     * @param options the options
     * @return the entity id
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
     * @param entityName the entity name
     * @param propsList the props list
     * @param options the options
     * @return the list
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
     * @param entity the entity
     * @return the int
     */
    @Override
    public int update(final E entity) {
        return update(entity, null);
    }

    /**
     * Update.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
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
     * @param entities the entities
     * @return the int
     */
    @Override
    public int updateAll(final Collection<? extends E> entities) {
        return updateAll(entities, null);
    }

    /**
     * Update all.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
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
     * @param entityId the entity id
     * @param props the props
     *
     * @return the int
     */
    @Override
    public int update(final EntityId entityId, final Map<String, Object> props) {
        return update(entityId, props, null);
    }

    /**
     * Update.
     * @param entityId the entity id
     * @param props the props
     * @param options the options
     *
     * @return the int
     */
    @Override
    public int update(final EntityId entityId, final Map<String, Object> props, final Map<String, Object> options) {
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
     * @param entityIds the entity ids
     * @param props the props
     *
     * @return the int
     */
    @Override
    public int updateAll(final List<? extends EntityId> entityIds, final Map<String, Object> props) {
        return updateAll(entityIds, props, null);
    }

    /**
     * Update all.
     * @param entityIds the entity ids
     * @param props the props
     * @param options the options
     *
     * @return the int
     */
    @Override
    public int updateAll(final List<? extends EntityId> entityIds, final Map<String, Object> props, final Map<String, Object> options) {
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
     * @param entityName the entity name
     * @param props the props
     * @param condition the condition
     * @param options the options
     * @return the int
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
     * @param entityId the entity id
     * @return the int
     */
    @Override
    public int delete(final EntityId entityId) {
        return delete(entityId, null);
    }

    /**
     * Delete.
     *
     * @param entityId the entity id
     * @param options the options
     * @return the int
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
     * @param entityIds the entity ids
     * @return the int
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds) {
        return deleteAll(entityIds, null);
    }

    /**
     * Delete all.
     *
     * @param entityIds the entity ids
     * @param options the options
     * @return the int
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
     * @param entity the entity
     * @return the int
     */
    @Override
    public int delete(final E entity) {
        return delete(entity, null);
    }

    /**
     * Delete.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
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
     * @param entities the entities
     * @return the int
     */
    @Override
    public int deleteAll(final Collection<? extends E> entities) {
        return deleteAll(entities, null);
    }

    /**
     * Delete all.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
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
     * @param entityName the entity name
     * @param condition the condition
     * @param options the options
     * @return the int
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
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @return the data set
     */
    @Override
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition condition) {
        return query(entityName, selectPropNames, condition, null, null);
    }

    /**
     * Query.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param resultHandle the result handle
     * @param options the options
     * @return the data set
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
     * @param resultHandle the result handle
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the result by handle
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
     * @param resultHandle the result handle
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
     * @param isolationLevel the isolation level
     * @param options the options
     * @return the string
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
     * @param transactionId the transaction id
     * @param transactionAction the transaction action
     * @param options the options
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
     * @param entityId the entity id
     * @param options the options
     * @return the record version
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
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options the options
     * @return the string
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
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options the options
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
     * @return the entity definition factory
     */
    @Override
    public EntityDefinitionFactory getEntityDefinitionFactory() {
        return internalGetEntityDefinitionFactory();
    }

    /**
     * Internal get.
     *
     * @param <T> the generic type
     * @param targetClass the target class
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
     */
    protected abstract <T> T internalGet(Class<T> targetClass, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Internal get.
     *
     * @param <T> the generic type
     * @param targetClass the target class
     * @param entityIds the entity ids
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the list
     */
    protected abstract <T> List<T> internalGet(Class<T> targetClass, List<? extends EntityId> entityIds, Collection<String> selectPropNames,
            Map<String, Object> options);

    /**
     * Internal list.
     *
     * @param <T> the generic type
     * @param targetClass the target class
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param options the options
     * @return the list
     */
    protected abstract <T> List<T> internalList(Class<T> targetClass, String entityName, Collection<String> selectPropNames, Condition condition,
            Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entity the entity
     * @param options the options
     * @return the entity id
     */
    protected abstract EntityId internalAdd(E entity, Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entities the entities
     * @param options the options
     * @return the list
     */
    protected abstract List<EntityId> internalAdd(Collection<? extends E> entities, Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entityName the entity name
     * @param props the props
     * @param options the options
     * @return the entity id
     */
    protected abstract EntityId internalAdd(String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     * Internal add.
     *
     * @param entityName the entity name
     * @param propsList the props list
     * @param options the options
     * @return the list
     */
    protected abstract List<EntityId> internalAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
     */
    protected abstract int internalUpdate(E entity, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
     */
    protected abstract int internalUpdate(Collection<? extends E> entities, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entityId the entity id
     * @param props the props
     * @param options the options
     * @return the int
     */
    protected abstract int internalUpdate(EntityId entityId, Map<String, Object> props, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entityIds the entity ids
     * @param props the props
     * @param options the options
     * @return the int
     */
    protected abstract int internalUpdate(List<? extends EntityId> entityIds, Map<String, Object> props, Map<String, Object> options);

    /**
     * Internal update.
     *
     * @param entityName the entity name
     * @param props the props
     * @param condition the condition
     * @param options the options
     * @return the int
     */
    protected abstract int internalUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entityId the entity id
     * @param options the options
     * @return the int
     */
    protected abstract int internalDelete(EntityId entityId, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entityIds the entity ids
     * @param options the options
     * @return the int
     */
    protected abstract int internalDelete(List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
     */
    protected abstract int internalDelete(E entity, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
     */
    protected abstract int internalDelete(Collection<? extends E> entities, Map<String, Object> options);

    /**
     * Internal delete.
     *
     * @param entityName the entity name
     * @param condition the condition
     * @param options the options
     * @return the int
     */
    protected abstract int internalDelete(String entityName, Condition condition, Map<String, Object> options);

    /**
     * Internal query.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param resultHandle the result handle
     * @param options the options
     * @return the data set
     */
    protected abstract DataSet internalQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options);

    /**
     * Internal get result by handle.
     *
     * @param resultHandle the result handle
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the data set
     */
    protected abstract DataSet internalGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Internal release result handle.
     *
     * @param resultHandle the result handle
     */
    protected abstract void internalReleaseResultHandle(String resultHandle);

    /**
     * Internal begin transaction.
     *
     * @param isolationLevel the isolation level
     * @param options the options
     * @return the string
     */
    protected abstract String internalBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Internal end transaction.
     *
     * @param transactionId the transaction id
     * @param transactionAction the transaction action
     * @param options the options
     */
    protected abstract void internalEndTransaction(String transactionId, Action transactionAction, Map<String, Object> options);

    /**
     * Internal get record version.
     *
     * @param entityId the entity id
     * @param options the options
     * @return the long
     */
    protected abstract long internalGetRecordVersion(EntityId entityId, Map<String, Object> options);

    /**
     * Internal lock record.
     *
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options the options
     * @return the string
     */
    protected abstract String internalLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Internal unlock record.
     *
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options the options
     * @return true, if successful
     */
    protected abstract boolean internalUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options);

    /**
     * Internal get entity definition factory.
     *
     * @return the entity definition factory
     */
    protected abstract EntityDefinitionFactory internalGetEntityDefinitionFactory();

    /**
     * Check entity name.
     *
     * @param entityName the entity name
     * @return the entity definition
     */
    protected EntityDefinition checkEntityName(final String entityName) {
        return EntityManagerUtil.checkEntityName(getEntityDefinitionFactory(), entityName);
    }

    /**
     * Check entity id.
     *
     * @param entityId the entity id
     * @return the entity definition
     */
    protected EntityDefinition checkEntityId(final EntityId entityId) {
        return EntityManagerUtil.checkEntityId(getEntityDefinitionFactory(), entityId);
    }

    /**
     * Check entity.
     *
     * @param entity the entity
     * @return the entity definition
     */
    protected EntityDefinition checkEntity(final E entity) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entity);
    }
}

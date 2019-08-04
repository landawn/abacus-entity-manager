/*
 * Copyright (C) 2015 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus.handler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.util.u.Holder;

// TODO: Auto-generated Javadoc
/**
 * The Interface Handler.
 *
 * @author Haiyang Li
 * @param <T> the generic type
 * @since 0.8
 */
public interface Handler<T> {

    /**
     * Pre get.
     *
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     */
    void preGet(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Post get.
     *
     * @param result the result
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     */
    void postGet(T result, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Pre get.
     *
     * @param entityIds the entity ids
     * @param selectPropNames the select prop names
     * @param options the options
     */
    void preGet(List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Post get.
     *
     * @param result the result
     * @param entityIds the entity ids
     * @param selectPropNames the select prop names
     * @param options the options
     */
    void postGet(List<T> result, List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Pre list.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param options the options
     */
    void preList(String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    /**
     * Post list.
     *
     * @param result the result
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param options the options
     */
    void postList(List<T> result, String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    /**
     * Pre add.
     *
     * @param entity the entity
     * @param options the options
     */
    void preAdd(T entity, Map<String, Object> options);

    /**
     * Post add.
     *
     * @param entityId the entity id
     * @param entity the entity
     * @param options the options
     */
    void postAdd(EntityId entityId, T entity, Map<String, Object> options);

    /**
     * Pre add.
     *
     * @param entities the entities
     * @param options the options
     */
    void preAdd(Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Post add.
     *
     * @param entityIds the entity ids
     * @param entities the entities
     * @param options the options
     */
    void postAdd(List<EntityId> entityIds, Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Pre add.
     *
     * @param entityName the entity name
     * @param props the props
     * @param options the options
     */
    void preAdd(String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     * Post add.
     *
     * @param entityId the entity id
     * @param entityName the entity name
     * @param props the props
     * @param options the options
     */
    void postAdd(EntityId entityId, String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     * Pre add.
     *
     * @param entityName the entity name
     * @param propsList the props list
     * @param options the options
     */
    void preAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     * Post add.
     *
     * @param entityIds the entity ids
     * @param entityName the entity name
     * @param propsList the props list
     * @param options the options
     */
    void postAdd(List<EntityId> entityIds, String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     * Pre update.
     *
     * @param entity the entity
     * @param options the options
     */
    void preUpdate(T entity, Map<String, Object> options);

    /**
     * Post update.
     *
     * @param result the result
     * @param entity the entity
     * @param options the options
     */
    void postUpdate(int result, T entity, Map<String, Object> options);

    /**
     * Pre update.
     *
     * @param props the props
     * @param entityId the entity id
     * @param options the options
     */
    void preUpdate(Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    /**
     * Post update.
     *
     * @param result the result
     * @param props the props
     * @param entityId the entity id
     * @param options the options
     */
    void postUpdate(int result, Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    /**
     * Pre update.
     *
     * @param entities the entities
     * @param options the options
     */
    void preUpdate(Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Post update.
     *
     * @param result the result
     * @param entities the entities
     * @param options the options
     */
    void postUpdate(int result, Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Pre update.
     *
     * @param props the props
     * @param entityIds the entity ids
     * @param options the options
     */
    void preUpdate(Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Post update.
     *
     * @param result the result
     * @param props the props
     * @param entityIds the entity ids
     * @param options the options
     */
    void postUpdate(int result, Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Pre update.
     *
     * @param entityName the entity name
     * @param props the props
     * @param condition the condition
     * @param options the options
     */
    void preUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     * Post update.
     *
     * @param result the result
     * @param entityName the entity name
     * @param props the props
     * @param condition the condition
     * @param options the options
     */
    void postUpdate(int result, String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     * Pre delete.
     *
     * @param entityId the entity id
     * @param options the options
     */
    void preDelete(EntityId entityId, Map<String, Object> options);

    /**
     * Post delete.
     *
     * @param result the result
     * @param entityId the entity id
     * @param options the options
     */
    void postDelete(int result, EntityId entityId, Map<String, Object> options);

    /**
     * Pre delete.
     *
     * @param entity the entity
     * @param options the options
     */
    void preDelete(T entity, Map<String, Object> options);

    /**
     * Post delete.
     *
     * @param result the result
     * @param entity the entity
     * @param options the options
     */
    void postDelete(int result, T entity, Map<String, Object> options);

    /**
     * Pre delete.
     *
     * @param entityIds the entity ids
     * @param options the options
     */
    void preDelete(List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Post delete.
     *
     * @param result the result
     * @param entityIds the entity ids
     * @param options the options
     */
    void postDelete(int result, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Pre delete.
     *
     * @param entities the entities
     * @param options the options
     */
    void preDelete(Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Post delete.
     *
     * @param result the result
     * @param entities the entities
     * @param options the options
     */
    void postDelete(int result, Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Pre delete.
     *
     * @param entityName the entity name
     * @param condition the condition
     * @param options the options
     */
    void preDelete(String entityName, Condition condition, Map<String, Object> options);

    /**
     * Post delete.
     *
     * @param result the result
     * @param entityName the entity name
     * @param condition the condition
     * @param options the options
     */
    void postDelete(int result, String entityName, Condition condition, Map<String, Object> options);

    /**
     * Pre query.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param resultHandle the result handle
     * @param options the options
     */
    void preQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle, Map<String, Object> options);

    /**
     * Post query.
     *
     * @param dataSet the data set
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @param resultHandle the result handle
     * @param options the options
     */
    void postQuery(DataSet dataSet, String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options);

    /**
     * Pre get result by handle.
     *
     * @param resultHandle the result handle
     * @param selectPropNames the select prop names
     * @param options the options
     */
    void preGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Post get result by handle.
     *
     * @param dataSet the data set
     * @param resultHandle the result handle
     * @param selectPropNames the select prop names
     * @param options the options
     */
    void postGetResultByHandle(DataSet dataSet, String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Pre release result handle.
     *
     * @param resultHandle the result handle
     */
    void preReleaseResultHandle(String resultHandle);

    /**
     * Post release result handle.
     *
     * @param resultHandle the result handle
     */
    void postReleaseResultHandle(String resultHandle);

    /**
     * Pre begin transaction.
     *
     * @param isolationLevel the isolation level
     * @param options the options
     */
    void preBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Post begin transaction.
     *
     * @param transactionId the transaction id
     * @param isolationLevel the isolation level
     * @param options the options
     */
    void postBeginTransaction(String transactionId, IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Pre end transaction.
     *
     * @param transactionId the transaction id
     * @param tractionAction the traction action
     * @param options the options
     */
    void preEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options);

    /**
     * Post end transaction.
     *
     * @param transactionId the transaction id
     * @param tractionAction the traction action
     * @param options the options
     */
    void postEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options);

    /**
     * Pre get record version.
     *
     * @param entityId the entity id
     * @param options the options
     */
    @Deprecated
    void preGetRecordVersion(EntityId entityId, Map<String, Object> options);

    /**
     * Post get record version.
     *
     * @param version the version
     * @param entityId the entity id
     * @param options the options
     */
    @Deprecated
    void postGetRecordVersion(long version, EntityId entityId, Map<String, Object> options);

    /**
     * Pre lock record.
     *
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options the options
     */
    @Deprecated
    void preLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Post lock record.
     *
     * @param lockCode the lock code
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options the options
     */
    @Deprecated
    void postLockRecord(String lockCode, EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Pre unlock record.
     *
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options the options
     */
    @Deprecated
    void preUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options);

    /**
     * Post unlock record.
     *
     * @param result the result
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options the options
     */
    @Deprecated
    void postUnlockRecord(boolean result, EntityId entityId, String lockCode, Map<String, Object> options);
}

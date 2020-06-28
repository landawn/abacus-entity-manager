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
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public interface Handler<T> {

    /**
     *
     * @param entityId
     * @param selectPropNames
     * @param options
     */
    void preGet(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityId
     * @param selectPropNames
     * @param options
     */
    void postGet(T result, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     *
     * @param entityIds
     * @param selectPropNames
     * @param options
     */
    void preGet(List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityIds
     * @param selectPropNames
     * @param options
     */
    void postGet(List<T> result, List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     */
    void preList(String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     */
    void postList(List<T> result, String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    /**
     *
     * @param entity
     * @param options
     */
    void preAdd(T entity, Map<String, Object> options);

    /**
     *
     * @param entityId
     * @param entity
     * @param options
     */
    void postAdd(EntityId entityId, T entity, Map<String, Object> options);

    /**
     *
     * @param entities
     * @param options
     */
    void preAdd(Collection<? extends T> entities, Map<String, Object> options);

    /**
     *
     * @param entityIds
     * @param entities
     * @param options
     */
    void postAdd(List<EntityId> entityIds, Collection<? extends T> entities, Map<String, Object> options);

    /**
     *
     * @param entityName
     * @param props
     * @param options
     */
    void preAdd(String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     *
     * @param entityId
     * @param entityName
     * @param props
     * @param options
     */
    void postAdd(EntityId entityId, String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     *
     * @param entityName
     * @param propsList
     * @param options
     */
    void preAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     *
     * @param entityIds
     * @param entityName
     * @param propsList
     * @param options
     */
    void postAdd(List<EntityId> entityIds, String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     *
     * @param entity
     * @param options
     */
    void preUpdate(T entity, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entity
     * @param options
     */
    void postUpdate(int result, T entity, Map<String, Object> options);

    /**
     *
     * @param props
     * @param entityId
     * @param options
     */
    void preUpdate(Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    /**
     *
     * @param result
     * @param props
     * @param entityId
     * @param options
     */
    void postUpdate(int result, Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    /**
     *
     * @param entities
     * @param options
     */
    void preUpdate(Collection<? extends T> entities, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entities
     * @param options
     */
    void postUpdate(int result, Collection<? extends T> entities, Map<String, Object> options);

    /**
     *
     * @param props
     * @param entityIds
     * @param options
     */
    void preUpdate(Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     *
     * @param result
     * @param props
     * @param entityIds
     * @param options
     */
    void postUpdate(int result, Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     *
     * @param entityName
     * @param props
     * @param condition
     * @param options
     */
    void preUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityName
     * @param props
     * @param condition
     * @param options
     */
    void postUpdate(int result, String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     *
     * @param entityId
     * @param options
     */
    void preDelete(EntityId entityId, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityId
     * @param options
     */
    void postDelete(int result, EntityId entityId, Map<String, Object> options);

    /**
     *
     * @param entity
     * @param options
     */
    void preDelete(T entity, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entity
     * @param options
     */
    void postDelete(int result, T entity, Map<String, Object> options);

    /**
     *
     * @param entityIds
     * @param options
     */
    void preDelete(List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityIds
     * @param options
     */
    void postDelete(int result, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     *
     * @param entities
     * @param options
     */
    void preDelete(Collection<? extends T> entities, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entities
     * @param options
     */
    void postDelete(int result, Collection<? extends T> entities, Map<String, Object> options);

    /**
     *
     * @param entityName
     * @param condition
     * @param options
     */
    void preDelete(String entityName, Condition condition, Map<String, Object> options);

    /**
     *
     * @param result
     * @param entityName
     * @param condition
     * @param options
     */
    void postDelete(int result, String entityName, Condition condition, Map<String, Object> options);

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     */
    void preQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle, Map<String, Object> options);

    /**
     *
     * @param dataSet
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     */
    void postQuery(DataSet dataSet, String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options);

    /**
     * Pre get result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     */
    void preGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Post get result by handle.
     *
     * @param dataSet
     * @param resultHandle
     * @param selectPropNames
     * @param options
     */
    void postGetResultByHandle(DataSet dataSet, String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Pre release result handle.
     *
     * @param resultHandle
     */
    void preReleaseResultHandle(String resultHandle);

    /**
     * Post release result handle.
     *
     * @param resultHandle
     */
    void postReleaseResultHandle(String resultHandle);

    /**
     * Pre begin transaction.
     *
     * @param isolationLevel
     * @param options
     */
    void preBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Post begin transaction.
     *
     * @param transactionId
     * @param isolationLevel
     * @param options
     */
    void postBeginTransaction(String transactionId, IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Pre end transaction.
     *
     * @param transactionId
     * @param tractionAction
     * @param options
     */
    void preEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options);

    /**
     * Post end transaction.
     *
     * @param transactionId
     * @param tractionAction
     * @param options
     */
    void postEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options);

    /**
     * Pre get record version.
     *
     * @param entityId
     * @param options
     */
    @Deprecated
    void preGetRecordVersion(EntityId entityId, Map<String, Object> options);

    /**
     * Post get record version.
     *
     * @param version
     * @param entityId
     * @param options
     */
    @Deprecated
    void postGetRecordVersion(long version, EntityId entityId, Map<String, Object> options);

    /**
     * Pre lock record.
     *
     * @param entityId
     * @param lockMode
     * @param options
     */
    @Deprecated
    void preLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Post lock record.
     *
     * @param lockCode
     * @param entityId
     * @param lockMode
     * @param options
     */
    @Deprecated
    void postLockRecord(String lockCode, EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Pre unlock record.
     *
     * @param entityId
     * @param lockCode
     * @param options
     */
    @Deprecated
    void preUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options);

    /**
     * Post unlock record.
     *
     * @param result
     * @param entityId
     * @param lockCode
     * @param options
     */
    @Deprecated
    void postUnlockRecord(boolean result, EntityId entityId, String lockCode, Map<String, Object> options);
}

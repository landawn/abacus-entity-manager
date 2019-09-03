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
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.util.u.Holder;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractHandler.
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public abstract class AbstractHandler<T> implements Handler<T> {

    /** The entity manager. */
    protected final EntityManager<T> entityManager;

    /**
     * Instantiates a new abstract handler.
     *
     * @param entityManager
     */
    public AbstractHandler(EntityManager<T> entityManager) {
        this.entityManager = entityManager;
    }

    /**
     *
     * @param entityId
     * @param selectPropNames
     * @param options
     */
    @Override
    public void preGet(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityId
     * @param selectPropNames
     * @param options
     */
    @Override
    public void postGet(T result, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityIds
     * @param selectPropNames
     * @param options
     */
    @Override
    public void preGet(List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityIds
     * @param selectPropNames
     * @param options
     */
    @Override
    public void postGet(List<T> result, List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     */
    @Override
    public void preList(String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     */
    @Override
    public void postList(List<T> result, String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entity
     * @param options
     */
    @Override
    public void preAdd(T entity, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityId
     * @param entity
     * @param options
     */
    @Override
    public void postAdd(EntityId entityId, T entity, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entities
     * @param options
     */
    @Override
    public void preAdd(Collection<? extends T> entities, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityIds
     * @param entities
     * @param options
     */
    @Override
    public void postAdd(List<EntityId> entityIds, Collection<? extends T> entities, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityName
     * @param props
     * @param options
     */
    @Override
    public void preAdd(String entityName, Map<String, Object> props, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityId
     * @param entityName
     * @param props
     * @param options
     */
    @Override
    public void postAdd(EntityId entityId, String entityName, Map<String, Object> props, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityName
     * @param propsList
     * @param options
     */
    @Override
    public void preAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityIds
     * @param entityName
     * @param propsList
     * @param options
     */
    @Override
    public void postAdd(List<EntityId> entityIds, String entityName, List<Map<String, Object>> propsList, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entity
     * @param options
     */
    @Override
    public void preUpdate(T entity, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entity
     * @param options
     */
    @Override
    public void postUpdate(int result, T entity, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param props
     * @param entityId
     * @param options
     */
    @Override
    public void preUpdate(Map<String, Object> props, EntityId entityId, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param props
     * @param entityId
     * @param options
     */
    @Override
    public void postUpdate(int result, Map<String, Object> props, EntityId entityId, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entities
     * @param options
     */
    @Override
    public void preUpdate(Collection<? extends T> entities, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entities
     * @param options
     */
    @Override
    public void postUpdate(int result, Collection<? extends T> entities, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param props
     * @param entityIds
     * @param options
     */
    @Override
    public void preUpdate(Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param props
     * @param entityIds
     * @param options
     */
    @Override
    public void postUpdate(int result, Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityName
     * @param props
     * @param condition
     * @param options
     */
    @Override
    public void preUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityName
     * @param props
     * @param condition
     * @param options
     */
    @Override
    public void postUpdate(int result, String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityId
     * @param options
     */
    @Override
    public void preDelete(EntityId entityId, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityId
     * @param options
     */
    @Override
    public void postDelete(int result, EntityId entityId, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entity
     * @param options
     */
    @Override
    public void preDelete(T entity, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entity
     * @param options
     */
    @Override
    public void postDelete(int result, T entity, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityIds
     * @param options
     */
    @Override
    public void preDelete(List<? extends EntityId> entityIds, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityIds
     * @param options
     */
    @Override
    public void postDelete(int result, List<? extends EntityId> entityIds, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entities
     * @param options
     */
    @Override
    public void preDelete(Collection<? extends T> entities, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entities
     * @param options
     */
    @Override
    public void postDelete(int result, Collection<? extends T> entities, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityName
     * @param condition
     * @param options
     */
    @Override
    public void preDelete(String entityName, Condition condition, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param result
     * @param entityName
     * @param condition
     * @param options
     */
    @Override
    public void postDelete(int result, String entityName, Condition condition, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     */
    @Override
    public void preQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle, Map<String, Object> options) {
        // do nothing
    }

    /**
     *
     * @param dataSet
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param resultHandle
     * @param options
     */
    @Override
    public void postQuery(DataSet dataSet, String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options) {
        // do nothing
    }

    /**
     * Pre get result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     */
    @Override
    public void preGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Post get result by handle.
     *
     * @param dataSet
     * @param resultHandle
     * @param selectPropNames
     * @param options
     */
    @Override
    public void postGetResultByHandle(DataSet dataSet, String resultHandle, Collection<String> selectPropNames, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Pre release result handle.
     *
     * @param resultHandle
     */
    @Override
    public void preReleaseResultHandle(String resultHandle) {
        // do nothing
    }

    /**
     * Post release result handle.
     *
     * @param resultHandle
     */
    @Override
    public void postReleaseResultHandle(String resultHandle) {
        // do nothing
    }

    /**
     * Pre begin transaction.
     *
     * @param isolationLevel
     * @param options
     */
    @Override
    public void preBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Post begin transaction.
     *
     * @param transactionId
     * @param isolationLevel
     * @param options
     */
    @Override
    public void postBeginTransaction(String transactionId, IsolationLevel isolationLevel, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Pre end transaction.
     *
     * @param transactionId
     * @param tractionAction
     * @param options
     */
    @Override
    public void preEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Post end transaction.
     *
     * @param transactionId
     * @param tractionAction
     * @param options
     */
    @Override
    public void postEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Pre get record version.
     *
     * @param entityId
     * @param options
     */
    @Override
    public void preGetRecordVersion(EntityId entityId, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Post get record version.
     *
     * @param version
     * @param entityId
     * @param options
     */
    @Override
    public void postGetRecordVersion(long version, EntityId entityId, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Pre lock record.
     *
     * @param entityId
     * @param lockMode
     * @param options
     */
    @Override
    public void preLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Post lock record.
     *
     * @param lockCode
     * @param entityId
     * @param lockMode
     * @param options
     */
    @Override
    public void postLockRecord(String lockCode, EntityId entityId, LockMode lockMode, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Pre unlock record.
     *
     * @param entityId
     * @param lockCode
     * @param options
     */
    @Override
    public void preUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options) {
        // do nothing
    }

    /**
     * Post unlock record.
     *
     * @param result
     * @param entityId
     * @param lockCode
     * @param options
     */
    @Override
    public void postUnlockRecord(boolean result, EntityId entityId, String lockCode, Map<String, Object> options) {
        // do nothing
    }
}

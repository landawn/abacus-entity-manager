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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public interface Handler<T> {

    void preGet(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    void postGet(T result, EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options);

    void preGet(List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options);

    void postGet(List<T> result, List<? extends EntityId> entityIds, Collection<String> selectPropNames, Map<String, Object> options);

    void preList(String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    void postList(List<T> result, String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    void preAdd(T entity, Map<String, Object> options);

    void postAdd(EntityId entityId, T entity, Map<String, Object> options);

    void preAdd(Collection<? extends T> entities, Map<String, Object> options);

    void postAdd(List<EntityId> entityIds, Collection<? extends T> entities, Map<String, Object> options);

    void preAdd(String entityName, Map<String, Object> props, Map<String, Object> options);

    void postAdd(EntityId entityId, String entityName, Map<String, Object> props, Map<String, Object> options);

    void preAdd(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    void postAdd(List<EntityId> entityIds, String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    void preUpdate(T entity, Map<String, Object> options);

    void postUpdate(int result, T entity, Map<String, Object> options);

    void preUpdate(Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    void postUpdate(int result, Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    void preUpdate(Collection<? extends T> entities, Map<String, Object> options);

    void postUpdate(int result, Collection<? extends T> entities, Map<String, Object> options);

    void preUpdate(Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    void postUpdate(int result, Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    void preUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    void postUpdate(int result, String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    void preDelete(EntityId entityId, Map<String, Object> options);

    void postDelete(int result, EntityId entityId, Map<String, Object> options);

    void preDelete(T entity, Map<String, Object> options);

    void postDelete(int result, T entity, Map<String, Object> options);

    void preDelete(List<? extends EntityId> entityIds, Map<String, Object> options);

    void postDelete(int result, List<? extends EntityId> entityIds, Map<String, Object> options);

    void preDelete(Collection<? extends T> entities, Map<String, Object> options);

    void postDelete(int result, Collection<? extends T> entities, Map<String, Object> options);

    void preDelete(String entityName, Condition condition, Map<String, Object> options);

    void postDelete(int result, String entityName, Condition condition, Map<String, Object> options);

    void preQuery(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle, Map<String, Object> options);

    void postQuery(DataSet dataSet, String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle,
            Map<String, Object> options);

    void preGetResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    void postGetResultByHandle(DataSet dataSet, String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    void preReleaseResultHandle(String resultHandle);

    void postReleaseResultHandle(String resultHandle);

    void preBeginTransaction(IsolationLevel isolationLevel, Map<String, Object> options);

    void postBeginTransaction(String transactionId, IsolationLevel isolationLevel, Map<String, Object> options);

    void preEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options);

    void postEndTransaction(String transactionId, Action tractionAction, Map<String, Object> options);

    @Deprecated
    void preGetRecordVersion(EntityId entityId, Map<String, Object> options);

    @Deprecated
    void postGetRecordVersion(long version, EntityId entityId, Map<String, Object> options);

    @Deprecated
    void preLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options);

    @Deprecated
    void postLockRecord(String lockCode, EntityId entityId, LockMode lockMode, Map<String, Object> options);

    @Deprecated
    void preUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options);

    @Deprecated
    void postUnlockRecord(boolean result, EntityId entityId, String lockCode, Map<String, Object> options);
}

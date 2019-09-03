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

package com.landawn.abacus;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.exception.DuplicatedResultException;
import com.landawn.abacus.exception.InvalidTransactionIdException;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.util.u.Holder;
import com.landawn.abacus.util.u.Optional;

// TODO: Auto-generated Javadoc
/**
 * This interface defines the basic APIs for data access between Java application and data store. Composed entities
 * which have entity properties are not supported.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface DBAccess {

    /**
     * Find entity from data store by the specified {@code entityId}.
     *
     * @param <T>
     * @param entityId
     * @return T
     * @throws DuplicatedResultException if more than one record found by the specified {@code entityId}.
     */
    <T> Optional<T> get(EntityId entityId) throws DuplicatedResultException;

    /**
     * Find entity from data store by the specified {@code entityId}.
     *
     * @param <T>
     * @param entityId
    mes specifies the properties need to be load. all properties will be loaded if it's null.
     * @return T
     * @throws DuplicatedResultException if more than one record found by the specified {@code entityId}.
     */
    <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames) throws DuplicatedResultException;

    /**
     * Find entity from data store by the specified {@code entityId}.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames specifies the properties need to be load. all properties will be loaded if it's null.
     * @param options {@link com.landawn.abacus.util.Options}
     * @return T
     * @throws DuplicatedResultException if more than one record found by the specified {@code entityId}.
     */
    <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) throws DuplicatedResultException;

    /**
     * Find entity from data store by the specified {@code entityId}.
     *
     * @param <T>
     * @param entityId
     * @return T
     * @throws DuplicatedResultException if more than one record found by the specified {@code entityId}.
     */
    <T> T gett(EntityId entityId) throws DuplicatedResultException;

    /**
     * Find entity from data store by the specified {@code entityId}.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames specifies the properties need to be load. all properties will be loaded if it's null.
     * @return T
     * @throws DuplicatedResultException if more than one record found by the specified {@code entityId}.
     */
    <T> T gett(EntityId entityId, Collection<String> selectPropNames) throws DuplicatedResultException;

    /**
     * Find entity from data store by the specified {@code entityId}.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames specifies the properties need to be load. all properties will be loaded if it's null.
     * @param options {@link com.landawn.abacus.util.Options}
     * @return T
     * @throws DuplicatedResultException if more than one record found by the specified {@code entityId}.
     */
    <T> T gett(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) throws DuplicatedResultException;

    /**
     *
     * @param <T>
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @return
     */
    <T> List<T> list(String entityName, Collection<String> selectPropNames, Condition condition);

    /**
     *
     * @param <T>
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     * @return
     */
    <T> List<T> list(String entityName, Collection<String> selectPropNames, Condition condition, Map<String, Object> options);

    /**
     * Insert a new entity into data store.
     *
     * @param entityName
     * @param props
     * @param options supported option:{@code Tran.ID}.
     * @return
     */
    EntityId add(String entityName, Map<String, Object> props, Map<String, Object> options);

    /**
     * Insert entities into data store. If the size of element in the {@code propsList} is different, or the element has
     * different properties, The vacancy property is inserted with {@code default} value.
     *
     * @param entityName
     * @param propsList
     * @param options {@link com.landawn.abacus.util.Options}
     * @return List<EntityId>
     */
    List<EntityId> addAll(String entityName, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     * Update the records identified by the specified {@code entityId} with the specified {@code props}.
     * @param props map key is property name; map value is property value.
     * @param entityId
     * @return
     */
    int update(Map<String, Object> props, EntityId entityId);

    /**
     * Update the records identified by the specified {@code entityId} with the specified {@code props}.
     * @param props map key is property name; map value is property value.
     * @param entityId
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    int update(Map<String, Object> props, EntityId entityId, Map<String, Object> options);

    /**
     * Update the records identified by the specified {@code entityIds} with the specified {@code props}.
     * @param props map key is property name; map value is property value.
     * @param entityIds
     * @return
     */
    int updateAll(Map<String, Object> props, List<? extends EntityId> entityIds);

    /**
     * Update the records identified by the specified {@code entityIds} with the specified {@code props}.
     * @param props map key is property name; map value is property value.
     * @param entityIds
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    int updateAll(Map<String, Object> props, List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Update entities by the specified {@code condition} with the values in {@code props}.
     *
     * @param entityName
     * @param props
     * @param condition {@code com.landawn.abacus.condition.ConditionFactory}. All of the records will be returned by query or
     *            updated/deleted if condition is 'Empty' or 'null' .
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    int update(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     * Delete record from data store by the specified {@code entityId}.
     *
     * @param entityId
     * @return
     */
    int delete(EntityId entityId);

    /**
     * Delete record from data store by the specified {@code entityId}.
     *
     * @param entityId
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    int delete(EntityId entityId, Map<String, Object> options);

    /**
     * Delete record from data store by the specified {@code entityIds}.
     *
     * @param entityIds
     * @return
     */
    int deleteAll(List<? extends EntityId> entityIds);

    /**
     * Delete record from data store by the specified {@code entityIds}.
     *
     * @param entityIds
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    int deleteAll(List<? extends EntityId> entityIds, Map<String, Object> options);

    /**
     * Delete the entities from data store by the specified {@code condition}.
     *
     * @param entityName
     * @param condition {@code com.landawn.abacus.condition.ConditionFactory}. All of the records will be returned by query or
     *            updated/deleted if condition is 'Empty' or 'null' .
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    int delete(String entityName, Condition condition, Map<String, Object> options);

    /**
     * Find the result from data store by the specified {@code condition}. . An empty {@code ResueltSet} will be
     * returned if no result found.
     *
     * @param entityName
     * @param selectPropNames specifies the properties need to be load. all properties will be loaded if it's null.
     * @param condition {@code com.landawn.abacus.condition.ConditionFactory}. All of the records will be returned by query or
     *            updated/deleted if condition is 'Empty' or 'null' .
     * @return DataSet
     */
    DataSet query(String entityName, Collection<String> selectPropNames, Condition condition);

    /**
     * Find the result from data store by the specified {@code condition}. An empty {@code ResueltSet} will be returned
     * if no result found.
     * 
     * Note: the cache in result handle won't be auto refreshed.
     *
     * @param entityName
     * @param selectPropNames specifies the properties need to be load. all properties will be loaded if it's null.
     * @param condition the condition will be ignored is {@code resultHandle} is not empty.
     * @param resultHandle
     * @param options {@link com.landawn.abacus.util.Options}
     * @return DataSet
     */
    DataSet query(String entityName, Collection<String> selectPropNames, Condition condition, Holder<String> resultHandle, Map<String, Object> options);

    /**
     * Get result by the specified {@code resultHandle}.
     *
     * @param resultHandle
     * @param selectPropNames specifies the properties need to be load. all properties will be loaded if it's null.
     * @param options {@link com.landawn.abacus.util.Options}
     * @return DataSet
     */
    DataSet getResultByHandle(String resultHandle, Collection<String> selectPropNames, Map<String, Object> options);

    /**
     * Release the result hook by the specified {@code resultHandle}.
     *
     * @param resultHandle
     */
    void releaseResultHandle(String resultHandle);

    /**
     * Start a transaction.
     *
     * @param isolationLevel
     * @param options {@link com.landawn.abacus.util.Options}
     * @return
     */
    String beginTransaction(IsolationLevel isolationLevel, Map<String, Object> options);

    /**
     * Commit or roll back the transaction by the specified transaction id and action. 
     * <br>Transaction will be automatically rolled back if error occurs when commit the transaction if <code>Options.AUTO_ROLLBACK_TRANSACTION</code> is not set or it's set to <code>true</code>
     * <br>No matter there is an error occurring or not, the transaction will be closed finally.
     *
     * @param transactionId
     * @param transactionAction
     * @param options {@link com.landawn.abacus.util.Options}
     * @throws InvalidTransactionIdException             If there is no transaction mapping to the specified {@code transactionId}.
     * @see com.landawn.abacus.Transaction
     */
    void endTransaction(String transactionId, Action transactionAction, Map<String, Object> options);

    /**
     * Gets the entity definition factory.
     *
     * @return EntityDefinitionFactory
     */
    @Internal
    @Deprecated
    EntityDefinitionFactory getEntityDefinitionFactory();
}

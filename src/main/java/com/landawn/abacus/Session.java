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

import com.landawn.abacus.annotation.Beta;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.util.u.Optional;

// TODO: Auto-generated Javadoc
/**
 * The Interface Session.
 *
 * @author Haiyang Li
 * @param <T> the entity type
 * @since 0.8
 */
@Beta
@Deprecated
public interface Session<T> {

    /**
     * Start a transaction.
     *
     * @param isolationLevel the isolation level
     * @return Transaction
     * @see com.landawn.abacus.DBAccess#startTransaction(IsolationLevel, Map)
     */
    Transaction beginTransaction(IsolationLevel isolationLevel);

    /**
     * Gets the.
     *
     * @param <TT> the target entity type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return the optional
     */
    <TT> Optional<TT> get(EntityId entityId, Collection<String> selectPropNames);

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return the t
     */
    <TT> TT gett(EntityId entityId, Collection<String> selectPropNames);

    /**
     * List.
     *
     * @param <TT> the target entity type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @return the list
     */
    <TT> List<TT> list(String entityName, Collection<String> selectPropNames, Condition condition);

    /**
     * Make the entity instances managed and persistent. The entity in {@code entities} must be the same type entity.
     * the changes will be committed to data store when flush API is called or the session is closed.
     *
     * @param entities the entities
     */
    void add(T... entities);

    /**
     * Make the entity instances managed and persistent. The entity in {@code entities} must be the same type entity.
     * the changes will be committed to data store when flush API is called or the session is closed.
     *
     * @param entities the entities
     */
    void add(Collection<? extends T> entities);

    /**
     * Add these entities to the entity list managed by this session. Any update in these entities will be committed to
     * data store when flush this session.
     *
     * @param entities the entities
     */
    void update(T... entities);

    /**
     * Add these entities to the entity list managed by this session. Any update in these entities will be committed to
     * data store when flush this session.s Collection<? extends E>
     *
     * @param entities the entities
     */
    void update(Collection<? extends T> entities);

    /**
     * Mark these entities to delete from data store. the changes will be committed to data store when flush API is
     * called or the session is closed. the specified entities will be detached if flush successfully.
     *
     * @param entities the entities
     * @see com.landawn.abacus.util.Options.Eran
     */
    void delete(T... entities);

    /**
     * Mark these entities to delete from data store. the changes will be committed to data store when flush API is
     * called or the session is closed. the specified entities will be detached if flush successfully.
     *
     * @param entities the entities
     * @see com.landawn.abacus.util.Options.Eran
     */
    void delete(Collection<? extends T> entities);

    /**
     * Remove these entities from the entity list managed by this session. Any update in these entities will not be
     * committed to data store when flush this session.
     *
     * @param entities the entities
     */
    void detach(T... entities);

    /**
     * Remove these entities from the entity list managed by this session. Any update in these entities will not be
     * committed to data store when flush this session.
     *
     * @param entities the entities
     */
    void detach(Collection<? extends T> entities);

    /**
     * Check if the instance belongs to the current persistence context.
     *
     * @param entity the entity
     * @return boolean
     */
    boolean contains(T entity);

    /**
     * Method isDirty.
     * 
     * @return boolean
     */
    boolean isDirty();

    /**
     * Synchronize the persistence context to the underlying database.
     */
    void flush();

    /**
     * Method clear.
     */
    void clear();

    /**
     * flush and close this session.
     */
    void close();

    /**
     * Method close.
     * 
     * @return boolean
     */
    boolean isClosed();
}

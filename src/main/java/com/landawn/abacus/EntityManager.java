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

// TODO: Auto-generated Javadoc
/**
 * <br>
 * Main interface of entity manager. <code>
 * <br> EntityManager<Author> entityManager =
 * <br> com.landawn.abacus.core.EntityManagerFactory.getInstance().getEntityManager(domainName);
 * <br>
 * <br> Author author = new Author();
 * <br> author.setFirstName("firstName");
 * <br> author.setLastName("lastName");
 * <br> EntityId entityId = entityManager.add(author);
 * <br>
 * <br> Author dbAuthor = entityManager.get(entityId);
 * <br> N.println(dbAuthor);
 * <br>
 * <br> dbAuthor.setFirstName("updatedFirstName");
 * <br> entityManager.update(dbAuthor);
 * <br> dbAuthor = entityManager.get(entityId);
 * <br> N.println(dbAuthor);
 * <br>
 * <br> Condition cond = ConditionFactory.eq(Author.FIRST_NAME, "updatedFirstName");
 * <br> ResultList resultList = entityManager.query(Author.__, null, cond);
 * <br> N.println(resultList);
 * <br>
 * <br> entityManager.delete(dbAuthor);
 * <br> dbAuthor = entityManager.get(entityId);
 * <br> N.println(dbAuthor);
 * </code>
 * 
 * <br>
 * <br>
 * Design principles: <li>1, Simple (is beautiful)</li> <li>2, Fast (is powerful)</li> <li>3, Concepts (must be integral
 * and consistent)</li> <br/>
 * These principles can't be broken by any change or reason. And basically programmable is > configurable. There is no
 * extra support by configuration file or annotation. <br>
 * <br>
 * All the implementation should be multiple thread safety. <br>
 * 
 * <br>
 * {@code EntityManager} doesn't support the inline changes made by {@code add/remove} methods in {@code ActiveRecord}.
 * these changes will be ignored. only the entity own property changes are committed if call {@code EntityManager}
 * .update(...) or delete(...) APIs to update an {@code ActiveRecord}.
 *
 * @author Haiyang Li
 * @param <T> the entity type
 * @since 0.8
 */
public interface EntityManager<T> extends DBAccess {

    /**
     * Insert the specified {@code entity} into data store.
     *
     * @param entity the entity
     * @return EntityId
     * @see #add(T[], Map)
     */
    EntityId add(T entity);

    /**
     * Insert the specified {@code entity} into data store.
     *
     * @param entity the entity
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return EntityId
     * @see #add(T[], Map)
     */
    EntityId add(T entity, Map<String, Object> options);

    /**
     * Insert the specified {@code entities} into data store. The entity in {@code entities} must be the same type
     * entity.
     *
     * @param entities the entities
     * @return EntityId[]
     * @see #add(T[], Map)
     */
    List<EntityId> addAll(Collection<? extends T> entities);

    /**
     * Insert the specified {@code entities} into data store. The entity in {@code entities} must be the same type
     * entity.
     *
     * @param entities the entities
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return int
     */
    List<EntityId> addAll(Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Update the record in data store by the updated properties in the specified {@code entity}.
     *
     * @param entity the entity
     * @return int
     * @see com.landawn.abacus.util.Options.Tran
     */
    int update(T entity);

    /**
     * Update the record in data store by the updated properties in the specified {@code entity}.
     *
     * @param entity the entity
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return int
     */
    int update(T entity, Map<String, Object> options);

    /**
     * Update the records in data store by the updated properties in the specified {@code entities}. The entity in
     * specified {@code entities} could be different type. The method will start a transaction to update.
     *
     * @param entities the entities
     * @return int
     * @see com.landawn.abacus.util.Options.Tran
     */
    int updateAll(Collection<? extends T> entities);

    /**
     * Update the records in data store by the updated properties in the specified {@code entities}. The entity in
     * specified {@code entities} could be different type. The method will start a transaction to update.
     *
     * @param entities the entities
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return int
     */
    int updateAll(Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Delete record from data store by the specified {@code entity}.
     *
     * @param entity the entity
     * @return int
     * @see com.landawn.abacus.util.Options.Tran
     */
    int delete(T entity);

    /**
     * Delete record from data store by the specified {@code entity}.
     *
     * @param entity the entity
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return int
     */
    int delete(T entity, Map<String, Object> options);

    /**
     * Delete record from data store by the specified {@code entities}.
     *
     * @param entities the entities
     * @return int
     */
    int deleteAll(Collection<? extends T> entities);

    /**
     * Delete record from data store by the specified {@code entities}.
     *
     * @param entities the entities
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return int
     */
    int deleteAll(Collection<? extends T> entities, Map<String, Object> options);

    /**
     * Lock the record identified by the specified {@code entityId} on the specified level {@code lockMode}.
     *
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return lock code. Return null if record has lock by other.
     * @deprecated 
     */
    @Deprecated
    String lockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options);

    /**
     * Release the lock on the record identified by the specified {@code entityId}.
     *
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return <code>true</code> if the record is locked by the specified {@code lockCode}.or already unlocked return
     *         {@code false} otherwise.
     * @deprecated 
     */
    @Deprecated
    boolean unlockRecord(EntityId entityId, String lockCode, Map<String, Object> options);

    /**
     * Return the version of the record identified by the specified {@code entityId}.
     *
     * @param entityId the entity id
     * @param options            {@link com.landawn.abacus.util.Options}
     * @return long
     * @deprecated 
     */
    @Deprecated
    long getRecordVersion(EntityId entityId, Map<String, Object> options);
}

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

package com.landawn.abacus.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.exception.DuplicatedResultException;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.u.Holder;
import com.landawn.abacus.util.u.Nullable;
import com.landawn.abacus.util.u.Optional;
import com.landawn.abacus.util.u.OptionalBoolean;
import com.landawn.abacus.util.u.OptionalByte;
import com.landawn.abacus.util.u.OptionalChar;
import com.landawn.abacus.util.u.OptionalDouble;
import com.landawn.abacus.util.u.OptionalFloat;
import com.landawn.abacus.util.u.OptionalInt;
import com.landawn.abacus.util.u.OptionalLong;
import com.landawn.abacus.util.u.OptionalShort;
import com.landawn.abacus.util.function.ToBooleanFunction;
import com.landawn.abacus.util.function.ToByteFunction;
import com.landawn.abacus.util.function.ToCharFunction;
import com.landawn.abacus.util.function.ToDoubleFunction;
import com.landawn.abacus.util.function.ToFloatFunction;
import com.landawn.abacus.util.function.ToIntFunction;
import com.landawn.abacus.util.function.ToLongFunction;
import com.landawn.abacus.util.function.ToShortFunction;

// TODO: Auto-generated Javadoc
/**
 * Multi-thread safe.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class NewEntityManager {

    /** The entity mapper pool. */
    @SuppressWarnings("rawtypes")
    private final Map<Class, Mapper> entityMapperPool = new HashMap<>();

    /** The entity manager. */
    private final EntityManagerEx<Object> em;

    /**
     * Instantiates a new entity manager ex.
     *
     * @param entityManager the entity manager
     */
    NewEntityManager(final EntityManagerEx<Object> entityManager) {
        this.em = entityManager;
    }

    /**
     * Mapper.
     *
     * @param <T> the generic type
     * @param entityClass the entity class
     * @return the mapper
     */
    public <T> Mapper<T> mapper(final Class<T> entityClass) {
        synchronized (entityMapperPool) {
            Mapper<T> mapper = entityMapperPool.get(entityClass);

            if (mapper == null) {
                mapper = new Mapper<T>(this, entityClass);
                entityMapperPool.put(entityClass, mapper);
            }

            return mapper;
        }
    }

    /**
     * Exists.
     *
     * @param entityClass the entity Class
     * @param id the id
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final long id) {
        return exists(createEntityId(entityClass, id));
    }

    /**
     * Exists.
     *
     * @param entityClass the entity Class
     * @param id the id
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final String id) {
        return exists(createEntityId(entityClass, id));
    }

    /**
     * Exists.
     *
     * @param entityId the entity id
     * @return true, if successful
     */
    public boolean exists(final EntityId entityId) {
        return exists(entityId, null);
    }

    /**
     * Exists.
     *
     * @param entityId the entity id
     * @param options the options
     * @return true, if successful
     */
    public boolean exists(final EntityId entityId, final Map<String, Object> options) {
        return em.exists(entityId, checkOptions(options));
    }

    /**
     * Exists.
     *
     * @param entityClass the entity Class
     * @param cond the cond
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final Condition cond) {
        return exists(entityClass, cond, null);
    }

    /**
     * Exists.
     *
     * @param entityClass the entity Class
     * @param cond the cond
     * @param options the options
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final Condition cond, final Map<String, Object> options) {
        return em.exists(EntityManagerUtil.getEntityName(entityClass), cond, checkOptions(options));
    }

    /**
     * Count.
     *
     * @param entityClass the entity Class
     * @param cond the cond
     * @return the int
     */
    public int count(final Class<?> entityClass, final Condition cond) {
        return count(entityClass, cond, null);
    }

    /**
     * Count.
     *
     * @param entityClass the entity Class
     * @param cond the cond
     * @param options the options
     * @return the int
     */
    public int count(final Class<?> entityClass, final Condition cond, final Map<String, Object> options) {
        return em.count(EntityManagerUtil.getEntityName(entityClass), cond, checkOptions(options));
    }

    /**
     * Query for boolean.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional boolean
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalBoolean queryForBoolean(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Boolean.class, entityClass, propName, cond).mapToBoolean(ToBooleanFunction.UNBOX);
    }

    /**
     * Query for char.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional char
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalChar queryForChar(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Character.class, entityClass, propName, cond).mapToChar(ToCharFunction.UNBOX);
    }

    /**
     * Query for byte.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional byte
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalByte queryForByte(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Byte.class, entityClass, propName, cond).mapToByte(ToByteFunction.UNBOX);
    }

    /**
     * Query for short.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional short
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalShort queryForShort(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Short.class, entityClass, propName, cond).mapToShort(ToShortFunction.UNBOX);
    }

    /**
     * Query for int.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional int
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalInt queryForInt(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Integer.class, entityClass, propName, cond).mapToInt(ToIntFunction.UNBOX);
    }

    /**
     * Query for long.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional long
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalLong queryForLong(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Long.class, entityClass, propName, cond).mapToLong(ToLongFunction.UNBOX);
    }

    /**
     * Query for float.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional float
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalFloat queryForFloat(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Float.class, entityClass, propName, cond).mapToFloat(ToFloatFunction.UNBOX);
    }

    /**
     * Query for double.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional double
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalDouble queryForDouble(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Double.class, entityClass, propName, cond).mapToDouble(ToDoubleFunction.UNBOX);
    }

    /**
     * Query for string.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<String> queryForString(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(String.class, entityClass, propName, cond);
    }

    /**
     * Query for date.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Date> queryForDate(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Date.class, entityClass, propName, cond);
    }

    /**
     * Query for time.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Time> queryForTime(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Time.class, entityClass, propName, cond);
    }

    /**
     * Query for timestamp.
     *
     * @param entityClass the entity class
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Timestamp> queryForTimestamp(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Timestamp.class, entityClass, propName, cond);
    }

    /**
     * Query for single result.
     *
     * @param <V> the value type
     * @param targetClass the target class
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(Class, String, String, Condition, Map<String, Object>).
     */
    public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(targetClass, entityClass, propName, cond, null);
    }

    /**
     * Returns a {@code Nullable} describing the value in the first row/column if it exists, otherwise return an empty {@code Nullable}.
     * 
     * 
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <V> the value type
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the nullable
     */
    @SuppressWarnings("unchecked")
    public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond,
            final Map<String, Object> options) {
        return em.queryForSingleResult(targetClass, EntityManagerUtil.getEntityName(entityClass), propName, cond, checkOptions(options));
    }

    /**
     * Query for single non null.
     *
     * @param <V> the value type
     * @param targetClass the target class
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional
     */
    public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleNonNull(targetClass, entityClass, propName, cond, null);
    }

    /**
     * Returns an {@code Optional} describing the value in the first row/column if it exists, otherwise return an empty {@code Optional}.
     * 
     * 
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <V> the value type
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the optional
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond,
            final Map<String, Object> options) {
        return em.queryForSingleNonNull(targetClass, EntityManagerUtil.getEntityName(entityClass), propName, cond, checkOptions(options));
    }

    /**
     * Query for unique result.
     *
     * @param <V> the value type
     * @param targetClass the target class
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @throws DuplicatedResultException if more than one record found.
     * @see SQLExecutor#queryForUniqueResult(Class, String, String, Condition, Map<String, Object>).
     */
    public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond)
            throws DuplicatedResultException {
        return queryForUniqueResult(targetClass, entityClass, propName, cond, null);
    }

    /**
     * Returns a {@code Nullable} describing the value in the first row/column if it exists, otherwise return an empty {@code Nullable}.
     * And throws {@code DuplicatedResultException} if more than one record found.
     * 
     * 
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <V> the value type
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the nullable
     * @throws DuplicatedResultException if more than one record found.
     */
    @SuppressWarnings("unchecked")
    public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond,
            final Map<String, Object> options) throws DuplicatedResultException {
        return em.queryForUniqueResult(targetClass, EntityManagerUtil.getEntityName(entityClass), propName, cond, checkOptions(options));
    }

    /**
     * Query for unique non null.
     *
     * @param <V> the value type
     * @param targetClass the target class
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @return the optional
     * @throws DuplicatedResultException if more than one record found.
     */
    public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond)
            throws DuplicatedResultException {
        return queryForUniqueNonNull(targetClass, entityClass, propName, cond, null);
    }

    /**
     * Returns an {@code Optional} describing the value in the first row/column if it exists, otherwise return an empty {@code Optional}.
     * And throws {@code DuplicatedResultException} if more than one record found.
     * 
     * 
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <V> the value type
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass the entity Class
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the optional
     * @throws DuplicatedResultException if more than one record found.
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond,
            final Map<String, Object> options) throws DuplicatedResultException {
        return em.queryForUniqueNonNull(targetClass, EntityManagerUtil.getEntityName(entityClass), propName, cond, checkOptions(options));
    }

    /**
     * Query.
     *
     * @param entityClass the entity Class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @return the data set
     */
    public DataSet query(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond) {
        return query(entityClass, selectPropNames, cond, null);
    }

    /**
     * Query.
     *
     * @param entityClass the entity Class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the data set
     */
    public DataSet query(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return query(entityClass, selectPropNames, cond, null, options);
    }

    /**
     * Query.
     *
     * @param entityClass the entity Class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param resultHandle the result handle
     * @param options the options
     * @return the data set
     */
    public DataSet query(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond, final Holder<String> resultHandle,
            final Map<String, Object> options) {
        return em.query(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, resultHandle, checkOptions(options));
    }

    /**
     * Returns the merged ResultSet acquired by querying with the specified entity and its slices if it has.
     * Mostly it's designed for partition to query different partitioning tables in the specified data sources.
     * 
     * By default it's queried in parallel. but it can be set to sequential query by set <code>Query.QUERY_IN_PARALLEL=false</code> in options
     *
     * @param entityClass the entity Class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options            Multiple data sources can be specified by query options: <code>Query.QUERY_WITH_DATA_SOURCES</code>
     * @return the merged result
     */
    public DataSet queryAll(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return em.queryAll(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, checkOptions(options));
    }

    /**
     * Find first.
     *
     * @param <T> the generic type
     * @param entityClass the entity class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @return the optional
     */
    //
    public <T> Optional<T> findFirst(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond) {
        return findFirst(entityClass, selectPropNames, cond, null);
    }

    /**
     * Just fetch the result in the 1st row. {@code null} is returned if no result is found. This method will try to
     * convert the column value to the type of mapping entity property if the mapping entity property is not assignable
     * from column value.
     * 
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <T> the generic type
     * @param entityClass the entity class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the optional
     */
    public <T> Optional<T> findFirst(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options) {
        return em.findFirst(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, checkOptions(options));
    }

    /**
     * List.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @return the list
     */
    public <T> List<T> list(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond) {
        return list(entityClass, selectPropNames, cond, null);
    }

    /**
     * List.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the list
     */
    public <T> List<T> list(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return em.list(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, checkOptions(options));
    }

    /**
     * 
     * @param <T>
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    public <T> List<T> listAll(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return em.listAll(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, checkOptions(options));
    }

    /**
     * Adds the.
     *
     * @param entityClass the entity Class
     * @param props the props
     * @return the entity id
     */
    public EntityId add(final Class<?> entityClass, final Map<String, Object> props) {
        return add(entityClass, props, null);
    }

    /**
     * Adds the.
     *
     * @param entityClass the entity Class
     * @param props the props
     * @param options the options
     * @return the entity id
     */
    public EntityId add(final Class<?> entityClass, final Map<String, Object> props, final Map<String, Object> options) {
        return em.add(EntityManagerUtil.getEntityName(entityClass), props, checkOptions(options));
    }

    /**
     * Adds the all.
     *
     * @param entityClass the entity Class
     * @param propsList the props list
     * @return the list
     */
    public List<EntityId> addAll(final Class<?> entityClass, final List<Map<String, Object>> propsList) {
        return addAll(entityClass, propsList, null);
    }

    /**
     * Adds the all.
     *
     * @param entityClass the entity Class
     * @param propsList the props list
     * @param options the options
     * @return the list
     */
    public List<EntityId> addAll(final Class<?> entityClass, final List<Map<String, Object>> propsList, final Map<String, Object> options) {
        return em.addAll(EntityManagerUtil.getEntityName(entityClass), propsList, checkOptions(options));
    }

    /**
     * Update.
     *
     * @param entityClass the entity Class
     * @param props the props
     * @param cond the cond
     * @return the int
     */
    public int update(final Class<?> entityClass, final Map<String, Object> props, final Condition cond) {
        return update(entityClass, props, cond, null);
    }

    /**
     * Update.
     *
     * @param entityClass the entity Class
     * @param props the props
     * @param cond the cond
     * @param options the options
     * @return the int
     */
    public int update(final Class<?> entityClass, final Map<String, Object> props, final Condition cond, final Map<String, Object> options) {
        return em.update(EntityManagerUtil.getEntityName(entityClass), props, cond, checkOptions(options));
    }

    /**
     * Delete.
     *
     * @param entityClass the entity Class
     * @param cond the cond
     * @return the int
     */
    public int delete(final Class<?> entityClass, final Condition cond) {
        return delete(entityClass, cond, null);
    }

    /**
     * Delete.
     *
     * @param entityClass the entity Class
     * @param cond the cond
     * @param options the options
     * @return the int
     */
    public int delete(final Class<?> entityClass, final Condition cond, final Map<String, Object> options) {
        return em.delete(EntityManagerUtil.getEntityName(entityClass), cond, checkOptions(options));
    }

    /**
     * Gets the result by handle.
     *
     * @param resultHandle the result handle
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the result by handle
     */
    public DataSet getResultByHandle(final String resultHandle, final Collection<String> selectPropNames, final Map<String, Object> options) {
        return em.getResultByHandle(resultHandle, selectPropNames, checkOptions(options));
    }

    /**
     * Release result handle.
     *
     * @param resultHandle the result handle
     */
    public void releaseResultHandle(final String resultHandle) {
        em.releaseResultHandle(resultHandle);
    }

    /**
     * Begin transaction.
     *
     * @param isolationLevel the isolation level 
     * @return the string
     */
    public String beginTransaction(final IsolationLevel isolationLevel) {
        return beginTransaction(isolationLevel, null);
    }

    /**
     * Begin transaction.
     *
     * @param isolationLevel the isolation level
     * @param options the options
     * @return the string
     */
    public String beginTransaction(final IsolationLevel isolationLevel, final Map<String, Object> options) {
        return em.beginTransaction(isolationLevel, checkOptions(options));
    }

    /**
     * End transaction.
     *
     * @param transactionId the transaction id
     * @param transactionAction the transaction action
     */
    public void endTransaction(final String transactionId, final Action transactionAction) {
        endTransaction(transactionId, transactionAction, null);
    }

    /**
     * End transaction.
     *
     * @param transactionId the transaction id
     * @param transactionAction the transaction action
     * @param options the options
     */
    public void endTransaction(final String transactionId, final Action transactionAction, final Map<String, Object> options) {
        em.endTransaction(transactionId, transactionAction, checkOptions(options));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final long id) throws DuplicatedResultException {
        return get(entityClass, id, null);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return get(entityClass, id, selectPropNames, null);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.get(EntityManagerUtil.getEntityName(entityClass), id, selectPropNames, checkOptions(options));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final String id) throws DuplicatedResultException {
        return get(entityClass, id, null);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return get(entityClass, id, selectPropNames, null);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.get(EntityManagerUtil.getEntityName(entityClass), id, selectPropNames, checkOptions(options));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId) throws DuplicatedResultException {
        return get(entityId, null);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return get(entityId, selectPropNames, null);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.get(entityId, selectPropNames, checkOptions(options));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final long id) throws DuplicatedResultException {
        return gett(entityClass, id, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return gett(entityClass, id, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.gett(EntityManagerUtil.getEntityName(entityClass), id, selectPropNames, checkOptions(options));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final String id) throws DuplicatedResultException {
        return gett(entityClass, id, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return gett(entityClass, id, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityClass the entity Class
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.gett(EntityManagerUtil.getEntityName(entityClass), id, selectPropNames, checkOptions(options));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final EntityId entityId) throws DuplicatedResultException {
        return gett(entityId, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return gett(entityId, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
        return (T) em.gett(entityId, selectPropNames, checkOptions(options));
    }

    /**
     * Refresh.
     *
     * @param entity the entity
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final Object entity) {
        return refresh(entity, null);
    }

    /**
     * Refresh.
     *
     * @param entity the entity
     * @param options the options
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final Object entity, final Map<String, Object> options) {
        return em.refresh(entity, checkOptions(options));
    }

    /**
     * Refresh all.
     *
     * @param entities the entities
     * @return the int
     */
    @Deprecated
    public int refreshAll(final Collection<?> entities) {
        return refreshAll(entities, null);
    }

    /**
     * Refresh all.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
     */
    @Deprecated
    public int refreshAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.refreshAll(entities, checkOptions(options));
    }

    /**
     * Adds the.
     *
     * @param entity the entity
     * @return the entity id
     */
    public EntityId add(final Object entity) {
        return add(entity, null);
    }

    /**
     * Adds the.
     *
     * @param entity the entity
     * @param options the options
     * @return the entity id
     */
    public EntityId add(final Object entity, final Map<String, Object> options) {
        return em.add(entity, checkOptions(options));
    }

    /**
     * Adds the all.
     *
     * @param entities the entities
     * @return the list
     */
    public List<EntityId> addAll(final Collection<?> entities) {
        return addAll(entities, null);
    }

    /**
     * Adds the all.
     *
     * @param entities the entities
     * @param options the options
     * @return the list
     */
    public List<EntityId> addAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.addAll(entities, checkOptions(options));
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned. 
     *
     * @param <T> the generic type
     * @param entity the entity
     * @param cond the cond
     * @return the e
     */
    public <T> T addOrUpdate(final T entity, final Condition cond) {
        return addOrUpdate(entity, cond, null);
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned. 
     *
     * @param <T> the generic type
     * @param entity the entity
     * @param cond the cond
     * @param options the options
     * @return the e
     */
    public <T> T addOrUpdate(final T entity, final Condition cond, final Map<String, Object> options) {
        return (T) em.addOrUpdate(entity, cond, checkOptions(options));
    }

    /**
     * Update.
     *
     * @param entity the entity
     * @return the int
     */
    public int update(final Object entity) {
        return update(entity, null);
    }

    /**
     * Update.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
     */
    public int update(final Object entity, final Map<String, Object> options) {
        return em.update(entity, checkOptions(options));
    }

    /**
     * Update all.
     *
     * @param entities the entities
     * @return the int
     */
    public int updateAll(final Collection<?> entities) {
        return updateAll(entities, null);
    }

    /**
     * Update all.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
     */
    public int updateAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.updateAll(entities, checkOptions(options));
    }

    /**
     * Update.
     * @param props the props
     * @param entityId the entity id
     * @return the int
     */
    public int update(final Map<String, Object> props, final EntityId entityId) {
        return update(props, entityId, null);
    }

    /**
     * Update.
     * @param props the props
     * @param entityId the entity id
     * @param options the options
     * @return the int
     */
    public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
        return em.update(props, entityId, checkOptions(options));
    }

    /**
     * Update all.
     * @param props the props
     * @param entityIds the entity ids
     * @return the int
     */
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
        return updateAll(props, entityIds, null);
    }

    /**
     * Update all.
     * @param props the props
     * @param entityIds the entity ids
     * @param options the options
     * @return the int
     */
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return em.updateAll(props, entityIds, checkOptions(options));
    }

    /**
     * Delete.
     *
     * @param entity the entity
     * @return the int
     */
    public int delete(final Object entity) {
        return delete(entity, null);
    }

    /**
     * Delete.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
     */
    public int delete(final Object entity, final Map<String, Object> options) {
        return em.delete(entity, checkOptions(options));
    }

    /**
     * Delete all.
     *
     * @param entities the elements in the collection must be the same type
     * @return the int
     */
    public int deleteAll(final Collection<?> entities) {
        return deleteAll(entities, null);
    }

    /**
     * Delete all.
     *
     * @param entities the elements in the collection must be the same type
     * @param options the options
     * @return the int
     */
    public int deleteAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.deleteAll(entities, checkOptions(options));
    }

    /**
     * Delete.
     *
     * @param entityId the entity id
     * @return the int
     */
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
    public int delete(final EntityId entityId, final Map<String, Object> options) {
        return em.delete(entityId, checkOptions(options));
    }

    /**
     * Delete all.
     *
     * @param entityIds the entity ids
     * @return the int
     */
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
    public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return em.deleteAll(entityIds, checkOptions(options));
    }

    EntityId createEntityId(final Class<?> entityClass, final Object id) {
        return em.createEntityId(EntityManagerUtil.getEntityName(entityClass), id);
    }

    static Map<String, Object> checkOptions(Map<String, Object> options) {
        return options;
    }

    /**
     * Multi-thread safe.
     *
     * @param <T> the entity type
     */
    public static class Mapper<T> {

        /** The em. */
        final NewEntityManager nem;

        /** The entity class. */
        final Class<T> entityClass;

        /** The entity name. */
        final String entityName;

        /**
         * Instantiates a new mapper.
         *
         * @param nem the em
         * @param entityClass the entity class
         */
        Mapper(final NewEntityManager nem, final Class<T> entityClass) {
            this.nem = nem;
            this.entityClass = entityClass;
            this.entityName = EntityManagerUtil.getEntityName(entityClass);
        }

        /**
         * Exists.
         *
         * @param id the id
         * @return true, if successful
         */
        public boolean exists(final long id) {
            return nem.exists(entityClass, id);
        }

        /**
         * Exists.
         *
         * @param id the id
         * @return true, if successful
         */
        public boolean exists(final String id) {
            return nem.exists(entityClass, id);
        }

        /**
         * Exists.
         *
         * @param entityId the entity id
         * @return true, if successful
         */
        public boolean exists(final EntityId entityId) {
            return nem.exists(checkEntityId(entityId));
        }

        /**
         * Exists.
         *
         * @param entityId the entity id
         * @param options the options
         * @return true, if successful
         */
        public boolean exists(final EntityId entityId, final Map<String, Object> options) {
            return nem.exists(checkEntityId(entityId), options);
        }

        /**
         * Exists.
         *
         * @param cond the cond
         * @return true, if successful
         */
        public boolean exists(final Condition cond) {
            return nem.exists(entityClass, cond);
        }

        /**
         * Exists.
         *
         * @param cond the cond
         * @param options the options
         * @return true, if successful
         */
        public boolean exists(final Condition cond, final Map<String, Object> options) {
            return nem.exists(entityClass, cond, options);
        }

        /**
         * Count.
         *
         * @param cond the cond
         * @return the int
         */
        public int count(final Condition cond) {
            return nem.count(entityClass, cond);
        }

        /**
         * Count.
         *
         * @param cond the cond
         * @param options the options
         * @return the int
         */
        public int count(final Condition cond, final Map<String, Object> options) {
            return nem.count(entityClass, cond, options);
        }

        /**
         * Query for boolean.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional boolean
         */
        public OptionalBoolean queryForBoolean(final String propName, final Condition cond) {
            return nem.queryForBoolean(entityClass, propName, cond);
        }

        /**
         * Query for char.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional char
         */
        public OptionalChar queryForChar(final String propName, final Condition cond) {
            return nem.queryForChar(entityClass, propName, cond);
        }

        /**
         * Query for byte.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional byte
         */
        public OptionalByte queryForByte(final String propName, final Condition cond) {
            return nem.queryForByte(entityClass, propName, cond);
        }

        /**
         * Query for short.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional short
         */
        public OptionalShort queryForShort(final String propName, final Condition cond) {
            return nem.queryForShort(entityClass, propName, cond);
        }

        /**
         * Query for int.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional int
         */
        public OptionalInt queryForInt(final String propName, final Condition cond) {
            return nem.queryForInt(entityClass, propName, cond);
        }

        /**
         * Query for long.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional long
         */
        public OptionalLong queryForLong(final String propName, final Condition cond) {
            return nem.queryForLong(entityClass, propName, cond);
        }

        /**
         * Query for float.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional float
         */
        public OptionalFloat queryForFloat(final String propName, final Condition cond) {
            return nem.queryForFloat(entityClass, propName, cond);
        }

        /**
         * Query for double.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional double
         */
        public OptionalDouble queryForDouble(final String propName, final Condition cond) {
            return nem.queryForDouble(entityClass, propName, cond);
        }

        /**
         * Query for string.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<String> queryForString(final String propName, final Condition cond) {
            return nem.queryForString(entityClass, propName, cond);
        }

        /**
         * Query for date.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<java.sql.Date> queryForDate(final String propName, final Condition cond) {
            return nem.queryForDate(entityClass, propName, cond);
        }

        /**
         * Query for time.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<java.sql.Time> queryForTime(final String propName, final Condition cond) {
            return nem.queryForTime(entityClass, propName, cond);
        }

        /**
         * Query for timestamp.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<java.sql.Timestamp> queryForTimestamp(final String propName, final Condition cond) {
            return nem.queryForTimestamp(entityClass, propName, cond);
        }

        /**
         * Query for single result.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final String propName, final Condition cond) {
            return nem.queryForSingleResult(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for single result.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @param options the options
         * @return the nullable
         */
        @SuppressWarnings("unchecked")
        public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final String propName, final Condition cond,
                final Map<String, Object> options) {
            return nem.queryForSingleResult(targetClass, entityClass, propName, cond, options);
        }

        /**
         * Query for single non null.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @return the optional
         */
        public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final String propName, final Condition cond) {
            return nem.queryForSingleNonNull(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for single non null.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @param options the options
         * @return the optional
         */
        @SuppressWarnings("unchecked")
        public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final String propName, final Condition cond,
                final Map<String, Object> options) {
            return nem.queryForSingleNonNull(targetClass, entityClass, propName, cond, options);
        }

        /**
         * Query for unique result.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         * @throws DuplicatedResultException the duplicated result exception
         */
        public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final String propName, final Condition cond) throws DuplicatedResultException {
            return nem.queryForUniqueResult(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for unique result.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @param options the options
         * @return the nullable
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final String propName, final Condition cond, final Map<String, Object> options)
                throws DuplicatedResultException {
            return nem.queryForUniqueResult(targetClass, entityClass, propName, cond, options);
        }

        /**
         * Query for unique non null.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final String propName, final Condition cond) throws DuplicatedResultException {
            return nem.queryForUniqueNonNull(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for unique non null.
         *
         * @param <V> the value type
         * @param targetClass the target class
         * @param propName the prop name
         * @param cond the cond
         * @param options the options
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final String propName, final Condition cond, final Map<String, Object> options)
                throws DuplicatedResultException {
            return nem.queryForUniqueNonNull(targetClass, entityClass, propName, cond, options);
        }

        /**
         * Find first.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @return the optional
         */
        public Optional<T> findFirst(final Collection<String> selectPropNames, final Condition cond) {
            return nem.findFirst(entityClass, selectPropNames, cond);
        }

        /**
         * Find first.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the optional
         */
        public Optional<T> findFirst(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.findFirst(entityClass, selectPropNames, cond, options);
        }

        /**
         * Query.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @return the data set
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond) {
            return nem.query(entityClass, selectPropNames, cond);
        }

        /**
         * Query.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the data set
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.query(entityClass, selectPropNames, cond, options);
        }

        /**
         * Query.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param resultHandle the result handle
         * @param options the options
         * @return the data set
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond, final Holder<String> resultHandle,
                final Map<String, Object> options) {
            return nem.query(entityClass, selectPropNames, cond, resultHandle, options);
        }

        /**
         * Query all.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the data set
         */
        public DataSet queryAll(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.queryAll(entityClass, selectPropNames, cond, options);
        }

        /**
         * List.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @return the list
         */
        public List<T> list(final Collection<String> selectPropNames, final Condition cond) {
            return nem.list(entityClass, selectPropNames, cond);
        }

        /**
         * List.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the list
         */
        public List<T> list(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.list(entityClass, selectPropNames, cond, options);
        }

        /**
         * List all.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the list
         */
        @SuppressWarnings("unchecked")
        public List<T> listAll(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.listAll(entityClass, selectPropNames, cond, options);
        }

        /**
         * Adds the.
         *
         * @param props the props
         * @return the entity id
         */
        public EntityId add(final Map<String, Object> props) {
            return nem.add(entityClass, props);
        }

        /**
         * Adds the.
         *
         * @param props the props
         * @param options the options
         * @return the entity id
         */
        public EntityId add(final Map<String, Object> props, final Map<String, Object> options) {
            return nem.add(entityClass, props, options);
        }

        /**
         * Adds the all.
         *
         * @param propsList the props list
         * @return the list
         */
        public List<EntityId> addAll(final List<Map<String, Object>> propsList) {
            return nem.addAll(entityClass, propsList);
        }

        /**
         * Adds the all.
         *
         * @param propsList the props list
         * @param options the options
         * @return the list
         */
        public List<EntityId> addAll(final List<Map<String, Object>> propsList, final Map<String, Object> options) {
            return nem.addAll(entityClass, propsList, options);
        }

        /**
         * Update.
         *
         * @param props the props
         * @param cond the cond
         * @return the int
         */
        public int update(final Map<String, Object> props, final Condition cond) {
            return nem.update(entityClass, props, cond);
        }

        /**
         * Update.
         *
         * @param props the props
         * @param cond the cond
         * @param options the options
         * @return the int
         */
        public int update(final Map<String, Object> props, final Condition cond, final Map<String, Object> options) {
            return nem.update(entityClass, props, cond, options);
        }

        /**
         * Delete.
         *
         * @param cond the cond
         * @return the int
         */
        public int delete(final Condition cond) {
            return nem.delete(entityClass, cond);
        }

        /**
         * Delete.
         *
         * @param cond the cond
         * @param options the options
         * @return the int
         */
        public int delete(final Condition cond, final Map<String, Object> options) {
            return nem.delete(entityClass, cond, options);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final long id) throws DuplicatedResultException {
            return nem.get(entityClass, id);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.get(entityClass, id, selectPropNames);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @param options the options
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final long id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.get(entityClass, id, selectPropNames, options);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final String id) throws DuplicatedResultException {
            return nem.get(entityClass, id);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @return the optional
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final String id, final Collection<String> selectPropNames) {
            return nem.get(entityClass, id, selectPropNames);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @param options the options
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final String id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.get(entityClass, id, selectPropNames, options);
        }

        /**
         * Gets the.
         *
         * @param entityId the entity id
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final EntityId entityId) throws DuplicatedResultException {
            return nem.get(checkEntityId(entityId));
        }

        /**
         * Gets the.
         *
         * @param entityId the entity id
         * @param selectPropNames the select prop names
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.get(checkEntityId(entityId), selectPropNames);
        }

        /**
         * Gets the.
         *
         * @param entityId the entity id
         * @param selectPropNames the select prop names
         * @param options the options
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options)
                throws DuplicatedResultException {
            return nem.get(checkEntityId(entityId), selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final long id) throws DuplicatedResultException {
            return nem.gett(entityClass, id);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.gett(entityClass, id, selectPropNames);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @param options the options
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final long id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.gett(entityClass, id, selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final String id) throws DuplicatedResultException {
            return nem.gett(entityClass, id);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.gett(entityClass, id, selectPropNames);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @param options the options
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final String id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.gett(entityClass, id, selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param entityId the entity id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final EntityId entityId) throws DuplicatedResultException {
            return nem.gett(checkEntityId(entityId));
        }

        /**
         * Gets the t.
         *
         * @param entityId the entity id
         * @param selectPropNames the select prop names
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.gett(checkEntityId(entityId), selectPropNames);
        }

        /**
         * Gets the t.
         *
         * @param entityId the entity id
         * @param selectPropNames the select prop names
         * @param options the options
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.gett(checkEntityId(entityId), selectPropNames, options);
        }

        /**
         * Refresh.
         *
         * @param entity the entity
         * @return true, if successful
         */
        @Deprecated
        public boolean refresh(final T entity) {
            return nem.refresh(entity);
        }

        /**
         * Refresh.
         *
         * @param entity the entity
         * @param options the options
         * @return true, if successful
         */
        @Deprecated
        public boolean refresh(final T entity, final Map<String, Object> options) {
            return nem.refresh(entity, options);
        }

        /**
         * Refresh all.
         *
         * @param entities the entities
         * @return the int
         */
        @Deprecated
        public int refreshAll(final Collection<? extends T> entities) {
            return nem.refreshAll(entities);
        }

        /**
         * Refresh all.
         *
         * @param entities the entities
         * @param options the options
         * @return the int
         */
        @Deprecated
        public int refreshAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.refreshAll(entities, options);
        }

        /**
         * Adds the.
         *
         * @param entity the entity
         * @return the entity id
         */
        public EntityId add(final T entity) {
            return nem.add(entity);
        }

        /**
         * Adds the.
         *
         * @param entity the entity
         * @param options the options
         * @return the entity id
         */
        public EntityId add(final T entity, final Map<String, Object> options) {
            return nem.add(entity, options);
        }

        /**
         * Adds the all.
         *
         * @param entities the entities
         * @return the list
         */
        public List<EntityId> addAll(final Collection<? extends T> entities) {
            return nem.addAll(entities);
        }

        /**
         * Adds the all.
         *
         * @param entities the entities
         * @param options the options
         * @return the list
         */
        public List<EntityId> addAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.addAll(entities, options);
        }

        /**
         * Adds the or update.
         *
         * @param entity the entity
         * @param cond the cond
         * @return the e
         */
        public T addOrUpdate(final T entity, final Condition cond) {
            return nem.addOrUpdate(entity, cond);
        }

        /**
         * Adds the or update.
         *
         * @param entity the entity
         * @param cond the cond
         * @param options the options
         * @return the e
         */
        public T addOrUpdate(final T entity, final Condition cond, final Map<String, Object> options) {
            return nem.addOrUpdate(entity, cond, options);
        }

        /**
         * Update.
         *
         * @param entity the entity
         * @return the int
         */
        public int update(final T entity) {
            return nem.update(entity);
        }

        /**
         * Update.
         *
         * @param entity the entity
         * @param options the options
         * @return the int
         */
        public int update(final T entity, final Map<String, Object> options) {
            return nem.update(entity, options);
        }

        /**
         * Update all.
         *
         * @param entities the entities
         * @return the int
         */
        public int updateAll(final Collection<? extends T> entities) {
            return nem.updateAll(entities);
        }

        /**
         * Update all.
         *
         * @param entities the entities
         * @param options the options
         * @return the int
         */
        public int updateAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.updateAll(entities, options);
        }

        /**
         * Update.
         * @param props the props
         * @param entityId the entity id
         *
         * @return the int
         */
        public int update(final Map<String, Object> props, final EntityId entityId) {
            return nem.update(props, checkEntityId(entityId));
        }

        /**
         * Update.
         * @param props the props
         * @param entityId the entity id
         * @param options the options
         *
         * @return the int
         */
        public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
            return nem.update(props, checkEntityId(entityId), options);
        }

        /**
         * Update all.
         * @param props the props
         * @param entityIds the entity ids
         *
         * @return the int
         */
        public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
            return nem.updateAll(props, checkEntityIds(entityIds));
        }

        /**
         * Update all.
         * @param props the props
         * @param entityIds the entity ids
         * @param options the options
         *
         * @return the int
         */
        public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
            return nem.updateAll(props, checkEntityIds(entityIds), options);
        }

        /**
         * Delete.
         *
         * @param entity the entity
         * @return the int
         */
        public int delete(final T entity) {
            return nem.delete(entity);
        }

        /**
         * Delete.
         *
         * @param entity the entity
         * @param options the options
         * @return the int
         */
        public int delete(final T entity, final Map<String, Object> options) {
            return nem.delete(entity, options);
        }

        /**
         * Delete all.
         *
         * @param entities the entities
         * @return the int
         */
        public int deleteAll(final Collection<? extends T> entities) {
            return nem.deleteAll(entities);
        }

        /**
         * Delete all.
         *
         * @param entities the entities
         * @param options the options
         * @return the int
         */
        public int deleteAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.deleteAll(entities, options);
        }

        /**
         * Delete.
         *
         * @param entityId the entity id
         * @return the int
         */
        public int delete(final EntityId entityId) {
            return nem.delete(checkEntityId(entityId));
        }

        /**
         * Delete.
         *
         * @param entityId the entity id
         * @param options the options
         * @return the int
         */
        public int delete(final EntityId entityId, final Map<String, Object> options) {
            return nem.delete(checkEntityId(entityId), options);
        }

        /**
         * Delete all.
         *
         * @param entityIds the entity ids
         * @return the int
         */
        public int deleteAll(final List<? extends EntityId> entityIds) {
            return nem.deleteAll(checkEntityIds(entityIds));
        }

        /**
         * Delete all.
         *
         * @param entityIds the entity ids
         * @param options the options
         * @return the int
         */
        public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
            return nem.deleteAll(checkEntityIds(entityIds), options);
        }

        EntityId checkEntityId(final EntityId entityId) {
            N.checkArgument(entityName.equals(entityId.entityName()), "Incorrect entity id with entity name: {} is called in Mapper({})", entityId.entityName(),
                    entityName);

            return entityId;
        }

        <EID extends EntityId> List<EID> checkEntityIds(final List<EID> entityIds) {
            if (N.notNullOrEmpty(entityIds)) {
                for (EntityId entityId : entityIds) {
                    checkEntityId(checkEntityId(entityId));
                }
            }

            return entityIds;
        }
    }
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.DataSourceManager;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.core.SQLTransaction.CreatedBy;
import com.landawn.abacus.exception.DuplicatedResultException;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Seq;
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

    /** The dsm. */
    private final DataSourceManager _dsm;

    /** The ds. */
    private final DataSource _ds;

    /**
     * Instantiates a new entity manager ex.
     *
     * @param entityManager the entity manager
     * @param dsm the dsm
     */
    NewEntityManager(final EntityManagerEx<Object> entityManager, final DataSourceManager dsm) {
        this.em = entityManager;
        this._dsm = dsm;
        this._ds = dsm.getPrimaryDataSource();
    }

    /**
     * Mapper.
     *
     * @param <T> the generic type
     * @param <ID> the generic type
     * @param entityClass the id class
     * @param idClass the id class type of target id property. 
     * It should be {@code Void} class if there is no id property defined for the target entity, or {@code EntityId} class if there is zero or multiple id properties. 
     * @return the mapper
     */
    @SuppressWarnings("deprecation")
    public <T, ID> Mapper<T, ID> mapper(final Class<T> entityClass, final Class<ID> idClass) {
        synchronized (entityMapperPool) {
            Mapper<T, ID> mapper = entityMapperPool.get(entityClass);

            if (mapper == null) {
                mapper = new Mapper<T, ID>(this, entityClass, idClass,
                        em.getEntityDefinitionFactory().getDefinition(EntityManagerUtil.getEntityName(entityClass)));
                entityMapperPool.put(entityClass, mapper);
            } else if (!mapper.idClass.equals(idClass)) {
                throw new IllegalArgumentException(
                        "Mapper for entity \"" + mapper.entityName + "\" has already been created with different id class: " + mapper.idClass);
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
     * List all.
     *
     * @param <T> the generic type
     * @param entityClass the entity class
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the list
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
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @return the SQL transaction
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction() {
        return beginTransaction(IsolationLevel.DEFAULT);
    }

    /**
     *   
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @param isolationLevel the isolation level
     * @return the SQL transaction
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction(final IsolationLevel isolationLevel) {
        return beginTransaction(isolationLevel, false);
    }

    /**
     *  
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @param forUpdateOnly the for update only
     * @return the SQL transaction
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction(final boolean forUpdateOnly) {
        return beginTransaction(IsolationLevel.DEFAULT, forUpdateOnly);
    }

    /**
     *  
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @param isolationLevel the isolation level
     * @param forUpdateOnly the for update only
     * @return the SQL transaction
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction(IsolationLevel isolationLevel, boolean forUpdateOnly) {
        return beginTransaction(isolationLevel, forUpdateOnly, null);
    }

    /**
     *   
     * 
     * The general programming way with SQLExeucte is to execute sql scripts(generated by SQLBuilder) with array/list/map/entity by calling (batch)insert/update/delete/query/... methods.
     * if transaction is required, it can be started:
     * 
     * <pre>
     * <code>
     *   final SQLTransaction tran = entityManager.beginTransaction(IsolationLevel.READ_COMMITTED);
     *   try {
     *       // entityManager.insert(...);
     *       // entityManager.update(...);
     *       // entityManager.query(...);
     * 
     *       tran.commit();
     *   } finally {
     *       // The connection will be automatically closed after the transaction is committed or rolled back.            
     *       tran.rollbackIfNotCommitted();
     *   }
     * </code>
     * </pre>
     *
     * @param isolationLevel the isolation level
     * @param forUpdateOnly the for update only
     * @param options the options
     * @return the SQL transaction
     */
    public SQLTransaction beginTransaction(final IsolationLevel isolationLevel, final boolean forUpdateOnly, final Map<String, Object> options) {
        N.checkArgNotNull(isolationLevel, "isolationLevel");

        Map<String, Object> newOptions = options;

        if (forUpdateOnly) {
            newOptions = Options.copy(options);
            newOptions.put(Options.TRANSACTION_FOR_UPDATE_ONLY, forUpdateOnly);
        }

        final String tranId = em.beginTransaction(isolationLevel, newOptions);

        return SQLTransaction.getTransaction(tranId);
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

    /**
     * Creates the entity id.
     *
     * @param entityClass the entity class
     * @param id the id
     * @return the entity id
     */
    @SuppressWarnings("deprecation")
    private EntityId createEntityId(final Class<?> entityClass, final Object id) {
        final String entityName = EntityManagerUtil.getEntityName(entityClass);

        if (id instanceof EntityId) {
            final EntityId entityId = (EntityId) id;

            if (entityName.equals(entityId.entityName())) {
                return entityId;
            } else {
                final Seid newEntityId = Seid.of(entityName);

                for (String keyName : entityId.keySet()) {
                    newEntityId.set(keyName, entityId.get(keyName));
                }

                return newEntityId;
            }
        } else {
            return em.createEntityId(EntityManagerUtil.getEntityName(entityClass), id);
        }
    }

    /**
     * Check options.
     *
     * @param options the options
     * @return the map
     */
    private Map<String, Object> checkOptions(final Map<String, Object> options) {
        if (!EntityManagerUtil.isInTransaction(options)) {
            final String queryWithDataSource = EntityManagerUtil.getQueryWithDataSource(options);
            final DataSource ds = N.isNullOrEmpty(queryWithDataSource) ? _ds : _dsm.getActiveDataSource(queryWithDataSource);
            final SQLTransaction tran = SQLTransaction.getTransaction(ds, CreatedBy.NEW_ENTITY_MANAGER);

            if (tran != null) {
                final Map<String, Object> newOptions = Options.copy(options);
                newOptions.put(Options.TRANSACTION_ID, tran.id());

                return newOptions;
            }
        }

        return options;
    }

    /**
     * Multi-thread safe.
     *
     * @param <T> the entity type
     * @param <ID> the generic type
     */
    public static class Mapper<T, ID> {

        /** The em. */
        final NewEntityManager nem;

        /** The entity class. */
        final Class<T> entityClass;

        final Class<ID> idClass;

        final boolean isEntityId;
        final boolean isVoidId;

        /** The entity name. */
        final String entityName;

        final EntityDefinition entityDef;

        final String idPropName;

        /**
         * Instantiates a new mapper.
         *
         * @param nem the em
         * @param entityClass the entity class
         */
        Mapper(final NewEntityManager nem, final Class<T> entityClass, final Class<ID> idClass, final EntityDefinition entityDef) {
            this.nem = nem;
            this.entityClass = entityClass;
            this.idClass = idClass;
            this.entityName = EntityManagerUtil.getEntityName(entityClass);
            this.entityDef = entityDef;
            this.isEntityId = idClass.equals(EntityId.class);
            this.isVoidId = idClass.equals(Void.class);

            final List<Property> idPropList = entityDef.getIdPropertyList();

            N.checkArgNotNullOrEmpty(idPropList, "Target class: " + ClassUtil.getCanonicalClassName(entityClass)
                    + " must have at least one id property annotated by @Id or @ReadOnlyId on field or class");

            if (N.isNullOrEmpty(idPropList)) {
                if (!(idClass.equals(Void.class) || idClass.equals(EntityId.class))) {
                    throw new IllegalArgumentException("Id class only can be Void or EntityId class for entity with no id property");
                }
            } else if (idPropList.size() > 1) {
                if (!idClass.equals(EntityId.class)) {
                    throw new IllegalArgumentException("Id class only can be EntityId class for entity with two or more id properties");
                }
            }

            this.idPropName = N.isNullOrEmpty(idPropList) ? null : idPropList.get(0).getName();
        }

        /**
         * Exists.
         *
         * @param id the id
         * @return true, if successful
         */
        public boolean exists(final ID id) {
            return nem.exists(nem.createEntityId(entityClass, id));
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
        public ID add(final Map<String, Object> props) {
            return add(props, null);
        }

        /**
         * Adds the.
         *
         * @param props the props
         * @param options the options
         * @return the entity id
         */
        public ID add(final Map<String, Object> props, final Map<String, Object> options) {
            final EntityId entityId = nem.add(entityClass, props, options);

            return convertId(entityId);
        }

        /**
         * Adds the all.
         *
         * @param propsList the props list
         * @return the list
         */
        public List<ID> addAll(final List<Map<String, Object>> propsList) {
            return addAll(propsList, null);
        }

        /**
         * Adds the all.
         *
         * @param propsList the props list
         * @param options the options
         * @return the list
         */
        public List<ID> addAll(final List<Map<String, Object>> propsList, final Map<String, Object> options) {
            final List<EntityId> entityIds = nem.addAll(entityClass, propsList, options);

            return convertId(entityIds);
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
        public Optional<T> get(final ID id) throws DuplicatedResultException {
            return nem.get(nem.createEntityId(entityClass, id));
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
        public Optional<T> get(final ID id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.get(nem.createEntityId(entityClass, id), selectPropNames);
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
        public Optional<T> get(final ID id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.get(nem.createEntityId(entityClass, id), selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final ID id) throws DuplicatedResultException {
            return nem.gett(nem.createEntityId(entityClass, id));
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
        public T gett(final ID id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.gett(nem.createEntityId(entityClass, id), selectPropNames);
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
        public T gett(final ID id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.gett(nem.createEntityId(entityClass, id), selectPropNames, options);
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
        public ID add(final T entity) {
            return add(entity, null);
        }

        /**
         * Adds the.
         *
         * @param entity the entity
         * @param options the options
         * @return the entity id
         */
        public ID add(final T entity, final Map<String, Object> options) {
            final EntityId entityId = nem.add(entity, options);

            return convertId(entityId);
        }

        /**
         * Adds the all.
         *
         * @param entities the entities
         * @return the list
         */
        public List<ID> addAll(final Collection<? extends T> entities) {
            return addAll(entities, null);
        }

        /**
         * Adds the all.
         *
         * @param entities the entities
         * @param options the options
         * @return the list
         */
        public List<ID> addAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            final List<EntityId> entityIds = nem.addAll(entities, options);

            return convertId(entityIds);
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
         *
         * @param props the props
         * @param id the id
         * @return the int
         */
        public int update(final Map<String, Object> props, final ID id) {
            return nem.update(props, nem.createEntityId(entityClass, id));
        }

        /**
         * Update.
         *
         * @param props the props
         * @param id the id
         * @param options the options
         * @return the int
         */
        public int update(final Map<String, Object> props, final ID id, final Map<String, Object> options) {
            return nem.update(props, nem.createEntityId(entityClass, id), options);
        }

        /**
         * Update all.
         *
         * @param props the props
         * @param ids the ids
         * @return the int
         */
        public int updateAll(final Map<String, Object> props, final Collection<? extends ID> ids) {
            return nem.updateAll(props, createEntityIds(ids));
        }

        /**
         * Update all.
         *
         * @param props the props
         * @param ids the ids
         * @param options the options
         * @return the int
         */
        public int updateAll(final Map<String, Object> props, final Collection<? extends ID> ids, final Map<String, Object> options) {
            return nem.updateAll(props, createEntityIds(ids), options);
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
         * @param id the id
         * @return the int
         */
        public int deleteById(final ID id) {
            return nem.delete(nem.createEntityId(entityClass, id));
        }

        /**
         * Delete.
         *
         * @param id the id
         * @param options the options
         * @return the int
         */
        public int deleteById(final ID id, final Map<String, Object> options) {
            return nem.delete(nem.createEntityId(entityClass, id), options);
        }

        /**
         * Delete all.
         *
         * @param entityIds the entity ids
         * @return the int
         */
        public int deleteByIds(final Collection<? extends ID> entityIds) {
            return nem.deleteAll(createEntityIds(entityIds));
        }

        /**
         * Delete all.
         *
         * @param entityIds the entity ids
         * @param options the options
         * @return the int
         */
        public int deleteByIds(final Collection<? extends ID> entityIds, final Map<String, Object> options) {
            return nem.deleteAll(createEntityIds(entityIds), options);
        }

        /**
         * Creates the entity ids.
         *
         * @param ids the ids
         * @return the list
         */
        private List<EntityId> createEntityIds(final Collection<? extends ID> ids) {
            if (isEntityId) {
                if (ids instanceof List) {
                    return (List<EntityId>) ids;
                } else {
                    return new ArrayList<EntityId>((Collection<EntityId>) ids);
                }
            } else {
                final List<EntityId> entityIds = new ArrayList<>(ids.size());

                if (N.notNullOrEmpty(ids)) {
                    for (ID id : ids) {
                        entityIds.add(nem.createEntityId(entityClass, id));
                    }
                }

                return entityIds;
            }
        }

        private ID convertId(final EntityId entityId) {
            if (isEntityId) {
                return (ID) entityId;
            } else if (isVoidId) {
                return null;
            } else {
                return entityId.get(idClass, idPropName);
            }
        }

        private List<ID> convertId(List<EntityId> entityIds) {
            if (isEntityId) {
                return (List<ID>) entityIds;
            } else if (isVoidId) {
                return Seq.of(entityIds).map(entityId -> null);
            } else {
                return Seq.of(entityIds).map(entityId -> entityId.get(idClass, idPropName));
            }
        }
    }
}

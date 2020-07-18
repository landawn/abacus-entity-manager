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
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class NewEntityManager {

    @SuppressWarnings("rawtypes")
    private final Map<Class, Mapper> entityMapperPool = new HashMap<>();

    private final EntityManagerEx<Object> em;

    private final DataSourceManager _dsm;

    private final DataSource _ds;

    NewEntityManager(final EntityManagerEx<Object> entityManager, final DataSourceManager dsm) {
        this.em = entityManager;
        this._dsm = dsm;
        this._ds = dsm.getPrimaryDataSource();
    }

    /**
     *
     * @param <T>
     * @param <ID>
     * @param entityClass the id class
     * @param idClass the id class type of target id property.
     * It should be {@code Void} class if there is no id property defined for the target entity, or {@code EntityId} class if there is zero or multiple id properties.
     * @return
     */
    @SuppressWarnings("deprecation")
    public <T, ID> Mapper<T, ID> mapper(final Class<T> entityClass, final Class<ID> idClass) {
        synchronized (entityMapperPool) {
            Mapper<T, ID> mapper = entityMapperPool.get(entityClass);

            if (mapper == null) {
                mapper = new Mapper<>(this, entityClass, idClass, em.getEntityDefinitionFactory().getDefinition(EntityManagerUtil.getEntityName(entityClass)));
                entityMapperPool.put(entityClass, mapper);
            } else if (!mapper.idClass.equals(idClass)) {
                throw new IllegalArgumentException(
                        "Mapper for entity \"" + mapper.entityName + "\" has already been created with different id class: " + mapper.idClass);
            }

            return mapper;
        }
    }

    /**
     *
     * @param entityClass
     * @param id
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final long id) {
        return exists(createEntityId(entityClass, id));
    }

    /**
     *
     * @param entityClass
     * @param id
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final String id) {
        return exists(createEntityId(entityClass, id));
    }

    /**
     *
     * @param entityId
     * @return true, if successful
     */
    public boolean exists(final EntityId entityId) {
        return exists(entityId, null);
    }

    /**
     *
     * @param entityId
     * @param options
     * @return true, if successful
     */
    public boolean exists(final EntityId entityId, final Map<String, Object> options) {
        return em.exists(entityId, checkOptions(options));
    }

    /**
     *
     * @param entityClass
     * @param cond
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final Condition cond) {
        return exists(entityClass, cond, null);
    }

    /**
     *
     * @param entityClass
     * @param cond
     * @param options
     * @return true, if successful
     */
    public boolean exists(final Class<?> entityClass, final Condition cond, final Map<String, Object> options) {
        return em.exists(EntityManagerUtil.getEntityName(entityClass), cond, checkOptions(options));
    }

    /**
     *
     * @param entityClass
     * @param cond
     * @return
     */
    public int count(final Class<?> entityClass, final Condition cond) {
        return count(entityClass, cond, null);
    }

    /**
     *
     * @param entityClass
     * @param cond
     * @param options
     * @return
     */
    public int count(final Class<?> entityClass, final Condition cond, final Map<String, Object> options) {
        return em.count(EntityManagerUtil.getEntityName(entityClass), cond, checkOptions(options));
    }

    /**
     * Query for boolean.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalBoolean queryForBoolean(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Boolean.class, entityClass, propName, cond).mapToBoolean(ToBooleanFunction.UNBOX);
    }

    /**
     * Query for char.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalChar queryForChar(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Character.class, entityClass, propName, cond).mapToChar(ToCharFunction.UNBOX);
    }

    /**
     * Query for byte.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalByte queryForByte(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Byte.class, entityClass, propName, cond).mapToByte(ToByteFunction.UNBOX);
    }

    /**
     * Query for short.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalShort queryForShort(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Short.class, entityClass, propName, cond).mapToShort(ToShortFunction.UNBOX);
    }

    /**
     * Query for int.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalInt queryForInt(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Integer.class, entityClass, propName, cond).mapToInt(ToIntFunction.UNBOX);
    }

    /**
     * Query for long.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalLong queryForLong(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Long.class, entityClass, propName, cond).mapToLong(ToLongFunction.UNBOX);
    }

    /**
     * Query for float.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalFloat queryForFloat(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Float.class, entityClass, propName, cond).mapToFloat(ToFloatFunction.UNBOX);
    }

    /**
     * Query for double.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalDouble queryForDouble(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(Double.class, entityClass, propName, cond).mapToDouble(ToDoubleFunction.UNBOX);
    }

    /**
     * Query for string.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<String> queryForString(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(String.class, entityClass, propName, cond);
    }

    /**
     * Query for date.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Date> queryForDate(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Date.class, entityClass, propName, cond);
    }

    /**
     * Query for time.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Time> queryForTime(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Time.class, entityClass, propName, cond);
    }

    /**
     * Query for timestamp.
     *
     * @param entityClass
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Timestamp> queryForTimestamp(final Class<?> entityClass, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Timestamp.class, entityClass, propName, cond);
    }

    /**
     * Query for single result.
     *
     * @param <V> the value type
     * @param targetClass
     * @param entityClass
     * @param propName
     * @param cond
     * @return
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
     * @param targetClass set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass
     * @param propName
     * @param cond
     * @param options
     * @return
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
     * @param targetClass
     * @param entityClass
     * @param propName
     * @param cond
     * @return
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
     * @param targetClass set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass
     * @param propName
     * @param cond
     * @param options
     * @return
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
     * @param targetClass
     * @param entityClass
     * @param propName
     * @param cond
     * @return
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
     * @param targetClass set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass
     * @param propName
     * @param cond
     * @param options
     * @return
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
     * @param targetClass
     * @param entityClass
     * @param propName
     * @param cond
     * @return
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
     * @param targetClass set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityClass
     * @param propName
     * @param cond
     * @param options
     * @return
     * @throws DuplicatedResultException if more than one record found.
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final Class<?> entityClass, final String propName, final Condition cond,
            final Map<String, Object> options) throws DuplicatedResultException {
        return em.queryForUniqueNonNull(targetClass, EntityManagerUtil.getEntityName(entityClass), propName, cond, checkOptions(options));
    }

    /**
     *
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @return
     */
    public DataSet query(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond) {
        return query(entityClass, selectPropNames, cond, null);
    }

    /**
     *
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    public DataSet query(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return query(entityClass, selectPropNames, cond, null, options);
    }

    /**
     *
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @param resultHandle
     * @param options
     * @return
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
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @param options Multiple data sources can be specified by query options: <code>Query.QUERY_WITH_DATA_SOURCES</code>
     * @return
     */
    public DataSet queryAll(final Class<?> entityClass, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return em.queryAll(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, checkOptions(options));
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @return
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
     * @param <T>
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    public <T> Optional<T> findFirst(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options) {
        return em.findFirst(EntityManagerUtil.getEntityName(entityClass), selectPropNames, cond, checkOptions(options));
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @return
     */
    public <T> List<T> list(final Class<T> entityClass, final Collection<String> selectPropNames, final Condition cond) {
        return list(entityClass, selectPropNames, cond, null);
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
     *
     * @param entityClass
     * @param props
     * @return
     */
    public EntityId add(final Class<?> entityClass, final Map<String, Object> props) {
        return add(entityClass, props, null);
    }

    /**
     *
     * @param entityClass
     * @param props
     * @param options
     * @return
     */
    public EntityId add(final Class<?> entityClass, final Map<String, Object> props, final Map<String, Object> options) {
        return em.add(EntityManagerUtil.getEntityName(entityClass), props, checkOptions(options));
    }

    /**
     * Adds the all.
     *
     * @param entityClass
     * @param propsList
     * @return
     */
    public List<EntityId> addAll(final Class<?> entityClass, final List<Map<String, Object>> propsList) {
        return addAll(entityClass, propsList, null);
    }

    /**
     * Adds the all.
     *
     * @param entityClass
     * @param propsList
     * @param options
     * @return
     */
    public List<EntityId> addAll(final Class<?> entityClass, final List<Map<String, Object>> propsList, final Map<String, Object> options) {
        return em.addAll(EntityManagerUtil.getEntityName(entityClass), propsList, checkOptions(options));
    }

    /**
     *
     * @param entityClass
     * @param props
     * @param cond
     * @return
     */
    public int update(final Class<?> entityClass, final Map<String, Object> props, final Condition cond) {
        return update(entityClass, props, cond, null);
    }

    /**
     *
     * @param entityClass
     * @param props
     * @param cond
     * @param options
     * @return
     */
    public int update(final Class<?> entityClass, final Map<String, Object> props, final Condition cond, final Map<String, Object> options) {
        return em.update(EntityManagerUtil.getEntityName(entityClass), props, cond, checkOptions(options));
    }

    /**
     *
     * @param entityClass
     * @param cond
     * @return
     */
    public int delete(final Class<?> entityClass, final Condition cond) {
        return delete(entityClass, cond, null);
    }

    /**
     *
     * @param entityClass
     * @param cond
     * @param options
     * @return
     */
    public int delete(final Class<?> entityClass, final Condition cond, final Map<String, Object> options) {
        return em.delete(EntityManagerUtil.getEntityName(entityClass), cond, checkOptions(options));
    }

    /**
     * Gets the result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     * @return
     */
    public DataSet getResultByHandle(final String resultHandle, final Collection<String> selectPropNames, final Map<String, Object> options) {
        return em.getResultByHandle(resultHandle, selectPropNames, checkOptions(options));
    }

    /**
     * Release result handle.
     *
     * @param resultHandle
     */
    public void releaseResultHandle(final String resultHandle) {
        em.releaseResultHandle(resultHandle);
    }

    /**
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @return
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction() {
        return beginTransaction(IsolationLevel.DEFAULT);
    }

    /**
     *
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @param isolationLevel
     * @return
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction(final IsolationLevel isolationLevel) {
        return beginTransaction(isolationLevel, false);
    }

    /**
     *
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @param forUpdateOnly
     * @return
     * @see #beginTransaction(IsolationLevel, boolean, Map<String, Object>)
     */
    public SQLTransaction beginTransaction(final boolean forUpdateOnly) {
        return beginTransaction(IsolationLevel.DEFAULT, forUpdateOnly);
    }

    /**
     *
     * Refer to {@code beginTransaction(IsolationLevel, boolean, Map<String, Object>)}.
     *
     * @param isolationLevel
     * @param forUpdateOnly
     * @return
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
     * @param isolationLevel
     * @param forUpdateOnly
     * @param options
     * @return
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
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final long id) throws DuplicatedResultException {
        return get(entityClass, id, null);
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return get(entityClass, id, selectPropNames, null);
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.get(EntityManagerUtil.getEntityName(entityClass), id, selectPropNames, checkOptions(options));
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final String id) throws DuplicatedResultException {
        return get(entityClass, id, null);
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return get(entityClass, id, selectPropNames, null);
    }

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Class<T> entityClass, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return em.get(EntityManagerUtil.getEntityName(entityClass), id, selectPropNames, checkOptions(options));
    }

    /**
     *
     * @param <T>
     * @param entityId
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId) throws DuplicatedResultException {
        return get(entityId, null);
    }

    /**
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return get(entityId, selectPropNames, null);
    }

    /**
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
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
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final long id) throws DuplicatedResultException {
        return gett(entityClass, id, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return gett(entityClass, id, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @param options
     * @return
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
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final String id) throws DuplicatedResultException {
        return gett(entityClass, id, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final Class<T> entityClass, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return gett(entityClass, id, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @param selectPropNames
     * @param options
     * @return
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
     * @param <T>
     * @param entityId
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final EntityId entityId) throws DuplicatedResultException {
        return gett(entityId, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return gett(entityId, selectPropNames, null);
    }

    /**
     * Gets the t.
     *
     * @param <T>
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
        return (T) em.gett(entityId, selectPropNames, checkOptions(options));
    }

    /**
     *
     * @param entity
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final Object entity) {
        return refresh(entity, null);
    }

    /**
     *
     * @param entity
     * @param options
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final Object entity, final Map<String, Object> options) {
        return em.refresh(entity, checkOptions(options));
    }

    /**
     *
     * @param entities
     * @return
     */
    @Deprecated
    public int refreshAll(final Collection<?> entities) {
        return refreshAll(entities, null);
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    @Deprecated
    public int refreshAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.refreshAll(entities, checkOptions(options));
    }

    /**
     *
     * @param entity
     * @return
     */
    public EntityId add(final Object entity) {
        return add(entity, null);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    public EntityId add(final Object entity, final Map<String, Object> options) {
        return em.add(entity, checkOptions(options));
    }

    /**
     * Adds the all.
     *
     * @param entities
     * @return
     */
    public List<EntityId> addAll(final Collection<?> entities) {
        return addAll(entities, null);
    }

    /**
     * Adds the all.
     *
     * @param entities
     * @param options
     * @return
     */
    public List<EntityId> addAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.addAll(entities, checkOptions(options));
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned.
     *
     * @param <T>
     * @param entity
     * @param cond
     * @return
     */
    public <T> T addOrUpdate(final T entity, final Condition cond) {
        return addOrUpdate(entity, cond, null);
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned.
     *
     * @param <T>
     * @param entity
     * @param cond
     * @param options
     * @return
     */
    public <T> T addOrUpdate(final T entity, final Condition cond, final Map<String, Object> options) {
        return (T) em.addOrUpdate(entity, cond, checkOptions(options));
    }

    /**
     *
     * @param entity
     * @return
     */
    public int update(final Object entity) {
        return update(entity, null);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    public int update(final Object entity, final Map<String, Object> options) {
        return em.update(entity, checkOptions(options));
    }

    /**
     *
     * @param entities
     * @return
     */
    public int updateAll(final Collection<?> entities) {
        return updateAll(entities, null);
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    public int updateAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.updateAll(entities, checkOptions(options));
    }

    /**
     * Update.
     * @param props
     * @param entityId
     * @return
     */
    public int update(final Map<String, Object> props, final EntityId entityId) {
        return update(props, entityId, null);
    }

    /**
     * Update.
     * @param props
     * @param entityId
     * @param options
     * @return
     */
    public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
        return em.update(props, entityId, checkOptions(options));
    }

    /**
     * Update all.
     * @param props
     * @param entityIds
     * @return
     */
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
        return updateAll(props, entityIds, null);
    }

    /**
     * Update all.
     * @param props
     * @param entityIds
     * @param options
     * @return
     */
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return em.updateAll(props, entityIds, checkOptions(options));
    }

    /**
     *
     * @param entity
     * @return
     */
    public int delete(final Object entity) {
        return delete(entity, null);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    public int delete(final Object entity, final Map<String, Object> options) {
        return em.delete(entity, checkOptions(options));
    }

    /**
     *
     * @param entities the elements in the collection must be the same type
     * @return
     */
    public int deleteAll(final Collection<?> entities) {
        return deleteAll(entities, null);
    }

    /**
     *
     * @param entities the elements in the collection must be the same type
     * @param options
     * @return
     */
    public int deleteAll(final Collection<?> entities, final Map<String, Object> options) {
        return em.deleteAll(entities, checkOptions(options));
    }

    /**
     *
     * @param entityId
     * @return
     */
    public int delete(final EntityId entityId) {
        return delete(entityId, null);
    }

    /**
     *
     * @param entityId
     * @param options
     * @return
     */
    public int delete(final EntityId entityId, final Map<String, Object> options) {
        return em.delete(entityId, checkOptions(options));
    }

    /**
     *
     * @param entityIds
     * @return
     */
    public int deleteAll(final List<? extends EntityId> entityIds) {
        return deleteAll(entityIds, null);
    }

    /**
     *
     * @param entityIds
     * @param options
     * @return
     */
    public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return em.deleteAll(entityIds, checkOptions(options));
    }

    /**
     * Creates the entity id.
     *
     * @param entityClass
     * @param id
     * @return
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
     *
     * @param options
     * @return
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
     *
     * @param <T> the entity type
     * @param <ID>
     */
    public static class Mapper<T, ID> {

        /** The em. */
        final NewEntityManager nem;

        /** The entity class. */
        final Class<T> entityClass;

        final Class<ID> idClass;

        final boolean isEntityId;
        final boolean isNoId;

        /** The entity name. */
        final String entityName;

        final EntityDefinition entityDef;

        final String idPropName;

        /**
         * Instantiates a new mapper.
         *
         * @param nem the em
         * @param entityClass
         */
        Mapper(final NewEntityManager nem, final Class<T> entityClass, final Class<ID> idClass, final EntityDefinition entityDef) {
            final List<Property> idPropList = entityDef.getIdPropertyList();

            // Not a good idea to define Mapper<SomeEntity, Void>.
            N.checkArgNotNullOrEmpty(idPropList, "Target class: " + ClassUtil.getCanonicalClassName(entityClass)
                    + " must have at least one id property annotated by @Id or @ReadOnlyId on field or class");

            final Class<?> idReturnType = idPropList.size() == 1 ? ClassUtil.getPropGetMethod(entityClass, idPropList.get(0).getName()).getReturnType()
                    : Object.class;

            if (N.isNullOrEmpty(idPropList)) {
                if (!idClass.equals(Void.class)) {
                    throw new IllegalArgumentException("'ID' type only can be Void for entity with no id property");
                }
            } else if (idPropList.size() == 1) {
                if (!(N.wrap(idClass).isAssignableFrom(N.wrap(idReturnType)))) {
                    throw new IllegalArgumentException("The 'ID' type declared in Dao type parameters: " + idClass
                            + " is not assignable from the id property type in the entity class: " + idReturnType);
                }
            } else if (idPropList.size() > 1) {
                if (!idClass.equals(EntityId.class)) {
                    throw new IllegalArgumentException("'ID' type only can be EntityId for entity with two or more id properties");
                }
            }

            this.nem = nem;
            this.entityClass = entityClass;
            this.idClass = N.wrap(idClass).isAssignableFrom(N.wrap(idReturnType)) ? (Class<ID>) idReturnType : idClass;
            this.entityName = EntityManagerUtil.getEntityName(entityClass);
            this.entityDef = entityDef;
            this.isEntityId = idClass.equals(EntityId.class);
            this.isNoId = idClass.equals(Void.class);

            this.idPropName = N.isNullOrEmpty(idPropList) ? null : idPropList.get(0).getName();
        }

        /**
         *
         * @param id
         * @return true, if successful
         */
        public boolean exists(final ID id) {
            return nem.exists(nem.createEntityId(entityClass, id));
        }

        /**
         *
         * @param cond
         * @return true, if successful
         */
        public boolean exists(final Condition cond) {
            return nem.exists(entityClass, cond);
        }

        /**
         *
         * @param cond
         * @param options
         * @return true, if successful
         */
        public boolean exists(final Condition cond, final Map<String, Object> options) {
            return nem.exists(entityClass, cond, options);
        }

        /**
         *
         * @param cond
         * @return
         */
        public int count(final Condition cond) {
            return nem.count(entityClass, cond);
        }

        /**
         *
         * @param cond
         * @param options
         * @return
         */
        public int count(final Condition cond, final Map<String, Object> options) {
            return nem.count(entityClass, cond, options);
        }

        /**
         * Query for boolean.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalBoolean queryForBoolean(final String propName, final Condition cond) {
            return nem.queryForBoolean(entityClass, propName, cond);
        }

        /**
         * Query for char.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalChar queryForChar(final String propName, final Condition cond) {
            return nem.queryForChar(entityClass, propName, cond);
        }

        /**
         * Query for byte.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalByte queryForByte(final String propName, final Condition cond) {
            return nem.queryForByte(entityClass, propName, cond);
        }

        /**
         * Query for short.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalShort queryForShort(final String propName, final Condition cond) {
            return nem.queryForShort(entityClass, propName, cond);
        }

        /**
         * Query for int.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalInt queryForInt(final String propName, final Condition cond) {
            return nem.queryForInt(entityClass, propName, cond);
        }

        /**
         * Query for long.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalLong queryForLong(final String propName, final Condition cond) {
            return nem.queryForLong(entityClass, propName, cond);
        }

        /**
         * Query for float.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalFloat queryForFloat(final String propName, final Condition cond) {
            return nem.queryForFloat(entityClass, propName, cond);
        }

        /**
         * Query for double.
         *
         * @param propName
         * @param cond
         * @return
         */
        public OptionalDouble queryForDouble(final String propName, final Condition cond) {
            return nem.queryForDouble(entityClass, propName, cond);
        }

        /**
         * Query for string.
         *
         * @param propName
         * @param cond
         * @return
         */
        public Nullable<String> queryForString(final String propName, final Condition cond) {
            return nem.queryForString(entityClass, propName, cond);
        }

        /**
         * Query for date.
         *
         * @param propName
         * @param cond
         * @return
         */
        public Nullable<java.sql.Date> queryForDate(final String propName, final Condition cond) {
            return nem.queryForDate(entityClass, propName, cond);
        }

        /**
         * Query for time.
         *
         * @param propName
         * @param cond
         * @return
         */
        public Nullable<java.sql.Time> queryForTime(final String propName, final Condition cond) {
            return nem.queryForTime(entityClass, propName, cond);
        }

        /**
         * Query for timestamp.
         *
         * @param propName
         * @param cond
         * @return
         */
        public Nullable<java.sql.Timestamp> queryForTimestamp(final String propName, final Condition cond) {
            return nem.queryForTimestamp(entityClass, propName, cond);
        }

        /**
         * Query for single result.
         *
         * @param <V> the value type
         * @param targetClass
         * @param propName
         * @param cond
         * @return
         */
        public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final String propName, final Condition cond) {
            return nem.queryForSingleResult(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for single result.
         *
         * @param <V> the value type
         * @param targetClass
         * @param propName
         * @param cond
         * @param options
         * @return
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
         * @param targetClass
         * @param propName
         * @param cond
         * @return
         */
        public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final String propName, final Condition cond) {
            return nem.queryForSingleNonNull(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for single non null.
         *
         * @param <V> the value type
         * @param targetClass
         * @param propName
         * @param cond
         * @param options
         * @return
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
         * @param targetClass
         * @param propName
         * @param cond
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final String propName, final Condition cond) throws DuplicatedResultException {
            return nem.queryForUniqueResult(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for unique result.
         *
         * @param <V> the value type
         * @param targetClass
         * @param propName
         * @param cond
         * @param options
         * @return
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
         * @param targetClass
         * @param propName
         * @param cond
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final String propName, final Condition cond) throws DuplicatedResultException {
            return nem.queryForUniqueNonNull(targetClass, entityClass, propName, cond);
        }

        /**
         * Query for unique non null.
         *
         * @param <V> the value type
         * @param targetClass
         * @param propName
         * @param cond
         * @param options
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final String propName, final Condition cond, final Map<String, Object> options)
                throws DuplicatedResultException {
            return nem.queryForUniqueNonNull(targetClass, entityClass, propName, cond, options);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @return
         */
        public Optional<T> findFirst(final Collection<String> selectPropNames, final Condition cond) {
            return nem.findFirst(entityClass, selectPropNames, cond);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @param options
         * @return
         */
        public Optional<T> findFirst(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.findFirst(entityClass, selectPropNames, cond, options);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @return
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond) {
            return nem.query(entityClass, selectPropNames, cond);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @param options
         * @return
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.query(entityClass, selectPropNames, cond, options);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @param resultHandle
         * @param options
         * @return
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond, final Holder<String> resultHandle,
                final Map<String, Object> options) {
            return nem.query(entityClass, selectPropNames, cond, resultHandle, options);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @param options
         * @return
         */
        public DataSet queryAll(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.queryAll(entityClass, selectPropNames, cond, options);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @return
         */
        public List<T> list(final Collection<String> selectPropNames, final Condition cond) {
            return nem.list(entityClass, selectPropNames, cond);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @param options
         * @return
         */
        public List<T> list(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.list(entityClass, selectPropNames, cond, options);
        }

        /**
         *
         * @param selectPropNames
         * @param cond
         * @param options
         * @return
         */
        @SuppressWarnings("unchecked")
        public List<T> listAll(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return nem.listAll(entityClass, selectPropNames, cond, options);
        }

        /**
         *
         * @param props
         * @return
         */
        public ID add(final Map<String, Object> props) {
            return add(props, null);
        }

        /**
         *
         * @param props
         * @param options
         * @return
         */
        public ID add(final Map<String, Object> props, final Map<String, Object> options) {
            final EntityId entityId = nem.add(entityClass, props, options);

            return convertId(entityId);
        }

        /**
         * Adds the all.
         *
         * @param propsList
         * @return
         */
        public List<ID> addAll(final List<Map<String, Object>> propsList) {
            return addAll(propsList, null);
        }

        /**
         * Adds the all.
         *
         * @param propsList
         * @param options
         * @return
         */
        public List<ID> addAll(final List<Map<String, Object>> propsList, final Map<String, Object> options) {
            final List<EntityId> entityIds = nem.addAll(entityClass, propsList, options);

            return convertId(entityIds);
        }

        /**
         *
         * @param props
         * @param cond
         * @return
         */
        public int update(final Map<String, Object> props, final Condition cond) {
            return nem.update(entityClass, props, cond);
        }

        /**
         *
         * @param props
         * @param cond
         * @param options
         * @return
         */
        public int update(final Map<String, Object> props, final Condition cond, final Map<String, Object> options) {
            return nem.update(entityClass, props, cond, options);
        }

        /**
         *
         * @param cond
         * @return
         */
        public int delete(final Condition cond) {
            return nem.delete(entityClass, cond);
        }

        /**
         *
         * @param cond
         * @param options
         * @return
         */
        public int delete(final Condition cond, final Map<String, Object> options) {
            return nem.delete(entityClass, cond, options);
        }

        /**
         *
         * @param id
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final ID id) throws DuplicatedResultException {
            return nem.get(nem.createEntityId(entityClass, id));
        }

        /**
         *
         * @param id
         * @param selectPropNames
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final ID id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.get(nem.createEntityId(entityClass, id), selectPropNames);
        }

        /**
         *
         * @param id
         * @param selectPropNames
         * @param options
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<T> get(final ID id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.get(nem.createEntityId(entityClass, id), selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param id
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final ID id) throws DuplicatedResultException {
            return nem.gett(nem.createEntityId(entityClass, id));
        }

        /**
         * Gets the t.
         *
         * @param id
         * @param selectPropNames
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final ID id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return nem.gett(nem.createEntityId(entityClass, id), selectPropNames);
        }

        /**
         * Gets the t.
         *
         * @param id
         * @param selectPropNames
         * @param options
         * @return
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public T gett(final ID id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return nem.gett(nem.createEntityId(entityClass, id), selectPropNames, options);
        }

        /**
         *
         * @param entity
         * @return true, if successful
         */
        @Deprecated
        public boolean refresh(final T entity) {
            return nem.refresh(entity);
        }

        /**
         *
         * @param entity
         * @param options
         * @return true, if successful
         */
        @Deprecated
        public boolean refresh(final T entity, final Map<String, Object> options) {
            return nem.refresh(entity, options);
        }

        /**
         *
         * @param entities
         * @return
         */
        @Deprecated
        public int refreshAll(final Collection<? extends T> entities) {
            return nem.refreshAll(entities);
        }

        /**
         *
         * @param entities
         * @param options
         * @return
         */
        @Deprecated
        public int refreshAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.refreshAll(entities, options);
        }

        /**
         *
         * @param entity
         * @return
         */
        public ID add(final T entity) {
            return add(entity, null);
        }

        /**
         *
         * @param entity
         * @param options
         * @return
         */
        public ID add(final T entity, final Map<String, Object> options) {
            final EntityId entityId = nem.add(entity, options);

            return convertId(entityId);
        }

        /**
         * Adds the all.
         *
         * @param entities
         * @return
         */
        public List<ID> addAll(final Collection<? extends T> entities) {
            return addAll(entities, null);
        }

        /**
         * Adds the all.
         *
         * @param entities
         * @param options
         * @return
         */
        public List<ID> addAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            final List<EntityId> entityIds = nem.addAll(entities, options);

            return convertId(entityIds);
        }

        /**
         * Adds the or update.
         *
         * @param entity
         * @param cond
         * @return
         */
        public T addOrUpdate(final T entity, final Condition cond) {
            return nem.addOrUpdate(entity, cond);
        }

        /**
         * Adds the or update.
         *
         * @param entity
         * @param cond
         * @param options
         * @return
         */
        public T addOrUpdate(final T entity, final Condition cond, final Map<String, Object> options) {
            return nem.addOrUpdate(entity, cond, options);
        }

        /**
         *
         * @param entity
         * @return
         */
        public int update(final T entity) {
            return nem.update(entity);
        }

        /**
         *
         * @param entity
         * @param options
         * @return
         */
        public int update(final T entity, final Map<String, Object> options) {
            return nem.update(entity, options);
        }

        /**
         *
         * @param entities
         * @return
         */
        public int updateAll(final Collection<? extends T> entities) {
            return nem.updateAll(entities);
        }

        /**
         *
         * @param entities
         * @param options
         * @return
         */
        public int updateAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.updateAll(entities, options);
        }

        /**
         *
         * @param props
         * @param id
         * @return
         */
        public int update(final Map<String, Object> props, final ID id) {
            return nem.update(props, nem.createEntityId(entityClass, id));
        }

        /**
         *
         * @param props
         * @param id
         * @param options
         * @return
         */
        public int update(final Map<String, Object> props, final ID id, final Map<String, Object> options) {
            return nem.update(props, nem.createEntityId(entityClass, id), options);
        }

        /**
         *
         * @param props
         * @param ids
         * @return
         */
        public int updateAll(final Map<String, Object> props, final Collection<? extends ID> ids) {
            return nem.updateAll(props, createEntityIds(ids));
        }

        /**
         *
         * @param props
         * @param ids
         * @param options
         * @return
         */
        public int updateAll(final Map<String, Object> props, final Collection<? extends ID> ids, final Map<String, Object> options) {
            return nem.updateAll(props, createEntityIds(ids), options);
        }

        /**
         *
         * @param entity
         * @return
         */
        public int delete(final T entity) {
            return nem.delete(entity);
        }

        /**
         *
         * @param entity
         * @param options
         * @return
         */
        public int delete(final T entity, final Map<String, Object> options) {
            return nem.delete(entity, options);
        }

        /**
         *
         * @param entities
         * @return
         */
        public int deleteAll(final Collection<? extends T> entities) {
            return nem.deleteAll(entities);
        }

        /**
         *
         * @param entities
         * @param options
         * @return
         */
        public int deleteAll(final Collection<? extends T> entities, final Map<String, Object> options) {
            return nem.deleteAll(entities, options);
        }

        /**
         *
         * @param id
         * @return
         */
        public int deleteById(final ID id) {
            return nem.delete(nem.createEntityId(entityClass, id));
        }

        /**
         *
         * @param id
         * @param options
         * @return
         */
        public int deleteById(final ID id, final Map<String, Object> options) {
            return nem.delete(nem.createEntityId(entityClass, id), options);
        }

        /**
         *
         * @param entityIds
         * @return
         */
        public int deleteByIds(final Collection<? extends ID> entityIds) {
            return nem.deleteAll(createEntityIds(entityIds));
        }

        /**
         *
         * @param entityIds
         * @param options
         * @return
         */
        public int deleteByIds(final Collection<? extends ID> entityIds, final Map<String, Object> options) {
            return nem.deleteAll(createEntityIds(entityIds), options);
        }

        /**
         * Creates the entity ids.
         *
         * @param ids
         * @return
         */
        private List<EntityId> createEntityIds(final Collection<? extends ID> ids) {
            if (isEntityId) {
                if (ids instanceof List) {
                    return (List<EntityId>) ids;
                } else {
                    return new ArrayList<>((Collection<EntityId>) ids);
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
            } else if (isNoId) {
                return null;
            } else {
                return entityId.get(idClass, idPropName);
            }
        }

        private List<ID> convertId(List<EntityId> entityIds) {
            if (isEntityId) {
                return (List<ID>) entityIds;
            } else if (isNoId) {
                return N.map(entityIds, entityId -> null);
            } else {
                return N.map(entityIds, entityId -> entityId.get(idClass, idPropName));
            }
        }
    }
}

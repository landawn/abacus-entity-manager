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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.exception.DuplicatedResultException;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.AsyncExecutor;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Query;
import com.landawn.abacus.util.SQLBuilder.NSC;
import com.landawn.abacus.util.StringUtil.Strings;
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
import com.landawn.abacus.util.function.Runnable;
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
 * @param <T> the entity type
 * @since 0.8
 */
public final class EntityManagerEx<T> implements EntityManager<T> {

    static final AsyncExecutor DEFAULT_ASYNC_EXECUTOR = new AsyncExecutor(Math.max(64, Math.min(IOUtil.CPU_CORES * 8, IOUtil.MAX_MEMORY_IN_MB / 1024) * 32),
            Math.max(256, (IOUtil.MAX_MEMORY_IN_MB / 1024) * 64), 180L, TimeUnit.SECONDS);

    private final AsyncExecutor asyncExecutor;

    private final EntityManager<T> entityManager;

    private final boolean isVersionSupported;

    EntityManagerEx(final EntityManager<T> entityManager) {
        this(entityManager, DEFAULT_ASYNC_EXECUTOR);
    }

    @SuppressWarnings("deprecation")
    EntityManagerEx(final EntityManager<T> entityManager, final AsyncExecutor asyncExecutor) {
        this.entityManager = entityManager;
        this.asyncExecutor = asyncExecutor;

        boolean temp = true;

        try {
            Seid entityId = null;

            for (EntityDefinition entityDef : getEntityDefinitionFactory().getDefinitionList()) {
                if (N.notNullOrEmpty(entityDef.getIdPropertyList())) {
                    entityId = Seid.of(entityDef.getName());

                    for (Property idProp : entityDef.getIdPropertyList()) {
                        entityId.set(idProp.getName(), idProp.getType().defaultValue());
                    }

                    break;
                }
            }

            getRecordVersion(entityId, null);

            temp = true;

        } catch (UnsupportedOperationException e) {
            temp = false;
        } catch (Exception e) {
            // ignore.
        }

        this.isVersionSupported = temp;
    }

    /**
     *
     * @param entityName
     * @param id
     * @return true, if successful
     */
    public boolean exists(final String entityName, final long id) {
        return exists(createEntityId(entityName, id));
    }

    /**
     *
     * @param entityName
     * @param id
     * @return true, if successful
     */
    public boolean exists(final String entityName, final String id) {
        return exists(createEntityId(entityName, id));
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
        return gett(entityId, N.asList(entityId.keySet().iterator().next()), options) != null;
    }

    /**
     *
     * @param entityName
     * @param cond
     * @return true, if successful
     */
    public boolean exists(final String entityName, final Condition cond) {
        return exists(entityName, cond, null);
    }

    /**
     *
     * @param entityName
     * @param cond
     * @param options
     * @return true, if successful
     */
    public boolean exists(final String entityName, final Condition cond, final Map<String, Object> options) {
        Map<String, Object> newOptions = setSingleResultOption(options);

        return query(entityName, NSC._1_list, cond, newOptions).size() > 0;
    }

    /**
     *
     * @param entityName
     * @param cond
     * @return
     */
    public int count(final String entityName, final Condition cond) {
        return count(entityName, cond, null);
    }

    /**
     *
     * @param entityName
     * @param cond
     * @param options
     * @return
     */
    public int count(final String entityName, final Condition cond, final Map<String, Object> options) {
        Map<String, Object> newOptions = setSingleResultOption(options);

        return queryForSingleResult(int.class, entityName, NSC.COUNT_ALL, cond, newOptions).orElse(0);
    }

    /**
     * Sets the single result option.
     *
     * @param options
     * @return
     */
    protected Map<String, Object> setSingleResultOption(final Map<String, Object> options) {
        Map<String, Object> newOptions = EntityManagerUtil.copyOptions(options);
        newOptions.put(Options.Query.COUNT, 1);

        return newOptions;
    }

    /**
     * Sets the unique result option.
     *
     * @param options
     * @return
     */
    protected Map<String, Object> setUniqueResultOption(final Map<String, Object> options) {
        Map<String, Object> newOptions = EntityManagerUtil.copyOptions(options);
        newOptions.put(Options.Query.COUNT, 2);

        return newOptions;
    }

    /**
     * Query for boolean.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalBoolean queryForBoolean(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Boolean.class, entityName, propName, cond).mapToBoolean(ToBooleanFunction.UNBOX);
    }

    /**
     * Query for char.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalChar queryForChar(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Character.class, entityName, propName, cond).mapToChar(ToCharFunction.UNBOX);
    }

    /**
     * Query for byte.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalByte queryForByte(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Byte.class, entityName, propName, cond).mapToByte(ToByteFunction.UNBOX);
    }

    /**
     * Query for short.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalShort queryForShort(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Short.class, entityName, propName, cond).mapToShort(ToShortFunction.UNBOX);
    }

    /**
     * Query for int.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalInt queryForInt(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Integer.class, entityName, propName, cond).mapToInt(ToIntFunction.UNBOX);
    }

    /**
     * Query for long.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalLong queryForLong(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Long.class, entityName, propName, cond).mapToLong(ToLongFunction.UNBOX);
    }

    /**
     * Query for float.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalFloat queryForFloat(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Float.class, entityName, propName, cond).mapToFloat(ToFloatFunction.UNBOX);
    }

    /**
     * Query for double.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalDouble queryForDouble(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Double.class, entityName, propName, cond).mapToDouble(ToDoubleFunction.UNBOX);
    }

    /**
     * Query for string.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<String> queryForString(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(String.class, entityName, propName, cond);
    }

    /**
     * Query for date.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Date> queryForDate(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Date.class, entityName, propName, cond);
    }

    /**
     * Query for time.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Time> queryForTime(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Time.class, entityName, propName, cond);
    }

    /**
     * Query for timestamp.
     *
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Timestamp> queryForTimestamp(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Timestamp.class, entityName, propName, cond);
    }

    /**
     * Query for single result.
     *
     * @param <V> the value type
     * @param targetClass
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @see SQLExecutor#queryForSingleResult(Class, String, String, Condition, Map<String, Object>).
     */
    public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(targetClass, entityName, propName, cond, null);
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
     * @param entityName
     * @param propName
     * @param cond
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    public <V> Nullable<V> queryForSingleResult(final Class<V> targetClass, final String entityName, final String propName, final Condition cond,
            final Map<String, Object> options) {

        // For performance improvement with EntityId when entity cache is enable.
        if (cond instanceof EntityId && checkEntityName(entityName).getProperty(propName) != null) {
            final Object entity = gett((EntityId) cond, N.asList(propName), options);
            return entity == null ? Nullable.<V> empty() : Nullable.of(N.convert(ClassUtil.getPropValue(entity, propName), targetClass));
        } else {
            final Map<String, Object> newOptions = setSingleResultOption(options);
            final DataSet dataSet = this.query(entityName, N.asList(propName), cond, newOptions);

            return N.isNullOrEmpty(dataSet) ? Nullable.<V> empty() : Nullable.of(N.convert(dataSet.get(0), targetClass));
        }
    }

    /**
     * Query for single non null.
     *
     * @param <V> the value type
     * @param targetClass
     * @param entityName
     * @param propName
     * @param cond
     * @return
     */
    public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final String entityName, final String propName, final Condition cond) {
        return queryForSingleNonNull(targetClass, entityName, propName, cond, null);
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
     * @param entityName
     * @param propName
     * @param cond
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<V> queryForSingleNonNull(final Class<V> targetClass, final String entityName, final String propName, final Condition cond,
            final Map<String, Object> options) {

        // For performance improvement with EntityId when entity cache is enable.
        if (cond instanceof EntityId && checkEntityName(entityName).getProperty(propName) != null) {
            final Object entity = gett((EntityId) cond, N.asList(propName), options);
            return entity == null ? Optional.<V> empty() : Optional.of(N.convert(ClassUtil.getPropValue(entity, propName), targetClass));
        } else {
            final Map<String, Object> newOptions = setSingleResultOption(options);
            final DataSet dataSet = this.query(entityName, N.asList(propName), cond, newOptions);

            return N.isNullOrEmpty(dataSet) ? Optional.<V> empty() : Optional.of(N.convert(dataSet.get(0), targetClass));
        }
    }

    /**
     * Query for unique result.
     *
     * @param <V> the value type
     * @param targetClass
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @throws DuplicatedResultException if more than one record found.
     * @see SQLExecutor#queryForUniqueResult(Class, String, String, Condition, Map<String, Object>).
     */
    public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final String entityName, final String propName, final Condition cond)
            throws DuplicatedResultException {
        return queryForUniqueResult(targetClass, entityName, propName, cond, null);
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
     * @param entityName
     * @param propName
     * @param cond
     * @param options
     * @return
     * @throws DuplicatedResultException if more than one record found.
     */
    @SuppressWarnings("unchecked")
    public <V> Nullable<V> queryForUniqueResult(final Class<V> targetClass, final String entityName, final String propName, final Condition cond,
            final Map<String, Object> options) throws DuplicatedResultException {

        // For performance improvement with EntityId when entity cache is enable.
        if (cond instanceof EntityId && checkEntityName(entityName).getProperty(propName) != null) {
            final Object entity = gett((EntityId) cond, N.asList(propName), options);
            return entity == null ? Nullable.<V> empty() : Nullable.of(N.convert(ClassUtil.getPropValue(entity, propName), targetClass));
        } else {
            final Map<String, Object> newOptions = setUniqueResultOption(options);
            final DataSet ds = this.query(entityName, N.asList(propName), cond, newOptions);

            if (ds.isEmpty()) {
                return Nullable.empty();
            } else if (ds.size() == 1) {
                return Nullable.of(N.convert(ds.get(0), targetClass));
            } else {
                throw new DuplicatedResultException("At least two results found: " + Strings.concat(ds.get(0, 0), ", ", ds.get(1, 0)));
            }
        }
    }

    /**
     * Query for unique non null.
     *
     * @param <V> the value type
     * @param targetClass
     * @param entityName
     * @param propName
     * @param cond
     * @return
     * @throws DuplicatedResultException if more than one record found.
     */
    public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final String entityName, final String propName, final Condition cond)
            throws DuplicatedResultException {
        return queryForUniqueNonNull(targetClass, entityName, propName, cond, null);
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
     * @param entityName
     * @param propName
     * @param cond
     * @param options
     * @return
     * @throws DuplicatedResultException if more than one record found.
     */
    @SuppressWarnings("unchecked")
    public <V> Optional<V> queryForUniqueNonNull(final Class<V> targetClass, final String entityName, final String propName, final Condition cond,
            final Map<String, Object> options) throws DuplicatedResultException {

        // For performance improvement with EntityId when entity cache is enable.
        if (cond instanceof EntityId && checkEntityName(entityName).getProperty(propName) != null) {
            final Object entity = gett((EntityId) cond, N.asList(propName), options);
            return entity == null ? Optional.<V> empty() : Optional.of(N.convert(ClassUtil.getPropValue(entity, propName), targetClass));
        } else {
            final Map<String, Object> newOptions = setUniqueResultOption(options);
            final DataSet ds = this.query(entityName, N.asList(propName), cond, newOptions);

            if (ds.isEmpty()) {
                return Optional.empty();
            } else if (ds.size() == 1) {
                return Optional.of(N.convert(ds.get(0), targetClass));
            } else {
                throw new DuplicatedResultException("At least two results found: " + Strings.concat(ds.get(0, 0), ", ", ds.get(1, 0)));
            }
        }
    }

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @return
     */
    @Override
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition cond) {
        return query(entityName, selectPropNames, cond, null);
    }

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return entityManager.query(entityName, selectPropNames, cond, null, options);
    }

    /**
     *
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @param resultHandle
     * @param options
     * @return
     */
    @Override
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Holder<String> resultHandle,
            final Map<String, Object> options) {
        return entityManager.query(entityName, selectPropNames, cond, resultHandle, options);
    }

    //    /**
    //     * Query the same property list with the same condition from multiple entities(main entity and history entities).
    //     *
    //     * @param entityNameList
    //     * @param selectPropNames
    //     *            the property name should not contains any entity name.
    //     * @param cond
    //     *            the property name in the condition should not contains any entity name.
    //     * @param options
    //     * @return the merged result set.
    //     */
    //    public DataSet query(List<String> entityNameList, Collection<String> selectPropNames, Condition cond, Map<String, Object> options) {
    //        DataSet resultSet = null;
    //
    //        for (String entityName : entityNameList) {
    //            if (resultSet == null) {
    //                resultSet = entityManager.query(entityName, selectPropNames, cond, null, options);
    //            } else {
    //                resultSet.merge(entityManager.query(entityName, selectPropNames, cond, null, options));
    //            }
    //        }
    //
    //        return resultSet;
    //    }

    /**
     * Returns the merged ResultSet acquired by querying with the specified entity and its slices if it has.
     * Mostly it's designed for partition to query different partitioning tables in the specified data sources.
     *
     * By default it's queried in parallel. but it can be set to sequential query by set <code>Query.QUERY_IN_PARALLEL=false</code> in options
     *
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @param options Multiple data sources can be specified by query options: <code>Query.QUERY_WITH_DATA_SOURCES</code>
     * @return
     */
    public DataSet queryAll(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (options != null && options.containsKey(Query.OFFSET)) {
            throw new IllegalArgumentException("offset can't be set for partitioning query");
        }

        final AtomicInteger counter = new AtomicInteger(
                (options != null && options.containsKey(Query.COUNT)) ? (Integer) options.get(Query.COUNT) : Integer.MAX_VALUE);

        return queryAll(entityDef, selectPropNames, cond, options, counter);
    }

    //
    //    public Map<String, Object> queryForMap(String entityName, Collection<String> selectPropNames, Condition cond) {
    //        return queryForMap(entityName, selectPropNames, cond, null);
    //    }
    //
    //    /**
    //     * Just fetch the result in the 1st row. {@code null} is returned if no result is found. Remember to add
    //     * {@code limit} condition if big result will be returned by the query.
    //     *
    //     * @param entityName
    //     * @param selectPropNames
    //     * @param cond
    //     * @param options
    //     * @return
    //     */
    //    @SuppressWarnings("unchecked")
    //    public Map<String, Object> queryForMap(String entityName, Collection<String> selectPropNames, Condition cond, Map<String, Object> options) {
    //        Map<String, Object> newOptions = setSingleResultOption(options);
    //        DataSet dataSet = query(entityName, selectPropNames, cond, newOptions);
    //
    //        return (dataSet.size() == 0) ? null : (Map<String, Object>) dataSet.getRow(Map.class, 0);
    //    }
    /**
     *
     * @param <TT> the target entity type
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @return
     */
    //
    public <TT> Optional<TT> findFirst(final Class<TT> entityClass, final Collection<String> selectPropNames, final Condition cond) {
        return findFirst(entityClass, selectPropNames, cond, null);
    }

    /**
     * Just fetch the result in the 1st row. {@code null} is returned if no result is found. This method will try to
     * convert the column value to the type of mapping entity property if the mapping entity property is not assignable
     * from column value.
     *
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <TT> the target entity type
     * @param entityClass
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    public <TT> Optional<TT> findFirst(final Class<TT> entityClass, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options) {
        final Map<String, Object> newOptions = setSingleResultOption(options);
        final List<TT> entities = list(ClassUtil.getSimpleClassName(entityClass), selectPropNames, cond, newOptions);

        return N.isNullOrEmpty(entities) ? (Optional<TT>) Optional.empty() : Optional.of(entities.get(0));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @return
     */
    public <TT> Optional<TT> findFirst(final String entityName, final Collection<String> selectPropNames, final Condition cond) {
        return findFirst(entityName, selectPropNames, cond, null);
    }

    /**
     * Just fetch the result in the 1st row. {@code null} is returned if no result is found. This method will try to
     * convert the column value to the type of mapping entity property if the mapping entity property is not assignable
     * from column value.
     *
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    public <TT> Optional<TT> findFirst(final String entityName, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options) {
        final Map<String, Object> newOptions = setSingleResultOption(options);
        final List<TT> entities = list(entityName, selectPropNames, cond, newOptions);

        return N.isNullOrEmpty(entities) ? (Optional<TT>) Optional.empty() : Optional.of(entities.get(0));
    }

    /**
     *
     * @param entityDef
     * @param selectPropNames
     * @param cond
     * @param options
     * @param counter
     * @return
     */
    private DataSet queryAll(final EntityDefinition entityDef, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options, final AtomicInteger counter) {
        final Collection<String> dataSourceNames = N.isNullOrEmpty(options) ? null : (Collection<String>) options.get(Query.QUERY_WITH_DATA_SOURCES);
        final boolean isQueryWithMultiDataSources = N.notNullOrEmpty(dataSourceNames);
        final boolean isQueryInParallel = EntityManagerUtil.isQueryInParallel(options);
        final List<EntityDefinition> sliceEntityList = entityDef.getSliceEntityList();

        if (isQueryWithMultiDataSources == false && N.isNullOrEmpty(sliceEntityList)) {
            return query(entityDef.getName(), selectPropNames, cond, options);
        }

        final List<DataSet> resultSetList = Objectory.createList();
        final List<RuntimeException> exceptionList = Objectory.createList();
        final AtomicInteger activeThreadNum = new AtomicInteger(0);

        try {
            if (isQueryWithMultiDataSources) {
                for (String dataSourceName : dataSourceNames) {
                    final Map<String, Object> newOptions = options == null ? Options.create() : Options.copy(options);
                    newOptions.remove(Query.QUERY_WITH_DATA_SOURCES);
                    newOptions.put(Query.QUERY_WITH_DATA_SOURCE, dataSourceName);

                    final Runnable cmd = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final int count = counter.get();

                                if (count <= 0 || exceptionList.size() > 0) {
                                    return;
                                }

                                newOptions.put(Query.COUNT, count);

                                final DataSet resultSet = queryAll(entityDef, selectPropNames, cond, newOptions, counter);

                                synchronized (resultSetList) {
                                    resultSetList.add(resultSet);

                                    counter.addAndGet(-resultSet.size());
                                }
                            } catch (RuntimeException e) {
                                if (isTableNotExistsException(e)) {
                                    // ignore;
                                } else {
                                    synchronized (exceptionList) {
                                        exceptionList.add(e);
                                    }
                                }
                            } finally {
                                activeThreadNum.decrementAndGet();
                            }
                        }

                    };

                    activeThreadNum.incrementAndGet();

                    if (isQueryInParallel) {
                        asyncExecutor.execute(cmd);
                    } else {
                        cmd.run();
                    }
                }
            } else {
                final Runnable cmd = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final int count = counter.get();

                            if (count <= 0 || exceptionList.size() > 0) {
                                return;
                            }

                            final Map<String, Object> newOptions = options == null ? Options.create() : Options.copy(options);
                            newOptions.put(Query.COUNT, count);

                            final DataSet resultSet = query(entityDef.getName(), selectPropNames, cond, newOptions);

                            synchronized (resultSetList) {
                                resultSetList.add(resultSet);

                                counter.addAndGet(-resultSet.size());
                            }
                        } catch (RuntimeException e) {
                            if (isTableNotExistsException(e)) {
                                // ignore;
                            } else {
                                synchronized (exceptionList) {
                                    exceptionList.add(e);
                                }
                            }
                        } finally {
                            activeThreadNum.decrementAndGet();
                        }
                    }
                };

                activeThreadNum.incrementAndGet();

                if (isQueryInParallel) {
                    asyncExecutor.execute(cmd);
                } else {
                    cmd.run();
                }

                for (EntityDefinition e : sliceEntityList) {
                    final EntityDefinition sliceEntity = e;

                    final Runnable cmd2 = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final int count = counter.get();

                                if (count <= 0 || exceptionList.size() > 0) {
                                    return;
                                }

                                final Map<String, Object> newOptions = options == null ? Options.create() : Options.copy(options);
                                newOptions.put(Query.COUNT, count);

                                final DataSet resultSet = query(sliceEntity.getName(), selectPropNames, cond, newOptions);

                                synchronized (resultSetList) {
                                    resultSetList.add(resultSet);

                                    counter.addAndGet(-resultSet.size());
                                }
                            } catch (RuntimeException e) {
                                if (isTableNotExistsException(e)) {
                                    // ignore;
                                } else {
                                    synchronized (exceptionList) {
                                        exceptionList.add(e);
                                    }
                                }
                            } finally {
                                activeThreadNum.decrementAndGet();
                            }
                        }

                    };

                    activeThreadNum.incrementAndGet();

                    if (isQueryInParallel) {
                        asyncExecutor.execute(cmd2);
                    } else {
                        cmd2.run();
                    }
                }
            }

            while (activeThreadNum.get() > 0) {
                N.sleep(1);
            }

            if (exceptionList.size() > 0) {
                throw exceptionList.get(0);
            }

            DataSet dataSet = null;
            int count = (options != null && options.containsKey(Query.COUNT)) ? (Integer) options.get(Query.COUNT) : Integer.MAX_VALUE;

            for (int i = 0, len = resultSetList.size(); i < len; i++) {
                if (N.isNullOrEmpty(dataSet)) {
                    dataSet = resultSetList.get(i);
                } else {
                    dataSet = dataSet.merge(resultSetList.get(i));
                }

                if (dataSet.size() >= count) {
                    break;
                }
            }

            if (dataSet.size() > count) {
                dataSet = dataSet.copy(dataSet.columnNameList(), 0, count);
            }

            return dataSet;
        } finally {
            Objectory.recycle(exceptionList);
            Objectory.recycle(resultSetList);
        }
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @return
     */
    @Override
    public <TT> List<TT> list(final String entityName, final Collection<String> selectPropNames, final Condition cond) {
        return list(entityName, selectPropNames, cond, null);
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @param options
     * @return
     */
    @Override
    public <TT> List<TT> list(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return entityManager.list(entityName, selectPropNames, cond, options);
    }

    /**
     * Returns the merged entity list acquired by querying with the specified entity and its slices if it has.
     * Mostly it's designed for partition to query different partitioning tables in the specified data sources.
     *
     * By default it's queried in parallel. but it can be set to sequential query by set <code>Query.QUERY_IN_PARALLEL=false</code> in options
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param selectPropNames
     * @param cond
     * @param options Multiple data sources can be specified by query options: <code>Query.QUERY_WITH_DATA_SOURCES</code>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <TT> List<TT> listAll(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (options != null && options.containsKey(Query.OFFSET)) {
            throw new IllegalArgumentException("offset can't be set for partitioning query");
        }

        final AtomicInteger counter = new AtomicInteger(
                (options != null && options.containsKey(Query.COUNT)) ? (Integer) options.get(Query.COUNT) : Integer.MAX_VALUE);

        return listAll(entityDef, selectPropNames, cond, options, counter);
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityDef
     * @param selectPropNames
     * @param cond
     * @param options
     * @param counter
     * @return
     */
    @SuppressWarnings("unchecked")
    private <TT> List<TT> listAll(final EntityDefinition entityDef, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options, final AtomicInteger counter) {
        final Collection<String> dataSourceNames = N.isNullOrEmpty(options) ? null : (Collection<String>) options.get(Query.QUERY_WITH_DATA_SOURCES);
        final boolean isQueryWithMultiDataSources = N.notNullOrEmpty(dataSourceNames);
        final boolean isQueryInParallel = EntityManagerUtil.isQueryInParallel(options);
        final List<EntityDefinition> sliceEntityList = entityDef.getSliceEntityList();

        if (isQueryWithMultiDataSources == false && N.isNullOrEmpty(sliceEntityList)) {
            return list(entityDef.getName(), selectPropNames, cond, options);
        }

        final List<List<TT>> resultList = Objectory.createList();
        final List<RuntimeException> exceptionList = Objectory.createList();

        final AtomicInteger activeThreadNum = new AtomicInteger(0);

        try {
            if (isQueryWithMultiDataSources) {
                for (String dataSourceName : dataSourceNames) {
                    final Map<String, Object> newOptions = options == null ? Options.create() : Options.copy(options);
                    newOptions.remove(Query.QUERY_WITH_DATA_SOURCES);
                    newOptions.put(Query.QUERY_WITH_DATA_SOURCE, dataSourceName);

                    final Runnable cmd = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final int count = counter.get();

                                if (count <= 0 || exceptionList.size() > 0) {
                                    return;
                                }

                                newOptions.put(Query.COUNT, count);

                                final List<TT> entities = listAll(entityDef, selectPropNames, cond, newOptions, counter);

                                synchronized (resultList) {
                                    resultList.add(entities);

                                    counter.addAndGet(-entities.size());
                                }
                            } catch (RuntimeException e) {
                                if (isTableNotExistsException(e)) {
                                    // ignore;
                                } else {
                                    synchronized (exceptionList) {
                                        exceptionList.add(e);
                                    }
                                }
                            } finally {
                                activeThreadNum.decrementAndGet();
                            }
                        }

                    };

                    activeThreadNum.incrementAndGet();

                    if (isQueryInParallel) {
                        asyncExecutor.execute(cmd);
                    } else {
                        cmd.run();
                    }
                }
            } else {
                final Runnable cmd = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final int count = counter.get();

                            if (count <= 0 || exceptionList.size() > 0) {
                                return;
                            }

                            final Map<String, Object> newOptions = options == null ? Options.create() : Options.copy(options);
                            newOptions.put(Query.COUNT, count);

                            final List<TT> entities = list(entityDef.getName(), selectPropNames, cond, newOptions);

                            synchronized (resultList) {
                                resultList.add(entities);

                                counter.addAndGet(-entities.size());
                            }
                        } catch (RuntimeException e) {
                            if (isTableNotExistsException(e)) {
                                // ignore;
                            } else {
                                synchronized (exceptionList) {
                                    exceptionList.add(e);
                                }
                            }
                        } finally {
                            activeThreadNum.decrementAndGet();
                        }
                    }
                };

                activeThreadNum.incrementAndGet();

                if (isQueryInParallel) {
                    asyncExecutor.execute(cmd);
                } else {
                    cmd.run();
                }

                for (EntityDefinition e : sliceEntityList) {
                    final EntityDefinition sliceEntity = e;

                    final Runnable cmd2 = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final int count = counter.get();

                                if (count <= 0 || exceptionList.size() > 0) {
                                    return;
                                }

                                final Map<String, Object> newOptions = options == null ? Options.create() : Options.copy(options);
                                newOptions.put(Query.COUNT, count);

                                List<TT> entities = list(sliceEntity.getName(), selectPropNames, cond, newOptions);

                                synchronized (resultList) {
                                    resultList.add(entities);

                                    counter.addAndGet(-entities.size());
                                }
                            } catch (RuntimeException e) {
                                if (isTableNotExistsException(e)) {
                                    // ignore;
                                } else {
                                    synchronized (exceptionList) {
                                        exceptionList.add(e);
                                    }
                                }
                            } finally {
                                activeThreadNum.decrementAndGet();
                            }
                        }
                    };

                    activeThreadNum.incrementAndGet();

                    if (isQueryInParallel) {
                        asyncExecutor.execute(cmd2);
                    } else {
                        cmd2.run();
                    }
                }
            }

            while (activeThreadNum.get() > 0) {
                N.sleep(1);
            }

            if (exceptionList.size() > 0) {
                throw exceptionList.get(0);
            }

            if (resultList.size() == 1) {
                return resultList.get(0);
            }

            final List<TT> entityList = new ArrayList<>();
            int count = (options != null && options.containsKey(Query.COUNT)) ? (Integer) options.get(Query.COUNT) : Integer.MAX_VALUE;

            for (int i = 0, len = resultList.size(); i < len; i++) {
                if (N.notNullOrEmpty(resultList.get(i))) {
                    for (TT entity : resultList.get(i)) {
                        entityList.add(entity);

                        if (entityList.size() >= count) {
                            break;
                        }
                    }
                }

                if (entityList.size() >= count) {
                    break;
                }
            }

            return entityList;
        } finally {
            Objectory.recycle(exceptionList);
            Objectory.recycle(resultList);
        }
    }

    /**
     *
     * @param entityName
     * @param props
     * @return
     */
    public EntityId add(final String entityName, final Map<String, Object> props) {
        return add(entityName, props, null);
    }

    /**
     *
     * @param entityName
     * @param props
     * @param options
     * @return
     */
    @Override
    public EntityId add(final String entityName, final Map<String, Object> props, final Map<String, Object> options) {
        return entityManager.add(entityName, props, options);
    }

    /**
     * Adds the all.
     *
     * @param entityName
     * @param propsList
     * @return
     */
    public List<EntityId> addAll(final String entityName, final List<Map<String, Object>> propsList) {
        return addAll(entityName, propsList, null);
    }

    /**
     * Adds the all.
     *
     * @param entityName
     * @param propsList
     * @param options
     * @return
     */
    @Override
    public List<EntityId> addAll(final String entityName, final List<Map<String, Object>> propsList, final Map<String, Object> options) {
        return entityManager.addAll(entityName, propsList, options);
    }

    /**
     *
     * @param entityName
     * @param props
     * @param cond
     * @return
     */
    public int update(final String entityName, final Map<String, Object> props, final Condition cond) {
        return update(entityName, props, cond, null);
    }

    /**
     *
     * @param entityName
     * @param props
     * @param cond
     * @param options
     * @return
     */
    @Override
    public int update(final String entityName, final Map<String, Object> props, final Condition cond, final Map<String, Object> options) {
        return entityManager.update(entityName, props, cond, options);
    }

    /**
     *
     * @param entityName
     * @param cond
     * @return
     */
    public int delete(final String entityName, final Condition cond) {
        return delete(entityName, cond, null);
    }

    /**
     *
     * @param entityName
     * @param cond
     * @param options
     * @return
     */
    @Override
    public int delete(final String entityName, final Condition cond, final Map<String, Object> options) {
        return entityManager.delete(entityName, cond, options);
    }

    /**
     * Gets the result by handle.
     *
     * @param resultHandle
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    public DataSet getResultByHandle(final String resultHandle, final Collection<String> selectPropNames, final Map<String, Object> options) {
        return entityManager.getResultByHandle(resultHandle, selectPropNames, options);
    }

    /**
     * Release result handle.
     *
     * @param resultHandle
     */
    @Override
    public void releaseResultHandle(final String resultHandle) {
        entityManager.releaseResultHandle(resultHandle);
    }

    /**
     *
     * @param isolationLevel
     * @return
     */
    public String beginTransaction(final IsolationLevel isolationLevel) {
        return beginTransaction(isolationLevel, null);
    }

    /**
     *
     * @param isolationLevel
     * @param options
     * @return
     */
    @Override
    public String beginTransaction(final IsolationLevel isolationLevel, final Map<String, Object> options) {
        return entityManager.beginTransaction(isolationLevel, options);
    }

    /**
     *
     * @param transactionId
     * @param transactionAction
     */
    public void endTransaction(final String transactionId, final Action transactionAction) {
        endTransaction(transactionId, transactionAction, null);
    }

    /**
     *
     * @param transactionId
     * @param transactionAction
     * @param options
     */
    @Override
    public void endTransaction(final String transactionId, final Action transactionAction, final Map<String, Object> options) {
        entityManager.endTransaction(transactionId, transactionAction, options);
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final String entityName, final long id) throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(createEntityId(entityName, id)));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final String entityName, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(createEntityId(entityName, id), selectPropNames));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final String entityName, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(createEntityId(entityName, id), selectPropNames, options));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final String entityName, final String id) throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(createEntityId(entityName, id)));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final String entityName, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(createEntityId(entityName, id), selectPropNames));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final String entityName, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(createEntityId(entityName, id), selectPropNames, options));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityId
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final EntityId entityId) throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(entityId));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityId
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(entityId, selectPropNames));
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public <TT> Optional<TT> get(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return Optional.ofNullable((TT) entityManager.gett(entityId, selectPropNames, options));
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> TT gett(final String entityName, final long id) throws DuplicatedResultException {
        return (TT) entityManager.gett(createEntityId(entityName, id));
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    <TT> TT gett(final String entityName, final long id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.gett(entityName, id, selectPropNameList);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> TT gett(final String entityName, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return (TT) entityManager.gett(createEntityId(entityName, id), selectPropNames);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> TT gett(final String entityName, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return (TT) entityManager.gett(createEntityId(entityName, id), selectPropNames, options);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> TT gett(final String entityName, final String id) throws DuplicatedResultException {
        return (TT) entityManager.gett(createEntityId(entityName, id));
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    <TT> TT gett(final String entityName, final String id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.gett(entityName, id, selectPropNameList);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> TT gett(final String entityName, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return (TT) entityManager.gett(createEntityId(entityName, id), selectPropNames);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <TT> TT gett(final String entityName, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return (TT) entityManager.gett(createEntityId(entityName, id), selectPropNames, options);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityId
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public <TT> TT gett(final EntityId entityId) throws DuplicatedResultException {
        return (TT) entityManager.gett(entityId);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityId
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    <TT> TT gett(final EntityId entityId, final String... selectPropNames) throws DuplicatedResultException {
        final List<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return gett(entityId, selectPropNameList);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityId
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public <TT> TT gett(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return (TT) entityManager.gett(entityId, selectPropNames);
    }

    /**
     * Gets the t.
     *
     * @param <TT> the target entity type
     * @param entityId
     * @param selectPropNames
     * @param options
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public <TT> TT gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
        return (TT) entityManager.gett(entityId, selectPropNames, options);
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    <TT> Optional<TT> get(final String entityName, final long id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.get(entityName, id, selectPropNameList);
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityName
     * @param id
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    <TT> Optional<TT> get(final String entityName, final String id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.get(entityName, id, selectPropNameList);
    }

    /**
     *
     * @param <TT> the target entity type
     * @param entityId
     * @param selectPropNames
     * @return
     * @throws DuplicatedResultException the duplicated result exception
     */
    <TT> Optional<TT> get(final EntityId entityId, final String... selectPropNames) throws DuplicatedResultException {
        final List<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return get(entityId, selectPropNameList);
    }

    //    @Override
    //    public <T> List<T> getAll(final List<? extends EntityId> entityIds) throws DuplicatedResultException {
    //        final Collection<String> selectPropNameList = null;
    //
    //        return getAll(entityIds, selectPropNameList);
    //    }
    //
    //    <T> List<T> get(final List<? extends EntityId> entityIds, final String... selectPropNames) throws DuplicatedResultException {
    //        final List<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);
    //
    //        return getAll(entityIds, selectPropNameList);
    //    }
    //
    //    @Override
    //    public <T> List<T> getAll(final List<? extends EntityId> entityIds, final Collection<String> selectPropNames) throws DuplicatedResultException {
    //        return getAll(entityIds, selectPropNames, null);
    //    }
    //
    //    @Override
    //    public <T> List<T> getAll(final List<? extends EntityId> entityIds, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
    //        return entityManager.getAll(entityIds, selectPropNames, options);
    //    }

    /**
     *
     * @param entity
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final T entity) {
        return refresh(entity, null);
    }

    /**
     *
     * @param entity
     * @param options
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final T entity, final Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntity(entity);
        EntityId entityId = EntityManagerUtil.getEntityIdByEntity(entityDef, entity);

        if (isVersionSupported && entity instanceof DirtyMarker && (((DirtyMarker) entity).version() == getRecordVersion(entityId, options))) {
            return true;
        }

        Collection<String> selectPropNames = EntityManagerUtil.getSignedPropNames(entityDef, entity);
        Object newEntity = gett(entityId, selectPropNames, options);

        if (newEntity == null) {
            return false;
        } else {
            EntityUtil.refresh(entityDef, newEntity, entity);

            return true;
        }
    }

    /**
     *
     * @param entities
     * @return
     */
    @Deprecated
    public int refreshAll(final Collection<? extends T> entities) {
        return refreshAll(entities, null);
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    @Deprecated
    public int refreshAll(final Collection<? extends T> entities, final Map<String, Object> options) {
        if (N.isNullOrEmpty(entities)) {
            return 0;
        }

        int result = 0;
        final EntityDefinition entityDef = checkEntity(entities);
        final boolean isDirtyMarker = entities.iterator().next() instanceof DirtyMarker;
        final Map<EntityId, T> oldEntityMap = new HashMap<>();

        for (T entity : entities) {
            EntityId entityId = EntityManagerUtil.getEntityIdByEntity(entityDef, entity);

            if (isVersionSupported && isDirtyMarker && (((DirtyMarker) entity).version() == getRecordVersion(entityId, options))) {
                result += 1;
            } else {
                oldEntityMap.put(entityId, entity);
            }
        }

        if (oldEntityMap.size() > 0) {
            T entity = null;
            final Collection<String> selectPropNames = N.newHashSet();

            for (EntityId entityId : oldEntityMap.keySet()) {
                entity = oldEntityMap.get(entityId);

                if (isDirtyMarker) {
                    selectPropNames.addAll(DirtyMarkerUtil.signedPropNames((DirtyMarker) entity));
                } else {
                    selectPropNames.addAll(entityDef.getPropertyNameList());
                }
            }

            final List<?> newEntities = list(entityDef.getName(), selectPropNames, EntityManagerUtil.entityId2Condition(new ArrayList<>(oldEntityMap.keySet())),
                    options);

            for (Object newEntity : newEntities) {
                EntityId entityId = EntityManagerUtil.getEntityIdByEntity(entityDef, newEntity);
                entity = oldEntityMap.get(entityId);
                EntityUtil.refresh(entityDef, newEntity, entity);
            }

            result += newEntities.size();
        }

        return result;
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public EntityId add(final T entity) {
        return entityManager.add(entity);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    public EntityId add(final T entity, final Map<String, Object> options) {
        return entityManager.add(entity, options);
    }

    /**
     * Adds the all.
     *
     * @param entities
     * @return
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends T> entities) {
        return addAll(entities, null);
    }

    /**
     * Adds the all.
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends T> entities, final Map<String, Object> options) {
        return entityManager.addAll(entities, options);
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned.
     *
     * @param entity
     * @param cond
     * @return
     */
    public T addOrUpdate(final T entity, final Condition cond) {
        return addOrUpdate(entity, cond, null);
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned.
     *
     * @param entity
     * @param cond
     * @param options
     * @return
     */
    public T addOrUpdate(final T entity, final Condition cond, final Map<String, Object> options) {
        if (cond == null) {
            throw new IllegalArgumentException("Condition can't be null");
        }

        final EntityDefinition entityDef = checkEntity(entity);
        final String entityName = entityDef.getName();

        List<T> entities = list(entityName, null, cond, options);

        if (entities.size() > 1) {
            throw new DuplicatedResultException("Multiple entities are found by condition: " + cond);
        }

        if (N.isNullOrEmpty(entities)) {
            add(entity, options);
            return entity;
        } else {
            final T existedEntity = entities.get(0);
            EntityUtil.merge(entityDef, entity, existedEntity, false, false);
            update(existedEntity, options);
            return existedEntity;
        }
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public int update(final T entity) {
        return entityManager.update(entity);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    public int update(final T entity, final Map<String, Object> options) {
        return entityManager.update(entity, options);
    }

    /**
     *
     * @param entities
     * @return
     */
    @Override
    public int updateAll(final Collection<? extends T> entities) {
        return updateAll(entities, null);
    }

    /**
     *
     * @param entities
     * @param options
     * @return
     */
    @Override
    public int updateAll(final Collection<? extends T> entities, final Map<String, Object> options) {
        return entityManager.updateAll(entities, options);
    }

    /**
     * Update.
     * @param props
     * @param entityId
     * @return
     */
    @Override
    public int update(final Map<String, Object> props, final EntityId entityId) {
        return entityManager.update(props, entityId);
    }

    /**
     * Update.
     * @param props
     * @param entityId
     * @param options
     * @return
     */
    @Override
    public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
        return entityManager.update(props, entityId, options);
    }

    /**
     * Update all.
     * @param props
     * @param entityIds
     * @return
     */
    @Override
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
        return entityManager.updateAll(props, entityIds);
    }

    /**
     * Update all.
     * @param props
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return entityManager.updateAll(props, entityIds, options);
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public int delete(final T entity) {
        return entityManager.delete(entity);
    }

    /**
     *
     * @param entity
     * @param options
     * @return
     */
    @Override
    public int delete(final T entity, final Map<String, Object> options) {
        return entityManager.delete(entity, options);
    }

    /**
     *
     * @param entities the elements in the collection must be the same type
     * @return
     */
    @Override
    public int deleteAll(final Collection<? extends T> entities) {
        return deleteAll(entities, null);
    }

    /**
     *
     * @param entities the elements in the collection must be the same type
     * @param options
     * @return
     */
    @Override
    public int deleteAll(final Collection<? extends T> entities, final Map<String, Object> options) {
        return entityManager.deleteAll(entities, options);
    }

    /**
     *
     * @param entityId
     * @return
     */
    @Override
    public int delete(final EntityId entityId) {
        return entityManager.delete(entityId);
    }

    /**
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    public int delete(final EntityId entityId, final Map<String, Object> options) {
        return entityManager.delete(entityId, options);
    }

    /**
     *
     * @param entityIds
     * @return
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds) {
        return entityManager.deleteAll(entityIds);
    }

    /**
     *
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return entityManager.deleteAll(entityIds, options);
    }

    /**
     * Gets the record version.
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    @Deprecated
    public long getRecordVersion(final EntityId entityId, final Map<String, Object> options) {
        return entityManager.getRecordVersion(entityId, options);
    }

    /**
     *
     * @param entityId
     * @param lockMode
     * @param options
     * @return
     */
    @Override
    @Deprecated
    public String lockRecord(final EntityId entityId, final LockMode lockMode, final Map<String, Object> options) {
        return entityManager.lockRecord(entityId, lockMode, options);
    }

    /**
     *
     * @param entityId
     * @param lockCode
     * @param options
     * @return true, if successful
     */
    @Override
    @Deprecated
    public boolean unlockRecord(final EntityId entityId, final String lockCode, final Map<String, Object> options) {
        return entityManager.unlockRecord(entityId, lockCode, options);
    }

    /**
     * Gets the entity definition factory.
     *
     * @return
     */
    @Override
    @Deprecated
    @Internal
    public EntityDefinitionFactory getEntityDefinitionFactory() {
        return entityManager.getEntityDefinitionFactory();
    }

    /**
     *
     * @param <TT> the target entity type
     * @param c
     * @return
     */
    protected <TT> TT[] toArray(final Collection<? extends TT> c) {
        if (N.isNullOrEmpty(c)) {
            return (TT[]) N.EMPTY_OBJECT_ARRAY;
        }

        Class<?> componentClass = Object.class;

        for (Object e : c) {
            if (e != null) {
                componentClass = e.getClass();

                break;
            }
        }

        try {
            return c.toArray((TT[]) N.newArray(componentClass, c.size()));
        } catch (Exception e) {
            return (TT[]) c.toArray(new Object[c.size()]);
        }
    }

    /**
     *
     * @param entity
     * @return
     */
    protected EntityDefinition checkEntity(final T entity) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entity);
    }

    /**
     *
     * @param entities
     * @return
     */
    protected EntityDefinition checkEntity(final T[] entities) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entities);
    }

    /**
     *
     * @param entities
     * @return
     */
    protected EntityDefinition checkEntity(final Collection<?> entities) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entities);
    }

    /**
     * Check entity name.
     *
     * @param entityName
     * @return
     */
    protected EntityDefinition checkEntityName(final String entityName) {
        return EntityManagerUtil.checkEntityName(getEntityDefinitionFactory(), entityName);
    }

    /**
     * Creates the entity id.
     *
     * @param entityName
     * @param id
     * @return
     */
    protected EntityId createEntityId(final String entityName, final Object id) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (entityDef.getIdPropertyList().size() != 1) {
            throw new IllegalArgumentException("the size of id property is not 1. " + N.toString(entityDef.getIdPropertyList()));
        }

        final Property idProp = entityDef.getIdPropertyList().get(0);
        EntityId entityId = null;

        if (idProp.getType().clazz().isAssignableFrom(id.getClass())) {
            entityId = EntityId.of(entityName, idProp.getName(), id);
        } else {
            entityId = EntityId.of(entityName, idProp.getName(), idProp.getType().valueOf(id.toString()));
        }

        return entityId;
    }

    /**
     * Checks if is table not exists exception.
     *
     * @param e
     * @return true, if is table not exists exception
     */
    @SuppressWarnings("unused")
    static boolean isTableNotExistsException(RuntimeException e) {
        // TODO Auto-generated method stub
        return false;
    }
}

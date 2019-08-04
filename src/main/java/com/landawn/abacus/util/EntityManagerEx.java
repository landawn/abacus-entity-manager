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

package com.landawn.abacus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import com.landawn.abacus.core.DirtyMarkerUtil;
import com.landawn.abacus.core.EntityManagerUtil;
import com.landawn.abacus.core.EntityUtil;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.exception.DuplicatedResultException;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.Property;
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
 * The Class EntityManagerEx.
 *
 * @author Haiyang Li
 * @param <E> the element type
 * @since 0.8
 */
public final class EntityManagerEx<E> implements EntityManager<E> {
    
    /** The entity mapper pool. */
    @SuppressWarnings("rawtypes")
    private final Map<Class, Mapper> entityMapperPool = new HashMap<>();

    /** The async executor. */
    private final AsyncExecutor asyncExecutor;
    
    /** The entity manager. */
    private final EntityManager<E> entityManager;

    /** The is version supported. */
    private final boolean isVersionSupported;

    /**
     * Instantiates a new entity manager ex.
     *
     * @param entityManager the entity manager
     */
    public EntityManagerEx(final EntityManager<E> entityManager) {
        this(entityManager, new AsyncExecutor(Math.min(8, IOUtil.CPU_CORES), 64, 180L, TimeUnit.SECONDS));
    }

    /**
     * Instantiates a new entity manager ex.
     *
     * @param entityManager the entity manager
     * @param asyncExecutor the async executor
     */
    public EntityManagerEx(final EntityManager<E> entityManager, final AsyncExecutor asyncExecutor) {
        this.entityManager = entityManager;
        this.asyncExecutor = asyncExecutor;

        boolean temp = true;

        try {
            EntityId entityId = null;

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
     * Mapper.
     *
     * @param <T> the generic type
     * @param entityClass the entity class
     * @return the mapper
     */
    public <T> Mapper<T> mapper(Class<T> entityClass) {
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
     * @param entityName the entity name
     * @param id the id
     * @return true, if successful
     */
    public boolean exists(final String entityName, final long id) {
        return exists(createEntityId(entityName, id));
    }

    /**
     * Exists.
     *
     * @param entityName the entity name
     * @param id the id
     * @return true, if successful
     */
    public boolean exists(final String entityName, final String id) {
        return exists(createEntityId(entityName, id));
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
        return gett(entityId, N.asList(entityId.keySet().iterator().next()), options) != null;
    }

    /**
     * Exists.
     *
     * @param entityName the entity name
     * @param cond the cond
     * @return true, if successful
     */
    public boolean exists(final String entityName, final Condition cond) {
        return exists(entityName, cond, null);
    }

    /**
     * Exists.
     *
     * @param entityName the entity name
     * @param cond the cond
     * @param options the options
     * @return true, if successful
     */
    public boolean exists(final String entityName, final Condition cond, final Map<String, Object> options) {
        Map<String, Object> newOptions = setSingleResultOption(options);

        return query(entityName, NSC._1_list, cond, newOptions).size() > 0;
    }

    /**
     * Count.
     *
     * @param entityName the entity name
     * @param cond the cond
     * @return the int
     */
    public int count(final String entityName, final Condition cond) {
        return count(entityName, cond, null);
    }

    /**
     * Count.
     *
     * @param entityName the entity name
     * @param cond the cond
     * @param options the options
     * @return the int
     */
    public int count(final String entityName, final Condition cond, final Map<String, Object> options) {
        Map<String, Object> newOptions = setSingleResultOption(options);

        return queryForSingleResult(int.class, entityName, NSC.COUNT_ALL, cond, newOptions).orElse(0);
    }

    /**
     * Sets the single result option.
     *
     * @param options the options
     * @return the map
     */
    protected Map<String, Object> setSingleResultOption(final Map<String, Object> options) {
        Map<String, Object> newOptions = EntityManagerUtil.copyOptions(options);
        newOptions.put(Options.Query.COUNT, 1);

        return newOptions;
    }

    /**
     * Sets the unique result option.
     *
     * @param options the options
     * @return the map
     */
    protected Map<String, Object> setUniqueResultOption(final Map<String, Object> options) {
        Map<String, Object> newOptions = EntityManagerUtil.copyOptions(options);
        newOptions.put(Options.Query.COUNT, 2);

        return newOptions;
    }

    /**
     * Query for boolean.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional boolean
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalBoolean queryForBoolean(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Boolean.class, entityName, propName, cond).mapToBoolean(ToBooleanFunction.UNBOX);
    }

    /**
     * Query for char.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional char
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalChar queryForChar(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Character.class, entityName, propName, cond).mapToChar(ToCharFunction.UNBOX);
    }

    /**
     * Query for byte.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional byte
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalByte queryForByte(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Byte.class, entityName, propName, cond).mapToByte(ToByteFunction.UNBOX);
    }

    /**
     * Query for short.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional short
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalShort queryForShort(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Short.class, entityName, propName, cond).mapToShort(ToShortFunction.UNBOX);
    }

    /**
     * Query for int.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional int
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalInt queryForInt(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Integer.class, entityName, propName, cond).mapToInt(ToIntFunction.UNBOX);
    }

    /**
     * Query for long.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional long
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalLong queryForLong(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Long.class, entityName, propName, cond).mapToLong(ToLongFunction.UNBOX);
    }

    /**
     * Query for float.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional float
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalFloat queryForFloat(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Float.class, entityName, propName, cond).mapToFloat(ToFloatFunction.UNBOX);
    }

    /**
     * Query for double.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional double
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public OptionalDouble queryForDouble(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(Double.class, entityName, propName, cond).mapToDouble(ToDoubleFunction.UNBOX);
    }

    /**
     * Query for string.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<String> queryForString(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(String.class, entityName, propName, cond);
    }

    /**
     * Query for date.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Date> queryForDate(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Date.class, entityName, propName, cond);
    }

    /**
     * Query for time.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Time> queryForTime(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Time.class, entityName, propName, cond);
    }

    /**
     * Query for timestamp.
     *
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
     * @see SQLExecutor#queryForSingleResult(String, String, Condition).
     */
    public Nullable<java.sql.Timestamp> queryForTimestamp(final String entityName, final String propName, final Condition cond) {
        return queryForSingleResult(java.sql.Timestamp.class, entityName, propName, cond);
    }

    /**
     * Query for single result.
     *
     * @param <V> the value type
     * @param targetClass the target class
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
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
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the nullable
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
     * @param targetClass the target class
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional
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
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the optional
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
     * @param targetClass the target class
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the nullable
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
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the nullable
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
     * @param targetClass the target class
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @return the optional
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
     * @param targetClass            set result type to avoid the NullPointerException if result is null and T is primitive type
     *            "int, long. short ... char, boolean..".
     * @param entityName the entity name
     * @param propName the prop name
     * @param cond the cond
     * @param options the options
     * @return the optional
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
     * Query.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @return the data set
     */
    @Override
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition cond) {
        return query(entityName, selectPropNames, cond, null);
    }

    /**
     * Query.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the data set
     */
    public DataSet query(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return entityManager.query(entityName, selectPropNames, cond, null, options);
    }

    /**
     * Query.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param resultHandle the result handle
     * @param options the options
     * @return the data set
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
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options            Multiple data sources can be specified by query options: <code>Query.QUERY_WITH_DATA_SOURCES</code>
     * @return the merged result
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
        final Map<String, Object> newOptions = setSingleResultOption(options);
        final List<T> entities = list(ClassUtil.getSimpleClassName(entityClass), selectPropNames, cond, newOptions);

        return N.isNullOrEmpty(entities) ? (Optional<T>) Optional.empty() : Optional.of(entities.get(0));
    }

    /**
     * Find first.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @return the optional
     */
    public <T> Optional<T> findFirst(final String entityName, final Collection<String> selectPropNames, final Condition cond) {
        return findFirst(entityName, selectPropNames, cond, null);
    }

    /**
     * Just fetch the result in the 1st row. {@code null} is returned if no result is found. This method will try to
     * convert the column value to the type of mapping entity property if the mapping entity property is not assignable
     * from column value.
     * 
     * Remember to add {@code limit} condition if big result will be returned by the query.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the optional
     */
    public <T> Optional<T> findFirst(final String entityName, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options) {
        final Map<String, Object> newOptions = setSingleResultOption(options);
        final List<T> entities = list(entityName, selectPropNames, cond, newOptions);

        return N.isNullOrEmpty(entities) ? (Optional<T>) Optional.empty() : Optional.of(entities.get(0));
    }

    /**
     * Query all.
     *
     * @param entityDef the entity def
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @param counter the counter
     * @return the data set
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
                                if (JdbcUtil.isTableNotExistsException(e)) {
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
                            if (JdbcUtil.isTableNotExistsException(e)) {
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
                                if (JdbcUtil.isTableNotExistsException(e)) {
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
     * List.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @return the list
     */
    @Override
    public <T> List<T> list(final String entityName, final Collection<String> selectPropNames, final Condition cond) {
        return list(entityName, selectPropNames, cond, null);
    }

    /**
     * List.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @return the list
     */
    @Override
    public <T> List<T> list(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        return entityManager.list(entityName, selectPropNames, cond, options);
    }

    /**
     * Returns the merged entity list acquired by querying with the specified entity and its slices if it has.
     * Mostly it's designed for partition to query different partitioning tables in the specified data sources.
     * 
     * By default it's queried in parallel. but it can be set to sequential query by set <code>Query.QUERY_IN_PARALLEL=false</code> in options
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options            Multiple data sources can be specified by query options: <code>Query.QUERY_WITH_DATA_SOURCES</code>
     * @return the merged result
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> listAll(final String entityName, final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (options != null && options.containsKey(Query.OFFSET)) {
            throw new IllegalArgumentException("offset can't be set for partitioning query");
        }

        final AtomicInteger counter = new AtomicInteger(
                (options != null && options.containsKey(Query.COUNT)) ? (Integer) options.get(Query.COUNT) : Integer.MAX_VALUE);

        return listAll(entityDef, selectPropNames, cond, options, counter);
    }

    /**
     * List all.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param selectPropNames the select prop names
     * @param cond the cond
     * @param options the options
     * @param counter the counter
     * @return the list
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> listAll(final EntityDefinition entityDef, final Collection<String> selectPropNames, final Condition cond,
            final Map<String, Object> options, final AtomicInteger counter) {
        final Collection<String> dataSourceNames = N.isNullOrEmpty(options) ? null : (Collection<String>) options.get(Query.QUERY_WITH_DATA_SOURCES);
        final boolean isQueryWithMultiDataSources = N.notNullOrEmpty(dataSourceNames);
        final boolean isQueryInParallel = EntityManagerUtil.isQueryInParallel(options);
        final List<EntityDefinition> sliceEntityList = entityDef.getSliceEntityList();

        if (isQueryWithMultiDataSources == false && N.isNullOrEmpty(sliceEntityList)) {
            return list(entityDef.getName(), selectPropNames, cond, options);
        }

        final List<List<T>> resultList = Objectory.createList();
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

                                final List<T> entities = listAll(entityDef, selectPropNames, cond, newOptions, counter);

                                synchronized (resultList) {
                                    resultList.add(entities);

                                    counter.addAndGet(-entities.size());
                                }
                            } catch (RuntimeException e) {
                                if (JdbcUtil.isTableNotExistsException(e)) {
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

                            final List<T> entities = list(entityDef.getName(), selectPropNames, cond, newOptions);

                            synchronized (resultList) {
                                resultList.add(entities);

                                counter.addAndGet(-entities.size());
                            }
                        } catch (RuntimeException e) {
                            if (JdbcUtil.isTableNotExistsException(e)) {
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

                                List<T> entities = list(sliceEntity.getName(), selectPropNames, cond, newOptions);

                                synchronized (resultList) {
                                    resultList.add(entities);

                                    counter.addAndGet(-entities.size());
                                }
                            } catch (RuntimeException e) {
                                if (JdbcUtil.isTableNotExistsException(e)) {
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

            final List<T> entityList = new ArrayList<>();
            int count = (options != null && options.containsKey(Query.COUNT)) ? (Integer) options.get(Query.COUNT) : Integer.MAX_VALUE;

            for (int i = 0, len = resultList.size(); i < len; i++) {
                if (N.notNullOrEmpty(resultList.get(i))) {
                    for (T entity : resultList.get(i)) {
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
     * Adds the.
     *
     * @param entityName the entity name
     * @param props the props
     * @return the entity id
     */
    public EntityId add(final String entityName, final Map<String, Object> props) {
        return add(entityName, props, null);
    }

    /**
     * Adds the.
     *
     * @param entityName the entity name
     * @param props the props
     * @param options the options
     * @return the entity id
     */
    @Override
    public EntityId add(final String entityName, final Map<String, Object> props, final Map<String, Object> options) {
        return entityManager.add(entityName, props, options);
    }

    /**
     * Adds the all.
     *
     * @param entityName the entity name
     * @param propsList the props list
     * @return the list
     */
    public List<EntityId> addAll(final String entityName, final List<Map<String, Object>> propsList) {
        return addAll(entityName, propsList, null);
    }

    /**
     * Adds the all.
     *
     * @param entityName the entity name
     * @param propsList the props list
     * @param options the options
     * @return the list
     */
    @Override
    public List<EntityId> addAll(final String entityName, final List<Map<String, Object>> propsList, final Map<String, Object> options) {
        return entityManager.addAll(entityName, propsList, options);
    }

    /**
     * Update.
     *
     * @param entityName the entity name
     * @param props the props
     * @param cond the cond
     * @return the int
     */
    public int update(final String entityName, final Map<String, Object> props, final Condition cond) {
        return update(entityName, props, cond, null);
    }

    /**
     * Update.
     *
     * @param entityName the entity name
     * @param props the props
     * @param cond the cond
     * @param options the options
     * @return the int
     */
    @Override
    public int update(final String entityName, final Map<String, Object> props, final Condition cond, final Map<String, Object> options) {
        return entityManager.update(entityName, props, cond, options);
    }

    /**
     * Delete.
     *
     * @param entityName the entity name
     * @param cond the cond
     * @return the int
     */
    public int delete(final String entityName, final Condition cond) {
        return delete(entityName, cond, null);
    }

    /**
     * Delete.
     *
     * @param entityName the entity name
     * @param cond the cond
     * @param options the options
     * @return the int
     */
    @Override
    public int delete(final String entityName, final Condition cond, final Map<String, Object> options) {
        return entityManager.delete(entityName, cond, options);
    }

    /**
     * Gets the result by handle.
     *
     * @param resultHandle the result handle
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the result by handle
     */
    @Override
    public DataSet getResultByHandle(final String resultHandle, final Collection<String> selectPropNames, final Map<String, Object> options) {
        return entityManager.getResultByHandle(resultHandle, selectPropNames, options);
    }

    /**
     * Release result handle.
     *
     * @param resultHandle the result handle
     */
    @Override
    public void releaseResultHandle(final String resultHandle) {
        entityManager.releaseResultHandle(resultHandle);
    }

    /**
     * Begin transaction.
     *
     * @param isolationLevel the isolation level
     * @param options the options
     * @return the string
     */
    @Override
    public String beginTransaction(final IsolationLevel isolationLevel, final Map<String, Object> options) {
        return entityManager.beginTransaction(isolationLevel, options);
    }

    /**
     * End transaction.
     *
     * @param transactionId the transaction id
     * @param transactionAction the transaction action
     * @param options the options
     */
    @Override
    public void endTransaction(final String transactionId, final Action transactionAction, final Map<String, Object> options) {
        entityManager.endTransaction(transactionId, transactionAction, options);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String entityName, final long id) throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(createEntityId(entityName, id)));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String entityName, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(createEntityId(entityName, id), selectPropNames));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String entityName, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(createEntityId(entityName, id), selectPropNames, options));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String entityName, final String id) throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(createEntityId(entityName, id)));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String entityName, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(createEntityId(entityName, id), selectPropNames));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String entityName, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(createEntityId(entityName, id), selectPropNames, options));
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId) throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(entityId));
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
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(entityId, selectPropNames));
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
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return Optional.ofNullable((T) entityManager.gett(entityId, selectPropNames, options));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final String entityName, final long id) throws DuplicatedResultException {
        return (T) entityManager.gett(createEntityId(entityName, id));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    <T> T gett(final String entityName, final long id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.gett(entityName, id, selectPropNameList);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final String entityName, final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return (T) entityManager.gett(createEntityId(entityName, id), selectPropNames);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final String entityName, final long id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return (T) entityManager.gett(createEntityId(entityName, id), selectPropNames, options);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final String entityName, final String id) throws DuplicatedResultException {
        return (T) entityManager.gett(createEntityId(entityName, id));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    <T> T gett(final String entityName, final String id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.gett(entityName, id, selectPropNameList);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final String entityName, final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return (T) entityManager.gett(createEntityId(entityName, id), selectPropNames);
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the t
     * @throws DuplicatedResultException the duplicated result exception
     */
    @SuppressWarnings("unchecked")
    public <T> T gett(final String entityName, final String id, final Collection<String> selectPropNames, final Map<String, Object> options)
            throws DuplicatedResultException {
        return (T) entityManager.gett(createEntityId(entityName, id), selectPropNames, options);
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
    @Override
    public <T> T gett(final EntityId entityId) throws DuplicatedResultException {
        return (T) entityManager.gett(entityId);
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
    <T> T gett(final EntityId entityId, final String... selectPropNames) throws DuplicatedResultException {
        final List<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return gett(entityId, selectPropNameList);
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
    @Override
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
        return (T) entityManager.gett(entityId, selectPropNames);
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
    @Override
    public <T> T gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
        return (T) entityManager.gett(entityId, selectPropNames, options);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    <T> Optional<T> get(final String entityName, final long id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.get(entityName, id, selectPropNameList);
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param id the id
     * @param selectPropNames the select prop names
     * @return the optional
     * @throws DuplicatedResultException the duplicated result exception
     */
    <T> Optional<T> get(final String entityName, final String id, final String... selectPropNames) throws DuplicatedResultException {
        final Collection<String> selectPropNameList = N.isNullOrEmpty(selectPropNames) ? null : N.asList(selectPropNames);

        return this.get(entityName, id, selectPropNameList);
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
    <T> Optional<T> get(final EntityId entityId, final String... selectPropNames) throws DuplicatedResultException {
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
     * Refresh.
     *
     * @param entity the entity
     * @return true, if successful
     */
    @Deprecated
    public boolean refresh(final E entity) {
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
    public boolean refresh(final E entity, final Map<String, Object> options) {
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
     * Refresh all.
     *
     * @param entities the entities
     * @return the int
     */
    @Deprecated
    public int refreshAll(final Collection<? extends E> entities) {
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
    public int refreshAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        if (N.isNullOrEmpty(entities)) {
            return 0;
        }

        int result = 0;
        final EntityDefinition entityDef = checkEntity(entities);
        final boolean isDirtyMarker = entities.iterator().next() instanceof DirtyMarker;
        final Map<EntityId, E> oldEntityMap = new HashMap<>();

        for (E entity : entities) {
            EntityId entityId = EntityManagerUtil.getEntityIdByEntity(entityDef, entity);

            if (isVersionSupported && isDirtyMarker && (((DirtyMarker) entity).version() == getRecordVersion(entityId, options))) {
                result += 1;
            } else {
                oldEntityMap.put(entityId, entity);
            }
        }

        if (oldEntityMap.size() > 0) {
            E entity = null;
            final Collection<String> selectPropNames = new HashSet<>();

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
     * Adds the.
     *
     * @param entity the entity
     * @return the entity id
     */
    @Override
    public EntityId add(final E entity) {
        return entityManager.add(entity);
    }

    /**
     * Adds the.
     *
     * @param entity the entity
     * @param options the options
     * @return the entity id
     */
    @Override
    public EntityId add(final E entity, final Map<String, Object> options) {
        return entityManager.add(entity, options);
    }

    /**
     * Adds the all.
     *
     * @param entities the entities
     * @return the list
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends E> entities) {
        return addAll(entities, null);
    }

    /**
     * Adds the all.
     *
     * @param entities the entities
     * @param options the options
     * @return the list
     */
    @Override
    public List<EntityId> addAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        return entityManager.addAll(entities, options);
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned. 
     *
     * @param entity the entity
     * @param cond the cond
     * @return the e
     */
    public E addOrUpdate(final E entity, final Condition cond) {
        return addOrUpdate(entity, cond, null);
    }

    /**
     * Execute {@code add} and return the added entity if the record doesn't, otherwise, {@code update} is executed and updated db record is returned. 
     *
     * @param entity the entity
     * @param cond the cond
     * @param options the options
     * @return the e
     */
    public E addOrUpdate(final E entity, final Condition cond, final Map<String, Object> options) {
        if (cond == null) {
            throw new IllegalArgumentException("Condition can't be null");
        }

        final EntityDefinition entityDef = checkEntity(entity);
        final String entityName = entityDef.getName();

        List<E> entities = list(entityName, null, cond, options);

        if (entities.size() > 1) {
            throw new DuplicatedResultException("Multiple entities are found by condition: " + cond);
        }

        if (N.isNullOrEmpty(entities)) {
            add(entity, options);
            return entity;
        } else {
            final E existedEntity = entities.get(0);
            EntityUtil.merge(entityDef, entity, existedEntity, false, false);
            update(existedEntity, options);
            return existedEntity;
        }
    }

    /**
     * Update.
     *
     * @param entity the entity
     * @return the int
     */
    @Override
    public int update(final E entity) {
        return entityManager.update(entity);
    }

    /**
     * Update.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
     */
    @Override
    public int update(final E entity, final Map<String, Object> options) {
        return entityManager.update(entity, options);
    }

    /**
     * Update all.
     *
     * @param entities the entities
     * @return the int
     */
    @Override
    public int updateAll(final Collection<? extends E> entities) {
        return updateAll(entities, null);
    }

    /**
     * Update all.
     *
     * @param entities the entities
     * @param options the options
     * @return the int
     */
    @Override
    public int updateAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        return entityManager.updateAll(entities, options);
    }

    /**
     * Update.
     *
     * @param props the props
     * @param entityId the entity id
     * @return the int
     */
    @Override
    public int update(final Map<String, Object> props, final EntityId entityId) {
        return entityManager.update(props, entityId);
    }

    /**
     * Update.
     *
     * @param props the props
     * @param entityId the entity id
     * @param options the options
     * @return the int
     */
    @Override
    public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
        return entityManager.update(props, entityId, options);
    }

    /**
     * Update all.
     *
     * @param props the props
     * @param entityIds the entity ids
     * @return the int
     */
    @Override
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
        return entityManager.updateAll(props, entityIds);
    }

    /**
     * Update all.
     *
     * @param props the props
     * @param entityIds the entity ids
     * @param options the options
     * @return the int
     */
    @Override
    public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return entityManager.updateAll(props, entityIds, options);
    }

    /**
     * Delete.
     *
     * @param entity the entity
     * @return the int
     */
    @Override
    public int delete(final E entity) {
        return entityManager.delete(entity);
    }

    /**
     * Delete.
     *
     * @param entity the entity
     * @param options the options
     * @return the int
     */
    @Override
    public int delete(final E entity, final Map<String, Object> options) {
        return entityManager.delete(entity, options);
    }

    /**
     * Delete all.
     *
     * @param entities the elements in the collection must be the same type
     * @return the int
     */
    @Override
    public int deleteAll(final Collection<? extends E> entities) {
        return deleteAll(entities, null);
    }

    /**
     * Delete all.
     *
     * @param entities the elements in the collection must be the same type
     * @param options the options
     * @return the int
     */
    @Override
    public int deleteAll(final Collection<? extends E> entities, final Map<String, Object> options) {
        return entityManager.deleteAll(entities, options);
    }

    /**
     * Delete.
     *
     * @param entityId the entity id
     * @return the int
     */
    @Override
    public int delete(final EntityId entityId) {
        return entityManager.delete(entityId);
    }

    /**
     * Delete.
     *
     * @param entityId the entity id
     * @param options the options
     * @return the int
     */
    @Override
    public int delete(final EntityId entityId, final Map<String, Object> options) {
        return entityManager.delete(entityId, options);
    }

    /**
     * Delete all.
     *
     * @param entityIds the entity ids
     * @return the int
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds) {
        return entityManager.deleteAll(entityIds);
    }

    /**
     * Delete all.
     *
     * @param entityIds the entity ids
     * @param options the options
     * @return the int
     */
    @Override
    public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
        return entityManager.deleteAll(entityIds, options);
    }

    /**
     * Gets the record version.
     *
     * @param entityId the entity id
     * @param options the options
     * @return the record version
     */
    @Override
    @Deprecated
    public long getRecordVersion(final EntityId entityId, final Map<String, Object> options) {
        return entityManager.getRecordVersion(entityId, options);
    }

    /**
     * Lock record.
     *
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options the options
     * @return the string
     */
    @Override
    @Deprecated
    public String lockRecord(final EntityId entityId, final LockMode lockMode, final Map<String, Object> options) {
        return entityManager.lockRecord(entityId, lockMode, options);
    }

    /**
     * Unlock record.
     *
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options the options
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
     * @return the entity definition factory
     */
    @Override
    @Deprecated
    @Internal
    public EntityDefinitionFactory getEntityDefinitionFactory() {
        return entityManager.getEntityDefinitionFactory();
    }

    /**
     * To array.
     *
     * @param <T> the generic type
     * @param c the c
     * @return the t[]
     */
    protected <T> T[] toArray(final Collection<? extends T> c) {
        if (N.isNullOrEmpty(c)) {
            return (T[]) N.EMPTY_OBJECT_ARRAY;
        }

        Class<?> componentClass = Object.class;

        for (Object e : c) {
            if (e != null) {
                componentClass = e.getClass();

                break;
            }
        }

        try {
            return c.toArray((T[]) N.newArray(componentClass, c.size()));
        } catch (Exception e) {
            return (T[]) c.toArray(new Object[c.size()]);
        }
    }

    /**
     * Check entity.
     *
     * @param entity the entity
     * @return the entity definition
     */
    protected EntityDefinition checkEntity(final E entity) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entity);
    }

    /**
     * Check entity.
     *
     * @param entities the entities
     * @return the entity definition
     */
    protected EntityDefinition checkEntity(final E[] entities) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entities);
    }

    /**
     * Check entity.
     *
     * @param entities the entities
     * @return the entity definition
     */
    protected EntityDefinition checkEntity(final Collection<?> entities) {
        return EntityManagerUtil.checkEntity(getEntityDefinitionFactory(), entities);
    }

    /**
     * Check entity name.
     *
     * @param entityName the entity name
     * @return the entity definition
     */
    protected EntityDefinition checkEntityName(final String entityName) {
        return EntityManagerUtil.checkEntityName(getEntityDefinitionFactory(), entityName);
    }

    /**
     * Creates the entity id.
     *
     * @param entityName the entity name
     * @param id the id
     * @return the entity id
     */
    protected EntityId createEntityId(final String entityName, final Object id) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (entityDef.getIdPropertyList().size() != 1) {
            throw new IllegalArgumentException("the size of id property is not 1. " + N.toString(entityDef.getIdPropertyList()));
        }

        Property idProp = entityDef.getIdPropertyList().get(0);
        EntityId entityId = Seid.of(entityName);

        if (idProp.getType().clazz().isAssignableFrom(id.getClass())) {
            entityId.set(idProp.getName(), id);
        } else {
            entityId.set(idProp.getName(), idProp.getType().valueOf(id.toString()));
        }

        return entityId;
    }

    /**
     * The Class Mapper.
     *
     * @param <E> the element type
     */
    public static class Mapper<E> {
        
        /** The em. */
        private final EntityManagerEx<E> em;
        
        /** The entity class. */
        private final Class<E> entityClass;
        
        /** The entity name. */
        private final String entityName;

        /**
         * Instantiates a new mapper.
         *
         * @param em the em
         * @param entityClass the entity class
         */
        @SuppressWarnings("rawtypes")
        Mapper(final EntityManagerEx em, final Class<E> entityClass) {
            this.em = em;
            this.entityClass = entityClass;
            this.entityName = ClassUtil.getSimpleClassName(entityClass);
        }

        /**
         * Exists.
         *
         * @param id the id
         * @return true, if successful
         */
        public boolean exists(final long id) {
            return em.exists(entityName, id);
        }

        /**
         * Exists.
         *
         * @param id the id
         * @return true, if successful
         */
        public boolean exists(final String id) {
            return em.exists(entityName, id);
        }

        /**
         * Exists.
         *
         * @param entityId the entity id
         * @return true, if successful
         */
        public boolean exists(final EntityId entityId) {
            return em.exists(entityId);
        }

        /**
         * Exists.
         *
         * @param entityId the entity id
         * @param options the options
         * @return true, if successful
         */
        public boolean exists(final EntityId entityId, final Map<String, Object> options) {
            return em.exists(entityId, options);
        }

        /**
         * Exists.
         *
         * @param cond the cond
         * @return true, if successful
         */
        public boolean exists(final Condition cond) {
            return em.exists(entityName, cond);
        }

        /**
         * Exists.
         *
         * @param cond the cond
         * @param options the options
         * @return true, if successful
         */
        public boolean exists(final Condition cond, final Map<String, Object> options) {
            return em.exists(entityName, cond, options);
        }

        /**
         * Count.
         *
         * @param cond the cond
         * @return the int
         */
        public int count(final Condition cond) {
            return em.count(entityName, cond);
        }

        /**
         * Count.
         *
         * @param cond the cond
         * @param options the options
         * @return the int
         */
        public int count(final Condition cond, final Map<String, Object> options) {
            return em.count(entityName, cond, options);
        }

        /**
         * Query for boolean.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional boolean
         */
        public OptionalBoolean queryForBoolean(final String propName, final Condition cond) {
            return em.queryForBoolean(entityName, propName, cond);
        }

        /**
         * Query for char.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional char
         */
        public OptionalChar queryForChar(final String propName, final Condition cond) {
            return em.queryForChar(entityName, propName, cond);
        }

        /**
         * Query for byte.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional byte
         */
        public OptionalByte queryForByte(final String propName, final Condition cond) {
            return em.queryForByte(entityName, propName, cond);
        }

        /**
         * Query for short.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional short
         */
        public OptionalShort queryForShort(final String propName, final Condition cond) {
            return em.queryForShort(entityName, propName, cond);
        }

        /**
         * Query for int.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional int
         */
        public OptionalInt queryForInt(final String propName, final Condition cond) {
            return em.queryForInt(entityName, propName, cond);
        }

        /**
         * Query for long.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional long
         */
        public OptionalLong queryForLong(final String propName, final Condition cond) {
            return em.queryForLong(entityName, propName, cond);
        }

        /**
         * Query for float.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional float
         */
        public OptionalFloat queryForFloat(final String propName, final Condition cond) {
            return em.queryForFloat(entityName, propName, cond);
        }

        /**
         * Query for double.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the optional double
         */
        public OptionalDouble queryForDouble(final String propName, final Condition cond) {
            return em.queryForDouble(entityName, propName, cond);
        }

        /**
         * Query for string.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<String> queryForString(final String propName, final Condition cond) {
            return em.queryForString(entityName, propName, cond);
        }

        /**
         * Query for date.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<java.sql.Date> queryForDate(final String propName, final Condition cond) {
            return em.queryForDate(entityName, propName, cond);
        }

        /**
         * Query for time.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<java.sql.Time> queryForTime(final String propName, final Condition cond) {
            return em.queryForTime(entityName, propName, cond);
        }

        /**
         * Query for timestamp.
         *
         * @param propName the prop name
         * @param cond the cond
         * @return the nullable
         */
        public Nullable<java.sql.Timestamp> queryForTimestamp(final String propName, final Condition cond) {
            return em.queryForTimestamp(entityName, propName, cond);
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
            return em.queryForSingleResult(targetClass, entityName, propName, cond);
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
            return em.queryForSingleResult(targetClass, entityName, propName, cond, options);
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
            return em.queryForSingleNonNull(targetClass, entityName, propName, cond);
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
            return em.queryForSingleNonNull(targetClass, entityName, propName, cond, options);
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
            return em.queryForUniqueResult(targetClass, entityName, propName, cond);
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
            return em.queryForUniqueResult(targetClass, entityName, propName, cond, options);
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
            return em.queryForUniqueNonNull(targetClass, entityName, propName, cond);
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
            return em.queryForUniqueNonNull(targetClass, entityName, propName, cond, options);
        }

        /**
         * Find first.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @return the optional
         */
        public Optional<E> findFirst(final Collection<String> selectPropNames, final Condition cond) {
            return em.findFirst(entityClass, selectPropNames, cond);
        }

        /**
         * Find first.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the optional
         */
        public Optional<E> findFirst(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return em.findFirst(entityClass, selectPropNames, cond, options);
        }

        /**
         * Query.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @return the data set
         */
        public DataSet query(final Collection<String> selectPropNames, final Condition cond) {
            return em.query(entityName, selectPropNames, cond);
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
            return em.query(entityName, selectPropNames, cond, options);
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
            return em.query(entityName, selectPropNames, cond, resultHandle, options);
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
            return em.queryAll(entityName, selectPropNames, cond, options);
        }

        /**
         * List.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @return the list
         */
        public List<E> list(final Collection<String> selectPropNames, final Condition cond) {
            return em.list(entityName, selectPropNames, cond);
        }

        /**
         * List.
         *
         * @param selectPropNames the select prop names
         * @param cond the cond
         * @param options the options
         * @return the list
         */
        public List<E> list(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return em.list(entityName, selectPropNames, cond, options);
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
        public List<E> listAll(final Collection<String> selectPropNames, final Condition cond, final Map<String, Object> options) {
            return em.listAll(entityName, selectPropNames, cond, options);
        }

        /**
         * Adds the.
         *
         * @param props the props
         * @return the entity id
         */
        public EntityId add(final Map<String, Object> props) {
            return em.add(entityName, props);
        }

        /**
         * Adds the.
         *
         * @param props the props
         * @param options the options
         * @return the entity id
         */
        public EntityId add(final Map<String, Object> props, final Map<String, Object> options) {
            return em.add(entityName, props, options);
        }

        /**
         * Adds the all.
         *
         * @param propsList the props list
         * @return the list
         */
        public List<EntityId> addAll(final List<Map<String, Object>> propsList) {
            return em.addAll(entityName, propsList);
        }

        /**
         * Adds the all.
         *
         * @param propsList the props list
         * @param options the options
         * @return the list
         */
        public List<EntityId> addAll(final List<Map<String, Object>> propsList, final Map<String, Object> options) {
            return em.addAll(entityName, propsList, options);
        }

        /**
         * Update.
         *
         * @param props the props
         * @param cond the cond
         * @return the int
         */
        public int update(final Map<String, Object> props, final Condition cond) {
            return em.update(entityName, props, cond);
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
            return em.update(entityName, props, cond, options);
        }

        /**
         * Delete.
         *
         * @param cond the cond
         * @return the int
         */
        public int delete(final Condition cond) {
            return em.delete(entityName, cond);
        }

        /**
         * Delete.
         *
         * @param cond the cond
         * @param options the options
         * @return the int
         */
        public int delete(final Condition cond, final Map<String, Object> options) {
            return em.delete(entityName, cond, options);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<E> get(final long id) throws DuplicatedResultException {
            return em.get(entityName, id);
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
        public Optional<E> get(final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return em.get(entityName, id, selectPropNames);
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
        public Optional<E> get(final long id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return em.get(entityName, id, selectPropNames, options);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<E> get(final String id) throws DuplicatedResultException {
            return em.get(entityName, id);
        }

        /**
         * Gets the.
         *
         * @param id the id
         * @param selectPropNames the select prop names
         * @return the optional
         */
        @SuppressWarnings("unchecked")
        public Optional<E> get(final String id, final Collection<String> selectPropNames) {
            return em.get(entityName, id, selectPropNames);
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
        public Optional<E> get(final String id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return em.get(entityName, id, selectPropNames, options);
        }

        /**
         * Gets the.
         *
         * @param entityId the entity id
         * @return the optional
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public Optional<E> get(final EntityId entityId) throws DuplicatedResultException {
            return em.get(entityId);
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
        public Optional<E> get(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return em.get(entityId, selectPropNames);
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
        public Optional<E> get(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options)
                throws DuplicatedResultException {
            return em.get(entityId, selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public E gett(final long id) throws DuplicatedResultException {
            return em.gett(entityName, id);
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
        public E gett(final long id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return em.gett(entityName, id, selectPropNames);
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
        public E gett(final long id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return em.gett(entityName, id, selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param id the id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public E gett(final String id) throws DuplicatedResultException {
            return em.gett(entityName, id);
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
        public E gett(final String id, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return em.gett(entityName, id, selectPropNames);
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
        public E gett(final String id, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return em.gett(entityName, id, selectPropNames, options);
        }

        /**
         * Gets the t.
         *
         * @param entityId the entity id
         * @return the t
         * @throws DuplicatedResultException the duplicated result exception
         */
        @SuppressWarnings("unchecked")
        public E gett(final EntityId entityId) throws DuplicatedResultException {
            return em.gett(entityId);
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
        public E gett(final EntityId entityId, final Collection<String> selectPropNames) throws DuplicatedResultException {
            return em.gett(entityId, selectPropNames);
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
        public E gett(final EntityId entityId, final Collection<String> selectPropNames, final Map<String, Object> options) throws DuplicatedResultException {
            return em.gett(entityId, selectPropNames, options);
        }

        /**
         * Refresh.
         *
         * @param entity the entity
         * @return true, if successful
         */
        public boolean refresh(final E entity) {
            return em.refresh(entity);
        }

        /**
         * Refresh.
         *
         * @param entity the entity
         * @param options the options
         * @return true, if successful
         */
        public boolean refresh(final E entity, final Map<String, Object> options) {
            return em.refresh(entity, options);
        }

        /**
         * Refresh all.
         *
         * @param entities the entities
         * @return the int
         */
        public int refreshAll(final Collection<? extends E> entities) {
            return em.refreshAll(entities);
        }

        /**
         * Refresh all.
         *
         * @param entities the entities
         * @param options the options
         * @return the int
         */
        public int refreshAll(final Collection<? extends E> entities, final Map<String, Object> options) {
            return em.refreshAll(entities, options);
        }

        /**
         * Adds the.
         *
         * @param entity the entity
         * @return the entity id
         */
        public EntityId add(final E entity) {
            return em.add(entity);
        }

        /**
         * Adds the.
         *
         * @param entity the entity
         * @param options the options
         * @return the entity id
         */
        public EntityId add(final E entity, final Map<String, Object> options) {
            return em.add(entity, options);
        }

        /**
         * Adds the all.
         *
         * @param entities the entities
         * @return the list
         */
        public List<EntityId> addAll(final Collection<? extends E> entities) {
            return em.addAll(entities);
        }

        /**
         * Adds the all.
         *
         * @param entities the entities
         * @param options the options
         * @return the list
         */
        public List<EntityId> addAll(final Collection<? extends E> entities, final Map<String, Object> options) {
            return em.addAll(entities, options);
        }

        /**
         * Adds the or update.
         *
         * @param entity the entity
         * @param cond the cond
         * @return the e
         */
        public E addOrUpdate(final E entity, final Condition cond) {
            return em.addOrUpdate(entity, cond);
        }

        /**
         * Adds the or update.
         *
         * @param entity the entity
         * @param cond the cond
         * @param options the options
         * @return the e
         */
        public E addOrUpdate(final E entity, final Condition cond, final Map<String, Object> options) {
            return em.addOrUpdate(entity, cond, options);
        }

        /**
         * Update.
         *
         * @param entity the entity
         * @return the int
         */
        public int update(final E entity) {
            return em.update(entity);
        }

        /**
         * Update.
         *
         * @param entity the entity
         * @param options the options
         * @return the int
         */
        public int update(final E entity, final Map<String, Object> options) {
            return em.update(entity, options);
        }

        /**
         * Update all.
         *
         * @param entities the entities
         * @return the int
         */
        public int updateAll(final Collection<? extends E> entities) {
            return em.updateAll(entities);
        }

        /**
         * Update all.
         *
         * @param entities the entities
         * @param options the options
         * @return the int
         */
        public int updateAll(final Collection<? extends E> entities, final Map<String, Object> options) {
            return em.updateAll(entities, options);
        }

        /**
         * Update.
         *
         * @param props the props
         * @param entityId the entity id
         * @return the int
         */
        public int update(final Map<String, Object> props, final EntityId entityId) {
            return em.update(props, entityId);
        }

        /**
         * Update.
         *
         * @param props the props
         * @param entityId the entity id
         * @param options the options
         * @return the int
         */
        public int update(final Map<String, Object> props, final EntityId entityId, final Map<String, Object> options) {
            return em.update(props, entityId, options);
        }

        /**
         * Update all.
         *
         * @param props the props
         * @param entityIds the entity ids
         * @return the int
         */
        public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds) {
            return em.updateAll(props, entityIds);
        }

        /**
         * Update all.
         *
         * @param props the props
         * @param entityIds the entity ids
         * @param options the options
         * @return the int
         */
        public int updateAll(final Map<String, Object> props, final List<? extends EntityId> entityIds, final Map<String, Object> options) {
            return em.updateAll(props, entityIds, options);
        }

        /**
         * Delete.
         *
         * @param entity the entity
         * @return the int
         */
        public int delete(final E entity) {
            return em.delete(entity);
        }

        /**
         * Delete.
         *
         * @param entity the entity
         * @param options the options
         * @return the int
         */
        public int delete(final E entity, final Map<String, Object> options) {
            return em.delete(entity, options);
        }

        /**
         * Delete all.
         *
         * @param entities the entities
         * @return the int
         */
        public int deleteAll(final Collection<? extends E> entities) {
            return em.deleteAll(entities);
        }

        /**
         * Delete all.
         *
         * @param entities the entities
         * @param options the options
         * @return the int
         */
        public int deleteAll(final Collection<? extends E> entities, final Map<String, Object> options) {
            return em.deleteAll(entities, options);
        }

        /**
         * Delete.
         *
         * @param entityId the entity id
         * @return the int
         */
        public int delete(final EntityId entityId) {
            return em.delete(entityId);
        }

        /**
         * Delete.
         *
         * @param entityId the entity id
         * @param options the options
         * @return the int
         */
        public int delete(final EntityId entityId, final Map<String, Object> options) {
            return em.delete(entityId, options);
        }

        /**
         * Delete all.
         *
         * @param entityIds the entity ids
         * @return the int
         */
        public int deleteAll(final List<? extends EntityId> entityIds) {
            return em.deleteAll(entityIds);
        }

        /**
         * Delete all.
         *
         * @param entityIds the entity ids
         * @param options the options
         * @return the int
         */
        public int deleteAll(final List<? extends EntityId> entityIds, final Map<String, Object> options) {
            return em.deleteAll(entityIds, options);
        }
    }
}

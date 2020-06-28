/*
 * Copyright (C) 2018 HaiYang Li
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 1.2
 */
public class OptionsBuilder {

    private final Map<String, Object> options = new HashMap<>();

    OptionsBuilder() {

    }

    public static OptionsBuilder create() {
        return new OptionsBuilder();
    }

    /**
     *
     * @param offset
     * @return
     */
    public OptionsBuilder offset(int offset) {
        N.checkArgument(offset >= 0, "'offset' can't be negative %s", offset);

        options.put(Options.Query.OFFSET, offset);

        return this;
    }

    /**
     *
     * @param count
     * @return
     */
    public OptionsBuilder count(int count) {
        N.checkArgument(count >= 0, "'count' can't be negative %s", count);

        options.put(Options.Query.COUNT, count);

        return this;
    }

    /**
     *
     * @param batchSize
     * @return
     */
    public OptionsBuilder batchSize(int batchSize) {
        N.checkArgument(batchSize >= 0, "'batchSize' can't be negative %s", batchSize);

        options.put(Options.BATCH_SIZE, batchSize == 0 ? Options.DEFAULT_BATCH_SIZE : batchSize);

        return this;
    }

    /**
     *
     * @param transactionId
     * @return
     */
    public OptionsBuilder transactionId(String transactionId) {
        N.checkArgument(N.notNullOrEmpty(transactionId), "'transactionId' can't be null or empyt.");

        options.put(Options.TRANSACTION_ID, transactionId);

        return this;
    }

    /**
     * Auto rollback transaction.
     *
     * @param autoRollbackTransaction
     * @return
     */
    public OptionsBuilder autoRollbackTransaction(boolean autoRollbackTransaction) {
        options.put(Options.AUTO_ROLLBACK_TRANSACTION, autoRollbackTransaction);

        return this;
    }

    /**
     * Record lock code.
     *
     * @param recordLockCode
     * @return
     */
    public OptionsBuilder recordLockCode(String recordLockCode) {
        N.checkArgument(N.notNullOrEmpty(recordLockCode), "'recordLockCode' can't be null or empyt.");

        options.put(Options.RECORD_LOCK_CODE, recordLockCode);

        return this;
    }

    /**
     * Record lock timeout.
     *
     * @param timeout
     * @return
     */
    public OptionsBuilder recordLockTimeout(long timeout) {
        N.checkArgument(timeout >= 0, "'recordLockTimeout' can't be negative %s", timeout);

        options.put(Options.RECORD_LOCK_TIMEOUT, timeout);

        return this;
    }

    /**
     * Enable my SQL batch add.
     *
     * @param enableMySQLBatchAdd
     * @return
     */
    public OptionsBuilder enableMySQLBatchAdd(boolean enableMySQLBatchAdd) {
        options.put(Options.ENABLE_MYSQL_BATCH_ADD, enableMySQLBatchAdd);

        return this;
    }

    /**
     * Query with data source.
     *
     * @param dataSource
     * @return
     */
    public OptionsBuilder queryWithDataSource(String dataSource) {
        N.checkArgument(N.notNullOrEmpty(dataSource), "'dataSource' can't be null or empyt.");

        options.put(Options.Query.QUERY_WITH_DATA_SOURCE, dataSource);

        return this;
    }

    /**
     * Query with data sources.
     *
     * @param dataSources
     * @return
     */
    public OptionsBuilder queryWithDataSources(Collection<String> dataSources) {
        N.checkArgument(N.notNullOrEmpty(dataSources), "'dataSources' can't be null or empyt.");

        options.put(Options.Query.QUERY_WITH_DATA_SOURCES, dataSources);

        return this;
    }

    /**
     * Query with read only connection.
     *
     * @param queryWithReadOnlyConnection
     * @return
     */
    public OptionsBuilder queryWithReadOnlyConnection(boolean queryWithReadOnlyConnection) {
        options.put(Options.Query.QUERY_WITH_READ_ONLY_CONNECTION, queryWithReadOnlyConnection);

        return this;
    }

    /**
     * Query in parallel.
     *
     * @param queryInParallel
     * @return
     */
    public OptionsBuilder queryInParallel(boolean queryInParallel) {
        options.put(Options.Query.QUERY_IN_PARALLEL, queryInParallel);

        return this;
    }

    /**
     * If search between two or more objects, like {@code join}, the result maybe is repetition beside some
     * property. Will union the repetition result if {@code COMBINE_PROPERTIES=true}.
     *
     * @param b
     * @return
     */
    public OptionsBuilder combineProperties(boolean b) {
        options.put(Options.Query.COMBINE_PROPERTIES, b);

        return this;
    }

    /**
     * Handle live time.
     *
     * @param liveTime
     * @return
     */
    public OptionsBuilder handleLiveTime(long liveTime) {
        N.checkArgument(liveTime >= 0, "'handleLiveTime' can't be negative %s", liveTime);

        options.put(Options.Query.HANDLE_LIVE_TIME, liveTime);

        return this;
    }

    /**
     * Handle max idle time.
     *
     * @param maxIdleTime
     * @return
     */
    public OptionsBuilder handleMaxIdleTime(long maxIdleTime) {
        N.checkArgument(maxIdleTime >= 0, "'handleMaxIdleTime' can't be negative %s", maxIdleTime);

        options.put(Options.Query.HANDLE_MAX_IDLE_TIME, maxIdleTime);

        return this;
    }

    /**
     * Query from cache.
     *
     * @param queryFromCache
     * @return
     */
    public OptionsBuilder queryFromCache(boolean queryFromCache) {
        options.put(Options.Query.QUERY_FROM_CACHE, queryFromCache);

        return this;
    }

    /**
     *
     * @param refreshCache
     * @return
     */
    public OptionsBuilder refreshCache(boolean refreshCache) {
        options.put(Options.Query.REFRESH_CACHE, refreshCache);

        return this;
    }

    /**
     * This option identify if cache the query result. If have set {@code CACHE_RESULT_SYN} or
     * {@code CACHE_RESULT_ASY} for this option, and the result meets the {@code QueryCache.CACHE_RESULT_CONDITION}
     * option, will cache the result in the specified {@code CACHE_RESULT_RANGE}.
     * 
     * No result will be cached in transaction.
     * 
     * By the default, no query result will be cached if this options is not set.
     *
     * @param async
     * @return
     */
    public OptionsBuilder cacheResult(boolean async) {
        options.put(Options.Query.CACHE_RESULT, async ? Options.Query.CACHE_RESULT_ASYNC : Options.Query.CACHE_RESULT_SYNC);

        return this;
    }

    /**
     *
     * @param queryTime
     * @return
     */
    public OptionsBuilder cacheCondition(long queryTime) {
        N.checkArgument(queryTime >= 0, "'cacheConditionQueryTime' can't be negative %s", queryTime);

        options.put(Options.Cache.CACHE_RESULT_CONDITION, Options.Cache.condition(queryTime));

        return this;
    }

    /**
     *
     * @param minCount
     * @param maxCount
     * @return
     */
    public OptionsBuilder cacheCondition(int minCount, int maxCount) {
        N.checkArgument(minCount >= 0, "'cacheConditionMinCount' can't be negative %s", minCount);
        N.checkArgument(maxCount >= 0, "'cacheConditionMaxCount' can't be negative %s", maxCount);

        options.put(Options.Cache.CACHE_RESULT_CONDITION, Options.Cache.condition(minCount, maxCount));

        return this;
    }

    /**
     *
     * @param queryTime
     * @param minCount
     * @param maxCount
     * @return
     */
    public OptionsBuilder cacheCondition(long queryTime, int minCount, int maxCount) {
        N.checkArgument(queryTime >= 0, "'cacheConditionQueryTime' can't be negative %s", queryTime);
        N.checkArgument(minCount >= 0, "'cacheConditionMinCount' can't be negative %s", minCount);
        N.checkArgument(maxCount >= 0, "'cacheConditionMaxCount' can't be negative %s", maxCount);

        options.put(Options.Cache.CACHE_RESULT_CONDITION, Options.Cache.condition(queryTime, minCount, maxCount));

        return this;
    }

    /**
     *
     * @param range
     * @return
     */
    public OptionsBuilder cacheRange(int... range) {
        options.put(Options.Cache.CACHE_RESULT_RANGE, Options.Cache.range(range));

        return this;
    }

    /**
     * Uncached prop names.
     *
     * @param propNames
     * @return
     */
    public OptionsBuilder uncachedPropNames(Collection<String> propNames) {
        options.put(Options.Cache.UNCACHED_PROP_NAMES, propNames);

        return this;
    }

    /**
     * Cache live time.
     *
     * @param liveTime
     * @return
     */
    public OptionsBuilder cacheLiveTime(long liveTime) {
        N.checkArgument(liveTime >= 0, "'cacheLiveTime' can't be negative %s", liveTime);

        options.put(Options.Cache.LIVE_TIME, liveTime);

        return this;
    }

    /**
     * Cache max idle time.
     *
     * @param maxIdleTime
     * @return
     */
    public OptionsBuilder cacheMaxIdleTime(long maxIdleTime) {
        N.checkArgument(maxIdleTime >= 0, "'cacheMaxIdleTime' can't be negative %s", maxIdleTime);

        options.put(Options.Cache.MAX_IDLE_TIME, maxIdleTime);

        return this;
    }

    /**
     * The limited time to update the cache after update data. unit is milliseconds.
     *
     * @param maxCheckQueryCacheTime
     * @return
     */
    public OptionsBuilder maxCheckQueryCacheTime(long maxCheckQueryCacheTime) {
        N.checkArgument(maxCheckQueryCacheTime >= 0, "'maxCheckQueryCacheTime' can't be negative %s", maxCheckQueryCacheTime);

        options.put(Options.Cache.MAX_CHECK_QUERY_CACHE_TIME, maxCheckQueryCacheTime);

        return this;
    }

    /**
     * The condition that a cache must meet when to check if it's need be update. If the cache's size less than this
     * condition, it will be removed without checking if the result in it is updated when database was updated.
     *
     * @param minCheckQueryCacheSize
     * @return
     */
    public OptionsBuilder minCheckQueryCacheSize(int minCheckQueryCacheSize) {
        N.checkArgument(minCheckQueryCacheSize >= 0, "'minCheckQueryCacheSize' can't be negative %s", minCheckQueryCacheSize);

        options.put(Options.Cache.MIN_CHECK_QUERY_CACHE_SIZE, minCheckQueryCacheSize);

        return this;
    }

    /**
     * Jdbc query timeout.
     *
     * @param jdbcQueryTimeout
     * @return
     * @see java.sql.Statement#setQueryTimeout(int)
     */
    public OptionsBuilder jdbcQueryTimeout(long jdbcQueryTimeout) {
        N.checkArgument(jdbcQueryTimeout >= 0, "'jdbcQueryTimeout' can't be negative %s", jdbcQueryTimeout);

        options.put(Options.Jdbc.QUERY_TIMEOUT, jdbcQueryTimeout);

        return this;
    }

    /**
     * Jdbc result set type.
     *
     * @param jdbcResultSetType one of {<code>java.sql.ResultSet.TYPE_FORWARD_ONLY</code>, <code>java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE</code> and <code>java.sql.ResultSet.TYPE_SCROLL_SENSITIVE</code>}
     * @return
     * @see java.sql.Connection#prepareStatement(String, int, int, int)
     */
    public OptionsBuilder jdbcResultSetType(int jdbcResultSetType) {
        N.checkArgument(jdbcResultSetType >= 0, "'jdbcResultSetType' can't be negative %s", jdbcResultSetType);

        options.put(Options.Jdbc.RESULT_SET_TYPE, jdbcResultSetType);

        return this;
    }

    /**
     * Jdbc result set concurrency.
     *
     * @param jdbcResultSetConcurrency either <code>java.sql.ResultSet.CONCUR_READ_ONLY</code> or <code>java.sql.ResultSet.CONCUR_UPDATABLE</code>.
     * @return
     * @see java.sql.Connection#prepareStatement(String, int, int, int)
     */
    public OptionsBuilder jdbcResultSetConcurrency(int jdbcResultSetConcurrency) {
        N.checkArgument(jdbcResultSetConcurrency >= 0, "'jdbcResultSetConcurrency' can't be negative %s", jdbcResultSetConcurrency);

        options.put(Options.Jdbc.RESULT_SET_CONCURRENCY, jdbcResultSetConcurrency);

        return this;
    }

    /**
     * Jdbc result set holdability.
     *
     * @param jdbcResultSetHoldability either <code>java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or <code>java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @return
     * @see java.sql.Connection#prepareStatement(String, int, int, int)
     */
    public OptionsBuilder jdbcResultSetHoldability(int jdbcResultSetHoldability) {
        N.checkArgument(jdbcResultSetHoldability >= 0, "'jdbcResultSetHoldability' can't be negative %s", jdbcResultSetHoldability);

        options.put(Options.Jdbc.RESULT_SET_HOLDABILITY, jdbcResultSetHoldability);

        return this;
    }

    /**
     * Jdbc fetch direction.
     *
     * @param jdbcFetchDirection
     * @return
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    public OptionsBuilder jdbcFetchDirection(int jdbcFetchDirection) {
        N.checkArgument(jdbcFetchDirection >= 0, "'jdbcFetchDirection' can't be negative %s", jdbcFetchDirection);

        options.put(Options.Jdbc.FETCH_DIRECTION, jdbcFetchDirection);

        return this;
    }

    /**
     * Jdbc fetch size.
     *
     * @param jdbcFetchSize
     * @return
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    public OptionsBuilder jdbcFetchSize(int jdbcFetchSize) {
        N.checkArgument(jdbcFetchSize >= 0, "'jdbcFetchSize' can't be negative %s", jdbcFetchSize);

        options.put(Options.Jdbc.FETCH_SIZE, jdbcFetchSize);

        return this;
    }

    /**
     * Jdbc max rows.
     *
     * @param jdbcMaxRows
     * @return
     * @see java.sql.Statement#setMaxRows(int)
     */
    public OptionsBuilder jdbcMaxRows(int jdbcMaxRows) {
        N.checkArgument(jdbcMaxRows >= 0, "'jdbcMaxRows' can't be negative %s", jdbcMaxRows);

        options.put(Options.Jdbc.MAX_ROWS, jdbcMaxRows);

        return this;
    }

    /**
     * Jdbc max field size.
     *
     * @param jdbcMaxFieldSize
     * @return
     * @see java.sql.Statement#setMaxFieldSize(int)
     */
    public OptionsBuilder jdbcMaxFieldSize(int jdbcMaxFieldSize) {
        N.checkArgument(jdbcMaxFieldSize >= 0, "'jdbcMaxFieldSize' can't be negative %s", jdbcMaxFieldSize);

        options.put(Options.Jdbc.MAX_FIELD_SIZE, jdbcMaxFieldSize);

        return this;
    }

    //    /**
    //     * 
    //     * @param name
    //     * @param val
    //     * @return
    //     */
    //    public OptionsBuilder set(String name, Object val) {
    //        N.checkArgNotNullOrEmpty(name, "name");
    //        N.checkArgNotNull(val);
    //
    //        options.put(name, val);
    //
    //        return this;
    //    }

    public Map<String, Object> build() {
        return options;
    }

    //    public final static class OB extends OptionsBuilder {
    //
    //    }
}

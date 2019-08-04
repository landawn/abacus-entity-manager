/*
 * Copyright (c) 2015, Haiyang Li.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.cache.CacheZipper;
import com.landawn.abacus.cache.QueryCache;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.QueryCacheConfiguration;
import com.landawn.abacus.core.sql.command.Command;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.pool.EvictionPolicy;
import com.landawn.abacus.pool.GenericKeyedObjectPool;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.MoreExecutors;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
class QueryCachePool<K, V extends QueryCache> extends GenericKeyedObjectPool<K, V> {
    private static final long serialVersionUID = -4494321879106210592L;

    private static final Logger logger = LoggerFactory.getLogger(QueryCachePool.class);
    private static final long ZIP_DELAY = 3 * 60 * 1000L;

    private static final ScheduledExecutorService scheduledExecutor;
    static {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(IOUtil.CPU_CORES);
        executor.setRemoveOnCancelPolicy(true);
        scheduledExecutor = MoreExecutors.getExitingScheduledExecutorService(executor);
    }

    private ScheduledFuture<?> scheduleFuture;

    private final QueryCacheConfiguration queryCacheConfig;

    private CacheZipper cacheZipper;

    public QueryCachePool(int capacity, long evictDelay, QueryCacheConfiguration queryCacheConfig) {
        super(capacity, evictDelay, EvictionPolicy.LAST_ACCESS_TIME);
        this.queryCacheConfig = queryCacheConfig;

        if ((queryCacheConfig != null) && queryCacheConfig.isZipCache()) {
            cacheZipper = new CacheZipper();

            final Runnable zipTask = new Runnable() {
                @Override
                public void run() {
                    // Evict from the pool
                    try {
                        cacheZipper.zip(values());
                    } catch (Exception e) {
                        // ignore

                        if (logger.isWarnEnabled()) {
                            logger.warn(AbacusException.getErrorMsg(e));
                        }
                    }
                }
            };

            scheduleFuture = scheduledExecutor.scheduleWithFixedDelay(zipTask, ZIP_DELAY, ZIP_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    public void updateCache(Command command, Map<String, Object> options) {
        // TODO [how to handle distribution].
        assertNotClosed();

        // if it's query option, do nothing.
        if (command.getOperationType() == OperationType.QUERY) {
            return;
        }

        if (!(queryCacheConfig == null || queryCacheConfig.isAutoRefresh())) {
            return;
        }

        final long startTime = System.currentTimeMillis();
        final List<K> updatedCacheKeyList = new ArrayList<>();
        final long maxCheckQueryCacheTime = EntityManagerUtil.getMaxCheckQueryCacheTime(queryCacheConfig, options);
        final int minCheckQueryCacheSize = EntityManagerUtil.getMinCheckQueryCacheSize(queryCacheConfig, options);

        lock();

        try {
            for (K cacheKey : this.keySet()) {
                V cache = peek(cacheKey);

                if (cache == null || cache.isClosed()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Removing query cache because the cache is closed. Cache query key: " + cacheKey);
                    }

                    updatedCacheKeyList.add(cacheKey);
                } else if (cache.size() < minCheckQueryCacheSize) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Removing query cache because the cache size " + cache.size() + " is less than 'min query cache check size' "
                                + minCheckQueryCacheSize + ". Cache query key: " + cacheKey);
                    }

                    updatedCacheKeyList.add(cacheKey);
                } else if ((System.currentTimeMillis() - startTime) > maxCheckQueryCacheTime) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Removing query cache because the spent cache check time " + (System.currentTimeMillis() - startTime)
                                + " is more than 'max query cache check time' " + maxCheckQueryCacheTime + ". Cache query key: " + cacheKey);
                    }

                    updatedCacheKeyList.add(cacheKey);
                } else {
                    try {
                        if (!cache.update(command, options)) {
                            if (logger.isInfoEnabled()) {
                                logger.info("Removing query cache: " + cacheKey + ". It's updated by sql: " + command.toString());
                            }

                            updatedCacheKeyList.add(cacheKey);
                        }
                    } catch (Exception e) {
                        if (logger.isInfoEnabled()) {
                            logger.info(
                                    "Removing query cache: " + cacheKey + ". It's updated by sql: " + command.toString() + ". Exception: " + e.getMessage());
                        }

                        updatedCacheKeyList.add(cacheKey);
                    }
                }
            }

            for (K removeCacheKey : updatedCacheKeyList) {
                QueryCache queryCache = remove(removeCacheKey);

                if (queryCache != null) {
                    queryCache.close();
                }
            }
        } finally {
            unlock();
        }
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        try {
            if (scheduleFuture != null) {
                scheduleFuture.cancel(true);
            }
        } finally {
            super.close();
        }
    }

    protected String createPoolKey() {
        return N.uuid();
    }
}

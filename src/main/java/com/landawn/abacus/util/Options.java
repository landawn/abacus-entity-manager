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

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.QueryCacheConfiguration.CacheResultConditionConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class Options.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class Options {
    /**
     * Field BATCH_SIZE.
     */
    public static final String BATCH_SIZE = "batchSize";

    /**
     * Field DEFAULT_BATCH_SIZE.
     */
    public static final int DEFAULT_BATCH_SIZE = EntityManagerConfiguration.DEFAULT_BATCH_SIZE;

    /**
     * This option accept an unique {@code  String} to identify if this operation is in transaction.
     * 
     */
    public static final String TRANSACTION_ID = "transactionId";

    /**
     * Type is boolean.
     */
    public static final String TRANSACTION_FOR_UPDATE_ONLY = "transactionForUpdateOnly";

    /**
     * This option is set to identify if need to automatically roll back transaction if fail to commit it.
     *  Default value is true.
     */
    public static final String AUTO_ROLLBACK_TRANSACTION = "autoRollbackTransaction";

    /**
     * The String returned by
     * {@link com.landawn.abacus.EntityManager#lockRecord(com.landawn.abacus.EntityId, com.landawn.abacus.LockMode, Map)}
     * , Which should be set into the parameter {@code options} when to operate the locked record or unlock
     * 
     */
    public static final String RECORD_LOCK_CODE = "recordLockCode";

    /**
     * The max waiting time when try to lock or operate(read, update or delete) a record which may has been locked by
     * others. unit is milliseconds
     */
    public static final String RECORD_LOCK_TIMEOUT = "recordLockTimeout";

    /**
     * It's <code>false</code> by default.
     */
    public static final String ENABLE_MYSQL_BATCH_ADD = "enableMySQLBatchAdd";

    /**
     * Constructor for Options.
     */
    private Options() {
        // no instance
    }

    /**
     * Creates the.
     *
     * @return
     */
    public static Map<String, Object> create() {
        return new HashMap<>();
    }

    /**
     * Method create.
     *
     * @param offset
     * @param count
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(int offset, int count) {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Query.OFFSET, offset);
        option.put(Query.COUNT, count);

        return option;
    }

    /**
     * Method create.
     *
     * @param offset
     * @param count
     * @param fromCache
     * @param cacheResult
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(int offset, int count, boolean fromCache, String cacheResult) {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Query.OFFSET, offset);
        option.put(Query.COUNT, count);
        option.put(Query.QUERY_FROM_CACHE, fromCache);

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        return option;
    }

    /**
     * Method create.
     *
     * @param cacheResult
     * @param range
     * @param cond
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(String cacheResult, Cache.Range range, Cache.Condition cond) {
        Map<String, Object> option = new HashMap<String, Object>();

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        if (range != null) {
            option.put(Cache.CACHE_RESULT_RANGE, range);
        }

        if (cond != null) {
            option.put(Cache.CACHE_RESULT_CONDITION, cond);
        }

        return option;
    }

    /**
     * Method create.
     *
     * @param cacheResult
     * @param range
     * @param cond
     * @param liveTime
     * @param maxIdleTime
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(String cacheResult, Cache.Range range, Cache.Condition cond, long liveTime, long maxIdleTime) {
        Map<String, Object> option = new HashMap<String, Object>();

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        if (range != null) {
            option.put(Cache.CACHE_RESULT_RANGE, range);
        }

        if (cond != null) {
            option.put(Cache.CACHE_RESULT_CONDITION, cond);
        }

        option.put(Cache.LIVE_TIME, liveTime);
        option.put(Cache.MAX_IDLE_TIME, maxIdleTime);

        return option;
    }

    /**
     * Method create.
     *
     * @param offset
     * @param count
     * @param cacheResult
     * @param range
     * @param cond
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(int offset, int count, String cacheResult, Cache.Range range, Cache.Condition cond) {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Query.OFFSET, offset);
        option.put(Query.COUNT, count);

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        if (range != null) {
            option.put(Cache.CACHE_RESULT_RANGE, range);
        }

        if (cond != null) {
            option.put(Cache.CACHE_RESULT_CONDITION, cond);
        }

        return option;
    }

    /**
     * Method create.
     *
     * @param offset
     * @param count
     * @param cacheResult
     * @param range
     * @param cond
     * @param liveTime
     * @param maxIdleTime
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(int offset, int count, String cacheResult, Cache.Range range, Cache.Condition cond, long liveTime,
            long maxIdleTime) {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Query.OFFSET, offset);
        option.put(Query.COUNT, count);

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        if (range != null) {
            option.put(Cache.CACHE_RESULT_RANGE, range);
        }

        if (cond != null) {
            option.put(Cache.CACHE_RESULT_CONDITION, cond);
        }

        option.put(Cache.LIVE_TIME, liveTime);
        option.put(Cache.MAX_IDLE_TIME, maxIdleTime);

        return option;
    }

    /**
     * Method create.
     *
     * @param offset
     * @param count
     * @param fromCache
     * @param cacheResult
     * @param range
     * @param cond
     * @param transactionId
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(int offset, int count, boolean fromCache, String cacheResult, Cache.Range range, Cache.Condition cond,
            String transactionId) {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Query.OFFSET, offset);
        option.put(Query.COUNT, count);
        option.put(Query.QUERY_FROM_CACHE, fromCache);

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        if (range != null) {
            option.put(Cache.CACHE_RESULT_RANGE, range);
        }

        if (cond != null) {
            option.put(Cache.CACHE_RESULT_CONDITION, cond);
        }

        if (transactionId != null) {
            option.put(Options.TRANSACTION_ID, transactionId);
        }

        return option;
    }

    /**
     * Method create.
     *
     * @param offset
     * @param count
     * @param fromCache
     * @param cacheResult
     * @param range
     * @param cond
     * @param liveTime
     * @param maxIdleTime
     * @param transactionId
     * @return Map<String,Object>
     */
    public static Map<String, Object> create(int offset, int count, boolean fromCache, String cacheResult, Cache.Range range, Cache.Condition cond,
            long liveTime, long maxIdleTime, String transactionId) {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Query.OFFSET, offset);
        option.put(Query.COUNT, count);
        option.put(Query.QUERY_FROM_CACHE, fromCache);

        if (cacheResult != null) {
            option.put(Query.CACHE_RESULT, cacheResult);
        }

        if (range != null) {
            option.put(Cache.CACHE_RESULT_RANGE, range);
        }

        if (cond != null) {
            option.put(Cache.CACHE_RESULT_CONDITION, cond);
        }

        if (transactionId != null) {
            option.put(Options.TRANSACTION_ID, transactionId);
        }

        option.put(Cache.LIVE_TIME, liveTime);
        option.put(Cache.MAX_IDLE_TIME, maxIdleTime);

        return option;
    }

    /**
     * Copy.
     *
     * @param options
     * @return
     */
    public static Map<String, Object> copy(final Map<String, Object> options) {
        return N.isNullOrEmpty(options) ? new HashMap<String, Object>() : new HashMap<String, Object>(options);
    }

    /**
     * Of.
     *
     * @param a
     * @return
     */
    @SafeVarargs
    public static Map<String, Object> of(final Object... a) {
        if (N.isNullOrEmpty(a)) {
            return new HashMap<>();
        } else if (a.length == 1 && a[0] instanceof Map) {
            return new HashMap<>((Map<String, Object>) a[0]);
        } else {
            return N.asMap(a);
        }
    }

    /**
     * The Class Query.
     *
     * @author Haiyang Li
     * @version $Revision: 0.8 $ 07/01/15
     */
    public static final class Query {
        /**
         * Field QUERY_WITH_DATA_SOURCE.
         */
        public static final String QUERY_WITH_DATA_SOURCE = "queryWithDataSource";

        /**
         * It's designed to support sharding. the value type is {@code Collection<String>}.
         */
        public static final String QUERY_WITH_DATA_SOURCES = "queryWithDataSources";

        /**
         * Field QUERY_WITH_READ_ONLY_CONNECTION.
         */
        public static final String QUERY_WITH_READ_ONLY_CONNECTION = "queryWithReadOnlyConnection";

        /**
         * It's designed for partitioning query. Default is true
         */
        public static final String QUERY_IN_PARALLEL = "queryInParallel";

        /**
         * This option identify if try to get result from cache at first. value is <code>true</code> or
         * <code>false</code>.
         */
        public static final String QUERY_FROM_CACHE = "queryFromCache";

        /**
         * Boolean value to identify if refresh the target result in cache when get or search.
         */
        public static final String REFRESH_CACHE = "refreshCache";

        /**
         * This option identify if cache the query result. If have set {@code CACHE_RESULT_SYNC} or
         * {@code CACHE_RESULT_ASYNC} for this option, and the result meets the {@code QueryCache.CACHE_RESULT_CONDITION}
         * option, will cache the result in the specified {@code CACHE_RESULT_RANGE}.
         * 
         * No result will be cached in transaction.
         * 
         * By the default, no query result will be cached if this options is not set.
         */
        public static final String CACHE_RESULT = "cacheResult";

        /**
         * Predicate value for {@code CACHE_RESULT_SYNC}.
         */
        public static final String CACHE_RESULT_SYNC = "cacheResultSync";

        /**
         * Predicate value for {@code  CACHE_RESULT_ASYNC}.
         */
        public static final String CACHE_RESULT_ASYNC = "cacheResultAsync";

        /**
         * Field offset.
         */
        public static final String OFFSET = "offset";

        /**
         * Field DEFAULT_OFFSET.
         */
        public static final int DEFAULT_OFFSET = 0;

        /**
         * Field COUNT.
         */
        public static final String COUNT = "count";

        /**
         * Field DEFAULT_COUNT.
         */
        public static final int DEFAULT_COUNT = Integer.MAX_VALUE;

        /**
         * If search between two or more objects, like {@code join}, the result maybe is repetition beside some
         * property. Will union the repetition result if {@code COMBINE_PROPERTIES=true}.
         */
        public static final String COMBINE_PROPERTIES = "combineProperties";

        /**
         * The Handle<String> valid duration. Over this duration. the Handle<String> is removed. Unit is milliseconds
         */
        public static final String HANDLE_LIVE_TIME = "resultHandleLiveTime";

        /**
         * Field HANDLE_DEFAULT_LIVE_TIME.
         */
        public static final long HANDLE_DEFAULT_LIVE_TIME = 7 * 24 * 3600 * 1000L;

        /**
         * The Handle<String> maxCount inactive time. If in this duration, the Handle<String> is not accessed. the cache is
         * removed. Unit is milliseconds
         */
        public static final String HANDLE_MAX_IDLE_TIME = "resultHandleMaxIdleTime";

        /** Field HANDLE_DEFAULT_MAX_IDLE_TIME. */
        public static final long HANDLE_DEFAULT_MAX_IDLE_TIME = 7 * 24 * 3600 * 1000L;

        /**
         * Constructor for Query.
         */
        private Query() {
            // no instance
        }
    }

    /**
     * The Class Cache.
     *
     * @author Haiyang Li
     * @version $Revision: 0.8 $
     */
    public static final class Cache {
        /**
         * The cache valid duration. Over this duration. the cache is removed auto. Unit is milliseconds
         */
        public static final String LIVE_TIME = "cacheLiveTime";

        /**
         * The cache maxCount inactive time. If in this duration, the cache is not accessed. the cache is removed. Unit
         * is milliseconds
         */
        public static final String MAX_IDLE_TIME = "cacheMaxIdleTime";

        /**
         * The limited time to update the cache after update data. unit is milliseconds
         */
        public static final String MAX_CHECK_QUERY_CACHE_TIME = "maxCheckQueryCacheTime";

        /**
         * The condition that a cache must meet when to check if it's need be update. If the cache's size less than this
         * condition, it will be removed without checking if the result in it is updated when database was updated.
         */
        public static final String MIN_CHECK_QUERY_CACHE_SIZE = "minCheckQueryCacheSize";

        /** Cache range option. */
        public static final String CACHE_RESULT_RANGE = "cacheResultRange";

        /**
         * Only if '{@code minCount} <= cached result's size <= {@code maxCount} ', will cache this result.
         */
        public static final String CACHE_RESULT_CONDITION = "cacheResultCondition";

        /**
         * Field UNCACHED_PROP_NAMES.
         */
        public static final String UNCACHED_PROP_NAMES = "uncachedPropNames";

        /**
         * Constructor for Query.
         */
        private Cache() {
            // no instance
        }

        /**
         * Method condition.
         * 
         * @param queryTime
         *            long
         * @return Condition
         */
        public static Cache.Condition condition(long queryTime) {
            return new Cache.Condition(queryTime);
        }

        /**
         * Method condition.
         * 
         * @param minCount
         *            int
         * @param maxCount
         *            int
         * @return Condition
         */
        public static Cache.Condition condition(int minCount, int maxCount) {
            return new Cache.Condition(minCount, maxCount);
        }

        /**
         * Method condition.
         * 
         * @param queryTime
         *            long
         * @param minCount
         *            int
         * @param maxCount
         *            int
         * @return Condition
         */
        public static Cache.Condition condition(long queryTime, int minCount, int maxCount) {
            return new Cache.Condition(queryTime, minCount, maxCount);
        }

        /**
         * Method range.
         * 
         * @param range
         *            int[]
         * @return Range
         */
        @SafeVarargs
        public static Cache.Range range(int... range) {
            return new Cache.Range(range);
        }

        /**
         * Cache the result only when the query database time equal or greater than the specified query time or the result size is between the minimum count
         * and maximum count.
         * 
         * @author Haiyang Li
         * @version $Revision: 0.8 $ 07/01/15
         */
        public static final class Condition implements Serializable {
            /**
             * Field serialVersionUID.
             */
            private static final long serialVersionUID = 1843417142154099651L;

            /**
             * If the query database time less the specified query database time. Don't cache the result. Unit is
             * milliseconds.
             */
            public static final long DEFAULT_MIN_QUERY_TIME = CacheResultConditionConfiguration.DEFAULT_MIN_QUERY_TIME;

            /** Default value for {@code DEFAULT_MIN_COUNT} option. */
            public static final int DEFAULT_MIN_COUNT = CacheResultConditionConfiguration.DEFAULT_MIN_COUNT;

            /** Default value for {@code CACHE_RESULT_CONDITION} option. */
            public static final int DEFAULT_MAX_COUNT = CacheResultConditionConfiguration.DEFAULT_MAX_COUNT;

            /**
             * Field minQueryTime.
             */
            private long minQueryTime;

            /**
             * Field minCount.
             */
            private int minCount;

            /**
             * Field maxCount.
             */
            private int maxCount;

            /**
             * Instantiates a new condition.
             */
            // For Kryo
            Condition() {
            }

            /**
             * Constructor for Condition.
             * 
             * @param minQueryTime
             *            int
             */
            public Condition(long minQueryTime) {
                this(minQueryTime, DEFAULT_MIN_COUNT, DEFAULT_MAX_COUNT);
            }

            /**
             * Constructor for Condition.
             * 
             * @param minCount
             *            int
             * @param maxCount
             *            int
             */
            public Condition(int minCount, int maxCount) {
                this(DEFAULT_MIN_QUERY_TIME, minCount, maxCount);
            }

            /**
             * Constructor for Condition.
             * 
             * @param minQueryTime
             *            int
             * @param minCount
             *            int
             * @param maxCount
             *            int
             */
            public Condition(long minQueryTime, int minCount, int maxCount) {
                if (minQueryTime < 0) {
                    throw new IllegalArgumentException("The min query db time condition[" + minQueryTime + "] must greater than zero. ");
                }

                if (minCount < 0) {
                    throw new IllegalArgumentException("Min condition[" + minCount + "] must greater than zero. ");
                }

                if (maxCount < 0) {
                    throw new IllegalArgumentException("Max condition[" + maxCount + "] must greater than zero. ");
                }

                if (minCount > maxCount) {
                    throw new IllegalArgumentException("Max condition[" + maxCount + "] must greater or equal min condition[" + minCount + "] . ");
                }

                this.minQueryTime = minQueryTime;
                this.minCount = minCount;
                this.maxCount = maxCount;
            }

            /**
             * Method getMinQueryTime.
             * 
             * @return long
             */
            public long getMinQueryTime() {
                return minQueryTime;
            }

            /**
             * Method setMinQueryTime.
             * 
             * @param minQueryTime
             *            long
             */
            public void setMinQueryTime(long minQueryTime) {
                if (minQueryTime < 0) {
                    throw new IllegalArgumentException("The query min query time condition[" + minQueryTime + "] must greater than zero. ");
                }

                this.minQueryTime = minQueryTime;
            }

            /**
             * Method getMinCount.
             * 
             * @return int
             */
            public int getMinCount() {
                return minCount;
            }

            /**
             * Method setMinCount.
             * 
             * @param minCount
             *            int
             */
            public void setMinCount(int minCount) {
                if (minCount < 0) {
                    throw new IllegalArgumentException("Min condition[" + minCount + "] must greater than zero. ");
                }

                if (minCount > maxCount) {
                    throw new IllegalArgumentException("Max condition[" + maxCount + "] must greater or equal min condition[" + minCount + "] . ");
                }

                this.minCount = minCount;
            }

            /**
             * Method getMaxCount.
             * 
             * @return int
             */
            public int getMaxCount() {
                return maxCount;
            }

            /**
             * Method setMaxCount.
             * 
             * @param maxCount
             *            int
             */
            public void setMaxCount(int maxCount) {
                if (maxCount < 0) {
                    throw new IllegalArgumentException("Max condition[" + maxCount + "] must greater than zero. ");
                }

                if (minCount > maxCount) {
                    throw new IllegalArgumentException("Max condition[" + maxCount + "] must greater or equal min condition[" + minCount + "] . ");
                }

                this.maxCount = maxCount;
            }

            /**
             * Hash code.
             *
             * @return
             */
            @Override
            public int hashCode() {
                int h = 17;
                h = (h * 31) + N.hashCode(minQueryTime);
                h = (h * 31) + minCount;
                h = (h * 31) + maxCount;

                return h;
            }

            /**
             * Equals.
             *
             * @param obj
             * @return true, if successful
             */
            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }

                if (obj instanceof Condition) {
                    Condition anotherCondition = (Condition) obj;

                    if ((minQueryTime == anotherCondition.minQueryTime) && (minCount == anotherCondition.minCount) && (maxCount == anotherCondition.maxCount)) {
                        return true;
                    }
                }

                return false;
            }

            /**
             * To string.
             *
             * @return
             */
            @Override
            public String toString() {
                return "{minQueryTime=" + minQueryTime + ", minCount=" + minCount + ", maxCount=" + maxCount + "}";
            }
        }

        /**
         * The Class Range.
         *
         * @author Haiyang Li
         * @version $Revision: 0.8 $ 07/01/15
         */
        public static final class Range implements Serializable {
            /**
             * Field serialVersionUID.
             */
            private static final long serialVersionUID = -671047873949339184L;

            /**
             * Default value for {@code CACHE_RESULT_RANGE}.
             */
            public static final int DEFAULT_FROM = 0;

            /**
             * Default value for {@code CACHE_RESULT_RANGE}.
             */
            public static final int DEFAULT_TO = Integer.MAX_VALUE;

            /**
             * Field bitSet.
             */
            private final BitSet bitSet;

            /**
             * Instantiates a new range.
             */
            // For Kryo
            Range() {
                bitSet = null;
            }

            /**
             * Constructor for Range.
             *
             * @param range
             */
            @SafeVarargs
            public Range(int... range) {
                bitSet = new BitSet();

                if (0 != (range.length % 2)) {
                    throw new IllegalArgumentException("Range: " + N.toString(range) + " must be paired. ");
                }

                for (int i = 0; i < range.length; i++) {
                    bitSet.set(range[i], range[++i]);
                }
            }

            /**
             * Method getRangeBitSet.
             * 
             * @return BitSet
             */
            public BitSet getRangeBitSet() {
                return bitSet;
            }

            /**
             * Method getRangeCount.
             * 
             * @return int
             */
            public int getRangeCount() {
                return bitSet.cardinality();
            }

            /*
             * public Range(int from, int to) { if (from < 0) { throw new IllegalArgumentException(
             * "Invalid cache range. from range must greater than zero. "); }
             * 
             * if (to < 0) { throw new IllegalArgumentException(
             * "Invalid cache range. to range must greater than zero. "); }
             * 
             * if (from > to) { throw new IllegalArgumentException(
             * "Invalid cache range. to range must greater or equal to from range. " ); }
             * 
             * this.from = from; this.to = to; }
             */

            /**
             * Hash code.
             *
             * @return
             */
            @Override
            public int hashCode() {
                int h = 17;
                h = (h * 31) + bitSet.hashCode();

                return h;
            }

            /**
             * Equals.
             *
             * @param obj
             * @return true, if successful
             */
            @Override
            public boolean equals(Object obj) {
                return this == obj || (obj instanceof Range && N.equals(((Range) obj).bitSet, bitSet));
            }

            /**
             * To string.
             *
             * @return
             */
            @Override
            public String toString() {
                String st = "{";
                int nextPair = -1;

                for (int nextSet = bitSet.nextSetBit(0); nextSet >= 0; nextSet = bitSet.nextSetBit(nextPair)) {
                    nextPair = bitSet.nextClearBit(nextSet);

                    if (st.length() == 1) {
                        st += ("(" + nextSet + ", " + nextPair + ")");
                    } else {
                        st += (", (" + nextSet + ", " + nextPair + ")");
                    }
                }

                st += "}";

                return st;
            }
        }
    }

    /**
     * The Class Jdbc.
     *
     * @author Haiyang Li
     * @version $Revision: 0.8 $ 07/01/15
     */
    public static final class Jdbc {
        /**
         * Field TIMEOUT. unit is seconds
         */
        public static final String QUERY_TIMEOUT = "jdbcQueryTimeout";

        /**
         * Field RESULT_SET_TYPE.
         */
        public static final String RESULT_SET_TYPE = "jdbcResultSetType";

        /**
         * 
         * Field RESULT_SET_CONCURRENCY.
         */
        public static final String RESULT_SET_CONCURRENCY = "jdbcResultSetConcurrency";

        /**
         * 
         * Field RESULT_SET_HOLDABILITY.
         */
        public static final String RESULT_SET_HOLDABILITY = "jdbcResultSetHoldability";

        /**
         * 
         * This option is conflict with {@code  FROM_CACHE} and {@code  CACHE_RESULT} options, Can't set this option and
         * {@code  FROM_CACHE} and {@code  CACHE_RESULT} options at the same time. Field MAX_ROWS.
         */
        public static final String MAX_ROWS = "jdbcMaxRows";

        /**
         * This option is conflict with {@code  FROM_CACHE} and {@code  CACHE_RESULT} options, Can't set this option and
         * {@code  FROM_CACHE} and {@code  CACHE_RESULT} options at the same time.s. Field MAX_FIELD_SIZE.
         */
        public static final String MAX_FIELD_SIZE = "jdbcMaxFieldSize";

        /**
         * 
         * This option is conflict with {@code  FROM_CACHE} and {@code  CACHE_RESULT} options, Can't set this option and
         * {@code  FROM_CACHE} and {@code  CACHE_RESULT} options at the same time. Field FETCH_DIRECTION.
         */
        public static final String FETCH_DIRECTION = "jdbcFetchDirection";

        /** Field FETCH_SIZE. */
        public static final String FETCH_SIZE = "jdbcFetchSize";

        /**
         * Constructor for Jdbc.
         */
        private Jdbc() {
            // no instance
        }
    }
}

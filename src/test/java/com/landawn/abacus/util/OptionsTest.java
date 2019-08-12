/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.util.Options.Cache;
import com.landawn.abacus.util.Options.Cache.Condition;
import com.landawn.abacus.util.Options.Cache.Range;
import com.landawn.abacus.util.Options.Query;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class OptionsTest extends AbstractTest {
    @Test
    public void test_create() {
        Map<String, Object> options = Options.create(1, 5);
        assertEquals(N.asProps(Query.OFFSET, 1, Query.COUNT, 5), options);

        options = Options.create(1, 5, true, Query.CACHE_RESULT_SYNC);
        assertEquals(N.asProps(Query.OFFSET, 1, Query.COUNT, 5, Query.QUERY_FROM_CACHE, true, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC), options);

        options = Options.create(1, 5, Query.CACHE_RESULT_SYNC, Cache.range(1, 10, 20, 100), Cache.condition(100));

        assertEquals(N.asProps(Query.OFFSET, 1, Query.COUNT, 5, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_RANGE,
                Cache.range(1, 10, 20, 100), Cache.CACHE_RESULT_CONDITION, Cache.condition(100)), options);

        options = Options.create(1, 5, true, Query.CACHE_RESULT_SYNC, Cache.range(1, 10, 20, 100), Cache.condition(100), "abc123");

        assertEquals(N.asProps(Query.OFFSET, 1, Query.COUNT, 5, Query.QUERY_FROM_CACHE, true, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC,
                Cache.CACHE_RESULT_RANGE, Cache.range(1, 10, 20, 100), Cache.CACHE_RESULT_CONDITION, Cache.condition(100), Options.TRANSACTION_ID, "abc123"),
                options);

        options = Options.create(1, 5, Query.CACHE_RESULT_SYNC, Cache.range(1, 10, 20, 100), Cache.condition(100), 1000, 100);

        assertEquals(N.asProps(Query.OFFSET, 1, Query.COUNT, 5, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_RANGE,
                Cache.range(1, 10, 20, 100), Cache.CACHE_RESULT_CONDITION, Cache.condition(100), Cache.LIVE_TIME, 1000L, Cache.MAX_IDLE_TIME, 100L), options);

        options = Options.create(1, 5, true, Query.CACHE_RESULT_SYNC, Cache.range(1, 10, 20, 100), Cache.condition(100), 1000, 100, "abc123");

        assertEquals(N.asProps(Query.OFFSET, 1, Query.COUNT, 5, Query.QUERY_FROM_CACHE, true, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC,
                Cache.CACHE_RESULT_RANGE, Cache.range(1, 10, 20, 100), Cache.CACHE_RESULT_CONDITION, Cache.condition(100), Cache.LIVE_TIME, 1000L,
                Cache.MAX_IDLE_TIME, 100L, Options.TRANSACTION_ID, "abc123"), options);

        options = Options.create(Query.CACHE_RESULT_SYNC, Cache.range(1, 10, 20, 100), Cache.condition(100), 1000, 100);

        assertEquals(N.asProps(Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_RANGE, Cache.range(1, 10, 20, 100), Cache.CACHE_RESULT_CONDITION,
                Cache.condition(100), Cache.LIVE_TIME, 1000L, Cache.MAX_IDLE_TIME, 100L), options);

        options = Options.create(Query.CACHE_RESULT_SYNC, Cache.range(1, 10, 20, 100), Cache.condition(100));
        assertEquals(N.asProps(Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_RANGE, Cache.range(1, 10, 20, 100), Cache.CACHE_RESULT_CONDITION,
                Cache.condition(100)), options);
    }

    @Test
    public void test_condition() {
        Condition cond = Cache.condition(10);
        N.println(cond);
        assertEquals(10, cond.getMinQueryTime());
        assertEquals(100, cond.getMinCount());
        assertEquals(100000, cond.getMaxCount());
        cond.setMinQueryTime(100);
        cond.setMinCount(10);
        cond.setMaxCount(10000);
        assertEquals(cond, Cache.condition(100, 10, 10000));
        N.println(Cache.condition(10, 10000));
        N.println(Cache.condition(10, 100, 100000));

        Set<Condition> set = N.asSet(Cache.condition(10, 100, 100000));
        assertTrue(set.contains(Cache.condition(10, 100, 100000)));

        try {
            Cache.condition(-1);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            Cache.condition(-1, 10);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            Cache.condition(10, -1);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            Cache.condition(10, 1);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            cond = Cache.condition(100);
            cond.setMinQueryTime(-1);
        } catch (IllegalArgumentException e) {
        }

        try {
            cond = Cache.condition(10);
            cond.setMinCount(-1);
        } catch (IllegalArgumentException e) {
        }

        try {
            cond = Cache.condition(100);
            cond.setMinCount(1000000000);
        } catch (IllegalArgumentException e) {
        }

        try {
            cond = Cache.condition(100);
            cond.setMaxCount(-1);
        } catch (IllegalArgumentException e) {
        }

        try {
            cond = Cache.condition(100);
            cond.setMaxCount(1);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_range() {
        Range range = Cache.range(1, 10, 20, 100);
        assertEquals(89, range.getRangeCount());
        N.println(range.getRangeBitSet());
        N.println(range);

        Set<Range> set = N.asSet(range);
        assertTrue(set.contains(Cache.range(1, 10, 20, 100)));
    }
}

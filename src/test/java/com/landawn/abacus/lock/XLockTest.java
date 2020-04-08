/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.lock;

import java.util.List;

import org.junit.Test;

import com.landawn.abacus.LockMode;
import com.landawn.abacus.cache.SpyMemcached;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Profiler;
import com.landawn.abacus.util.Throwables;

import junit.framework.TestCase;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public abstract class XLockTest extends TestCase {

    @Test
    public void test_SpyMemcached() {
        SpyMemcached<Object> spyMemcached = new SpyMemcached<>("localhost:11211");
        N.println(spyMemcached.get("abc"));

        spyMemcached.incr("abc", 1, 1000);

        N.println(spyMemcached.get("abc"));

        N.println(spyMemcached.incr("efg", 1, 100000));
    }

    @Test
    public void test_isLocked() {
        final List<XLock<Object>> xLocks = N.asList(new LocalXLock<>(), XLockFactory.createLock("Memcached(localhost:11211)"));

        for (XLock<Object> xLock : xLocks) {
            String target = N.uuid();
            for (LockMode lockMode : LockMode.values()) {
                String refLockCode = N.uuid();
                assertEquals(refLockCode, xLock.lock(target, lockMode, refLockCode, 1000));

                for (LockMode lockMode2 : LockMode.values()) {
                    assertFalse(xLock.isLocked(target, lockMode2, refLockCode));
                    assertNull(refLockCode, xLock.lock(target, lockMode2, refLockCode, 100));
                }

                xLock.unlock(target, refLockCode);
            }
        }
    }

    @Test
    public void test_isLocked_2() {
        final List<XLock<Object>> xLocks = N.asList(new LocalXLock<>(), XLockFactory.createLock("Memcached(localhost:11211)"));

        for (XLock<Object> xLock : xLocks) {
            String target = N.uuid();
            for (LockMode lockMode : LockMode.values()) {
                N.println("-------------: " + lockMode);
                String refLockCode = N.uuid();
                assertEquals(refLockCode, xLock.lock(target, lockMode, refLockCode, 1000));

                for (LockMode lockMode2 : LockMode.values()) {
                    N.println("=============: " + lockMode2);

                    String refLockCode2 = N.uuid();

                    if (lockMode.isXLockOf(lockMode2)) {
                        assertTrue(xLock.isLocked(target, lockMode2, refLockCode2));
                    } else {
                        assertFalse(xLock.isLocked(target, lockMode2, refLockCode2));
                    }

                    assertNull(xLock.lock(target, lockMode2, refLockCode2, 100));
                }

                for (LockMode lockMode3 : LockMode.values()) {
                    N.println("############: " + lockMode3);

                    String target3 = N.uuid();
                    String refLockCode3 = N.uuid();

                    assertFalse(xLock.isLocked(target3, lockMode3, refLockCode3));

                    assertEquals(refLockCode3, xLock.lock(target3, lockMode3, refLockCode3, 100));

                    assertTrue(xLock.unlock(target3, refLockCode3));
                }

                assertTrue(xLock.unlock(target, refLockCode));
            }

            for (LockMode lockMode : LockMode.values()) {
                String refLockCode = N.uuid();
                assertFalse(xLock.isLocked(target, lockMode, refLockCode));
            }
        }
    }

    //    @Test
    //    public void test_isLocked_3() {
    //        Profiler.run(this, "test_isLocked_2", 32, 10, 1).printResult();
    //    }

    @Test
    public void test_Multi_1() {
        final XLock<Object> xLock = new LocalXLock<>();

        for (int i = 0; i < 100; i++) {
            Object target = i % 13;
            String refLockCode = N.uuid();
            LockMode lockMode = LockMode.values()[i % LockMode.values().length];
            assertEquals(refLockCode, xLock.lock(target, lockMode, refLockCode, 1000));

            xLock.unlock(target, refLockCode);
        }

        Profiler.run(33, 1000, 1, new Throwables.Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Object target = i % 13;
                    String refLockCode = N.uuid();
                    LockMode lockMode = LockMode.values()[i % LockMode.values().length];
                    assertEquals(refLockCode, xLock.lock(target, lockMode, refLockCode, 1000));

                    xLock.unlock(target, refLockCode);
                }
            }
        }).printResult();
    }

    @Test
    public void test_Multi_2() {
        final XLock<Object> xLock = XLockFactory.createLock("Memcached(localhost:11211)");

        for (int i = 0; i < 100; i++) {
            Object target = i % 13;
            String refLockCode = N.uuid();
            LockMode lockMode = LockMode.values()[i % LockMode.values().length];
            assertEquals(refLockCode, xLock.lock(target, lockMode, refLockCode, 1000));

            xLock.unlock(target, refLockCode);
        }

        Profiler.run(33, 100, 1, new Throwables.Runnable<RuntimeException>() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Object target = i % 33;
                    String refLockCode = N.uuid();
                    LockMode lockMode = LockMode.values()[i % LockMode.values().length];
                    assertEquals(refLockCode, xLock.lock(target, lockMode, refLockCode, 3000));

                    xLock.unlock(target, refLockCode);
                }
            }
        }).printResult();
    }
}

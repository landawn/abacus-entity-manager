/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.lock;

import java.util.List;

import org.junit.Test;

import com.landawn.abacus.util.N;

import junit.framework.TestCase;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public class RWLockTest extends TestCase {

    @Test
    public void test_rwLock_1() {
        final List<RWLock<Object>> rwLocks = N.asList(new LocalRWLock<>(), RWLockFactory.createLock("Memcached(localhost:11211)"));

        for (RWLock<Object> rwLock : rwLocks) {
            String target = N.uuid();

            rwLock.lockReadOn(target);
            rwLock.lockReadOn(target);
            rwLock.lockReadOn(target);

            rwLock.unlockReadOn(target);
            rwLock.unlockReadOn(target);
            rwLock.unlockReadOn(target);

            rwLock.lockWriteOn(target);
            rwLock.unlockWriteOn(target);
        }
    }
}

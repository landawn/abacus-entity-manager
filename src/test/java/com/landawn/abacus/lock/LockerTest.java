/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.lock;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.lock.RWLock;
import com.landawn.abacus.lock.LocalRWLock;
import com.landawn.abacus.util.N;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class LockerTest extends AbstractTest {
    public void testLockRead() {
        int threadNum = 1000;
        final AtomicInteger threadCounter = new AtomicInteger(threadNum);
        final RWLock<String> locker = new LocalRWLock<String>();
        long startTime = System.currentTimeMillis();
        final String blocker = "";

        for (int i = 0; i < threadNum; i++) {
            Thread th = new Thread() {
                public void run() {
                    try {
                        locker.lockReadOn(blocker);
                        locker.unlockReadOn(blocker);
                        locker.lockWriteOn(blocker);
                        locker.unlockWriteOn(blocker);
                    } finally {
                        threadCounter.decrementAndGet();
                    }
                }
            };

            th.start();
        }

        while (threadCounter.get() > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        N.println(System.currentTimeMillis() - startTime);
    }
}

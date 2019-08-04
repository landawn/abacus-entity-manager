/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.concurrent.atomic.AtomicInteger;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.exception.RecordLockedException;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Try;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class ConcurrentTest extends AbstractAbacusTest {
    public void testCRUD() {
        final AtomicInteger activeThreadNum = new AtomicInteger(0);
        final int threadNumber = 100;

        for (int i = 0; i < threadNumber; i++) {
            Try.Runnable<RuntimeException> command = new Try.Runnable<RuntimeException>() {
                @Override
                public void run() {
                    try {
                        Account account = createAccount(Account.class);
                        EntityId entityId = em.add(account);

                        Account dbAccount = em.gett(entityId);

                        assertEquals(account.getFirstName(), dbAccount.getFirstName());

                        String updatedFirstName = "updatedfn";
                        dbAccount.setFirstName(updatedFirstName);
                        em.update(dbAccount);
                        assertEquals(updatedFirstName, dbAccount.getFirstName());

                        em.delete(dbAccount);
                        dbAccount = em.gett(entityId);
                        assertEquals(null, dbAccount);
                    } finally {
                        activeThreadNum.decrementAndGet();
                        N.println(activeThreadNum.get());
                    }
                }
            };

            activeThreadNum.incrementAndGet();
            N.asyncExecute(command);
        }

        while (activeThreadNum.get() > 0) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // ignore;
            }
        }

        assertEquals(0, em.query(Account.__, null, null).size());
    }

    public void testCRUDWithLock() {
        final AtomicInteger activeThreadNum = new AtomicInteger(0);
        final int threadNumber = 100;

        for (int i = 0; i < threadNumber; i++) {
            Try.Runnable<RuntimeException> command = new Try.Runnable<RuntimeException>() {
                @Override
                public void run() {
                    try {
                        Account account = createAccount(Account.class);
                        EntityId entityId = em.add(account);

                        Account dbAccount = em.gett(entityId);

                        assertEquals(account.getFirstName(), dbAccount.getFirstName());

                        String updatedFirstName = "updatedfn";
                        dbAccount.setFirstName(updatedFirstName);

                        String lockCode = em.lockRecord(entityId, LockMode.D, null);

                        try {
                            em.update(dbAccount);
                        } catch (Exception e) {
                            assertEquals(RecordLockedException.class, e.getClass());
                        } finally {
                            em.unlockRecord(entityId, lockCode, null);
                        }

                        dbAccount.setLastName("newLastName");
                        em.update(dbAccount);
                        assertEquals(updatedFirstName, dbAccount.getFirstName());

                        em.delete(dbAccount);
                        dbAccount = em.gett(entityId);
                        assertEquals(null, dbAccount);
                    } finally {
                        activeThreadNum.decrementAndGet();
                        N.println(activeThreadNum.get());
                    }
                }
            };

            activeThreadNum.incrementAndGet();
            N.asyncExecute(command);
        }

        while (activeThreadNum.get() > 0) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // ignore;
            }
        }

        assertEquals(0, em.query(Account.__, null, null).size());
    }
}

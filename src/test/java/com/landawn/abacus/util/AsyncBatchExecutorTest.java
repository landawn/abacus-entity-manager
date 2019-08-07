/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.Profiler.MultiLoopsStatistics;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class AsyncBatchExecutorTest extends AbstractEntityManager1Test {
    private final AsyncBatchExecutor<Object> asyncBatchExecutor = emf.createAsyncBatchExecutor(domainName);

    @Override
    public void setUp() {
        em.delete(Account.__, CF.alwaysTrue());
    }

    @Override
    public void tearDown() {
        em.delete(Account.__, CF.alwaysTrue());
    }

    public void testBatchAdd() throws InterruptedException, ExecutionException {
        Account account = createAccount(Account.class);
        asyncBatchExecutor.add(account);

        N.sleep(4000);

        asyncBatchExecutor.add(createAccount(Account.class));

        N.sleep(4000);

        assertNotNull(em.gett(Seid.of(Account.ID, account.getId())));

        N.println(asyncBatchExecutor.getCapacity());
        N.println(asyncBatchExecutor.getBatchSize());
        N.println(asyncBatchExecutor.getEvictDelay());
    }

    public void testBatchAddByMultiThread() throws InterruptedException, ExecutionException {
        MultiLoopsStatistics result = Profiler.run(this, ClassUtil.getDeclaredMethod(this.getClass(), "testBatchAdd"), 48, 1, 1);
        assertTrue(result.getAllFailedMethodStatisticsList().size() == 0);

        DataSet dataSet = em.query(Account.__, null, null);
        dataSet.println();
    }

    public void testBatchAdd2() throws InterruptedException, ExecutionException {
        int count = 500;

        for (int i = 0; i < count; i++) {
            asyncBatchExecutor.add(createAccount(Account.class));
        }

        N.sleep(8000);

        List<Account> dbAccounts = em.list(Account.__, null, null, null);
        assertEquals(count, dbAccounts.size());

        String newFirstName = "updatedFirstName";

        for (int i = 0; i < dbAccounts.size(); i++) {
            Account account = dbAccounts.get(i);
            account.setFirstName(newFirstName);
            asyncBatchExecutor.update(account);
            asyncBatchExecutor.delete(dbAccounts.get(++i));
            asyncBatchExecutor.add(createAccount(Account.class));
        }

        N.sleep(8000);

        dbAccounts = em.list(Account.__, null, null, null);
        assertEquals(count, dbAccounts.size());

        dbAccounts = em.list(Account.__, null, CF.eq(Account.FIRST_NAME, newFirstName), null);
        assertEquals(count / 2, dbAccounts.size());
    }
}

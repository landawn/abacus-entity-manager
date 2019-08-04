/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class MySQLTest extends AbstractAbacusTest {
    static final int batchSize = Options.DEFAULT_BATCH_SIZE;

    @Test
    public void test_mysqlBatch() {
        List<Account> accounts = new ArrayList<>(batchSize);

        for (int i = 0; i < batchSize; i++) {
            accounts.add(createAccount(Account.class));
        }

        em.addAll(accounts, N.asProps(Options.ENABLE_MYSQL_BATCH_ADD, true));
    }

    //    @Test
    //    public void test_batchAdd() {
    //        em.delete(Account.__, null, null);
    //
    //        Profiler.run(this, "jdbcBatch", 1, 3, 1).printResult();
    //        Profiler.run(this, "mysqlBatch", 1, 3, 1).printResult();
    //
    //        em.delete(Account.__, null, null);
    //    }

    void jdbcBatch() {
        List<Account> accounts = new ArrayList<>(batchSize);

        for (int i = 0; i < batchSize; i++) {
            accounts.add(createAccount(Account.class));
        }

        em.addAll(accounts, N.asProps(Options.ENABLE_MYSQL_BATCH_ADD, false));
    }

    void mysqlBatch() {
        List<Account> accounts = new ArrayList<>(batchSize);

        for (int i = 0; i < batchSize; i++) {
            accounts.add(createAccount(Account.class));
        }

        em.addAll(accounts, N.asProps(Options.ENABLE_MYSQL_BATCH_ADD, true));
    }
}

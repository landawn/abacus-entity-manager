/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import static com.landawn.abacus.dataSource.DataSourceConfiguration.MAX_ACTIVE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.u.Holder;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DataSourceTest extends AbstractEntityManager1Test {
    public void testPoolSize() throws Exception {
        for (int k = 0; k < 3; k++) {
            final AtomicInteger activeThreadNum = new AtomicInteger();
            final int exceededNum = 2;
            final int maxActive = Integer.valueOf(dsm.getPrimaryDataSource().getProperties().get(MAX_ACTIVE));
            println(maxActive);

            ExecutorService executorService = Executors.newFixedThreadPool(maxActive + exceededNum);
            final List<Connection> connList = Collections.synchronizedList(new ArrayList<Connection>());

            for (int i = 0; i < (maxActive + exceededNum); i++) {
                activeThreadNum.incrementAndGet();

                Runnable command = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connList.add(dsm.getPrimaryDataSource().getConnection());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            activeThreadNum.decrementAndGet();
                        }
                    }
                };

                executorService.execute(command);
            }

            while (activeThreadNum.get() > 0) {
                N.sleep(1);
            }

            try {
                // one connection is took for read-only operation.
                assertEquals(maxActive - 1, connList.size());
            } finally {
                for (Connection conn : connList) {
                    conn.close();
                }

                connList.clear();
            }
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // initialize the persistent connection.
        Holder<String> resultHandle = new Holder<String>(); 
        Collection<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME, Account.CONTACT);
        dbAccess.query(Account.__, selectPropNames, null, resultHandle, N.asProps(Options.Query.COUNT, 0));
        dbAccess.releaseResultHandle(resultHandle.value());
    }
}

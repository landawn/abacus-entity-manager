/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.impl.MySqlDef;
import com.landawn.abacus.util.Profiler.MultiLoopsStatistics;
import com.landawn.abacus.util.SQLExecutor.ResultExtractor;
import com.landawn.abacus.util.SQLExecutor.StatementSetter;

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
        sqlExecutor.update(MySqlDef.deleteAllAccount);
    }

    @Override
    public void tearDown() {
        sqlExecutor.update(MySqlDef.deleteAllAccount);
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

    public void test_update() throws Exception {
        Connection conn = asyncSQLExecutor.sync().getConnection();

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long id = (Long) asyncSQLExecutor.insert(MySqlDef.insertAccount, "fn", "ln", UUID.randomUUID().toString(), now, now).get();

        DataSet result = asyncSQLExecutor.query(MySqlDef.selectAccountById, id).get();
        N.println(result);

        asyncSQLExecutor.update(MySqlDef.updateAccountFirstNameById, "updatedFirstName1", id).get();
        assertEquals("updatedFirstName1", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        asyncSQLExecutor.update(MySqlDef.updateAccountFirstNameById, "updatedFirstName2", id).get();
        assertEquals("updatedFirstName2", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        asyncSQLExecutor.update(MySqlDef.updateAccountFirstNameById, "updatedFirstName3", id).get();
        assertEquals("updatedFirstName3", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        asyncSQLExecutor.update(conn, MySqlDef.updateAccountFirstNameById, "updatedFirstName4", id).get();
        assertEquals("updatedFirstName4", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        List<Object> listOfParameters = new ArrayList<>();
        listOfParameters.add(N.asArray("updatedFirstName5", id));
        asyncSQLExecutor.batchUpdate(MySqlDef.updateAccountFirstNameById, listOfParameters).get();
        assertEquals("updatedFirstName5", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        listOfParameters = new ArrayList<>();
        listOfParameters.add(N.asArray("updatedFirstName6", id));
        asyncSQLExecutor.batchUpdate(MySqlDef.updateAccountFirstNameById, null, null, listOfParameters).get();
        assertEquals("updatedFirstName6", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        listOfParameters = new ArrayList<>();
        listOfParameters.add(N.asArray("updatedFirstName7", id));
        asyncSQLExecutor.batchUpdate(conn, MySqlDef.updateAccountFirstNameById, listOfParameters).get();
        assertEquals("updatedFirstName7", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        listOfParameters = new ArrayList<>();
        listOfParameters.add(N.asArray("updatedFirstName8", id));
        asyncSQLExecutor.batchUpdate(conn, MySqlDef.updateAccountFirstNameById, listOfParameters).get();
        assertEquals("updatedFirstName8", asyncSQLExecutor.findFirst(Account.class, MySqlDef.selectAccountById, id).get().get().getFirstName());

        asyncSQLExecutor.update(MySqlDef.deleteAccountById, id).get();
        result = asyncSQLExecutor.query(MySqlDef.selectAccountById, id).get();
        N.println(result);
        assertEquals(0, result.size());

        asyncSQLExecutor.sync().closeConnection(conn);
    }

    @Test
    public void test_query() throws Exception {
        Connection conn = asyncSQLExecutor.sync().getConnection();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long id = (Long) asyncSQLExecutor.insert(MySqlDef.insertAccount, "fn", "ln", UUID.randomUUID().toString(), now, now).get();

        DataSet result = asyncSQLExecutor.query(MySqlDef.selectAccountById, id).get();
        N.println(result);
        assertEquals(1, result.size());

        result = asyncSQLExecutor.query(MySqlDef.selectAccountById, id).get();
        assertEquals(1, result.size());

        result = asyncSQLExecutor.query(MySqlDef.selectAccountById, ResultExtractor.DATA_SET, id).get();
        assertEquals(1, result.size());

        result = asyncSQLExecutor.query(MySqlDef.selectAccountById, (StatementSetter) null, ResultExtractor.DATA_SET, null, id).get();
        assertEquals(1, result.size());

        result = asyncSQLExecutor.query(conn, MySqlDef.selectAccountById, id).get();
        assertEquals(1, result.size());

        asyncSQLExecutor.execute(MySqlDef.deleteAccountById, id).get();
        result = asyncSQLExecutor.query(MySqlDef.selectAccountById, id).get();
        N.println(result);
        assertEquals(0, result.size());

        asyncSQLExecutor.sync().closeConnection(conn);
    }
}

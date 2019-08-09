/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.util.List;
import java.util.Map;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.entity.extendDirty.lvc.AccountContact;
import com.landawn.abacus.exception.InvalidResultHandleException;
import com.landawn.abacus.util.Options.Query;
import com.landawn.abacus.util.u.Holder;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class ExEntityManagerTest extends AbstractEntityManager1Test {

    public void test_refresh() {
        List<Account> accounts = addAccountWithContact(Account.class, 2);

        Account account = accounts.get(0);
        em.update(N.asProps(Account.FIRST_NAME, "updatedFirstName"), Seid.of(Account.ID, account.getId()));

        assertEquals(true, em.refresh(account));
        assertEquals(true, em.refresh(account, null));

        assertEquals(2, em.refreshAll(accounts));
        assertEquals(2, em.refreshAll(accounts, null));

        em.delete(account);
    }

    public void test_query() {
        em.delete(Account.__, null, null);

        List<Account> accounts = addAccountWithContact(Account.class, 5);

        assertEquals(10, em.queryAll(Account.__, null, null, null).size());
        assertEquals(10, em.listAll(Account.__, null, null, null).size());

        Holder<String> resultHandle = new Holder<>();
        DataSet rs = em.query(Account.__, null, null, resultHandle, null);
        assertEquals(5, rs.size());
        rs = em.getResultByHandle(resultHandle.value(), null, null);
        assertEquals(5, rs.size());
        em.releaseResultHandle(resultHandle.value());

        try {
            em.getResultByHandle(resultHandle.value(), null, null);
            fail("Should throw InvalidResultHandleException");
        } catch (InvalidResultHandleException e) {
        }

        em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME), null).println();

        em.deleteAll(accounts);
    }

    public void testCRUD0() {
        List<Account> accounts = addAccountWithContact(Account.class, 5);
        assertEquals(5, em.queryForInt(Account.__, "count(*)", null).orElse(0));

        Account account = accounts.get(0);
        long id = account.getId();
        assertTrue(em.exists(Account.__, id));

        assertTrue(em.exists(Account.__, String.valueOf(id)));

        assertTrue(em.exists(Account.__, CF.eq(Account.ID, id)));

        assertEquals(id, ((Account) em.gett(Account.__, id)).getId());

        N.println(em.gett(Account.__, accounts.get(0).getId(), N.asList(Account.FIRST_NAME, Account.LAST_NAME)));

        assertEquals(false, em.queryForBoolean(Account.__, Account.STATUS, null).orElse(false));
        assertEquals(0, em.queryForByte(Account.__, Account.STATUS, null).orElse((byte) 0));
        assertEquals(0, em.queryForChar(Account.__, Account.STATUS, null).orElse(N.CHAR_0));
        assertEquals(0, em.queryForShort(Account.__, Account.STATUS, null).orElse((short) 0));
        assertEquals(0, em.queryForLong(Account.__, Account.STATUS, null).orElse(0));
        assertEquals(0f, em.queryForFloat(Account.__, Account.STATUS, null).orElse(0f));
        assertEquals(0d, em.queryForDouble(Account.__, Account.STATUS, null).orElse(0d));
        assertEquals("0", em.queryForSingleResult(String.class, Account.__, Account.STATUS, null).orElse(null));
        N.println(em.queryForSingleResult(String.class, Account.__, Account.CREATE_TIME, null));

        // N.println(em.queryForMap(Account.__, null, null));
        N.println(em.findFirst(Account.class, null, null));
        N.println(em.query(Account.__, null, null));

        // N.println(em.queryForMap(Account.__, Account._PNL, null));
        N.println(em.findFirst(Account.class, Account._PNL, null));
        N.println(em.query(Account.__, Account._PNL, null));

        assertEquals(0, em.query(Account.__, null, CF.eq(Account.ID, 0), null, null).size());

        try {
            N.println(em.gett(Seid.of(Account.FIRST_NAME, accounts.get(0).getFirstName())));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        EntityId entityId = Seid.of(Account.ID, id);
        List<EntityId> entityIdList = N.asList(entityId);

        em.gett(Account.__, String.valueOf(id));
        em.gett(Account.__, String.valueOf(id), Account._PNL);

        em.gett(entityId);
        em.gett(entityId, Account._PNL);
        em.gett(entityId, Account._PNL, null);

        //        em.getAll(entityIdList);
        //        em.getAll(entityIdList, Account._PNL);
        //        em.getAll(entityIdList, Account._PNL, null);

        em.update(N.asProps(Account.FIRST_NAME, "updatedFirstName"), entityId);
        em.delete(entityId);

        em.update(N.asProps(Account.FIRST_NAME, "updatedFirstName"), entityId, null);
        em.delete(entityId, null);

        try {
            em.updateAll(null, N.asList());
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            em.updateAll(null, N.asList());
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        em.delete(account);
        em.add(account);

        try {
            em.update(account);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        em.delete(account);

        em.delete(account, null);
        em.add(account, null);

        try {
            em.update(account, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        em.delete(account, null);

        em.deleteAll(accounts);
        em.addAll(accounts);

        try {
            em.updateAll(accounts);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        em.deleteAll(accounts);

        em.deleteAll(accounts, null);
        em.addAll(accounts, null);

        try {
            em.updateAll(accounts, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        em.deleteAll(accounts, null);

        String lockCode = em.lockRecord(entityId, LockMode.D, null);
        em.unlockRecord(entityId, lockCode, null);

        String tranId = em.beginTransaction(IsolationLevel.READ_COMMITTED, null);
        em.endTransaction(tranId, Action.COMMIT, null);
    }

    public void testCRUD1() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = em.add(Account.__, props, null);
        assertTrue(em.exists(entityId));

        Account dbAccount = em.gett(entityId, null, null);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        Map<String, Object> updateProps = N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME);
        int updatedCount = em.update(Account.__, updateProps, CF.eq(Account.FIRST_NAME, dbAccount.getFirstName()), null);
        assertEquals(1, updatedCount);

        List<Account> dbAccounts = em.list(Account.__, Account._PNL, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(1, dbAccounts.size());
        assertEquals(UPDATED_FIRST_NAME, dbAccounts.get(0).getFirstName());

        updatedCount = em.delete(Account.__, CF.ge(Account.ID, dbAccount.getId()), null);
        assertEquals(1, updatedCount);
        assertFalse(em.exists(entityId));
        dbAccounts = em.list(Account.__, Account._PNL, CF.eq(Account.ID, dbAccount.getId()));
        assertEquals(0, dbAccounts.size());
        dbAccounts = em.list(Account.__, Account._PNL, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(0, dbAccounts.size());
    }

    public void testMerge() {
        List<Map<String, Object>> propsList = createAccountPropsList(1);
        EntityId entityId = em.addAll(Account.__, propsList, null).get(0);
        assertTrue(em.exists(entityId));

        Account dbAccount = em.gett(entityId, null, null);
        assertEquals(propsList.get(0).get(Account.FIRST_NAME), dbAccount.getFirstName());

        dbAccount.setFirstName(UPDATED_FIRST_NAME);
        em.addOrUpdate(dbAccount, CF.eq(Account.EMAIL_ADDRESS, dbAccount.getEmailAddress()));

        dbAccount = em.gett(entityId, null, null);
        assertEquals(UPDATED_FIRST_NAME, dbAccount.getFirstName());

        int updatedCount = em.delete(Account.__, CF.ge(Account.ID, dbAccount.getId()), null);
        assertEquals(1, updatedCount);

        em.addOrUpdate(dbAccount, CF.eq(Account.EMAIL_ADDRESS, dbAccount.getEmailAddress()));
        dbAccount = em.gett(entityId, null, null);
        assertEquals(UPDATED_FIRST_NAME, dbAccount.getFirstName());
    }

    public void testMerge_2() {
        Account account = createAccount(Account.class);
        em.addOrUpdate(account, CF.eq(Account.EMAIL_ADDRESS, account.getEmailAddress()));

        EntityId entityId = Seid.of(Account.ID, account.getId());

        Account dbAccount = em.gett(entityId, null, null);
        dbAccount.setFirstName(UPDATED_FIRST_NAME);
        em.addOrUpdate(dbAccount, CF.eq(Account.EMAIL_ADDRESS, dbAccount.getEmailAddress()));
        dbAccount = em.gett(entityId, null, null);
        assertEquals(UPDATED_FIRST_NAME, dbAccount.getFirstName());

        int updatedCount = em.delete(Account.__, CF.ge(Account.ID, dbAccount.getId()), null);
        assertEquals(1, updatedCount);

        em.addOrUpdate(dbAccount, CF.eq(Account.EMAIL_ADDRESS, dbAccount.getEmailAddress()));
        dbAccount = em.gett(entityId, null, null);
        assertEquals(UPDATED_FIRST_NAME, dbAccount.getFirstName());
    }

    public void test_queryAll() {
        addAccountWithContact(Account.class, 5);
        assertEquals(5, em.queryForInt(Account.__, "count(*)", null).get());

        DataSet dataSet = em.queryAll(Account.__, null, null, null);
        dataSet.println();
        assertEquals(10, dataSet.size());

        dataSet = em.queryAll(Account.__, null, null, Options.of(Query.COUNT, 7));
        dataSet.println();
        assertEquals(7, dataSet.size());

        dataSet = em.queryAll(Account.__, null, null,
                Options.of(Query.QUERY_WITH_DATA_SOURCES, emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        dataSet.println();

        dataSet = em.queryAll(Account.__, null, null,
                Options.of(Query.COUNT, 8, Query.QUERY_WITH_DATA_SOURCES, emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        dataSet.println();
        assertEquals(8, dataSet.size());

        dataSet = em.queryAll(Account.__, null, null, Options.of(Query.QUERY_IN_PARALLEL, false, Query.COUNT, 8, Query.QUERY_WITH_DATA_SOURCES,
                emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        dataSet.println();
        assertEquals(8, dataSet.size());

        dataSet = em.queryAll(AccountContact.__, null, null, null);
        dataSet.println();

        dataSet = em.queryAll(AccountContact.__, null, null,
                Options.of(Query.QUERY_WITH_DATA_SOURCES, emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        dataSet.println();

        em.delete(Account.__, CF.ge(Account.ID, 0), null);
    }

    public void test_listAll() {
        addAccountWithContact(Account.class, 999);
        assertEquals(999, em.queryForInt(Account.__, "count(*)", null).orElse(0));

        List<Account> accounts = em.listAll(Account.__, null, null,
                Options.of(Query.QUERY_WITH_DATA_SOURCES, emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        N.println(accounts);
        assertEquals(1998, accounts.size());

        accounts = em.listAll(Account.__, null, null,
                Options.of(Query.COUNT, 10, Query.QUERY_WITH_DATA_SOURCES, emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        N.println(accounts);
        assertEquals(10, accounts.size());

        accounts = em.listAll(Account.__, null, null, null);
        N.println(accounts);
        assertEquals(1998, accounts.size());

        accounts = em.listAll(Account.__, null, null, Options.of(Query.COUNT, 10));
        N.println(accounts);
        assertEquals(10, accounts.size());

        accounts = em.listAll(Account.__, null, null, Options.of(Query.COUNT, 10, Query.QUERY_IN_PARALLEL, false));
        N.println(accounts);
        assertEquals(10, accounts.size());

        List<AccountContact> accountContacts = em.listAll(AccountContact.__, null, null, null);
        N.println(accountContacts);

        accountContacts = em.listAll(AccountContact.__, null, null,
                Options.of(Query.QUERY_WITH_DATA_SOURCES, emf.getDataSourceManager(domainName).getActiveDataSources().keySet()));
        N.println(accountContacts);

        em.delete(Account.__, CF.ge(Account.ID, 0), null);
    }
}

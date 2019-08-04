/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SessionTest extends AbstractEntityManager1Test {

    @Test
    public void test_01() {
        N.println("abc");
    }

    //    public void testCRUD() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        session.add(N.asList(account));
    //        session.flush();
    //
    //        Account dbAccount = session.get(Seid.of(Account.ID, account.getId()), Account._PNL);
    //        assertEquals(account.getContact().getId(), dbAccount.getContact().getId());
    //        assertEquals(account.getContact().getAccountId(), dbAccount.getContact().getAccountId());
    //
    //        account.setLastName(UPDATED_LAST_NAME);
    //        session.flush();
    //
    //        dbAccount = em.get(Seid.of(Account.ID, account.getId()), Account._PNL);
    //        assertEquals(UPDATED_LAST_NAME, dbAccount.getLastName());
    //
    //        session.delete(account);
    //        session.close();
    //
    //        try {
    //            session.close();
    //            throw new IllegalStateException("Should throw IllegalStateException");
    //        } catch (IllegalStateException e) {
    //        }
    //
    //        dbAccount = em.get(Seid.of(Account.ID, account.getId()));
    //        assertEquals(null, dbAccount);
    //
    //        N.println(session.toString());
    //    }
    //
    //    public void testContains() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        session.add(account);
    //
    //        assertTrue(session.contains(account));
    //        account.setId(100000);
    //        assertTrue(session.contains(account));
    //
    //        Account clone = N.copy(account);
    //        assertTrue(clone.equals(account));
    //        assertFalse(session.contains(clone));
    //
    //        account.setId(268545);
    //        session.detach(account);
    //        assertFalse(session.contains(account));
    //
    //        Account dbAccount = em.get(Account.__, account.getId());
    //        assertNull(dbAccount);
    //        session.close();
    //
    //        dbAccount = em.get(Account.__, account.getId(), null);
    //        assertNull(dbAccount);
    //    }
    //
    //    public void testAttach() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        session.add(account);
    //
    //        Account copy = N.copy(account);
    //
    //        Collection<?> list = N.asList(copy);
    //        session.update(list);
    //        assertTrue(session.contains(copy));
    //        session.detach(N.asList(copy));
    //        assertFalse(session.contains(copy));
    //    }
    //
    //    public void testFlush() {
    //        Session<Object> session = emf.createSession(domainName);
    //        List<Account> accounts = addAccount(Account.class, 5);
    //        accounts = session.find(Account.__, null, null);
    //        session.flush();
    //
    //        for (Account account : accounts) {
    //            account.setLastName(UPDATED_LAST_NAME);
    //        }
    //
    //        assertTrue(session.isDirty());
    //        session.delete(accounts.get(1));
    //        session.delete(accounts.get(2));
    //        session.flush();
    //
    //        session.delete(accounts);
    //        session.close();
    //    }
    //
    //    public void testClose() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        session.add(account);
    //        session.close();
    //
    //        Account dbAccount = em.get(Account.__, account.getId());
    //        assertEquals(account.getFirstName(), dbAccount.getFirstName());
    //        assertTrue(session.isClosed());
    //
    //        try {
    //            session.update(dbAccount);
    //            fail("show throw IllegalStateException");
    //        } catch (IllegalStateException e) {
    //        }
    //    }
    //
    //    public void testTransaction() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Transaction tran = session.beginTransaction(IsolationLevel.DEFAULT);
    //        tran.commit();
    //
    //        tran = session.beginTransaction(IsolationLevel.DEFAULT);
    //        tran.rollback();
    //    }
    //
    //    public void testTransaction2() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Transaction tran = session.beginTransaction(IsolationLevel.DEFAULT);
    //
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        session.add(account);
    //        session.flush();
    //        tran.commit();
    //
    //        Account dbAccount = em.get(Account.__, account.getId(), null);
    //        assertEquals(account.getFirstName(), dbAccount.getFirstName());
    //        assertFalse(session.isClosed());
    //    }
    //
    //    public void testTransaction3() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Transaction tran = session.beginTransaction(IsolationLevel.DEFAULT);
    //
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        session.add(account);
    //        session.flush();
    //        tran.rollback();
    //
    //        Account dbAccount = em.get(Account.__, account.getId(), null);
    //        assertNull(dbAccount);
    //        assertFalse(session.isClosed());
    //    }
    //
    //    public void testAutoRollback() {
    //        Session<Object> session = emf.createSession(domainName);
    //        Account account = createAccount(Account.class);
    //        account.setContact(createAccountContact(AccountContact.class));
    //        account.setGUI(null);
    //        session.add(account);
    //
    //        try {
    //            session.flush();
    //            fail("should throw AbacusSQLException");
    //        } catch (UncheckedSQLException e) {
    //        }
    //
    //        Account dbAccount = em.get(Account.__, account.getId(), null);
    //        assertEquals(null, dbAccount);
    //    }
}

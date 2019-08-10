/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core.extendDirty.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.basic.Account;
import com.landawn.abacus.entity.extendDirty.basic.AccountContact;
import com.landawn.abacus.entity.extendDirty.basic.ExtendDirtyBasicPNL;
import com.landawn.abacus.entity.extendDirty.basic.ExtendDirtyBasicPNL.Account1PNL;
import com.landawn.abacus.entity.extendDirty.basic.ExtendDirtyBasicPNL.Account2PNL;
import com.landawn.abacus.exception.InvalidResultHandleException;
import com.landawn.abacus.exception.InvalidTransactionIdException;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.u.Holder;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DBAccessTest extends AbstractEntityManager1Test {
    @Test
    public void testCRUD() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);
        Account dbAccount = dbAccess.gett(entityId, null, null);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        Map<String, Object> updateProps = N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME);
        int updatedCount = dbAccess.update(Account.__, updateProps, CF.eq(Account.FIRST_NAME, dbAccount.getFirstName()), null);
        assertEquals(1, updatedCount);

        List<Account> dbAccounts = dbAccess.list(Account.__, null, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(1, dbAccounts.size());
        assertEquals(UPDATED_FIRST_NAME, dbAccounts.get(0).getFirstName());

        updatedCount = dbAccess.delete(Account.__, CF.ge(Account.ID, dbAccount.getId()), null);
        assertEquals(1, updatedCount);

        dbAccounts = dbAccess.list(Account.__, null, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(0, dbAccounts.size());
    }

    @Test
    public void testCRUD_2() {
        dbAccess.delete(Account.__, null, null);
        dbAccess.delete(Account1PNL.__, null, null);
        dbAccess.delete(Account2PNL.__, null, null);

        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account1PNL.__, props, null);
        Account dbAccount = dbAccess.gett(entityId, null, null);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        List<Account> dbAccounts = em.listAll(Account.__, null, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(props.get(Account.FIRST_NAME), dbAccounts.get(0).getFirstName());

        em.queryAll(Account.__, null, null, null).println();

        Map<String, Object> updateProps = N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME);
        int updatedCount = dbAccess.update(Account1PNL.__, updateProps, CF.eq(Account.FIRST_NAME, dbAccount.getFirstName()), null);
        assertEquals(1, updatedCount);

        dbAccounts = dbAccess.list(Account1PNL.__, null, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(1, dbAccounts.size());
        assertEquals(UPDATED_FIRST_NAME, dbAccounts.get(0).getFirstName());

        updatedCount = dbAccess.delete(Account1PNL.__, CF.ge(Account.ID, dbAccount.getId()), null);
        assertEquals(1, updatedCount);

        dbAccounts = dbAccess.list(Account1PNL.__, null, CF.eq(Account.ID, dbAccount.getId()), null);
        assertEquals(0, dbAccounts.size());
    }

    @Test
    public void testCRUD_3() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);
        Account dbAccount = dbAccess.gett(entityId);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        dbAccount = dbAccess.gett(entityId, null);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        //        dbAccount = (Account) dbAccess.getAll(N.asList(entityId)).get(0);
        //        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());
        //
        //        dbAccount = (Account) dbAccess.getAll(N.asList(entityId), null).get(0);
        //        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        dbAccount = (Account) dbAccess.list(Account.__, null, CF.eq(Account.ID, dbAccount.getId())).get(0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        Map<String, Object> updateProps = N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME);
        int updatedCount = dbAccess.update(updateProps, entityId);
        assertEquals(1, updatedCount);

        updatedCount = dbAccess.updateAll(updateProps, N.asList(entityId));
        assertEquals(1, updatedCount);

        updatedCount = dbAccess.deleteAll(N.asList(entityId));
        assertEquals(1, updatedCount);

        updatedCount = dbAccess.delete(entityId);
        assertEquals(0, updatedCount);

        List<Map<String, Object>> propsList = createAccountPropsList(777);
        List<EntityId> entityIds = dbAccess.addAll(Account.__, propsList, null);
        assertEquals(propsList.size(), entityIds.size());

        //        List<Account> dbAccounts = dbAccess.getAll(entityIds);
        //        assertEquals(propsList.size(), dbAccounts.size());

        updatedCount = dbAccess.updateAll(updateProps, entityIds);
        assertEquals(propsList.size(), updatedCount);

        updatedCount = dbAccess.deleteAll(entityIds);
        assertEquals(propsList.size(), updatedCount);
    }

    @Test
    public void testGet_NullEntityId() {
        try {
            dbAccess.gett((EntityId) null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_EmptyEntityId() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);

        try {
            dbAccess.gett(Seid.of(Account.__), null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_NonExistEntity() {
        EntityId entityId = Seid.of("non-exist-entity.id", 1);

        try {
            dbAccess.gett(entityId, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list("non-exist-entity", null, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_NullEntity() {
        String entityName = null;

        try {
            Seid.of(entityName);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(null, null, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list("", null, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(" ", null, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_NonExistEntityId_2() {
        EntityId entityId = Seid.of("non-exist-entity");

        try {
            dbAccess.gett(entityId, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_NullSelectPropNames() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);
        Account dbAccount = dbAccess.gett(entityId, null, null);
        assertTrue(dbAccount.getId() > 0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        List<Account> dbAccounts = dbAccess.list(Account.__, null, null, null);
        assertTrue(dbAccounts.get(0).getId() > 0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccounts.get(0).getFirstName());
    }

    @Test
    public void testGet_EmptySelectPropNames() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);

        List<String> selectPropNames = new ArrayList<>();
        Account dbAccount = dbAccess.gett(entityId, selectPropNames, null);
        assertTrue(dbAccount.getId() > 0);
        assertEquals("firstName", dbAccount.getFirstName());

        List<Account> dbAccounts = dbAccess.list(Account.__, selectPropNames, null, null);
        assertTrue(dbAccounts.get(0).getId() > 0);
        assertEquals("firstName", dbAccounts.get(0).getFirstName());
    }

    @Test
    public void testGet_WithOtherEntitySelectPropNames() {
        addAccount(Account.class, 5);

        List<String> selectPropNames = N.asList(Account.FIRST_NAME, AccountContact.ADDRESS);

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_WithIdSelectPropNames() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);

        List<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME);
        Account dbAccount = dbAccess.gett(entityId, selectPropNames, null);
        assertTrue(dbAccount.getId() > 0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        List<Account> dbAccounts = dbAccess.list(Account.__, selectPropNames, null, null);
        assertTrue(dbAccounts.get(0).getId() > 0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccounts.get(0).getFirstName());
    }

    @Test
    public void testGet_WithoutIdSelectPropNames() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);
        List<String> selectPropNames = N.asList(Account.FIRST_NAME);
        Account dbAccount = dbAccess.gett(entityId, selectPropNames, null);
        assertTrue(dbAccount.getId() > 0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccount.getFirstName());

        List<Account> dbAccounts = dbAccess.list(Account.__, selectPropNames, null, null);
        assertTrue(dbAccounts.get(0).getId() > 0);
        assertEquals(props.get(Account.FIRST_NAME), dbAccounts.get(0).getFirstName());
    }

    @Test
    public void testGet_NonExistSelectPropNames() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);
        List<String> selectPropNames = N.asList("non-exist-prop", Account.FIRST_NAME);

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = N.asList("non-exist-prop");

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = N.asList("non-exist-prop");

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
        }

        selectPropNames = N.asList("");

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = N.asList("黎");

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = N.asList("");

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = N.asList(" ");

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = new ArrayList<>();
        selectPropNames.add(null);

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGet_NullEleSelectPropNames() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);
        List<String> selectPropNames = N.asList(Account.FIRST_NAME, null);

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        selectPropNames = new ArrayList<>();
        selectPropNames.add(null);

        try {
            dbAccess.gett(entityId, selectPropNames, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.list(Account.__, selectPropNames, null, null);
            fail("IllegalArgumentException should be threw.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testAdd_NullOrEmptyEntity() {
        Map<String, Object> props = N.asProps(Account.FIRST_NAME, FIRST_NAME, Account.LAST_NAME, LAST_NAME);
        List<Map<String, Object>> propsList = N.asList(props);
        List<String> propNames = new ArrayList<>(Account._PNL);
        propNames.remove(Account.CONTACT);

        try {
            dbAccess.add(null, props, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.addAll(null, propsList, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.add("", props, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.addAll("", propsList, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.add(" ", props, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.addAll(" ", propsList, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testAdd_NullOrEmptyPropName() {
        addAccount(Account.class, 5);

        Map<String, Object> props = N.asProps(Account.FIRST_NAME, FIRST_NAME, Account.LAST_NAME, LAST_NAME, null, LAST_NAME);
        List<Map<String, Object>> propsList = N.asList(props);
        List<String> propNames = new ArrayList<>(Account._PNL);
        propNames.remove(Account.CONTACT);
        propNames.add(null);

        try {
            dbAccess.add(Account.__, props, null);
            fail("NullPointerException should be threw.");
        } catch (NullPointerException e) {
        }

        try {
            dbAccess.addAll(Account.__, propsList, null);
            fail("NullPointerException should be threw.");
        } catch (NullPointerException e) {
        }

        props = N.asProps(Account.FIRST_NAME, FIRST_NAME, Account.LAST_NAME, LAST_NAME, "", LAST_NAME);
        propsList = N.asList(props);
        propNames = new ArrayList<>(Account._PNL);
        propNames.remove(Account.CONTACT);
        propNames.add("");

        try {
            dbAccess.add(Account.__, props, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.addAll(Account.__, propsList, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        props = N.asProps(Account.FIRST_NAME, FIRST_NAME, Account.LAST_NAME, LAST_NAME, " a", LAST_NAME);
        propsList = N.asList(props);
        propNames = new ArrayList<>(Account._PNL);
        propNames.remove(Account.CONTACT);
        propNames.add(" a");

        try {
            dbAccess.add(Account.__, props, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            dbAccess.addAll(Account.__, propsList, null);
            fail("IllegalArgumentException should be threw.");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testAdd_byProps() {
        Map<String, Object> props = createAccountProps();
        EntityId entityId = dbAccess.add(Account.__, props, null);

        Account account = dbAccess.gett(entityId, null, null);
        assertEquals(props.get(Account.FIRST_NAME), account.getFirstName());

        dbAccess.delete(Account.__, CF.eq(Account.ID, account.getId()), null);

        props = createAccountProps();
        props.put(Account.ID, 1000000000);
        entityId = dbAccess.add(Account.__, props, null);

        account = dbAccess.gett(entityId, null, null);
        assertEquals(props.get(Account.FIRST_NAME), account.getFirstName());

        dbAccess.delete(Account.__, CF.eq(Account.ID, account.getId()), null);
    }

    @Test
    public void testAdd_byPropsList_1() {
        Map<String, Object> props = createAccountProps();
        List<Map<String, Object>> propsList = N.asList(props);
        List<EntityId> entityIds = dbAccess.addAll(Account.__, propsList, null);
        assertEquals(propsList.size(), entityIds.size());

        Account account = dbAccess.gett(entityIds.get(0), null, null);
        assertEquals(props.get(Account.FIRST_NAME), account.getFirstName());

        dbAccess.delete(Account.__, CF.eq(Account.ID, account.getId()), null);

        // -----------------
        props = createAccountProps();
        props.put(Account.ID, 1000000000);
        propsList = N.asList(props);
        entityIds = dbAccess.addAll(Account.__, propsList, null);
        assertEquals(propsList.size(), entityIds.size());

        account = dbAccess.gett(entityIds.get(0), null, null);
        assertEquals(props.get(Account.FIRST_NAME), account.getFirstName());

        dbAccess.delete(Account.__, CF.eq(Account.ID, account.getId()), null);
    }

    //
    //    @Test
    //    public void testAdd_byPropsList_2() {
    //        final int batchSize = 200;
    //        int count = 500;
    //        Map<String, Object> options = N.asProps(Options.BATCH_SIZE, batchSize);
    //
    //        // -----------------
    //        List<Map<String, Object>> propsList = createAccountPropsList(count);
    //        String st = propsList.toString();
    //        List<EntityId> entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(0).put(Account.ID, 0);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(1).put(Account.ID, 0);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(111).put(Account.ID, 0);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(count - 1).put(Account.ID, 0);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(0).put(Account.ID, 1111);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(1).put(Account.ID, 1111);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(111).put(Account.ID, 1111);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(count - 1).put(Account.ID, 1111);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.FIRST_NAME), account.getFirstName());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //    }
    //
    //    @Test
    //    public void testAdd_byPropsList_3() {
    //        final int batchSize = 199;
    //        int count = 399;
    //        Map<String, Object> options = N.asProps(Options.BATCH_SIZE, batchSize);
    //
    //        // -----------------
    //        List<Map<String, Object>> propsList = createAccountPropsList(count);
    //        String st = propsList.toString();
    //        List<EntityId> entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.MIDDLE_NAME), account.getMiddleName());
    //            assertEquals(propsList.get(i).get(Account.STATUS), account.getStatus());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(0).remove(Account.MIDDLE_NAME);
    //        propsList.get(0).put(Account.STATUS, 2);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.MIDDLE_NAME), account.getMiddleName());
    //            assertEquals(propsList.get(i).get(Account.STATUS), account.getStatus());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(1).remove(Account.MIDDLE_NAME);
    //        propsList.get(1).put(Account.STATUS, 2);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.MIDDLE_NAME), account.getMiddleName());
    //            assertEquals(propsList.get(i).get(Account.STATUS), account.getStatus());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(111).remove(Account.MIDDLE_NAME);
    //        propsList.get(111).put(Account.STATUS, 2);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.MIDDLE_NAME), account.getMiddleName());
    //            assertEquals(propsList.get(i).get(Account.STATUS), account.getStatus());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //
    //        // -----------------
    //        propsList = createAccountPropsList(count);
    //        propsList.get(count - 1).remove(Account.MIDDLE_NAME);
    //        propsList.get(count - 1).put(Account.STATUS, 2);
    //        st = propsList.toString();
    //        entityIds = dbAccess.add(Account.__, propsList, options);
    //        assertEquals(propsList.size(), entityIds.size());
    //        assertEquals(st, propsList.toString());
    //
    //        for (int i = 0; i < count; i++) {
    //            Account account = dbAccess.get(entityIds[i], null, null);
    //            assertEquals(propsList.get(i).get(Account.MIDDLE_NAME), account.getMiddleName());
    //            assertEquals(propsList.get(i).get(Account.STATUS), account.getStatus());
    //        }
    //
    //        dbAccess.delete(Account.__, L.ge(Account.ID, 0), null);
    //    }
    public void testUpdate_1() {
        int count = 5;
        addAccount(Account.class, count);

        // ----------------------------------------------
        int result = dbAccess.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), null, null);
        assertEquals(count, result);

        DataSet dataSet = dbAccess.query(Account.__, null, null);

        for (int i = 0; i < dataSet.size(); i++) {
            assertEquals(UPDATED_FIRST_NAME, dataSet.absolute(i).get(Account.FIRST_NAME));
        }

        // ----------------------------------------------
        result = dbAccess.update(Account.__, N.asProps(Account.MIDDLE_NAME, null), null, null);
        assertEquals(count, result);

        dataSet = dbAccess.query(Account.__, null, null);

        for (int i = 0; i < dataSet.size(); i++) {
            assertNull(dataSet.absolute(i).get(Account.MIDDLE_NAME));
        }

        // ----------------------------------------------
        result = dbAccess.update(Account.__, N.asProps(Account.FIRST_NAME, CF.expr(Account.LAST_NAME)), null, null);
        assertEquals(count, result);

        dataSet = dbAccess.query(Account.__, null, null);

        for (int i = 0; i < dataSet.size(); i++) {
            assertEquals(LAST_NAME + i, dataSet.absolute(i).get(Account.FIRST_NAME));
        }

        // ----------------------------------------------
        try {
            dbAccess.update(Account.__, null, null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        // ----------------------------------------------
        try {
            result = dbAccess.update(Account.__, N.asProps(), null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        // ----------------------------------------------
        try {
            dbAccess.update("", N.asProps(), null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        // ----------------------------------------------
        try {
            dbAccess.update(null, N.asProps(), null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        // ----------------------------------------------
        try {
            dbAccess.update(Account.__, null, null, null);
            fail("should throw NullPointerException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testDelete_1() {
        int count = 5;
        addAccount(Account.class, count);

        // ----------------------------------------------
        int result = dbAccess.delete(Account.__, CF.le(Account.ID, 0), null);
        assertEquals(0, result);

        // ----------------------------------------------
        result = dbAccess.delete(Account.__, null, null);
        assertEquals(count, result);

        // ----------------------------------------------
        try {
            dbAccess.delete("", null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        // ----------------------------------------------
        try {
            dbAccess.delete(null, null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetResultByHandle() {
        addAccountWithContact(Account.class, 5);

        try {
            dbAccess.getResultByHandle(null, null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.getResultByHandle("", null, null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.getResultByHandle(N.uuid(), null, null);
            fail("should throw InvalidResultHandleException");
        } catch (InvalidResultHandleException e) {
        }

        // --------------------------------
        Holder<String> resultHandle = new Holder<String>();
        ;
        Collection<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME);
        DataSet dataSet = dbAccess.query(Account.__, selectPropNames, null, resultHandle, N.asProps(Options.Query.COUNT, 0));
        assertEquals(0, dataSet.size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), null, N.asProps(Options.Query.COUNT, 0));
        assertEquals(0, dataSet.size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), null, N.asProps(Options.Query.OFFSET, 4, Options.Query.COUNT, 3));
        assertEquals(1, dataSet.size());
        assertEquals(3, dataSet.columnNameList().size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), N.asList(Account.FIRST_NAME), N.asProps(Options.Query.OFFSET, 3, Options.Query.COUNT, 3));
        assertEquals(2, dataSet.size());
        assertEquals(1, dataSet.columnNameList().size());

        // --------------------------------
        dataSet = dbAccess.getResultByHandle(resultHandle.value(), N.asList(Account.ID), null);
        assertEquals(5, dataSet.size());
        assertEquals(1, dataSet.columnNameList().size());

        // --------------------------------
        try {
            dbAccess.getResultByHandle(resultHandle.value(), N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME, Account.MIDDLE_NAME), null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        dbAccess.releaseResultHandle(resultHandle.value());

        try {
            dbAccess.getResultByHandle(resultHandle.value(), null, null);
            fail("should throw InvalidResultHandleException");
        } catch (InvalidResultHandleException e) {
        }

        // --------------------------------
        resultHandle = new Holder<String>();
        ;
        dataSet = dbAccess.query(Account.__, selectPropNames, null, resultHandle, N.asProps(Options.Query.HANDLE_LIVE_TIME, 1000));
        assertEquals(5, dataSet.size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), null, N.asProps(Options.Query.COUNT, 0));
        assertEquals(0, dataSet.size());

        N.sleep(2000);

        try {
            dbAccess.getResultByHandle(resultHandle.value(), null, null);
            fail("should throw InvalidResultHandleException");
        } catch (InvalidResultHandleException e) {
        }

        resultHandle = new Holder<String>();
        dataSet = dbAccess.query(Account.__, selectPropNames, null, resultHandle, N.asProps(Options.Query.HANDLE_MAX_IDLE_TIME, 1000));
        assertEquals(5, dataSet.size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), null, N.asProps(Options.Query.COUNT, 0));
        assertEquals(0, dataSet.size());

        N.sleep(3000);

        try {
            dbAccess.getResultByHandle(resultHandle.value(), null, null);
            fail("should throw InvalidResultHandleException");
        } catch (InvalidResultHandleException e) {
        }
    }

    public void testReleaseResultHandle() {
        addAccount(Account.class, 5);

        try {
            dbAccess.releaseResultHandle(null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            dbAccess.releaseResultHandle("");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        dbAccess.releaseResultHandle(N.uuid());

        // --------------------------------
        Holder<String> resultHandle = new Holder<String>();
        ;
        Collection<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME);
        DataSet dataSet = dbAccess.query(Account.__, selectPropNames, null, resultHandle, N.asProps(Options.Query.COUNT, 0));
        assertEquals(0, dataSet.size());

        dbAccess.releaseResultHandle(resultHandle.value());
        dbAccess.releaseResultHandle(N.uuid());

        try {
            dbAccess.getResultByHandle(resultHandle.value(), null, null);
            fail("should throw InvalidResultHandleException");
        } catch (InvalidResultHandleException e) {
        }
    }

    public void testTransaction() {
        addAccount(Account.class, 5);

        // --------------------------------
        String transactionId = null;

        try {
            dbAccess.beginTransaction(null, null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        transactionId = dbAccess.beginTransaction(IsolationLevel.DEFAULT, null);
        dbAccess.endTransaction(transactionId, Action.COMMIT, null);

        transactionId = dbAccess.beginTransaction(IsolationLevel.SERIALIZABLE, null);
        dbAccess.endTransaction(transactionId, Action.ROLLBACK, null);

        try {
            dbAccess.endTransaction(transactionId, Action.COMMIT, null);
            fail("Should throw InvalidTransactionIdException");
        } catch (InvalidTransactionIdException e) {
        }

        try {
            dbAccess.query(Account.__, null, null, null, N.asProps(Options.TRANSACTION_ID, transactionId));
            fail("Should throw InvalidTransactionIdException");
        } catch (InvalidTransactionIdException e) {
        }

        // --------------------------------
        transactionId = dbAccess.beginTransaction(IsolationLevel.DEFAULT, null);

        Map<String, Object> options = N.asProps(Options.TRANSACTION_ID, transactionId);
        dbAccess.query(Account.__, null, null, null, options);
        dbAccess.endTransaction(transactionId, Action.COMMIT, null);

        // --------------------------------
        transactionId = dbAccess.beginTransaction(IsolationLevel.DEFAULT, null);
        options = N.asProps(Options.TRANSACTION_ID, transactionId);

        EntityId entityId = dbAccess.add(Account.__, createAccountProps(), options);
        Account dbAccount = dbAccess.gett(entityId, null, options);
        assertNotNull(dbAccount);
        dbAccess.endTransaction(transactionId, Action.COMMIT, null);
        dbAccount = dbAccess.gett(entityId, null, null);
        assertNotNull(dbAccount);

        // --------------------------------
        transactionId = dbAccess.beginTransaction(IsolationLevel.DEFAULT, null);
        options = N.asProps(Options.TRANSACTION_ID, transactionId);

        entityId = dbAccess.add(Account.__, createAccountProps(), options);
        dbAccount = dbAccess.gett(entityId, null, options);
        assertNotNull(dbAccount);
        dbAccess.endTransaction(transactionId, Action.ROLLBACK, null);
        dbAccount = dbAccess.gett(entityId, null, null);
        assertNull(dbAccount);

        // --------------------------------
        transactionId = dbAccess.beginTransaction(IsolationLevel.DEFAULT, null);
        options = N.asProps(Options.TRANSACTION_ID, transactionId);

        Map<String, Object> props = createAccountProps();

        entityId = dbAccess.add(Account.__, props, options);

        try {
            dbAccess.add(Account.__, props, options);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        dbAccount = dbAccess.gett(entityId, null, options);
        assertNotNull(dbAccount);
        dbAccess.endTransaction(transactionId, Action.COMMIT, null);
        dbAccount = dbAccess.gett(entityId, null, null);
        assertNotNull(dbAccount);

        // --------------------------------
        transactionId = dbAccess.beginTransaction(IsolationLevel.SERIALIZABLE, null);
        options = N.asProps(Options.TRANSACTION_ID, transactionId);

        props = createAccountProps();

        entityId = dbAccess.add(Account.__, props, options);

        try {
            dbAccess.add(Account.__, props, options);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        dbAccount = dbAccess.gett(entityId, null, options);
        assertNotNull(dbAccount);
        dbAccess.endTransaction(transactionId, Action.COMMIT, null);
        dbAccount = dbAccess.gett(entityId, null, null);
        assertNotNull(dbAccount);

        // --------------------------------
        List<Map<String, Object>> propsList = createAccountPropsList(930);
        props = createAccountProps();
        propsList.add(props);
        propsList.add(props);

        DataSet resultSet1 = dbAccess.query(Account.__, null, null);

        try {
            dbAccess.addAll(Account.__, propsList, null);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        DataSet resultSet2 = dbAccess.query(Account.__, null, null);

        assertEquals(resultSet1, resultSet2);
    }

    public void testGetEntityDefinitionFactory() {
        dbAccess.getEntityDefinitionFactory().domainName();
        println(dbAccess.getEntityDefinitionFactory().getEntityNameList());
        assertEquals(12, dbAccess.getEntityDefinitionFactory().getEntityNameList().size());
    }

    @Test
    public void testQuery() {
        addAccountWithContact(Account.class, 5);

        Collection<String> selectPropNames = N.asList(Account.FIRST_NAME, Account.LAST_NAME, AccountContact.ADDRESS);
        dbAccess.query(Account.__, selectPropNames, null).println();
    }

    @Override
    protected String getDomainName() {
        return ExtendDirtyBasicPNL._DN;
    }
}

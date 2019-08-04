/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core.extendDirty.lvc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.core.EntityManagerUtil;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.entity.extendDirty.lvc.AccountContact;
import com.landawn.abacus.entity.extendDirty.lvc.AccountDevice;
import com.landawn.abacus.entity.extendDirty.lvc.AclGroup;
import com.landawn.abacus.entity.extendDirty.lvc.AclUser;
import com.landawn.abacus.entity.extendDirty.lvc.AclUserGroupRelationship;
import com.landawn.abacus.entity.extendDirty.lvc.DataType;
import com.landawn.abacus.entity.extendDirty.lvc.ExtendDirtyLVCPNL;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.impl.MySqlDef;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.types.WeekDay;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public class EntityManagerTest extends AbstractAbacusTest {
    @Test
    public void testCRUD() {
        Account account = em.gett(Seid.of(Account.ID, 1));
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setMiddleName("middle - name...................");
        account.setLastName("lastName...................");
        account.setGUI(UUID.randomUUID().toString());
        em.add(account);

        try {
            em.add(account);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        account = em.gett(Seid.of(Account.ID, account.getId()));

        account = em.gett(Seid.of(Account.ID, account.getId()), N.asList(Account.FIRST_NAME, Account.LAST_NAME));
        N.println(account);
        em.delete(account);
        assertNull(em.gett(Seid.of(Account.ID, account.getId())));

        em.delete(Account.__, CF.gt(Account.ID, 1), null);
    }

    @Test
    public void testIdType() {
        Account account = em.gett(Seid.of(Account.ID, 1));
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setMiddleName("middle - name...................");
        account.setLastName("lastName...................");
        account.setGUI(UUID.randomUUID().toString());
        em.add(account);

        long id = Long.valueOf(String.valueOf(account.getId()));

        account = em.gett(Seid.of(Account.ID, id));
        N.println(account);
        em.delete(account);
        assertNull(em.gett(Seid.of(Account.ID, id)));
    }

    @Test
    public void testCRUD2() {
        Account account = new Account();
        account.setFirstName("firstName");
        account.setLastName("lastName");
        account.setGUI(UUID.randomUUID().toString());
        em.add(account);

        Seid accountId = Seid.of(Account.ID, account.getId());
        Account dbAccount = em.gett(accountId);
        N.println(dbAccount);
        assertEquals(account.getId(), dbAccount.getId());

        dbAccount.setFirstName("updatedFirstName");
        em.update(dbAccount);
        dbAccount = em.gett(accountId);
        N.println(dbAccount);
        assertEquals("updatedFirstName", dbAccount.getFirstName());

        em.delete(dbAccount);
        dbAccount = em.gett(accountId);
        N.println(dbAccount);
        assertNull(dbAccount);
    }

    @Test
    public void testCRUD3() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);
        assertEquals((long) entityId.get(Account.ID), account.getId());
        assertFalse(account.isDirty());

        account.setFirstName(UPDATED_FIRST_NAME);
        em.update(account);
        assertFalse(account.isDirty());

        account.setFirstName(UPDATED_FIRST_NAME);
        assertTrue(account.isDirty());
        em.delete(account);
        assertFalse(account.isDirty());
    }

    @Test
    public void testCRUD4() {
        Account account = createAccount(Account.class);
        account.setContact(createAccountContact(AccountContact.class));

        List<AccountDevice> devices = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            AccountDevice device = new AccountDevice();
            device.setUDID(N.uuid());
            device.setName("mine");
            devices.add(device);
        }

        account.setDevices(devices);

        EntityId entityId = em.add(account);
        assertEquals((long) entityId.get(Account.ID), account.getId());
        assertFalse(account.isDirty());

        List<Account> accounts = em.list(Account.__, Account._PNL, CF.eq(Account.ID, account.getId()), null);
        N.println(accounts);

        DataSet dataSet = em.query(Account.__, Account._PNL, CF.eq(Account.ID, account.getId()), null, N.asProps(Options.Query.COMBINE_PROPERTIES, true));
        dataSet.println();

        Account account2 = dataSet.getRow(Account.class, 0);
        assertEquals(accounts.get(0), account2);

        account.setFirstName(UPDATED_FIRST_NAME);
        em.update(account);
        assertFalse(account.isDirty());

        account.setFirstName(UPDATED_FIRST_NAME);
        assertTrue(account.isDirty());
        em.delete(account);
        assertFalse(account.isDirty());
    }

    @Test
    public void testPropName() throws Exception {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);
        assertEquals((long) entityId.get(Account.ID), account.getId());
        assertFalse(account.isDirty());

        List<Account> accounts1 = em.list(Account.__, null, CF.eq(Account.ID, account.getId()), null);
        List<Account> accounts2 = em.list(Account.__, null, CF.eq(Account.ID, account.getId()), null);
        N.println(accounts1);
        assertEquals(accounts1.get(0), accounts2.get(0));

        account.setFirstName(UPDATED_FIRST_NAME);
        em.update(account);
        assertFalse(account.isDirty());

        account.setFirstName(UPDATED_FIRST_NAME);
        assertTrue(account.isDirty());
        em.delete(account);
        assertFalse(account.isDirty());
    }

    @Test
    public void testCasacadeDelete() {
        Account account = new Account();
        account.setFirstName("firstName...................");
        account.setMiddleName("middle - name...................");
        account.setLastName("lastName...................");
        account.setGUI(UUID.randomUUID().toString());

        AccountContact contact = new AccountContact();
        contact.setCity("sunnyvale");
        contact.setZipCode("94087");
        account.setContact(contact);
        em.add(account);

        account = em.gett(Seid.of(Account.ID, account.getId()), Account._PNL);
        N.println(account);
        assertNotNull(account);
        contact = em.gett(Seid.of(AccountContact.ID, contact.getId()));
        N.println(contact);
        assertNotNull(contact);

        em.delete(account);
        assertNull(em.gett(Seid.of(Account.ID, account.getId())));
        assertNull(em.gett(Seid.of(AccountContact.ID, contact.getId())));
    }

    @Test
    public void testCasacadeDelete_2() {
        AclUser aclUser = createAclUserWithAclGroup(AclUser.class);
        em.add(aclUser);

        aclUser = em.gett(Seid.of(AclUser.ID, aclUser.getId()), AclUser._PNL);
        N.println(aclUser);
        assertNotNull(aclUser);

        List<AclUserGroupRelationship> aclUserGroupRelationships = em.list(AclUserGroupRelationship.__, null,
                CF.eq(AclUserGroupRelationship.USER_GUI, aclUser.getGUI()));
        N.println(aclUserGroupRelationships);
        assertEquals(aclUserGroupRelationships.size(), 1);

        AclGroup aclGroup = aclUser.getGroupList().get(0);
        aclGroup = em.gett(Seid.of(AclGroup.ID, aclGroup.getId()));
        N.println(aclGroup);
        assertNotNull(aclGroup);

        em.delete(aclUser);

        aclUser = em.gett(Seid.of(AclUser.ID, aclUser.getId()), AclUser._PNL);
        N.println(aclUser);
        assertNull(aclUser);

        aclUserGroupRelationships = em.list(AclUserGroupRelationship.__, null,
                CF.eq(AclUserGroupRelationship.USER_GUI, aclUserGroupRelationships.get(0).getUserGUI()));
        N.println(aclUserGroupRelationships);
        assertEquals(aclUserGroupRelationships.size(), 0);

        aclGroup = em.gett(Seid.of(AclGroup.ID, aclGroup.getId()));
        N.println(aclGroup);
        assertNull(aclGroup);
    }

    @Test
    public void testEnumDataType() {
        DataType dataType = new DataType();
        dataType.setEnumType(WeekDay.WEDNESDAY);
        // dataType.setId(100);
        em.add(dataType);

        List<DataType> dbDataType = em.list(DataType.__, N.asList(DataType.ENUM_TYPE), CF.eq(DataType.ENUM_TYPE, WeekDay.WEDNESDAY), null);

        assertEquals(dataType, dbDataType.get(0));

        em.delete(DataType.__, CF.eq(DataType.ENUM_TYPE, WeekDay.WEDNESDAY), null);
    }

    @Test
    public void testSQLExecutor() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long id = (Long) sqlExecutor.insert(MySqlDef.insertAccount, "fn", "ln", UUID.randomUUID().toString(), now, now);

        DataSet result = sqlExecutor.query(MySqlDef.selectAccountById, id);
        N.println(result);

        sqlExecutor.update(MySqlDef.updateAccountFirstNameById, "updated fn", id);
        sqlExecutor.update(MySqlDef.deleteAccountById, id);

        sqlExecutor.update(MySqlDef.deleteAllAccount);

        result = sqlExecutor.query(MySqlDef.selectAccountById, id);
        N.println(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testSQLExecutor2() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long id = (Long) sqlExecutor.insert(MySqlDef.insertAccount, "fn", "ln", UUID.randomUUID().toString(), now, now);

        DataSet result = sqlExecutor.query("select  id   as  id, first_name  as   firstName from account where id=?", id);
        N.println(result);

        sqlExecutor.update(MySqlDef.updateAccountFirstNameById, "updated fn", id);
        sqlExecutor.update(MySqlDef.deleteAccountById, id);

        sqlExecutor.update(MySqlDef.deleteAllAccount);

        result = sqlExecutor.query(MySqlDef.selectAccountById, id);
        N.println(result);
        assertEquals(0, result.size());
    }

    public void testBatchAdd() {
        List<Account> accounts = addAccount(Account.class, 133);
        em.deleteAll(accounts);
    }

    public void testLoadByUID() {
        Account account = this.addAccount(Account.class);
        account = em.gett(Seid.of(Account.ID, account.getId()));

        try {
            em.gett(Seid.of(Account.GUI, account));
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            em.findFirst(Account.class, null, CF.eq(Account.GUI, account.getGUI()));
        }

        N.println(System.currentTimeMillis() - startTime);

        account.setFirstName(UPDATED_FIRST_NAME);
        em.update(account);

        Account account2 = em.findFirst(Account.class, null, CF.eq(Account.GUI, account.getGUI())).get();
        assertEquals(account2.getFirstName(), account.getFirstName());
        startTime = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            em.findFirst(Account.class, null, CF.eq(Account.GUI, account.getGUI()));
        }

        N.println(System.currentTimeMillis() - startTime);

        em.delete(account);
    }

    public void testSelectAll() {
        Account account = this.addAccount(Account.class);

        try {
            em.query(Account.__, N.asList("*"), null).println();
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        em.delete(account);
    }

    public void test_batch() {
        List<Account> accounts = this.addAccount(Account.class, 333);
        EntityDefinition entityDef = em.getEntityDefinitionFactory().getDefinition(Account.__);
        List<EntityId> entityIds = EntityManagerUtil.getEntityIdByEntity(entityDef, accounts);

        List<Account> dbAccounts = em.list(Account.__, null, EntityManagerUtil.entityId2Condition(entityIds));

        assertTrue(N.equals(accounts.size(), dbAccounts.size()));

        int num = em.updateAll(N.asProps(Account.FIRST_NAME, "newFirstName"), entityIds);
        N.println(num);
        assertEquals(accounts.size(), num);

        Account dbAccount2 = em.gett(Seid.of(Account.ID, dbAccounts.get(0).getId()));
        assertEquals("newFirstName", dbAccount2.getFirstName());

        for (Account e : dbAccounts) {
            e.setLastName("newLastName");
        }

        num = em.updateAll(dbAccounts);
        N.println(num);
        assertEquals(accounts.size(), num);

        dbAccount2 = em.gett(Seid.of(Account.ID, dbAccounts.get(0).getId()));
        assertEquals(dbAccount2.getLastName(), dbAccounts.get(0).getLastName());

        em.deleteAll(entityIds);

        em.deleteAll(accounts);
    }

    @Override
    protected String getDomainName() {
        return ExtendDirtyLVCPNL._DN;
    }
}

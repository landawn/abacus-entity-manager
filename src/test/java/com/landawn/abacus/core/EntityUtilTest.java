/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.List;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.lvc.AclUser;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.entity.extendDirty.lvc.AccountContact;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class EntityUtilTest extends AbstractEntityManager1Test {
    final EntityDefinition accountEntityDef = em.getEntityDefinitionFactory().getDefinition(Account.__);
    final EntityDefinition aclUserEntityDef = em.getEntityDefinitionFactory().getDefinition(AclUser.__);

    @Test
    public void test_clone_1() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);
        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        Account copy = EntityUtil.clone(accountEntityDef, account);

        assertEquals(account, copy);

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);
        MapEntity mapEntity2 = EntityUtil.clone(accountEntityDef, mapEntity);
        assertEquals(mapEntity, mapEntity2);

        em.deleteAll(accounts);
    }

    @Test
    public void test_clone_2() {
        List<AclUser> aclUsers = addAclUserWithAclGroup(AclUser.class, 5);
        AclUser aclUser = aclUsers.get(0);
        N.println(aclUser);

        AclUser copy = EntityUtil.clone(aclUserEntityDef, aclUser);

        assertEquals(aclUser, copy);

        MapEntity mapEntity = EntityUtil.disassemble(aclUserEntityDef, aclUser);
        MapEntity mapEntity2 = EntityUtil.clone(aclUserEntityDef, mapEntity);
        assertEquals(mapEntity, mapEntity2);

        em.deleteAll(aclUsers);
    }

    @Test
    public void test_copy() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);
        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        Account copy = EntityUtil.clone(accountEntityDef, account);

        assertEquals(account, copy);

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);
        MapEntity mapEntity2 = EntityUtil.copy(accountEntityDef, mapEntity);
        assertEquals(mapEntity, mapEntity2);

        em.deleteAll(accounts);
    }

    @Test
    public void test_copy_2() {
        List<AclUser> aclUsers = addAclUserWithAclGroup(AclUser.class, 5);
        AclUser aclUser = aclUsers.get(0);

        AclUser copy = EntityUtil.clone(aclUserEntityDef, aclUser);

        assertEquals(aclUser, copy);

        MapEntity mapEntity = EntityUtil.disassemble(aclUserEntityDef, aclUser);
        MapEntity mapEntity2 = EntityUtil.clone(aclUserEntityDef, mapEntity);
        assertEquals(mapEntity, mapEntity2);

        em.deleteAll(aclUsers);
    }

    @Test
    public void test_transfer() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);

        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);

        Account account2 = EntityUtil.transfer(accountEntityDef, account, Account.class);
        assertEquals(account, account2);

        account2 = EntityUtil.transfer(accountEntityDef, mapEntity, Account.class);
        assertEquals(account, account2);

        mapEntity = EntityUtil.transfer(accountEntityDef, account, MapEntity.class);

        MapEntity mapEntity2 = EntityUtil.transfer(accountEntityDef, account, MapEntity.class);
        assertEquals(mapEntity, mapEntity2);

        mapEntity2 = EntityUtil.transfer(accountEntityDef, mapEntity, MapEntity.class);
        assertEquals(mapEntity, mapEntity2);

        em.deleteAll(accounts);
    }

    @Test
    public void test_transfer_2() {
        List<AclUser> aclUsers = addAclUserWithAclGroup(AclUser.class, 5);
        AclUser aclUser = aclUsers.get(0);

        MapEntity mapEntity = EntityUtil.transfer(aclUserEntityDef, aclUser, MapEntity.class);

        AclUser copy = EntityUtil.transfer(aclUserEntityDef, mapEntity, AclUser.class);

        assertEquals(aclUser, copy);

        em.deleteAll(aclUsers);
    }

    @Test
    public void test_assemble() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);

        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);
        Account account2 = EntityUtil.assemble(accountEntityDef, mapEntity, Account.class);

        assertEquals(account, account2);

        em.deleteAll(accounts);
    }

    @Test
    public void test_assemble_2() {
        List<AclUser> aclUsers = addAclUserWithAclGroup(AclUser.class, 5);
        AclUser aclUser = aclUsers.get(0);

        MapEntity mapEntity = EntityUtil.disassemble(aclUserEntityDef, aclUser);

        AclUser copy = EntityUtil.assemble(aclUserEntityDef, mapEntity, AclUser.class);

        assertEquals(aclUser, copy);

        em.deleteAll(aclUsers);
    }

    @Test
    public void test_refesh() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);

        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        EntityUtil.refresh(accountEntityDef, account, accounts.get(1));
        assertEquals(account, accounts.get(1));

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);

        EntityUtil.refresh(accountEntityDef, account, mapEntity);

        N.println(mapEntity);

        MapEntity mapEntity2 = EntityUtil.copy(accountEntityDef, mapEntity);
        mapEntity.set(Account.FIRST_NAME, "newFirstName");

        EntityUtil.refresh(accountEntityDef, mapEntity, account);
        assertEquals(mapEntity.get(Account.FIRST_NAME), account.getFirstName());

        EntityUtil.refresh(accountEntityDef, mapEntity, mapEntity2);
        assertEquals(mapEntity.get(Account.FIRST_NAME), mapEntity2.get(Account.FIRST_NAME));
    }

    @Test
    public void test_merge() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);

        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        EntityUtil.merge(accountEntityDef, account, accounts.get(1), false, false);

        assertFalse(account.equals(accounts.get(1)));

        account.markDirty(true);

        EntityUtil.merge(accountEntityDef, account, accounts.get(1), false, false);
        N.println(account);
        N.println(accounts.get(1));

        assertFalse(account.equals(accounts.get(1)));
        accounts.get(1).setId(account.getId());
        assertTrue(account.equals(accounts.get(1)));

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);

        EntityUtil.merge(accountEntityDef, account, mapEntity, false, false);

        N.println(mapEntity);

        MapEntity mapEntity2 = EntityUtil.copy(accountEntityDef, mapEntity);
        mapEntity.set(Account.FIRST_NAME, "newFirstName");

        EntityUtil.merge(accountEntityDef, mapEntity, account, false, false);
        assertEquals(mapEntity.get(Account.FIRST_NAME), account.getFirstName());

        EntityUtil.merge(accountEntityDef, mapEntity, mapEntity2, false, false);
        assertEquals(mapEntity.get(Account.FIRST_NAME), mapEntity2.get(Account.FIRST_NAME));
    }

    @Test
    public void test_merge_2() {
        List<AclUser> aclUsers = addAclUserWithAclGroup(AclUser.class, 5);
        AclUser aclUser = aclUsers.get(0);
        N.println(aclUser);

        AclUser copy = EntityUtil.clone(aclUserEntityDef, aclUser);

        assertEquals(aclUser, copy);

        aclUser.setName("newUserNmae");
        aclUser.getGroupList().get(0).setName("newGroupName");

        EntityUtil.merge(aclUserEntityDef, aclUser, copy, false, false);
        assertEquals(aclUser.getName(), copy.getName());
        assertEquals(aclUser.getGroupList().get(0).getName(), copy.getGroupList().get(0).getName());

        em.deleteAll(aclUsers);
    }

    @Test
    public void test_merge_3() {
        List<AclUser> aclUsers = addAclUserWithAclGroup(AclUser.class, 5);
        AclUser aclUser = aclUsers.get(0);
        N.println(aclUser);

        com.landawn.abacus.entity.pjo.lvc.AclUser copy = EntityUtil.transfer(aclUserEntityDef, aclUser, com.landawn.abacus.entity.pjo.lvc.AclUser.class);

        com.landawn.abacus.entity.pjo.lvc.AclUser aclUser2 = EntityUtil.transfer(aclUserEntityDef, aclUser, com.landawn.abacus.entity.pjo.lvc.AclUser.class);

        aclUser2.setName("newUserNmae");
        aclUser2.getGroupList().get(0).setName("newGroupName");

        EntityUtil.merge(aclUserEntityDef, aclUser2, copy, false, false);

        assertEquals(aclUser2.getName(), copy.getName());
        assertEquals(aclUser2.getGroupList().get(0).getName(), copy.getGroupList().get(0).getName());

        em.deleteAll(aclUsers);
    }

    @Test
    public void test_toString() {
        List<Account> accounts = addAccount(Account.class, 5);
        Account account = accounts.get(0);

        AccountContact contact = new AccountContact();
        contact.setCity("SunnyVale");
        account.setContact(contact);

        MapEntity mapEntity = EntityUtil.disassemble(accountEntityDef, account);

        EntityUtil.toString(accountEntityDef, account);
        EntityUtil.toString(accountEntityDef, mapEntity);

        EntityUtil.hashCode(accountEntityDef, account);
        EntityUtil.hashCode(accountEntityDef, mapEntity);

        Account account2 = EntityUtil.copy(accountEntityDef, account);
        MapEntity mapEntity2 = EntityUtil.copy(accountEntityDef, mapEntity);
        assertTrue(EntityUtil.equals(accountEntityDef, account, account2));
        assertTrue(EntityUtil.equals(accountEntityDef, mapEntity, mapEntity2));

        em.deleteAll(accounts);
    }
}

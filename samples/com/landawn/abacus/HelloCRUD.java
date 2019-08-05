package com.landawn.abacus;

import java.util.UUID;

import org.junit.Test;

import com.landawn.abacus.core.EntityManagerFactory;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.basic.Account;
import com.landawn.abacus.entity.extendDirty.basic.ExtendDirtyBasicPNL;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.util.EntityManagerEx;
import com.landawn.abacus.util.N;

import junit.framework.TestCase;

public class HelloCRUD extends TestCase {
    // Default configuration file: ./config/abacus-entity-manager.xml
    public static final EntityManagerFactory emf = EntityManagerFactory.getInstance();
    public static final DBAccess dbAccess = emf.getDBAccess(ExtendDirtyBasicPNL._DN);
    public static final EntityManagerEx<Object> em = emf.getEntityManager(ExtendDirtyBasicPNL._DN);

    @Test
    public void test_CRUD() {
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
    }
}

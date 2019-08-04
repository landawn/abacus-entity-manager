/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class PropEntityPropsTest extends AbstractEntityManager1Test {
    @Test
    public void test_01() {
        Account account = createAccount(Account.class);

        Property prop = em.getEntityDefinitionFactory().getDefinition(Account.__).getProperty(Account.__);
        PropEntityProps propEntity = new PropEntityProps(prop, account);
        N.println(propEntity);

        PropEntityProps propEntity2 = new PropEntityProps(prop, account);
        assertTrue(N.asSet(propEntity).contains(propEntity2));
    }

    @Test
    public void test_02() {
        Account account = addAccountWithContact(Account.class);

        N.println(account);

        account = em.gett(Seid.of(Account.ID, account.getId()), Account._PNL);

        N.println(account);
    }
}

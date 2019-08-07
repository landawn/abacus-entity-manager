/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core.implDirty.basic;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.entity.implDirty.basic.Account;
import com.landawn.abacus.entity.implDirty.basic.ImplDirtyBasicPNL;
import com.landawn.abacus.util.N;

import org.junit.Test;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DBAccessTest extends AbstractEntityManager1Test {
    @Test
    public void testCRUD() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);
        println(dbAccess.gett(entityId, null, null));

        dbAccess.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), null).println();
    }

    @Override
    protected String getDomainName() {
        return ImplDirtyBasicPNL._DN;
    }
}

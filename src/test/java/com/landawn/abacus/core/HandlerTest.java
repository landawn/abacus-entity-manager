/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class HandlerTest extends AbstractEntityManager1Test {
    @Test
    public void test_methods() {
        Account account = addAccount(Account.class);
        EntityId entityId = Seid.of(Account.ID, account.getId());
        em.update(entityId, N.asProps(Account.LAST_NAME, "updatedLastName"), null);
        em.updateAll(N.asList(entityId), N.asProps(Account.LAST_NAME, "updatedLastName2"), null);

        em.deleteAll(N.asList(entityId), null);
    }
}

/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class HandlerTest extends AbstractAbacusTest {
    @Test
    public void test_methods() {
        Account account = addAccount(Account.class);
        EntityId entityId = Seid.of(Account.ID, account.getId());
        em.update(N.asProps(Account.LAST_NAME, "updatedLastName"), entityId, null);
        em.updateAll(N.asProps(Account.LAST_NAME, "updatedLastName2"), N.asList(entityId), null);

        em.deleteAll(N.asList(entityId), null);
    }
}

/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.basic.Account;
import com.landawn.abacus.util.DateUtil;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class EntityIdTest extends AbstractEntityManager1Test {
    @Test
    public void test_1() {
        Seid entityId = Seid.of(Account.ID, 1);
        assertEquals(1, entityId.get(Account.ID, int.class).intValue());

        assertEquals(entityId, entityId.copy());

        entityId.set(Account.FIRST_NAME, "firstName");
        entityId.set(Account.LAST_NAME, "lastName");
        entityId.set(Account.BIRTH_DATE, DateUtil.currentDate());
        N.println(entityId);

        entityId.remove(Account.FIRST_NAME);
        entityId.remove(Account.LAST_NAME);

        entityId.set(N.asProps(Account.ID, 2));

        entityId = Seid.of(Account.FIRST_NAME, "firstName", Account.LAST_NAME, "lastName");

        N.println(entityId);
    }
}

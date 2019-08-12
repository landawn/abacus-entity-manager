/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options.Query;
import com.landawn.abacus.util.u.Holder;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class ResultHandleTest extends AbstractEntityManager1Test {
    @Test
    public void test_getFromCache() {
        List<Account> accounts = addAccount(Account.class, 5);

        // --------------------------------
        Map<String, Object> options = N.asProps(Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Query.QUERY_FROM_CACHE, true);
        Holder<String> resultHandle = new Holder<String>();
        ;
        DataSet dataSet = dbAccess.query(Account.__, null, null, resultHandle, options);
        assertEquals(5, dataSet.size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), null, options);
        assertEquals(5, dataSet.size());

        dataSet = dbAccess.getResultByHandle(resultHandle.value(), null, N.asProps(Query.REFRESH_CACHE, true, Query.QUERY_FROM_CACHE, true));
        assertEquals(5, dataSet.size());

        dbAccess.releaseResultHandle(resultHandle.value());

        em.deleteAll(accounts, null);
    }

    @Test
    public void testValueOf() {
        Holder<String> resultHandle = new Holder<String>();
        resultHandle.setValue("b");
        N.println(resultHandle.toString());
        resultHandle.hashCode();

        assertEquals(Holder.of("a"), Holder.of("a"));

        Holder<String> a = new Holder<String>();
        Holder<String> b = new Holder<String>();
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setValue("abc");
        assertFalse(a.equals(b));
        assertFalse(a.equals(new Object()));
    }
}

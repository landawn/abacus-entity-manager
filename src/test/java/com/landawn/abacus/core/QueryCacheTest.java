/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Cache;
import com.landawn.abacus.util.Options.Query;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class QueryCacheTest extends AbstractEntityManager1Test {
    //
    //  int[] resultSizes = { 0, 1, 2, 9, 11, 99, 101, 1023, 1024, 1025, 2099 };
    //  int[] offsets = { 0, 1, 99, 100, 1023, 1024, 1025, 2099 };
    //
    final int[] resultSizes = { 0, 1, 2, 9, 11, 99, 101 };
    final int[] offsets = { 0, 1, 99, 100 };

    public void test_asyncQueryCache_03() {
        addAccounts();

        Criteria cond = CF.criteria().where(CF.gt(Account.ID, 0)).orderBy(Account.FIRST_NAME);
        Map<String, Object> options = N.asProps(Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Query.QUERY_FROM_CACHE, true);
        DataSet rs = em.query(Account.__, null, cond, options);
        assertEquals(0, getCachedPropNames(rs).size());
        rs = em.query(Account.__, null, cond, options);
        assertEquals(10, getCachedPropNames(rs).size());

        em.addAll(N.asList(createAccount(Account.class), createAccount(Account.class)));

        rs = em.query(Account.__, null, cond, options);
        assertEquals(0, getCachedPropNames(rs).size());
        em.delete(Account.__, null, null);
    }

    public void test_asyncQueryCache_01() {
        Criteria cond = CF.criteria().where(CF.gt(Account.ID, 0)).limit(0, 100).orderBy(Account.FIRST_NAME);
        Map<String, Object> options = N.asProps(Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Query.QUERY_FROM_CACHE, true);

        try {
            em.query(Account.__, null, cond, options);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void test_asyncQueryCache_02() {
        addAccounts();

        Map<String, Object> options = N.asProps(Query.OFFSET, 100, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_ASYNC);
        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        N.sleep(1000);
        options = N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true);
        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.LAST_NAME, UPDATED_LAST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(8, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testCRUD_zip() {
        addAccounts();

        Map<String, Object> options = N.asProps(Query.OFFSET, 100, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC);
        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        options = N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true);
        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        N.sleep(3000);

        DataSet result2 = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result2).size());
    }

    public void testCRUD() {
        addAccounts();

        Map<String, Object> options = N.asProps(Query.OFFSET, 100, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC);
        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        options = N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true);
        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.LAST_NAME, UPDATED_LAST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(8, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testCRUD1() {
        addAccounts();

        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC));

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(8, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testCRUD2() {
        addAccounts();

        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null,
                N.asProps(Query.OFFSET, 1000, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC));

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(8, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    // 和主表相关。
    public void testCRUD3() {
        addAccounts();

        Map<String, Object> options = N.asProps(Query.COUNT, 0, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC);

        List<Object> ids = em.query(Account.__, N.asList(Account.ID), CF.ge(Account.ID, 100)).getColumn(0);

        // Condition condition = L.in(Account.ID, L.subQuery(Account.__, N.asList(Account.ID), L.ge(Account.ID, 100)));

        Condition condition = CF.in(Account.ID, ids);

        DataSet result = em.query(Account.__, null, condition, null, options);

        result = em.query(Account.__, null, condition, null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, condition, null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, condition, null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        // assertEquals(0, getCachedPropNames(result).size());

        em.delete(Account.__, CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, condition, null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    //    // 和join表相关
    //    public void testCRUD4() {
    //        addAccounts();
    //
    //        Map<String, Object> options = N.asProps(Query.COUNT, 0, Query.CACHE_RESULT, Query.CACHE_RESULT_IN_ASYNC);
    //        ResultSet result = em.query(Account.__, null, L.lt(Account.ID, 1000), null, options);
    //
    //        options = N.asProps(Query.COUNT, 0, Query.CACHE_RESULT, Query.CACHE_RESULT_IN_SYNC);
    //        result = em.query(Account.__, null, L.ge(Account.ID, 100), null, options);
    //
    //        result = em.query(Account.__, null, L.ge(Account.ID, 100), null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(13, getCachedPropNames(result).size());
    //
    //        em.update(Book.__, N.asProps(Book.NAME, "testBookName"), L.lt(Book.ID, 3), null);
    //
    //        result = em.query(Account.__, null, L.ge(Account.ID, 100), null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(13, getCachedPropNames(result).size());
    //
    //        em.delete(Book.__, L.ge(Book.ID, 4), null);
    //
    //        result = em.query(Account.__, null, L.ge(Account.ID, 100), null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(13, getCachedPropNames(result).size());
    //    }
    //
    //    // 和subquery表相关。
    //    public void testCRUD5() {
    //        addAccounts();
    //
    //        Map<String, Object> options = N.asProps(Query.COUNT, 0, Query.CACHE_RESULT, Query.CACHE_RESULT_IN_SYNC);
    //
    //        Condition condition = L.criteria().where(L.notIn(Account.ID, L.subQuery(N.asList(Book.ID), L.ge(Book.ID, 3))))
    //                               .orderBy(Account.ID);
    //
    //        ResultSet result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition,
    //                null, options);
    //
    //        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition, null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(3, getCachedPropNames(result).size());
    //
    //        em.update(Book.__, N.asProps(Book.NAME, "testBookName"), L.lt(Book.ID, 3), null);
    //
    //        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition, null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(0, getCachedPropNames(result).size());
    //
    //        em.delete(Book.__, L.ge(Book.ID, 4), null);
    //
    //        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition, null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(0, getCachedPropNames(result).size());
    //    }
    //
    //    // 无任何关系。
    //    public void testCRUD6() {
    //        addAccounts();
    //
    //        Map<String, Object> options = N.asProps(Query.COUNT, 0, Query.CACHE_RESULT, Query.CACHE_RESULT_IN_SYNC);
    //
    //        Condition condition = L.in(Account.ID, L.subQuery(N.asList(Account.ID), L.ge(Account.ID, 100)));
    //
    //        ResultSet result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition,
    //                null, options);
    //
    //        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition, null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(3, getCachedPropNames(result).size());
    //
    //        em.update(Book.__, N.asProps(Book.NAME, "testBookName"), L.lt(Book.ID, 3), null);
    //
    //        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition, null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(3, getCachedPropNames(result).size());
    //
    //        em.delete(Book.__, L.ge(Book.ID, 4), null);
    //
    //        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), condition, null,
    //                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.FROM_CACHE, true));
    //
    //        // result.println();
    //        assertEquals(3, getCachedPropNames(result).size());
    //    }
    public void testCRUD7() {
        addAccounts();

        DataSet result = em.query(Account.__, null, CF.like(Account.FIRST_NAME, "first%"), null,
                N.asProps(Query.OFFSET, 100, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC));

        result = em.query(Account.__, null, CF.like(Account.FIRST_NAME, "first%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.like(Account.FIRST_NAME, "fn%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, null, CF.like(Account.FIRST_NAME, "fn%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, N.asProps(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testCRUD8() {
        addAccounts();

        Map<String, Object> options = Options.of(Query.OFFSET, 100, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC);
        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        options = Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true);
        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        N.println(getCachedPropNames(result));
        assertEquals(10, getCachedPropNames(result).size());

        List<EntityId> entityIds = new ArrayList<>();

        for (int i = 0, len = 200; i < len; i++) {
            entityIds.add(Seid.of(Account.ID, i));
        }

        em.updateAll(N.asProps(Account.LAST_NAME, UPDATED_LAST_NAME), entityIds, null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        N.println(getCachedPropNames(result));
        assertEquals(8, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testCRUD9() {
        addAccounts();

        Map<String, Object> options = Options.of(Query.OFFSET, 100, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC);
        DataSet result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        options = Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true);
        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, options);

        // result.println();
        assertEquals(10, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        N.println(getCachedPropNames(result));
        assertEquals(10, getCachedPropNames(result).size());

        List<Account> accounts = new ArrayList<>(100);

        for (int i = 0, len = 100; i < len; i++) {
            Account account = createAccount(Account.class);
            account.setId(i);
            accounts.add(account);
        }

        em.addAll(accounts);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        N.println(getCachedPropNames(result));
        assertEquals(0, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, null, CF.ge(Account.ID, 100), null, Options.of(Query.OFFSET, 9, Query.COUNT, 80, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testUpdateConditionProp() {
        addAccounts();

        Collection<String> selectPropNames = N.asList(Account.ID, Account.BIRTH_DATE);

        DataSet result = em.query(Account.__, selectPropNames, CF.like(Account.FIRST_NAME, "firstN%"), null,
                N.asProps(Query.OFFSET, 80, Query.COUNT, 1000, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC));

        result = em.query(Account.__, selectPropNames, CF.like(Account.FIRST_NAME, "firstN%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 1000, Query.QUERY_FROM_CACHE, true));

        // result.println();
        assertEquals(2, getCachedPropNames(result).size());

        em.update(Account.__, N.asProps(Account.FIRST_NAME, UPDATED_FIRST_NAME), CF.lt(Account.ID, 200), null);

        result = em.query(Account.__, selectPropNames, CF.like(Account.FIRST_NAME, "fn%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 1000, Query.QUERY_FROM_CACHE, true));

        // N.println(result);
        result = em.query(Account.__, selectPropNames, CF.like(Account.FIRST_NAME, "fn%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 1000, Query.QUERY_FROM_CACHE, false));

        // N.println(result);
        assertEquals(0, getCachedPropNames(result).size());

        em.delete(Account.__, CF.gt(Account.ID, 100), null);

        result = em.query(Account.__, selectPropNames, CF.like(Account.FIRST_NAME, "fn%"), null,
                N.asProps(Query.OFFSET, 9, Query.COUNT, 1000, Query.QUERY_FROM_CACHE, false));

        //
        //        result = em.search(Account.__, selectPropNames, Cond.ge(Account.ID, 100), null,
        //                N.asProps(Query.OFFSET, 9, Query.COUNT, 1000, Query.FROM_CACHE, true));

        // result.println();
        assertEquals(0, getCachedPropNames(result).size());
    }

    public void testBigResult_1() {
        for (int resultSize : resultSizes) {
            for (int offset : offsets) {
                Condition cond = CF.ge(Account.ID, 0);
                em.delete(Account.__, cond, null);
                super.addAccount(Account.class, resultSize);

                Map<String, Object> options = N.asProps(Query.OFFSET, offset, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_CONDITION,
                        Cache.condition(0, 1000000), Cache.CACHE_RESULT_RANGE, Cache.range(offset, Math.max(resultSize, offset)));
                DataSet result = em.query(Account.__, null, cond, null, options);

                long startTime = System.currentTimeMillis();
                DataSet result1 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, false);
                    result1 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);
                startTime = System.currentTimeMillis();

                DataSet result2 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, true);
                    result2 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);

                N.println(getCachedPropNames(result2));
                result.println();
                assertEquals(10, getCachedPropNames(result2).size());
                assertEquals(Math.max(0, resultSize - offset), result2.size());
                assertEquals(result, result1);
                assertEquals(result1, result2);
            }
        }
    }

    public void testBigResult_2() {
        for (int resultSize : resultSizes) {
            for (int offset : offsets) {
                Condition cond = CF.ge(Account.ID, 0);
                em.delete(Account.__, cond, null);
                super.addAccount(Account.class, resultSize);

                Map<String, Object> options = N.asProps(Query.OFFSET, offset, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_CONDITION,
                        Cache.condition(0, 1000000), Cache.CACHE_RESULT_RANGE, Cache.range(Math.max(0, resultSize - 1), resultSize));
                DataSet result = em.query(Account.__, null, cond, null, options);

                options = N.asProps(Query.OFFSET, offset, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_CONDITION, Cache.condition(0, 1000000),
                        Cache.CACHE_RESULT_RANGE, Cache.range(offset, Math.max(resultSize, offset)));
                result = em.query(Account.__, null, cond, null, options);

                long startTime = System.currentTimeMillis();
                DataSet result1 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, false);
                    result1 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);
                startTime = System.currentTimeMillis();

                DataSet result2 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, true);
                    result2 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);

                N.println(getCachedPropNames(result2));
                result.println();
                assertEquals(10, getCachedPropNames(result2).size());
                assertEquals(Math.max(0, resultSize - offset), result2.size());
                assertEquals(result, result1);
                assertEquals(result1, result2);
            }
        }
    }

    public void testBigResult_3() {
        for (int resultSize : resultSizes) {
            for (int offset : offsets) {
                Condition cond = CF.ge(Account.ID, 0);
                em.delete(Account.__, cond, null);
                super.addAccount(Account.class, resultSize);

                Map<String, Object> options = N.asProps(Query.OFFSET, offset, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_CONDITION,
                        Cache.condition(0, 1000000), Cache.CACHE_RESULT_RANGE, Cache.range(offset + 1, Math.max(resultSize, offset + 1)));
                DataSet result = em.query(Account.__, null, cond, null, options);

                long startTime = System.currentTimeMillis();
                DataSet result1 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, false);
                    result1 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);
                startTime = System.currentTimeMillis();

                DataSet result2 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, true);
                    result2 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);

                N.println(getCachedPropNames(result2));
                result.println();

                if (resultSize <= offset) {
                    assertEquals(10, getCachedPropNames(result2).size());
                } else {
                    assertEquals(0, getCachedPropNames(result2).size());
                }

                assertEquals(Math.max(0, resultSize - offset), result2.size());
                assertEquals(result, result1);
                assertEquals(result1, result2);
            }
        }
    }

    public void testBigResult_4() {
        for (int resultSize : resultSizes) {
            for (int offset : offsets) {
                Condition cond = CF.ge(Account.ID, 0);
                em.delete(Account.__, cond, null);
                super.addAccount(Account.class, resultSize);

                Map<String, Object> options = N.asProps(Query.OFFSET, offset, Query.CACHE_RESULT, Query.CACHE_RESULT_SYNC, Cache.CACHE_RESULT_CONDITION,
                        Cache.condition(0, 1000000), Cache.CACHE_RESULT_RANGE, Cache.range(offset, Math.max(resultSize - 1, offset)));
                DataSet result = em.query(Account.__, null, cond, null, options);

                long startTime = System.currentTimeMillis();
                DataSet result1 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, false);
                    result1 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);
                startTime = System.currentTimeMillis();

                DataSet result2 = null;

                for (int i = 0; i < 10; i++) {
                    options = N.asProps(Query.OFFSET, offset, Query.QUERY_FROM_CACHE, true);
                    result2 = em.query(Account.__, null, cond, null, options);
                }

                N.println(System.currentTimeMillis() - startTime);

                N.println(getCachedPropNames(result2));
                result.println();

                if (resultSize <= offset) {
                    assertEquals(10, getCachedPropNames(result2).size());
                } else {
                    assertEquals(0, getCachedPropNames(result2).size());
                }

                assertEquals(Math.max(0, resultSize - offset), result2.size());
                assertEquals(result, result1);
                assertEquals(result1, result2);
            }
        }
    }

    private List<Account> addAccounts() {
        List<Account> accountList = new ArrayList<Account>();

        for (int i = 100; i < 300; i++) {
            Account account = createAccount(Account.class);
            account.setId(i);
            accountList.add(account);
        }

        em.addAll(accountList);

        return accountList;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // emf.getDataSourceManager(domainName).getProperties().put(DataSourceConfiguration.SQL_LOG, "true");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // emf.getDataSourceManager(domainName).getProperties().put(DataSourceConfiguration.SQL_LOG, "false");
    }
}

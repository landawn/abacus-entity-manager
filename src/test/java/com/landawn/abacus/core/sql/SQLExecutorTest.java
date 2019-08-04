/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.u.Holder;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Jdbc;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLExecutorTest extends AbstractAbacusTest {
    @Test
    public void test_getResultByHandleInTransaction() {
        List<Account> accounts = addAccount(Account.class, 103);
        Criteria cond = CF.criteria().where(CF.gt(Account.ID, 0)).limit(0, accounts.size()).orderBy(Account.FIRST_NAME);

        Holder<String> resultHandle = new Holder<String>();
        DataSet rs = em.query(Account.__, null, cond, resultHandle, null);
        rs.println();

        DataSet rs2 = em.getResultByHandle(resultHandle.value(), null, null);

        assertEquals(rs, rs2);

        rs2 = em.getResultByHandle(resultHandle.value(), null, N.asProps(Options.TRANSACTION_ID, N.uuid()));
    }

    @Test
    public void test_jdbcOptions() {
        List<Account> accounts = addAccount(Account.class, 103);
        Criteria cond = CF.criteria().where(CF.gt(Account.ID, 0)).limit(0, accounts.size()).orderBy(Account.FIRST_NAME);
        List<Map<String, Object>> optionsList = new ArrayList<>();
        optionsList.add(N.asProps(Jdbc.QUERY_TIMEOUT, 100));
        optionsList.add(N.asProps(Jdbc.FETCH_SIZE, 64));
        optionsList.add(N.asProps(Jdbc.MAX_ROWS, 1024));
        optionsList.add(N.asProps(Jdbc.MAX_FIELD_SIZE, 1024));
        optionsList.add(N.asProps(Jdbc.FETCH_DIRECTION, java.sql.ResultSet.FETCH_FORWARD));
        optionsList.add(N.asProps(Jdbc.RESULT_SET_TYPE, java.sql.ResultSet.TYPE_FORWARD_ONLY));
        optionsList.add(N.asProps(Jdbc.RESULT_SET_CONCURRENCY, java.sql.ResultSet.CONCUR_READ_ONLY));
        optionsList.add(N.asProps(Jdbc.RESULT_SET_HOLDABILITY, java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT));

        for (Map<String, Object> options : optionsList) {
            DataSet rs = em.query(Account.__, null, cond, options);
            assertEquals(accounts.size(), rs.size());
        }

        optionsList = new ArrayList<>();
        optionsList.add(N.asProps(Jdbc.QUERY_TIMEOUT, -1));
        optionsList.add(N.asProps(Jdbc.FETCH_SIZE, -1));
        optionsList.add(N.asProps(Jdbc.MAX_ROWS, -1));
        optionsList.add(N.asProps(Jdbc.MAX_FIELD_SIZE, -1));
        optionsList.add(N.asProps(Jdbc.FETCH_DIRECTION, -1));
        optionsList.add(N.asProps(Jdbc.RESULT_SET_TYPE, -1));
        optionsList.add(N.asProps(Jdbc.RESULT_SET_CONCURRENCY, -1));
        optionsList.add(N.asProps(Jdbc.RESULT_SET_HOLDABILITY, -1));

        for (Map<String, Object> options : optionsList) {
            try {
                em.query(Account.__, null, cond, options);
                fail("should throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
            }
        }

        em.deleteAll(accounts);
    }
}

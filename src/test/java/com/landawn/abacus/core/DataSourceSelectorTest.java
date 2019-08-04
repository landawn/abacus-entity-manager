/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.impl.MyDataSourceSelector;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options.Query;

import org.junit.Test;

import java.util.Map;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DataSourceSelectorTest extends AbstractEntityManager1Test {
    @Test
    public void testSelectNonExistDataSource() {
        Map<String, Object> options = N.asProps(Query.QUERY_WITH_DATA_SOURCE, MyDataSourceSelector.NON_EXIST_DATA_SOURCE);

        Account account = createAccount(Account.class);

        try {
            em.add(account, options);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    @Test
    public void testQueryWithMutlDataSource() {
        for (String dataSourceName : dsm.getActiveDataSources().keySet()) {
            Map<String, Object> options = N.asProps(Query.QUERY_WITH_DATA_SOURCE, dataSourceName);

            N.println(em.query(Account.__, null, null, null, options));
        }
    }
}

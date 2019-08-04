/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.server;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.http.HttpClient;
import com.landawn.abacus.util.Array;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.URLEncodedUtil;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class GetTest extends AbstractEntityManager1Test {
    static final String url = "http://localhost:8080/abacus/extendDirtyBasic";
    static final HttpClient httpClient = HttpClient.create(url);

    @Test
    public void test_01() {
        Account account = addAccount(Account.class);
        String request = "<get><entityId entityName=\"Account\"><id>" + account.getId() + "</id></entityId></get>";
        String response = httpClient.post(request);
        N.println(response);

        response = HttpClient.create(url + "?" + URLEncodedUtil.encode(N.asArray("call", request))).get();
        N.println(response);
    }
}

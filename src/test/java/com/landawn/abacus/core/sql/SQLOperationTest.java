/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core.sql;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.SQLOperation;


/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLOperationTest extends AbstractTest {
    @Test
    public void test_01() {
        N.println(SQLOperation.getOperation(SQLOperation.DELETE.getName()));
        N.println(SQLOperation.getOperation(SQLOperation.DELETE.toString()));
    }
}

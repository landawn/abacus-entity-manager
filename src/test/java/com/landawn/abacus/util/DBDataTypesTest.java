/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DBDataTypesTest extends AbstractEntityManager1Test {
    @Test
    public void test_getMysqlDataType() throws Exception {
        assertEquals("int", DBDataTypeMapper.getMySQLDataType("int"));
        assertEquals("TEXT", DBDataTypeMapper.getMySQLDataType("CLOB"));

        assertEquals("int", DBDataTypeMapper.getPostgreSQLDataType("int"));
        assertEquals("SMALLINT", DBDataTypeMapper.getPostgreSQLDataType("TINYINT"));
    }
}

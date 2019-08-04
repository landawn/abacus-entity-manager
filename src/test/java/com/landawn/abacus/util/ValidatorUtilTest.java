/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import com.landawn.abacus.AbstractEntityManager1Test;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class ValidatorUtilTest extends AbstractEntityManager1Test {

    public void test_01() {
        assertTrue(ValidatorUtil.isValidEmailAddress("70abc._22BB12@gmail.com"));
    }

    public void test_02() {
        assertTrue(ValidatorUtil.isValidPassword("aB~@#$%1^&?"));
        assertTrue(ValidatorUtil.isValidPassword("12A#345abd"));
        assertTrue(ValidatorUtil.isValidPassword("12A345abd1^&?"));
    }
}

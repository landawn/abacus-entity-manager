/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.entity.extendDirty.basic.DataType;
import com.landawn.abacus.exception.ValidationException;
import com.landawn.abacus.util.Array;
import com.landawn.abacus.util.DateUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.validator.AfterValidator;
import com.landawn.abacus.validator.BeforeValidator;
import com.landawn.abacus.validator.EmailValidator;
import com.landawn.abacus.validator.EqualValidator;
import com.landawn.abacus.validator.GreaterEqualValidator;
import com.landawn.abacus.validator.GreaterThanValidator;
import com.landawn.abacus.validator.LessEqualValidator;
import com.landawn.abacus.validator.LessThanValidator;
import com.landawn.abacus.validator.NameValidator;
import com.landawn.abacus.validator.NotEqualValidator;
import com.landawn.abacus.validator.NotNullValidator;
import com.landawn.abacus.validator.NullValidator;
import com.landawn.abacus.validator.Validator;
import com.landawn.abacus.validator.ValidatorFactory;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class ValidatorTest extends AbstractTest {
    @Test
    public void test_ValidatorFactory() {
        ValidatorFactory.registerValidator("myEqualValidator", EqualValidator.class);

        Validator<String> validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "myEqualValidator(abc)");

        assertFalse(validator.isValid("aac"));
        assertTrue(validator.isValid("abc"));
        assertFalse(validator.isValid("abd"));
        assertTrue(validator.isValid(null));

        println(validator.toString());

        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "myEqualValidator(abc, false)");

        assertTrue(validator.isValid("abc"));
        assertTrue(validator.isValid("ABC"));
        assertTrue(validator.isValid(null));

        println(validator.toString());

        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "myEqualValidator(abc, true)");

        assertTrue(validator.isValid("abc"));
        assertFalse(validator.isValid("ABC"));
        assertTrue(validator.isValid(null));

        println(validator.toString());
    }

    @Test
    public void test_Validator() {
        Validator<Integer> validator = ValidatorFactory.create(DataType.INT_TYPE, N.typeOf(Integer.class), "In(1, 3, 5)");
        assertTrue(validator.isValid(1));
        assertFalse(validator.isValid(2));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
        N.println(validator.getPropName());
        N.println(validator.getType());

        assertEquals(1, validator.validate(1).intValue());

        try {
            validator.validate(2);
            fail("Should throw ValidationException");
        } catch (ValidationException e) {
        }
    }

    @Test
    public void test_InValidator() {
        Validator<Integer> validator = ValidatorFactory.create(DataType.INT_TYPE, N.typeOf(Integer.class), "In(1, 3, 5)");
        assertTrue(validator.isValid(1));
        assertFalse(validator.isValid(2));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_NameValidator() {
        NameValidator validator = (NameValidator) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Name");

        assertTrue(validator.isValid("Hannah"));
        assertTrue(validator.isValid(null));

        assertFalse(validator.isValid("\\123s"));
        N.println(validator.toString());
    }

    @Test
    public void test_EmailValidator() {
        EmailValidator validator = (EmailValidator) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Email");

        assertTrue(validator.isValid("Hannah@123.com"));
        assertTrue(validator.isValid(null));

        assertFalse(validator.isValid("123s"));
        N.println(validator.toString());
    }

    @Test
    public void test_NullValidator() {
        NullValidator validator = (NullValidator) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Null");

        assertFalse(validator.isValid(new Object()));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_NotNullValidator() {
        NotNullValidator validator = (NotNullValidator) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "NotNull");

        assertTrue(validator.isValid(new Object()));
        assertFalse(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_LessThanValidator() {
        LessThanValidator<Integer> validator = (LessThanValidator<Integer>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE,
                N.typeOf(Integer.class), "LessThan(10)");

        assertTrue(validator.isValid(9));
        assertFalse(validator.isValid(10));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_LessEqualValidator() {
        LessEqualValidator<String> validator = (LessEqualValidator<String>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class),
                "LessEqual(abc)");

        assertTrue(validator.isValid("abc"));
        assertFalse(validator.isValid("abd"));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_GreaterThanValidator() {
        GreaterThanValidator<Integer> validator = (GreaterThanValidator<Integer>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE,
                N.typeOf(Integer.class), "GreaterThan(10)");

        assertFalse(validator.isValid(9));
        assertTrue(validator.isValid(11));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_GreaterEqualValidator() {
        GreaterEqualValidator<String> validator = (GreaterEqualValidator<String>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE,
                N.typeOf(String.class), "GreaterEqual(abc)");

        assertFalse(validator.isValid("aac"));
        assertTrue(validator.isValid("abc"));
        assertTrue(validator.isValid("abd"));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_BeforeValidator() {
        BeforeValidator<Date> validator = (BeforeValidator<Date>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Date.class),
                "Before(2010-01-01)");

        assertFalse(validator.isValid(DateUtil.currentJUDate()));
        assertTrue(validator.isValid(DateUtil.parseJUDate("2009-01-01")));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_AfterValidator() {
        AfterValidator<Date> validator = (AfterValidator<Date>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Date.class),
                "After(2010-01-01)");

        assertTrue(validator.isValid(DateUtil.currentJUDate()));
        assertFalse(validator.isValid(DateUtil.parseJUDate("2009-01-01")));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());

        validator = (AfterValidator<Date>) (Validator<?>) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Date.class), "After(sysTime)");

        assertFalse(validator.isValid(DateUtil.currentJUDate()));
        assertTrue(validator.isValid(DateUtil.parseJUDate("2050-01-01")));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_NotValidator() {
        Validator<Date> validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Date.class), "!After(2010-01-01)");

        assertFalse(validator.isValid(DateUtil.currentJUDate()));
        assertTrue(validator.isValid(DateUtil.parseJUDate("2009-01-01")));
        assertTrue(validator.isValid(null));

        N.println(validator.toString());
        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Date.class), "!NotNull");

        assertTrue(validator.isValid(null));

        N.println(validator.toString());

        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Date.class), "!Null");

        assertFalse(validator.isValid(null));

        N.println(validator.toString());
    }

    @Test
    public void test_EqualValidator() {
        Validator<String> validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Equal(abc)");

        assertFalse(validator.isValid("aac"));
        assertTrue(validator.isValid("abc"));
        assertFalse(validator.isValid("abd"));
        assertTrue(validator.isValid(null));

        println(validator.toString());

        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Equal(abc, false)");

        assertTrue(validator.isValid("abc"));
        assertTrue(validator.isValid("ABC"));
        assertTrue(validator.isValid(null));

        println(validator.toString());

        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Equal(abc, true)");

        assertTrue(validator.isValid("abc"));
        assertFalse(validator.isValid("ABC"));
        assertTrue(validator.isValid(null));

        println(validator.toString());
    }

    @Test
    public void test_NotEqualValidator() {
        NotEqualValidator<String> validator = (NotEqualValidator) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "NotEqual(abc)");

        assertTrue(validator.isValid("aac"));
        assertFalse(validator.isValid("abc"));
        assertTrue(validator.isValid("abd"));
        assertTrue(validator.isValid(null));

        println(validator.toString());

        validator = (NotEqualValidator) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "NotEqual(abc, false)");

        assertFalse(validator.isValid("abc"));
        assertFalse(validator.isValid("ABC"));
        assertTrue(validator.isValid(null));
        assertTrue(validator.isValid("123"));

        validator = (NotEqualValidator) ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "NotEqual(abc, true)");

        assertFalse(validator.isValid("abc"));
        assertTrue(validator.isValid("ABC"));
        assertTrue(validator.isValid(null));

        N.println(validator.getBenchmark());
    }

    @Test
    public void test_RangeValidator() {
        Validator<Integer> validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(Integer.class), "Range(1, 10)");

        assertTrue(validator.isValid(1));
        assertTrue(validator.isValid(9));
        assertFalse(validator.isValid(10));
        assertFalse(validator.isValid(0));
        assertTrue(validator.isValid(null));

        println(validator.toString());
    }

    @Test
    public void test_SizeValidator() {
        Validator<Object> validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Size(1)");

        assertFalse(validator.isValid(new ArrayList<>()));
        assertTrue(validator.isValid(N.asList(1)));
        assertTrue(validator.isValid(Array.of(1)));
        assertTrue(validator.isValid(new int[1]));
        assertFalse(validator.isValid(new int[0]));
        assertTrue(validator.isValid(null));

        println(validator.toString());

        validator = ValidatorFactory.create(DataType.STRING_TYPE, N.typeOf(String.class), "Size(1, 3)");

        assertFalse(validator.isValid(N.EMPTY_STRING));

        assertTrue(validator.isValid("ab"));
        assertFalse(validator.isValid("abc"));

        assertFalse(validator.isValid(new ArrayList<>()));
        assertTrue(validator.isValid(N.asList(1)));
        assertFalse(validator.isValid(new HashMap<>()));
        assertTrue(validator.isValid(N.asMap(1, 2, 3, 4)));
        assertTrue(validator.isValid(Array.of(1)));
        assertTrue(validator.isValid(N.asList(1, 2)));
        assertTrue(validator.isValid(Array.of(1, 2)));
        assertFalse(validator.isValid(N.asList(1, 2, 3)));
        assertFalse(validator.isValid(Array.of(1, 2, 3)));
        assertTrue(validator.isValid(new int[2]));
        assertFalse(validator.isValid(new int[3]));
        assertFalse(validator.isValid(new int[0]));
        assertTrue(validator.isValid(null));

        println(validator.toString());
    }
}

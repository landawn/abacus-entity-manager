/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.myPackage.y.Account2;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public class CodeGeneratorTest extends AbstractTest {
    static final String dbName = "abacustest";
    static final String srcPath = "./src/test/java/";
    static final boolean sqlLog = false;
    static final boolean reinitEntityDefinition = true;
    static final boolean reinitClass = true;

    @Test
    public void test_type() {
        String typeName = "int[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "int[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "String[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "String[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "Date[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "Date[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "List[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "List[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "ArrayList[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "ArrayList[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "java.util.Date[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "java.util.Date[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "JUDate[]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());

        typeName = "JUDate[][]";
        N.println(N.typeOf(typeName).name());
        N.println(N.typeOf(typeName).clazz());
    }

    @Test
    public void test_writeClassMethod() {
        final File srcDir = new File("./src/test/java");
        final Map<String, String> fieldName2MethodNameMap = N.asMap("gui", "GUI");

        CodeGenerator.writeClassMethod(srcDir, Entity_1.class, true, true, true, null, fieldName2MethodNameMap, null);

        CodeGenerator.writeClassMethod(srcDir, Entity_2.class, true, true, true, null, null, null, null, N.class);

        CodeGenerator.writeClassMethod(srcDir, Entity_3.class, true, true, true, null, null, null, null, Objects.class);
    }

    @Test
    public void test_printTransfer() {
        CodeGenerator.printTransferMethod(Account.class, Account.class);

        CodeGenerator.printTransferMethod(Account.class, Account2.class);
    }

    public static Account account2Account(Account source) {
        final Account result = new Account();
        result.setId(source.getId());
        result.setGUI(source.getGUI());
        result.setEmailAddress(source.getEmailAddress());
        result.setFirstName(source.getFirstName());
        result.setMiddleName(source.getMiddleName());
        result.setLastName(source.getLastName());
        result.setBirthDate(source.getBirthDate());
        result.setStatus(source.getStatus());
        result.setLastUpdateTime(source.getLastUpdateTime());
        result.setCreateTime(source.getCreateTime());
        result.setContact(source.getContact());
        result.setDevices(source.getDevices());
        return result;
    }

    public static Account2 account2Account2(Account source) {
        final Account2 result = new Account2();
        // No set method found for: source.getId()
        // No set method found for: source.getGUI()
        // No set method found for: source.getEmailAddress()
        result.setFirstName(source.getFirstName());
        // No set method found for: source.getMiddleName()
        result.setLastName(source.getLastName());
        // Incompatible parameter type for: source.getBirthDate()
        // No set method found for: source.getStatus()
        // No set method found for: source.getLastUpdateTime()
        // No set method found for: source.getCreateTime()
        // No set method found for: source.getContact()
        // No set method found for: source.getDevices()
        return result;
    }

    //    @Test
    //    public void test_writeUtilClassForHashEqualsToString() {
    //        final File srcDir = new File("./src/test/java");
    //
    //        CodeGenerator.printUtilClassForHashEqualsToString("", "XXX");
    //
    //        CodeGenerator.writeUtilClassForHashEqualsToString(srcDir, this.getClass().getPackage().getName() + ".other", "HashEqualsToStringUtil");
    //    }

    static String propName2MethodName(String propName) {
        if (propName.equals("gui")) {
            return "GUI";
        } else {
            return StringUtil.capitalize(propName);
        }
    }
}

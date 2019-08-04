/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.landawn.abacus.server.GetTest;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.N;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class AllUnitTest extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("All Unit Test");
        final Set<Class<?>> set = new HashSet<>();

        for (int i = 0; i < 1; i++) {
            String pkgName = AbstractTest.class.getPackage().getName();
            boolean isRecursive = true;
            boolean skipClassLoaddingException = true;
            List<Class<?>> clsList = ClassUtil.getClassesByPackage(pkgName, isRecursive, skipClassLoaddingException);

            for (@SuppressWarnings("rawtypes")
            Class cls : clsList) {
                N.println("found class: " + cls.getCanonicalName());

                if (AbstractTest.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                    if ( /* CodeGeneratorTest.class.equals(cls) || */ GetTest.class.equals(cls)) {
                        continue;
                    }

                    //                    if (cls.equals(QueryCacheTest.class) || cls.equals(OffHeapCacheTest.class)) {
                    //                        continue;
                    //                    }

                    //                if (cls.getSimpleName().contains("EntityManager") || cls.getSimpleName().contains("DBAccess")
                    //                        || cls.getSimpleName().contains("QueryCacheTest")) {
                    //                    N.println("### Skip test case: " + cls.getCanonicalName());
                    //                    continue;
                    //                }

                    if (set.add(cls)) {
                        suite.addTestSuite(cls);
                        N.println("Add test case" + cls.getCanonicalName());
                    }
                }
            }
        }

        return suite;
    }
}

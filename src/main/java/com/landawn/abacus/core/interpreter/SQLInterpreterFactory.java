/*
 * Copyright (c) 2015, Haiyang Li.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.core.interpreter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating SQLInterpreter objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class SQLInterpreterFactory {
    /**
     * Field DEFAULT. (value is ""DEFAULT"")
     */
    public static final String BASIC = "DEFAULT";

    /**
     * Field ORACLE. (value is ""ORACLE"")
     */
    public static final String ORACLE = "ORACLE";

    /**
     * Field DB2. (value is ""DB2"")
     */
    public static final String DB2 = "DB2";

    /**
     * Field SQL_SERVER. (value is ""SQL SERVER"")
     */
    public static final String SQL_SERVER = "SQL SERVER";

    /**
     * Field POSTGRESQL. (value is ""POSTGRESQL"")
     */
    public static final String POSTGRESQL = "POSTGRESQL";

    /**
     * Field MYSQL. (value is ""MYSQL"")
     */
    public static final String MYSQL = "MYSQL";

    /**
     * For HSQLDB. (value is ""HSQL"").
     */
    public static final String HSQL = "HSQL";

    /**
     * For H2. (value is ""H2"").
     */
    public static final String H2 = "H2";

    /** The registered interpreters. */
    public static Map<String, Class<?>> registeredInterpreters = new ConcurrentHashMap<>();

    static {
        registerInterpreter(BASIC, SQLInterpreter.class);
        registerInterpreter(MYSQL, MySQLInterpreter.class);
        registerInterpreter(POSTGRESQL, PostgreSQLInterpreter.class);
        registerInterpreter(ORACLE, OracleInterpreter.class);
        registerInterpreter(DB2, DB2Interpreter.class);
        registerInterpreter(SQL_SERVER, SQLServerInterpreter.class);
        registerInterpreter(H2, H2Interpreter.class);
        registerInterpreter(HSQL, HSQLDBInterpreter.class);
    }

    /**
     * Instantiates a new SQL interpreter factory.
     */
    private SQLInterpreterFactory() {
        // singleton.
    }

    /**
     * Gets the interpreter.
     *
     * @param productName
     * @param productVersion
     * @return
     */
    public static Interpreter getInterpreter(String productName, String productVersion) {
        productName = productName.toUpperCase();

        Class<?> clazz = registeredInterpreters.get(productName);

        if (clazz == null) {
            for (String dbProductName : registeredInterpreters.keySet()) {
                if (productName.indexOf(dbProductName) >= 0) {
                    clazz = registeredInterpreters.get(dbProductName);

                    break;
                }
            }
        }

        if (clazz == null) {
            clazz = registeredInterpreters.get(BASIC);
        }

        try {
            return (Interpreter) clazz.getConstructor(String.class, String.class).newInstance(productName, productVersion);
        } catch (Exception e) {
            throw N.toRuntimeException(e);
        }
    }

    /**
     *
     * @param interpreter
     * @param clazz
     */
    public static void registerInterpreter(String interpreter, Class<?> clazz) {
        registeredInterpreters.put(interpreter.toUpperCase(), clazz);
    }
}

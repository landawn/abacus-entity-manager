/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.core.interpreter.DB2Interpreter;
import com.landawn.abacus.core.interpreter.H2Interpreter;
import com.landawn.abacus.core.interpreter.HSQLDBInterpreter;
import com.landawn.abacus.core.interpreter.Interpreter;
import com.landawn.abacus.core.interpreter.MySQLInterpreter;
import com.landawn.abacus.core.interpreter.OracleInterpreter;
import com.landawn.abacus.core.interpreter.PostgreSQLInterpreter;
import com.landawn.abacus.core.interpreter.SQLInterpreter;
import com.landawn.abacus.core.interpreter.SQLInterpreterFactory;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLInterpreterFactoryTest extends AbstractTest {
    @Test
    public void test_01() {
        Interpreter interpreter = SQLInterpreterFactory.getInterpreter("mysql", "5.6");
        assertEquals(MySQLInterpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());

        interpreter = SQLInterpreterFactory.getInterpreter("postgresql", "3.0");
        assertEquals(PostgreSQLInterpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());

        interpreter = SQLInterpreterFactory.getInterpreter("hsqldb", "3.0");
        assertEquals(HSQLDBInterpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());

        interpreter = SQLInterpreterFactory.getInterpreter("oracle", "11");
        assertEquals(OracleInterpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());

        interpreter = SQLInterpreterFactory.getInterpreter("sqlserver", "11");
        assertEquals(SQLInterpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());

        interpreter = SQLInterpreterFactory.getInterpreter("db2", "11");
        assertEquals(DB2Interpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());

        interpreter = SQLInterpreterFactory.getInterpreter("H2", "2");
        assertEquals(H2Interpreter.class.getSimpleName(), interpreter.getClass().getSimpleName());
    }
}

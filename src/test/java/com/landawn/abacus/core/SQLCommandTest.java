/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.core.command.SQLCommandFactory;
import com.landawn.abacus.core.command.SQLCondCommand;
import com.landawn.abacus.core.command.SQLOperationCommand;
import com.landawn.abacus.entity.extendDirty.basic.Account;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.Options.Query;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLCommandTest extends AbstractEntityManager1Test {
    @Test
    public void test_01() {
        SQLCondCommand sqlCondCmd = SQLCommandFactory.createSqlCondCmd();
        N.println(sqlCondCmd.toString());
        sqlCondCmd = SQLCommandFactory.createSqlCondCmd("id = ?");
        sqlCondCmd.setParameter(0, 1, null);
        N.println(sqlCondCmd.toString());

        sqlCondCmd.addTargetTable("abc");
        sqlCondCmd.addTargetTables(N.asList("123"));
        sqlCondCmd.removeTargetTable("abc");
        sqlCondCmd.removeTargetTables(N.asList("abc"));
        sqlCondCmd.clearTargetTable();

        sqlCondCmd.addJoinTable("abc");
        sqlCondCmd.addJoinTables(N.asList("123"));
        sqlCondCmd.removeJoinTable("abc");
        sqlCondCmd.removeJoinTables(N.asList("abc"));
        sqlCondCmd.clearJoinTable();

        sqlCondCmd.addSubQueryTable("abc");
        sqlCondCmd.addSubQueryTables(N.asList("123"));
        sqlCondCmd.removeSubQueryTable("abc");
        sqlCondCmd.removeSubQueryTables(N.asList("abc"));
        sqlCondCmd.clearSubQueryTable();

        N.println(sqlCondCmd.toString());
        sqlCondCmd.clear();
        N.println(sqlCondCmd.toString());

        SQLCondCommand sqlCondCmd2 = SQLCommandFactory.createSqlCondCmd();
        sqlCondCmd.combine(sqlCondCmd2);

        SQLCondCommand copy = (SQLCondCommand) sqlCondCmd.clone();
        N.println(copy.toString());
        assertEquals(sqlCondCmd, copy);

        Set<SQLCondCommand> set = N.asSet(copy);
        assertTrue(set.contains(copy));

        try {
            sqlCondCmd.getOperationType();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            sqlCondCmd.getEntityDef();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            sqlCondCmd.getOptions();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            sqlCondCmd.getParameter("id");
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            sqlCondCmd.getParameterType("id");
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            sqlCondCmd.setParameter("id", null, null);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void test_02() {
        EntityDefinition entityDef = entityDefFactory.getDefinition(Account.__);
        Map<String, Object> options = N.asProps(Query.OFFSET, 2);
        SQLOperationCommand sqlCondCmd = SQLCommandFactory.createSqlCommand(OperationType.ADD, entityDef, options);
        N.println(sqlCondCmd.toString());
        sqlCondCmd = SQLCommandFactory.createSqlCommand(OperationType.ADD, entityDef, "select * from account", options);
        sqlCondCmd.setParameter(0, 1, null);
        N.println(sqlCondCmd.toString());

        sqlCondCmd.addTargetTable("abc");
        sqlCondCmd.addTargetTables(N.asList("123"));
        sqlCondCmd.removeTargetTable("abc");
        sqlCondCmd.removeTargetTables(N.asList("abc"));
        sqlCondCmd.clearTargetTable();

        sqlCondCmd.addJoinTable("abc");
        sqlCondCmd.addJoinTables(N.asList("123"));
        sqlCondCmd.removeJoinTable("abc");
        sqlCondCmd.removeJoinTables(N.asList("abc"));
        sqlCondCmd.clearJoinTable();

        sqlCondCmd.addSubQueryTable("abc");
        sqlCondCmd.addSubQueryTables(N.asList("123"));
        sqlCondCmd.removeSubQueryTable("abc");
        sqlCondCmd.removeSubQueryTables(N.asList("abc"));
        sqlCondCmd.clearSubQueryTable();

        N.println(sqlCondCmd.toString());
        sqlCondCmd.clear();
        N.println(sqlCondCmd.toString());

        SQLCondCommand sqlCondCmd2 = SQLCommandFactory.createSqlCondCmd();
        sqlCondCmd.combine(sqlCondCmd2);

        sqlCondCmd.setParameter(0, 1, null);
        sqlCondCmd.setParameter(1, 2, null);
        sqlCondCmd.addBatch();
        sqlCondCmd.setParameter(0, 3, null);
        sqlCondCmd.setParameter(1, 4, null);
        sqlCondCmd.addBatch();

        N.println(sqlCondCmd.getBatchParameters());

        SQLCondCommand copy = (SQLCondCommand) sqlCondCmd.clone();
        N.println(copy.toString());

        Set<SQLCondCommand> set = N.asSet(copy);
        assertTrue(set.contains(copy));
    }

    @Test
    public void test_03() {
        EntityDefinition entityDef = entityDefFactory.getDefinition(Account.__);
        Map<String, Object> options = N.asProps(Query.OFFSET, 2);
        SQLOperationCommand sqlCondCmd = SQLCommandFactory.createSqlCommand(OperationType.ADD, entityDef, options);
        N.println(sqlCondCmd.toString());
        sqlCondCmd = SQLCommandFactory.createSqlCommand(OperationType.ADD, entityDef, "select * from account", options);
        sqlCondCmd.setParameter(0, 1, null);
        N.println(sqlCondCmd.toString());

        SQLOperationCommand copy = sqlCondCmd.copy();
        assertEquals(sqlCondCmd, copy);

        sqlCondCmd.addBatch();
        copy = sqlCondCmd.copy();
        assertEquals(sqlCondCmd, copy);

        sqlCondCmd.combine(copy);

        N.println(sqlCondCmd);
    }
}

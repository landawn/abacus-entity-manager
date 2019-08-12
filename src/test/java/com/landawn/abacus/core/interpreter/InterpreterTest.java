/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Criteria;
import com.landawn.abacus.core.command.SQLCommandFactory;
import com.landawn.abacus.core.command.SQLCondCommand;
import com.landawn.abacus.entity.extendDirty.basic.Account;
import com.landawn.abacus.entity.extendDirty.basic.AclUser;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;
import com.landawn.abacus.util.SortDirection;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class InterpreterTest extends AbstractEntityManager1Test {
    @Test
    public void test_OracleInterpreter_01() {
        OracleInterpreter interpreter = new OracleInterpreter("oracle", "11.1");
        Criteria criteria = CF.criteria();
        criteria.join(AclUser.__)
                .where(CF.eq(Account.ID, 1))
                .limit(100)
                .groupBy(Account.FIRST_NAME, Account.LAST_NAME)
                .having(CF.gt("count(*)", 5))
                .orderBy(N.asList(Account.BIRTH_DATE), SortDirection.DESC);

        EntityDefinition entityDef = entityDefFactory.getDefinition(Account.__);

        SQLCondCommand sqlCondCmd = SQLCommandFactory.createSqlCondCmd();

        final StringBuilder sql = Objectory.createStringBuilder();

        interpreter.buildCriteria(entityDef, criteria, sqlCondCmd, sql);
        N.println(sql.toString());

        try {
            criteria = CF.criteria().limit(56, 100);
            interpreter.buildCriteria(entityDef, criteria, sqlCondCmd, sql);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        Objectory.recycle(sql);
    }

    @Test
    public void test_OracleInterpreter_02() {
        OracleInterpreter interpreter = new OracleInterpreter("oracle", "11.1");
        EntityDefinition entityDef = entityDefFactory.getDefinition(Account.__);

        List<Map<String, Object>> propsList = new ArrayList<>();
        propsList.add(N.asProps(Account.FIRST_NAME, "firstName1", Account.LAST_NAME, "lastName1"));
        propsList.add(N.asProps(Account.FIRST_NAME, "firstName2", Account.LAST_NAME, "lastName2"));

        SQLCondCommand cmd = interpreter.interpretAdd(entityDef, propsList, null);
        N.println(cmd.getSql());

        String sql = "INSERT INTO account (first_name, last_name) VALUES (?, ?)";
        N.println(sql);
        assertEquals(sql.length(), cmd.getSql().length());

        String str = "INSERT INTO account (first_name, last_name) VALUES (?, ?) {{1=firstName1, 2=lastName1}, {1=firstName2, 2=lastName2}}";
        N.println(cmd.toString());
        assertEquals(str.length(), cmd.toString().length());
    }

    @Test
    public void test_SQLServerInterpreter_01() {
        EntityDefinition entityDef = entityDefFactory.getDefinition(Account.__);
        SQLServerInterpreter interpreter9 = new SQLServerInterpreter("SQLServer", "9.1");

        SQLCondCommand sqlCondCmd = SQLCommandFactory.createSqlCondCmd();
        StringBuilder sql = Objectory.createStringBuilder();
        Criteria criteria = CF.criteria();
        criteria.join(AclUser.__)
                .where(CF.eq(Account.ID, 1))
                .limit(100)
                .groupBy(Account.FIRST_NAME, Account.LAST_NAME)
                .having(CF.gt("count(*)", 5))
                .orderBy(N.asList(Account.BIRTH_DATE), SortDirection.DESC);

        interpreter9.buildCriteria(entityDef, criteria, sqlCondCmd, sql);
        N.println(sql.toString());

        Objectory.recycle(sql);

        SQLServerInterpreter interpreter11 = new SQLServerInterpreter("SQLServer", "11.1");

        sqlCondCmd = SQLCommandFactory.createSqlCondCmd();
        sql = Objectory.createStringBuilder();
        criteria = CF.criteria();
        criteria.join(AclUser.__)
                .where(CF.eq(Account.ID, 1))
                .limit(56, 100)
                .groupBy(Account.FIRST_NAME, Account.LAST_NAME)
                .having(CF.gt("count(*)", 5))
                .orderBy(N.asList(Account.BIRTH_DATE), SortDirection.DESC);
        interpreter11.buildCriteria(entityDef, criteria, sqlCondCmd, sql);
        N.println(sql.toString());

        Objectory.recycle(sql);

        sqlCondCmd = SQLCommandFactory.createSqlCondCmd();
        sql = Objectory.createStringBuilder();

        N.println(sql.toString());
        Objectory.recycle(sql);
    }
}

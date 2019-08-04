/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.condition.Expression.Expr;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.entity.extendDirty.lvc.AccountContact;
import com.landawn.abacus.entity.extendDirty.lvc.Login;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.SortDirection;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public class ConditionTest extends AbstractEntityManager1Test {

    @Test
    public void test_Junction() {
        Or or = CF.or();

        or.set(CF.eq(Account.ID, 1));
        assertEquals("((Account.id = 1))", or.toString());

        or.remove(CF.eq(Account.ID, 1));
        assertEquals("", or.toString());

        or.set(N.asList(CF.eq(Account.ID, 1)));
        assertEquals("((Account.id = 1))", or.toString());

        or.remove(N.asList(CF.eq(Account.ID, 1)));
        assertEquals("", or.toString());

    }

    @Test
    public void test_Criteria() {
        Criteria criteria = CF.criteria();

        Condition where = CF.where(CF.eq(Account.ID, 2));

        criteria.where(where);
        CriteriaUtil.remove(criteria, Operator.WHERE);
        assertTrue(criteria.getWhere() == null);

        criteria.where(where);
        CriteriaUtil.remove(criteria, where);
        assertTrue(criteria.getWhere() == null);

        criteria.where(where);
        CriteriaUtil.remove(criteria, N.asList(where));
        assertTrue(criteria.getWhere() == null);

        criteria.where(where);
        criteria.clear();
        assertTrue(criteria.getWhere() == null);

        criteria.distinct(true);

        criteria.join(Account.__, CF.eq(Account.ID, Account.LAST_NAME));
        criteria.join(N.asList(Account.__), CF.eq(Account.ID, Account.LAST_NAME));

        criteria.where("id = 1");

        Map<String, SortDirection> map = N.asMap(Account.ID, CF.ASC);

        criteria.groupBy(CF.expr(Account.ID));
        criteria.groupBy(Account.ID, CF.ASC);
        criteria.groupBy(N.asList(Account.ID), CF.ASC);
        criteria.groupBy(map);

        criteria.orderBy(CF.expr(Account.ID));
        criteria.orderBy(Account.ID, CF.ASC);
        criteria.orderBy(N.asList(Account.ID), CF.ASC);
        criteria.orderBy(map);

        criteria.intersect(CF.subQuery(Account.__, "select * from account"));
        criteria.except(CF.subQuery(Account.__, "select * from account"));
        criteria.minus(CF.subQuery(Account.__, "select * from account"));

        N.println(criteria.toString());

        String str = " DISTINCT JOIN Account Account.id = 'Account.lastName' JOIN Account Account.id = 'Account.lastName' WHERE id = 1 GROUP BY Account.id ASC INTERSECT select * from account EXCEPT select * from account MINUS select * from account ORDER BY Account.id ASC";

        assertEquals(str, criteria.toString());
    }

    @Test
    public void test_clearParameters() {
        Condition cond = CF.between(Account.ID, 0, 3);
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.criteria().where(CF.between(Account.ID, CF.expr("0"), CF.expr("3")));
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.or(CF.between(Account.ID, 0, 3), CF.eq(Account.FIRST_NAME, "firstName"));
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.subQuery(Account.__, "select * from account");
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.subQuery(Account.__, Account._PNL, CF.eq(Account.ID, 1));
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.eq(Account.ID, 1).not();
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.join(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME)));
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        cond = CF.limit(1, 10);
        assertTrue(N.asSet(cond).contains(cond.copy()));

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());

        N.println(cond.toString());
        cond.clearParameters();
        N.println(cond.toString());
    }

    @Test
    public void test_AbstractCondition() {
        String str = "(Account.id, Account.gui, Account.emailAddress, Account.firstName, Account.middleName, Account.lastName, Account.birthDate, Account.status, Account.lastUpdateTime, Account.createTime, Account.contact, Account.devices)";
        assertEquals(str, AbstractCondition.concatPropNames(Account._PNL));
        assertEquals(str, AbstractCondition.concatPropNames(Account._PNL.toArray(new String[0])));

        assertEquals("", AbstractCondition.concatPropNames(new String[] {}));
        assertEquals("", AbstractCondition.concatPropNames(new ArrayList<String>()));

        assertEquals("Account.id", AbstractCondition.concatPropNames(N.asArray("Account.id")));
        assertEquals("Account.id", AbstractCondition.concatPropNames(N.asList("Account.id")));

        assertEquals("(Account.id, Account.GUI)", AbstractCondition.concatPropNames(N.asArray("Account.id", "Account.GUI")));
        assertEquals("(Account.id, Account.GUI)", AbstractCondition.concatPropNames(N.asList("Account.id", "Account.GUI")));

        assertEquals("(Account.id, Account.GUI, Account.emailAddress)",
                AbstractCondition.concatPropNames(N.asArray("Account.id", "Account.GUI", "Account.emailAddress")));
        assertEquals("(Account.id, Account.GUI, Account.emailAddress)",
                AbstractCondition.concatPropNames(N.asList("Account.id", "Account.GUI", "Account.emailAddress")));

        assertEquals("((Account.id = 1) AND (Account.id <> 2))", (CF.eq(Account.ID, 1).and(CF.ne(Account.ID, 2))).toString());

    }

    @Test
    public void test_Expression() {
        assertEquals("Account.id LIKE '%abc'", Expr.like(Account.ID, "%abc"));

        assertEquals("Account.id = 111", Expr.eq(Account.ID, 111));
        assertEquals("Account.firstName = 'abc'", Expr.eq(Account.FIRST_NAME, "abc"));
        assertEquals("Account.id BETWEEN (1, 3)", Expr.between(Account.ID, 1, 3));
        assertEquals("first_name BETWEEN ('aa', 'bb')", Expr.between("first_name", "aa", "bb"));

        assertEquals("Account.id = 111 OR Account.firstName = 'abc'", Expr.or(Expr.eq(Account.ID, 111), Expr.eq(Account.FIRST_NAME, "abc")));
        assertEquals("Account.id = 111 AND Account.firstName = 'abc'", Expr.and(Expr.eq(Account.ID, 111), Expr.eq(Account.FIRST_NAME, "abc")));

        assertEquals("Account.id = 111", Expr.equal(Account.ID, 111));
        assertEquals("Account.id = 111", Expr.eq(Account.ID, 111));

        assertEquals("Account.id <> 111", Expr.notEqual(Account.ID, 111));
        assertEquals("Account.id <> 111", Expr.ne(Account.ID, 111));

        assertEquals("Account.id > 111", Expr.greaterThan(Account.ID, 111));
        assertEquals("Account.id > 111", Expr.gt(Account.ID, 111));

        assertEquals("Account.id >= 111", Expr.greaterEqual(Account.ID, 111));
        assertEquals("Account.id >= 111", Expr.ge(Account.ID, 111));

        assertEquals("Account.id < 111", Expr.lessThan(Account.ID, 111));
        assertEquals("Account.id < 111", Expr.lt(Account.ID, 111));

        assertEquals("Account.id <= 111", Expr.lessEqual(Account.ID, 111));
        assertEquals("Account.id <= 111", Expr.le(Account.ID, 111));

        assertEquals("Account.id BETWEEN (1, 3)", Expr.between(Account.ID, 1, 3));
        assertEquals("first_name BETWEEN ('aa', 'bb')", Expr.bt("first_name", "aa", "bb"));

        assertEquals("Account.firstName IS NULL", Expr.isNull(Account.FIRST_NAME));
        assertEquals("Account.firstName IS NOT NULL", Expr.isNotNull(Account.FIRST_NAME));
        assertEquals("Account.firstName IS BLANK", Expr.isEmpty(Account.FIRST_NAME));
        assertEquals("Account.firstName IS NOT BLANK", Expr.isNotEmpty(Account.FIRST_NAME));

        assertEquals("1 + 3 + 'aa'", Expr.plus(1, 3, "aa"));
        assertEquals("1 - 3 - 'aa'", Expr.minus(1, 3, "aa"));
        assertEquals("1 * 3 * 'aa'", Expr.multi(1, 3, "aa"));
        assertEquals("1 / 3 / 'aa'", Expr.division(1, 3, "aa"));
        assertEquals("1 % 3 % 'aa'", Expr.modulus(1, 3, "aa"));
        assertEquals("1 << 3 << 'aa'", Expr.lShift(1, 3, "aa"));
        assertEquals("1 >> 3 >> 'aa'", Expr.rShift(1, 3, "aa"));
        assertEquals("1 & 3 & 'aa'", Expr.bitwiseAnd(1, 3, "aa"));
        assertEquals("1 | 3 | 'aa'", Expr.bitwiseOr(1, 3, "aa"));
        assertEquals("1 ^ 3 ^ 'aa'", Expr.bitwiseXOr(1, 3, "aa"));

        assertEquals("COUNT(Account.id)", Expr.count(Account.ID));
        assertEquals("AVG(Account.id)", Expr.average(Account.ID));
        assertEquals("SUM(Account.id)", Expr.sum(Account.ID));
        assertEquals("MIN(Account.id)", Expr.min(Account.ID));
        assertEquals("MAX(Account.id)", Expr.max(Account.ID));
        assertEquals("ABS(12)", Expr.abs("12"));
        assertEquals("ACOS(12)", Expr.acos("12"));
        assertEquals("ASIN(12)", Expr.asin("12"));
        assertEquals("ATAN(12)", Expr.atan("12"));
        assertEquals("CEIL(12)", Expr.ceil("12"));
        assertEquals("COS(12)", Expr.cos("12"));
        assertEquals("EXP(12)", Expr.exp("12"));
        assertEquals("FLOOR(12)", Expr.floor("12"));
        assertEquals("LOG(12, 2)", Expr.log("12", "2"));
        assertEquals("LN(12)", Expr.ln("12"));
        assertEquals("MOD(12, 2)", Expr.mod("12", "2"));
        assertEquals("POWER(12, 2)", Expr.power("12", "2"));
        assertEquals("SIGN(12)", Expr.sign("12"));
        assertEquals("SIN(12)", Expr.sin("12"));
        assertEquals("SQRT(12)", Expr.sqrt("12"));
        assertEquals("TAN(12)", Expr.tan("12"));
        assertEquals("CONCAT(Account.id, 'abc')", Expr.concat(Account.ID, "'abc'"));
        assertEquals("REPLACE(Account.id, 'a', 'c')", Expr.replace(Account.ID, "'a'", "'c'"));
        assertEquals("LENGTH(Account.id)", Expr.stringLength(Account.ID));
        assertEquals("SUBSTR(Account.id, 10)", Expr.subString(Account.ID, 10));
        assertEquals("TRIM(Account.id)", Expr.trim(Account.ID));
        assertEquals("LTRIM(Account.id)", Expr.lTrim(Account.ID));
        assertEquals("RTRIM(Account.id)", Expr.rTrim(Account.ID));
        assertEquals("LPAD(Account.id, 20, 'abc')", Expr.lPad(Account.ID, 20, "'abc'"));
        assertEquals("RPAD(Account.id, 20, 'abc')", Expr.rPad(Account.ID, 20, "'abc'"));
        assertEquals("LOWER(Account.id)", Expr.lower(Account.ID));
        assertEquals("UPPER(Account.id)", Expr.upper(Account.ID));
    }

    @Test
    public void test_01() {
        N.println(CF.or(N.asList(CF.eq(Account.ID, 0), CF.eq(Account.ID, 1))));
        N.println(CF.and(N.asList(CF.eq(Account.ID, 0), CF.eq(Account.ID, 1))));
        N.println(CF.namedProperty(Account.ID));
        N.println(CF.namedProperty(Account.ID));

        NamedProperty np = CF.namedProperty(Account.ID);
        N.println(np.bt(1, 3));
        N.println(np.contains('1'));
        N.println(np.endsWith("1"));
        N.println(np.eq("1"));
        N.println(np.eqOr(N.asList(1, 2)));
        N.println(np.eqOr(1, 2, 3));
        N.println(np.eq("a"));
        N.println(np.ge(2));
        N.println(np.gt(2));
        N.println(np.hashCode());
        N.println(np.in(1, 2, 3));
        N.println(np.in(N.asList(1, 2, 3)));
        N.println(np.isNotNull());
        N.println(np.isNull());
        N.println(np.le(1));
        N.println(np.like("a"));
        N.println(np.lt("a"));
        N.println(np.ne("a"));
        N.println(np.startsWith("a"));
        N.println(NamedProperty.of(Account.ID));

        N.println(CF.binary(Account.ID, Operator.EQUAL, 2));
        N.println(CF.equal(Account.ID, 2));
        N.println(CF.eqOr(Account.ID, 2, 3, 4));
        N.println(CF.eqOr(Account.ID, N.asList(2, 3, 4)));
        N.println(CF.eqOr(N.asProps(Account.ID, 1, Account.FIRST_NAME, "firstName")));
        N.println(CF.eqOr(new Account()));
        N.println(CF.eqAnd(N.asProps(Account.ID, 1, Account.FIRST_NAME, "firstName")));
        N.println(CF.eqAnd(new Account()));

        N.println(CF.notEqual(Account.ID, 2));
        N.println(CF.greaterThan(Account.ID, 2));
        N.println(CF.greaterEqual(Account.ID, 2));
        N.println(CF.lessThan(Account.ID, 2));
        N.println(CF.lessEqual(Account.ID, 2));
        N.println(CF.isNaN(Account.ID));
        N.println(CF.isNotNaN(Account.ID));
        N.println(CF.isInfinite(Account.ID));
        N.println(CF.isNotInfinite(Account.ID));
        N.println(CF.is(Account.ID, 1));
        N.println(CF.isNot(Account.ID, 1));
        N.println(CF.is(Account.ID, 1));
        N.println(CF.xor(Account.ID, 1));
        N.println(CF.is(Account.ID, 1));
        N.println(CF.bt(Account.ID, 1, 3));
        N.println(CF.junction(Operator.OR, CF.eq(Account.ID, 0), CF.eq(Account.ID, 1)));
        N.println(CF.junction(Operator.OR, N.asList(CF.eq(Account.ID, 0), CF.eq(Account.ID, 1))));

        N.println(CF.groupBy(Account.ID));
        N.println(CF.groupBy(Account.ID, Account.FIRST_NAME));

        N.println(CF.groupBy(Account.ID, SortDirection.ASC));
        N.println(CF.groupBy(N.asList(Account.ID, Account.FIRST_NAME), SortDirection.ASC));

        Map<String, SortDirection> orders = N.asMap(Account.ID, SortDirection.ASC, Account.FIRST_NAME, SortDirection.DESC);
        N.println(CF.groupBy(orders));
        N.println(CF.groupBy(CF.expr("ID DESC")));

        N.println(CF.having(CF.eq("count(*)", 1)));
        N.println(CF.having("count(*)=1"));

        N.println(CF.orderBy(Account.ID));
        N.println(CF.orderBy(Account.ID, Account.FIRST_NAME));

        N.println(CF.orderBy(Account.ID, SortDirection.ASC));
        N.println(CF.orderBy(N.asList(Account.ID, Account.FIRST_NAME), SortDirection.ASC));

        orders = N.asMap(Account.ID, SortDirection.ASC, Account.FIRST_NAME, SortDirection.DESC);
        N.println(CF.orderBy(orders));
        N.println(CF.orderBy(CF.expr("ID DESC")));

        N.println(CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME)));

        N.println(CF.using(Account.ID, Account.FIRST_NAME));
        N.println(CF.using(N.asList(Account.ID, Account.FIRST_NAME)));

        N.println(CF.join(Account.__));
        N.println(CF.join(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.join(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.leftJoin(Account.__));
        N.println(CF.leftJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.leftJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.rightJoin(Account.__));
        N.println(CF.rightJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.rightJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.crossJoin(Account.__));
        N.println(CF.crossJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.crossJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.fullJoin(Account.__));
        N.println(CF.fullJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.fullJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.innerJoin(Account.__));
        N.println(CF.innerJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.innerJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.naturalJoin(Account.__));
        N.println(CF.naturalJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.naturalJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.in(Account.ID, new Integer[] { 1, 2, 3 }));
        N.println(CF.fullJoin(Account.__, CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));
        N.println(CF.fullJoin(N.asList(Account.__), CF.on(CF.eq(Account.FIRST_NAME, Account.LAST_NAME))));

        N.println(CF.subQuery(Account.__, "select * from account"));
        N.println(CF.subQuery(Account.__, N.asList(Account.ID, Account.FIRST_NAME), "id = 1"));
        N.println(CF.subQuery(Account.__, N.asList(Account.ID, Account.FIRST_NAME), CF.eq(Account.ID, 1)));

        N.println(CF.all(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.any(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.some(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.exists(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.except(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.intersect(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.union(CF.subQuery(Account.__, "select * from account")));
        N.println(CF.unionAll(CF.subQuery(Account.__, "select * from account")));

        N.println(CF.limit(0));
    }

    public void testIn() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);

        //    Condition condition = in(Account.ID, subQuery(Account.__, N.asList(Account.ID), eq(Account.FIRST_NAME, account.getFirstName())));
        //    DataSet result = em.query(Account.__, null, condition);
        //    assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        Condition condition = CF.in(Account.ID, new Long[] { account.getId(), account.getId() + 1 });
        DataSet result = em.query(Account.__, null, condition);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        condition = CF.in(Account.ID, N.asList(account.getId()));
        result = em.query(Account.__, null, condition);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        condition = CF.in(Account.ID, N.asList(account.getId(), account.getId() + 1));
        result = em.query(Account.__, null, condition);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        condition = CF.in(Account.FIRST_NAME, N.asArray(account.getFirstName()));
        result = em.query(Account.__, null, condition);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        condition = CF.in(Account.FIRST_NAME, N.asList(account.getFirstName(), account.getFirstName() + 1));
        result = em.query(Account.__, null, condition);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        em.delete(entityId);
    }

    public void testOrderBy() {
        List<Account> accounts = addAccount(Account.class, 5);

        DataSet result = em.query(Account.__, null, CF.orderBy(N.asList(Account.FIRST_NAME, Account.ID), SortDirection.ASC));
        assertEquals("firstName0", result.get(Account.FIRST_NAME));

        result = em.query(Account.__, null, CF.orderBy(N.asList(Account.FIRST_NAME), SortDirection.DESC));
        assertEquals("firstName4", result.get(Account.FIRST_NAME));

        em.deleteAll(accounts);
    }

    public void testGroupBy() {
        List<Account> accounts = addAccount(Account.class, 5);

        DataSet result = em.query(Account.__, N.asList(Expr.count(Account.ID)), CF.groupBy(Account.MIDDLE_NAME), null, null);
        assertEquals(5L, (long) result.get(Expr.count(Account.ID)));

        result = em.query(Account.__, N.asList(Expr.count(Account.ID)), CF.groupBy(Account.FIRST_NAME), null, null);
        assertEquals(5, result.size());

        em.deleteAll(accounts);
    }

    public void testHaving() {
        List<Account> accounts = addAccount(Account.class, 5);

        DataSet result = em.query(Account.__, N.asList(Expr.sum(Account.ID), Account.FIRST_NAME, Account.LAST_NAME),
                CF.criteria().groupBy(Account.ID, Account.FIRST_NAME, Account.LAST_NAME).having(CF.ge(Account.ID, 0)), null, null);
        assertEquals(5, result.size());

        em.deleteAll(accounts);
    }

    public void testJoin() {
        List<Account> accounts = addAccountWithContact(Account.class, 5);

        Join joinCond = CF.leftJoin(AccountContact.__, CF.on(AccountContact.ACCOUNT_ID, Account.ID));
        Criteria criteria = CF.criteria().join(joinCond);
        List<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME);
        selectPropNames.addAll(AccountContact._PNL);

        DataSet result = em.query(Account.__, selectPropNames, criteria);
        result.println();
        assertEquals(19, result.columnNameList().size());
        assertEquals(5, result.size());

        em.deleteAll(accounts);
    }

    public void testJoin_2() {
        List<Account> accounts = addAccountWithContact(Account.class, 5);

        Criteria criteria = CF.criteria();
        Join joinCond = CF.leftJoin(AccountContact.__, CF.on(AccountContact.ACCOUNT_ID, Account.ID));
        criteria.join(joinCond);
        joinCond = CF.leftJoin(Login.__, CF.on(Login.ACCOUNT_ID, Account.ID));
        criteria.join(joinCond);

        List<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME);
        selectPropNames.addAll(AccountContact._PNL);
        selectPropNames.addAll(Login._PNL);

        DataSet result = em.query(Account.__, selectPropNames, criteria);
        result.println();
        assertEquals(26, result.columnNameList().size());
        assertEquals(5, result.size());

        em.deleteAll(accounts);
    }

    public void testJoin_3() {
        List<Account> accounts = addAccountWithContact(Account.class, 5);

        Criteria criteria = CF.criteria();
        List<String> list = N.asList(AccountContact.__, Login.__);
        Map<String, String> m = N.asMap(Login.ACCOUNT_ID, Account.ID, AccountContact.ACCOUNT_ID, Account.ID);
        Join joinCond = CF.join(list, CF.on(m));
        criteria.join(joinCond);

        List<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME);
        selectPropNames.addAll(AccountContact._PNL);
        selectPropNames.addAll(Login._PNL);

        DataSet result = em.query(Account.__, selectPropNames, criteria);
        result.println();
        assertEquals(26, result.columnNameList().size());
        assertEquals(0, result.size());

        em.deleteAll(accounts);
    }

    public void testInnerJoin() {
        List<Account> accounts = addAccountWithContact(Account.class, 5);

        Join joinCond = CF.innerJoin(AccountContact.__, CF.on(AccountContact.ACCOUNT_ID, Account.ID));
        Criteria criteria = CF.criteria().join(joinCond);
        List<String> selectPropNames = N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME);
        selectPropNames.addAll(AccountContact._PNL);

        DataSet result = em.query(Account.__, selectPropNames, criteria);
        assertEquals(19, result.columnNameList().size());
        assertEquals(5, result.size());

        em.deleteAll(accounts);
    }

    public void testUnion() {
        List<Account> accounts = addAccount(Account.class, 10);

        Criteria criteria = CF.criteria().where(CF.gt(Account.ID, 0)).union(
                CF.subQuery(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), CF.gt(Account.FIRST_NAME, "firstName5")));
        DataSet result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), criteria, null, null);
        println(result);
        assertEquals(10, result.size());

        criteria = CF.criteria().where(CF.gt(Account.ID, 0)).unionAll(
                CF.subQuery(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), CF.gt(Account.FIRST_NAME, "firstName5")));
        result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), criteria, null, null);
        println(result);
        assertEquals(14, result.size());

        em.deleteAll(accounts);
    }

    public void estIntersect() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);
        Account dbAccount = em.gett(entityId);

        dbAccount.setFirstName("updatedfn");
        em.update(dbAccount);

        Criteria criteria = CF.criteria()
                .where(CF.gt(Account.ID, 0))
                .intersect(CF.subQuery(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), CF.eq(Account.ID, 5)));
        println(criteria.toString());

        DataSet result = em.query(Account.__, N.asList(Account.ID, Account.FIRST_NAME, Account.LAST_NAME), criteria, null, null);
        N.println(result);
        assertEquals(6, result.size());

        dbAccount = em.gett(entityId);
        assertEquals("updatedfn", dbAccount.getFirstName());

        em.delete(entityId);
        dbAccount = em.gett(entityId);
        assertEquals(null, dbAccount);
    }

    public void testNamedProperty() {
        Account account = addAccount(Account.class);
        NamedProperty np = NamedProperty.of(Account.FIRST_NAME);
        DataSet result = em.query(Account.__, null, np.startsWith("first"), null, null);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        result = em.query(Account.__, null, np.endsWith("Name"), null, null);
        assertEquals(account.getFirstName(), result.get(Account.FIRST_NAME));

        em.delete(account);
    }

    public void testLimit() {
        List<Account> accounts = addAccount(Account.class, 5);

        DataSet result = em.query(Account.__, N.asList(Account.FIRST_NAME, Account.LAST_NAME),
                CF.criteria().where(CF.gt(Account.ID, 0).or(CF.lt(Account.ID, 0))).orderBy(Account.ID).limit(0, 3));
        assertEquals(3, result.size());

        em.deleteAll(accounts);
    }

    public void testPropertyValue() {
        Account account = addAccount(Account.class);

        List<Account> dbAccounts = em.list(Account.__, null, CF.lt(Account.FIRST_NAME, CF.expr(Account.LAST_NAME)), null);
        assertEquals(1, dbAccounts.size());

        dbAccounts = em.list(Account.__, null, CF.gt(Account.FIRST_NAME, CF.expr(Account.LAST_NAME)), null);
        assertEquals(0, dbAccounts.size());

        em.delete(account);
    }

    public void testNullPropertyValue() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);

        //    Condition condition = in(Account.ID, subQuery(Account.__, N.asList(Account.ID), eq(Account.FIRST_NAME, null)));
        //    DataSet result = em.query(Account.__, null, condition);
        //    assertEquals(0, result.size());

        Condition condition = CF.lt(Account.ID, null);
        DataSet result = em.query(Account.__, null, condition);
        assertEquals(0, result.size());

        condition = CF.between(Account.ID, 0, account.getId());
        result = em.query(Account.__, null, condition);
        assertEquals(1, result.size());

        condition = CF.between(Account.FIRST_NAME, null, null);
        result = em.query(Account.__, null, condition);
        assertEquals(0, result.size());

        condition = CF.between(Account.FIRST_NAME, null, account.getFirstName());
        result = em.query(Account.__, null, condition);
        assertEquals(0, result.size());

        condition = CF.between(Account.FIRST_NAME, account.getFirstName(), null);
        result = em.query(Account.__, null, condition);
        assertEquals(0, result.size());

        condition = CF.between(Account.FIRST_NAME, account.getFirstName(), account.getFirstName());
        result = em.query(Account.__, null, condition);
        assertEquals(1, result.size());

        em.delete(entityId);
    }

    public void testCopy() {
        Between cond = CF.between(Account.ID, 1, 2);
        Between copy = cond.copy();
        assertEquals((int) copy.getMinValue(), 1);
        assertEquals((int) copy.getMaxValue(), 2);
        assertEquals(cond, copy);

        cond = CF.between(Account.ID, CF.expr("ab"), CF.expr("bc"));
        copy = cond.copy();
        assertTrue(cond.getMinValue().equals(copy.getMinValue()));
        assertTrue(cond.getMaxValue().equals(copy.getMaxValue()));
        assertEquals(cond.getMaxValue().toString(), copy.getMaxValue().toString());
        assertTrue(cond.equals(copy));

        assertFalse(cond.getMinValue() == copy.getMinValue());
        assertFalse(cond.getMaxValue() == copy.getMaxValue());
    }

    public void testCopy_2() {
        Join cond = CF.join(N.asList(Account.__), null);

        Join copy = cond.copy();

        assertEquals(cond, copy);

        cond.getJoinEntities().clear();

        assertEquals(0, cond.getJoinEntities().size());

        assertEquals(1, copy.getJoinEntities().size());
    }

    public void testStringCondition() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);

        DataSet dataSet = em.query(Account.__, null, CF.expr("firstName=" + account.getFirstName()), null);
        dataSet.println();

        dataSet = em.query(Account.__, null, CF.where("firstName=" + account.getFirstName()), null);
        dataSet.println();

        try {
            em.query(Account.__, null, CF.where("firstname=" + account.getFirstName()), null);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        em.delete(entityId);
    }

    public void testSimplePropName() {
        Account account = createAccount(Account.class);
        EntityId entityId = em.add(account);

        DataSet dataSet = em.query(Account.__, null, CF.eq("firstName", account.getFirstName()), null);
        dataSet.println();

        em.delete(entityId);
    }
}

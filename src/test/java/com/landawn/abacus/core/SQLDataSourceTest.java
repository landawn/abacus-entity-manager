/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import static com.landawn.abacus.dataSource.DataSourceConfiguration.C3P0;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.DBCP;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.DRIVER;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.EVICT_DELAY;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.INITIAL_SIZE;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.JNDI_CONTEXT_FACTORY;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.JNDI_NAME;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.JNDI_PROVIDER_URL;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.LIVE_TIME;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.MAX_ACTIVE;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.MAX_IDLE;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.MAX_IDLE_TIME;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.MAX_OPEN_PREPARED_STATEMENTS_PER_CONNECTION;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.MAX_WAIT_TIME;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.MIN_IDLE;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.PASSWORD;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.PERF_LOG;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.PROVIDER;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.SQL_LOG;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.TEST_ON_BORROW;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.TEST_ON_RETURN;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.URL;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.USER;
import static com.landawn.abacus.dataSource.DataSourceConfiguration.VALIDATION_QUERY;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.dataSource.ConnectionManager;
import com.landawn.abacus.dataSource.SQLDataSource;
import com.landawn.abacus.exception.UncheckedException;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLDataSourceTest extends AbstractTest {
    static final String driver = "com.mysql.jdbc.Driver";
    static final String url = "jdbc:mysql://localhost:3306/abacustest";
    static final String user = "root";
    static final String password = "admin";

    @Test
    public void test_01() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put(DRIVER, driver);
        props.put(URL, url);
        props.put(USER, user);
        props.put(PASSWORD, password);

        SQLDataSource dataSource = new SQLDataSource(props);

        N.println(dataSource.getDatabaseProductName());
        N.println(dataSource.getDatabaseProductVersion());

        try {
            dataSource.getConnection(user, password);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.getLogWriter();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.setLogWriter(null);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.getLoginTimeout();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.setLoginTimeout(1000);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.unwrap(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            dataSource.isWrapperFor(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            dataSource.getParentLogger();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        assertEquals(32, dataSource.getMaxActive());
        assertEquals(1, dataSource.getCurrentActive());

        SQLDataSource dataSource2 = new SQLDataSource(props);
        Set<SQLDataSource> set = N.asSet(dataSource);
        assertTrue(set.contains(dataSource2));

        N.println(dataSource);

        dataSource.close();
        dataSource2.close();
    }

    @Test
    public void test_02() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put(DRIVER, driver);
        props.put(URL, url);
        props.put(USER, user);
        props.put(PASSWORD, password);
        props.put(INITIAL_SIZE, "1");
        props.put(MIN_IDLE, "16");
        props.put(MAX_IDLE, "16");
        props.put(MAX_ACTIVE, "32");
        props.put(MAX_OPEN_PREPARED_STATEMENTS_PER_CONNECTION, "200");
        props.put(LIVE_TIME, "3600000");
        props.put(MAX_IDLE_TIME, "3600000");
        props.put(MAX_WAIT_TIME, "3000");
        props.put(EVICT_DELAY, "1000");
        props.put(VALIDATION_QUERY, "select * from dual");
        props.put(TEST_ON_BORROW, "true");
        props.put(TEST_ON_RETURN, "true");
        props.put(SQL_LOG, "false");
        props.put(PERF_LOG, "3000");

        SQLDataSource dataSource = new SQLDataSource(props);

        SQLDataSource dataSource2 = new SQLDataSource(props);
        Set<SQLDataSource> set = N.asSet(dataSource);
        assertTrue(set.contains(dataSource2));

        N.println(dataSource);

        Connection conn = dataSource.getConnection();
        conn.close();
        conn = dataSource.getPersistentConnection();
        conn.close();

        N.println(dataSource.getProperties());
        N.println(dataSource.getMaxActive());
        N.println(dataSource.getCurrentActive());

        Field field = SQLDataSource.class.getDeclaredField("connectionManager");
        field.setAccessible(true);

        ConnectionManager connectionManager = (ConnectionManager) field.get(dataSource);
        ConnectionManager connectionManager2 = (ConnectionManager) field.get(dataSource2);
        Set<ConnectionManager> tmp = N.asSet(connectionManager);
        assertTrue(tmp.contains(connectionManager2));
        N.println(connectionManager.toString());
        N.println(connectionManager.getProperties());
        N.println(connectionManager.getConnectionProperties());

        conn = dataSource.getConnection();
        connectionManager.closeConnection(conn);
        conn = dataSource.getConnection();
        connectionManager.detroyConnection(conn);

        dataSource.close();
        dataSource2.close();
    }

    @Test
    public void test_03() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put(JNDI_NAME, "test");
        props.put(JNDI_CONTEXT_FACTORY, "test");
        props.put(JNDI_PROVIDER_URL, "test");

        try {
            SQLDataSource dataSource = new SQLDataSource(props);

            N.println(dataSource);
            fail("Should throw AbacusSQLException");
        } catch (UncheckedException e) {
        }
    }

    @Test
    public void test_dbcp() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put(DRIVER, driver);
        props.put(URL, url);
        props.put(USER, user);
        props.put(PASSWORD, password);
        props.put(INITIAL_SIZE, "1");
        props.put(MIN_IDLE, "16");
        props.put(MAX_IDLE, "16");
        props.put(MAX_ACTIVE, "32");
        props.put(MAX_OPEN_PREPARED_STATEMENTS_PER_CONNECTION, "200");
        props.put(LIVE_TIME, "3600000");
        props.put(MAX_IDLE_TIME, "3600000");
        props.put(MAX_WAIT_TIME, "3000");
        props.put(EVICT_DELAY, "1000");
        props.put(TEST_ON_BORROW, "true");
        props.put(TEST_ON_RETURN, "true");
        props.put(SQL_LOG, "false");
        props.put(PERF_LOG, "3000");
        props.put(PROVIDER, DBCP);

        SQLDataSource dataSource = new SQLDataSource(props);

        Connection conn = dataSource.getConnection();
        conn.close();
        conn = dataSource.getPersistentConnection();
        conn.close();

        SQLDataSource dataSource2 = new SQLDataSource(props);
        Set<SQLDataSource> set = N.asSet(dataSource);
        assertTrue(set.contains(dataSource2));

        N.println(dataSource);

        N.println(dataSource.getProperties());
        N.println(dataSource.getMaxActive());
        N.println(dataSource.getCurrentActive());

        Field field = SQLDataSource.class.getDeclaredField("connectionManager");
        field.setAccessible(true);

        ConnectionManager connectionManager = (ConnectionManager) field.get(dataSource);
        ConnectionManager connectionManager2 = (ConnectionManager) field.get(dataSource2);
        Set<ConnectionManager> tmp = N.asSet(connectionManager);
        assertTrue(tmp.contains(connectionManager2));
        N.println(connectionManager.toString());

        conn = dataSource.getConnection();
        connectionManager.closeConnection(conn);
        conn = dataSource.getConnection();
        connectionManager.detroyConnection(conn);

        try {
            dataSource.getConnection(user, password);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.getLogWriter();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.setLogWriter(null);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.getLoginTimeout();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.setLoginTimeout(1000);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.unwrap(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            dataSource.isWrapperFor(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            dataSource.getParentLogger();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        dataSource.close();
        dataSource2.close();
    }

    @Test
    public void test_c3p0() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put(DRIVER, driver);
        props.put(URL, url);
        props.put(USER, user);
        props.put(PASSWORD, password);
        props.put(INITIAL_SIZE, "1");
        props.put(MIN_IDLE, "16");
        props.put(MAX_IDLE, "16");
        props.put(MAX_ACTIVE, "32");
        props.put(MAX_OPEN_PREPARED_STATEMENTS_PER_CONNECTION, "200");
        props.put(LIVE_TIME, "3600000");
        props.put(MAX_IDLE_TIME, "3600000");
        props.put(MAX_WAIT_TIME, "3000");
        props.put(EVICT_DELAY, "1000");
        props.put(VALIDATION_QUERY, "select 1");
        props.put(TEST_ON_BORROW, "true");
        props.put(TEST_ON_RETURN, "true");
        props.put(SQL_LOG, "false");
        props.put(PERF_LOG, "3000");
        props.put(PROVIDER, C3P0);

        SQLDataSource dataSource = new SQLDataSource(props);

        Connection conn = dataSource.getConnection();
        conn.close();
        conn = dataSource.getPersistentConnection();
        conn.close();

        SQLDataSource dataSource2 = new SQLDataSource(props);
        Set<SQLDataSource> set = N.asSet(dataSource);
        assertTrue(set.contains(dataSource2));

        N.println(dataSource);

        N.println(dataSource.getProperties());

        N.println(dataSource.getMaxActive());
        N.println(dataSource.getCurrentActive());

        Field field = SQLDataSource.class.getDeclaredField("connectionManager");
        field.setAccessible(true);

        ConnectionManager connectionManager = (ConnectionManager) field.get(dataSource);
        ConnectionManager connectionManager2 = (ConnectionManager) field.get(dataSource2);
        Set<ConnectionManager> tmp = N.asSet(connectionManager);
        assertTrue(tmp.contains(connectionManager2));
        N.println(connectionManager.toString());

        conn = dataSource.getConnection();
        connectionManager.closeConnection(conn);
        conn = dataSource.getConnection();
        connectionManager.detroyConnection(conn);

        try {
            dataSource.getConnection(user, password);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.getLogWriter();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.setLogWriter(null);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.getLoginTimeout();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.setLoginTimeout(1000);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        try {
            dataSource.unwrap(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            dataSource.isWrapperFor(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            dataSource.getParentLogger();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }

        dataSource.close();
        dataSource2.close();
    }
}

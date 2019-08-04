/*
 * Copyright (C) 2015 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus.metadata.sql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.metadata.Database;
import com.landawn.abacus.metadata.EntityDefXmlEle.DatabaseEle;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.TableEle;
import com.landawn.abacus.metadata.Table;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.ImmutableMap;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.XMLUtil;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLDatabase implements Database {
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String TABLE = "TABLE";

    // private static final String REF_GENERATION = "REF_GENERATION";
    // private static final String USER = "USER";
    private static final String ORACLE = "ORACLE";
    private static final String BIN$ = "BIN$";

    private final String name;
    private final Map<String, String> attrs;
    private final Map<String, SQLTable> tableMap;

    public SQLDatabase(Connection conn, String dbName) {
        this(conn, dbName, null);
    }

    public SQLDatabase(Connection conn, String dbName, Collection<String> selectTableNames) {
        this(conn, dbName, null, null, null, selectTableNames);
    }

    public SQLDatabase(Connection conn, String dbName, String schemaPattern, String tableNamePattern, String[] types) {
        this(conn, dbName, schemaPattern, tableNamePattern, types, null);
    }

    SQLDatabase(Connection conn, String dbName, String schemaPattern, String tableNamePattern, String[] types, Collection<String> selectTableNames) {
        this(parse(conn, dbName, schemaPattern, tableNamePattern, types, selectTableNames));
    }

    public SQLDatabase(InputStream is) throws SAXException, IOException {
        this(Configuration.parse(is).getDocumentElement());
    }

    public SQLDatabase(Element databaseNode) {
        this(parse(databaseNode));
    }

    SQLDatabase(Object[] attrsTableMap) {
        this((Map<String, String>) attrsTableMap[0], (Map<String, SQLTable>) attrsTableMap[1]);
    }

    SQLDatabase(Map<String, String> attrs, Map<String, SQLTable> tableMap) {
        this.name = NameUtil.getCachedName(attrs.get(TableEle.NAME));
        attrs.put(TableEle.NAME, name);
        this.attrs = ImmutableMap.of(attrs);

        this.tableMap = ImmutableMap.of(tableMap);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProductName() {
        return attrs.get(DatabaseEle.PRODUCT_NAME);
    }

    @Override
    public String getProductVersion() {
        return attrs.get(DatabaseEle.PRODUCT_VERSTION);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Table> getTableList() {
        return (Collection) tableMap.values();
    }

    @Override
    public Collection<String> getTableNameList() {
        return tableMap.keySet();
    }

    @Override
    public Table getTable(String tableName) {
        return tableMap.get(tableName);
    }

    @Override
    public Map<String, String> getAttributes() {
        return attrs;
    }

    @Override
    public String getAttribute(String attrName) {
        return attrs.get(attrName);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLDatabase && ((SQLDatabase) obj).name.equals(name));
    }

    @Override
    public String toString() {
        return attrs.toString();
    }

    private static Object[] parse(Connection conn, String dbName, String schemaPattern, String tableNamePattern, String[] types,
            Collection<String> selectTableNames) {
        final Map<String, String> attrs = new LinkedHashMap<>();
        final Map<String, SQLTable> tableMap = new LinkedHashMap<>();

        List<String> tableNameList = new ArrayList<>();
        ResultSet rs = null;

        try {
            final DatabaseMetaData dmd = conn.getMetaData();

            attrs.put(DatabaseEle.NAME, dbName);
            attrs.put(DatabaseEle.PRODUCT_NAME, dmd.getDatabaseProductName());
            attrs.put(DatabaseEle.PRODUCT_VERSTION, dmd.getDatabaseProductVersion());

            if (types == null) {
                types = new String[] { TABLE };
            }

            rs = dmd.getTables(null, schemaPattern, tableNamePattern, types);

            while (rs.next()) {
                // if ((rs.getString(REF_GENERATION) != null) &&
                // !USER.equals(rs.getString(REF_GENERATION))) {
                // continue;
                // }
                String tableName = rs.getString(TABLE_NAME);

                // skip the tables in recycle bin for Oracle.
                if (tableName.toUpperCase().startsWith(BIN$) && dmd.getDatabaseProductName().toUpperCase().contains(ORACLE)) {
                    continue;
                }

                if (selectTableNames == null) {
                    tableNameList.add(tableName);
                } else {
                    for (String selectTableName : selectTableNames) {
                        if (tableName.equalsIgnoreCase(selectTableName) || tableName.matches(selectTableName)) {
                            tableNameList.add(tableName);

                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            JdbcUtil.closeQuietly(rs);
        }

        for (int i = 0; i < tableNameList.size(); i++) {
            SQLTable table = new SQLTable(tableNameList.get(i), conn);
            tableMap.put(table.getName(), table);
        }

        return new Object[] { attrs, tableMap };
    }

    private static Object[] parse(Element databaseNode) {
        final Map<String, String> attrs = XMLUtil.readAttributes(databaseNode);
        final Map<String, SQLTable> tableMap = new LinkedHashMap<>();

        final List<Element> tableElementList = XMLUtil.getElementsByTagName(databaseNode, TableEle.TABLE);
        for (Element tableElement : tableElementList) {
            SQLTable table = new SQLTable(tableElement);
            tableMap.put(table.getName(), table);
        }

        return new Object[] { attrs, tableMap };
    }
}

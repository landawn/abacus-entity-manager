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

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLDatabase implements Database {

    /** The Constant TABLE_NAME. */
    private static final String TABLE_NAME = "TABLE_NAME";

    /** The Constant TABLE. */
    private static final String TABLE = "TABLE";

    // private static final String REF_GENERATION = "REF_GENERATION";
    /** The Constant ORACLE. */
    // private static final String USER = "USER";
    private static final String ORACLE = "ORACLE";

    /** The Constant BIN$. */
    private static final String BIN$ = "BIN$";

    /** The name. */
    private final String name;

    /** The attrs. */
    private final ImmutableMap<String, String> attrs;

    /** The table map. */
    private final ImmutableMap<String, SQLTable> tableMap;

    /**
     * Instantiates a new SQL database.
     *
     * @param conn
     * @param dbName
     */
    public SQLDatabase(Connection conn, String dbName) {
        this(conn, dbName, null);
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param conn
     * @param dbName
     * @param selectTableNames
     */
    public SQLDatabase(Connection conn, String dbName, Collection<String> selectTableNames) {
        this(conn, dbName, null, null, null, selectTableNames);
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param conn
     * @param dbName
     * @param schemaPattern
     * @param tableNamePattern
     * @param types
     */
    public SQLDatabase(Connection conn, String dbName, String schemaPattern, String tableNamePattern, String[] types) {
        this(conn, dbName, schemaPattern, tableNamePattern, types, null);
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param conn
     * @param dbName
     * @param schemaPattern
     * @param tableNamePattern
     * @param types
     * @param selectTableNames
     */
    SQLDatabase(Connection conn, String dbName, String schemaPattern, String tableNamePattern, String[] types, Collection<String> selectTableNames) {
        this(parse(conn, dbName, schemaPattern, tableNamePattern, types, selectTableNames));
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param is
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public SQLDatabase(InputStream is) throws SAXException, IOException {
        this(Configuration.parse(is).getDocumentElement());
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param databaseNode
     */
    public SQLDatabase(Element databaseNode) {
        this(parse(databaseNode));
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param attrsTableMap
     */
    SQLDatabase(Object[] attrsTableMap) {
        this((Map<String, String>) attrsTableMap[0], (Map<String, SQLTable>) attrsTableMap[1]);
    }

    /**
     * Instantiates a new SQL database.
     *
     * @param attrs
     * @param tableMap
     */
    SQLDatabase(Map<String, String> attrs, Map<String, SQLTable> tableMap) {
        this.name = NameUtil.getCachedName(attrs.get(TableEle.NAME));
        attrs.put(TableEle.NAME, name);
        this.attrs = ImmutableMap.of(attrs);

        this.tableMap = ImmutableMap.of(tableMap);
    }

    /**
     * Gets the name.
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the product name.
     *
     * @return
     */
    @Override
    public String getProductName() {
        return attrs.get(DatabaseEle.PRODUCT_NAME);
    }

    /**
     * Gets the product version.
     *
     * @return
     */
    @Override
    public String getProductVersion() {
        return attrs.get(DatabaseEle.PRODUCT_VERSTION);
    }

    /**
     * Gets the table list.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Table> getTableList() {
        return (Collection) tableMap.values();
    }

    /**
     * Gets the table name list.
     *
     * @return
     */
    @Override
    public Collection<String> getTableNameList() {
        return tableMap.keySet();
    }

    /**
     * Gets the table.
     *
     * @param tableName
     * @return
     */
    @Override
    public Table getTable(String tableName) {
        return tableMap.get(tableName);
    }

    /**
     * Gets the attributes.
     *
     * @return
     */
    @Override
    public Map<String, String> getAttributes() {
        return attrs;
    }

    /**
     * Gets the attribute.
     *
     * @param attrName
     * @return
     */
    @Override
    public String getAttribute(String attrName) {
        return attrs.get(attrName);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLDatabase && ((SQLDatabase) obj).name.equals(name));
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return attrs.toString();
    }

    /**
     *
     * @param conn
     * @param dbName
     * @param schemaPattern
     * @param tableNamePattern
     * @param types
     * @param selectTableNames
     * @return
     */
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

    /**
     *
     * @param databaseNode
     * @return
     */
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

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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.metadata.Column;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.TableEle;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.TableEle.ColumnEle;
import com.landawn.abacus.metadata.Table;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.ImmutableMap;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.WD;
import com.landawn.abacus.util.XMLUtil;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLTable implements Table {

    protected static final Logger logger = LoggerFactory.getLogger(SQLTable.class);

    private static final String COLUMN_NAME = "COLUMN_NAME";

    private static final String PRIMARY_KEY_GOT_SQL = "SELECT cols.column_name FROM all_constraints cons, all_cons_columns cols WHERE cols.table_name = ? AND cons.constraint_type = 'P' AND cons.constraint_name = cols.constraint_name";

    private static final String ORACLE = "ORACLE";

    private static final String NUMBER = "NUMBER";

    private static final String SMALLINT = "SMALLINT";

    private static final String TINYINT = "TINYINT";

    private final String name;

    private final Map<String, String> attrs;

    private final Map<String, SQLColumn> columnMap;

    private final Map<String, SQLColumn> columnPool = new HashMap<>();

    public SQLTable(String name, Connection conn) {
        this(parse(name, conn));
    }

    public SQLTable(InputStream is) {
        this(Configuration.parse(is).getDocumentElement());
    }

    public SQLTable(Element tableNode) {
        this(parse(tableNode));
    }

    SQLTable(Object[] attrsColumnMap) {
        this((Map<String, String>) attrsColumnMap[0], (Map<String, SQLColumn>) attrsColumnMap[1]);
    }

    SQLTable(Map<String, String> attrs, Map<String, SQLColumn> columnMap) {
        this.name = NameUtil.getCachedName(attrs.get(TableEle.NAME));
        attrs.put(TableEle.NAME, name);
        this.attrs = ImmutableMap.of(attrs);

        for (SQLColumn column : columnMap.values()) {
            column.setTable(this);

            columnPool.put(column.getName(), column);
            columnPool.put(column.getCanonicalName(), column);
        }

        this.columnMap = ImmutableMap.of(columnMap);
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
     * Gets the column list.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Column> getColumnList() {
        return (Collection) columnMap.values();
    }

    /**
     * Gets the column name list.
     *
     * @return
     */
    @Override
    public Collection<String> getColumnNameList() {
        return columnMap.keySet();
    }

    /**
     * Gets the column.
     *
     * @param columnName
     * @return
     */
    @Override
    public Column getColumn(String columnName) {
        return columnPool.get(columnName);
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
        return this == obj || (obj instanceof SQLTable && ((SQLTable) obj).name.equals(name));
    }

    @Override
    public String toString() {
        return attrs.toString();
    }

    /**
     * Column type 2 java type.
     *
     * @param columnTypeName
     * @param columnClassName
     * @param precision
     * @param scale
     * @return
     */
    public static String columnType2JavaType(String columnTypeName, String columnClassName, int precision, int scale) {
        String javaType = null;

        if (NUMBER.equalsIgnoreCase(columnTypeName)) {
            if (scale == 0) {
                if (precision == 1) {
                    javaType = Boolean.class.getCanonicalName();
                } else if (precision <= 3) {
                    javaType = Byte.class.getCanonicalName();
                } else if (precision <= 5) {
                    javaType = Short.class.getCanonicalName();
                } else if (precision <= 10) {
                    javaType = Integer.class.getCanonicalName();
                } else if (precision <= 19) {
                    javaType = Long.class.getCanonicalName();
                } else {
                    javaType = columnClassName;
                }
            } else {
                if ((precision <= 39) && (Math.abs(scale) <= 53)) {
                    javaType = Float.class.getCanonicalName();
                } else if ((precision <= 79) && (Math.abs(scale) <= 107)) {
                    javaType = Double.class.getCanonicalName();
                } else {
                    javaType = columnClassName;
                }
            }
        } else if (TINYINT.equalsIgnoreCase(columnTypeName) && (precision <= 3)) {
            javaType = Byte.class.getCanonicalName();
        } else if (SMALLINT.equalsIgnoreCase(columnTypeName) && (precision <= 5)) {
            javaType = Short.class.getCanonicalName();
        } else {
            javaType = columnClassName.equals("[B") ? "byte[]" : columnClassName;
        }

        return javaType;
    }

    /**
     *
     * @param tableName
     * @param conn
     * @return
     */
    private static Object[] parse(String tableName, Connection conn) {
        final Map<String, SQLColumn> columnMap = new LinkedHashMap<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<String> primaryKeys = new ArrayList<>();

        try {
            DatabaseMetaData dmd = conn.getMetaData();

            if (dmd.getDatabaseProductName().equalsIgnoreCase(ORACLE)) {
                stmt = conn.prepareStatement(PRIMARY_KEY_GOT_SQL);
                stmt.setString(1, tableName);
                rs = stmt.executeQuery();
            } else {
                rs = dmd.getPrimaryKeys(null, null, tableName);
            }

            while (rs.next()) {
                primaryKeys.add(rs.getString(COLUMN_NAME));
            }
        } catch (SQLException e) {
            // ignore
            logger.error("Failed to get primary keys for table : " + tableName, e);
        } finally {
            JdbcUtil.closeQuietly(rs, stmt);
        }

        final String sql = "SELECT * FROM " + tableName + " WHERE 1 > 2";

        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ResultSetMetaData rmd = rs.getMetaData();

            for (int i = 1; i <= rmd.getColumnCount(); i++) {
                final Map<String, String> attrs = new LinkedHashMap<>();

                final String columnName = rmd.getColumnName(i);
                final String columnTypeName = rmd.getColumnTypeName(i);
                final String columnClassName = rmd.getColumnClassName(i);
                final int precision = rmd.getPrecision(i);
                final int scale = rmd.getScale(i);

                final String javaType = columnType2JavaType(columnTypeName, columnClassName, precision, scale);

                String jdbcType = columnTypeName;
                if (precision > 0 && Math.abs(scale) > 0) {
                    jdbcType += (WD.PARENTHESES_L + precision + WD.COMMA_SPACE + scale + WD.PARENTHESES_R);
                } else if (precision > 0) {
                    jdbcType += (WD.PARENTHESES_L + precision + WD.PARENTHESES_R);
                }

                attrs.put(ColumnEle.NAME, columnName);
                attrs.put(ColumnEle.JAVA_TYPE, javaType);
                attrs.put(ColumnEle.JDBC_TYPE, jdbcType);
                attrs.put(ColumnEle.SQL_TYPE, String.valueOf(rmd.getColumnType(i)));

                if (primaryKeys.contains(columnName)) {
                    attrs.put(ColumnEle.IS_PRIMARY_KEY, String.valueOf(true));
                    attrs.put(ColumnEle.IS_UNIQUE, String.valueOf(true));
                }

                attrs.put(ColumnEle.IS_AUTO_INCREMENT, String.valueOf(rmd.isAutoIncrement(i)));

                // some vendor jdbc doesn't support below property.
                try {
                    attrs.put(ColumnEle.IS_READ_ONLY, N.toString(rmd.isReadOnly(i)));
                } catch (Exception e) {
                    attrs.put(ColumnEle.IS_READ_ONLY, "false");
                }

                try {
                    attrs.put(ColumnEle.IS_WRITABLE, N.toString(rmd.isWritable(i)));
                } catch (Exception e) {
                    attrs.put(ColumnEle.IS_WRITABLE, "true");
                }

                int nullability = rmd.isNullable(i);

                if (ResultSetMetaData.columnNullable == nullability) {
                    attrs.put(ColumnEle.IS_NULLABLE, String.valueOf(true));
                } else if (ResultSetMetaData.columnNoNulls == nullability) {
                    attrs.put(ColumnEle.IS_NULLABLE, String.valueOf(false));
                } else {
                    // if unknown, set it to false.
                    attrs.put(ColumnEle.IS_NULLABLE, String.valueOf(false));
                }

                try {
                    attrs.put(ColumnEle.IS_SEARCHABLE, N.toString(rmd.isSearchable(i)));
                } catch (Exception e) {
                    attrs.put(ColumnEle.IS_SEARCHABLE, "true");
                }

                attrs.put(ColumnEle.IS_CASE_SENSITIVE, String.valueOf(rmd.isCaseSensitive(i)));

                SQLColumn column = new SQLColumn(attrs, tableName);

                columnMap.put(column.getName(), column);
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            JdbcUtil.closeQuietly(rs, stmt, null);
        }

        final Map<String, String> attrs = N.asLinkedHashMap(TableEle.NAME, tableName);

        return new Object[] { attrs, columnMap };
    }

    /**
     *
     * @param tableNode
     * @return
     */
    private static Object[] parse(Element tableNode) {
        final String tableName = tableNode.getAttribute(TableEle.NAME);
        final Map<String, String> attrs = XMLUtil.readAttributes(tableNode);
        final Map<String, SQLColumn> columnMap = new LinkedHashMap<>();

        final List<Element> columnElementList = XMLUtil.getElementsByTagName(tableNode, ColumnEle.COLUMN);
        for (Element columnElement : columnElementList) {
            SQLColumn column = new SQLColumn(XMLUtil.readAttributes(columnElement), tableName);
            columnMap.put(column.getName(), column);
        }

        return new Object[] { attrs, columnMap };
    }
}

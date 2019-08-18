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

import java.util.LinkedHashMap;
import java.util.Map;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.metadata.Column;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.TableEle.ColumnEle;
import com.landawn.abacus.metadata.Table;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.ImmutableMap;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLColumn.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLColumn implements Column {
    
    /** The name. */
    private final String name;
    
    /** The canonical name. */
    private final String canonicalName;
    
    /** The attrs. */
    private final Map<String, String> attrs;
    
    /** The java type. */
    private final String javaType;
    
    /** The jdbc type. */
    private final String jdbcType;
    
    /** The sql type. */
    private final int sqlType;
    
    /** The is primary key. */
    private final boolean isPrimaryKey;
    
    /** The is unique. */
    private final boolean isUnique;

    /** The is auto increment. */
    private final boolean isAutoIncrement;
    
    /** The default value. */
    private final Object defaultValue;
    
    /** The table. */
    private Table table;

    /**
     * Instantiates a new SQL column.
     *
     * @param attrs
     * @param tableName
     */
    public SQLColumn(Map<String, String> attrs, String tableName) {
        this.name = NameUtil.getCachedName(attrs.get(ColumnEle.NAME));
        attrs.put(ColumnEle.NAME, name);
        this.attrs = ImmutableMap.of(new LinkedHashMap<>(attrs));

        this.canonicalName = NameUtil.getCachedName(tableName + "." + name);
        this.javaType = this.attrs.get(ColumnEle.JAVA_TYPE);
        this.jdbcType = this.attrs.get(ColumnEle.JDBC_TYPE);
        this.sqlType = Integer.valueOf(this.attrs.get(ColumnEle.SQL_TYPE));

        this.isPrimaryKey = Boolean.valueOf(this.attrs.get(ColumnEle.IS_PRIMARY_KEY));
        this.isUnique = Boolean.valueOf(this.attrs.get(ColumnEle.IS_UNIQUE));
        this.isAutoIncrement = Boolean.valueOf(this.attrs.get(ColumnEle.IS_AUTO_INCREMENT));

        final Type<?> type = N.typeOf(javaType);
        String attr = this.attrs.get(ColumnEle.DEFAULT_VALUE);
        this.defaultValue = N.isNullOrEmpty(attr) ? type.defaultValue() : type.valueOf(attr);
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
     * Gets the canonical name.
     *
     * @return
     */
    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    /**
     * Gets the java type.
     *
     * @return
     */
    @Override
    public String getJavaType() {
        return javaType;
    }

    /**
     * Gets the jdbc type.
     *
     * @return
     */
    @Override
    public String getJdbcType() {
        return jdbcType;
    }

    /**
     * Gets the sql type.
     *
     * @return
     */
    @Override
    public int getSqlType() {
        return sqlType;
    }

    /**
     * Checks if is primary key.
     *
     * @return true, if is primary key
     */
    @Override
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * Checks if is unique.
     *
     * @return true, if is unique
     */
    @Override
    public boolean isUnique() {
        return isUnique;
    }

    /**
     * Checks if is auto increment.
     *
     * @return true, if is auto increment
     */
    @Override
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    @Override
    public boolean isReadOnly() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_READ_ONLY));
    }

    /**
     * Checks if is writable.
     *
     * @return true, if is writable
     */
    @Override
    public boolean isWritable() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_WRITABLE));
    }

    /**
     * Checks if is nullable.
     *
     * @return true, if is nullable
     */
    @Override
    public boolean isNullable() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_NULLABLE));
    }

    /**
     * Checks if is searchable.
     *
     * @return true, if is searchable
     */
    @Override
    public boolean isSearchable() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_SEARCHABLE));
    }

    /**
     * Checks if is case sensitive.
     *
     * @return true, if is case sensitive
     */
    @Override
    public boolean isCaseSensitive() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_CASE_SENSITIVE));
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
     * Gets the default value.
     *
     * @return
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the table.
     *
     * @return
     */
    @Override
    public Table getTable() {
        return table;
    }

    /**
     * Sets the table.
     *
     * @param table the new table
     */
    void setTable(Table table) {
        this.table = table;
    }

    /**
     * Hash code.
     *
     * @return
     */
    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }

    /**
     * Equals.
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLColumn && N.equals(((SQLColumn) obj).canonicalName, canonicalName));
    }

    /**
     * To string.
     *
     * @return
     */
    @Override
    public String toString() {
        return attrs.toString();
    }
}

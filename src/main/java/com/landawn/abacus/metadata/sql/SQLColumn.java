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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLColumn implements Column {
    private final String name;
    private final String canonicalName;
    private final Map<String, String> attrs;
    private final String javaType;
    private final String jdbcType;
    private final int sqlType;
    private final boolean isPrimaryKey;
    private final boolean isUnique;

    private final boolean isAutoIncrement;
    private final Object defaultValue;
    private Table table;

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public String getJavaType() {
        return javaType;
    }

    @Override
    public String getJdbcType() {
        return jdbcType;
    }

    @Override
    public int getSqlType() {
        return sqlType;
    }

    @Override
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    @Override
    public boolean isUnique() {
        return isUnique;
    }

    @Override
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    @Override
    public boolean isReadOnly() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_READ_ONLY));
    }

    @Override
    public boolean isWritable() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_WRITABLE));
    }

    @Override
    public boolean isNullable() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_NULLABLE));
    }

    @Override
    public boolean isSearchable() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_SEARCHABLE));
    }

    @Override
    public boolean isCaseSensitive() {
        return Boolean.valueOf(attrs.get(ColumnEle.IS_CASE_SENSITIVE));
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
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Table getTable() {
        return table;
    }

    void setTable(Table table) {
        this.table = table;
    }

    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLColumn && N.equals(((SQLColumn) obj).canonicalName, canonicalName));
    }

    @Override
    public String toString() {
        return attrs.toString();
    }
}

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

package com.landawn.abacus.metadata;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface Column.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Column {

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the canonical name.
     *
     * @return the canonical name
     */
    String getCanonicalName();

    /**
     * Gets the java type.
     *
     * @return the java type
     */
    String getJavaType();

    /**
     * Gets the jdbc type.
     *
     * @return the jdbc type
     */
    String getJdbcType();

    /**
     * Gets the sql type.
     *
     * @return the sql type
     */
    int getSqlType();

    /**
     * Checks if is primary key.
     *
     * @return true, if is primary key
     */
    boolean isPrimaryKey();

    /**
     * Checks if is unique.
     *
     * @return true, if is unique
     */
    boolean isUnique();

    /**
     * Checks if is auto increment.
     *
     * @return true, if is auto increment
     */
    boolean isAutoIncrement();

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    boolean isReadOnly();

    /**
     * Checks if is writable.
     *
     * @return true, if is writable
     */
    boolean isWritable();

    /**
     * Checks if is nullable.
     *
     * @return true, if is nullable
     */
    boolean isNullable();

    /**
     * Checks if is searchable.
     *
     * @return true, if is searchable
     */
    boolean isSearchable();

    /**
     * Checks if is case sensitive.
     *
     * @return true, if is case sensitive
     */
    boolean isCaseSensitive();

    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    Map<String, String> getAttributes();

    /**
     * Gets the attribute.
     *
     * @param attrName the attr name
     * @return the attribute
     */
    String getAttribute(String attrName);

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    Object getDefaultValue();

    /**
     * Gets the table.
     *
     * @return the table
     */
    Table getTable();
}

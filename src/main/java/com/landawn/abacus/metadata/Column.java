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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public interface Column {
    String getName();

    String getCanonicalName();

    String getJavaType();

    String getJdbcType();

    int getSqlType();

    boolean isPrimaryKey();

    boolean isUnique();

    boolean isAutoIncrement();

    boolean isReadOnly();

    boolean isWritable();

    boolean isNullable();

    boolean isSearchable();

    boolean isCaseSensitive();

    Map<String, String> getAttributes();

    String getAttribute(String attrName);

    Object getDefaultValue();

    Table getTable();
}

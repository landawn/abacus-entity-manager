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

import java.util.Collection;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface Database.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Database {

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    String getProductName();

    /**
     * Gets the product version.
     *
     * @return the product version
     */
    String getProductVersion();

    /**
     * Gets the table list.
     *
     * @return the table list
     */
    Collection<Table> getTableList();

    /**
     * Gets the table name list.
     *
     * @return the table name list
     */
    Collection<String> getTableNameList();

    /**
     * Gets the table.
     *
     * @param tableName the table name
     * @return the table
     */
    Table getTable(String tableName);

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
}

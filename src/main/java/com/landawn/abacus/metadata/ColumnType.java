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

// TODO: Auto-generated Javadoc
/**
 * The Enum ColumnType.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public enum ColumnType {

    /** Field TABLE_COLUMN. */
    TABLE_COLUMN("tableColumn"),

    /** Field ENTITY. */
    ENTITY("entity");

    /** Field name;. */
    private final String name;

    /**
     * Constructor.
     *
     * @param name the name
     */
    ColumnType(String name) {
        this.name = name;
    }

    /**
     * Method getName.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the.
     *
     * @param typeName the type name
     * @return EntityType
     */
    public static ColumnType get(String typeName) {
        if (ENTITY.name.equals(typeName)) {
            return ENTITY;
        } else {
            return TABLE_COLUMN;
        }
    }

    /**
     * Method isEntity.
     * 
     * @return boolean
     */
    public boolean isEntity() {
        return this == ENTITY;
    }

    /**
     * Method toString.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return name;
    }
}

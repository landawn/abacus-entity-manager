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
 * The Enum OnDeleteAction.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public enum OnDeleteAction {
    /**
     * Field NO_ACTION.
     */
    NO_ACTION(0),
    /**
     * Field SET_NULL.
     */
    SET_NULL(1),
    /**
     * Field CASCADE.
     */
    CASCADE(2);

    /**
     * Field intValue.
     */
    private int intValue;

    /**
     * Instantiates a new on delete action.
     *
     * @param intValue
     */
    OnDeleteAction(int intValue) {
        this.intValue = intValue;
    }

    /**
     *
     * @return int
     */
    public int value() {
        return intValue;
    }

    /**
     *
     * @param name
     * @return ConstraintType
     */
    public static OnDeleteAction get(String name) {
        if ("noAction".equalsIgnoreCase(name)) {
            return NO_ACTION;
        } else if ("setNull".equalsIgnoreCase(name)) {
            return SET_NULL;
        } else if ("cascade".equalsIgnoreCase(name)) {
            return CASCADE;
        } else {
            throw new IllegalArgumentException("Invalid CascadeType value[" + name + "]. ");
        }
    }
}

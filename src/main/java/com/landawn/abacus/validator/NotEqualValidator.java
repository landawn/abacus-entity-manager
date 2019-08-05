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

package com.landawn.abacus.validator;

import com.landawn.abacus.type.Type;

// TODO: Auto-generated Javadoc
/**
 * The Class NotEqualValidator.
 *
 * @author Haiyang Li
 * @param <T> the generic type
 * @since 0.8
 */
public final class NotEqualValidator<T> extends AbstractComparableValidator<T> {

    /** The case insensitive. */
    private boolean caseInsensitive = true;

    /**
     * Constructor for NotEqualValidator.
     *
     * @param propName the prop name
     * @param type the type
     * @param value the value
     */
    public NotEqualValidator(String propName, Type<T> type, String value) {
        super(propName, type, value);
    }

    /**
     * Instantiates a new not equal validator.
     *
     * @param propName the prop name
     * @param type the type
     * @param value the value
     * @param caseInsensitive the case insensitive
     */
    public NotEqualValidator(String propName, Type<T> type, String value, String caseInsensitive) {
        super(propName, type, value);
        this.caseInsensitive = Boolean.valueOf(caseInsensitive);
    }

    /**
     * Method isValid.
     *
     * @param propValue the prop value
     * @return boolean
     */
    @Override
    public boolean isValid(Object propValue) {
        if (propValue == null) {
            return true;
        }

        if (caseInsensitive) {
            return !benchmark.equals(propValue);
        } else {
            return !((String) benchmark).equalsIgnoreCase((String) propValue);
        }
    }
}

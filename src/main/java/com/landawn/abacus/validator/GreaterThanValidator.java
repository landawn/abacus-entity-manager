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
 * The Class GreaterThanValidator.
 *
 * @author Haiyang Li
 * @param <T> the generic type
 * @since 0.8
 */
public final class GreaterThanValidator<T extends Comparable<T>> extends AbstractComparableValidator<T> {
    
    /**
     * Constructor for GreaterThanValidator.
     *
     * @param propName the prop name
     * @param type the type
     * @param value the value
     */
    public GreaterThanValidator(String propName, Type<T> type, String value) {
        super(propName, type, value);
    }

    /**
     * Method isValid.
     *
     * @param propValue the prop value
     * @return boolean
     * @see com.landawn.abacus.validator.Validator#isValid(T)
     */
    @Override
    public boolean isValid(T propValue) {
        if (propValue == null) {
            return true;
        }

        return propValue.compareTo(benchmark) > 0;
    }
}

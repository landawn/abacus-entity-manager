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
 * The Class GreaterEqualValidator.
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public final class GreaterEqualValidator<T extends Comparable<T>> extends AbstractComparableValidator<T> {

    /**
     * Constructor for GreaterEqualValidator.
     *
     * @param propName
     * @param type
     * @param value
     */
    public GreaterEqualValidator(String propName, Type<T> type, String value) {
        super(propName, type, value);
    }

    /**
     * Method isValid.
     *
     * @param propValue
     * @return boolean
     * @see com.landawn.abacus.validator.Validator#isValid(T)
     */
    @Override
    public boolean isValid(T propValue) {
        if (propValue == null) {
            return true;
        }

        return propValue.compareTo(benchmark) >= 0;
    }
}

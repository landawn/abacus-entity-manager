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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public final class RangeValidator<T extends Comparable<T>> extends AbstractValidator<T> {
    private final T minValue;
    private final T maxValue;

    public RangeValidator(String propName, Type<T> type, String minValue, String maxValue) {
        super(propName, type);
        this.minValue = type.valueOf(minValue);
        this.maxValue = type.valueOf(maxValue);
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

        return (minValue.compareTo(propValue) <= 0) && (propValue.compareTo(maxValue) < 0);
    }

    /**
     * Method toString.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + minValue + ", " + maxValue + ")";
    }
}

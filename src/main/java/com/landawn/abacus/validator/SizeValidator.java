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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import com.landawn.abacus.type.Type;

// TODO: Auto-generated Javadoc
/**
 * The Class SizeValidator.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SizeValidator extends AbstractValidator<Object> {

    /** The min size. */
    final int minSize;

    /** The max size. */
    final int maxSize;

    /**
     * Instantiates a new size validator.
     *
     * @param propName
     * @param type
     * @param size
     */
    public SizeValidator(String propName, Type<Object> type, String size) {
        super(propName, type);
        this.minSize = Integer.valueOf(size);
        this.maxSize = Integer.valueOf(size) + 1;
    }

    /**
     * Constructor SizeValidator.
     *
     * @param propName
     * @param type
     * @param minSize
     * @param maxSize
     */
    public SizeValidator(String propName, Type<Object> type, String minSize, String maxSize) {
        super(propName, type);
        this.minSize = Integer.valueOf(minSize);
        this.maxSize = Integer.valueOf(maxSize);
    }

    /**
     * Method isValid.
     *
     * @param propValue
     * @return boolean
     * @see com.landawn.abacus.validator.Validator#isValid(T)
     */
    @Override
    public boolean isValid(Object propValue) {
        if (propValue == null) {
            return true;
        }

        int size = -1;

        if (propValue instanceof String) {
            size = ((String) propValue).length();
        } else if (propValue.getClass().isArray()) {
            size = Array.getLength(propValue);
        } else if (propValue instanceof Collection) {
            size = ((Collection<?>) propValue).size();
        } else if (propValue instanceof Map) {
            size = ((Map<?, ?>) propValue).size();
        }

        return (minSize <= size) && (size < maxSize);
    }

    /**
     * Method toString.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + minSize + ", " + maxSize + ")";
    }
}

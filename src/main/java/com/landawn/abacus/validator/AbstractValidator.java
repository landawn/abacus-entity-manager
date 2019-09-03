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

import com.landawn.abacus.exception.ValidationException;
import com.landawn.abacus.type.Type;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractValidator.
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public abstract class AbstractValidator<T> implements Validator<T> {

    /** The prop name. */
    private final String propName;

    /** The type. */
    private final Type<T> type;

    /**
     * Instantiates a new abstract validator.
     *
     * @param propName
     * @param type
     */
    protected AbstractValidator(String propName, Type<T> type) {
        this.propName = propName;
        this.type = type;
    }

    /**
     * 
     * @return Property
     * @see com.landawn.abacus.validator.Validator#getPropName()
     */
    @Override
    public String getPropName() {
        return propName;
    }

    /**
     * 
     * @return Type
     * @see com.landawn.abacus.validator.Validator#getType()
     */
    @Override
    public Type<T> getType() {
        return type;
    }

    /**
     *
     * @param propValue
     * @return
     * @throws ValidationException the validation exception
     * @see com.landawn.abacus.validator.Validator#validate(T)
     */
    @Override
    public T validate(T propValue) throws ValidationException {
        if (!isValid(propValue)) {
            throw new ValidationException("Property(" + getPropName() + ") should be " + toString() + ". It can't be(" + propValue + ").");
        }

        return propValue;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

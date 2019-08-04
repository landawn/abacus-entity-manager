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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractValidator<T> implements Validator<T> {
    private final String propName;
    private final Type<T> type;

    protected AbstractValidator(String propName, Type<T> type) {
        this.propName = propName;
        this.type = type;
    }

    /**
     * Method getProperty.
     * 
     * @return Property
     * @see com.landawn.abacus.validator.Validator#getPropName()
     */
    @Override
    public String getPropName() {
        return propName;
    }

    /**
     * Method getType.
     * 
     * @return Type
     * @see com.landawn.abacus.validator.Validator#getType()
     */
    @Override
    public Type<T> getType() {
        return type;
    }

    /**
     * Method check.
     * 
     * @param propValue
     * @throws ValidationException
     * @see com.landawn.abacus.validator.Validator#validate(T)
     */
    @Override
    public T validate(T propValue) throws ValidationException {
        if (!isValid(propValue)) {
            throw new ValidationException("Property(" + getPropName() + ") should be " + toString() + ". It can't be(" + propValue + ").");
        }

        return propValue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

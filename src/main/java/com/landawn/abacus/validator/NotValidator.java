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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
final class NotValidator<T> extends AbstractValidator<T> {
    private final Validator<T> srcValidator;
    private final boolean isNotNull;

    NotValidator(Validator<T> srcValidator) {
        super(srcValidator.getPropName(), srcValidator.getType());
        this.srcValidator = srcValidator;
        isNotNull = srcValidator instanceof NullValidator;
    }

    /**
     * Method isValid.
     * 
     * @param value
     * @return boolean
     * @see com.landawn.abacus.validator.Validator#isValid(T)
     */
    @Override
    public boolean isValid(T value) {
        if (value == null) {
            return !isNotNull;
        }

        return !srcValidator.isValid(value);
    }

    /**
     * Method toString.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Not" + srcValidator.toString();
    }
}

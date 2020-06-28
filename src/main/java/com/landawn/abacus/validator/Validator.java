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
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public interface Validator<T> {
    /**
     * 
     * @return Property
     */
    String getPropName();

    /**
     *
     * @return Type
     */
    Type<T> getType();

    /**
     *
     * @param propValue
     * @return boolean
     */
    boolean isValid(T propValue);

    /**
     *
     * @param propValue
     * @return TODO
     * @throws ValidationException             if {@code isValid()} returns {@code false}
     */
    T validate(T propValue) throws ValidationException;
}

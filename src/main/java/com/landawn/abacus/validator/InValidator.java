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

import java.util.Set;

import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.StringUtil;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public final class InValidator<T> extends AbstractValidator<T> {

    private final Set<T> valueSet = N.newHashSet();

    @SafeVarargs
    public InValidator(String propName, Type<T> type, String... values) {
        super(propName, type);

        for (String value : values) {
            valueSet.add(type.valueOf(value));
        }
    }

    /**
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

        return valueSet.contains(propValue);
    }

    /**
     * 
     * @return String
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + StringUtil.join(valueSet) + ")";
    }
}

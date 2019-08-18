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
 * The Class AbstractComparableValidator.
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public abstract class AbstractComparableValidator<T> extends AbstractValidator<T> {

    /** The benchmark. */
    protected final T benchmark;

    /**
     * Instantiates a new abstract comparable validator.
     *
     * @param propName
     * @param type
     * @param benchmark
     */
    protected AbstractComparableValidator(String propName, Type<T> type, String benchmark) {
        super(propName, type);
        this.benchmark = type.valueOf(benchmark);
    }

    /**
     * Gets the benchmark.
     *
     * @return
     */
    public T getBenchmark() {
        return benchmark;
    }

    /**
     * To string.
     *
     * @return
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + benchmark + ")";
    }
}

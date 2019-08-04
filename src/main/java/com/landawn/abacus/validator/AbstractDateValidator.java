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

import java.util.Date;

import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.PropertyEle;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.DateUtil;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractDateValidator<T extends Date> extends AbstractValidator<T> {
    private final T benchmark;

    private final boolean isSysTime;

    protected AbstractDateValidator(String propName, Type<T> type, String value) {
        super(propName, type);

        if (PropertyEle.SYS_TIME.equals(value)) {
            isSysTime = true;
            benchmark = null;
        } else {
            isSysTime = false;
            benchmark = type.valueOf(value);
        }
    }

    /**
     * Method getBenchmark.
     * 
     * @return long
     */
    protected long getMillis() {
        if (isSysTime) {
            return System.currentTimeMillis();
        } else {
            return benchmark.getTime();
        }
    }

    /**
     * Method toString.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (isSysTime ? PropertyEle.SYS_TIME : DateUtil.format(benchmark)) + ")";
    }
}

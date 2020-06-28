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

package com.landawn.abacus.idGenerator;

import java.math.BigInteger;

import com.landawn.abacus.metadata.Property;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public abstract class AbstractNumberIdGenerator<T extends Number> extends AbstractIdGenerator<T> {

    private static final int LONG_TYPE = 1;

    private static final int INT_TYPE = 2;

    private static final int SHORT_TYPE = 3;

    private static final int BYTE_TYPE = 4;

    private final int numberType;

    protected AbstractNumberIdGenerator(Property prop) {
        super(prop);

        Class<?> typeClass = prop.getType().clazz();

        if (typeClass.equals(long.class) || typeClass.equals(Long.class) || typeClass.equals(BigInteger.class)) {
            numberType = LONG_TYPE;
        } else if (typeClass.equals(int.class) || typeClass.equals(Integer.class)) {
            numberType = INT_TYPE;
        } else if (typeClass.equals(short.class) || typeClass.equals(Short.class)) {
            numberType = SHORT_TYPE;
        } else if (typeClass.equals(byte.class) || typeClass.equals(Byte.class)) {
            numberType = BYTE_TYPE;
        } else {
            throw new RuntimeException("'" + prop.getName() + "' property's is not a valid number type for  " + getClass().getName());
        }
    }

    /**
     *
     * @param t
     * @return
     */
    protected Number valueOf(Number t) {
        switch (numberType) {
            case LONG_TYPE:
                return t.longValue();

            case INT_TYPE:
                return t.intValue();

            case SHORT_TYPE:
                return t.shortValue();

            case BYTE_TYPE:
                return t.byteValue();

            default:
                return t;
        }
    }
}

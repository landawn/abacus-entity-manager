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

import com.landawn.abacus.core.Executant;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.ClassUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractIdGenerator.
 *
 * @author Haiyang Li
 * @param <T> the generic type
 * @since 0.8
 */
public abstract class AbstractIdGenerator<T> implements IdGenerator<T> {

    /** The prop. */
    final Property prop;

    /**
     * Instantiates a new abstract id generator.
     *
     * @param prop the prop
     */
    protected AbstractIdGenerator(Property prop) {
        this.prop = prop;
    }

    /**
     * Gets the property.
     *
     * @return the property
     */
    @Override
    public Property getProperty() {
        return prop;
    }

    /**
     * Initialize.
     *
     * @param executor the executor
     */
    @Override
    public void initialize(Executant executor) {
        // do nothing.
    }

    /**
     * Allocate.
     *
     * @return the t
     */
    @Override
    public abstract T allocate();

    /**
     * Reserve.
     *
     * @param value the value
     */
    @Override
    public void reserve(T value) {
        // do nothing.
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        int h = 7;
        h = (h * 31) + prop.getName().hashCode();
        h = (h * 31) + ClassUtil.getCanonicalClassName(getClass()).hashCode();

        return h;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AbstractIdGenerator) {
            AbstractIdGenerator<?> other = (AbstractIdGenerator<?>) obj;

            return N.equals(prop.getName(), other.prop.getName()) && N.equals(getClass().getCanonicalName(), other.getClass().getCanonicalName());
        }

        return false;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + prop.getName();
    }
}

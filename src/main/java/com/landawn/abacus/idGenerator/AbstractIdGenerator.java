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
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.8
 */
public abstract class AbstractIdGenerator<T> implements IdGenerator<T> {

    final Property prop;

    protected AbstractIdGenerator(Property prop) {
        this.prop = prop;
    }

    /**
     * Gets the property.
     *
     * @return
     */
    @Override
    public Property getProperty() {
        return prop;
    }

    /**
     *
     * @param executor
     */
    @Override
    public void initialize(Executant executor) {
        // do nothing.
    }

    @Override
    public abstract T allocate();

    /**
     *
     * @param value
     */
    @Override
    public void reserve(T value) {
        // do nothing.
    }

    @Override
    public int hashCode() {
        int h = 7;
        h = (h * 31) + prop.getName().hashCode();
        h = (h * 31) + ClassUtil.getCanonicalClassName(getClass()).hashCode();

        return h;
    }

    /**
     *
     * @param obj
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + prop.getName();
    }
}

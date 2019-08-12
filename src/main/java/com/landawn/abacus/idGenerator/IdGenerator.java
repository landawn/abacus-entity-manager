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

// TODO: Auto-generated Javadoc
/**
 * The Interface IdGenerator.
 *
 * @author Haiyang Li
 * @param <T> the generic type
 * @since 0.8
 */
public interface IdGenerator<T> {

    /** The Constant ID_GENERATOR. */
    public static final String ID_GENERATOR = IdGenerator.class.getSimpleName();

    /**
     * Initialize.
     *
     * @param executor the executor
     */
    void initialize(Executant executor);

    /**
     * Method getProperty.
     * 
     * @return Property
     */
    Property getProperty();

    /**
     * Method allocate.
     * 
     * @return T
     */
    T allocate();

    /**
     * Method reserve.
     *
     * @param value the value
     */
    void reserve(T value);
}

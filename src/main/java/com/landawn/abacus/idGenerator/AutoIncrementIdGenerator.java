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

import com.landawn.abacus.metadata.Property;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class AutoIncrementIdGenerator extends AbstractNumberIdGenerator<Number> {

    /**
     * Instantiates a new auto increment id generator.
     *
     * @param prop
     */
    public AutoIncrementIdGenerator(Property prop) {
        super(prop);
    }

    /**
     *
     * @return
     */
    @Override
    public Long allocate() {
        throw new RuntimeException("Can't allocate value for database auto-generated id.");
    }
}

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
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * The Class UUIDIdGenerator.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class UUIDIdGenerator extends AbstractStringIdGenerator {

    /** The header. */
    private final String header;

    /**
     * Instantiates a new UUID id generator.
     *
     * @param prop
     */
    public UUIDIdGenerator(Property prop) {
        this(prop, null);
    }

    /**
     * Instantiates a new UUID id generator.
     *
     * @param prop
     * @param header
     */
    public UUIDIdGenerator(Property prop, String header) {
        super(prop);
        this.header = header;
    }

    /**
     *
     * @return
     */
    @Override
    public String allocate() {
        if (header == null) {
            return N.uuid();
        } else {
            return header + N.uuid();
        }
    }
}

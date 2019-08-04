/*
 * Copyright (c) 2015, Haiyang Li.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.core.sql.command;

import java.util.Map;

import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.OperationType;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractCommand implements Command, Cloneable {
    @Override
    public OperationType getOperationType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDefinition getEntityDef() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getOptions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() {
        Object copy = null;

        try {
            copy = super.clone();
        } catch (CloneNotSupportedException e) {
            // ignore; never happen.
        }

        return copy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Command> T copy() {
        return (T) clone();
    }
}

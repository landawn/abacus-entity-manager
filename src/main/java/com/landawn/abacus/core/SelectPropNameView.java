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

package com.landawn.abacus.core;

import java.util.Collection;

import com.landawn.abacus.annotation.Internal;

// TODO: Auto-generated Javadoc
/**
 * The Class SelectPropNameView.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class SelectPropNameView {
    
    /** The entity name. */
    final String entityName;
    
    /** The select prop names. */
    final Collection<String> selectPropNames;
    
    /** The simple prop names. */
    final Collection<String> simplePropNames;
    
    /** The entity prop names. */
    final Collection<String> entityPropNames;

    /**
     * Instantiates a new select prop name view.
     *
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param simplePropNames the simple prop names
     * @param entityPropNames the entity prop names
     */
    SelectPropNameView(String entityName, Collection<String> selectPropNames, Collection<String> simplePropNames, Collection<String> entityPropNames) {
        this.entityName = entityName;
        this.selectPropNames = selectPropNames;
        this.simplePropNames = simplePropNames;
        this.entityPropNames = entityPropNames;
    }
}

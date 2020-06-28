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

import java.util.List;
import java.util.Map;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.metadata.Property;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class InsertPropsListView {

    /** The write props list. */
    final List<Map<String, Object>> writePropsList;

    /** The prop entity props list. */
    final Map<Property, List<PropEntityProps>> propEntityPropsList;

    /** The prop bi entity props list. */
    final Map<Property, List<BiEntityProps>> propBiEntityPropsList;

    /**
     * Instantiates a new insert props list view.
     *
     * @param writePropsList
     * @param propEntityPropsList
     * @param propBiEntityPropsList
     */
    InsertPropsListView(List<Map<String, Object>> writePropsList, Map<Property, List<PropEntityProps>> propEntityPropsList,
            Map<Property, List<BiEntityProps>> propBiEntityPropsList) {
        this.writePropsList = writePropsList;
        this.propEntityPropsList = propEntityPropsList;
        this.propBiEntityPropsList = propBiEntityPropsList;
    }
}

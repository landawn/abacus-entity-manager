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
 * The Class UpdatePropsView.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class UpdatePropsView {

    /** The update props. */
    final Map<String, Object> updateProps;

    /** The prop entity list. */
    final Map<Property, List<Object>> propEntityList;

    /**
     * Instantiates a new update props view.
     *
     * @param updateProps
     * @param propEntityList
     */
    UpdatePropsView(Map<String, Object> updateProps, Map<Property, List<Object>> propEntityList) {
        this.updateProps = updateProps;
        this.propEntityList = propEntityList;
    }
}

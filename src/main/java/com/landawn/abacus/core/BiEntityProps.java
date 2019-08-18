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

import java.util.HashMap;
import java.util.Map;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.metadata.Association;
import com.landawn.abacus.metadata.Property;

// TODO: Auto-generated Javadoc
/**
 * The Class BiEntityProps.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class BiEntityProps {

    /** The entity prop. */
    private final Property entityProp;

    /** The insert props. */
    private final Map<String, Object> insertProps;

    /** The associated entity. */
    private final Object associatedEntity;

    /**
     * Instantiates a new bi entity props.
     *
     * @param prop the prop
     * @param insertProps the insert props
     * @param associatedEntity the associated entity
     */
    BiEntityProps(Property prop, Map<String, Object> insertProps, Object associatedEntity) {
        this.entityProp = prop;
        this.insertProps = insertProps;
        this.associatedEntity = associatedEntity;
    }

    /**
     * Creates the props.
     *
     * @return
     */
    Map<String, Object> createProps() {
        Map<String, Object> biProps = new HashMap<>();
        Association association = entityProp.getAssociation();

        biProps.put(association.getBiEntityProperties()[0].getName(), insertProps.get(association.getSrcProperty().getName()));

        biProps.put(association.getBiEntityProperties()[1].getName(), EntityManagerUtil.getPropValue(associatedEntity, association.getTargetProperty()));

        return biProps;
    }
}

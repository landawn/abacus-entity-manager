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

import java.util.Map;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.metadata.Association;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * The Class PropEntityProps.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class PropEntityProps {
    
    /** The prop. */
    private final Property prop;
    
    /** The entity. */
    private final Object entity;
    
    /** The props. */
    private final Map<String, Object> props;
    
    /** The src prop value. */
    private Object srcPropValue;

    /**
     * Instantiates a new prop entity props.
     *
     * @param prop the prop
     * @param entity the entity
     */
    PropEntityProps(Property prop, Object entity) {
        this(prop, entity, null, null);
    }

    /**
     * Instantiates a new prop entity props.
     *
     * @param prop the prop
     * @param entity the entity
     * @param srcPropValue the src prop value
     * @param props the props
     */
    PropEntityProps(Property prop, Object entity, Object srcPropValue, Map<String, Object> props) {
        this.prop = prop;
        this.entity = entity;
        this.srcPropValue = srcPropValue;
        this.props = props;
    }

    /**
     * Gets the prop entity.
     *
     * @return the prop entity
     */
    public Object getPropEntity() {
        Association association = prop.getAssociation();
        Property srcProp = association.getSrcProperty();
        Property targetProp = association.getTargetProperty();

        if ((srcPropValue == null) && (props != null)) {
            srcPropValue = props.get(srcProp.getName());

            if (srcPropValue == null) {
                throw new AbacusException("Source property " + srcProp.getName() + " is null");
            }
        }

        if (srcPropValue != null) {
            EntityManagerUtil.setPropValue(entity, targetProp, srcPropValue);
        }

        return entity;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((prop == null) ? 0 : prop.hashCode());
        result = (prime * result) + ((entity == null) ? 0 : entity.hashCode());

        return result;
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

        if (obj instanceof PropEntityProps) {
            PropEntityProps other = (PropEntityProps) obj;

            return N.equals(prop, other.prop) && N.equals(entity, other.entity);
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
        return "{prop=" + prop + ", entity=" + entity + "}";
    }
}

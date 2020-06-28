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

import javax.xml.bind.annotation.XmlTransient;

import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class DirtyMarkerImpl extends AbstractDirtyMarker {

    /**
     * Instantiates a new dirty marker impl.
     */
    // for kryo
    DirtyMarkerImpl() {
        super(N.EMPTY_STRING);
    }

    /**
     * Instantiates a new dirty marker impl.
     *
     * @param entityName
     */
    public DirtyMarkerImpl(String entityName) {
        super(entityName);
    }

    /**
     * Sets the updated prop name.
     *
     * @param propName the new updated prop name
     */
    @Override
    @XmlTransient
    public void setUpdatedPropName(String propName) {
        super.setUpdatedPropName(propName);
    }

    /**
     * Sets the updated prop names.
     *
     * @param propNames the new updated prop names
     */
    @Override
    @XmlTransient
    public void setUpdatedPropNames(Collection<String> propNames) {
        super.setUpdatedPropNames(propNames);
    }

    /**
     * Checks if is entity dirty.
     *
     * @param entities
     * @return true, if is entity dirty
     */
    @Override
    @XmlTransient
    public boolean isEntityDirty(Collection<? extends DirtyMarker> entities) {
        return super.isEntityDirty(entities);
    }

    /**
     * Mark entity dirty.
     *
     * @param entities
     * @param isDirty
     */
    @Override
    public void markEntityDirty(Collection<? extends DirtyMarker> entities, boolean isDirty) {
        super.markEntityDirty(entities, isDirty);
    }
}

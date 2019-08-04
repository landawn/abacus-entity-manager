/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.impl;

import com.landawn.abacus.core.AbstractDirtyMarker;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class MyAbstractDirtyMarker extends AbstractDirtyMarker {
    protected MyAbstractDirtyMarker(String entityName) {
        super(entityName);
    }

    /**
     * Field serialVersionUID
     */
    private static final long serialVersionUID = 6779705102263381757L;
}

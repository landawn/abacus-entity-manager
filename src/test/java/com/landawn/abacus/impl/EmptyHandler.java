/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.impl;

import com.landawn.abacus.EntityManager;
import com.landawn.abacus.handler.AbstractHandler;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class EmptyHandler<T> extends AbstractHandler<T> {
    public EmptyHandler(EntityManager<T> entityManager) {
        super(entityManager);
    }
}

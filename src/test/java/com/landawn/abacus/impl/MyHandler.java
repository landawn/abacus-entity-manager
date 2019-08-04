/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.impl;

import java.util.Collection;
import java.util.Map;

import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.handler.AbstractHandler;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class MyHandler<T> extends AbstractHandler<T> {
    public MyHandler(EntityManager<T> entityManager) {
        super(entityManager);
    }

    public MyHandler(EntityManager<T> entityManager, String who) {
        super(entityManager);
        N.println("Hello " + who);
    }

    @Override
    public void preGet(EntityId entityId, Collection<String> selectPropNames, Map<String, Object> options) {
        N.println("it's me");
    }
}

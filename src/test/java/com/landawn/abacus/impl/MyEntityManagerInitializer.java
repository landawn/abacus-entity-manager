/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.impl;

import com.landawn.abacus.core.EntityManagerInitializer;
import com.landawn.abacus.handler.HandlerFactory;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class MyEntityManagerInitializer implements EntityManagerInitializer {
    @Override
    public void initialize() {
        HandlerFactory.registerHandler("EmptyHandler", EmptyHandler.class);
        HandlerFactory.registerHandler("MyHandler", MyHandler.class);
    }
}

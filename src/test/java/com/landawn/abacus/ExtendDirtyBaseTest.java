/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class ExtendDirtyBaseTest extends AbstractEntityManager0Test {
    public static final String domainName = "ExtendDirty";

    @Override
    protected String getDomainName() {
        return domainName;
    }
}

/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.core.AbacusConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration.CustomizedEntityCacheConfiguration;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.N;

import java.io.File;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class AbacusConfigurationTest extends AbstractTest {
    public void testEntityCacheConfiguration() {
        File file = Configuration.findFile(AbacusConfiguration.ABACUS_ENTITY_MANAGER_FILE_NAME);
        AbacusConfiguration abacusConfig = new AbacusConfiguration(file);

        // assertEquals(7, abacusConfig.getEntityManagerConfigurationList().size());
        EntityManagerConfiguration entityManagerConfig = abacusConfig.getEntityManagerConfigurationList().get(0);

        N.println(entityManagerConfig.getEntityDefinitionFile().getAbsolutePath());

        assertEquals(50000, entityManagerConfig.getEntityCacheConfiguration().getCapacity());
        assertEquals(3600, entityManagerConfig.getEntityCacheConfiguration().getLiveTime());
        assertFalse(entityManagerConfig.getEntityCacheConfiguration().isExcludedEntity("Author"));
        // assertFalse(entityManagerConfig.getEntityCacheConfiguration().isIncludedEntity("author"));
        assertEquals(2, entityManagerConfig.getEntityCacheConfiguration().getCustomizedEntityNames().size());

        CustomizedEntityCacheConfiguration tailoredEntity = entityManagerConfig.getEntityCacheConfiguration().getCustomizedEntityCacheConfiguration("Author");
        // assertEquals(1, tailoredEntity.getExcludedPropertyNames().size());
        assertEquals(1800000, tailoredEntity.getLiveTime());
        assertEquals(600000, tailoredEntity.getMaxIdleTime());

        // assertEquals("Author.birthDay", tailoredEntity.getExcludedPropertyNames().iterator().next());
    }
}

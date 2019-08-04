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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.landawn.abacus.EntityId;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.cache.AbstractCache;
import com.landawn.abacus.cache.Cache;
import com.landawn.abacus.cache.CacheFactory;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration.CustomizedEntityCacheConfiguration;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Properties;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
class EntityCacheDecorator extends AbstractCache<EntityId, MapEntity> {
    private static final Logger logger = LoggerFactory.getLogger(EntityCacheDecorator.class);

    private final EntityCacheConfiguration config;
    private final Cache<EntityId, MapEntity> entityCache;

    public EntityCacheDecorator(EntityCacheConfiguration config) {
        this.config = config;
        entityCache = newEntityCacheProviderInstance(config);

        if (config != null) {
            defaultLiveTime = config.getLiveTime();
            defaultMaxIdleTime = config.getMaxIdleTime();
        }
    }

    @Override
    public MapEntity gett(EntityId entityId) {
        checkEntityId(entityId);

        try {
            return entityCache.gett(entityId);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to get entity with id(" + N.toString(entityId) + ") from cache.", e);
            }
        }

        return null;
    }

    @Override
    public boolean put(EntityId entityId, MapEntity entity, long liveTime, long maxIdleTime) {
        checkEntityId(entityId);

        String entityName = entity.entityName();

        if (isExcludedEntity(entityName)) {
            return false;
        }

        CustomizedEntityCacheConfiguration customizedEntityConfiguration = (config == null) ? null : config.getCustomizedEntityCacheConfiguration(entityName);

        if (customizedEntityConfiguration != null) {
            final Collection<String> signedPropNames = new ArrayList<>(DirtyMarkerUtil.signedPropNames(entity));

            for (String propName : signedPropNames) {
                if (customizedEntityConfiguration.isExcludedProperty(propName)) {
                    entity.remove(propName);
                }
            }

            if (entity.keySet().size() == 0) {
                return false;
            }
        }

        try {
            return entityCache.put(entityId, entity, liveTime, maxIdleTime);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to put entity with id(" + N.toString(entityId) + "). ", e);
            }

            return false;
        }
    }

    @Override
    public void remove(EntityId entityId) {
        checkEntityId(entityId);

        try {
            entityCache.remove(entityId);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to remove entity with id(" + N.toString(entityId) + "). ", e);
            }
        }
    }

    @Override
    public boolean containsKey(EntityId entityId) {
        checkEntityId(entityId);

        return entityCache.containsKey(entityId);
    }

    @Override
    public Set<EntityId> keySet() {
        return entityCache.keySet();
    }

    @Override
    public int size() {
        return entityCache.size();
    }

    @Override
    public void clear() {
        entityCache.clear();
    }

    @Override
    public void close() {
        entityCache.close();
    }

    @Override
    public boolean isClosed() {
        return entityCache.isClosed();
    }

    private void checkEntityId(EntityId entityId) {
        if ((entityId == null) || entityId.isEmpty()) {
            throw new IllegalArgumentException("entityId can't be null or empty");
        }
    }

    private boolean isExcludedEntity(String entityName) {
        return (config != null) && config.isExcludedEntity(entityName);
    }

    private Cache<EntityId, MapEntity> newEntityCacheProviderInstance(EntityCacheConfiguration config) {
        if (config == null) {
            return CacheFactory.createLocalCache(EntityCacheConfiguration.DEFAULT_CAPACITY, EntityCacheConfiguration.DEFAULT_EVICT_DELAY);
        } else {
            if (config.getProvider() == null) {
                return CacheFactory.createLocalCache(config.getCapacity(), config.getEvictDelay());
            } else {
                return CacheFactory.createCache(config.getProvider());
            }
        }
    }

    @Override
    public Properties<String, Object> getProperties() {
        return entityCache.getProperties();
    }

    @Override
    public <T> T getProperty(String propName) {
        return entityCache.getProperty(propName);
    }

    @Override
    public <T> T setProperty(String propName, Object propValue) {
        return entityCache.setProperty(propName, propValue);
    }

    @Override
    public <T> T removeProperty(String propName) {
        return entityCache.removeProperty(propName);
    }
}

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

// TODO: Auto-generated Javadoc
/**
 * The Class EntityCacheDecorator.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
class EntityCacheDecorator extends AbstractCache<EntityId, MapEntity> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(EntityCacheDecorator.class);

    /** The config. */
    private final EntityCacheConfiguration config;

    /** The entity cache. */
    private final Cache<EntityId, MapEntity> entityCache;

    /**
     * Instantiates a new entity cache decorator.
     *
     * @param config
     */
    public EntityCacheDecorator(EntityCacheConfiguration config) {
        this.config = config;
        entityCache = newEntityCacheProviderInstance(config);

        if (config != null) {
            defaultLiveTime = config.getLiveTime();
            defaultMaxIdleTime = config.getMaxIdleTime();
        }
    }

    /**
     * Gets the t.
     *
     * @param entityId
     * @return
     */
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

    /**
     *
     * @param entityId
     * @param entity
     * @param liveTime
     * @param maxIdleTime
     * @return true, if successful
     */
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

    /**
     *
     * @param entityId
     */
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

    /**
     *
     * @param entityId
     * @return true, if successful
     */
    @Override
    public boolean containsKey(EntityId entityId) {
        checkEntityId(entityId);

        return entityCache.containsKey(entityId);
    }

    /**
     *
     * @return
     */
    @Override
    public Set<EntityId> keySet() {
        return entityCache.keySet();
    }

    /**
     *
     * @return
     */
    @Override
    public int size() {
        return entityCache.size();
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        entityCache.clear();
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        entityCache.close();
    }

    /**
     * Checks if is closed.
     *
     * @return true, if is closed
     */
    @Override
    public boolean isClosed() {
        return entityCache.isClosed();
    }

    /**
     * Check entity id.
     *
     * @param entityId
     */
    private void checkEntityId(EntityId entityId) {
        if ((entityId == null) || entityId.isEmpty()) {
            throw new IllegalArgumentException("entityId can't be null or empty");
        }
    }

    /**
     * Checks if is excluded entity.
     *
     * @param entityName
     * @return true, if is excluded entity
     */
    private boolean isExcludedEntity(String entityName) {
        return (config != null) && config.isExcludedEntity(entityName);
    }

    /**
     * New entity cache provider instance.
     *
     * @param config
     * @return
     */
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

    /**
     * Gets the properties.
     *
     * @return
     */
    @Override
    public Properties<String, Object> getProperties() {
        return entityCache.getProperties();
    }

    /**
     * Gets the property.
     *
     * @param <T>
     * @param propName
     * @return
     */
    @Override
    public <T> T getProperty(String propName) {
        return entityCache.getProperty(propName);
    }

    /**
     * Sets the property.
     *
     * @param <T>
     * @param propName
     * @param propValue
     * @return
     */
    @Override
    public <T> T setProperty(String propName, Object propValue) {
        return entityCache.setProperty(propName, propValue);
    }

    /**
     * Removes the property.
     *
     * @param <T>
     * @param propName
     * @return
     */
    @Override
    public <T> T removeProperty(String propName) {
        return entityCache.removeProperty(propName);
    }
}

/*
 * Copyright (C) 2015 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus.version;

import static com.landawn.abacus.cache.DistributedCacheClient.DEFAULT_TIMEOUT;
import static com.landawn.abacus.cache.DistributedCacheClient.MEMCACHED;
import static com.landawn.abacus.cache.DistributedCacheClient.REDIS;

import com.landawn.abacus.cache.JRedis;
import com.landawn.abacus.cache.SpyMemcached;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.StringUtil;
import com.landawn.abacus.util.TypeAttrParser;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Version objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class VersionFactory {

    private VersionFactory() {
        // singleton
    }

    /**
     * Creates a new Version object.
     *
     * @param <K> the key type
     * @return
     */
    public static <K> LocalVersion<K> createLocalVersion() {
        return new LocalVersion<>();
    }

    /**
     * Creates a new Version object.
     *
     * @param <K> the key type
     * @param capacity
     * @return
     */
    public static <K> LocalVersion<K> createLocalVersion(int capacity) {
        return new LocalVersion<>(capacity);
    }

    /**
     * Creates a new Version object.
     *
     * @param <K> the key type
     * @param provider
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K> Version<K> createVersion(String provider) {
        TypeAttrParser attrResult = TypeAttrParser.parse(provider);
        String[] parameters = attrResult.getParameters();
        String url = parameters[0];
        String className = attrResult.getClassName();
        Class<?> cls = null;

        if (MEMCACHED.equalsIgnoreCase(className)) {
            if (parameters.length == 1) {
                return new DistributedVersion<>(new SpyMemcached<Long>(url, DEFAULT_TIMEOUT));
            } else if (parameters.length == 2) {
                return new DistributedVersion<>(new SpyMemcached<Long>(url, DEFAULT_TIMEOUT), parameters[1], DistributedVersion.DEFAULT_LIVE_TIME);
            } else if (parameters.length == 3) {
                return new DistributedVersion<>(new SpyMemcached<Long>(url, DEFAULT_TIMEOUT), parameters[1], N.parseLong(parameters[2]));
            } else if (parameters.length == 4) {
                return new DistributedVersion<>(new SpyMemcached<Long>(url, N.parseLong(parameters[3])), parameters[1], N.parseLong(parameters[2]));
            } else {
                throw new IllegalArgumentException("Unsupported parameters: " + StringUtil.join(parameters));
            }
        } else if (REDIS.equalsIgnoreCase(className)) {
            if (parameters.length == 1) {
                return new DistributedVersion<>(new JRedis<Long>(url, DEFAULT_TIMEOUT));
            } else if (parameters.length == 2) {
                return new DistributedVersion<>(new JRedis<Long>(url, DEFAULT_TIMEOUT), parameters[1], DistributedVersion.DEFAULT_LIVE_TIME);
            } else if (parameters.length == 3) {
                return new DistributedVersion<>(new JRedis<Long>(url, DEFAULT_TIMEOUT), parameters[1], N.parseLong(parameters[2]));
            } else if (parameters.length == 4) {
                return new DistributedVersion<>(new JRedis<Long>(url, N.parseLong(parameters[3])), parameters[1], N.parseLong(parameters[2]));
            } else {
                throw new IllegalArgumentException("Unsupported parameters: " + StringUtil.join(parameters));
            }
        } else {
            cls = ClassUtil.forClass(className);

            return (Version<K>) TypeAttrParser.newInstance(cls, provider);
        }
    }
}

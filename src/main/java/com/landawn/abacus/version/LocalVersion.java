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

import java.util.LinkedHashMap;
import java.util.Map;

import com.landawn.abacus.util.MutableLong;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalVersion.
 *
 * @author Haiyang Li
 * @param <K> the key type
 * @since 0.8
 */
public final class LocalVersion<K> extends AbstractVersion<K> {

    /** The pool. */
    private final Map<K, MutableLong> pool;

    /** The capacity. */
    private final int capacity;

    /**
     * Instantiates a new local version.
     */
    public LocalVersion() {
        this(getDefaultCapacity());
    }

    /**
     * Instantiates a new local version.
     *
     * @param capacity the capacity
     */
    public LocalVersion(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity(" + capacity + ") can't be negative.");
        }

        this.capacity = (capacity == 0) ? getDefaultCapacity() : capacity;
        pool = new LinkedHashMap<K, MutableLong>(capacity);
    }

    /**
     * Gets the.
     *
     * @param k the k
     * @return
     */
    @Override
    public long get(K k) {
        synchronized (pool) {
            MutableLong version = pool.get(k);

            if (version == null) {
                if (pool.size() >= capacity) {
                    pool.remove(pool.keySet().iterator().next());
                }

                version = MutableLong.of(getStartNumber());
                pool.put(k, version);
            }

            return version.longValue();
        }
    }

    /**
     * Update.
     *
     * @param k the k
     * @param delta the delta
     */
    @Override
    public void update(K k, int delta) {
        synchronized (pool) {
            MutableLong version = pool.get(k);

            if (version != null) {
                version.longValue();
                version.add(delta);
            }
        }
    }

    /**
     * Removes the.
     *
     * @param k the k
     */
    @Override
    public void remove(K k) {
        synchronized (pool) {
            pool.remove(k);
        }
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        synchronized (pool) {
            pool.clear();
        }
    }
}

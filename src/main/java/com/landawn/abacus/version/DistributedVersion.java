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

import com.landawn.abacus.cache.DistributedCacheClient;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * The Class DistributedVersion.
 *
 * @author Haiyang Li
 * @param <K> the key type
 * @since 0.8
 */
public class DistributedVersion<K> extends AbstractVersion<K> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DistributedVersion.class);

    /** The Constant DEFAULT_LIVE_TIME. */
    static final long DEFAULT_LIVE_TIME = 24 * 60 * 60 * 1000L;

    /** The dcc. */
    private final DistributedCacheClient<Long> dcc;

    /** The key prefix. */
    private final String keyPrefix;

    /** The live time. */
    private final long liveTime;

    /**
     * Instantiates a new distributed version.
     *
     * @param dcc the dcc
     */
    public DistributedVersion(DistributedCacheClient<Long> dcc) {
        this(dcc, null, DEFAULT_LIVE_TIME);
    }

    /**
     * Instantiates a new distributed version.
     *
     * @param dcc the dcc
     * @param keyPrefix the key prefix
     * @param liveTime the live time
     */
    public DistributedVersion(DistributedCacheClient<Long> dcc, String keyPrefix, long liveTime) {
        this.dcc = dcc;
        this.keyPrefix = N.isNullOrEmpty(keyPrefix) ? N.EMPTY_STRING : keyPrefix;
        this.liveTime = (liveTime == 0) ? DEFAULT_LIVE_TIME : liveTime;
    }

    /**
     * Gets the.
     *
     * @param k the k
     * @return the long
     */
    @Override
    public long get(K k) {
        Long num = dcc.get(generateKey(k));

        if (num == null) {
            num = getStartNumber();
            dcc.set(generateKey(k), num, liveTime);
        }

        return num.longValue();
    }

    /**
     * Update.
     *
     * @param k the k
     * @param delta the delta
     */
    @Override
    public void update(K k, int delta) {
        try {
            dcc.incr(generateKey(k), delta);
        } catch (Exception e) {
            // ignore
            if (logger.isWarnEnabled()) {
                logger.warn(AbacusException.getErrorMsg(e));
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
        try {
            dcc.delete(generateKey(k));
        } catch (Exception e) {
            // ignore;
            if (logger.isWarnEnabled()) {
                logger.warn(AbacusException.getErrorMsg(e));
            }
        }
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        try {
            dcc.flushAll();
        } catch (Exception e) {
            // ignore;
            if (logger.isWarnEnabled()) {
                logger.warn(AbacusException.getErrorMsg(e));
            }
        }
    }

    /**
     * Generate key.
     *
     * @param k the k
     * @return the string
     */
    protected String generateKey(K k) {
        return N.isNullOrEmpty(keyPrefix) ? N.base64Encode(N.toString(k).getBytes()) : (keyPrefix + N.base64Encode(N.toString(k).getBytes()));
    }
}

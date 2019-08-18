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

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractVersion.
 *
 * @author Haiyang Li
 * @param <K> the key type
 * @since 0.8
 */
public abstract class AbstractVersion<K> implements Version<K> {
    // private static final int MAX_UPDATE_COUNT = 100000000;

    /**
     * Gets the default capacity.
     *
     * @return
     */
    protected static int getDefaultCapacity() {
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 * 1024; // N.ONE_MB;

        if (maxMemory >= 2000) {
            return 1000000;
        } else if (maxMemory >= 1000) {
            return 200000;
        } else if (maxMemory >= 500) {
            return 50000;
        } else {
            return 10000;
        }
    }

    /**
     * Gets the start number.
     *
     * @return
     */
    protected long getStartNumber() {
        // return (System.currentTimeMillis() % (1000000000000000000L /
        // MAX_UPDATE_COUNT)) * MAX_UPDATE_COUNT;
        return System.currentTimeMillis();
    }
}

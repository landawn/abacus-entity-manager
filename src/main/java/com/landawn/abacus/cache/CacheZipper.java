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

package com.landawn.abacus.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.util.ExceptionUtil;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * The Class CacheZipper.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class CacheZipper {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CacheZipper.class);

    /** The Constant CPU_CORES_FOR_ZIP. */
    private static final int CPU_CORES_FOR_ZIP = N.max(1, IOUtil.CPU_CORES / 2);

    /** The Constant EXECUTOR_SERVICE. */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(CPU_CORES_FOR_ZIP);

    /** The last scan time. */
    private volatile long lastScanTime = System.currentTimeMillis();

    //    static {
    //        Runtime.getRuntime().addShutdownHook(new Thread() {
    //            @Override
    //            public void run() {
    //                logger.warn("Starting to shutdown task in CacheZipper");
    //
    //                try {
    //                    EXECUTOR_SERVICE.shutdown();
    //
    //                    while (EXECUTOR_SERVICE.isTerminated() == false) {
    //                        N.sleepUninterruptibly(100);
    //                    }
    //                } finally {
    //                    logger.warn("Completed to shutdown task in CacheZipper");
    //                }
    //            }
    //        });
    //    }

    /**
     *
     * @param cacheList
     */
    public void zip(Collection<? extends QueryCache> cacheList) {
        boolean isCacheUpdated = false;

        for (QueryCache queryCache : cacheList) {
            if (queryCache.getLastUpdateTime() > lastScanTime) {
                isCacheUpdated = true;
            }
        }

        if (!isCacheUpdated) {
            return;
        }

        // Runtime.getRuntime().gc();

        final AtomicInteger activeThreadNum = new AtomicInteger();
        final Object[] hashArray = CacheZipper.createBigArray();

        if (hashArray.length > 0) {
            final List<List<QueryCache>> threadCachesList = divideCacheByProcessor(cacheList);

            for (final List<QueryCache> threadCaches : threadCachesList) {
                final Runnable command = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            zipCache(hashArray, threadCaches);
                        } finally {
                            activeThreadNum.decrementAndGet();
                        }
                    }
                };

                activeThreadNum.incrementAndGet();

                EXECUTOR_SERVICE.execute(command);
            }

            while (activeThreadNum.get() > 0) {
                N.sleep(1);
            }

            lastScanTime = System.currentTimeMillis();

            // Runtime.getRuntime().gc();
        }
    }

    /**
     *
     * @param hashArray
     * @param cacheList
     */
    @SuppressWarnings("unchecked")
    void zipCache(final Object[] hashArray, final List<? extends QueryCache> cacheList) {
        final int BIT_INDEX = hashArray.length - 1;
        int hitNum = 0;

        for (QueryCache queryCache : cacheList) {
            final DataGrid<Object> dataGrid = queryCache.getDataGrid();

            if (dataGrid != null) {
                final boolean isUpdatedCache = queryCache.getLastUpdateTime() > lastScanTime;

                synchronized (queryCache) {
                    final Collection<String> propNames = queryCache.getCachedPropNames();
                    final int size = dataGrid.getY();

                    Object obj = null;
                    int hashCode = 0;
                    int index = 0;
                    outer: for (String propName : propNames) {
                        if (queryCache.isClosed() || queryCache.activityPrint().isExpired()) {
                            break;
                        }

                        final int x = queryCache.getPropIndex(propName);

                        for (int y = 0; y < size; y++) {
                            obj = dataGrid.get(x, y);

                            if (obj != null) {
                                if (obj instanceof Boolean || obj instanceof Byte || obj instanceof Character) {
                                    continue outer;
                                } else {
                                    break;
                                }
                            }
                        }

                        if (isUpdatedCache) {
                            for (int y = 0; y < size; y++) {
                                obj = dataGrid.get(x, y);

                                if (obj != null) {
                                    hashCode = obj.hashCode();
                                    index = BIT_INDEX & hashCode;

                                    if (hashArray[index] == null) {
                                        hashArray[index] = obj;
                                    } else if (obj.equals(hashArray[index])) {
                                        dataGrid.put(x, y, hashArray[index]);

                                        hitNum++;
                                    } else {
                                        hashArray[index] = obj;
                                    }
                                }
                            }
                        } else {
                            for (int y = 0; y < size; y++) {
                                obj = dataGrid.get(x, y);

                                if (obj != null) {
                                    hashCode = obj.hashCode();
                                    index = BIT_INDEX & hashCode;

                                    hashArray[index] = obj;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (logger.isWarnEnabled()) {
            logger.warn(hitNum + " objects have been zipped.");
        }
    }

    /**
     * Divide cache by processor.
     *
     * @param cacheList
     * @return
     */
    static List<List<QueryCache>> divideCacheByProcessor(Collection<? extends QueryCache> cacheList) {
        List<List<QueryCache>> threadCachesList = new ArrayList<>();

        for (int i = 0; i < CPU_CORES_FOR_ZIP; i++) {
            threadCachesList.add(new ArrayList<QueryCache>());
        }

        int i = 0;

        for (QueryCache queryCache : cacheList) {
            threadCachesList.get(i % CPU_CORES_FOR_ZIP).add(queryCache);

            i++;
        }

        return threadCachesList;
    }

    /**
     * Creates the big array.
     *
     * @return
     */
    public static Object[] createBigArray() {
        final long M8 = 1024 * 1024 * 8; // N.ONE_MB * 8;

        int multi = (int) (Runtime.getRuntime().maxMemory() / M8);
        int memScale = 0;

        while ((multi = multi >> 1) > 0) {
            memScale++;
        }

        memScale = Math.min(memScale, 17);

        Object[] objects = null;

        switch (memScale) {
            case 17: // 1024G or >= 1T

                try {
                    objects = new Object[(int) Math.pow(2, 30)]; // length =
                    // 107374W, mem =
                    // 4G;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 16: // 512G

                try {
                    objects = new Object[(int) Math.pow(2, 29)]; // length = 53687W,
                    // mem = 2G;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;
                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 15: // 256G

                try {
                    objects = new Object[(int) Math.pow(2, 28)]; // length = 26843W,
                    // mem = 1G;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 14: // 128G

                try {
                    objects = new Object[(int) Math.pow(2, 27)]; // length = 13421W,
                    // mem = 512M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 13: // 64G

                try {
                    objects = new Object[(int) Math.pow(2, 26)]; // length = 6710W,
                    // mem = 256M

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 12: // 32G

                try {
                    objects = new Object[(int) Math.pow(2, 25)]; // 3355W, mem =
                    // 128M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 11: // 16G

                try {
                    objects = new Object[(int) Math.pow(2, 24)]; // 1677W, mem =
                    // 64M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 10: // 8G

                try {
                    objects = new Object[(int) Math.pow(2, 23)]; // 838W, mem = 32M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 9: // 4G

                try {
                    objects = new Object[(int) Math.pow(2, 22)]; // 419W, mem = 16M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 8: // 2G

                try {
                    objects = new Object[(int) Math.pow(2, 21)]; // 209W, mem = 8M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 7: // 1G

                try {
                    objects = new Object[(int) Math.pow(2, 20)]; // 104W, mem = 4M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 6: // 512M

                try {
                    if (memScale <= 6) {
                        objects = new Object[(int) Math.pow(2, 19)]; // 52W, mem =
                        // 2M;

                        break;
                    } else {
                        return new Object[0];
                    }
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 5: // 256M

                try {
                    objects = new Object[(int) Math.pow(2, 18)]; // 26W, mem = 1M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 4: // 128M

                try {
                    objects = new Object[(int) Math.pow(2, 17)]; // 13W, mem = 0.5M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 3: // 64M

                try {
                    objects = new Object[(int) Math.pow(2, 16)]; // 6.5W, mem = 0.25M;

                    break;
                } catch (OutOfMemoryError e) {
                    // ignore;

                    if (logger.isWarnEnabled()) {
                        logger.warn(ExceptionUtil.getMessage(e));
                    }
                }

            case 2: // 32M
            case 1: // 16M
            case 0: // < 16M
            default:

                try {
                    if (memScale <= 2) {
                        objects = new Object[10000];

                        break;
                    } else {
                        objects = new Object[0];
                    }
                } catch (OutOfMemoryError e) {
                    objects = new Object[0];
                }
        }

        return objects;
    }
}

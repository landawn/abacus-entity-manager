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

package com.landawn.abacus.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.landawn.abacus.EntityManager;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.util.ExceptionUtil;
import com.landawn.abacus.util.MoreExecutors;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @param <E>
 * @since 0.8
 */
public final class AsyncBatchExecutor<E> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncBatchExecutor.class);

    public static final int DEFAULT_CAPACITY = 8192;

    public static final int DEFAULT_EVICT_DELAY = 3000;

    /**
     * The Constant DEFAULT_BATCH_SIZE.
     *
     * @see Options#DEFAULT_BATCH_SIZE
     */
    public static final int DEFAULT_BATCH_SIZE = Options.DEFAULT_BATCH_SIZE;

    static final ScheduledExecutorService scheduledExecutor;
    static {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(16);
        executor.setKeepAliveTime(180, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        executor.setRemoveOnCancelPolicy(true);
        scheduledExecutor = MoreExecutors.getExitingScheduledExecutorService(executor);
    }

    private final ScheduledFuture<?> scheduleFuture;

    protected final EntityManager<E> em;

    protected final Map<String, Object> options;

    protected final List<E> addQueue;

    protected final List<E> updateQueue;

    protected final List<E> deleteQueue;

    /** Unit is millisecond. */
    private final int capacity;

    private final long evictDelay;

    private final int batchSize;

    private boolean isClosed = false;

    public AsyncBatchExecutor(final EntityManager<E> em) {
        this(em, DEFAULT_CAPACITY, DEFAULT_EVICT_DELAY, DEFAULT_BATCH_SIZE);
    }

    public AsyncBatchExecutor(final EntityManager<E> em, final int capacity, final long evictDelay, final int batchSize) {
        if ((em == null) || (evictDelay <= 0) || (batchSize <= 0) || (capacity <= 0)) {
            throw new IllegalArgumentException();
        }

        this.em = em;

        this.addQueue = new ArrayList<>(capacity);
        this.updateQueue = new ArrayList<>(capacity);
        this.deleteQueue = new ArrayList<>(capacity);

        this.evictDelay = evictDelay;
        this.batchSize = batchSize;
        this.capacity = capacity;

        this.options = N.asProps(Options.BATCH_SIZE, batchSize);

        // start evict process.
        final Runnable commitTask = new Runnable() {
            @Override
            public void run() {
                if ((addQueue.size() > 0) || (updateQueue.size() > 0) || (deleteQueue.size() > 0)) {
                    commit();
                }
            }
        };

        scheduleFuture = scheduledExecutor.scheduleWithFixedDelay(commitTask, evictDelay, evictDelay, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.warn("Starting to shutdown task in AsyncBatchExecutor");

                try {
                    close();
                } finally {
                    logger.warn("Completed to shutdown task in AsyncBatchExecutor");
                }
            }
        });
    }

    /**
     * Gets the evict delay.
     *
     * @return
     */
    public long getEvictDelay() {
        return evictDelay;
    }

    /**
     * Gets the batch size.
     *
     * @return
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Gets the capacity.
     *
     * @return
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     *
     * @param e
     */
    public void add(E e) {
        assertNotClosed();

        synchronized (addQueue) {
            addElement(addQueue, e);
        }
    }

    /**
     *
     * @param e
     */
    public void update(E e) {
        assertNotClosed();

        synchronized (updateQueue) {
            addElement(updateQueue, e);
        }
    }

    /**
     *
     * @param e
     */
    public void delete(E e) {
        assertNotClosed();

        synchronized (deleteQueue) {
            addElement(deleteQueue, e);
        }
    }

    /**
     * Commit.
     */
    @SuppressWarnings("unchecked")
    public void commit() {
        assertNotClosed();

        internalCommit();
    }

    /**
     * Internal commit.
     */
    private void internalCommit() {
        if (addQueue.size() > 0) {
            List<E> entities = new ArrayList<>();

            synchronized (addQueue) {
                entities.addAll(addQueue);
                addQueue.clear();
            }

            if (logger.isInfoEnabled()) {
                logger.info("START-BATCH-ADD[" + entities.size() + "]");
            }

            for (int size = entities.size(), from = 0, to = Math.min(from + batchSize, size); from < size; from = to, to = Math.min(from + batchSize, size)) {
                try {
                    em.addAll(entities.subList(from, to), options);
                } catch (Exception e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("FAILED-BATCH-ADD[" + entities.size() + "]. " + ExceptionUtil.getMessage(e));
                    }
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info("END-BATCH-ADD[" + entities.size() + "]");
            }
        }

        if (updateQueue.size() > 0) {
            List<E> entities = new ArrayList<>();

            synchronized (updateQueue) {
                entities.addAll(updateQueue);
                updateQueue.clear();
            }

            if (logger.isInfoEnabled()) {
                logger.info("START-BATCH-UPDATE[" + entities.size() + "]");
            }

            for (int size = entities.size(), from = 0, to = Math.min(from + batchSize, size); from < size; from = to, to = Math.min(from + batchSize, size)) {
                try {
                    em.updateAll(entities.subList(from, to), options);
                } catch (Exception e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("FAILED-BATCH-UPDATE[" + entities.size() + "]. " + ExceptionUtil.getMessage(e));
                    }
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info("END-BATCH-UPDATE[" + entities.size() + "]");
            }
        }

        if (deleteQueue.size() > 0) {
            List<E> entities = new ArrayList<>();

            synchronized (deleteQueue) {
                entities.addAll(deleteQueue);
                deleteQueue.clear();
            }

            if (logger.isInfoEnabled()) {
                logger.info("START-BATCH-DELETE[" + entities.size() + "]");
            }

            for (int size = entities.size(), from = 0, to = Math.min(from + batchSize, size); from < size; from = to, to = Math.min(from + batchSize, size)) {
                try {
                    em.deleteAll(entities.subList(from, to), options);
                } catch (Exception e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("FAILED-BATCH-DELETE[" + entities.size() + "]. " + ExceptionUtil.getMessage(e));
                    }
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info("END-BATCH-DELETE[" + entities.size() + "]");
            }
        }
    }

    /**
     * Adds the element.
     *
     * @param queue
     * @param e
     */
    protected void addElement(List<E> queue, E e) {
        if ((addQueue.size() + updateQueue.size() + deleteQueue.size()) > capacity) {
            String msg = "Queue is full. The capacity is " + capacity;
            throw new RuntimeException(msg);
        }

        queue.add(e);
    }

    /**
     * Close.
     */
    public void close() {
        if (isClosed) {
            return;
        }

        isClosed = true;

        try {
            if (scheduleFuture != null) {
                scheduleFuture.cancel(true);
            }
        } finally {
            internalCommit();
        }
    }

    /**
     * Assert not closed.
     */
    protected void assertNotClosed() {
        if (isClosed) {
            throw new RuntimeException("This object pool has been closed");
        }
    }
}

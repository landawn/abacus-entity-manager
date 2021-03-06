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
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.landawn.abacus.pool.AbstractPoolable;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options.Cache;
import com.landawn.abacus.util.Properties;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public abstract class AbstractQueryCache extends AbstractPoolable implements QueryCache {

    protected final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    protected final Properties<String, Integer> propNameIndexMap = new Properties<>();

    protected long lastUpdatedTime = System.currentTimeMillis();

    protected boolean isClosed = false;

    protected AbstractQueryCache(long liveTime, long maxIdleTime) {
        super(liveTime, maxIdleTime);
    }

    /**
     * Lock.
     */
    @Override
    public void lock() {
        rwLock.readLock().lock();
    }

    /**
     * Unlock.
     */
    @Override
    public void unlock() {
        rwLock.readLock().unlock();
    }

    /**
     * Gets the prop index.
     *
     * @param propName
     * @return
     */
    @Override
    public int getPropIndex(String propName) {
        assertNotClosed();

        return propNameIndexMap.getOrDefault(propName, -1);
    }

    /**
     * Gets the cached prop names.
     *
     * @return
     */
    @Override
    public Collection<String> getCachedPropNames() {
        assertNotClosed();

        lock();

        try {
            return (getDataGrid() == null) ? N.<String> emptyList() : propNameIndexMap.keySet();
        } finally {
            unlock();
        }
    }

    /**
     * Checks if is cached result.
     *
     * @param propName
     * @param range
     * @return true, if is cached result
     */
    @Override
    public boolean isCachedResult(String propName, Cache.Range range) {
        assertNotClosed();

        lock();

        try {
            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid == null) {
                return false;
            }

            Integer proxIndex = propNameIndexMap.get(propName);

            if (proxIndex == null) {
                return false;
            } else {
                final BitSet bitSet = range.getRangeBitSet();

                for (int fromIndex = bitSet.nextSetBit(0), endIndex = 0; fromIndex > -1; fromIndex = bitSet.nextSetBit(endIndex)) {
                    endIndex = bitSet.nextClearBit(fromIndex);

                    if (!dataGrid.isXFull(proxIndex, fromIndex, endIndex)) {
                        return false;
                    }
                }

                return true;
            }
        } finally {
            unlock();
        }
    }

    /**
     * Gets the result.
     *
     * @param propName
     * @param beginIndex
     * @param endIndex
     * @return
     */
    @Override
    public Object[] getResult(String propName, int beginIndex, int endIndex) {
        assertNotClosed();

        checkBound(beginIndex, endIndex);

        lock();

        Object[] result = null;

        try {
            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid == null) {
                result = new Object[0];
            } else {
                result = dataGrid.getX(checkPropIndex(propName), beginIndex, endIndex);
            }
        } finally {
            unlock();
        }

        return result;
    }

    /**
     * Gets the result.
     *
     * @param propNames
     * @param beginIndex
     * @param endIndex
     * @return
     */
    @Override
    public Object[][] getResult(Collection<String> propNames, int beginIndex, int endIndex) {
        assertNotClosed();

        checkBound(beginIndex, endIndex);

        lock();

        Object[][] result = null;

        try {
            if (!propNameIndexMap.keySet().containsAll(propNames)) {
                List<String> temp = new ArrayList<>(propNames);
                temp.removeAll(propNameIndexMap.keySet());
                throw new IllegalArgumentException(
                        "This cache(" + propNameIndexMap.keySet() + ") doesn't cache result for the properties( " + temp.toString() + "). ");
            }

            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid == null) {
                result = new Object[0][0];
            } else {
                result = new Object[propNames.size()][];

                int i = 0;

                for (String propName : propNames) {
                    result[i++] = dataGrid.getX(propNameIndexMap.get(propName), beginIndex, endIndex);
                }
            }
        } finally {
            unlock();
        }

        return result;
    }

    /**
     * Gets the result.
     *
     * @param beginIndex
     * @param endIndex
     * @return
     */
    @Override
    public Object[][] getResult(int beginIndex, int endIndex) {
        return getResult(propNameIndexMap.keySet(), beginIndex, endIndex);
    }

    /**
     * Gets the last update time.
     *
     * @return
     */
    @Override
    public long getLastUpdateTime() {
        assertNotClosed();

        return lastUpdatedTime;
    }

    @Override
    public int size() {
        assertNotClosed();

        lock();

        try {
            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid == null) {
                // not initialized
                return -1;
            } else {
                return dataGrid.getY();
            }
        } finally {
            unlock();
        }
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        if (isClosed) {
            return;
        }

        rwLock.writeLock().lock();

        try {
            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid != null) {
                dataGrid.clear();
            }

            propNameIndexMap.clear();
            lastUpdatedTime = System.currentTimeMillis();
            isClosed = true;
        } finally {
            rwLock.writeLock().unlock();
        }

        // Runtime.getRuntime().gc();
    }

    /**
     * Checks if is closed.
     *
     * @return true, if is closed
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Destroy.
     */
    @Override
    public void destroy() {
        close();
    }

    @Override
    public String toString() {
        DataGrid<Object> dataGrid = getDataGrid();

        if (dataGrid == null) {
            return propNameIndexMap.keySet().toString();
        } else {
            return propNameIndexMap.keySet().toString() + IOUtil.LINE_SEPARATOR + dataGrid.toString();
        }
    }

    /**
     * Check prop index.
     *
     * @param propName
     * @return
     */
    protected int checkPropIndex(String propName) {
        Integer propIndex = propNameIndexMap.get(propName);

        if (propIndex == null) {
            throw new IllegalArgumentException("This cache: " + propNameIndexMap.keySet() + " doesn't contain the result for the property: " + propName);
        }

        return propIndex;
    }

    /**
     *
     * @param beginIndex
     * @param endIndex
     */
    protected void checkBound(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException("The beginIndex[" + beginIndex + "] must not be negative. ");
        }

        if (endIndex < 0) {
            throw new IllegalArgumentException("The endIndex[" + endIndex + "] must not be negative. ");
        }

        if (beginIndex > endIndex) {
            throw new IllegalArgumentException("The beginIndex[" + beginIndex + "] should less than or equal to the endIndex[" + endIndex + "]. ");
        }
    }

    /**
     * Assert not closed.
     */
    protected void assertNotClosed() {
        if (isClosed) {
            throw new IllegalStateException(ClassUtil.getSimpleClassName(this.getClass()) + " has been closed");
        }
    }
}

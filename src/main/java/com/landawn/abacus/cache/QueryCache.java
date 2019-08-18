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

import java.util.Collection;
import java.util.Map;

import com.landawn.abacus.core.SQLResult;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.pool.Poolable;
import com.landawn.abacus.util.Options.Cache;

// TODO: Auto-generated Javadoc
/**
 * The Interface QueryCache.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface QueryCache extends Poolable {
    /**
     * Method lock.
     */
    void lock();

    /**
     * Method unLock.
     */
    void unlock();

    /**
     * Method getPropIndex.
     *
     * @param propName the prop name
     * @return int
     */
    int getPropIndex(String propName);

    /**
     * Method cacheResult.
     *
     * @param queryResult the query result
     * @param cachePropNames the cache prop names
     * @param cond the cond
     * @param range the range
     */
    void cacheResult(SQLResult queryResult, Collection<String> cachePropNames, Cache.Condition cond, Cache.Range range);

    /**
     * Method asyncCacheResult.
     *
     * @param queryResult the query result
     * @param cachePropNames the cache prop names
     * @param cacheCond the cache cond
     * @param range the range
     * @param closeResult the close result
     */
    void asyncCacheResult(final SQLResult queryResult, final Collection<String> cachePropNames, final Cache.Condition cacheCond, final Cache.Range range,
            final boolean closeResult);

    /**
     * Method update.
     *
     * @param command the command
     * @param options the options
     * @return boolean
     */
    boolean update(Command command, Map<String, Object> options);

    /**
     * Method getCachedPropNames.
     * 
     * @return Collection<String>
     */
    Collection<String> getCachedPropNames();

    /**
     * Method isCachedResult.
     *
     * @param propName the prop name
     * @param range the range
     * @return boolean
     */
    boolean isCachedResult(String propName, Cache.Range range);

    /**
     * Method getDataGrid.
     * 
     * @return DataGrid<Object>
     */
    DataGrid<Object> getDataGrid();

    /**
     * Method getResult.
     *
     * @param propName the prop name
     * @param beginIndex the begin index
     * @param endIndex the end index
     * @return Object[]
     */
    Object[] getResult(String propName, int beginIndex, int endIndex);

    /**
     * Method getResult.
     *
     * @param propNames the prop names
     * @param beginIndex the begin index
     * @param endIndex the end index
     * @return Object[][]
     */
    Object[][] getResult(Collection<String> propNames, int beginIndex, int endIndex);

    /**
     * Method getResult.
     *
     * @param beginIndex the begin index
     * @param endIndex the end index
     * @return Object[][]
     */
    Object[][] getResult(int beginIndex, int endIndex);

    /**
     * Removes the result.
     *
     * @param propName the prop name
     */
    void removeResult(String propName);

    /**
     * Gets the last update time.
     *
     * @return
     */
    long getLastUpdateTime();

    /**
     * Size.
     *
     * @return
     */
    int size();

    /**
     * Zip.
     */
    void zip();

    /**
     * Close.
     */
    void close();

    /**
     * Checks if is closed.
     *
     * @return true, if is closed
     */
    boolean isClosed();
}

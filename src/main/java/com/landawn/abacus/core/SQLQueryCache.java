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

import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.cache.AbstractQueryCache;
import com.landawn.abacus.cache.Cache;
import com.landawn.abacus.cache.DataGrid;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.QueryCacheConfiguration;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.core.command.SQLCommandFactory;
import com.landawn.abacus.core.command.SQLOperationCommand;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.AsyncExecutor;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Try;
import com.landawn.abacus.util.WD;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLQueryCache.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
class SQLQueryCache extends AbstractQueryCache {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SQLQueryCache.class);

    /** The Constant WHERE. */
    private static final String WHERE = WD.SPACE + WD.WHERE + WD.SPACE;

    /** The Constant WHERE_PARENTHESES_L. */
    private static final String WHERE_PARENTHESES_L = WHERE + WD._PARENTHESES_L;

    /** The Constant PARENTHESES_R_AND_PARENTHESES_L. */
    private static final String PARENTHESES_R_AND_PARENTHESES_L = WD.PARENTHESES_R_SPACE + WD.AND + WD.SPACE_PARENTHESES_L;

    /** The Constant ASYNC_EXECUTOR. */
    private static final AsyncExecutor ASYNC_EXECUTOR = new AsyncExecutor();

    /** The Constant CACHED_DATA_GRID_ID_IN_EXECUTING. */
    private static final Map<String, Object> CACHED_DATA_GRID_ID_IN_EXECUTING = new ConcurrentHashMap<>();

    /** The data grid cache. */
    private final Cache<String, DataGrid<Object>> dataGridCache;

    /** The data grid cache id. */
    private final String dataGridCacheId;

    /** The soft data grid. */
    private SoftReference<DataGrid<Object>> softDataGrid;

    /** The executant. */
    private Executant executant;

    /** The query cmd. */
    private SQLOperationCommand queryCmd;

    /**
     * Instantiates a new SQL query cache.
     */
    public SQLQueryCache() {
        this(QueryCacheConfiguration.DEFAULT_LIVE_TIME, QueryCacheConfiguration.DEFAULT_MAX_IDLE_TIME, null);
    }

    /**
     * Instantiates a new SQL query cache.
     *
     * @param liveTime
     * @param maxIdleTime
     * @param dataGridCache
     */
    public SQLQueryCache(long liveTime, long maxIdleTime, Cache<String, DataGrid<Object>> dataGridCache) {
        super(liveTime, maxIdleTime);
        this.dataGridCache = dataGridCache;

        this.dataGridCacheId = dataGridCache == null ? null : N.uuid();
    }

    /**
     *
     * @param queryResult
     * @param cachePropNames
     * @param cacheCond
     * @param range
     */
    @Override
    public void cacheResult(SQLResult queryResult, Collection<String> cachePropNames, Options.Cache.Condition cacheCond, Options.Cache.Range range) {
        assertNotClosed();

        try {
            synchronized (queryResult) {
                if ((queryResult.getExecutionTime() >= cacheCond.getMinQueryTime())
                        || ((queryResult.size() >= cacheCond.getMinCount()) && (queryResult.size() <= cacheCond.getMaxCount()))) {

                    cacheResult(queryResult, range.getRangeBitSet(), cachePropNames);

                    lastUpdatedTime = System.currentTimeMillis();
                }

                queryCmd = (SQLOperationCommand) queryResult.getSQLCommand();
                executant = queryResult.getExecutant();
            }
        } catch (Exception e) {
            logger.error("Failed to cache result by condition: " + cacheCond, e);
        }
    }

    /**
     * Async cache result.
     *
     * @param queryResult
     * @param cachePropNames
     * @param cacheCond
     * @param range
     * @param closeResult
     */
    @Override
    public void asyncCacheResult(final SQLResult queryResult, final Collection<String> cachePropNames, final Options.Cache.Condition cacheCond,
            final Options.Cache.Range range, final boolean closeResult) {
        assertNotClosed();

        ASYNC_EXECUTOR.execute(new Try.Runnable<RuntimeException>() {
            @Override
            public void run() {
                try {
                    cacheResult(queryResult, cachePropNames, cacheCond, range);
                } finally {
                    if (closeResult) {
                        queryResult.close();
                    }
                }
            }
        });
    }

    /**
     *
     * @param queryResult
     * @param rowRange
     * @param selectPropNames
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    private void cacheResult(SQLResult queryResult, BitSet rowRange, Collection<String> selectPropNames) throws UncheckedSQLException {
        DataGrid<Object> dataGrid = null;

        rwLock.writeLock().lock();

        try {
            if (isClosed()) {
                return;
            }

            int[] propPositionInThisCache = getPropPositionInCache(selectPropNames);

            dataGrid = init(queryResult, selectPropNames);

            int propCount = selectPropNames.size();

            for (int fromIndex = rowRange.nextSetBit(0), endIndex = 0; fromIndex > -1; fromIndex = rowRange.nextSetBit(endIndex)) {
                endIndex = rowRange.nextClearBit(fromIndex);

                if (queryResult.absolute(fromIndex)) {
                    int rowNum = fromIndex;

                    do {
                        for (int propIndex = 0; propIndex < propCount; propIndex++) {
                            if (dataGrid.isClean(propPositionInThisCache[propIndex], rowNum)) {
                                dataGrid.put(propPositionInThisCache[propIndex], rowNum, queryResult.get(propIndex));
                            }
                        }

                        rowNum++;
                    } while (queryResult.next() && (rowNum < endIndex));
                }
            }

            if ((dataGridCache != null) && (dataGrid != null)) {
                boolean isToCache = false;

                synchronized (CACHED_DATA_GRID_ID_IN_EXECUTING) {
                    if (!CACHED_DATA_GRID_ID_IN_EXECUTING.containsKey(dataGridCacheId)) {
                        CACHED_DATA_GRID_ID_IN_EXECUTING.put(dataGridCacheId, dataGridCacheId);

                        isToCache = true;
                    }
                }

                if (isToCache) {
                    try {
                        if (dataGrid != null) {
                            dataGridCache.put(dataGridCacheId, dataGrid, activityPrint().getLiveTime(), activityPrint().getMaxIdleTime());
                        }
                    } finally {
                        CACHED_DATA_GRID_ID_IN_EXECUTING.remove(dataGridCacheId);
                    }
                }
            }

        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        } finally {
            rwLock.writeLock().unlock();
        }

        //
        //        if (dataGridCache != null) {
        //            synchronized (CACHED_DATA_GRID_ID_IN_EXECUTING) {
        //                if (!CACHED_DATA_GRID_ID_IN_EXECUTING.containsKey(dataGridIdInCache)) {
        //                    CACHED_DATA_GRID_ID_IN_EXECUTING.put(dataGridIdInCache, dataGridIdInCache);
        //
        //                    Runnable command = new Runnable() {
        //                        /**
        //                         * Method run.
        //                         * 
        //                         * @see java.lang.Runnable#run()
        //                         */
        //                        @Override
        //                        public void run() {
        //                            rwLock.writeLock().lock();
        //
        //                            try {
        //                                if (!isClosed()) {
        //                                    final DataGrid<Object> dataGrid = getDataGrid();
        //
        //                                    if (dataGrid != null) {
        //                                        dataGridCache.put(dataGridIdInCache, dataGrid, getActivityPrint().getLiveTime(), getActivityPrint().getMaxIdleTime());
        //                                    }
        //                                }
        //                            } finally {
        //                                CACHED_DATA_GRID_ID_IN_EXECUTING.remove(dataGridIdInCache);
        //                                rwLock.writeLock().unlock();
        //                            }
        //                        }
        //                    };
        //
        //                    ASYNC_EXECUTOR.execute(command);
        //                }
        //            }
        //        }
    }

    /**
     *
     * @param command
     * @param options
     * @return true, if successful
     */
    @Override
    public synchronized boolean update(Command command, Map<String, Object> options) {
        assertNotClosed();

        if (command.getOperationType() == OperationType.QUERY) {
            return true;
        }

        SQLOperationCommand updateCmd = (SQLOperationCommand) command;

        if ((queryCmd == null) || (queryCmd.getWhereBeginIndex() < 0) || (updateCmd.getWhereBeginIndex() < 0)) {
            return false;
        }

        for (String updateTableName : updateCmd.getTargetTables()) {
            if (N.notNullOrEmpty(queryCmd.getSubQueryTables())) {
                for (String tableName : queryCmd.getSubQueryTables()) {
                    if (tableName.equalsIgnoreCase(updateTableName)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Remove query cache because the table in sub query is updated. Cache query SQL: " + queryCmd.getSql() + ". Update SQL: "
                                    + updateCmd.getParameterCount());
                        }

                        return false;
                    }
                }
            }

            boolean isUpdated = false;

            if (N.notNullOrEmpty(queryCmd.getTargetTables())) {
                for (String tableName : queryCmd.getTargetTables()) {
                    if (tableName.equalsIgnoreCase(updateTableName)) {
                        isUpdated = true;

                        break;
                    }
                }
            }

            if (!isUpdated && N.notNullOrEmpty(queryCmd.getJoinTables())) {
                for (String tableName : queryCmd.getJoinTables()) {
                    if (tableName.equalsIgnoreCase(updateTableName)) {
                        isUpdated = true;

                        break;
                    }
                }
            }

            //
            //            // TODO it's too complicate to check update and just return false.
            //            if (isUpdated) {
            //                return false;
            //            }
            //

            if (isUpdated) {
                final EntityDefinition updateEntityDef = updateCmd.getEntityDef();

                if (updateCmd.getOperationType() == OperationType.UPDATE && queryCmd.getWhereBeginIndex() > 0) {
                    String lowerCaseQuerySql = queryCmd.getSql().toLowerCase();

                    int querySqlWhereBeginIndex = queryCmd.getWhereBeginIndex();
                    int querySqlWhereEndIndex = queryCmd.getWhereEndIndex();
                    Property prop = null;
                    int index = -1;

                    for (String propName : updateCmd.getTargetPropNames()) {
                        prop = updateEntityDef.getProperty(propName);

                        index = lowerCaseQuerySql.indexOf(prop.getColumnName().toLowerCase(), querySqlWhereBeginIndex);

                        if ((index >= 0) && (index < querySqlWhereEndIndex)) {
                            if (logger.isInfoEnabled()) {
                                logger.info("Remove query cache because the property in query condition is updated. Cache query SQL: " + queryCmd.getSql()
                                        + ". Update SQL: " + updateCmd.getParameterCount());
                            }

                            return false;
                        }
                    }
                }

                SQLOperationCommand checkCmd = createCheckSqlCmd(updateCmd);
                SQLResult checkSQLResult = null;

                try {
                    checkSQLResult = executant.internalExecuteQuery(checkCmd, false, true, options);

                    if (checkSQLResult.next()) {
                        if (updateCmd.getOperationType() == OperationType.UPDATE) {
                            Collection<String> cachedPropNames = new ArrayList<>(getCachedPropNames());

                            Property prop = null;
                            for (String cachedPropName : cachedPropNames) {
                                prop = queryCmd.getEntityDef().getProperty(cachedPropName);

                                if (prop == null || updateCmd.getTargetPropNames().contains(prop.getName())) {
                                    if (logger.isInfoEnabled()) {
                                        logger.info("Remove cached property from query cache" + cachedPropName + " because it's updated. Cache query SQL: "
                                                + queryCmd.getSql() + ". update SQL: " + updateCmd.getParameterCount());
                                    }

                                    removeResult(cachedPropName);
                                }
                            }

                        } else {
                            if (logger.isInfoEnabled()) {
                                logger.info("Remove query cache because some cached record is deleted. Cache query SQL: " + queryCmd.getSql() + ". update SQL: "
                                        + updateCmd.getParameterCount());
                            }

                            return false;
                        }
                    }
                } catch (Exception e) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Remove query cache because of error: " + e.getMessage() + ". Cache query SQL: " + queryCmd.getSql() + ". update SQL: "
                                + updateCmd.getParameterCount());
                    }

                    return false;
                } finally {
                    if (checkSQLResult != null) {
                        checkSQLResult.close();
                    }
                }
            }
        }

        return true;
    }

    /**
     * Removes the result.
     *
     * @param propName
     */
    @Override
    public void removeResult(String propName) {
        assertNotClosed();

        rwLock.writeLock().lock();

        try {
            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid != null) {
                int propIndex = checkPropIndex(propName);

                dataGrid.clearX(propIndex);

                if (dataGridCache != null) {
                    dataGridCache.remove(dataGridCacheId);

                    dataGridCache.put(dataGridCacheId, dataGrid, activityPrint().getLiveTime(), activityPrint().getMaxIdleTime());
                }

                lastUpdatedTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info("Failed to remove cache result by property name: " + propName, e);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Gets the data grid.
     *
     * @return
     */
    @Override
    public DataGrid<Object> getDataGrid() {
        assertNotClosed();

        DataGrid<Object> dataGrid = null;

        if (softDataGrid != null) {
            dataGrid = softDataGrid.get();
        }

        if (dataGrid == null) {
            // TODO, is possible. it's too slow to transfer huge array to/from
            // remote.
            if (dataGridCache != null) {
                try {
                    dataGrid = dataGridCache.get(dataGridCacheId).orElse(null);
                } catch (Exception e) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Failed to get DataGrid by id: " + dataGridCacheId, e);
                    }
                }

                if (dataGrid != null) {
                    softDataGrid = new SoftReference<DataGrid<Object>>(dataGrid);
                }
            }
        }

        return dataGrid;
    }

    /**
     * Zip.
     */
    @Override
    public void zip() {
        assertNotClosed();
        rwLock.writeLock().lock();

        try {
            if (softDataGrid != null) {
                DataGrid<Object> dataGrid = softDataGrid.get();

                if (dataGrid != null) {
                    dataGrid.zip();
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        rwLock.writeLock().lock();

        try {
            if (isClosed) {
                return;
            }

            DataGrid<Object> dataGrid = getDataGrid();

            if (dataGrid != null) {
                dataGrid.clear();
            }

            if (softDataGrid != null) {
                softDataGrid.clear();
            }

            if (dataGridCache != null) {

                try {
                    dataGridCache.remove(dataGridCacheId);
                } catch (Exception e) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Failed to remove DataGrid by id: " + dataGridCacheId, e);
                    }
                }
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
     * Creates the check sql cmd.
     *
     * @param updateCmd
     * @return
     */
    private SQLOperationCommand createCheckSqlCmd(SQLOperationCommand updateCmd) {
        String checkSql = null;
        int queryWhereBeginIndex = queryCmd.getWhereBeginIndex();
        int queryWhereEndIndex = queryCmd.getWhereEndIndex();
        String queryBeforeWhere = queryCmd.getSql().substring(0, queryWhereBeginIndex);

        String queryWhere = queryCmd.getSql().substring((queryCmd.getWhereBeginIndex() + 7), queryCmd.getWhereEndIndex());

        String udpateWhere = updateCmd.getSql().substring((updateCmd.getWhereBeginIndex() + 7), updateCmd.getWhereEndIndex());

        if (updateCmd.isBatch()) {
            int batchSize = updateCmd.getBatchParameters().size();
            final StringBuilder sb = Objectory.createStringBuilder(batchSize * 32);

            for (int i = 0; i < batchSize; i++) {
                if (i > 0) {
                    sb.append(WD._SPACE);
                    sb.append(WD.OR);
                    sb.append(WD._SPACE);
                }

                sb.append(WD._PARENTHESES_L);
                sb.append(udpateWhere);
                sb.append(WD._PARENTHESES_R);
            }

            udpateWhere = sb.toString();

            Objectory.recycle(sb);
        }

        checkSql = queryBeforeWhere + WHERE_PARENTHESES_L + udpateWhere + PARENTHESES_R_AND_PARENTHESES_L + queryWhere + WD._PARENTHESES_R
                + queryCmd.getSql().substring(queryWhereEndIndex);

        SQLOperationCommand checkSqlCmd = SQLCommandFactory.createSqlCommand(OperationType.QUERY, queryCmd.getEntityDef(), checkSql, queryCmd.getOptions());

        int parameterFromIndex = (updateCmd.getOperationType() == OperationType.UPDATE) ? updateCmd.getTargetPropNames().size() : 0;

        if (updateCmd.isBatch()) {
            List<Object[]> parametersList = updateCmd.getBatchParameters();

            for (Object[] parameters : parametersList) {
                for (int parameterIndex = parameterFromIndex; parameterIndex < parameters.length; parameterIndex++) {
                    checkSqlCmd.setParameter(checkSqlCmd.getParameterCount(), parameters[parameterIndex], updateCmd.getParameterType(parameterIndex));
                }
            }
        } else {
            checkSqlCmd.appendParameters(updateCmd, parameterFromIndex, updateCmd.getParameterCount());
        }

        checkSqlCmd.appendParameters(queryCmd);

        return checkSqlCmd;
    }

    /**
     * Gets the prop position in cache.
     *
     * @param cachingPropNames
     * @return
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    private int[] getPropPositionInCache(Collection<String> cachingPropNames) throws UncheckedSQLException {
        int[] propPositionInCache = new int[cachingPropNames.size()];
        int arrayIndex = 0;

        for (String propName : cachingPropNames) {
            if (propNameIndexMap.get(propName) != null) {
                propPositionInCache[arrayIndex] = propNameIndexMap.get(propName);
            } else {
                propPositionInCache[arrayIndex] = propNameIndexMap.size();
                propNameIndexMap.put(propName, propPositionInCache[arrayIndex]);
            }

            arrayIndex++;
        }

        return propPositionInCache;
    }

    /**
     *
     * @param queryResult
     * @param cachingPropNames
     * @return
     * @throws UncheckedSQLException the unchecked SQL exception
     */
    private DataGrid<Object> init(SQLResult queryResult, Collection<String> cachingPropNames) throws UncheckedSQLException {
        DataGrid<Object> dataGrid = getDataGrid();

        if (dataGrid == null) {
            dataGrid = new DataGrid<Object>(cachingPropNames.size(), queryResult.size());
        } else if (propNameIndexMap.size() > dataGrid.getX()) {
            dataGrid.extendX(propNameIndexMap.size());
        }

        if ((softDataGrid == null) || (softDataGrid.get() == null)) {
            softDataGrid = new SoftReference<DataGrid<Object>>(dataGrid);
        }

        return dataGrid;
    }
}

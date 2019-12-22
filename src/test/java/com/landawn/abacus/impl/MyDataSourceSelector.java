/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.impl;

import java.util.List;
import java.util.Map;

import com.landawn.abacus.DataSource;
import com.landawn.abacus.DataSourceManager;
import com.landawn.abacus.DataSourceSelector;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options.Query;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class MyDataSourceSelector implements DataSourceSelector {
    public static final String NON_EXIST_DATA_SOURCE = "no data source found";

    public MyDataSourceSelector(String arg0) {
    }

    @Override
    public DataSource select(DataSourceManager dataSourceManager, String entityName, String sql, Object[] parameters, Map<String, Object> options) {
        return getDataSourceByName(dataSourceManager, sql, options);
    }

    @Override
    public DataSource select(DataSourceManager dataSourceManager, String entityName, String sql, List<?> parameters, Map<String, Object> options) {
        return getDataSourceByName(dataSourceManager, sql, options);
    }

    protected DataSource getDataSourceByName(DataSourceManager dataSourceManager, String sql, Map<String, Object> options) {
        N.println("#################################" + sql);

        String targetDataSourceName = (options != null) ? (String) options.get(Query.QUERY_WITH_DATA_SOURCE) : null;
        DataSource targetDS = null;

        if (targetDataSourceName != null) {
            Map<String, DataSource> activeDataSources = dataSourceManager.getActiveDataSources();

            for (String dataSourceName : activeDataSources.keySet()) {
                if (targetDataSourceName.equals(dataSourceName)) {
                    targetDS = activeDataSources.get(dataSourceName);

                    break;
                }
            }

            if (targetDS == null) {
                throw new RuntimeException("no data source found by '" + NON_EXIST_DATA_SOURCE + "'.");
            }

            return targetDS;
        } else {
            return dataSourceManager.getPrimaryDataSource();
        }
    }
}

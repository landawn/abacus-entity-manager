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

import java.util.Collection;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.cache.QueryCache;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.pool.AbstractPoolable;

// TODO: Auto-generated Javadoc
/**
 * The Class HandleResult.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class HandleResult extends AbstractPoolable {

    /** The entity def. */
    private final EntityDefinition entityDef;

    /** The select prop name. */
    private Collection<String> selectPropName;

    /** The query result. */
    private final SQLResult queryResult;

    /** The query cache. */
    private QueryCache queryCache;

    /**
     * Instantiates a new handle result.
     *
     * @param entityDef the entity def
     * @param selectPropName the select prop name
     * @param queryResult the query result
     * @param queryCache the query cache
     * @param liveTime the live time
     * @param maxIdleTime the max idle time
     */
    HandleResult(EntityDefinition entityDef, Collection<String> selectPropName, SQLResult queryResult, QueryCache queryCache, long liveTime, long maxIdleTime) {
        super(liveTime, maxIdleTime);
        this.entityDef = entityDef;
        this.selectPropName = selectPropName;
        this.queryResult = queryResult;
        this.queryCache = queryCache;
    }

    /**
     * Gets the entity def.
     *
     * @return the entity def
     */
    public EntityDefinition getEntityDef() {
        return entityDef;
    }

    /**
     * Gets the SQL result.
     *
     * @return the SQL result
     */
    public SQLResult getSQLResult() {
        return queryResult;
    }

    /**
     * Gets the select prop names.
     *
     * @return the select prop names
     */
    public Collection<String> getSelectPropNames() {
        return selectPropName;
    }

    /**
     * Sets the select prop names.
     *
     * @param selectPropName the new select prop names
     */
    public void setSelectPropNames(Collection<String> selectPropName) {
        this.selectPropName = selectPropName;
    }

    /**
     * Gets the query cache.
     *
     * @return the query cache
     */
    public QueryCache getQueryCache() {
        return queryCache;
    }

    /**
     * Sets the query cache.
     *
     * @param queryCache the new query cache
     */
    public void setQueryCache(QueryCache queryCache) {
        this.queryCache = queryCache;
    }

    /**
     * Refresh query cache.
     */
    public void refreshQueryCache() {
        if (queryCache != null) {
            queryCache.close();

            queryCache = null;
        }

    }

    /**
     * Close.
     */
    public void close() {
        if (queryCache != null) {
            queryCache.close();
        }

        if (queryResult != null) {
            queryResult.close();
        }
    }

    /**
     * Destroy.
     */
    @Override
    public void destroy() {
        close();
    }
}

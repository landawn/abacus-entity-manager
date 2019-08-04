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
import com.landawn.abacus.core.sql.SQLResult;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.pool.AbstractPoolable;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
final class HandleResult extends AbstractPoolable {
    private final EntityDefinition entityDef;
    private Collection<String> selectPropName;
    private final SQLResult queryResult;
    private QueryCache queryCache;

    HandleResult(EntityDefinition entityDef, Collection<String> selectPropName, SQLResult queryResult, QueryCache queryCache, long liveTime, long maxIdleTime) {
        super(liveTime, maxIdleTime);
        this.entityDef = entityDef;
        this.selectPropName = selectPropName;
        this.queryResult = queryResult;
        this.queryCache = queryCache;
    }

    public EntityDefinition getEntityDef() {
        return entityDef;
    }

    public SQLResult getSQLResult() {
        return queryResult;
    }

    public Collection<String> getSelectPropNames() {
        return selectPropName;
    }

    public void setSelectPropNames(Collection<String> selectPropName) {
        this.selectPropName = selectPropName;
    }

    public QueryCache getQueryCache() {
        return queryCache;
    }

    public void setQueryCache(QueryCache queryCache) {
        this.queryCache = queryCache;
    }

    public void refreshQueryCache() {
        if (queryCache != null) {
            queryCache.close();

            queryCache = null;
        }

    }

    public void close() {
        if (queryCache != null) {
            queryCache.close();
        }

        if (queryResult != null) {
            queryResult.close();
        }
    }

    @Override
    public void destroy() {
        close();
    }
}

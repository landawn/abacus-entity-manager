/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.Collection;
import java.util.Map;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.core.sql.SQLQueryCache;
import com.landawn.abacus.core.sql.SQLResult;
import com.landawn.abacus.core.sql.command.Command;
import com.landawn.abacus.entity.extendDirty.basic.Account;
import com.landawn.abacus.entity.extendDirty.basic.ExtendDirtyBasicPNL;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options.Cache;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLQueryCacheTest extends AbstractAbacusTest {
    public void testA() {
        addAccount(Account.class, 3000);

        EntityDefinition entityDef = dbAccess.getEntityDefinitionFactory().getDefinition(Account.__);
        Collection<String> selectPropNames = entityDef.getDefaultLoadPropertyNameList();
        Condition condition = null;
        SQLResult queryResult = this.getSQLResultBySearch(entityDef, selectPropNames, condition, null);
        SQLQueryCache queryCache = new SQLQueryCache(100000, 100000, null);
        Cache.Condition cacheCond = Cache.condition(0, 0, 100000);
        Cache.Range range = Cache.range(1, 10000);
        queryCache.cacheResult(queryResult, queryResult.getPropNameList(), cacheCond, range);
        N.println(N.deepToString(queryCache.getResult(100, 1000)));
        queryResult.close();
        queryCache.close();
    }

    private SQLResult getSQLResultBySearch(EntityDefinition entityDef, Collection<String> selectPropNames, Condition condition, Map<String, Object> options) {
        DBAccessImpl dbAccessImpl = (DBAccessImpl) dbAccess;
        Command command = dbAccessImpl.getExecutant().getInterpreter().interpretQuery(entityDef, selectPropNames, condition, options);

        return dbAccessImpl.getExecutant().executeQuery(command, options);
    }

    @Override
    protected String getDomainName() {
        return ExtendDirtyBasicPNL._DN;
    }
}

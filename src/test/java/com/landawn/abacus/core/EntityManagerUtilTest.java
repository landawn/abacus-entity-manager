/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.entity.extendDirty.basic.AccountContact;
import com.landawn.abacus.entity.extendDirty.basic.DataType;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.util.DateUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Cache;
import com.landawn.abacus.util.Options.Jdbc;
import com.landawn.abacus.util.Options.Query;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public class EntityManagerUtilTest extends AbstractEntityManager1Test {
    final EntityDefinitionFactory entityDefinitionFactory = em.getEntityDefinitionFactory();
    final EntityDefinition entityDef = em.getEntityDefinitionFactory().getDefinition(Account.__);

    @Test
    public void test_checkEntityId() {
        try {
            EntityManagerUtil.checkEntityId(entityDefinitionFactory, N.asList(Seid.of(Account.__)));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.checkEntityId(entityDefinitionFactory, N.asList(Seid.of("non")));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.checkEntityId(entityDefinitionFactory, N.asList(Seid.of(Account.FIRST_NAME, "aa")));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.checkEntityId(entityDefinitionFactory, N.asList(Seid.of(Account.ID, 1), Seid.of(AccountContact.ID, 2)));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_checkEntity() {
        Object entity = null;

        try {
            EntityManagerUtil.checkEntity(entityDefinitionFactory, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.checkEntity(entityDefinitionFactory, "abc");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        Object[] entities = null;

        try {
            EntityManagerUtil.checkEntity(entityDefinitionFactory, entities);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        entities = N.asArray(new Account(), new AccountContact());

        try {
            EntityManagerUtil.checkEntity(entityDefinitionFactory, entities);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        List<?> entityList = null;

        try {
            EntityManagerUtil.checkEntity(entityDefinitionFactory, entityList);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        entityList = N.asList(new Account(), new AccountContact());

        try {
            EntityManagerUtil.checkEntity(entityDefinitionFactory, entityList);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_checkProps() {
        List<Map<String, Object>> propsList = null;

        try {
            EntityManagerUtil.checkPropsList(propsList);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_checkOptions() {
        Map<String, Object> options = N.asProps(Query.QUERY_FROM_CACHE, true, Jdbc.MAX_FIELD_SIZE, 3);

        try {
            EntityManagerUtil.checkConflictOptions(options);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getEntityCacheLiveTime(Account.__, null, N.asProps(Cache.LIVE_TIME, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getEntityCacheMaxIdleTime(Account.__, null, N.asProps(Cache.MAX_IDLE_TIME, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getQueryCacheLiveTime(null, N.asProps(Cache.LIVE_TIME, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getQueryCacheMaxIdleTime(null, N.asProps(Cache.MAX_IDLE_TIME, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getMinCheckQueryCacheSize(null, N.asProps(Cache.MIN_CHECK_QUERY_CACHE_SIZE, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getMaxCheckQueryCacheTime(null, N.asProps(Cache.MAX_CHECK_QUERY_CACHE_TIME, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getOffset(N.asProps(Query.OFFSET, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getCount(N.asProps(Query.COUNT, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getRecordLockTimeout(N.asProps(Options.RECORD_LOCK_TIMEOUT, -1), null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.getBatchSize(N.asProps(Options.BATCH_SIZE, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        try {
            EntityManagerUtil.isCacheResult(N.asProps(Query.CACHE_RESULT, -1));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_getEntityNameFromPropName() {
        //        try {
        //            NameUtil.getParentName("id");
        //            fail("Should throw IllegalArgumentException");
        //        } catch (IllegalArgumentException e) {
        //        }

        assertEquals(N.EMPTY_STRING, NameUtil.getParentName("id"));
    }

    @Test
    public void test_setPropValue() {
        MapEntity entity = MapEntity.valueOf(Account.__);
        EntityManagerUtil.setPropValue(entity, entityDef.getProperty(Account.ID), 1);
        assertEquals(1, (int) entity.get(Account.ID));
    }

    @Test
    public void test_getUpdatedProps() {
        MapEntity entity = MapEntity.valueOf(Account.__, N.asProps(Account.ID, 2, Account.FIRST_NAME, "firstName"));
        Map<String, Object> props = EntityManagerUtil.getUpdatedProps(entityDef, entity);
        N.println(entity.dirtyPropNames());
        N.println(props);
        //        assertEquals("firstName", props.get(Account._PNM.get(Account.FIRST_NAME)));
        //        assertEquals(null, props.get(Account._PNM.get(Account.ID)));
    }

    @Test
    public void test_parseUpdateProps() {
        Map<String, Object> props = N.asProps("abc", 11);

        try {
            EntityManagerUtil.parseUpdateProps(entityDef, props, true);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        props = N.asProps(Account.ID, 1);

        try {
            EntityManagerUtil.parseUpdateProps(entityDef, props, true);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_parseWriteProps() {
        try {
            List<Map<String, Object>> propsList = N.asList(N.asProps("aa", DateUtil.currentTimestamp()));
            EntityManagerUtil.parseInsertPropsList(entityDef, propsList, true);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_getEntityIdByEntity() {
        try {
            EntityManagerUtil.getEntityIdByEntity(entityDefinitionFactory.getDefinition(DataType.__), new DataType());
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_entityId2Condition() {
        Condition entityId = EntityManagerUtil.entityId2Condition(N.asList(Seid.of(Account.ID, 1)));

        assertEquals(1, entityId.getParameters().get(0));

        try {
            EntityManagerUtil.entityId2Condition(Seid.of(Account.__));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }
}

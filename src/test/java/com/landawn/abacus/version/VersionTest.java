/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.version;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class VersionTest extends AbstractTest {

    @Test
    public void test_01() {
        N.println("TODO");
    }

    //    @Test
    //    public void test_01() {
    //        Seid entityId = Seid.valueOf(Account.ID, 1);
    //        Version<EntityId> version = VersionFactory.createVersion("Memcached(localhost:11211)");
    //        N.println(version.get(entityId));
    //        version.update(entityId, 100);
    //        N.println(version.get(entityId));
    //        version.remove(entityId);
    //        version.clear();
    //        version = VersionFactory.createVersion("Memcached(localhost:11211, aa)");
    //        N.println(version.get(entityId));
    //        version = VersionFactory.createVersion("Memcached(localhost:11211, aa, 10000000)");
    //        N.println(version.get(entityId));
    //        version = VersionFactory.createVersion("Memcached(localhost:11211, aa, 10000000, 100)");
    //        N.println(version.get(entityId));
    //
    //        try {
    //            version = VersionFactory.createVersion("Memcached(localhost:11211, aa, 10000000, 100, wrong)");
    //        } catch (RuntimeException e) {
    //        }
    //
    //        try {
    //            version = VersionFactory.createVersion("Redis(localhost:11211)");
    //        } catch (Exception e) {
    //        }
    //
    //        try {
    //            version = VersionFactory.createVersion("Redis(localhost:11211, aa)");
    //        } catch (Exception e) {
    //        }
    //
    //        try {
    //            version = VersionFactory.createVersion("Redis(localhost:11211, aa, 10000000)");
    //        } catch (Exception e) {
    //        }
    //
    //        try {
    //            version = VersionFactory.createVersion("Redis(localhost:11211, aa, 10000000, 100)");
    //        } catch (Exception e) {
    //        }
    //
    //        try {
    //            version = VersionFactory.createVersion("Redis(localhost:11211, aa, 10000000, 100, wrong)");
    //        } catch (RuntimeException e) {
    //        }
    //
    //        version = new LocalVersion<EntityId>(100);
    //        version.clear();
    //    }
}

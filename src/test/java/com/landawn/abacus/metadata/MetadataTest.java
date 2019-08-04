/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.metadata;

import java.util.Set;

import org.junit.Test;

import com.landawn.abacus.AbstractEntityManager1Test;
import com.landawn.abacus.entity.extendDirty.basic.AclUser;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class MetadataTest extends AbstractEntityManager1Test {
    @Test
    public void test_01() {
        N.println(ColumnType.get(ColumnType.ENTITY.getName()));

        N.println(OnDeleteAction.get("noAction"));
        N.println(OnDeleteAction.get("setNull"));
        N.println(OnDeleteAction.get("cascade"));

        try {
            N.println(OnDeleteAction.get("123"));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        N.println(OnUpdateAction.get("noAction"));
        N.println(OnUpdateAction.get("setNull"));
        N.println(OnUpdateAction.get("cascade"));

        try {
            N.println(OnUpdateAction.get("123"));
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test_Association() {
        EntityDefinition entityDef = entityDefFactory.getDefinition(AclUser.__);
        Property prop = entityDef.getProperty(AclUser.GUI);
        Association association = new Association(prop, "AclUser.gui=AclUserGroupRelationship.userGUI and AclUserGroupRelationship.groupGUI = AclGroup.gui");
        N.println(association.hashCode());
        N.println(association.toString());

        Association association2 = new Association(prop, "AclUser.gui=AclUserGroupRelationship.userGUI and AclUserGroupRelationship.groupGUI = AclGroup.gui");
        Set<Association> set = N.asSet(association);
        assertTrue(set.contains(association2));

        association = new Association(prop, "AclUserGroupRelationship.userGUI=AclUser.gui and AclUserGroupRelationship.groupGUI = AclGroup.gui");
        association = new Association(prop, "AclUserGroupRelationship.userGUI=AclUser.gui and AclGroup.gui=AclUserGroupRelationship.groupGUI");

        association = new Association(prop, "AclUserGroupRelationship.groupGUI = AclGroup.gui and AclUser.gui=AclUserGroupRelationship.userGUI ");
        association = new Association(prop, "AclUserGroupRelationship.groupGUI = AclGroup.gui and AclUserGroupRelationship.userGUI = AclUser.gui ");
    }
}

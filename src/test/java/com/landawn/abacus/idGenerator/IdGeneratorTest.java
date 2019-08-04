/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.idGenerator;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.entity.extendDirty.basic.DataType;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class IdGeneratorTest extends AbstractAbacusTest {
    public void testCRUD() {
        Account account = createAccount(Account.class);
        em.add(account);

        long id = account.getId();
        em.delete(account);

        for (int i = 1; i < 10; i++) {
            account = createAccount(Account.class);
            em.add(account);

            assertEquals(id + i, account.getId());
            em.delete(account);
        }
    }

    public void test_01() {
        EntityDefinition entityDef = entityDefFactory.getDefinition(DataType.__);
        Property prop = entityDef.getProperty(DataType.INT_TYPE);
        AutoIncrementIdGenerator idGenerator = (AutoIncrementIdGenerator) (IdGenerator<?>) IdGeneratorFactory.create("AutoIncrement", prop);

        prop = entityDef.getProperty(DataType.SHORT_TYPE);

        AutoIncrementIdGenerator idGenerator2 = (AutoIncrementIdGenerator) (IdGenerator<?>) IdGeneratorFactory.create("AutoIncrement", prop);

        N.println(idGenerator.hashCode());
        N.println(idGenerator.toString());
        N.println(idGenerator.equals(idGenerator2));

        prop = entityDef.getProperty(DataType.BYTE_TYPE);

        prop = entityDef.getProperty(DataType.LONG_TYPE);

        SequenceIdGenerator idGenerator4 = (SequenceIdGenerator) (IdGenerator<?>) IdGeneratorFactory.create("Sequence(select * from seq)", prop);
        N.println(idGenerator4.hashCode());
        N.println(idGenerator4.toString());
        N.println(idGenerator4.equals(idGenerator2));

        // N.println(idGenerator4.allocate());

        UUIDIdGenerator idGenerator5 = (UUIDIdGenerator) (IdGenerator<?>) IdGeneratorFactory.create("UUID", prop);
        N.println(idGenerator5.hashCode());
        N.println(idGenerator5.toString());
        N.println(idGenerator5.equals(idGenerator2));
        assertFalse(idGenerator5.allocate().equals(idGenerator5.allocate()));
        N.println(idGenerator5.allocate());

        idGenerator5 = (UUIDIdGenerator) (IdGenerator<?>) IdGeneratorFactory.create("uuid(G:)", prop);
        N.println(idGenerator5.hashCode());
        N.println(idGenerator5.toString());
        N.println(idGenerator5.equals(idGenerator2));
        assertFalse(idGenerator5.allocate().equals(idGenerator5.allocate()));
        assertTrue(idGenerator5.allocate().startsWith("G:"));

        LocalIdGenerator idGenerator6 = (LocalIdGenerator) (IdGenerator<?>) IdGeneratorFactory.create("Local", prop);
        N.println(idGenerator6.hashCode());
        N.println(idGenerator6.toString());
        N.println(idGenerator6.equals(idGenerator2));
    }
}

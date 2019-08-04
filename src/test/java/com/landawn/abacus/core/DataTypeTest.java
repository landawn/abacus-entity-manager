/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.AbstractAbacusTest;
import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.entity.extendDirty.lvc.DataType;
import com.landawn.abacus.types.WeekDay;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DataTypeTest extends AbstractAbacusTest {

    public void test_clob() throws IOException, SQLException {
        EntityManager<DataType> em = emf.getEntityManager(domainName);
        DataType dataType = new DataType();
        dataType.setByteType((byte) 1);
        dataType.setCharType((char) 50);
        dataType.setBooleanType(true);
        dataType.setShortType(Short.MAX_VALUE);
        dataType.setIntType(Integer.MIN_VALUE);
        dataType.setLongType(0);
        dataType.setFloatType(0.00000000f);
        dataType.setDoubleType(000000000000000000000000);
        dataType.setStringType("String");
        Clob clobType = new org.hsqldb.jdbc.JDBCNClob("aaaaaaaa");
        dataType.setClobType(clobType);

        Blob blob = new org.hsqldb.jdbc.JDBCBlobFile(new File("./src/test/java/json.json"));
        dataType.setBlobType(blob);

        em.add(dataType);
        List<DataType> result = em.list(DataType.__, null, CF.eq(DataType.STRING_TYPE, dataType.getStringType()));
        N.println(result);
        N.println(IOUtil.readString(result.get(0).getClobType().getAsciiStream()));
        N.println(IOUtil.readString(result.get(0).getBlobType().getBinaryStream()));
        em.delete(DataType.__, null, null);
    }

    public void testCRUD() throws IOException {
        EntityManager<DataType> em = emf.getEntityManager(domainName);
        DataType dataType = new DataType();
        dataType.setByteType((byte) 1);
        dataType.setCharType((char) 50);
        dataType.setBooleanType(true);
        dataType.setShortType(Short.MAX_VALUE);
        dataType.setIntType(Integer.MIN_VALUE);
        dataType.setLongType(0);
        dataType.setFloatType(0.00000000f);
        dataType.setDoubleType(000000000000000000000000);
        dataType.setStringType("String");

        // dataType.setEnumType(WeekDay.FRI);
        ArrayList<String> stringArrayList = new ArrayList<String>();
        stringArrayList.add("aa");
        stringArrayList.add("黎");
        stringArrayList.add("cc");
        dataType.setStringArrayListType(stringArrayList);

        LinkedList<Boolean> booleanLinkedList = new LinkedList<Boolean>();
        booleanLinkedList.add(false);
        booleanLinkedList.add(false);
        booleanLinkedList.add(true);
        dataType.setBooleanLinkedListType(booleanLinkedList);

        Vector<String> stringVector = new Vector<String>();
        stringVector.add("false");
        dataType.setStringVectorType(stringVector);

        Map<BigDecimal, String> bigDecimalHashMap = new HashMap<BigDecimal, String>();

        bigDecimalHashMap.put(BigDecimal.valueOf(3993.000), "3993.000");
        bigDecimalHashMap.put(BigDecimal.valueOf(3993.001), "3993.001");

        HashMap<Timestamp, Float> timestampHashMap = new HashMap<Timestamp, Float>();

        timestampHashMap.put(new Timestamp(System.currentTimeMillis()), 3993.000f);
        timestampHashMap.put(new Timestamp(System.currentTimeMillis()), 3993.001f);
        dataType.setTimestampHashMapType(timestampHashMap);

        ConcurrentHashMap<BigDecimal, String> StringConcurrentHashMap = new ConcurrentHashMap<BigDecimal, String>();
        StringConcurrentHashMap.put(BigDecimal.valueOf(3993.000), "3993.000");
        StringConcurrentHashMap.put(BigDecimal.valueOf(3993.001), "3993.001");
        dataType.setStringConcurrentHashMapType(StringConcurrentHashMap);

        dataType.setByteArrayType(new byte[] { 1, 2, 3 });
        dataType.setDateType(new Date(System.currentTimeMillis()));
        dataType.setTimeType(new Time(System.currentTimeMillis()));
        dataType.setTimestampType(new Timestamp(System.currentTimeMillis()));

        EntityId entityId = em.add(dataType);
        assertTrue(entityId.isEmpty());

        Condition cond = CF.eq(DataType.STRING_CONCURRENT_HASH_MAP_TYPE, dataType.getStringConcurrentHashMapType());
        DataType dbDataType = get(null, cond);
        println(dbDataType);

        // assertEquals(dataType, dbDataType);
        DataSet dataSet = em.query(DataType.__, null, cond);
        dataSet.println();
        // assertEquals(dbDataType, dataSet.getRow(DataType.class, 0));

        em.delete(DataType.__, cond, null);
        dbDataType = get(null, cond);
        assertEquals(null, dbDataType);
    }

    public void testEnumType() {
        EntityManager<DataType> em = emf.getEntityManager(domainName);
        DataType dataType = new DataType();
        dataType.setCharType((char) 50);
        dataType.setEnumType(WeekDay.FRIDAY);

        EntityId entityId = em.add(dataType);
        assertTrue(entityId.isEmpty());

        Condition cond = CF.eq(DataType.ENUM_TYPE, WeekDay.FRIDAY);
        DataType dataTypeFromDB = get(null, cond);
        assertEquals(dataType.getEnumType(), dataTypeFromDB.getEnumType());
        em.delete(DataType.__, cond, null);
    }

    public void testLongTime() {
        EntityManager<DataType> em = emf.getEntityManager(domainName);
        long now = System.currentTimeMillis();
        DataType dataType = new DataType();
        dataType.setCharType((char) 50);
        dataType.setLongDateType(now);
        dataType.setLongTimeType(now);
        dataType.setLongTimestampType(now);

        EntityId entityId = em.add(dataType);
        assertTrue(entityId.isEmpty());

        Condition cond = CF.eq(DataType.LONG_DATE_TYPE, now);
        DataType dataTypeFromDB = get(N.asList(DataType.LONG_DATE_TYPE, DataType.LONG_TIME_TYPE, DataType.LONG_TIMESTAMP_TYPE), cond);
        assertEquals(dataType.getLongDateType(), dataTypeFromDB.getLongDateType());
        assertEquals(dataType.getLongTimeType(), dataTypeFromDB.getLongTimeType());
        assertEquals(dataType.getLongTimestampType(), dataTypeFromDB.getLongTimestampType());
        em.delete(DataType.__, cond, null);
    }

    DataType get(Collection<String> selectPropNames, Condition cond) {
        EntityManager<DataType> em = emf.getEntityManager(domainName);
        List<DataType> dataTyps = em.list(DataType.__, null, cond, null);

        return (dataTyps.size() == 0) ? null : dataTyps.get(0);
    }
}

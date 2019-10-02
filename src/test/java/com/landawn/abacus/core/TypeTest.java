/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Map;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.util.DateUtil;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class TypeTest extends AbstractTest {

    public void test_MapEntry() {
        AbstractMap.SimpleEntry<String, Integer> entry = new AbstractMap.SimpleEntry<>("abc", 123);
        Type<Map.Entry<String, Integer>> type = N.typeOf("Map.Entry<String, Integer>");

        String str = type.stringOf(entry);
        N.println(str);

        Map.Entry<String, Integer> entry2 = type.valueOf(str);
        N.println(entry2);
    }

    public void testUnit() {
        Type type = TypeFactory.getType(char.class.getCanonicalName());
        N.println(type.valueOf(String.valueOf((char) 0)));
        N.println("ok");
    }

    public void testTimestampType() {
        Type type = TypeFactory.getType(Timestamp.class.getCanonicalName());
        Timestamp t = DateUtil.currentTimestamp();
        String st = type.stringOf(t);
        N.println(st);
        t.setTime(t.getTime());
        assertEquals(t, type.valueOf(st));
    }

    public void testCalendarType() {
        Type type = TypeFactory.getType(Calendar.class.getCanonicalName());
        Calendar c = DateUtil.currentCalendar();
        String st = type.stringOf(c);
        N.println(st);
    }
}

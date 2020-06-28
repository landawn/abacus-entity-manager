package com.landawn.abacus.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.annotation.Beta;
import com.landawn.abacus.util.entity.AutoGeneratedClass;

public class Entity_2 {
    private ArrayList<Date[]> list5;
    private Date[] date2;
    private int intType;
    @Beta
    private String gui;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Timestamp createTime;
    private Map<String, List<Object>> attrs;
    private Entity_1 entit_1;
    private List<Entity_1> entit_1s;
    private Map<String, List<AutoGeneratedClass>> autoGeneratedClassMap;
    private List<Date> date;
    private ArrayList list1;
    @Beta
    private ArrayList<?> list2;
    @Beta
    private ArrayList<String> list3;
    private ArrayList<String[]> list4;
    private Set set2;
    private Set<?> set3;
    private Map map1;
    private Map<?, Object> map2;
    private Map<Object, ?> map3;
    private Map<?, ?> map4;
    private LinkedHashMap linkedHashMap1;
    private LinkedHashMap<?, Object> linkedHashMap2;
    private LinkedHashMap<Object, ?> linkedHashMap3;
    @Beta
    private LinkedHashMap<?, ?> linkedHashMap4;
    @Beta
    private ConcurrentHashMap map5;

    public static void reversedMethod() {
        // TODO
    }

    // =====>

    public Entity_2() {
    }

    public Entity_2(ArrayList<Date[]> list5, Date[] date2, int intType, String gui, String firstName, String lastName, Date birthDate, Timestamp createTime,
            Map<String, List<Object>> attrs, Entity_1 entit_1, List<Entity_1> entit_1s, Map<String, List<AutoGeneratedClass>> autoGeneratedClassMap,
            List<Date> date, ArrayList<Object> list1, ArrayList<?> list2, ArrayList<String> list3, ArrayList<java.lang.String[]> list4, Set<Object> set2,
            Set<?> set3, Map<Object, Object> map1, Map<?, Object> map2, Map<Object, ?> map3, Map<?, ?> map4, LinkedHashMap<Object, Object> linkedHashMap1,
            LinkedHashMap<?, Object> linkedHashMap2, LinkedHashMap<Object, ?> linkedHashMap3, LinkedHashMap<?, ?> linkedHashMap4,
            ConcurrentHashMap<Object, Object> map5) {
        this.list5 = list5;
        this.date2 = date2;
        this.intType = intType;
        this.gui = gui;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.createTime = createTime;
        this.attrs = attrs;
        this.entit_1 = entit_1;
        this.entit_1s = entit_1s;
        this.autoGeneratedClassMap = autoGeneratedClassMap;
        this.date = date;
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
        this.list4 = list4;
        this.set2 = set2;
        this.set3 = set3;
        this.map1 = map1;
        this.map2 = map2;
        this.map3 = map3;
        this.map4 = map4;
        this.linkedHashMap1 = linkedHashMap1;
        this.linkedHashMap2 = linkedHashMap2;
        this.linkedHashMap3 = linkedHashMap3;
        this.linkedHashMap4 = linkedHashMap4;
        this.map5 = map5;
    }

    public ArrayList<Date[]> getList5() {
        return list5;
    }

    public Entity_2 setList5(ArrayList<Date[]> list5) {
        this.list5 = list5;

        return this;
    }

    public Date[] getDate2() {
        return date2;
    }

    public Entity_2 setDate2(Date[] date2) {
        this.date2 = date2;

        return this;
    }

    public int getIntType() {
        return intType;
    }

    public Entity_2 setIntType(int intType) {
        this.intType = intType;

        return this;
    }

    public String getGui() {
        return gui;
    }

    public Entity_2 setGui(String gui) {
        this.gui = gui;

        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Entity_2 setFirstName(String firstName) {
        this.firstName = firstName;

        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Entity_2 setLastName(String lastName) {
        this.lastName = lastName;

        return this;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Entity_2 setBirthDate(Date birthDate) {
        this.birthDate = birthDate;

        return this;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Entity_2 setCreateTime(Timestamp createTime) {
        this.createTime = createTime;

        return this;
    }

    public Map<String, List<Object>> getAttrs() {
        return attrs;
    }

    public Entity_2 setAttrs(Map<String, List<Object>> attrs) {
        this.attrs = attrs;

        return this;
    }

    public Entity_1 getEntit_1() {
        return entit_1;
    }

    public Entity_2 setEntit_1(Entity_1 entit_1) {
        this.entit_1 = entit_1;

        return this;
    }

    public List<Entity_1> getEntit_1s() {
        return entit_1s;
    }

    public Entity_2 setEntit_1s(List<Entity_1> entit_1s) {
        this.entit_1s = entit_1s;

        return this;
    }

    public Map<String, List<AutoGeneratedClass>> getAutoGeneratedClassMap() {
        return autoGeneratedClassMap;
    }

    public Entity_2 setAutoGeneratedClassMap(Map<String, List<AutoGeneratedClass>> autoGeneratedClassMap) {
        this.autoGeneratedClassMap = autoGeneratedClassMap;

        return this;
    }

    public List<Date> getDate() {
        return date;
    }

    public Entity_2 setDate(List<Date> date) {
        this.date = date;

        return this;
    }

    public ArrayList<Object> getList1() {
        return list1;
    }

    public Entity_2 setList1(ArrayList<Object> list1) {
        this.list1 = list1;

        return this;
    }

    public ArrayList<?> getList2() {
        return list2;
    }

    public Entity_2 setList2(ArrayList<?> list2) {
        this.list2 = list2;

        return this;
    }

    public ArrayList<String> getList3() {
        return list3;
    }

    public Entity_2 setList3(ArrayList<String> list3) {
        this.list3 = list3;

        return this;
    }

    public ArrayList<java.lang.String[]> getList4() {
        return list4;
    }

    public Entity_2 setList4(ArrayList<java.lang.String[]> list4) {
        this.list4 = list4;

        return this;
    }

    public Set<Object> getSet2() {
        return set2;
    }

    public Entity_2 setSet2(Set<Object> set2) {
        this.set2 = set2;

        return this;
    }

    public Set<?> getSet3() {
        return set3;
    }

    public Entity_2 setSet3(Set<?> set3) {
        this.set3 = set3;

        return this;
    }

    public Map<Object, Object> getMap1() {
        return map1;
    }

    public Entity_2 setMap1(Map<Object, Object> map1) {
        this.map1 = map1;

        return this;
    }

    public Map<?, Object> getMap2() {
        return map2;
    }

    public Entity_2 setMap2(Map<?, Object> map2) {
        this.map2 = map2;

        return this;
    }

    public Map<Object, ?> getMap3() {
        return map3;
    }

    public Entity_2 setMap3(Map<Object, ?> map3) {
        this.map3 = map3;

        return this;
    }

    public Map<?, ?> getMap4() {
        return map4;
    }

    public Entity_2 setMap4(Map<?, ?> map4) {
        this.map4 = map4;

        return this;
    }

    public LinkedHashMap<Object, Object> getLinkedHashMap1() {
        return linkedHashMap1;
    }

    public Entity_2 setLinkedHashMap1(LinkedHashMap<Object, Object> linkedHashMap1) {
        this.linkedHashMap1 = linkedHashMap1;

        return this;
    }

    public LinkedHashMap<?, Object> getLinkedHashMap2() {
        return linkedHashMap2;
    }

    public Entity_2 setLinkedHashMap2(LinkedHashMap<?, Object> linkedHashMap2) {
        this.linkedHashMap2 = linkedHashMap2;

        return this;
    }

    public LinkedHashMap<Object, ?> getLinkedHashMap3() {
        return linkedHashMap3;
    }

    public Entity_2 setLinkedHashMap3(LinkedHashMap<Object, ?> linkedHashMap3) {
        this.linkedHashMap3 = linkedHashMap3;

        return this;
    }

    public LinkedHashMap<?, ?> getLinkedHashMap4() {
        return linkedHashMap4;
    }

    public Entity_2 setLinkedHashMap4(LinkedHashMap<?, ?> linkedHashMap4) {
        this.linkedHashMap4 = linkedHashMap4;

        return this;
    }

    public ConcurrentHashMap<Object, Object> getMap5() {
        return map5;
    }

    public Entity_2 setMap5(ConcurrentHashMap<Object, Object> map5) {
        this.map5 = map5;

        return this;
    }

    public Entity_2 copy() {
        final Entity_2 copy = new Entity_2();

        copy.list5 = this.list5;
        copy.date2 = this.date2;
        copy.intType = this.intType;
        copy.gui = this.gui;
        copy.firstName = this.firstName;
        copy.lastName = this.lastName;
        copy.birthDate = this.birthDate;
        copy.createTime = this.createTime;
        copy.attrs = this.attrs;
        copy.entit_1 = this.entit_1;
        copy.entit_1s = this.entit_1s;
        copy.autoGeneratedClassMap = this.autoGeneratedClassMap;
        copy.date = this.date;
        copy.list1 = this.list1;
        copy.list2 = this.list2;
        copy.list3 = this.list3;
        copy.list4 = this.list4;
        copy.set2 = this.set2;
        copy.set3 = this.set3;
        copy.map1 = this.map1;
        copy.map2 = this.map2;
        copy.map3 = this.map3;
        copy.map4 = this.map4;
        copy.linkedHashMap1 = this.linkedHashMap1;
        copy.linkedHashMap2 = this.linkedHashMap2;
        copy.linkedHashMap3 = this.linkedHashMap3;
        copy.linkedHashMap4 = this.linkedHashMap4;
        copy.map5 = this.map5;

        return copy;
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + N.hashCode(list5);
        h = 31 * h + N.hashCode(date2);
        h = 31 * h + N.hashCode(intType);
        h = 31 * h + N.hashCode(gui);
        h = 31 * h + N.hashCode(firstName);
        h = 31 * h + N.hashCode(lastName);
        h = 31 * h + N.hashCode(birthDate);
        h = 31 * h + N.hashCode(createTime);
        h = 31 * h + N.hashCode(attrs);
        h = 31 * h + N.hashCode(entit_1);
        h = 31 * h + N.hashCode(entit_1s);
        h = 31 * h + N.hashCode(autoGeneratedClassMap);
        h = 31 * h + N.hashCode(date);
        h = 31 * h + N.hashCode(list1);
        h = 31 * h + N.hashCode(list2);
        h = 31 * h + N.hashCode(list3);
        h = 31 * h + N.hashCode(list4);
        h = 31 * h + N.hashCode(set2);
        h = 31 * h + N.hashCode(set3);
        h = 31 * h + N.hashCode(map1);
        h = 31 * h + N.hashCode(map2);
        h = 31 * h + N.hashCode(map3);
        h = 31 * h + N.hashCode(map4);
        h = 31 * h + N.hashCode(linkedHashMap1);
        h = 31 * h + N.hashCode(linkedHashMap2);
        h = 31 * h + N.hashCode(linkedHashMap3);
        h = 31 * h + N.hashCode(linkedHashMap4);
        h = 31 * h + N.hashCode(map5);

        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Entity_2) {
            final Entity_2 other = (Entity_2) obj;

            return N.equals(list5, other.list5) && N.equals(date2, other.date2) && N.equals(intType, other.intType) && N.equals(gui, other.gui)
                    && N.equals(firstName, other.firstName) && N.equals(lastName, other.lastName) && N.equals(birthDate, other.birthDate)
                    && N.equals(createTime, other.createTime) && N.equals(attrs, other.attrs) && N.equals(entit_1, other.entit_1)
                    && N.equals(entit_1s, other.entit_1s) && N.equals(autoGeneratedClassMap, other.autoGeneratedClassMap) && N.equals(date, other.date)
                    && N.equals(list1, other.list1) && N.equals(list2, other.list2) && N.equals(list3, other.list3) && N.equals(list4, other.list4)
                    && N.equals(set2, other.set2) && N.equals(set3, other.set3) && N.equals(map1, other.map1) && N.equals(map2, other.map2)
                    && N.equals(map3, other.map3) && N.equals(map4, other.map4) && N.equals(linkedHashMap1, other.linkedHashMap1)
                    && N.equals(linkedHashMap2, other.linkedHashMap2) && N.equals(linkedHashMap3, other.linkedHashMap3)
                    && N.equals(linkedHashMap4, other.linkedHashMap4) && N.equals(map5, other.map5);
        }

        return false;
    }

    @Override
    public String toString() {
        return "{list5=" + N.toString(list5) + ", date2=" + N.toString(date2) + ", intType=" + N.toString(intType) + ", gui=" + N.toString(gui) + ", firstName="
                + N.toString(firstName) + ", lastName=" + N.toString(lastName) + ", birthDate=" + N.toString(birthDate) + ", createTime="
                + N.toString(createTime) + ", attrs=" + N.toString(attrs) + ", entit_1=" + N.toString(entit_1) + ", entit_1s=" + N.toString(entit_1s)
                + ", autoGeneratedClassMap=" + N.toString(autoGeneratedClassMap) + ", date=" + N.toString(date) + ", list1=" + N.toString(list1) + ", list2="
                + N.toString(list2) + ", list3=" + N.toString(list3) + ", list4=" + N.toString(list4) + ", set2=" + N.toString(set2) + ", set3="
                + N.toString(set3) + ", map1=" + N.toString(map1) + ", map2=" + N.toString(map2) + ", map3=" + N.toString(map3) + ", map4=" + N.toString(map4)
                + ", linkedHashMap1=" + N.toString(linkedHashMap1) + ", linkedHashMap2=" + N.toString(linkedHashMap2) + ", linkedHashMap3="
                + N.toString(linkedHashMap3) + ", linkedHashMap4=" + N.toString(linkedHashMap4) + ", map5=" + N.toString(map5) + "}";
    }

    // <=====

    // kept
    public static void reversedMethod2() {
        // TODO
    }
}

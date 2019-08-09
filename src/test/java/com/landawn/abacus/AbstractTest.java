/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.core.MapEntity;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.entity.extendDirty.lvc.AccountContact;
import com.landawn.abacus.entity.extendDirty.lvc.AclGroup;
import com.landawn.abacus.entity.extendDirty.lvc.AclUser;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.types.WeekDay;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.DateUtil;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;

import junit.framework.TestCase;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractTest extends TestCase {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    protected static com.landawn.abacus.core.EntityManagerFactory emf = com.landawn.abacus.core.EntityManagerFactory
            .getInstance("./src/test/resources/config/abacus-entity-manager.xml");

    protected static final String FIRST_NAME = "firstName";
    public static final String MIDDLE_NAME = "MN";
    protected static final String LAST_NAME = "lastName";
    protected static final String UPDATED_FIRST_NAME = "updatedFirstName";
    protected static final String UPDATED_LAST_NAME = "updatedLastName";
    protected static final String ACL_USER_NAME = "aclUserName";
    protected static final String ACL_GROUP_NAME = "aclGroupName";
    protected static final String ACL_DESCRIPTION = "I don't know";
    protected static final String ADDRESS = "ca, US";
    protected static final String CITY = "sunnyvale";
    protected static final String STATE = "CA";
    protected static final String COUNTRY = "U.S.";

    static {
        N.println(IOUtil.JAVA_VERSION);
        TypeFactory.registerClass(WeekDay.class, "WeekDay");
    }

    public static void println(Object obj) {
        N.println(obj);
    }

    public static Map<String, Object> createAccountProps() {
        return createAccountProps(FIRST_NAME, LAST_NAME);
    }

    public static Map<String, Object> createAccountProps(String firstName, String lastName) {
        String uuid = N.uuid();

        return N.asProps(Account.GUI, uuid, Account.FIRST_NAME, firstName, Account.LAST_NAME, lastName, Account.MIDDLE_NAME, MIDDLE_NAME, Account.EMAIL_ADDRESS,
                getEmail(uuid), Account.BIRTH_DATE, DateUtil.currentTimestamp(), Account.STATUS, 0);
    }

    public static List<Map<String, Object>> createAccountPropsList(int size) {
        return createAccountPropsList(FIRST_NAME, LAST_NAME, size);
    }

    public static List<Map<String, Object>> createAccountPropsList(String firstName, String lastName, int size) {
        List<Map<String, Object>> propsList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String uuid = N.uuid();
            propsList.add(N.asProps(Account.GUI, uuid, Account.FIRST_NAME, firstName + i, Account.LAST_NAME, lastName + i, Account.MIDDLE_NAME, MIDDLE_NAME,
                    Account.EMAIL_ADDRESS, getEmail(uuid), Account.BIRTH_DATE, DateUtil.currentTimestamp(), Account.STATUS, 0));
        }

        return propsList;
    }

    public static <T> T createAccount(Class<T> cls) {
        return createAccount(cls, FIRST_NAME, LAST_NAME);
    }

    public static <T> List<T> createAccountList(Class<T> cls, int size) {
        List<T> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(createAccount(cls, FIRST_NAME, LAST_NAME));
        }

        return list;
    }

    public static <T> T createAccountContact(Class<T> cls) {
        Object entity = N.newEntity(cls, Account.__);

        if (entity instanceof MapEntity) {
            MapEntity mapEntity = (MapEntity) entity;
            mapEntity.set(AccountContact.ADDRESS, ADDRESS);
            mapEntity.set(AccountContact.CITY, CITY);
            mapEntity.set(AccountContact.STATE, STATE);
            mapEntity.set(AccountContact.COUNTRY, COUNTRY);
            mapEntity.set(AccountContact.CREATE_TIME, DateUtil.currentTimestamp());
        } else {
            ClassUtil.setPropValue(entity, AccountContact.ADDRESS, ADDRESS);
            ClassUtil.setPropValue(entity, AccountContact.CITY, CITY);
            ClassUtil.setPropValue(entity, AccountContact.STATE, STATE);
            ClassUtil.setPropValue(entity, AccountContact.COUNTRY, COUNTRY);
            ClassUtil.setPropValue(entity, AccountContact.CREATE_TIME, DateUtil.currentTimestamp());
        }

        return (T) entity;
    }

    public static <T> T createAccount(Class<T> cls, String firstName, String lastName) {
        Object entity = N.newEntity(cls, Account.__);
        String uuid = N.uuid();

        if (entity instanceof MapEntity) {
            MapEntity mapEntity = (MapEntity) entity;
            mapEntity.set(Account.GUI, N.uuid());
            mapEntity.set(Account.FIRST_NAME, firstName);
            mapEntity.set(Account.MIDDLE_NAME, MIDDLE_NAME);
            mapEntity.set(Account.LAST_NAME, lastName);
            mapEntity.set(Account.BIRTH_DATE, DateUtil.currentTimestamp());
            mapEntity.set(Account.EMAIL_ADDRESS, getEmail(uuid));
            mapEntity.set(Account.LAST_UPDATE_TIME, DateUtil.currentTimestamp());
            mapEntity.set(Account.CREATE_TIME, DateUtil.currentTimestamp());
        } else {
            ClassUtil.setPropValue(entity, Account.GUI, N.uuid());

            ClassUtil.setPropValue(entity, Account.FIRST_NAME, firstName);
            ClassUtil.setPropValue(entity, Account.MIDDLE_NAME, MIDDLE_NAME);
            ClassUtil.setPropValue(entity, Account.LAST_NAME, lastName);

            ClassUtil.setPropValue(entity, Account.BIRTH_DATE, DateUtil.currentTimestamp());
            ClassUtil.setPropValue(entity, Account.EMAIL_ADDRESS, getEmail(uuid));
            ClassUtil.setPropValue(entity, Account.LAST_UPDATE_TIME, DateUtil.currentTimestamp());
            ClassUtil.setPropValue(entity, Account.CREATE_TIME, DateUtil.currentTimestamp());
        }

        return (T) entity;
    }

    public static <T> T createAccountWithContact(Class<T> cls) {
        T account = createAccount(cls);
        Method propSetMethod = ClassUtil.getPropSetMethod(cls, Account.CONTACT);
        ClassUtil.setPropValue(account, propSetMethod, createAccountContact(propSetMethod.getParameterTypes()[0]));

        return account;
    }

    public static <T> List<T> createAccountWithContact(Class<T> cls, int size) {
        List<T> accounts = new ArrayList<>();

        Method propSetMethod = ClassUtil.getPropSetMethod(cls, Account.CONTACT);

        T account = null;

        for (int i = 0; i < size; i++) {
            account = AbstractEntityManager1Test.createAccount(cls, FIRST_NAME + i, LAST_NAME + i);
            ClassUtil.setPropValue(account, propSetMethod, createAccountContact(propSetMethod.getParameterTypes()[0]));

            accounts.add(account);
        }

        return accounts;
    }

    public static Map<String, Object> createAclUserProps() {
        return createAclUserProps(ACL_USER_NAME);
    }

    public static Map<String, Object> createAclUserProps(String name) {
        return N.asProps(AclUser.GUI, N.uuid(), AclUser.NAME, name, AclUser.DESCRIPTION, ACL_DESCRIPTION);
    }

    public static List<Map<String, Object>> createAclUserPropsList(int size) {
        return createAclUserPropsList(ACL_USER_NAME, size);
    }

    public static List<Map<String, Object>> createAclUserPropsList(String name, int size) {
        List<Map<String, Object>> propsList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            propsList.add(createAclUserProps(name));
        }

        return propsList;
    }

    public static <T> T createAclUser(Class<T> cls) {
        return createAclUser(cls, ACL_USER_NAME);
    }

    public static <T> T createAclUser(Class<T> cls, String name) {
        AclUser aclUser = new AclUser();
        aclUser.setGUI(N.uuid());
        aclUser.setName(name);
        aclUser.setDescription(ACL_DESCRIPTION);

        return N.copy(cls, aclUser);
    }

    public static <T> T createAclGroup(Class<T> cls) {
        return createAclGroup(cls, ACL_GROUP_NAME);
    }

    public static <T> T createAclGroup(Class<T> cls, String name) {
        AclGroup aclGroup = new AclGroup();
        aclGroup.setGUI(N.uuid());
        aclGroup.setName(name);
        aclGroup.setDescription(ACL_DESCRIPTION);

        return N.copy(cls, aclGroup);
    }

    public static <T> T createAclUserWithAclGroup(Class<T> cls) {
        T aclUser = createAclUser(cls);

        Method propSetMethod = ClassUtil.getPropSetMethod(cls, AclUser.GROUP_LIST);
        Class aclGroupClass = (Class) ((ParameterizedType) propSetMethod.getGenericParameterTypes()[0]).getActualTypeArguments()[0];

        ClassUtil.setPropValue(aclUser, propSetMethod, N.asList(createAclGroup(aclGroupClass)));

        return aclUser;
    }

    public static <T> List<T> createAclUserWithAclGroup(Class<T> cls, int size) {
        List<T> aclUsers = new ArrayList<>();

        Method propSetMethod = ClassUtil.getPropSetMethod(cls, AclUser.GROUP_LIST);
        Class aclGroupClass = (Class) ((ParameterizedType) propSetMethod.getGenericParameterTypes()[0]).getActualTypeArguments()[0];

        T aclUser = null;

        for (int i = 0; i < size; i++) {
            aclUser = createAclUser(cls, ACL_USER_NAME + i);
            ClassUtil.setPropValue(aclUser, propSetMethod, N.asList(createAclGroup(aclGroupClass, ACL_GROUP_NAME + i)));

            aclUsers.add(aclUser);
        }

        return aclUsers;
    }

    public static String getEmail(String uuid) {
        return uuid + "@earth.com";
    }
}

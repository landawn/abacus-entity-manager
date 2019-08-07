/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public interface CodesPNL {
    public static final String _DN = "codes".intern();

    public static interface AccountPNL {
        /**
         * Name of "Account" entity. 
         */
        public static final String __ = "Account".intern();

        /**
         * Name of "id" property. 
         * type: long. 
         * column: "id". 
         */
        public static final String ID = (__ + ".id").intern();

        /**
         * Name of "firstName" property. 
         * type: String. 
         * column: "first_name". 
         */
        public static final String FIRST_NAME = (__ + ".firstName").intern();

        /**
         * Name of "lastName" property. 
         * type: String. 
         * column: "last_name". 
         */
        public static final String LAST_NAME = (__ + ".lastName").intern();

        /**
         * Name of "emailAddress" property. 
         * type: String. 
         * column: "email_address". 
         */
        public static final String EMAIL_ADDRESS = (__ + ".emailAddress").intern();

        /**
         * Name of "createTime" property. 
         * type: Timestamp. 
         * column: "create_time". 
         */
        public static final String CREATE_TIME = (__ + ".createTime").intern();

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList(ID, FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, CREATE_TIME));

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList("account.id".intern(), "account.first_name".intern(), "account.last_name".intern(), "account.email_address".intern(), "account.create_time".intern()));
    }

    public static final String CREATE_TIME = "createTime".intern();

    public static final String EMAIL_ADDRESS = "emailAddress".intern();

    public static final String FIRST_NAME = "firstName".intern();

    public static final String ID = "id".intern();

    public static final String LAST_NAME = "lastName".intern();
}

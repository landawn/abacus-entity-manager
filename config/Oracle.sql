

-- create db
-- DROP DATABASE IF EXISTS abacustest;
-- CREATE DATABASE abacustest DEFAULT CHARACTER SET utf8;

-- begin
-- execute immediate 'drop table login';
-- exception when others then null;
-- end;

DROP SEQUENCE abacus_test_seq;
CREATE SEQUENCE abacus_test_seq start with 10000000 increment by 1 nomaxvalue;

-- account table.
DROP TABLE login;
DROP TABLE account_contact;
DROP TABLE account_device;
DROP TABLE account;
CREATE TABLE account(
    id NUMBER(20),
    gui varchar(64) NOT NULL,
    email_address varchar(64),
    first_name varchar(32) NOT NULL,
    middle_name varchar(32),
    last_name varchar(32) NOT NULL,
    birth_date DATE,
    status NUMBER(6) DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp DEFAULT SYSDATE,
    UNIQUE(gui),
    UNIQUE(email_address),
    PRIMARY KEY (id)
);
CREATE INDEX account_first_name_ind on account (first_name);
CREATE INDEX account_last_name_ind on account (last_name);
CREATE INDEX account_birth_date_ind on account (birth_date);


-- login table
DROP TABLE login;
CREATE TABLE login(
    id NUMBER(20),
    account_id NUMBER(20) NOT NULL,
    login_id varchar(64) NOT NULL,
    login_password varchar(128) NOT NULL,
    status NUMBER(6) DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp DEFAULT SYSDATE,
    UNIQUE(login_id),
    PRIMARY KEY (id),
    CONSTRAINT fk_account_id FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE CASCADE
);


-- contact
DROP TABLE account_contact;
CREATE TABLE account_contact(
    id NUMBER(20),
    account_id NUMBER(20) NOT NULL,
    mobile varchar(16) DEFAULT NULL,
    telephone varchar(16) DEFAULT NULL,
    email varchar(64) DEFAULT NULL,
    address varchar(128) DEFAULT NULL,
    address_2 varchar(128) DEFAULT NULL,
    city varchar(32) DEFAULT NULL,
    state varchar(32) DEFAULT NULL,
    country varchar(32) DEFAULT NULL,
    zip_code varchar(16) DEFAULT NULL,
    category varchar(32) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6) DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp DEFAULT SYSDATE,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE CASCADE
);


-- device table
DROP TABLE account_device;
CREATE TABLE account_device(
    id NUMBER(20),
    account_id NUMBER(20) NOT NULL,
    name varchar(64) NOT NULL,
    udid varchar(64) NOT NULL,
    platform varchar(64) DEFAULT NULL,
    model varchar(64) DEFAULT NULL,
    manufacturer varchar(64) DEFAULT NULL,
    produce_time timestamp,
    category varchar(32) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6) DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp DEFAULT SYSDATE,
    UNIQUE(udid),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE CASCADE
);


-- acl user
DROP TABLE acl_user_group_relationship;
DROP TABLE acl_user;
CREATE TABLE acl_user(
    id NUMBER(20),
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6)  DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp NOT NULL,
    UNIQUE(gui),
    UNIQUE(name),
    PRIMARY KEY (id)
);


-- acl group.
DROP TABLE acl_user_group_relationship;
DROP TABLE acl_group;
CREATE TABLE acl_group(
    id NUMBER(20),
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6)  DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp NOT NULL,
    UNIQUE(name),
    UNIQUE(gui),
    PRIMARY KEY (id)
);


-- acl_user_group_relationship
DROP TABLE acl_user_group_relationship;
CREATE TABLE acl_user_group_relationship(
    id NUMBER(20),
    user_gui varchar(64) NOT NULL,
    group_gui varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6)  DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_gui)
        REFERENCES acl_user (gui)
        ON DELETE CASCADE,
    FOREIGN KEY (group_gui)
        REFERENCES acl_group (gui)
        ON DELETE CASCADE
);


-- acl_target.
DROP TABLE acl_ug_target_relationship;
DROP TABLE acl_target;
CREATE TABLE acl_target(
    id NUMBER(20),
    gui varchar(64) NOT NULL,
    name varchar(128) NOT NULL,
    category varchar(64) NOT NULL,
    sub_category varchar(64) NOT NULL,
    type varchar(64) NOT NULL,
    sub_type varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6)  DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp NOT NULL,
    UNIQUE (gui),
    UNIQUE (name),
    PRIMARY KEY (id)
);
CREATE INDEX acl_target_category_ind on acl_target (category);
CREATE INDEX acl_target_sub_category_ind on acl_target (sub_category);
CREATE INDEX acl_target_type_ind on acl_target (type);
CREATE INDEX acl_target_sub_type_ind on acl_target (sub_type);


-- acl_ug_target_relationship
DROP TABLE acl_ug_target_relationship;
CREATE TABLE acl_ug_target_relationship(
    id NUMBER(20),
    ug_gui varchar(64) NOT NULL,
    target_gui varchar(64) NOT NULL,
    privilege NUMBER(20) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status NUMBER(6)  DEFAULT 0,
    last_update_time timestamp DEFAULT SYSDATE,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (target_gui)
        REFERENCES acl_target (gui)
        ON DELETE CASCADE
);
CREATE INDEX acl_ug_target_r_ug_gui_ind on acl_ug_target_relationship (ug_gui);


-- date type
DROP TABLE data_type;
CREATE TABLE data_type(
    byteType NUMBER(3) DEFAULT NULL,
    charType NUMBER(8) DEFAULT NULL,
    booleanType NUMBER(1) DEFAULT NULL,
    shortType NUMBER(8) DEFAULT NULL,
    intType NUMBER(10) DEFAULT NULL,
    longType NUMBER(20) DEFAULT NULL,
    floatType NUMBER(16,4) DEFAULT NULL,
    doubleType NUMBER(32,8) DEFAULT NULL,
    bigIntegerType NUMBER(64) DEFAULT NULL,
    bigDecimalType decimal(32,8) DEFAULT NULL,
    stringType varchar(1024) DEFAULT NULL,
    byteArrayType varchar(1024) DEFAULT NULL,
    characterStreamType char(255) DEFAULT NULL,
    binaryStreamType varchar(1024) DEFAULT NULL,
    clobType clob DEFAULT NULL,
    blobType blob DEFAULT NULL,
    dateType date DEFAULT NULL,
    timeType date DEFAULT NULL,
    timestampType timestamp DEFAULT NULL,
    longDatetype NUMBER(32) DEFAULT NULL,
    longTimeType NUMBER(32) DEFAULT NULL,
    longTimestampType NUMBER(32) DEFAULT NULL,
    enumType varchar(128)DEFAULT null,
    stringArrayListType varchar(1024) DEFAULT NULL,
    booleanLinkedListType varchar(1024) DEFAULT NULL,
    doubleArrayListType varchar(1024) DEFAULT NULL,
    dateArrayListType varchar(1024) DEFAULT NULL,
    timestampArrayListType varchar(1024) DEFAULT NULL,
    bigDecimalArrayListType varchar(1024) DEFAULT NULL,
    stringHashSetType varchar(1024) DEFAULT NULL,
    booleanLinkedHashSetType varchar(1024) DEFAULT NULL,
    dateHashSetType varchar(1024) DEFAULT NULL,
    timestampHashSetType varchar(1024) DEFAULT NULL,
    bigDecimalHashSetType varchar(1024) DEFAULT NULL,
    stringHashMapType varchar(1024) DEFAULT NULL,
    booleanLinkedHashMapType varchar(1024) DEFAULT NULL,
    floatHashMapType varchar(1024) DEFAULT NULL,
    dateHashMapType varchar(1024) DEFAULT NULL,
    timestampHashMapType varchar(1024) DEFAULT NULL,
    bigDecimalHashMapType varchar(1024) DEFAULT NULL,
    StringVectorType varchar(1024) DEFAULT NULL,
    StringConcurrentHashMapType varchar(1024) DEFAULT NULL,
    jsonType varchar(1024) DEFAULT NULL,
    xmlType varchar(1024) DEFAULT NULL
);



-- ================================================================================================

-- author
DROP TABLE author;
CREATE TABLE author(
    id NUMBER(10),
    firstName varchar(64) NOT NULL,
    lastName varchar(64) NOT NULL,
    birthDay timestamp DEFAULT TO_DATE('2007-07-07T00:00:00Z', 'YYYY-MM-DD"T"HH24:MI:SS"Z"'),
    PRIMARY KEY (id)
);


-- book
DROP TABLE book;
CREATE TABLE book(
    id NUMBER(10),
    name varchar(64) NOT NULL,
    language varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- authorbook
DROP TABLE authorbook;
CREATE TABLE authorbook(
    AuthorId NUMBER(10) NOT NULL,
    bookId NUMBER(10) NOT NULL,
    UNIQUE (bookId,AuthorId)
);


-- contact
DROP TABLE contact;
CREATE TABLE contact(
    id NUMBER(10),
    hostId NUMBER(10) NOT NULL,
    mobile varchar(32) DEFAULT NULL,
    telephone varchar(32) DEFAULT NULL,
    email varchar(64) DEFAULT NULL,
    address varchar(128) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- email
DROP TABLE email;
CREATE TABLE email(
    id NUMBER(20) NOT NULL,
    contactId NUMBER(20) NOT NULL,
    emailAddress varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- prepare test data
INSERT INTO book (id, name ) VALUES ( 1, 'book1');
INSERT INTO book (id, name ) VALUES ( 2, 'book2');
INSERT INTO book (id, name ) VALUES ( 3, 'book3');
INSERT INTO book (id, name ) VALUES ( 4, 'book4');
INSERT INTO book (id, name ) VALUES ( 5, 'book5');
INSERT INTO book (id, name ) VALUES ( 6, 'book6');
INSERT INTO book (id, name ) VALUES ( 7, 'book7');

INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 1, 'author1', 'num1', to_date('2001-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 2, 'author2', 'num2', to_date('2002-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 3, 'author3', 'num3', to_date('2003-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 4, 'author4', 'num4', to_date('2004-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 5, 'author5', 'num5', to_date('2005-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'));

INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 1 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 2 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 3 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 2, 2 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 2, 3 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 3, 3 );

INSERT INTO contact (id, hostId, email) VALUES ( 1, 1, 'author1.num1@world.com' );
INSERT INTO contact (id, hostId, email) VALUES ( 2, 2 , 'author2.num2@world.com');
INSERT INTO contact (id, hostId, email) VALUES ( 3, 3 , 'author3.num4@world.com');
INSERT INTO contact (id, hostId, email) VALUES ( 4, 4 , 'author4.num4@world.com');
INSERT INTO contact (id, hostId, email) VALUES ( 5, 5 , 'author5.num5@world.com');

-- ======================================================================================

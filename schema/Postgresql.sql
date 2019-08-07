
-- create db
-- DROP DATABASE IF EXISTS abacustest;
-- CREATE DATABASE abacustest;


DROP TABLE IF EXISTS login;
DROP TABLE IF EXISTS account_contact;
DROP TABLE IF EXISTS account_device;
DROP TABLE IF EXISTS account;

DROP TABLE IF EXISTS acl_ug_target_relationship;
DROP TABLE IF EXISTS acl_target;
DROP TABLE IF EXISTS acl_user_group_relationship;
DROP TABLE IF EXISTS acl_group;
DROP TABLE IF EXISTS acl_user;

DROP TABLE IF EXISTS data_type;


-- account table.
DROP TABLE IF EXISTS login;
DROP TABLE IF EXISTS account_contact;
DROP TABLE IF EXISTS account_device;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_1;
DROP TABLE IF EXISTS account_2;
CREATE TABLE account(
    id bigserial,
    gui varchar(64) NOT NULL,
    email_address varchar(64),
    first_name varchar(32) NOT NULL,
    middle_name varchar(32),
    last_name varchar(32) NOT NULL,
    birth_date date,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX gui_ind on account (gui);
CREATE UNIQUE INDEX email_ind on account (email_address);
CREATE INDEX first_name_ind on account (first_name);
CREATE INDEX last_name_ind on account (last_name);
CREATE INDEX birth_date_ind on account (birth_date);

CREATE TABLE account_1 AS SELECT * from account;
CREATE TABLE account_2 AS SELECT * from account;

-- login table
DROP TABLE IF EXISTS login;
CREATE TABLE login(
    id bigserial,
    account_id bigint NOT NULL,
    login_id varchar(64) NOT NULL,
    login_password varchar(128) NOT NULL,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE UNIQUE INDEX login_id_ind on login (login_id);


-- contact
DROP TABLE IF EXISTS account_contact;
CREATE TABLE account_contact(
    id bigserial,
    account_id bigint NOT NULL,
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
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);


-- device table
DROP TABLE IF EXISTS account_device;
CREATE TABLE account_device(
    id bigserial,
    account_id bigint NOT NULL,
    name varchar(64) NOT NULL,
    udid varchar(64) NOT NULL,
    platform varchar(64) DEFAULT NULL,
    model varchar(64) DEFAULT NULL,
    manufacturer varchar(64) DEFAULT NULL,
    produce_time timestamp,
    category varchar(32) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE UNIQUE INDEX udid_ind on account_device (udid);


-- acl user
DROP TABLE IF EXISTS acl_user_group_relationship;
DROP TABLE IF EXISTS acl_user;
CREATE TABLE acl_user(
    id bigserial,
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX acl_user_gui_ind on acl_user (gui);
CREATE UNIQUE INDEX acl_user_name_ind on acl_user (name);


-- acl group.
DROP TABLE IF EXISTS acl_user_group_relationship;
DROP TABLE IF EXISTS acl_group;
CREATE TABLE acl_group(
    id bigserial,
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX acl_group_gui_ind on acl_group (gui);
CREATE UNIQUE INDEX acl_group_name_ind on acl_group (name);


-- acl_user_group_relationship
DROP TABLE IF EXISTS acl_user_group_relationship;
CREATE TABLE acl_user_group_relationship(
    id bigserial,
    user_gui varchar(64) NOT NULL,
    group_gui varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_gui)
        REFERENCES acl_user (gui)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (group_gui)
        REFERENCES acl_group (gui)
        ON UPDATE CASCADE ON DELETE CASCADE
);


-- acl_target.
DROP TABLE IF EXISTS acl_ug_target_relationship;
DROP TABLE IF EXISTS acl_target;
CREATE TABLE acl_target(
    id bigserial,
    gui varchar(64) NOT NULL,
    name varchar(128) NOT NULL,
    category varchar(64) NOT NULL,
    sub_category varchar(64) NOT NULL,
    type varchar(64) NOT NULL,
    sub_type varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE (gui),
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX acl_target_gui_ind on acl_target (gui);
CREATE UNIQUE INDEX acl_target_name_ind on acl_target (name);
CREATE INDEX acl_target_category_ind on acl_target (category);
CREATE INDEX acl_target_sub_category_ind on acl_target (sub_category);
CREATE INDEX acl_target_type_ind on acl_target (type);
CREATE INDEX acl_target_sub_type_ind on acl_target (sub_type);


-- acl_ug_target_relationship
DROP TABLE IF EXISTS acl_ug_target_relationship;
CREATE TABLE acl_ug_target_relationship(
    id bigserial,
    ug_gui varchar(64) NOT NULL,
    target_gui varchar(64) NOT NULL,
    privilege bigint NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (target_gui)
        REFERENCES acl_target (gui)
        ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX acl_ug_target_relationship_ug_gui_ind on acl_ug_target_relationship (ug_gui);

-- date type
DROP TABLE IF EXISTS data_type;
CREATE TABLE data_type(
    byteType smallint DEFAULT NULL,
    charType smallint DEFAULT NULL,
    booleanType boolean DEFAULT NULL,
    shortType smallint DEFAULT NULL,
    intType int DEFAULT NULL,
    longType bigint DEFAULT NULL,
    floatType float DEFAULT NULL,
    doubleType double precision DEFAULT NULL,
    bigIntegerType varchar(64) DEFAULT NULL,
    bigDecimalType decimal(10, 0 ) DEFAULT NULL,
    stringType varchar(1024) DEFAULT NULL,
    byteArrayType bytea DEFAULT NULL,
    characterStreamType char(255) DEFAULT NULL,
    binaryStreamType bit varying(1024) DEFAULT NULL,
    clobType text DEFAULT NULL,
    blobType bytea DEFAULT NULL,
    dateType date DEFAULT NULL,
    timeType time DEFAULT NULL,
    timestampType timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    longDateType bigint DEFAULT NULL,  
    longTimeType bigint DEFAULT NULL,  
    longTimestampType bigint DEFAULT NULL,
    enumType varchar(64) DEFAULT NULL,
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
DROP TABLE IF EXISTS author;
CREATE TABLE author(
    id SERIAL NOT NULL,
    firstName varchar(64) NOT NULL,
    lastName varchar(64) NOT NULL,
    birthDay timestamp NOT NULL DEFAULT '2007-07-07 00:00:00',
    PRIMARY KEY (id)
);


-- book
DROP TABLE IF EXISTS book;
CREATE TABLE book(
    id SERIAL,
    name varchar(64) NOT NULL,
    language varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- authorbook
DROP TABLE IF EXISTS authorbook;
CREATE TABLE authorbook(
    AuthorId int NOT NULL,
    bookId int NOT NULL,
    UNIQUE (bookId,AuthorId)
);


-- contact
DROP TABLE IF EXISTS contact;
CREATE TABLE contact(
    id SERIAL,
    hostId int NOT NULL,
    mobile varchar(32) DEFAULT NULL,
    telephone varchar(32) DEFAULT NULL,
    email varchar(64) DEFAULT NULL,
    address varchar(128) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- email
DROP TABLE IF EXISTS email;
CREATE TABLE email(
    id SERIAL NOT NULL,
    contactId int NOT NULL,
    emailAddress varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);

-- config
DROP TABLE IF EXISTS config;
CREATE TABLE config(
    id bigserial,
    name varchar(64) NOT NULL,
    content varchar(8192) NOT NULL,
    included_servers varchar(512) DEFAULT NULL,
    excluded_servers varchar(512) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status varchar(64) DEFAULT NULL,
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE(name),
    PRIMARY KEY (id)
);


-- prepare test data

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


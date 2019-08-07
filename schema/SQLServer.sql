

-- create db
-- DROP DATABASE IF EXISTS abacustest;
-- CREATE DATABASE abacustest DEFAULT CHARACTER SET utf8;

-- account table.
DROP TABLE login;
DROP TABLE account_contact;
DROP TABLE account_device;
DROP TABLE account;
CREATE TABLE account(
    id bigint IDENTITY,
    gui varchar(64) NOT NULL,
    email_address varchar(64),
    first_name varchar(32) NOT NULL,
    middle_name varchar(32),
    last_name varchar(32) NOT NULL,
    birth_date DATE,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
    UNIQUE(gui),
    PRIMARY KEY (id)
);
CREATE INDEX account_email_address_ind on account (email_address);
CREATE INDEX account_first_name_ind on account (first_name);
CREATE INDEX account_last_name_ind on account (last_name);
CREATE INDEX account_birth_date_ind on account (birth_date);


-- login table
DROP TABLE login;
CREATE TABLE login(
    id bigint IDENTITY,
    account_id bigint NOT NULL,
    login_id varchar(64) NOT NULL,
    login_password varchar(128) NOT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
    UNIQUE(login_id),
    PRIMARY KEY (id),
    CONSTRAINT fk_account_id FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE CASCADE
);


-- contact
DROP TABLE account_contact;
CREATE TABLE account_contact(
    id bigint IDENTITY,
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
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE CASCADE
);


-- device table
DROP TABLE account_device;
CREATE TABLE account_device(
    id bigint IDENTITY,
    account_id bigint NOT NULL,
    name varchar(64) NOT NULL,
    udid varchar(64) NOT NULL,
    platform varchar(64) DEFAULT NULL,
    model varchar(64) DEFAULT NULL,
    manufacturer varchar(64) DEFAULT NULL,
    produce_time datetime,
    category varchar(32) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
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
    id bigint IDENTITY,
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
    UNIQUE(gui),
    UNIQUE(name),
    PRIMARY KEY (id)
);


-- acl group.
DROP TABLE acl_user_group_relationship;
DROP TABLE acl_group;
CREATE TABLE acl_group(
    id bigint IDENTITY,
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
    UNIQUE(name),
    UNIQUE(gui),
    PRIMARY KEY (id)
);


-- acl_user_group_relationship
DROP TABLE acl_user_group_relationship;
CREATE TABLE acl_user_group_relationship(
    id bigint IDENTITY,
    user_gui varchar(64) NOT NULL,
    group_gui varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
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
    id bigint IDENTITY,
    gui varchar(64) NOT NULL,
    name varchar(128) NOT NULL,
    category varchar(64) NOT NULL,
    sub_category varchar(64) NOT NULL,
    type varchar(64) NOT NULL,
    sub_type varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
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
    id bigint IDENTITY,
    ug_gui varchar(64) NOT NULL,
    target_gui varchar(64) NOT NULL,
    privilege bigint NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status tinyint DEFAULT 0,
    last_update_time datetime DEFAULT GETDATE(),
    create_time datetime NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (target_gui)
        REFERENCES acl_target (gui)
        ON DELETE CASCADE
);
CREATE INDEX acl_ug_target_r_ug_gui_ind on acl_ug_target_relationship (ug_gui);


-- date type
DROP TABLE data_type;
CREATE TABLE data_type(
    byteType tinyint DEFAULT NULL,
    charType char DEFAULT NULL,
    booleanType bit DEFAULT NULL,
    shortType smallint DEFAULT NULL,
    intType int DEFAULT NULL,
    longType bigint DEFAULT NULL,
    floatType float DEFAULT NULL,
    doubleType float DEFAULT NULL,
    bigIntegerType varchar(64) DEFAULT NULL,
    bigDecimalType decimal DEFAULT NULL,
    stringType varchar(1024) DEFAULT NULL,
    byteArrayType varchar(1024) DEFAULT NULL,
    characterStreamType binary DEFAULT NULL,
    binaryStreamType varchar(1024) DEFAULT NULL,
    clobType text DEFAULT NULL,
    blobType image DEFAULT NULL,
    dateType date DEFAULT NULL,
    timeType time DEFAULT NULL,
    timestampType datetime DEFAULT NULL,
    longDatetype bigint DEFAULT NULL,
    longTimeType bigint DEFAULT NULL,
    longTimestampType bigint DEFAULT NULL,
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
    id int IDENTITY,
    firstName varchar(64) NOT NULL,
    lastName varchar(64) NOT NULL,
    birthDay datetime DEFAULT CONVERT(DATETIME, '2007-07-07', 101),
    PRIMARY KEY (id)
);


-- book
DROP TABLE book;
CREATE TABLE book(
    id int IDENTITY,
    name varchar(64) NOT NULL,
    language varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- authorbook
DROP TABLE authorbook;
CREATE TABLE authorbook(
    AuthorId int NOT NULL,
    bookId int NOT NULL,
    UNIQUE (bookId,AuthorId)
);


-- contact
DROP TABLE contact;
CREATE TABLE contact(
    id int IDENTITY,
    hostId int NOT NULL,
    mobile varchar(32) DEFAULT NULL,
    telephone varchar(32) DEFAULT NULL,
    email varchar(64) DEFAULT NULL,
    address varchar(128) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- email
DROP TABLE email;
CREATE TABLE email(
    id bigint NOT NULL,
    contactId bigint NOT NULL,
    emailAddress varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
);


-- prepare test data
SET IDENTITY_INSERT book ON;
INSERT INTO book (id, name ) VALUES ( 1, 'book1');
INSERT INTO book (id, name ) VALUES ( 2, 'book2');
INSERT INTO book (id, name ) VALUES ( 3, 'book3');
INSERT INTO book (id, name ) VALUES ( 4, 'book4');
INSERT INTO book (id, name ) VALUES ( 5, 'book5');
INSERT INTO book (id, name ) VALUES ( 6, 'book6');
INSERT INTO book (id, name ) VALUES ( 7, 'book7');
SET IDENTITY_INSERT book OFF;

SET IDENTITY_INSERT author ON;
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 1, 'author1', 'num1', CONVERT(DATETIME, '2001-01-01', 101));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 2, 'author2', 'num2', CONVERT(DATETIME, '2002-01-01', 101));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 3, 'author3', 'num3', CONVERT(DATETIME, '2003-01-01', 101));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 4, 'author4', 'num4', CONVERT(DATETIME, '2004-01-01', 101));
INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 5, 'author5', 'num5', CONVERT(DATETIME, '2005-01-01', 101));
SET IDENTITY_INSERT author OFF;

INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 1 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 2 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 3 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 2, 2 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 2, 3 );
INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 3, 3 );

SET IDENTITY_INSERT contact ON;
INSERT INTO contact (id, hostId, email) VALUES ( 1, 1, 'author1.num1@world.com' );
INSERT INTO contact (id, hostId, email) VALUES ( 2, 2 , 'author2.num2@world.com');
INSERT INTO contact (id, hostId, email) VALUES ( 3, 3 , 'author3.num4@world.com');
INSERT INTO contact (id, hostId, email) VALUES ( 4, 4 , 'author4.num4@world.com');
INSERT INTO contact (id, hostId, email) VALUES ( 5, 5 , 'author5.num5@world.com');
SET IDENTITY_INSERT contact OFF;

-- ======================================================================================

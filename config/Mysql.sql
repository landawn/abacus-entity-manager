

-- create db
-- DROP DATABASE IF EXISTS abacustest;
-- CREATE DATABASE abacustest DEFAULT CHARACTER SET utf8;


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
    id bigint(20) NOT NULL AUTO_INCREMENT,
    gui varchar(64) NOT NULL,
    email_address varchar(64),
    first_name varchar(32) NOT NULL,
    middle_name varchar(32),
    last_name varchar(32) NOT NULL,
    birth_date date,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    UNIQUE (gui),
    UNIQUE (email_address),
    INDEX first_name_ind (first_name),
    INDEX last_name_ind (last_name),
    INDEX birth_date_ind (birth_date),
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;

CREATE TABLE account_1 like account;
CREATE TABLE account_2 like account;

-- login table
DROP TABLE IF EXISTS login;
CREATE TABLE login(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    account_id bigint(20) NOT NULL,
    login_id varchar(64) NOT NULL,
    login_password varchar(128) NOT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE (login_id),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- contact
DROP TABLE IF EXISTS account_contact;
CREATE TABLE account_contact(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    account_id bigint(20) NOT NULL,
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
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- device table
DROP TABLE IF EXISTS account_device;
CREATE TABLE account_device(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    account_id bigint(20) NOT NULL,
    name varchar(64) NOT NULL,
    udid varchar(64) NOT NULL,
    platform varchar(64) DEFAULT NULL,
    model varchar(64) DEFAULT NULL,
    manufacturer varchar(64) DEFAULT NULL,
    produce_time date,
    category varchar(32) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE (udid),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- acl user
DROP TABLE IF EXISTS acl_user_group_relationship;
DROP TABLE IF EXISTS acl_user;
CREATE TABLE acl_user(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE (gui),
    UNIQUE (name),
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- acl group.
DROP TABLE IF EXISTS acl_user_group_relationship;
DROP TABLE IF EXISTS acl_group;
CREATE TABLE acl_group(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    gui varchar(64) NOT NULL,
    name varchar(32) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE (gui),
    UNIQUE (name),
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- acl_user_group_relationship
DROP TABLE IF EXISTS acl_user_group_relationship;
CREATE TABLE acl_user_group_relationship(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    user_gui varchar(64) NOT NULL,
    group_gui varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_gui)
        REFERENCES acl_user (gui)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (group_gui)
        REFERENCES acl_group (gui)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- acl_target.
DROP TABLE IF EXISTS acl_ug_target_relationship;
DROP TABLE IF EXISTS acl_target;
CREATE TABLE acl_target(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    gui varchar(64) NOT NULL,
    name varchar(128) NOT NULL,
    category varchar(64) NOT NULL,
    sub_category varchar(64) NOT NULL,
    type varchar(64) NOT NULL,
    sub_type varchar(64) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE (gui),
    UNIQUE (name),
    INDEX category_ind (category),
    INDEX sub_category_ind (sub_category),
    INDEX type_ind (type),
    INDEX sub_type_ind (sub_type),
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- acl_ug_target_relationship
DROP TABLE IF EXISTS acl_ug_target_relationship;
CREATE TABLE acl_ug_target_relationship(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    ug_gui varchar(64) NOT NULL,
    target_gui varchar(64) NOT NULL,
    privilege bigint(20) NOT NULL,
    description varchar(1024) DEFAULT NULL,
    status int NOT NULL DEFAULT 0,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    INDEX ug_gui_ind (ug_gui),
    PRIMARY KEY (id),
    FOREIGN KEY (target_gui)
        REFERENCES acl_target (gui)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- data type
DROP TABLE IF EXISTS data_type;
CREATE TABLE data_type(
    byteType tinyint unsigned DEFAULT NULL,
    charType smallint unsigned DEFAULT NULL,
    booleanType tinyint DEFAULT NULL,
    shortType smallint DEFAULT NULL,
    intType int DEFAULT NULL,
    longType bigint(20) DEFAULT NULL,
    floatType float DEFAULT NULL,
    doubleType double DEFAULT NULL,
    bigIntegerType varchar(64) DEFAULT NULL,
    bigDecimalType decimal(10, 0 ) DEFAULT NULL,
    stringType varchar(1024) DEFAULT NULL,
    byteArrayType varbinary(1024) DEFAULT NULL,
    characterStreamType char(255) DEFAULT NULL,
    binaryStreamType varbinary(1024) DEFAULT NULL,
    clobType text DEFAULT NULL,
    blobType blob DEFAULT NULL,
    dateType date DEFAULT NULL,
    timeType time DEFAULT NULL,
    timestampType timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    longDateType bigint DEFAULT NULL,  
    longTimeType bigint DEFAULT NULL,  
    longTimestampType bigint DEFAULT NULL,
    enumType varchar(64) DEFAULT NULL,
    stringArrayListType varchar(512) DEFAULT NULL,
    booleanLinkedListType varchar(512) DEFAULT NULL,
    doubleArrayListType varchar(512) DEFAULT NULL,
    dateArrayListType varchar(512) DEFAULT NULL,
    timestampArrayListType varchar(512) DEFAULT NULL,
    bigDecimalArrayListType varchar(512) DEFAULT NULL,
    stringHashSetType varchar(512) DEFAULT NULL,
    booleanLinkedHashSetType varchar(512) DEFAULT NULL,
    dateHashSetType varchar(512) DEFAULT NULL,
    timestampHashSetType varchar(512) DEFAULT NULL,
    bigDecimalHashSetType varchar(512) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;


-- ================================================================================================

-- author
DROP TABLE IF EXISTS author;
CREATE TABLE author(
    id int(10) unsigned NOT NULL AUTO_INCREMENT,
    firstName varchar(64) NOT NULL,
    lastName varchar(64) NOT NULL,
    birthDay timestamp NOT NULL DEFAULT '2007-07-07 00:00:00',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


-- book
DROP TABLE IF EXISTS book;
CREATE TABLE book(
    id int(10) unsigned NOT NULL AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    language varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;


-- authorbook
DROP TABLE IF EXISTS authorbook;
CREATE TABLE authorbook(
    AuthorId int(10) unsigned NOT NULL,
    bookId int(10) unsigned NOT NULL,
    UNIQUE (bookId,AuthorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- contact
DROP TABLE IF EXISTS contact;
CREATE TABLE contact(
    id int(10) unsigned NOT NULL AUTO_INCREMENT,
    hostId int(10) unsigned NOT NULL,
    mobile varchar(32) DEFAULT NULL,
    telephone varchar(32) DEFAULT NULL,
    email varchar(64) DEFAULT NULL,
    address varchar(128) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


-- email
DROP TABLE IF EXISTS email;
CREATE TABLE email(
    id int(10) unsigned NOT NULL AUTO_INCREMENT,
    contactId int(10) NOT NULL,
    emailAddress varchar(64) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- config
DROP TABLE IF EXISTS config;
CREATE TABLE config(
    id int(10) unsigned NOT NULL AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    content varchar(8192) NOT NULL,
    included_servers varchar(512) DEFAULT NULL,
    excluded_servers varchar(512) DEFAULT NULL,
    description varchar(1024) DEFAULT NULL,
    status varchar(64) DEFAULT NULL,
    last_update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_time timestamp NOT NULL,
    UNIQUE(name),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- prepare test data
INSERT INTO book (id, name ) VALUES ( 1, 'book1' ),  ( 2, 'book2' ),  ( 3, 'book3' ),  ( 4, 'book4' ),  ( 5, 'book5' ), (6, 'book6'), (7, 'book7');

INSERT INTO author (id, firstName, lastName, birthDay) VALUES ( 1, 'author1' , 'num1', '2001-01-01'), ( 2, 'author2', 'num2', '2002-01-01' ), ( 3, 'author3' , 'num3', '2003-01-01'), ( 4, 'author4', 'num4' , '2004-01-01'), ( 5, 'author5' , 'num5', '2005-01-01');

INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 1 ), ( 1, 2 ), ( 1, 3 ), ( 2, 2 ), ( 2, 3 ), ( 3, 3 );

INSERT INTO contact (id, hostId, email) VALUES ( 1, 1, 'author1.num1@world.com' ), ( 2, 2 , 'author2.num2@world.com'), ( 3, 3 , 'author3.num4@world.com'), ( 4, 4 , 'author4.num4@world.com'), ( 5, 5 , 'author5.num5@world.com');


DROP TABLE IF EXISTS multi_id;
CREATE TABLE multi_id(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    id2 bigint(20) NOT NULL AUTO_INCREMENT, 
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100000000 DEFAULT CHARSET=utf8;
-- ======================================================================================

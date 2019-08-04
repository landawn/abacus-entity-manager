/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.extendDirty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public interface ExtendDirtyCNL {
    public static final String _DN = "ExtendDirty".intern();

    public static interface AuthorCNL {
        /**
         * Name of "author" table. 
         */
        public static final String __ = "author".intern();

        /**
         * Name of "id" column. 
         * Java type: long. 
         */
        public static final String ID = (__ + ".id").intern();

        /**
         * Name of "firstName" column. 
         * Java type: String. 
         */
        public static final String FIRST_NAME = (__ + ".firstName").intern();

        /**
         * Name of "lastName" column. 
         * Java type: String. 
         */
        public static final String LAST_NAME = (__ + ".lastName").intern();

        /**
         * Name of "birthday" column. 
         * Java type: Timestamp. 
         */
        public static final String BIRTHDAY = (__ + ".birthday").intern();

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList(ID, FIRST_NAME, LAST_NAME, BIRTHDAY));

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList("Author.id".intern(), "Author.firstName".intern(), "Author.lastName".intern(), "Author.birthDay".intern()));
    }

    public static interface AuthorBookCNL {
        /**
         * Name of "authorBook" table. 
         */
        public static final String __ = "authorBook".intern();

        /**
         * Name of "authorId" column. 
         * Java type: long. 
         */
        public static final String AUTHOR_ID = (__ + ".authorId").intern();

        /**
         * Name of "bookId" column. 
         * Java type: long. 
         */
        public static final String BOOK_ID = (__ + ".bookId").intern();

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList(AUTHOR_ID, BOOK_ID));

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList("AuthorBook.authorId".intern(), "AuthorBook.bookId".intern()));
    }

    public static interface BookCNL {
        /**
         * Name of "book" table. 
         */
        public static final String __ = "book".intern();

        /**
         * Name of "id" column. 
         * Java type: long. 
         */
        public static final String ID = (__ + ".id").intern();

        /**
         * Name of "name" column. 
         * Java type: String. 
         */
        public static final String NAME = (__ + ".name").intern();

        /**
         * Name of "language" column. 
         * Java type: String. 
         */
        public static final String LANGUAGE = (__ + ".language").intern();

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList(ID, NAME, LANGUAGE));

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList("Book.id".intern(), "Book.name".intern(), "Book.language".intern()));
    }

    public static interface ContactCNL {
        /**
         * Name of "contact" table. 
         */
        public static final String __ = "contact".intern();

        /**
         * Name of "id" column. 
         * Java type: long. 
         */
        public static final String ID = (__ + ".id").intern();

        /**
         * Name of "hostId" column. 
         * Java type: long. 
         */
        public static final String HOST_ID = (__ + ".hostId").intern();

        /**
         * Name of "mobile" column. 
         * Java type: String. 
         */
        public static final String MOBILE = (__ + ".mobile").intern();

        /**
         * Name of "telephone" column. 
         * Java type: String. 
         */
        public static final String TELEPHONE = (__ + ".telephone").intern();

        /**
         * Name of "email" column. 
         * Java type: String. 
         */
        public static final String EMAIL = (__ + ".email").intern();

        /**
         * Name of "address" column. 
         * Java type: String. 
         */
        public static final String ADDRESS = (__ + ".address").intern();

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList(ID, HOST_ID, MOBILE, TELEPHONE, EMAIL, ADDRESS));

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList("Contact.id".intern(), "Contact.hostId".intern(), "Contact.mobile".intern(), "Contact.telephone".intern(), "Contact.email".intern(), "Contact.address".intern()));
    }

    public static interface DataTypeCNL {
        /**
         * Name of "data_type" table. 
         */
        public static final String __ = "data_type".intern();

        /**
         * Name of "bytetype" column. 
         * Java type: byte. 
         */
        public static final String BYTETYPE = (__ + ".bytetype").intern();

        /**
         * Name of "chartype" column. 
         * Java type: char. 
         */
        public static final String CHARTYPE = (__ + ".chartype").intern();

        /**
         * Name of "booleantype" column. 
         * Java type: boolean. 
         */
        public static final String BOOLEANTYPE = (__ + ".booleantype").intern();

        /**
         * Name of "shorttype" column. 
         * Java type: short. 
         */
        public static final String SHORTTYPE = (__ + ".shorttype").intern();

        /**
         * Name of "inttype" column. 
         * Java type: int. 
         */
        public static final String INTTYPE = (__ + ".inttype").intern();

        /**
         * Name of "longtype" column. 
         * Java type: long. 
         */
        public static final String LONGTYPE = (__ + ".longtype").intern();

        /**
         * Name of "floattype" column. 
         * Java type: float. 
         */
        public static final String FLOATTYPE = (__ + ".floattype").intern();

        /**
         * Name of "doubletype" column. 
         * Java type: double. 
         */
        public static final String DOUBLETYPE = (__ + ".doubletype").intern();

        /**
         * Name of "bigIntegerType" column. 
         * Java type: BigInteger. 
         */
        public static final String BIG_INTEGER_TYPE = (__ + ".bigIntegerType").intern();

        /**
         * Name of "bigdecimaltype" column. 
         * Java type: BigDecimal. 
         */
        public static final String BIGDECIMALTYPE = (__ + ".bigdecimaltype").intern();

        /**
         * Name of "stringtype" column. 
         * Java type: String. 
         */
        public static final String STRINGTYPE = (__ + ".stringtype").intern();

        /**
         * Name of "bytearraytype" column. 
         * Java type: byte[]. 
         */
        public static final String BYTEARRAYTYPE = (__ + ".bytearraytype").intern();

        /**
         * Name of "characterstreamtype" column. 
         * Java type: CharacterStream. 
         */
        public static final String CHARACTERSTREAMTYPE = (__ + ".characterstreamtype").intern();

        /**
         * Name of "binarystreamtype" column. 
         * Java type: BinaryStream. 
         */
        public static final String BINARYSTREAMTYPE = (__ + ".binarystreamtype").intern();

        /**
         * Name of "clobtype" column. 
         * Java type: Clob. 
         */
        public static final String CLOBTYPE = (__ + ".clobtype").intern();

        /**
         * Name of "blobtype" column. 
         * Java type: Blob. 
         */
        public static final String BLOBTYPE = (__ + ".blobtype").intern();

        /**
         * Name of "datetype" column. 
         * Java type: Date. 
         */
        public static final String DATETYPE = (__ + ".datetype").intern();

        /**
         * Name of "timetype" column. 
         * Java type: Time. 
         */
        public static final String TIMETYPE = (__ + ".timetype").intern();

        /**
         * Name of "timestamptype" column. 
         * Java type: Timestamp. 
         */
        public static final String TIMESTAMPTYPE = (__ + ".timestamptype").intern();

        /**
         * Name of "longDateType" column. 
         * Java type: long. 
         */
        public static final String LONG_DATE_TYPE = (__ + ".longDateType").intern();

        /**
         * Name of "longTimeType" column. 
         * Java type: long. 
         */
        public static final String LONG_TIME_TYPE = (__ + ".longTimeType").intern();

        /**
         * Name of "longTimestampType" column. 
         * Java type: long. 
         */
        public static final String LONG_TIMESTAMP_TYPE = (__ + ".longTimestampType").intern();

        /**
         * Name of "enumType" column. 
         * Java type: String. 
         */
        public static final String ENUM_TYPE = (__ + ".enumType").intern();

        /**
         * Name of "stringarraylisttype" column. 
         * Java type: ArrayList<String>. 
         */
        public static final String STRINGARRAYLISTTYPE = (__ + ".stringarraylisttype").intern();

        /**
         * Name of "booleanlinkedlisttype" column. 
         * Java type: LinkedList<Boolean>. 
         */
        public static final String BOOLEANLINKEDLISTTYPE = (__ + ".booleanlinkedlisttype").intern();

        /**
         * Name of "doublearraylisttype" column. 
         * Java type: List<Double>. 
         */
        public static final String DOUBLEARRAYLISTTYPE = (__ + ".doublearraylisttype").intern();

        /**
         * Name of "datearraylisttype" column. 
         * Java type: ArrayList<Date>. 
         */
        public static final String DATEARRAYLISTTYPE = (__ + ".datearraylisttype").intern();

        /**
         * Name of "timestamparraylisttype" column. 
         * Java type: ArrayList<Timestamp>. 
         */
        public static final String TIMESTAMPARRAYLISTTYPE = (__ + ".timestamparraylisttype").intern();

        /**
         * Name of "bigdecimalarraylisttype" column. 
         * Java type: ArrayList<BigDecimal>. 
         */
        public static final String BIGDECIMALARRAYLISTTYPE = (__ + ".bigdecimalarraylisttype").intern();

        /**
         * Name of "stringhashsettype" column. 
         * Java type: HashSet<String>. 
         */
        public static final String STRINGHASHSETTYPE = (__ + ".stringhashsettype").intern();

        /**
         * Name of "booleanlinkedhashsettype" column. 
         * Java type: LinkedHashSet<Boolean>. 
         */
        public static final String BOOLEANLINKEDHASHSETTYPE = (__ + ".booleanlinkedhashsettype").intern();

        /**
         * Name of "datehashsettype" column. 
         * Java type: HashSet<Date>. 
         */
        public static final String DATEHASHSETTYPE = (__ + ".datehashsettype").intern();

        /**
         * Name of "timestamphashsettype" column. 
         * Java type: HashSet<Timestamp>. 
         */
        public static final String TIMESTAMPHASHSETTYPE = (__ + ".timestamphashsettype").intern();

        /**
         * Name of "bigdecimalhashsettype" column. 
         * Java type: HashSet<BigDecimal>. 
         */
        public static final String BIGDECIMALHASHSETTYPE = (__ + ".bigdecimalhashsettype").intern();

        /**
         * Name of "stringhashmaptype" column. 
         * Java type: HashMap<String, String>. 
         */
        public static final String STRINGHASHMAPTYPE = (__ + ".stringhashmaptype").intern();

        /**
         * Name of "booleanlinkedhashmaptype" column. 
         * Java type: LinkedHashMap<String, Boolean>. 
         */
        public static final String BOOLEANLINKEDHASHMAPTYPE = (__ + ".booleanlinkedhashmaptype").intern();

        /**
         * Name of "floathashmaptype" column. 
         * Java type: HashMap<String, Float>. 
         */
        public static final String FLOATHASHMAPTYPE = (__ + ".floathashmaptype").intern();

        /**
         * Name of "datehashmaptype" column. 
         * Java type: HashMap<String, Date>. 
         */
        public static final String DATEHASHMAPTYPE = (__ + ".datehashmaptype").intern();

        /**
         * Name of "timestamphashmaptype" column. 
         * Java type: HashMap<Timestamp, Float>. 
         */
        public static final String TIMESTAMPHASHMAPTYPE = (__ + ".timestamphashmaptype").intern();

        /**
         * Name of "bigdecimalhashmaptype" column. 
         * Java type: HashMap<BigDecimal, String>. 
         */
        public static final String BIGDECIMALHASHMAPTYPE = (__ + ".bigdecimalhashmaptype").intern();

        /**
         * Name of "stringvectortype" column. 
         * Java type: Vector<String>. 
         */
        public static final String STRINGVECTORTYPE = (__ + ".stringvectortype").intern();

        /**
         * Name of "stringconcurrenthashmaptype" column. 
         * Java type: ConcurrentHashMap<BigDecimal, String>. 
         */
        public static final String STRINGCONCURRENTHASHMAPTYPE = (__ + ".stringconcurrenthashmaptype").intern();

        /**
         * Name of "jsonType" column. 
         * Java type: String. 
         */
        public static final String JSON_TYPE = (__ + ".jsonType").intern();

        /**
         * Name of "xmlType" column. 
         * Java type: String. 
         */
        public static final String XML_TYPE = (__ + ".xmlType").intern();

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList(BYTETYPE, CHARTYPE, BOOLEANTYPE, SHORTTYPE, INTTYPE, LONGTYPE, FLOATTYPE, DOUBLETYPE, BIG_INTEGER_TYPE, BIGDECIMALTYPE, STRINGTYPE, BYTEARRAYTYPE, CHARACTERSTREAMTYPE, BINARYSTREAMTYPE, CLOBTYPE, BLOBTYPE, DATETYPE, TIMETYPE, TIMESTAMPTYPE, LONG_DATE_TYPE, LONG_TIME_TYPE, LONG_TIMESTAMP_TYPE, ENUM_TYPE, STRINGARRAYLISTTYPE, BOOLEANLINKEDLISTTYPE, DOUBLEARRAYLISTTYPE, DATEARRAYLISTTYPE, TIMESTAMPARRAYLISTTYPE, BIGDECIMALARRAYLISTTYPE, STRINGHASHSETTYPE, BOOLEANLINKEDHASHSETTYPE, DATEHASHSETTYPE, TIMESTAMPHASHSETTYPE, BIGDECIMALHASHSETTYPE, STRINGHASHMAPTYPE, BOOLEANLINKEDHASHMAPTYPE, FLOATHASHMAPTYPE, DATEHASHMAPTYPE, TIMESTAMPHASHMAPTYPE, BIGDECIMALHASHMAPTYPE, STRINGVECTORTYPE, STRINGCONCURRENTHASHMAPTYPE, JSON_TYPE, XML_TYPE));

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList("DataType.byteType".intern(), "DataType.charType".intern(), "DataType.booleanType".intern(), "DataType.shortType".intern(), "DataType.intType".intern(), "DataType.longType".intern(), "DataType.floatType".intern(), "DataType.doubleType".intern(), "DataType.bigIntegerType".intern(), "DataType.bigDecimalType".intern(), "DataType.stringType".intern(), "DataType.byteArrayType".intern(), "DataType.characterStreamType".intern(), "DataType.binaryStreamType".intern(), "DataType.clobType".intern(), "DataType.blobType".intern(), "DataType.dateType".intern(), "DataType.timeType".intern(), "DataType.timestampType".intern(), "DataType.longDateType".intern(), "DataType.longTimeType".intern(), "DataType.longTimestampType".intern(), "DataType.enumType".intern(), "DataType.stringArrayListType".intern(), "DataType.booleanLinkedListType".intern(), "DataType.doubleListType".intern(), "DataType.dateArrayListType".intern(), "DataType.timestampArrayListType".intern(), "DataType.bigDecimalArrayListType".intern(), "DataType.stringHashSetType".intern(), "DataType.booleanLinkedHashSetType".intern(), "DataType.dateHashSetType".intern(), "DataType.timestampHashSetType".intern(), "DataType.bigDecimalHashSetType".intern(), "DataType.stringHashMapType".intern(), "DataType.booleanLinkedHashMapType".intern(), "DataType.floatHashMapType".intern(), "DataType.dateHashMapType".intern(), "DataType.timestampHashMapType".intern(), "DataType.bigDecimalHashMapType".intern(), "DataType.stringVectorType".intern(), "DataType.stringConcurrentHashMapType".intern(), "DataType.jsonType".intern(), "DataType.xmlType".intern()));
    }

    public static interface EmailCNL {
        /**
         * Name of "email" table. 
         */
        public static final String __ = "email".intern();

        /**
         * Name of "id" column. 
         * Java type: long. 
         */
        public static final String ID = (__ + ".id").intern();

        /**
         * Name of "contactId" column. 
         * Java type: long. 
         */
        public static final String CONTACT_ID = (__ + ".contactId").intern();

        /**
         * Name of "emailAddress" column. 
         * Java type: String. 
         */
        public static final String EMAIL_ADDRESS = (__ + ".emailAddress").intern();

        /**
         * Immutable column name list
         */
        public static final List<String> _CNL = Collections.unmodifiableList(Arrays.asList(ID, CONTACT_ID, EMAIL_ADDRESS));

        /**
         * Immutable property name list
         */
        public static final List<String> _PNL = Collections.unmodifiableList(Arrays.asList("Email.id".intern(), "Email.contactId".intern(), "Email.emailAddress".intern()));
    }

    public static final String ADDRESS = "address".intern();

    public static final String AUTHOR_ID = "authorId".intern();

    public static final String BIGDECIMALARRAYLISTTYPE = "bigdecimalarraylisttype".intern();

    public static final String BIGDECIMALHASHMAPTYPE = "bigdecimalhashmaptype".intern();

    public static final String BIGDECIMALHASHSETTYPE = "bigdecimalhashsettype".intern();

    public static final String BIGDECIMALTYPE = "bigdecimaltype".intern();

    public static final String BIG_INTEGER_TYPE = "bigIntegerType".intern();

    public static final String BINARYSTREAMTYPE = "binarystreamtype".intern();

    public static final String BIRTHDAY = "birthday".intern();

    public static final String BLOBTYPE = "blobtype".intern();

    public static final String BOOK_ID = "bookId".intern();

    public static final String BOOLEANLINKEDHASHMAPTYPE = "booleanlinkedhashmaptype".intern();

    public static final String BOOLEANLINKEDHASHSETTYPE = "booleanlinkedhashsettype".intern();

    public static final String BOOLEANLINKEDLISTTYPE = "booleanlinkedlisttype".intern();

    public static final String BOOLEANTYPE = "booleantype".intern();

    public static final String BYTEARRAYTYPE = "bytearraytype".intern();

    public static final String BYTETYPE = "bytetype".intern();

    public static final String CHARACTERSTREAMTYPE = "characterstreamtype".intern();

    public static final String CHARTYPE = "chartype".intern();

    public static final String CLOBTYPE = "clobtype".intern();

    public static final String CONTACT_ID = "contactId".intern();

    public static final String DATEARRAYLISTTYPE = "datearraylisttype".intern();

    public static final String DATEHASHMAPTYPE = "datehashmaptype".intern();

    public static final String DATEHASHSETTYPE = "datehashsettype".intern();

    public static final String DATETYPE = "datetype".intern();

    public static final String DOUBLEARRAYLISTTYPE = "doublearraylisttype".intern();

    public static final String DOUBLETYPE = "doubletype".intern();

    public static final String EMAIL = "email".intern();

    public static final String EMAIL_ADDRESS = "emailAddress".intern();

    public static final String ENUM_TYPE = "enumType".intern();

    public static final String FIRST_NAME = "firstName".intern();

    public static final String FLOATHASHMAPTYPE = "floathashmaptype".intern();

    public static final String FLOATTYPE = "floattype".intern();

    public static final String HOST_ID = "hostId".intern();

    public static final String ID = "id".intern();

    public static final String INTTYPE = "inttype".intern();

    public static final String JSON_TYPE = "jsonType".intern();

    public static final String LANGUAGE = "language".intern();

    public static final String LAST_NAME = "lastName".intern();

    public static final String LONG_DATE_TYPE = "longDateType".intern();

    public static final String LONG_TIMESTAMP_TYPE = "longTimestampType".intern();

    public static final String LONG_TIME_TYPE = "longTimeType".intern();

    public static final String LONGTYPE = "longtype".intern();

    public static final String MOBILE = "mobile".intern();

    public static final String NAME = "name".intern();

    public static final String SHORTTYPE = "shorttype".intern();

    public static final String STRINGARRAYLISTTYPE = "stringarraylisttype".intern();

    public static final String STRINGCONCURRENTHASHMAPTYPE = "stringconcurrenthashmaptype".intern();

    public static final String STRINGHASHMAPTYPE = "stringhashmaptype".intern();

    public static final String STRINGHASHSETTYPE = "stringhashsettype".intern();

    public static final String STRINGTYPE = "stringtype".intern();

    public static final String STRINGVECTORTYPE = "stringvectortype".intern();

    public static final String TELEPHONE = "telephone".intern();

    public static final String TIMESTAMPARRAYLISTTYPE = "timestamparraylisttype".intern();

    public static final String TIMESTAMPHASHMAPTYPE = "timestamphashmaptype".intern();

    public static final String TIMESTAMPHASHSETTYPE = "timestamphashsettype".intern();

    public static final String TIMESTAMPTYPE = "timestamptype".intern();

    public static final String TIMETYPE = "timetype".intern();

    public static final String XML_TYPE = "xmlType".intern();
}

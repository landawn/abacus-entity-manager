/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.pjo.lvc;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class DataType {

    @Column("bytetype")
    private byte byteType;

    @Column("chartype")
    private char charType;

    @Column("booleantype")
    private boolean booleanType;

    @Column("shorttype")
    private short shortType;

    @Column("inttype")
    private int intType;

    @Column("longtype")
    private long longType;

    @Column("floattype")
    private float floatType;

    @Column("doubletype")
    private double doubleType;

    @Column("bigIntegerType")
    private BigInteger bigIntegerType;

    @Column("bigdecimaltype")
    private BigDecimal bigDecimalType;

    @Column("stringtype")
    private String stringType;

    @Column("bytearraytype")
    private byte[] byteArrayType;

    @Column("characterstreamtype")
    private Reader characterStreamType;

    @Column("binarystreamtype")
    private InputStream binaryStreamType;

    @Column("clobtype")
    private Clob clobType;

    @Column("blobtype")
    private Blob blobType;

    @Column("datetype")
    private Date dateType;

    @Column("timetype")
    private Time timeType;

    @Column("timestamptype")
    private Timestamp timestampType;

    @Column("longDateType")
    private long longDateType;

    @Column("longTimeType")
    private long longTimeType;

    @Column("longTimestampType")
    private long longTimestampType;

    @Column("enumType")
    private com.landawn.abacus.types.WeekDay enumType;

    @Column("stringarraylisttype")
    private ArrayList<String> stringArrayListType;

    @Column("booleanlinkedlisttype")
    private LinkedList<Boolean> booleanLinkedListType;

    @Column("doublearraylisttype")
    private List<Double> doubleListType;

    @Column("datearraylisttype")
    private ArrayList<Date> dateArrayListType;

    @Column("timestamparraylisttype")
    private ArrayList<Timestamp> timestampArrayListType;

    @Column("bigdecimalarraylisttype")
    private ArrayList<BigDecimal> bigDecimalArrayListType;

    @Column("stringhashsettype")
    private HashSet<String> stringHashSetType;

    @Column("booleanlinkedhashsettype")
    private LinkedHashSet<Boolean> booleanLinkedHashSetType;

    @Column("datehashsettype")
    private HashSet<Date> dateHashSetType;

    @Column("timestamphashsettype")
    private HashSet<Timestamp> timestampHashSetType;

    @Column("bigdecimalhashsettype")
    private HashSet<BigDecimal> bigDecimalHashSetType;

    @Column("stringhashmaptype")
    private HashMap<String, String> stringHashMapType;

    @Column("booleanlinkedhashmaptype")
    private LinkedHashMap<String, Boolean> booleanLinkedHashMapType;

    @Column("floathashmaptype")
    private HashMap<String, Float> floatHashMapType;

    @Column("datehashmaptype")
    private HashMap<String, Date> dateHashMapType;

    @Column("timestamphashmaptype")
    private HashMap<Timestamp, Float> timestampHashMapType;

    @Column("bigdecimalhashmaptype")
    private HashMap<BigDecimal, String> bigDecimalHashMapType;

    @Column("stringvectortype")
    private Vector<String> stringVectorType;

    @Column("stringconcurrenthashmaptype")
    private ConcurrentHashMap<BigDecimal, String> stringConcurrentHashMapType;

    @Column("jsonType")
    private com.landawn.abacus.entity.extendDirty.lvc.Account jsonType;

    @Column("xmlType")
    private com.landawn.abacus.entity.extendDirty.lvc.Account xmlType;

    public DataType() {
    }

    public DataType(byte byteType, char charType, boolean booleanType, short shortType, int intType, long longType, float floatType, double doubleType,
            BigInteger bigIntegerType, BigDecimal bigDecimalType, String stringType, byte[] byteArrayType, Reader characterStreamType,
            InputStream binaryStreamType, Clob clobType, Blob blobType, Date dateType, Time timeType, Timestamp timestampType, long longDateType,
            long longTimeType, long longTimestampType, com.landawn.abacus.types.WeekDay enumType, ArrayList<String> stringArrayListType,
            LinkedList<Boolean> booleanLinkedListType, List<Double> doubleListType, ArrayList<Date> dateArrayListType,
            ArrayList<Timestamp> timestampArrayListType, ArrayList<BigDecimal> bigDecimalArrayListType, HashSet<String> stringHashSetType,
            LinkedHashSet<Boolean> booleanLinkedHashSetType, HashSet<Date> dateHashSetType, HashSet<Timestamp> timestampHashSetType,
            HashSet<BigDecimal> bigDecimalHashSetType, HashMap<String, String> stringHashMapType, LinkedHashMap<String, Boolean> booleanLinkedHashMapType,
            HashMap<String, Float> floatHashMapType, HashMap<String, Date> dateHashMapType, HashMap<Timestamp, Float> timestampHashMapType,
            HashMap<BigDecimal, String> bigDecimalHashMapType, Vector<String> stringVectorType,
            ConcurrentHashMap<BigDecimal, String> stringConcurrentHashMapType, com.landawn.abacus.entity.extendDirty.lvc.Account jsonType,
            com.landawn.abacus.entity.extendDirty.lvc.Account xmlType) {
        this();

        setByteType(byteType);
        setCharType(charType);
        setBooleanType(booleanType);
        setShortType(shortType);
        setIntType(intType);
        setLongType(longType);
        setFloatType(floatType);
        setDoubleType(doubleType);
        setBigIntegerType(bigIntegerType);
        setBigDecimalType(bigDecimalType);
        setStringType(stringType);
        setByteArrayType(byteArrayType);
        setCharacterStreamType(characterStreamType);
        setBinaryStreamType(binaryStreamType);
        setClobType(clobType);
        setBlobType(blobType);
        setDateType(dateType);
        setTimeType(timeType);
        setTimestampType(timestampType);
        setLongDateType(longDateType);
        setLongTimeType(longTimeType);
        setLongTimestampType(longTimestampType);
        setEnumType(enumType);
        setStringArrayListType(stringArrayListType);
        setBooleanLinkedListType(booleanLinkedListType);
        setDoubleListType(doubleListType);
        setDateArrayListType(dateArrayListType);
        setTimestampArrayListType(timestampArrayListType);
        setBigDecimalArrayListType(bigDecimalArrayListType);
        setStringHashSetType(stringHashSetType);
        setBooleanLinkedHashSetType(booleanLinkedHashSetType);
        setDateHashSetType(dateHashSetType);
        setTimestampHashSetType(timestampHashSetType);
        setBigDecimalHashSetType(bigDecimalHashSetType);
        setStringHashMapType(stringHashMapType);
        setBooleanLinkedHashMapType(booleanLinkedHashMapType);
        setFloatHashMapType(floatHashMapType);
        setDateHashMapType(dateHashMapType);
        setTimestampHashMapType(timestampHashMapType);
        setBigDecimalHashMapType(bigDecimalHashMapType);
        setStringVectorType(stringVectorType);
        setStringConcurrentHashMapType(stringConcurrentHashMapType);
        setJsonType(jsonType);
        setXmlType(xmlType);
    }

    @Type("byte")
    public byte getByteType() {
        return byteType;
    }

    public DataType setByteType(byte byteType) {
        this.byteType = byteType;

        return this;
    }

    @Type("char")
    public char getCharType() {
        return charType;
    }

    public DataType setCharType(char charType) {
        this.charType = charType;

        return this;
    }

    @Type("boolean")
    public boolean getBooleanType() {
        return booleanType;
    }

    public DataType setBooleanType(boolean booleanType) {
        this.booleanType = booleanType;

        return this;
    }

    @Type("short")
    public short getShortType() {
        return shortType;
    }

    public DataType setShortType(short shortType) {
        this.shortType = shortType;

        return this;
    }

    @Type("int")
    public int getIntType() {
        return intType;
    }

    public DataType setIntType(int intType) {
        this.intType = intType;

        return this;
    }

    @Type("long")
    public long getLongType() {
        return longType;
    }

    public DataType setLongType(long longType) {
        this.longType = longType;

        return this;
    }

    @Type("float")
    public float getFloatType() {
        return floatType;
    }

    public DataType setFloatType(float floatType) {
        this.floatType = floatType;

        return this;
    }

    @Type("double")
    public double getDoubleType() {
        return doubleType;
    }

    public DataType setDoubleType(double doubleType) {
        this.doubleType = doubleType;

        return this;
    }

    @Type("BigInteger")
    public BigInteger getBigIntegerType() {
        return bigIntegerType;
    }

    public DataType setBigIntegerType(BigInteger bigIntegerType) {
        this.bigIntegerType = bigIntegerType;

        return this;
    }

    @Type("BigDecimal")
    public BigDecimal getBigDecimalType() {
        return bigDecimalType;
    }

    public DataType setBigDecimalType(BigDecimal bigDecimalType) {
        this.bigDecimalType = bigDecimalType;

        return this;
    }

    @Type("String")
    public String getStringType() {
        return stringType;
    }

    public DataType setStringType(String stringType) {
        this.stringType = stringType;

        return this;
    }

    @Type("byte[]")
    public byte[] getByteArrayType() {
        return byteArrayType;
    }

    public DataType setByteArrayType(byte[] byteArrayType) {
        this.byteArrayType = byteArrayType;

        return this;
    }

    @Type("CharacterStream")
    public Reader getCharacterStreamType() {
        return characterStreamType;
    }

    public DataType setCharacterStreamType(Reader characterStreamType) {
        this.characterStreamType = characterStreamType;

        return this;
    }

    @Type("BinaryStream")
    public InputStream getBinaryStreamType() {
        return binaryStreamType;
    }

    public DataType setBinaryStreamType(InputStream binaryStreamType) {
        this.binaryStreamType = binaryStreamType;

        return this;
    }

    @Type("Clob")
    public Clob getClobType() {
        return clobType;
    }

    public DataType setClobType(Clob clobType) {
        this.clobType = clobType;

        return this;
    }

    @Type("Blob")
    public Blob getBlobType() {
        return blobType;
    }

    public DataType setBlobType(Blob blobType) {
        this.blobType = blobType;

        return this;
    }

    @Type("Date")
    public Date getDateType() {
        return dateType;
    }

    public DataType setDateType(Date dateType) {
        this.dateType = dateType;

        return this;
    }

    @Type("Time")
    public Time getTimeType() {
        return timeType;
    }

    public DataType setTimeType(Time timeType) {
        this.timeType = timeType;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getTimestampType() {
        return timestampType;
    }

    public DataType setTimestampType(Timestamp timestampType) {
        this.timestampType = timestampType;

        return this;
    }

    @Type("long")
    public long getLongDateType() {
        return longDateType;
    }

    public DataType setLongDateType(long longDateType) {
        this.longDateType = longDateType;

        return this;
    }

    @Type("long")
    public long getLongTimeType() {
        return longTimeType;
    }

    public DataType setLongTimeType(long longTimeType) {
        this.longTimeType = longTimeType;

        return this;
    }

    @Type("long")
    public long getLongTimestampType() {
        return longTimestampType;
    }

    public DataType setLongTimestampType(long longTimestampType) {
        this.longTimestampType = longTimestampType;

        return this;
    }

    @Type("com.landawn.abacus.types.WeekDay")
    public com.landawn.abacus.types.WeekDay getEnumType() {
        return enumType;
    }

    public DataType setEnumType(com.landawn.abacus.types.WeekDay enumType) {
        this.enumType = enumType;

        return this;
    }

    @Type("ArrayList<String>")
    public ArrayList<String> getStringArrayListType() {
        return stringArrayListType;
    }

    public DataType setStringArrayListType(ArrayList<String> stringArrayListType) {
        this.stringArrayListType = stringArrayListType;

        return this;
    }

    @Type("LinkedList<Boolean>")
    public LinkedList<Boolean> getBooleanLinkedListType() {
        return booleanLinkedListType;
    }

    public DataType setBooleanLinkedListType(LinkedList<Boolean> booleanLinkedListType) {
        this.booleanLinkedListType = booleanLinkedListType;

        return this;
    }

    @Type("List<Double>")
    public List<Double> getDoubleListType() {
        return doubleListType;
    }

    public DataType setDoubleListType(List<Double> doubleListType) {
        this.doubleListType = doubleListType;

        return this;
    }

    @Type("ArrayList<Date>")
    public ArrayList<Date> getDateArrayListType() {
        return dateArrayListType;
    }

    public DataType setDateArrayListType(ArrayList<Date> dateArrayListType) {
        this.dateArrayListType = dateArrayListType;

        return this;
    }

    @Type("ArrayList<Timestamp>")
    public ArrayList<Timestamp> getTimestampArrayListType() {
        return timestampArrayListType;
    }

    public DataType setTimestampArrayListType(ArrayList<Timestamp> timestampArrayListType) {
        this.timestampArrayListType = timestampArrayListType;

        return this;
    }

    @Type("ArrayList<BigDecimal>")
    public ArrayList<BigDecimal> getBigDecimalArrayListType() {
        return bigDecimalArrayListType;
    }

    public DataType setBigDecimalArrayListType(ArrayList<BigDecimal> bigDecimalArrayListType) {
        this.bigDecimalArrayListType = bigDecimalArrayListType;

        return this;
    }

    @Type("HashSet<String>")
    public HashSet<String> getStringHashSetType() {
        return stringHashSetType;
    }

    public DataType setStringHashSetType(HashSet<String> stringHashSetType) {
        this.stringHashSetType = stringHashSetType;

        return this;
    }

    @Type("LinkedHashSet<Boolean>")
    public LinkedHashSet<Boolean> getBooleanLinkedHashSetType() {
        return booleanLinkedHashSetType;
    }

    public DataType setBooleanLinkedHashSetType(LinkedHashSet<Boolean> booleanLinkedHashSetType) {
        this.booleanLinkedHashSetType = booleanLinkedHashSetType;

        return this;
    }

    @Type("HashSet<Date>")
    public HashSet<Date> getDateHashSetType() {
        return dateHashSetType;
    }

    public DataType setDateHashSetType(HashSet<Date> dateHashSetType) {
        this.dateHashSetType = dateHashSetType;

        return this;
    }

    @Type("HashSet<Timestamp>")
    public HashSet<Timestamp> getTimestampHashSetType() {
        return timestampHashSetType;
    }

    public DataType setTimestampHashSetType(HashSet<Timestamp> timestampHashSetType) {
        this.timestampHashSetType = timestampHashSetType;

        return this;
    }

    @Type("HashSet<BigDecimal>")
    public HashSet<BigDecimal> getBigDecimalHashSetType() {
        return bigDecimalHashSetType;
    }

    public DataType setBigDecimalHashSetType(HashSet<BigDecimal> bigDecimalHashSetType) {
        this.bigDecimalHashSetType = bigDecimalHashSetType;

        return this;
    }

    @Type("HashMap<String, String>")
    public HashMap<String, String> getStringHashMapType() {
        return stringHashMapType;
    }

    public DataType setStringHashMapType(HashMap<String, String> stringHashMapType) {
        this.stringHashMapType = stringHashMapType;

        return this;
    }

    @Type("LinkedHashMap<String, Boolean>")
    public LinkedHashMap<String, Boolean> getBooleanLinkedHashMapType() {
        return booleanLinkedHashMapType;
    }

    public DataType setBooleanLinkedHashMapType(LinkedHashMap<String, Boolean> booleanLinkedHashMapType) {
        this.booleanLinkedHashMapType = booleanLinkedHashMapType;

        return this;
    }

    @Type("HashMap<String, Float>")
    public HashMap<String, Float> getFloatHashMapType() {
        return floatHashMapType;
    }

    public DataType setFloatHashMapType(HashMap<String, Float> floatHashMapType) {
        this.floatHashMapType = floatHashMapType;

        return this;
    }

    @Type("HashMap<String, Date>")
    public HashMap<String, Date> getDateHashMapType() {
        return dateHashMapType;
    }

    public DataType setDateHashMapType(HashMap<String, Date> dateHashMapType) {
        this.dateHashMapType = dateHashMapType;

        return this;
    }

    @Type("HashMap<Timestamp, Float>")
    public HashMap<Timestamp, Float> getTimestampHashMapType() {
        return timestampHashMapType;
    }

    public DataType setTimestampHashMapType(HashMap<Timestamp, Float> timestampHashMapType) {
        this.timestampHashMapType = timestampHashMapType;

        return this;
    }

    @Type("HashMap<BigDecimal, String>")
    public HashMap<BigDecimal, String> getBigDecimalHashMapType() {
        return bigDecimalHashMapType;
    }

    public DataType setBigDecimalHashMapType(HashMap<BigDecimal, String> bigDecimalHashMapType) {
        this.bigDecimalHashMapType = bigDecimalHashMapType;

        return this;
    }

    @Type("Vector<String>")
    public Vector<String> getStringVectorType() {
        return stringVectorType;
    }

    public DataType setStringVectorType(Vector<String> stringVectorType) {
        this.stringVectorType = stringVectorType;

        return this;
    }

    @Type("ConcurrentHashMap<BigDecimal, String>")
    public ConcurrentHashMap<BigDecimal, String> getStringConcurrentHashMapType() {
        return stringConcurrentHashMapType;
    }

    public DataType setStringConcurrentHashMapType(ConcurrentHashMap<BigDecimal, String> stringConcurrentHashMapType) {
        this.stringConcurrentHashMapType = stringConcurrentHashMapType;

        return this;
    }

    @Type("JSON<com.landawn.abacus.entity.extendDirty.lvc.Account>")
    public com.landawn.abacus.entity.extendDirty.lvc.Account getJsonType() {
        return jsonType;
    }

    public DataType setJsonType(com.landawn.abacus.entity.extendDirty.lvc.Account jsonType) {
        this.jsonType = jsonType;

        return this;
    }

    @Type("XML<com.landawn.abacus.entity.extendDirty.lvc.Account>")
    public com.landawn.abacus.entity.extendDirty.lvc.Account getXmlType() {
        return xmlType;
    }

    public DataType setXmlType(com.landawn.abacus.entity.extendDirty.lvc.Account xmlType) {
        this.xmlType = xmlType;

        return this;
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(byteType);
        h = 31 * h + Objects.hashCode(charType);
        h = 31 * h + Objects.hashCode(booleanType);
        h = 31 * h + Objects.hashCode(shortType);
        h = 31 * h + Objects.hashCode(intType);
        h = 31 * h + Objects.hashCode(longType);
        h = 31 * h + Objects.hashCode(floatType);
        h = 31 * h + Objects.hashCode(doubleType);
        h = 31 * h + Objects.hashCode(bigIntegerType);
        h = 31 * h + Objects.hashCode(bigDecimalType);
        h = 31 * h + Objects.hashCode(stringType);
        h = 31 * h + Objects.hashCode(byteArrayType);
        h = 31 * h + Objects.hashCode(characterStreamType);
        h = 31 * h + Objects.hashCode(binaryStreamType);
        h = 31 * h + Objects.hashCode(clobType);
        h = 31 * h + Objects.hashCode(blobType);
        h = 31 * h + Objects.hashCode(dateType);
        h = 31 * h + Objects.hashCode(timeType);
        h = 31 * h + Objects.hashCode(timestampType);
        h = 31 * h + Objects.hashCode(longDateType);
        h = 31 * h + Objects.hashCode(longTimeType);
        h = 31 * h + Objects.hashCode(longTimestampType);
        h = 31 * h + Objects.hashCode(enumType);
        h = 31 * h + Objects.hashCode(stringArrayListType);
        h = 31 * h + Objects.hashCode(booleanLinkedListType);
        h = 31 * h + Objects.hashCode(doubleListType);
        h = 31 * h + Objects.hashCode(dateArrayListType);
        h = 31 * h + Objects.hashCode(timestampArrayListType);
        h = 31 * h + Objects.hashCode(bigDecimalArrayListType);
        h = 31 * h + Objects.hashCode(stringHashSetType);
        h = 31 * h + Objects.hashCode(booleanLinkedHashSetType);
        h = 31 * h + Objects.hashCode(dateHashSetType);
        h = 31 * h + Objects.hashCode(timestampHashSetType);
        h = 31 * h + Objects.hashCode(bigDecimalHashSetType);
        h = 31 * h + Objects.hashCode(stringHashMapType);
        h = 31 * h + Objects.hashCode(booleanLinkedHashMapType);
        h = 31 * h + Objects.hashCode(floatHashMapType);
        h = 31 * h + Objects.hashCode(dateHashMapType);
        h = 31 * h + Objects.hashCode(timestampHashMapType);
        h = 31 * h + Objects.hashCode(bigDecimalHashMapType);
        h = 31 * h + Objects.hashCode(stringVectorType);
        h = 31 * h + Objects.hashCode(stringConcurrentHashMapType);
        h = 31 * h + Objects.hashCode(jsonType);
        h = 31 * h + Objects.hashCode(xmlType);

        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof DataType) {
            final DataType other = (DataType) obj;

            return Objects.equals(byteType, other.byteType) && Objects.equals(charType, other.charType) && Objects.equals(booleanType, other.booleanType)
                    && Objects.equals(shortType, other.shortType) && Objects.equals(intType, other.intType) && Objects.equals(longType, other.longType)
                    && Objects.equals(floatType, other.floatType) && Objects.equals(doubleType, other.doubleType)
                    && Objects.equals(bigIntegerType, other.bigIntegerType) && Objects.equals(bigDecimalType, other.bigDecimalType)
                    && Objects.equals(stringType, other.stringType) && Objects.equals(byteArrayType, other.byteArrayType)
                    && Objects.equals(characterStreamType, other.characterStreamType) && Objects.equals(binaryStreamType, other.binaryStreamType)
                    && Objects.equals(clobType, other.clobType) && Objects.equals(blobType, other.blobType) && Objects.equals(dateType, other.dateType)
                    && Objects.equals(timeType, other.timeType) && Objects.equals(timestampType, other.timestampType)
                    && Objects.equals(longDateType, other.longDateType) && Objects.equals(longTimeType, other.longTimeType)
                    && Objects.equals(longTimestampType, other.longTimestampType) && Objects.equals(enumType, other.enumType)
                    && Objects.equals(stringArrayListType, other.stringArrayListType) && Objects.equals(booleanLinkedListType, other.booleanLinkedListType)
                    && Objects.equals(doubleListType, other.doubleListType) && Objects.equals(dateArrayListType, other.dateArrayListType)
                    && Objects.equals(timestampArrayListType, other.timestampArrayListType)
                    && Objects.equals(bigDecimalArrayListType, other.bigDecimalArrayListType) && Objects.equals(stringHashSetType, other.stringHashSetType)
                    && Objects.equals(booleanLinkedHashSetType, other.booleanLinkedHashSetType) && Objects.equals(dateHashSetType, other.dateHashSetType)
                    && Objects.equals(timestampHashSetType, other.timestampHashSetType) && Objects.equals(bigDecimalHashSetType, other.bigDecimalHashSetType)
                    && Objects.equals(stringHashMapType, other.stringHashMapType) && Objects.equals(booleanLinkedHashMapType, other.booleanLinkedHashMapType)
                    && Objects.equals(floatHashMapType, other.floatHashMapType) && Objects.equals(dateHashMapType, other.dateHashMapType)
                    && Objects.equals(timestampHashMapType, other.timestampHashMapType) && Objects.equals(bigDecimalHashMapType, other.bigDecimalHashMapType)
                    && Objects.equals(stringVectorType, other.stringVectorType)
                    && Objects.equals(stringConcurrentHashMapType, other.stringConcurrentHashMapType) && Objects.equals(jsonType, other.jsonType)
                    && Objects.equals(xmlType, other.xmlType);
        }

        return false;
    }

    @Override
    public String toString() {
        return "{byteType=" + Objects.toString(byteType) + ", charType=" + Objects.toString(charType) + ", booleanType=" + Objects.toString(booleanType)
                + ", shortType=" + Objects.toString(shortType) + ", intType=" + Objects.toString(intType) + ", longType=" + Objects.toString(longType)
                + ", floatType=" + Objects.toString(floatType) + ", doubleType=" + Objects.toString(doubleType) + ", bigIntegerType="
                + Objects.toString(bigIntegerType) + ", bigDecimalType=" + Objects.toString(bigDecimalType) + ", stringType=" + Objects.toString(stringType)
                + ", byteArrayType=" + Objects.toString(byteArrayType) + ", characterStreamType=" + Objects.toString(characterStreamType)
                + ", binaryStreamType=" + Objects.toString(binaryStreamType) + ", clobType=" + Objects.toString(clobType) + ", blobType="
                + Objects.toString(blobType) + ", dateType=" + Objects.toString(dateType) + ", timeType=" + Objects.toString(timeType) + ", timestampType="
                + Objects.toString(timestampType) + ", longDateType=" + Objects.toString(longDateType) + ", longTimeType=" + Objects.toString(longTimeType)
                + ", longTimestampType=" + Objects.toString(longTimestampType) + ", enumType=" + Objects.toString(enumType) + ", stringArrayListType="
                + Objects.toString(stringArrayListType) + ", booleanLinkedListType=" + Objects.toString(booleanLinkedListType) + ", doubleListType="
                + Objects.toString(doubleListType) + ", dateArrayListType=" + Objects.toString(dateArrayListType) + ", timestampArrayListType="
                + Objects.toString(timestampArrayListType) + ", bigDecimalArrayListType=" + Objects.toString(bigDecimalArrayListType) + ", stringHashSetType="
                + Objects.toString(stringHashSetType) + ", booleanLinkedHashSetType=" + Objects.toString(booleanLinkedHashSetType) + ", dateHashSetType="
                + Objects.toString(dateHashSetType) + ", timestampHashSetType=" + Objects.toString(timestampHashSetType) + ", bigDecimalHashSetType="
                + Objects.toString(bigDecimalHashSetType) + ", stringHashMapType=" + Objects.toString(stringHashMapType) + ", booleanLinkedHashMapType="
                + Objects.toString(booleanLinkedHashMapType) + ", floatHashMapType=" + Objects.toString(floatHashMapType) + ", dateHashMapType="
                + Objects.toString(dateHashMapType) + ", timestampHashMapType=" + Objects.toString(timestampHashMapType) + ", bigDecimalHashMapType="
                + Objects.toString(bigDecimalHashMapType) + ", stringVectorType=" + Objects.toString(stringVectorType) + ", stringConcurrentHashMapType="
                + Objects.toString(stringConcurrentHashMapType) + ", jsonType=" + Objects.toString(jsonType) + ", xmlType=" + Objects.toString(xmlType) + "}";
    }
}

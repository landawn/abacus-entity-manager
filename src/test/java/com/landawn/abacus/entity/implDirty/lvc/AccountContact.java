/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty.lvc;

import java.util.Collection;
import java.util.Set;
import java.sql.Timestamp;
import com.landawn.abacus.core.DirtyMarkerImpl;
import java.util.Objects;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.entity.implDirty.lvc.ImplDirtyLVCPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AccountContact implements ImplDirtyLVCPNL.AccountContactPNL, DirtyMarker {
    private final DirtyMarkerImpl dirtyMarkerImpl = new DirtyMarkerImpl(__);

    private long id;
    private long accountId;
    private String mobile;
    private String telephone;
    private String email;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String category;
    private String description;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;

    public AccountContact() {
    }

    public AccountContact(long id) {
        this();

        setId(id);
    }

    public AccountContact(long accountId, String mobile, String telephone, String email, 
        String address, String address2, String city, String state, 
        String country, String zipCode, String category, String description, 
        int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setAccountId(accountId);
        setMobile(mobile);
        setTelephone(telephone);
        setEmail(email);
        setAddress(address);
        setAddress2(address2);
        setCity(city);
        setState(state);
        setCountry(country);
        setZipCode(zipCode);
        setCategory(category);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AccountContact(long id, long accountId, String mobile, String telephone, String email, 
        String address, String address2, String city, String state, 
        String country, String zipCode, String category, String description, 
        int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setId(id);
        setAccountId(accountId);
        setMobile(mobile);
        setTelephone(telephone);
        setEmail(email);
        setAddress(address);
        setAddress2(address2);
        setCity(city);
        setState(state);
        setCountry(country);
        setZipCode(zipCode);
        setCategory(category);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public String entityName() {
        return __;
    }

    @XmlTransient
    public boolean isDirty() {
        return dirtyMarkerImpl.isDirty();
    }

    @XmlTransient
    public boolean isDirty(String propName) {
        return dirtyMarkerImpl.isDirty(propName);
    }

    public void markDirty(boolean isDirty) {
        dirtyMarkerImpl.markDirty(isDirty);
    }

    public void markDirty(String propName, boolean isDirty) {
        dirtyMarkerImpl.markDirty(propName, isDirty);
    }

    public void markDirty(Collection<String> propNames, boolean isDirty) {
        dirtyMarkerImpl.markDirty(propNames, isDirty);
    }

    @XmlTransient
    public Set<String> signedPropNames() {
        return dirtyMarkerImpl.signedPropNames();
    }

    @XmlTransient
    public Set<String> dirtyPropNames() {
        return dirtyMarkerImpl.dirtyPropNames();
    }

    @XmlTransient
    public void freeze() {
        dirtyMarkerImpl.freeze();
    }

    @XmlTransient
    public boolean frozen() {
        return dirtyMarkerImpl.frozen();
    }

    @XmlTransient
    public long version() {
        return dirtyMarkerImpl.version();
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public AccountContact setId(long id) {
        dirtyMarkerImpl.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("long")
    public long getAccountId() {
        return accountId;
    }

    public AccountContact setAccountId(long accountId) {
        dirtyMarkerImpl.setUpdatedPropName(ACCOUNT_ID);
        this.accountId = accountId;

        return this;
    }

    @Type("String")
    public String getMobile() {
        return mobile;
    }

    public AccountContact setMobile(String mobile) {
        dirtyMarkerImpl.setUpdatedPropName(MOBILE);
        this.mobile = mobile;

        return this;
    }

    @Type("String")
    public String getTelephone() {
        return telephone;
    }

    public AccountContact setTelephone(String telephone) {
        dirtyMarkerImpl.setUpdatedPropName(TELEPHONE);
        this.telephone = telephone;

        return this;
    }

    @Type("String")
    public String getEmail() {
        return email;
    }

    public AccountContact setEmail(String email) {
        dirtyMarkerImpl.setUpdatedPropName(EMAIL);
        this.email = email;

        return this;
    }

    @Type("String")
    public String getAddress() {
        return address;
    }

    public AccountContact setAddress(String address) {
        dirtyMarkerImpl.setUpdatedPropName(ADDRESS);
        this.address = address;

        return this;
    }

    @Type("String")
    public String getAddress2() {
        return address2;
    }

    public AccountContact setAddress2(String address2) {
        dirtyMarkerImpl.setUpdatedPropName(ADDRESS_2);
        this.address2 = address2;

        return this;
    }

    @Type("String")
    public String getCity() {
        return city;
    }

    public AccountContact setCity(String city) {
        dirtyMarkerImpl.setUpdatedPropName(CITY);
        this.city = city;

        return this;
    }

    @Type("String")
    public String getState() {
        return state;
    }

    public AccountContact setState(String state) {
        dirtyMarkerImpl.setUpdatedPropName(STATE);
        this.state = state;

        return this;
    }

    @Type("String")
    public String getCountry() {
        return country;
    }

    public AccountContact setCountry(String country) {
        dirtyMarkerImpl.setUpdatedPropName(COUNTRY);
        this.country = country;

        return this;
    }

    @Type("String")
    public String getZipCode() {
        return zipCode;
    }

    public AccountContact setZipCode(String zipCode) {
        dirtyMarkerImpl.setUpdatedPropName(ZIP_CODE);
        this.zipCode = zipCode;

        return this;
    }

    @Type("String")
    public String getCategory() {
        return category;
    }

    public AccountContact setCategory(String category) {
        dirtyMarkerImpl.setUpdatedPropName(CATEGORY);
        this.category = category;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AccountContact setDescription(String description) {
        dirtyMarkerImpl.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AccountContact setStatus(int status) {
        dirtyMarkerImpl.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AccountContact setLastUpdateTime(Timestamp lastUpdateTime) {
        dirtyMarkerImpl.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AccountContact setCreateTime(Timestamp createTime) {
        dirtyMarkerImpl.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(accountId);
        h = 31 * h + Objects.hashCode(mobile);
        h = 31 * h + Objects.hashCode(telephone);
        h = 31 * h + Objects.hashCode(email);
        h = 31 * h + Objects.hashCode(address);
        h = 31 * h + Objects.hashCode(address2);
        h = 31 * h + Objects.hashCode(city);
        h = 31 * h + Objects.hashCode(state);
        h = 31 * h + Objects.hashCode(country);
        h = 31 * h + Objects.hashCode(zipCode);
        h = 31 * h + Objects.hashCode(category);
        h = 31 * h + Objects.hashCode(description);
        h = 31 * h + Objects.hashCode(status);
        h = 31 * h + Objects.hashCode(lastUpdateTime);
        h = 31 * h + Objects.hashCode(createTime);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AccountContact) {
            final AccountContact other = (AccountContact) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(accountId, other.accountId)
                && Objects.equals(mobile, other.mobile)
                && Objects.equals(telephone, other.telephone)
                && Objects.equals(email, other.email)
                && Objects.equals(address, other.address)
                && Objects.equals(address2, other.address2)
                && Objects.equals(city, other.city)
                && Objects.equals(state, other.state)
                && Objects.equals(country, other.country)
                && Objects.equals(zipCode, other.zipCode)
                && Objects.equals(category, other.category)
                && Objects.equals(description, other.description)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", accountId=" + Objects.toString(accountId)
                 + ", mobile=" + Objects.toString(mobile)
                 + ", telephone=" + Objects.toString(telephone)
                 + ", email=" + Objects.toString(email)
                 + ", address=" + Objects.toString(address)
                 + ", address2=" + Objects.toString(address2)
                 + ", city=" + Objects.toString(city)
                 + ", state=" + Objects.toString(state)
                 + ", country=" + Objects.toString(country)
                 + ", zipCode=" + Objects.toString(zipCode)
                 + ", category=" + Objects.toString(category)
                 + ", description=" + Objects.toString(description)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}

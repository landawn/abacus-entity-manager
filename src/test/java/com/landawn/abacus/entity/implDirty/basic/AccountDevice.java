/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty.basic;

import java.util.Collection;
import java.util.Set;
import java.sql.Timestamp;
import com.landawn.abacus.core.DirtyMarkerImpl;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.entity.implDirty.basic.ImplDirtyBasicPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AccountDevice implements ImplDirtyBasicPNL.AccountDevicePNL, DirtyMarker {
    private final DirtyMarkerImpl dirtyMarkerImpl = new DirtyMarkerImpl(__);

    @Id
    @Column("id")
    private long id;

    @Column("account_id")
    private long accountId;

    @Column("name")
    private String name;

    @Column("udid")
    private String udid;

    @Column("platform")
    private String platform;

    @Column("model")
    private String model;

    @Column("manufacturer")
    private String manufacturer;

    @Column("produce_time")
    private Timestamp produceTime;

    @Column("category")
    private String category;

    @Column("description")
    private String description;

    @Column("status")
    private int status;

    @Column("last_update_time")
    private Timestamp lastUpdateTime;

    @Column("create_time")
    private Timestamp createTime;

    public AccountDevice() {
    }

    public AccountDevice(long id) {
        this();

        setId(id);
    }

    public AccountDevice(long accountId, String name, String udid, String platform, String model, String manufacturer, Timestamp produceTime, String category,
            String description, int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setAccountId(accountId);
        setName(name);
        setUDID(udid);
        setPlatform(platform);
        setModel(model);
        setManufacturer(manufacturer);
        setProduceTime(produceTime);
        setCategory(category);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AccountDevice(long id, long accountId, String name, String udid, String platform, String model, String manufacturer, Timestamp produceTime,
            String category, String description, int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setId(id);
        setAccountId(accountId);
        setName(name);
        setUDID(udid);
        setPlatform(platform);
        setModel(model);
        setManufacturer(manufacturer);
        setProduceTime(produceTime);
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

    public AccountDevice setId(long id) {
        dirtyMarkerImpl.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("long")
    public long getAccountId() {
        return accountId;
    }

    public AccountDevice setAccountId(long accountId) {
        dirtyMarkerImpl.setUpdatedPropName(ACCOUNT_ID);
        this.accountId = accountId;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AccountDevice setName(String name) {
        dirtyMarkerImpl.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getUDID() {
        return udid;
    }

    public AccountDevice setUDID(String udid) {
        dirtyMarkerImpl.setUpdatedPropName(UDID);
        this.udid = udid;

        return this;
    }

    @Type("String")
    public String getPlatform() {
        return platform;
    }

    public AccountDevice setPlatform(String platform) {
        dirtyMarkerImpl.setUpdatedPropName(PLATFORM);
        this.platform = platform;

        return this;
    }

    @Type("String")
    public String getModel() {
        return model;
    }

    public AccountDevice setModel(String model) {
        dirtyMarkerImpl.setUpdatedPropName(MODEL);
        this.model = model;

        return this;
    }

    @Type("String")
    public String getManufacturer() {
        return manufacturer;
    }

    public AccountDevice setManufacturer(String manufacturer) {
        dirtyMarkerImpl.setUpdatedPropName(MANUFACTURER);
        this.manufacturer = manufacturer;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getProduceTime() {
        return produceTime;
    }

    public AccountDevice setProduceTime(Timestamp produceTime) {
        dirtyMarkerImpl.setUpdatedPropName(PRODUCE_TIME);
        this.produceTime = produceTime;

        return this;
    }

    @Type("String")
    public String getCategory() {
        return category;
    }

    public AccountDevice setCategory(String category) {
        dirtyMarkerImpl.setUpdatedPropName(CATEGORY);
        this.category = category;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AccountDevice setDescription(String description) {
        dirtyMarkerImpl.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AccountDevice setStatus(int status) {
        dirtyMarkerImpl.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AccountDevice setLastUpdateTime(Timestamp lastUpdateTime) {
        dirtyMarkerImpl.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AccountDevice setCreateTime(Timestamp createTime) {
        dirtyMarkerImpl.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(accountId);
        h = 31 * h + Objects.hashCode(name);
        h = 31 * h + Objects.hashCode(udid);
        h = 31 * h + Objects.hashCode(platform);
        h = 31 * h + Objects.hashCode(model);
        h = 31 * h + Objects.hashCode(manufacturer);
        h = 31 * h + Objects.hashCode(produceTime);
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

        if (obj instanceof AccountDevice) {
            final AccountDevice other = (AccountDevice) obj;

            return Objects.equals(id, other.id) && Objects.equals(accountId, other.accountId) && Objects.equals(name, other.name)
                    && Objects.equals(udid, other.udid) && Objects.equals(platform, other.platform) && Objects.equals(model, other.model)
                    && Objects.equals(manufacturer, other.manufacturer) && Objects.equals(produceTime, other.produceTime)
                    && Objects.equals(category, other.category) && Objects.equals(description, other.description) && Objects.equals(status, other.status)
                    && Objects.equals(lastUpdateTime, other.lastUpdateTime) && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", accountId=" + Objects.toString(accountId) + ", name=" + Objects.toString(name) + ", udid="
                + Objects.toString(udid) + ", platform=" + Objects.toString(platform) + ", model=" + Objects.toString(model) + ", manufacturer="
                + Objects.toString(manufacturer) + ", produceTime=" + Objects.toString(produceTime) + ", category=" + Objects.toString(category)
                + ", description=" + Objects.toString(description) + ", status=" + Objects.toString(status) + ", lastUpdateTime="
                + Objects.toString(lastUpdateTime) + ", createTime=" + Objects.toString(createTime) + "}";
    }
}

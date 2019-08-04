/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.extendDirty.vc;

import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.extendDirty.vc.ExtendDirtyVCPNL;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AccountDevice extends AbstractDirtyMarker implements ExtendDirtyVCPNL.AccountDevicePNL {
    private long id;
    private long accountId;
    private String name;
    private String udid;
    private String platform;
    private String model;
    private String manufacturer;
    private Timestamp produceTime;
    private String category;
    private String description;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;

    public AccountDevice() {
        super(__);
    }

    public AccountDevice(long id) {
        this();

        setId(id);
    }

    public AccountDevice(long accountId, String name, String udid, String platform, 
        String model, String manufacturer, Timestamp produceTime, 
        String category, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime) {
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

    public AccountDevice(long id, long accountId, String name, String udid, String platform, 
        String model, String manufacturer, Timestamp produceTime, 
        String category, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime) {
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

    @Type("long")
    public long getId() {
        return id;
    }

    public AccountDevice setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("long")
    public long getAccountId() {
        return accountId;
    }

    public AccountDevice setAccountId(long accountId) {
        super.setUpdatedPropName(ACCOUNT_ID);
        this.accountId = accountId;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AccountDevice setName(String name) {
        super.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getUDID() {
        return udid;
    }

    public AccountDevice setUDID(String udid) {
        super.setUpdatedPropName(UDID);
        this.udid = udid;

        return this;
    }

    @Type("String")
    public String getPlatform() {
        return platform;
    }

    public AccountDevice setPlatform(String platform) {
        super.setUpdatedPropName(PLATFORM);
        this.platform = platform;

        return this;
    }

    @Type("String")
    public String getModel() {
        return model;
    }

    public AccountDevice setModel(String model) {
        super.setUpdatedPropName(MODEL);
        this.model = model;

        return this;
    }

    @Type("String")
    public String getManufacturer() {
        return manufacturer;
    }

    public AccountDevice setManufacturer(String manufacturer) {
        super.setUpdatedPropName(MANUFACTURER);
        this.manufacturer = manufacturer;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getProduceTime() {
        return produceTime;
    }

    public AccountDevice setProduceTime(Timestamp produceTime) {
        super.setUpdatedPropName(PRODUCE_TIME);
        this.produceTime = produceTime;

        return this;
    }

    @Type("String")
    public String getCategory() {
        return category;
    }

    public AccountDevice setCategory(String category) {
        super.setUpdatedPropName(CATEGORY);
        this.category = category;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AccountDevice setDescription(String description) {
        super.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AccountDevice setStatus(int status) {
        super.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AccountDevice setLastUpdateTime(Timestamp lastUpdateTime) {
        super.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AccountDevice setCreateTime(Timestamp createTime) {
        super.setUpdatedPropName(CREATE_TIME);
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

            return Objects.equals(id, other.id)
                && Objects.equals(accountId, other.accountId)
                && Objects.equals(name, other.name)
                && Objects.equals(udid, other.udid)
                && Objects.equals(platform, other.platform)
                && Objects.equals(model, other.model)
                && Objects.equals(manufacturer, other.manufacturer)
                && Objects.equals(produceTime, other.produceTime)
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
                 + ", name=" + Objects.toString(name)
                 + ", udid=" + Objects.toString(udid)
                 + ", platform=" + Objects.toString(platform)
                 + ", model=" + Objects.toString(model)
                 + ", manufacturer=" + Objects.toString(manufacturer)
                 + ", produceTime=" + Objects.toString(produceTime)
                 + ", category=" + Objects.toString(category)
                 + ", description=" + Objects.toString(description)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}

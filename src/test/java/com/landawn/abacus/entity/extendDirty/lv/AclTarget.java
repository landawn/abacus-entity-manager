/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.extendDirty.lv;

import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.extendDirty.lv.ExtendDirtyLVPNL;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclTarget extends AbstractDirtyMarker implements ExtendDirtyLVPNL.AclTargetPNL {
    private long id;
    private String gui;
    private String name;
    private String category;
    private String subCategory;
    private String type;
    private String subType;
    private String description;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;

    public AclTarget() {
        super(__);
    }

    public AclTarget(long id) {
        this();

        setId(id);
    }

    public AclTarget(String gui, String name, String category, String subCategory, 
        String type, String subType, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setGUI(gui);
        setName(name);
        setCategory(category);
        setSubCategory(subCategory);
        setType(type);
        setSubType(subType);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AclTarget(long id, String gui, String name, String category, String subCategory, 
        String type, String subType, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setId(id);
        setGUI(gui);
        setName(name);
        setCategory(category);
        setSubCategory(subCategory);
        setType(type);
        setSubType(subType);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public AclTarget setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getGUI() {
        return gui;
    }

    public AclTarget setGUI(String gui) {
        super.setUpdatedPropName(GUI);
        this.gui = gui;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AclTarget setName(String name) {
        super.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getCategory() {
        return category;
    }

    public AclTarget setCategory(String category) {
        super.setUpdatedPropName(CATEGORY);
        this.category = category;

        return this;
    }

    @Type("String")
    public String getSubCategory() {
        return subCategory;
    }

    public AclTarget setSubCategory(String subCategory) {
        super.setUpdatedPropName(SUB_CATEGORY);
        this.subCategory = subCategory;

        return this;
    }

    @Type("String")
    public String getType() {
        return type;
    }

    public AclTarget setType(String type) {
        super.setUpdatedPropName(TYPE);
        this.type = type;

        return this;
    }

    @Type("String")
    public String getSubType() {
        return subType;
    }

    public AclTarget setSubType(String subType) {
        super.setUpdatedPropName(SUB_TYPE);
        this.subType = subType;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclTarget setDescription(String description) {
        super.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclTarget setStatus(int status) {
        super.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclTarget setLastUpdateTime(Timestamp lastUpdateTime) {
        super.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclTarget setCreateTime(Timestamp createTime) {
        super.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(gui);
        h = 31 * h + Objects.hashCode(name);
        h = 31 * h + Objects.hashCode(category);
        h = 31 * h + Objects.hashCode(subCategory);
        h = 31 * h + Objects.hashCode(type);
        h = 31 * h + Objects.hashCode(subType);
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

        if (obj instanceof AclTarget) {
            final AclTarget other = (AclTarget) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(gui, other.gui)
                && Objects.equals(name, other.name)
                && Objects.equals(category, other.category)
                && Objects.equals(subCategory, other.subCategory)
                && Objects.equals(type, other.type)
                && Objects.equals(subType, other.subType)
                && Objects.equals(description, other.description)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", gui=" + Objects.toString(gui)
                 + ", name=" + Objects.toString(name)
                 + ", category=" + Objects.toString(category)
                 + ", subCategory=" + Objects.toString(subCategory)
                 + ", type=" + Objects.toString(type)
                 + ", subType=" + Objects.toString(subType)
                 + ", description=" + Objects.toString(description)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}

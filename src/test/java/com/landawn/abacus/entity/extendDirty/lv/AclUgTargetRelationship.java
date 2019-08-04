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
public class AclUgTargetRelationship extends AbstractDirtyMarker implements ExtendDirtyLVPNL.AclUgTargetRelationshipPNL {
    private long id;
    private String ugGui;
    private String targetGui;
    private long privilege;
    private String description;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;

    public AclUgTargetRelationship() {
        super(__);
    }

    public AclUgTargetRelationship(long id) {
        this();

        setId(id);
    }

    public AclUgTargetRelationship(String ugGui, String targetGui, long privilege, String description, 
        int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setUgGui(ugGui);
        setTargetGui(targetGui);
        setPrivilege(privilege);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AclUgTargetRelationship(long id, String ugGui, String targetGui, long privilege, 
        String description, int status, Timestamp lastUpdateTime, 
        Timestamp createTime) {
        this();

        setId(id);
        setUgGui(ugGui);
        setTargetGui(targetGui);
        setPrivilege(privilege);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public AclUgTargetRelationship setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getUgGui() {
        return ugGui;
    }

    public AclUgTargetRelationship setUgGui(String ugGui) {
        super.setUpdatedPropName(UG_GUI);
        this.ugGui = ugGui;

        return this;
    }

    @Type("String")
    public String getTargetGui() {
        return targetGui;
    }

    public AclUgTargetRelationship setTargetGui(String targetGui) {
        super.setUpdatedPropName(TARGET_GUI);
        this.targetGui = targetGui;

        return this;
    }

    @Type("long")
    public long getPrivilege() {
        return privilege;
    }

    public AclUgTargetRelationship setPrivilege(long privilege) {
        super.setUpdatedPropName(PRIVILEGE);
        this.privilege = privilege;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUgTargetRelationship setDescription(String description) {
        super.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUgTargetRelationship setStatus(int status) {
        super.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUgTargetRelationship setLastUpdateTime(Timestamp lastUpdateTime) {
        super.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUgTargetRelationship setCreateTime(Timestamp createTime) {
        super.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(ugGui);
        h = 31 * h + Objects.hashCode(targetGui);
        h = 31 * h + Objects.hashCode(privilege);
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

        if (obj instanceof AclUgTargetRelationship) {
            final AclUgTargetRelationship other = (AclUgTargetRelationship) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(ugGui, other.ugGui)
                && Objects.equals(targetGui, other.targetGui)
                && Objects.equals(privilege, other.privilege)
                && Objects.equals(description, other.description)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", ugGui=" + Objects.toString(ugGui)
                 + ", targetGui=" + Objects.toString(targetGui)
                 + ", privilege=" + Objects.toString(privilege)
                 + ", description=" + Objects.toString(description)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}

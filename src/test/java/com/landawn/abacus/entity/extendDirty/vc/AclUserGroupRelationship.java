/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.extendDirty.vc;

import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.extendDirty.vc.ExtendDirtyVCPNL;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclUserGroupRelationship extends AbstractDirtyMarker implements ExtendDirtyVCPNL.AclUserGroupRelationshipPNL {

    @Id
    @Column("id")
    private long id;

    @Column("user_gui")
    private String userGUI;

    @Column("group_gui")
    private String groupGUI;

    @Column("description")
    private String description;

    @Column("status")
    private int status;

    @Column("last_update_time")
    private Timestamp lastUpdateTime;

    @Column("create_time")
    private Timestamp createTime;

    public AclUserGroupRelationship() {
        super(__);
    }

    public AclUserGroupRelationship(long id) {
        this();

        setId(id);
    }

    public AclUserGroupRelationship(String userGUI, String groupGUI, String description, int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setUserGUI(userGUI);
        setGroupGUI(groupGUI);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AclUserGroupRelationship(long id, String userGUI, String groupGUI, String description, int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setId(id);
        setUserGUI(userGUI);
        setGroupGUI(groupGUI);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public AclUserGroupRelationship setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getUserGUI() {
        return userGUI;
    }

    public AclUserGroupRelationship setUserGUI(String userGUI) {
        super.setUpdatedPropName(USER_GUI);
        this.userGUI = userGUI;

        return this;
    }

    @Type("String")
    public String getGroupGUI() {
        return groupGUI;
    }

    public AclUserGroupRelationship setGroupGUI(String groupGUI) {
        super.setUpdatedPropName(GROUP_GUI);
        this.groupGUI = groupGUI;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUserGroupRelationship setDescription(String description) {
        super.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUserGroupRelationship setStatus(int status) {
        super.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUserGroupRelationship setLastUpdateTime(Timestamp lastUpdateTime) {
        super.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUserGroupRelationship setCreateTime(Timestamp createTime) {
        super.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(userGUI);
        h = 31 * h + Objects.hashCode(groupGUI);
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

        if (obj instanceof AclUserGroupRelationship) {
            final AclUserGroupRelationship other = (AclUserGroupRelationship) obj;

            return Objects.equals(id, other.id) && Objects.equals(userGUI, other.userGUI) && Objects.equals(groupGUI, other.groupGUI)
                    && Objects.equals(description, other.description) && Objects.equals(status, other.status)
                    && Objects.equals(lastUpdateTime, other.lastUpdateTime) && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", userGUI=" + Objects.toString(userGUI) + ", groupGUI=" + Objects.toString(groupGUI) + ", description="
                + Objects.toString(description) + ", status=" + Objects.toString(status) + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                + ", createTime=" + Objects.toString(createTime) + "}";
    }
}

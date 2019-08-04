/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.pjo.lvc;

import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclUserGroupRelationship {
    private long id;
    private String userGUI;
    private String groupGUI;
    private String description;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;

    public AclUserGroupRelationship() {
    }

    public AclUserGroupRelationship(long id) {
        this();

        setId(id);
    }

    public AclUserGroupRelationship(String userGUI, String groupGUI, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setUserGUI(userGUI);
        setGroupGUI(groupGUI);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AclUserGroupRelationship(long id, String userGUI, String groupGUI, String description, 
        int status, Timestamp lastUpdateTime, Timestamp createTime) {
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
        this.id = id;

        return this;
    }

    @Type("String")
    public String getUserGUI() {
        return userGUI;
    }

    public AclUserGroupRelationship setUserGUI(String userGUI) {
        this.userGUI = userGUI;

        return this;
    }

    @Type("String")
    public String getGroupGUI() {
        return groupGUI;
    }

    public AclUserGroupRelationship setGroupGUI(String groupGUI) {
        this.groupGUI = groupGUI;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUserGroupRelationship setDescription(String description) {
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUserGroupRelationship setStatus(int status) {
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUserGroupRelationship setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUserGroupRelationship setCreateTime(Timestamp createTime) {
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

            return Objects.equals(id, other.id)
                && Objects.equals(userGUI, other.userGUI)
                && Objects.equals(groupGUI, other.groupGUI)
                && Objects.equals(description, other.description)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", userGUI=" + Objects.toString(userGUI)
                 + ", groupGUI=" + Objects.toString(groupGUI)
                 + ", description=" + Objects.toString(description)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}

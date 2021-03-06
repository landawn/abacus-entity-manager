/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.pjo.lv;

import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclUgTargetRelationship {

    @Id
    @Column("id")
    private long id;

    @Column("ug_gui")
    private String ugGui;

    @Column("target_gui")
    private String targetGui;

    @Column("privilege")
    private long privilege;

    @Column("description")
    private String description;

    @Column("status")
    private int status;

    @Column("last_update_time")
    private Timestamp lastUpdateTime;

    @Column("create_time")
    private Timestamp createTime;

    public AclUgTargetRelationship() {
    }

    public AclUgTargetRelationship(long id) {
        this();

        setId(id);
    }

    public AclUgTargetRelationship(String ugGui, String targetGui, long privilege, String description, int status, Timestamp lastUpdateTime,
            Timestamp createTime) {
        this();

        setUgGui(ugGui);
        setTargetGui(targetGui);
        setPrivilege(privilege);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AclUgTargetRelationship(long id, String ugGui, String targetGui, long privilege, String description, int status, Timestamp lastUpdateTime,
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
        this.id = id;

        return this;
    }

    @Type("String")
    public String getUgGui() {
        return ugGui;
    }

    public AclUgTargetRelationship setUgGui(String ugGui) {
        this.ugGui = ugGui;

        return this;
    }

    @Type("String")
    public String getTargetGui() {
        return targetGui;
    }

    public AclUgTargetRelationship setTargetGui(String targetGui) {
        this.targetGui = targetGui;

        return this;
    }

    @Type("long")
    public long getPrivilege() {
        return privilege;
    }

    public AclUgTargetRelationship setPrivilege(long privilege) {
        this.privilege = privilege;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUgTargetRelationship setDescription(String description) {
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUgTargetRelationship setStatus(int status) {
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUgTargetRelationship setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUgTargetRelationship setCreateTime(Timestamp createTime) {
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

            return Objects.equals(id, other.id) && Objects.equals(ugGui, other.ugGui) && Objects.equals(targetGui, other.targetGui)
                    && Objects.equals(privilege, other.privilege) && Objects.equals(description, other.description) && Objects.equals(status, other.status)
                    && Objects.equals(lastUpdateTime, other.lastUpdateTime) && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", ugGui=" + Objects.toString(ugGui) + ", targetGui=" + Objects.toString(targetGui) + ", privilege="
                + Objects.toString(privilege) + ", description=" + Objects.toString(description) + ", status=" + Objects.toString(status) + ", lastUpdateTime="
                + Objects.toString(lastUpdateTime) + ", createTime=" + Objects.toString(createTime) + "}";
    }
}

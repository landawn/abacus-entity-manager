/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.extendDirty.vc;

import java.util.List;
import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.extendDirty.vc.ExtendDirtyVCPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclUser extends AbstractDirtyMarker implements ExtendDirtyVCPNL.AclUserPNL {

    @Id
    @Column("id")
    private long id;

    @Column("gui")
    private String gui;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("status")
    private int status;

    @Column("last_update_time")
    private Timestamp lastUpdateTime;

    @Column("create_time")
    private Timestamp createTime;

    private List<AclGroup> groupList;

    public AclUser() {
        super(__);
    }

    public AclUser(long id) {
        this();

        setId(id);
    }

    public AclUser(String gui, String name, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime, List<AclGroup> groupList) {
        this();

        setGUI(gui);
        setName(name);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setGroupList(groupList);
    }

    public AclUser(long id, String gui, String name, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime, List<AclGroup> groupList) {
        this();

        setId(id);
        setGUI(gui);
        setName(name);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setGroupList(groupList);
    }

    @XmlTransient
    public boolean isDirty() {
        return super.isDirty()
               || (groupList == null ? false : super.isEntityDirty(groupList));
    }

    public void markDirty(boolean isDirty) {
        super.markDirty(isDirty);

        if (groupList != null) {
            super.markEntityDirty(groupList, isDirty);
        }
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public AclUser setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getGUI() {
        return gui;
    }

    public AclUser setGUI(String gui) {
        super.setUpdatedPropName(GUI);
        this.gui = gui;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AclUser setName(String name) {
        super.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUser setDescription(String description) {
        super.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUser setStatus(int status) {
        super.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUser setLastUpdateTime(Timestamp lastUpdateTime) {
        super.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUser setCreateTime(Timestamp createTime) {
        super.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.extendDirty.vc.AclGroup>")
    public List<AclGroup> getGroupList() {
        return groupList;
    }

    public AclUser setGroupList(List<AclGroup> groupList) {
        super.setUpdatedPropName(GROUP_LIST);
        this.groupList = groupList;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(gui);
        h = 31 * h + Objects.hashCode(name);
        h = 31 * h + Objects.hashCode(description);
        h = 31 * h + Objects.hashCode(status);
        h = 31 * h + Objects.hashCode(lastUpdateTime);
        h = 31 * h + Objects.hashCode(createTime);
        h = 31 * h + Objects.hashCode(groupList);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AclUser) {
            final AclUser other = (AclUser) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(gui, other.gui)
                && Objects.equals(name, other.name)
                && Objects.equals(description, other.description)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime)
                && Objects.equals(groupList, other.groupList);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", gui=" + Objects.toString(gui)
                 + ", name=" + Objects.toString(name)
                 + ", description=" + Objects.toString(description)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + ", groupList=" + Objects.toString(groupList)
                 + "}";
    }
}

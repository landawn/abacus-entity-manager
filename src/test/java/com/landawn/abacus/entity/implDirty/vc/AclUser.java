/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty.vc;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.sql.Timestamp;
import com.landawn.abacus.core.DirtyMarkerImpl;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.entity.implDirty.vc.ImplDirtyVCPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclUser implements ImplDirtyVCPNL.AclUserPNL, DirtyMarker {
    private final DirtyMarkerImpl dirtyMarkerImpl = new DirtyMarkerImpl(__);

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
    }

    public AclUser(long id) {
        this();

        setId(id);
    }

    public AclUser(String gui, String name, String description, int status, Timestamp lastUpdateTime, Timestamp createTime, List<AclGroup> groupList) {
        this();

        setGUI(gui);
        setName(name);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setGroupList(groupList);
    }

    public AclUser(long id, String gui, String name, String description, int status, Timestamp lastUpdateTime, Timestamp createTime, List<AclGroup> groupList) {
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

    public String entityName() {
        return __;
    }

    @XmlTransient
    public boolean isDirty() {
        return dirtyMarkerImpl.isDirty() || (groupList == null ? false : dirtyMarkerImpl.isEntityDirty(groupList));
    }

    @XmlTransient
    public boolean isDirty(String propName) {
        return dirtyMarkerImpl.isDirty(propName);
    }

    public void markDirty(boolean isDirty) {
        dirtyMarkerImpl.markDirty(isDirty);

        if (groupList != null) {
            dirtyMarkerImpl.markEntityDirty(groupList, isDirty);
        }
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

    public AclUser setId(long id) {
        dirtyMarkerImpl.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getGUI() {
        return gui;
    }

    public AclUser setGUI(String gui) {
        dirtyMarkerImpl.setUpdatedPropName(GUI);
        this.gui = gui;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AclUser setName(String name) {
        dirtyMarkerImpl.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUser setDescription(String description) {
        dirtyMarkerImpl.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUser setStatus(int status) {
        dirtyMarkerImpl.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUser setLastUpdateTime(Timestamp lastUpdateTime) {
        dirtyMarkerImpl.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUser setCreateTime(Timestamp createTime) {
        dirtyMarkerImpl.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.implDirty.vc.AclGroup>")
    public List<AclGroup> getGroupList() {
        return groupList;
    }

    public AclUser setGroupList(List<AclGroup> groupList) {
        dirtyMarkerImpl.setUpdatedPropName(GROUP_LIST);
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

            return Objects.equals(id, other.id) && Objects.equals(gui, other.gui) && Objects.equals(name, other.name)
                    && Objects.equals(description, other.description) && Objects.equals(status, other.status)
                    && Objects.equals(lastUpdateTime, other.lastUpdateTime) && Objects.equals(createTime, other.createTime)
                    && Objects.equals(groupList, other.groupList);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", gui=" + Objects.toString(gui) + ", name=" + Objects.toString(name) + ", description="
                + Objects.toString(description) + ", status=" + Objects.toString(status) + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                + ", createTime=" + Objects.toString(createTime) + ", groupList=" + Objects.toString(groupList) + "}";
    }
}

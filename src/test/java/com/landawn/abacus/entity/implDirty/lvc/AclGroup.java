/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty.lvc;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.sql.Timestamp;
import com.landawn.abacus.core.DirtyMarkerImpl;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.entity.implDirty.lvc.ImplDirtyLVCPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclGroup implements ImplDirtyLVCPNL.AclGroupPNL, DirtyMarker {
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

    private List<AclUser> userList;

    public AclGroup() {
    }

    public AclGroup(long id) {
        this();

        setId(id);
    }

    public AclGroup(String gui, String name, String description, int status, Timestamp lastUpdateTime, Timestamp createTime, List<AclUser> userList) {
        this();

        setGUI(gui);
        setName(name);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setUserList(userList);
    }

    public AclGroup(long id, String gui, String name, String description, int status, Timestamp lastUpdateTime, Timestamp createTime, List<AclUser> userList) {
        this();

        setId(id);
        setGUI(gui);
        setName(name);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setUserList(userList);
    }

    public String entityName() {
        return __;
    }

    @XmlTransient
    public boolean isDirty() {
        return dirtyMarkerImpl.isDirty() || (userList == null ? false : dirtyMarkerImpl.isEntityDirty(userList));
    }

    @XmlTransient
    public boolean isDirty(String propName) {
        return dirtyMarkerImpl.isDirty(propName);
    }

    public void markDirty(boolean isDirty) {
        dirtyMarkerImpl.markDirty(isDirty);

        if (userList != null) {
            dirtyMarkerImpl.markEntityDirty(userList, isDirty);
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

    public AclGroup setId(long id) {
        dirtyMarkerImpl.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getGUI() {
        return gui;
    }

    public AclGroup setGUI(String gui) {
        dirtyMarkerImpl.setUpdatedPropName(GUI);
        this.gui = gui;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AclGroup setName(String name) {
        dirtyMarkerImpl.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclGroup setDescription(String description) {
        dirtyMarkerImpl.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclGroup setStatus(int status) {
        dirtyMarkerImpl.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclGroup setLastUpdateTime(Timestamp lastUpdateTime) {
        dirtyMarkerImpl.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclGroup setCreateTime(Timestamp createTime) {
        dirtyMarkerImpl.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.implDirty.lvc.AclUser>")
    public List<AclUser> getUserList() {
        return userList;
    }

    public AclGroup setUserList(List<AclUser> userList) {
        dirtyMarkerImpl.setUpdatedPropName(USER_LIST);
        this.userList = userList;

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
        h = 31 * h + Objects.hashCode(userList);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AclGroup) {
            final AclGroup other = (AclGroup) obj;

            return Objects.equals(id, other.id) && Objects.equals(gui, other.gui) && Objects.equals(name, other.name)
                    && Objects.equals(description, other.description) && Objects.equals(status, other.status)
                    && Objects.equals(lastUpdateTime, other.lastUpdateTime) && Objects.equals(createTime, other.createTime)
                    && Objects.equals(userList, other.userList);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", gui=" + Objects.toString(gui) + ", name=" + Objects.toString(name) + ", description="
                + Objects.toString(description) + ", status=" + Objects.toString(status) + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                + ", createTime=" + Objects.toString(createTime) + ", userList=" + Objects.toString(userList) + "}";
    }
}

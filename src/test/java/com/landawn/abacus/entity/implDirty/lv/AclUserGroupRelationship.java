/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty.lv;

import java.util.Collection;
import java.util.Set;
import java.sql.Timestamp;
import com.landawn.abacus.core.DirtyMarkerImpl;
import java.util.Objects;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.entity.implDirty.lv.ImplDirtyLVPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclUserGroupRelationship implements ImplDirtyLVPNL.AclUserGroupRelationshipPNL, DirtyMarker {
    private final DirtyMarkerImpl dirtyMarkerImpl = new DirtyMarkerImpl(__);

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

    public AclUserGroupRelationship setId(long id) {
        dirtyMarkerImpl.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getUserGUI() {
        return userGUI;
    }

    public AclUserGroupRelationship setUserGUI(String userGUI) {
        dirtyMarkerImpl.setUpdatedPropName(USER_GUI);
        this.userGUI = userGUI;

        return this;
    }

    @Type("String")
    public String getGroupGUI() {
        return groupGUI;
    }

    public AclUserGroupRelationship setGroupGUI(String groupGUI) {
        dirtyMarkerImpl.setUpdatedPropName(GROUP_GUI);
        this.groupGUI = groupGUI;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclUserGroupRelationship setDescription(String description) {
        dirtyMarkerImpl.setUpdatedPropName(DESCRIPTION);
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclUserGroupRelationship setStatus(int status) {
        dirtyMarkerImpl.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclUserGroupRelationship setLastUpdateTime(Timestamp lastUpdateTime) {
        dirtyMarkerImpl.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclUserGroupRelationship setCreateTime(Timestamp createTime) {
        dirtyMarkerImpl.setUpdatedPropName(CREATE_TIME);
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

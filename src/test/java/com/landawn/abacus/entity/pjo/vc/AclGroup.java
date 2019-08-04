/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.pjo.vc;

import java.util.List;
import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AclGroup {
    private long id;
    private String gui;
    private String name;
    private String description;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;
    private List<AclUser> userList;

    public AclGroup() {
    }

    public AclGroup(long id) {
        this();

        setId(id);
    }

    public AclGroup(String gui, String name, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime, List<AclUser> userList) {
        this();

        setGUI(gui);
        setName(name);
        setDescription(description);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setUserList(userList);
    }

    public AclGroup(long id, String gui, String name, String description, int status, 
        Timestamp lastUpdateTime, Timestamp createTime, List<AclUser> userList) {
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

    @Type("long")
    public long getId() {
        return id;
    }

    public AclGroup setId(long id) {
        this.id = id;

        return this;
    }

    @Type("String")
    public String getGUI() {
        return gui;
    }

    public AclGroup setGUI(String gui) {
        this.gui = gui;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public AclGroup setName(String name) {
        this.name = name;

        return this;
    }

    @Type("String")
    public String getDescription() {
        return description;
    }

    public AclGroup setDescription(String description) {
        this.description = description;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public AclGroup setStatus(int status) {
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AclGroup setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public AclGroup setCreateTime(Timestamp createTime) {
        this.createTime = createTime;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.pjo.vc.AclUser>")
    public List<AclUser> getUserList() {
        return userList;
    }

    public AclGroup setUserList(List<AclUser> userList) {
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

            return Objects.equals(id, other.id)
                && Objects.equals(gui, other.gui)
                && Objects.equals(name, other.name)
                && Objects.equals(description, other.description)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime)
                && Objects.equals(userList, other.userList);
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
                 + ", userList=" + Objects.toString(userList)
                 + "}";
    }
}

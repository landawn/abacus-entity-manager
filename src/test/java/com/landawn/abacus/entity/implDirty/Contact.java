/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import com.landawn.abacus.core.DirtyMarkerImpl;
import java.util.Objects;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.entity.implDirty.ImplDirtyPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class Contact implements ImplDirtyPNL.ContactPNL, DirtyMarker {
    private final DirtyMarkerImpl dirtyMarkerImpl = new DirtyMarkerImpl(__);

    private long id;
    private long hostId;
    private String mobile;
    private String telephone;
    private String email;
    private String address;
    private List<Email> emailList;

    public Contact() {
    }

    public Contact(long id) {
        this();

        setId(id);
    }

    public Contact(long hostId, String mobile, String telephone, String email, 
        String address, List<Email> emailList) {
        this();

        setHostId(hostId);
        setMobile(mobile);
        setTelephone(telephone);
        setEmail(email);
        setAddress(address);
        setEmailList(emailList);
    }

    public Contact(long id, long hostId, String mobile, String telephone, String email, 
        String address, List<Email> emailList) {
        this();

        setId(id);
        setHostId(hostId);
        setMobile(mobile);
        setTelephone(telephone);
        setEmail(email);
        setAddress(address);
        setEmailList(emailList);
    }

    public String entityName() {
        return __;
    }

    @XmlTransient
    public boolean isDirty() {
        return dirtyMarkerImpl.isDirty()
               || (emailList == null ? false : dirtyMarkerImpl.isEntityDirty(emailList));
    }

    @XmlTransient
    public boolean isDirty(String propName) {
        return dirtyMarkerImpl.isDirty(propName);
    }

    public void markDirty(boolean isDirty) {
        dirtyMarkerImpl.markDirty(isDirty);

        if (emailList != null) {
            dirtyMarkerImpl.markEntityDirty(emailList, isDirty);
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

    public Contact setId(long id) {
        dirtyMarkerImpl.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("long")
    public long getHostId() {
        return hostId;
    }

    public Contact setHostId(long hostId) {
        dirtyMarkerImpl.setUpdatedPropName(HOST_ID);
        this.hostId = hostId;

        return this;
    }

    @Type("String")
    public String getMobile() {
        return mobile;
    }

    public Contact setMobile(String mobile) {
        dirtyMarkerImpl.setUpdatedPropName(MOBILE);
        this.mobile = mobile;

        return this;
    }

    @Type("String")
    public String getTelephone() {
        return telephone;
    }

    public Contact setTelephone(String telephone) {
        dirtyMarkerImpl.setUpdatedPropName(TELEPHONE);
        this.telephone = telephone;

        return this;
    }

    @Type("String")
    public String getEmail() {
        return email;
    }

    public Contact setEmail(String email) {
        dirtyMarkerImpl.setUpdatedPropName(EMAIL);
        this.email = email;

        return this;
    }

    @Type("String")
    public String getAddress() {
        return address;
    }

    public Contact setAddress(String address) {
        dirtyMarkerImpl.setUpdatedPropName(ADDRESS);
        this.address = address;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.implDirty.Email>")
    public List<Email> getEmailList() {
        return emailList;
    }

    public Contact setEmailList(List<Email> emailList) {
        dirtyMarkerImpl.setUpdatedPropName(EMAIL_LIST);
        this.emailList = emailList;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(hostId);
        h = 31 * h + Objects.hashCode(mobile);
        h = 31 * h + Objects.hashCode(telephone);
        h = 31 * h + Objects.hashCode(email);
        h = 31 * h + Objects.hashCode(address);
        h = 31 * h + Objects.hashCode(emailList);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Contact) {
            final Contact other = (Contact) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(hostId, other.hostId)
                && Objects.equals(mobile, other.mobile)
                && Objects.equals(telephone, other.telephone)
                && Objects.equals(email, other.email)
                && Objects.equals(address, other.address)
                && Objects.equals(emailList, other.emailList);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", hostId=" + Objects.toString(hostId)
                 + ", mobile=" + Objects.toString(mobile)
                 + ", telephone=" + Objects.toString(telephone)
                 + ", email=" + Objects.toString(email)
                 + ", address=" + Objects.toString(address)
                 + ", emailList=" + Objects.toString(emailList)
                 + "}";
    }
}

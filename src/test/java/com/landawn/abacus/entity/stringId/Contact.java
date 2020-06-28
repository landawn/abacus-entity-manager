/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.stringId;

import java.util.List;
import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.stringId.StringIdPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class Contact extends AbstractDirtyMarker implements StringIdPNL.ContactPNL {

    @Id
    @Column("id")
    private long id;

    @Column("hostid")
    private long hostId;

    @Column("mobile")
    private String mobile;

    @Column("telephone")
    private String telephone;

    @Column("email")
    private String email;

    @Column("address")
    private String address;

    private List<Email> emailList;

    public Contact() {
        super(__);
    }

    public Contact(long id) {
        this();

        setId(id);
    }

    public Contact(long hostId, String mobile, String telephone, String email, String address, List<Email> emailList) {
        this();

        setHostId(hostId);
        setMobile(mobile);
        setTelephone(telephone);
        setEmail(email);
        setAddress(address);
        setEmailList(emailList);
    }

    public Contact(long id, long hostId, String mobile, String telephone, String email, String address, List<Email> emailList) {
        this();

        setId(id);
        setHostId(hostId);
        setMobile(mobile);
        setTelephone(telephone);
        setEmail(email);
        setAddress(address);
        setEmailList(emailList);
    }

    @XmlTransient
    public boolean isDirty() {
        return super.isDirty() || (emailList == null ? false : super.isEntityDirty(emailList));
    }

    public void markDirty(boolean isDirty) {
        super.markDirty(isDirty);

        if (emailList != null) {
            super.markEntityDirty(emailList, isDirty);
        }
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public Contact setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("long")
    public long getHostId() {
        return hostId;
    }

    public Contact setHostId(long hostId) {
        super.setUpdatedPropName(HOST_ID);
        this.hostId = hostId;

        return this;
    }

    @Type("String")
    public String getMobile() {
        return mobile;
    }

    public Contact setMobile(String mobile) {
        super.setUpdatedPropName(MOBILE);
        this.mobile = mobile;

        return this;
    }

    @Type("String")
    public String getTelephone() {
        return telephone;
    }

    public Contact setTelephone(String telephone) {
        super.setUpdatedPropName(TELEPHONE);
        this.telephone = telephone;

        return this;
    }

    @Type("String")
    public String getEmail() {
        return email;
    }

    public Contact setEmail(String email) {
        super.setUpdatedPropName(EMAIL);
        this.email = email;

        return this;
    }

    @Type("String")
    public String getAddress() {
        return address;
    }

    public Contact setAddress(String address) {
        super.setUpdatedPropName(ADDRESS);
        this.address = address;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.stringId.Email>")
    public List<Email> getEmailList() {
        return emailList;
    }

    public Contact setEmailList(List<Email> emailList) {
        super.setUpdatedPropName(EMAIL_LIST);
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

            return Objects.equals(id, other.id) && Objects.equals(hostId, other.hostId) && Objects.equals(mobile, other.mobile)
                    && Objects.equals(telephone, other.telephone) && Objects.equals(email, other.email) && Objects.equals(address, other.address)
                    && Objects.equals(emailList, other.emailList);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", hostId=" + Objects.toString(hostId) + ", mobile=" + Objects.toString(mobile) + ", telephone="
                + Objects.toString(telephone) + ", email=" + Objects.toString(email) + ", address=" + Objects.toString(address) + ", emailList="
                + Objects.toString(emailList) + "}";
    }
}

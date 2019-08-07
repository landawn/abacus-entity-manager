/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.noInherit;

import java.util.List;
import java.util.Objects;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class Contact {
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

    @Type("long")
    public long getId() {
        return id;
    }

    public Contact setId(long id) {
        this.id = id;

        return this;
    }

    @Type("long")
    public long getHostId() {
        return hostId;
    }

    public Contact setHostId(long hostId) {
        this.hostId = hostId;

        return this;
    }

    @Type("String")
    public String getMobile() {
        return mobile;
    }

    public Contact setMobile(String mobile) {
        this.mobile = mobile;

        return this;
    }

    @Type("String")
    public String getTelephone() {
        return telephone;
    }

    public Contact setTelephone(String telephone) {
        this.telephone = telephone;

        return this;
    }

    @Type("String")
    public String getEmail() {
        return email;
    }

    public Contact setEmail(String email) {
        this.email = email;

        return this;
    }

    @Type("String")
    public String getAddress() {
        return address;
    }

    public Contact setAddress(String address) {
        this.address = address;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.noInherit.Email>")
    public List<Email> getEmailList() {
        return emailList;
    }

    public Contact setEmailList(List<Email> emailList) {
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

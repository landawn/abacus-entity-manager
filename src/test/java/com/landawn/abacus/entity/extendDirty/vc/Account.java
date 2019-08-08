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
public class Account extends AbstractDirtyMarker implements ExtendDirtyVCPNL.AccountPNL {

    @Id
    @Column("id")
    private long id;

    @Column("gui")
    private String gui;

    @Column("email_address")
    private String emailAddress;

    @Column("first_name")
    private String firstName;

    @Column("middle_name")
    private String middleName;

    @Column("last_name")
    private String lastName;

    @Column("birth_date")
    private Timestamp birthDate;

    @Column("status")
    private int status;

    @Column("last_update_time")
    private Timestamp lastUpdateTime;

    @Column("create_time")
    private Timestamp createTime;

    private AccountContact contact;

    private List<AccountDevice> devices;

    public Account() {
        super(__);
    }

    public Account(long id) {
        this();

        setId(id);
    }

    public Account(String gui, String emailAddress, String firstName, String middleName, 
        String lastName, Timestamp birthDate, int status, 
        Timestamp lastUpdateTime, Timestamp createTime, AccountContact contact, 
        List<AccountDevice> devices) {
        this();

        setGUI(gui);
        setEmailAddress(emailAddress);
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setBirthDate(birthDate);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setContact(contact);
        setDevices(devices);
    }

    public Account(long id, String gui, String emailAddress, String firstName, 
        String middleName, String lastName, Timestamp birthDate, int status, 
        Timestamp lastUpdateTime, Timestamp createTime, AccountContact contact, 
        List<AccountDevice> devices) {
        this();

        setId(id);
        setGUI(gui);
        setEmailAddress(emailAddress);
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setBirthDate(birthDate);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
        setContact(contact);
        setDevices(devices);
    }

    @XmlTransient
    public boolean isDirty() {
        return super.isDirty()
               || (contact == null ? false : contact.isDirty())
               || (devices == null ? false : super.isEntityDirty(devices));
    }

    public void markDirty(boolean isDirty) {
        super.markDirty(isDirty);

        if (contact != null) {
            contact.markDirty(isDirty);
        }

        if (devices != null) {
            super.markEntityDirty(devices, isDirty);
        }
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public Account setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getGUI() {
        return gui;
    }

    public Account setGUI(String gui) {
        super.setUpdatedPropName(GUI);
        this.gui = gui;

        return this;
    }

    @Type("String")
    public String getEmailAddress() {
        return emailAddress;
    }

    public Account setEmailAddress(String emailAddress) {
        super.setUpdatedPropName(EMAIL_ADDRESS);
        this.emailAddress = emailAddress;

        return this;
    }

    @Type("String")
    public String getFirstName() {
        return firstName;
    }

    public Account setFirstName(String firstName) {
        super.setUpdatedPropName(FIRST_NAME);
        this.firstName = firstName;

        return this;
    }

    @Type("String")
    public String getMiddleName() {
        return middleName;
    }

    public Account setMiddleName(String middleName) {
        super.setUpdatedPropName(MIDDLE_NAME);
        this.middleName = middleName;

        return this;
    }

    @Type("String")
    public String getLastName() {
        return lastName;
    }

    public Account setLastName(String lastName) {
        super.setUpdatedPropName(LAST_NAME);
        this.lastName = lastName;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getBirthDate() {
        return birthDate;
    }

    public Account setBirthDate(Timestamp birthDate) {
        super.setUpdatedPropName(BIRTH_DATE);
        this.birthDate = birthDate;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public Account setStatus(int status) {
        super.setUpdatedPropName(STATUS);
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Account setLastUpdateTime(Timestamp lastUpdateTime) {
        super.setUpdatedPropName(LAST_UPDATE_TIME);
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public Account setCreateTime(Timestamp createTime) {
        super.setUpdatedPropName(CREATE_TIME);
        this.createTime = createTime;

        return this;
    }

    @Type("com.landawn.abacus.entity.extendDirty.vc.AccountContact")
    public AccountContact getContact() {
        return contact;
    }

    public Account setContact(AccountContact contact) {
        super.setUpdatedPropName(CONTACT);
        this.contact = contact;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.extendDirty.vc.AccountDevice>")
    public List<AccountDevice> getDevices() {
        return devices;
    }

    public Account setDevices(List<AccountDevice> devices) {
        super.setUpdatedPropName(DEVICES);
        this.devices = devices;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(gui);
        h = 31 * h + Objects.hashCode(emailAddress);
        h = 31 * h + Objects.hashCode(firstName);
        h = 31 * h + Objects.hashCode(middleName);
        h = 31 * h + Objects.hashCode(lastName);
        h = 31 * h + Objects.hashCode(birthDate);
        h = 31 * h + Objects.hashCode(status);
        h = 31 * h + Objects.hashCode(lastUpdateTime);
        h = 31 * h + Objects.hashCode(createTime);
        h = 31 * h + Objects.hashCode(contact);
        h = 31 * h + Objects.hashCode(devices);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Account) {
            final Account other = (Account) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(gui, other.gui)
                && Objects.equals(emailAddress, other.emailAddress)
                && Objects.equals(firstName, other.firstName)
                && Objects.equals(middleName, other.middleName)
                && Objects.equals(lastName, other.lastName)
                && Objects.equals(birthDate, other.birthDate)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime)
                && Objects.equals(contact, other.contact)
                && Objects.equals(devices, other.devices);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", gui=" + Objects.toString(gui)
                 + ", emailAddress=" + Objects.toString(emailAddress)
                 + ", firstName=" + Objects.toString(firstName)
                 + ", middleName=" + Objects.toString(middleName)
                 + ", lastName=" + Objects.toString(lastName)
                 + ", birthDate=" + Objects.toString(birthDate)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + ", contact=" + Objects.toString(contact)
                 + ", devices=" + Objects.toString(devices)
                 + "}";
    }
}

/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.extendDirty;

import java.util.Objects;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.extendDirty.ExtendDirtyPNL;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class Email extends AbstractDirtyMarker implements ExtendDirtyPNL.EmailPNL {
    private long id;
    private long contactId;
    private String emailAddress;

    public Email() {
        super(__);
    }

    public Email(long id) {
        this();

        setId(id);
    }

    public Email(long contactId, String emailAddress) {
        this();

        setContactId(contactId);
        setEmailAddress(emailAddress);
    }

    public Email(long id, long contactId, String emailAddress) {
        this();

        setId(id);
        setContactId(contactId);
        setEmailAddress(emailAddress);
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public Email setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("long")
    public long getContactId() {
        return contactId;
    }

    public Email setContactId(long contactId) {
        super.setUpdatedPropName(CONTACT_ID);
        this.contactId = contactId;

        return this;
    }

    @Type("String")
    public String getEmailAddress() {
        return emailAddress;
    }

    public Email setEmailAddress(String emailAddress) {
        super.setUpdatedPropName(EMAIL_ADDRESS);
        this.emailAddress = emailAddress;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(contactId);
        h = 31 * h + Objects.hashCode(emailAddress);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Email) {
            final Email other = (Email) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(contactId, other.contactId)
                && Objects.equals(emailAddress, other.emailAddress);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", contactId=" + Objects.toString(contactId)
                 + ", emailAddress=" + Objects.toString(emailAddress)
                 + "}";
    }
}

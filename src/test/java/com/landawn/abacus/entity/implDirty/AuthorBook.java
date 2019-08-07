/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.implDirty;

import java.util.Collection;
import java.util.Set;
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
public class AuthorBook implements ImplDirtyPNL.AuthorBookPNL, DirtyMarker {
    private final DirtyMarkerImpl dirtyMarkerImpl = new DirtyMarkerImpl(__);

    private long authorId;
    private long bookId;

    public AuthorBook() {
    }

    public AuthorBook(long authorId, long bookId) {
        this();

        setAuthorId(authorId);
        setBookId(bookId);
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
    public long getAuthorId() {
        return authorId;
    }

    public AuthorBook setAuthorId(long authorId) {
        dirtyMarkerImpl.setUpdatedPropName(AUTHOR_ID);
        this.authorId = authorId;

        return this;
    }

    @Type("long")
    public long getBookId() {
        return bookId;
    }

    public AuthorBook setBookId(long bookId) {
        dirtyMarkerImpl.setUpdatedPropName(BOOK_ID);
        this.bookId = bookId;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(authorId);
        h = 31 * h + Objects.hashCode(bookId);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AuthorBook) {
            final AuthorBook other = (AuthorBook) obj;

            return Objects.equals(authorId, other.authorId) && Objects.equals(bookId, other.bookId);
        }

        return false;
    }

    public String toString() {
        return "{authorId=" + Objects.toString(authorId) + ", bookId=" + Objects.toString(bookId) + "}";
    }
}

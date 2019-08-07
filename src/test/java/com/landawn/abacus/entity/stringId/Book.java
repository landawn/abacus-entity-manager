/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.stringId;

import java.util.List;
import java.util.Objects;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.stringId.StringIdPNL;
import javax.xml.bind.annotation.XmlTransient;
import com.landawn.abacus.annotation.Type;

/**
 * Generated by Abacus.
 * @version ${version}
 */
public class Book extends AbstractDirtyMarker implements StringIdPNL.BookPNL {
    private long id;
    private String name;
    private String language;
    private List<Author> author;

    public Book() {
        super(__);
    }

    public Book(long id) {
        this();

        setId(id);
    }

    public Book(String name, String language, List<Author> author) {
        this();

        setName(name);
        setLanguage(language);
        setAuthor(author);
    }

    public Book(long id, String name, String language, List<Author> author) {
        this();

        setId(id);
        setName(name);
        setLanguage(language);
        setAuthor(author);
    }

    @XmlTransient
    public boolean isDirty() {
        return super.isDirty() || (author == null ? false : super.isEntityDirty(author));
    }

    public void markDirty(boolean isDirty) {
        super.markDirty(isDirty);

        if (author != null) {
            super.markEntityDirty(author, isDirty);
        }
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public Book setId(long id) {
        super.setUpdatedPropName(ID);
        this.id = id;

        return this;
    }

    @Type("String")
    public String getName() {
        return name;
    }

    public Book setName(String name) {
        super.setUpdatedPropName(NAME);
        this.name = name;

        return this;
    }

    @Type("String")
    public String getLanguage() {
        return language;
    }

    public Book setLanguage(String language) {
        super.setUpdatedPropName(LANGUAGE);
        this.language = language;

        return this;
    }

    @Type("List<com.landawn.abacus.entity.stringId.Author>")
    public List<Author> getAuthor() {
        return author;
    }

    public Book setAuthor(List<Author> author) {
        super.setUpdatedPropName(AUTHOR);
        this.author = author;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(name);
        h = 31 * h + Objects.hashCode(language);
        h = 31 * h + Objects.hashCode(author);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Book) {
            final Book other = (Book) obj;

            return Objects.equals(id, other.id) && Objects.equals(name, other.name) && Objects.equals(language, other.language)
                    && Objects.equals(author, other.author);
        }

        return false;
    }

    public String toString() {
        return "{id=" + Objects.toString(id) + ", name=" + Objects.toString(name) + ", language=" + Objects.toString(language) + ", author="
                + Objects.toString(author) + "}";
    }
}

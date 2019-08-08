/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.stringId;

import java.util.Objects;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.core.AbstractDirtyMarker;
import com.landawn.abacus.entity.stringId.StringIdPNL;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AuthorBook extends AbstractDirtyMarker implements StringIdPNL.AuthorBookPNL {

    @Id
    @Column("authorid")
    private long authorId;

    @Id
    @Column("bookid")
    private long bookId;

    public AuthorBook() {
        super(__);
    }

    public AuthorBook(long authorId, long bookId) {
        this();

        setAuthorId(authorId);
        setBookId(bookId);
    }

    @Type("long")
    public long getAuthorId() {
        return authorId;
    }

    public AuthorBook setAuthorId(long authorId) {
        super.setUpdatedPropName(AUTHOR_ID);
        this.authorId = authorId;

        return this;
    }

    @Type("long")
    public long getBookId() {
        return bookId;
    }

    public AuthorBook setBookId(long bookId) {
        super.setUpdatedPropName(BOOK_ID);
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

            return Objects.equals(authorId, other.authorId)
                && Objects.equals(bookId, other.bookId);
        }

        return false;
    }

    public String toString() {
         return "{authorId=" + Objects.toString(authorId)
                 + ", bookId=" + Objects.toString(bookId)
                 + "}";
    }
}

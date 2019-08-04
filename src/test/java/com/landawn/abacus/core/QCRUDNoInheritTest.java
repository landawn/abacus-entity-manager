/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.NoInheritBaseTest;
import com.landawn.abacus.entity.noInherit.Author;
import com.landawn.abacus.entity.noInherit.Book;
import com.landawn.abacus.util.N;

import java.sql.Timestamp;

import java.util.List;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class QCRUDNoInheritTest extends NoInheritBaseTest {
    public void testCRUD() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);
        Author dbAuthor = em.gett(entityId);
        dbAuthor = em.gett(entityId);

        assertEquals("fn", dbAuthor.getFirstName());
        assertEquals("ln", dbAuthor.getLastName());

        dbAuthor.setFirstName("updatedfn");
        em.update(dbAuthor);

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());

        em.delete(entityId);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testCRUD2() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        Book book = new Book();
        book.setName("name");
        author.setBook(N.asList(book));

        EntityId entityId = em.add(author);
        Author dbAuthor = em.gett(entityId);
        dbAuthor = em.gett(entityId);

        assertEquals("fn", dbAuthor.getFirstName());
        assertEquals("ln", dbAuthor.getLastName());

        List<Book> dbBooks = dbAuthor.getBook();
        assertEquals("name", dbBooks.get(0).getName());

        dbAuthor.setFirstName("updatedfn");
        dbBooks.get(0).setName("updatedName");
        em.update(dbAuthor);

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());

        dbBooks = dbAuthor.getBook();
        assertEquals("updatedName", dbBooks.get(0).getName());

        em.delete(entityId);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testLoadFromCache() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);
        Author dbAuthor = em.gett(entityId);
        dbAuthor = em.gett(entityId);

        assertEquals("fn", dbAuthor.getFirstName());
        assertEquals("ln", dbAuthor.getLastName());

        dbAuthor.setFirstName("updatedfn");

        Author dbAuthor2 = em.gett(entityId);
        assertEquals("fn", dbAuthor2.getFirstName());
        assertEquals("ln", dbAuthor2.getLastName());
        em.update(dbAuthor);

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());
        em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());

        em.delete(entityId);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    private Author createAuthor() {
        Author author = new Author();
        author.setBirthDay(new Timestamp(System.currentTimeMillis()));
        author.setFirstName("fn");
        author.setLastName("ln");

        return author;
    }
}

/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.sql.Timestamp;
import java.util.List;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.ImplDirtyBaseTest;
import com.landawn.abacus.condition.ConditionFactory;
import com.landawn.abacus.entity.implDirty.Author;
import com.landawn.abacus.entity.implDirty.Book;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class QCRUDImplDirtyTest extends ImplDirtyBaseTest {
    public QCRUDImplDirtyTest() {
        this.sqlLog = true;
    }

    public void testCRUD2() {
        EntityManager<Book> em = emf.getEntityManager(domainName);

        Book book1 = new Book();
        book1.setName("testBookName1");

        Book book2 = new Book();
        book2.setName("testBookName2");

        List<Book> books = N.asList(book1, book2);
        em.addAll(books);

        DataSet result = em.query(Book.__, N.asList(Book.ID, Book.NAME), ConditionFactory.startsWith(Book.NAME, "testBookName"), null, null);
        assertEquals(2, result.size());

        em.deleteAll(books);

        result = em.query(Book.__, N.asList(Book.ID, Book.NAME), ConditionFactory.startsWith(Book.NAME, "testBookName"), null, null);
        assertEquals(0, result.size());
    }

    public void testCRUD3() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);
        Author dbAuthor = em.gett(entityId);
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

    public void testCRUD4() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        Book book = new Book();
        book.setName("name");
        author.setBook(N.asList(book));

        EntityId entityId = em.add(author);
        Author dbAuthor = em.gett(entityId);
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

    public void testVersion() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);
        Author dbAuthor = em.gett(entityId);
        long initVersion = dbAuthor.version();
        assertEquals("fn", dbAuthor.getFirstName());
        assertEquals("ln", dbAuthor.getLastName());

        dbAuthor.setFirstName("updatedfn");
        em.update(dbAuthor);

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());
        assertEquals(initVersion + 1, dbAuthor.version());

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

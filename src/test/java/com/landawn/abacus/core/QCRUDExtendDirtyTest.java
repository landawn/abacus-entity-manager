/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import java.util.List;
import java.util.Map;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.ExtendDirtyBaseTest;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.ConditionFactory;
import com.landawn.abacus.condition.ConditionFactory.CF;
import com.landawn.abacus.entity.extendDirty.Author;
import com.landawn.abacus.entity.extendDirty.Book;
import com.landawn.abacus.exception.RecordLockedException;
import com.landawn.abacus.exception.ValidationException;
import com.landawn.abacus.util.Maps;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class QCRUDExtendDirtyTest extends ExtendDirtyBaseTest {
    public QCRUDExtendDirtyTest() {
        this.sqlLog = true;
    }

    //    public void testPerformance() {
    //        Profiler.run(this, "testCRUD1", 2, 10, 1).printResult();
    //    }

    public void testCRUD_get() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setFirstName("<fn>");
        author.setLastName("\"ln\"");

        EntityId entityId = em.add(Author.__, Maps.entity2Map(author), null);

        // entityId.set("a", "a");
        Author dbAuthor = em.gett(entityId);

        assertEquals("<fn>", dbAuthor.getFirstName());
        assertEquals("\"ln\"", dbAuthor.getLastName());

        // dbAuthor = (Author) em.getAll(N.asList(entityId)).get(0);

        dbAuthor.setFirstName("updatedfn");
        em.update(dbAuthor);

        entityId = em.addAll(Author.__, N.asList(Maps.entity2Map(author)), null).get(0);

        List<Author> authors = em.list(Author.__, null, null);
        for (Author e : authors) {
            e.setFirstName("updatedfn");
        }

        em.updateAll(authors);

        em.deleteAll(authors);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testCRUD_get_1() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setFirstName("<fn>");
        author.setLastName("\"ln\"");

        List<EntityId> entityIds = em.addAll(Author.__, N.asList(Maps.entity2Map(author), Maps.entity2Map(author)), null);

        N.println(em.list(Author.__, null, EntityManagerUtil.entityId2Condition(entityIds)));

        em.deleteAll(entityIds);
    }

    public void testCRUD1() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setFirstName("<fn>");
        author.setLastName("\"ln\"");

        EntityId entityId = em.add(author);

        // entityId.set("a", "a");
        Author dbAuthor = em.gett(entityId);

        assertEquals("<fn>", dbAuthor.getFirstName());
        assertEquals("\"ln\"", dbAuthor.getLastName());

        dbAuthor.setFirstName("updatedfn");
        em.update(dbAuthor);

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());

        em.delete(entityId);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testCRUD2() {
        EntityManager<Book> em = emf.getEntityManager(domainName);

        Book book1 = new Book();
        book1.setName("testBookName1");

        Book book2 = new Book();
        book2.setName("testBookName2");

        List<Book> books = N.asList(book1, book2);
        em.addAll(books);

        DataSet result = em.query(Book.__, N.asList(Book.ID, Book.NAME), CF.startsWith(Book.NAME, "testBookName"), null, null);
        assertEquals(2, result.size());

        em.deleteAll(books);

        result = em.query(Book.__, N.asList(Book.ID, Book.NAME), CF.startsWith(Book.NAME, "testBookName"), null, null);
        assertEquals(0, result.size());
    }

    public void testCRUD5() {
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

    public void testCRUD6() {
        EntityManager<Book> em = emf.getEntityManager(domainName);

        Book book1 = new Book();
        book1.setName("testBookName1");

        Book book2 = new Book();
        book2.setName("testBookName2");

        List<Book> books = N.asList(book1, book2);
        em.addAll(books);

        books = em.list(Book.__, null, CF.criteria().where(CF.startsWith(Book.NAME, "testBookName")).orderBy(Book.NAME), null);

        em.update(Book.__, N.asProps(Book.NAME, "testBookName1Update"), CF.eq(Book.NAME, "testBookName1"), null);

        List<Book> books2 = em.list(Book.__, null, CF.criteria().where(CF.startsWith(Book.NAME, "testBookName")).orderBy(Book.NAME), null);

        N.println(books);
        N.println(books2);
        assertEquals(books.get(0).version() + 1, books2.get(0).version());
        assertEquals(books.get(1).version(), books2.get(1).version());

        em.deleteAll(books);

        DataSet result = em.query(Book.__, N.asList(Book.ID, Book.NAME), ConditionFactory.startsWith(Book.NAME, "testBookName"), null, null);
        assertEquals(0, result.size());
    }

    public void testCRUD8() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);

        // entityId.set("a", "a");
        Author dbAuthor = em.gett(entityId);

        assertEquals("fn", dbAuthor.getFirstName());
        assertEquals("ln", dbAuthor.getLastName());

        dbAuthor.setFirstName("updatedfn");
        dbAuthor.setId(dbAuthor.getId());
        em.updateAll(N.asList(dbAuthor));

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());

        em.delete(entityId);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testCRUD9() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setId(1000);

        Author author2 = createAuthor();

        Author[] authors = new Author[] { author, author2 };

        try {
            em.add(authors);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    //
    //    public void testCRUD10() {
    //        EntityManager<Object> em = emf.getEntityManager(domainName);
    //
    //        Author author = createAuthor();
    //        author.setName(name);
    //        author.setId(1000);
    //
    //        Author author2 = new Author();
    //        author2.setName(name);
    //
    //        Author[] authors = new Author[] { author, author2 };
    //        List<EntityId> entityIds = em.add(authors);
    //
    //        em.find(Book.__, null, ConditionFactory.eq(Book.LANGUAGE, null), null);
    //
    //        em.find(Book.__, null, ConditionFactory.eq(Book.ID, null), null);
    //
    //        em.delete(entityIds);
    //    }
    public void testEntityIdCacheInfo() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        EntityId entityId = Seid.of(Author.__);
        entityId.set(Author.ID, 2L);

        Author author = em.gett(entityId);
        N.println(author);
        author = em.gett(entityId);
        N.println(author);
    }

    public void testRefresh() {
        EntityManagerEx<Book> em = emf.getEntityManager(domainName);
        Book book = new Book();
        book.setName("test");

        EntityId entityId = em.add(book);

        book = em.gett(entityId);
        assertEquals("test", book.getName());

        em.update(entityId, N.asProps(Book.NAME, "updatedName"));
        em.refresh(book);
        assertEquals("updatedName", book.getName());

        em.delete(entityId);
    }

    public void testLockRecord() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);

        String lockCode = em.lockRecord(entityId, LockMode.RU, null);

        Author dbAuthor = null;

        try {
            dbAuthor = em.gett(entityId, null, N.asProps(Options.RECORD_LOCK_TIMEOUT, 3000));
            fail("Should throw: " + RecordLockedException.class.getName() + ". ");
        } catch (Exception e) {
            if (!e.getClass().getName().equals(RecordLockedException.class.getName())) {
                fail("Should throw: " + RecordLockedException.class.getName() + ". but throw: " + e.getClass().getName() + ". ");
            }
        }

        dbAuthor = em.gett(entityId, null, N.asProps(Options.RECORD_LOCK_CODE, lockCode));

        assertEquals("fn", dbAuthor.getFirstName());
        assertEquals("ln", dbAuthor.getLastName());

        dbAuthor.setFirstName("updatedfn");

        try {
            em.update(dbAuthor, N.asProps(Options.RECORD_LOCK_TIMEOUT, 10));
            fail("Should throw: " + RecordLockedException.class.getName() + ". ");
        } catch (Exception e) {
            if (!e.getClass().getName().equals(RecordLockedException.class.getName())) {
                fail("Should throw: " + RecordLockedException.class.getName() + ". but throw: " + e.getClass().getName() + ". ");
            }
        }

        em.update(dbAuthor, N.asProps(Options.RECORD_LOCK_CODE, lockCode));

        em.unlockRecord(entityId, lockCode, null);

        dbAuthor = em.gett(entityId);
        assertEquals("updatedfn", dbAuthor.getFirstName());

        em.delete(entityId);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testCommitTransaction() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        String transactionId = em.beginTransaction(IsolationLevel.READ_COMMITTED, null);
        Author author = createAuthor();

        Map<String, Object> options = N.asProps(Options.TRANSACTION_ID, transactionId);
        EntityId entityId = em.add(author, options);
        Author dbAuthor = em.gett(entityId, null, options);
        em.endTransaction(transactionId, Action.COMMIT, null);
        dbAuthor = em.gett(entityId);
        assertEquals("fn", dbAuthor.getFirstName());

        em.delete(dbAuthor);
    }

    public void testRollbackTransaction() {
        EntityManager<Author> em = emf.getEntityManager(domainName);
        String transactionId = em.beginTransaction(IsolationLevel.READ_COMMITTED, null);
        Author author = createAuthor();

        EntityId entityId = em.add(author, N.asProps(Options.TRANSACTION_ID, transactionId));
        Author dbAuthor = em.gett(entityId, null, N.asProps(Options.TRANSACTION_ID, transactionId));
        em.endTransaction(transactionId, Action.ROLLBACK, null);
        dbAuthor = em.gett(entityId);
        assertEquals(null, dbAuthor);
    }

    public void testCallBack() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();

        EntityId entityId = em.add(author);

        // entityId.set("a", "a");
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

    public void testNotNullValidator() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setFirstName(null);
        author.setLastName(null);

        try {
            em.add(author);
            fail("should throw ViolatedDataException. ");
        } catch (ValidationException e) {
        }
    }

    public void testLengthValidator() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setFirstName("sd");
        author.setLastName("sad411212465452312345612125615fasfasf1");

        try {
            em.add(author);
            fail("should throw ViolatedDataException. ");
        } catch (ValidationException e) {
        }
    }

    public void testDefault() {
        EntityManager<Object> em = emf.getEntityManager(domainName);
        Author author = createAuthor();
        author.setFirstName("sd");
        author.setLastName("dafdads");

        EntityId entityId = em.add(author);
        author = em.gett(entityId);
        assertNotNull(author.getBirthDay());
        N.println(author);
    }

    public void testFrozen() {
        try {
            Book book = new Book();
            book.setName("testBookName");
            book.freeze();
            book.setName("updatedBookName");
            fail("IllegalStateException should be throwed");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        EntityManager<Book> em = emf.getEntityManager(domainName);
        List<Book> books = em.list(Book.__, null, ConditionFactory.greaterEqual(Book.ID, 0), null);
        books.get(0).freeze();

        try {
            books.get(0).setId(455);
            fail("IllegalStateException should be throwed");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void testTransaction() {
        EntityManager<Book> em = emf.getEntityManager(domainName);
        String transactionId = em.beginTransaction(IsolationLevel.DEFAULT, null);

        Book book = new Book();
        book.setName("testBookName");

        Map<String, Object> options = N.asProps(Options.TRANSACTION_ID, transactionId);
        em.add(book, options);

        Book dbBook = em.gett(Seid.of(Book.ID, book.getId()), null, options);

        println(dbBook);
        assertNotNull(dbBook);
        em.endTransaction(transactionId, Action.ROLLBACK, null);
        dbBook = em.gett(Seid.of(Book.ID, book.getId()));

        println(dbBook);
        assertNull(dbBook);
    }

    private Author createAuthor() {
        Author author = new Author();
        author.setFirstName("fn");
        author.setLastName("ln");

        return author;
    }
}

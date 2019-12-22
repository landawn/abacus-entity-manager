/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.landawn.abacus.entity.extendDirty.Author;
import com.landawn.abacus.entity.extendDirty.AuthorBook;
import com.landawn.abacus.entity.extendDirty.Book;
import com.landawn.abacus.entity.extendDirty.Contact;
import com.landawn.abacus.entity.extendDirty.Email;
import com.landawn.abacus.entity.extendDirty.basic.DataType;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractEntityManager0Test extends AbstractTest {
    public static final String databaseName = "abacustest";
    protected static final String srcPath = "./src/test/java/";
    protected static boolean isMySQL;
    protected static boolean isSQLServer;
    protected static com.landawn.abacus.core.EntityManagerFactory emf = com.landawn.abacus.core.EntityManagerFactory
            .getInstance("./src/test/resources/config/abacus-entity-manager.xml");
    protected static EntityManager<Object> entityManager;
    protected boolean sqlLog = false;

    protected abstract String getDomainName();

    protected void prepareTest(boolean... reinit) throws RuntimeException {
        if (entityManager == null) {
            entityManager = emf.getEntityManager(getDomainName());
            DataSource ds = emf.getDataSourceManager(getDomainName()).getPrimaryDataSource();

            try (Connection conn = ds.getConnection()) {
                if (conn.getMetaData().getDatabaseProductName().contains("MySQL")) {
                    isMySQL = true;
                }

                if (conn.getMetaData().getDatabaseProductName().contains("SQL Server")) {
                    isSQLServer = true;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected void sleep(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void importHugeData(int number) throws RuntimeException {
        long startTime = System.currentTimeMillis();
        int batchNum = 100;

        String sql = "insert into author(id, firstName, lastName) values (#{id}, #{firstName}, #{lastName})";

        for (int j = 0; j < (number / batchNum); j++) {
            long id = 0;

            List<Map<String, Object>> parameters = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < batchNum; i++) {
                id = (j * batchNum) + i + 100;

                Map<String, Object> props = new HashMap<String, Object>();
                props.put("id", id);
                props.put("firstName", "firstName" + id);
                props.put("lastName", "lastName" + id);
                parameters.add(props);
            }

            try (Connection conn = emf.getDataSourceManager(getDomainName()).getPrimaryDataSource().getConnection()) {
                JdbcUtil.executeBatchUpdate(conn, sql, parameters);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        N.println("Take " + (System.currentTimeMillis() - startTime) + " to import " + number + " record. ");
    }

    @Override
    protected void setUp() {
        prepareTest();

        emf.getEntityManager(getDomainName()).delete(Author.__, null, null);
        emf.getEntityManager(getDomainName()).delete(Book.__, null, null);
        emf.getEntityManager(getDomainName()).delete(AuthorBook.__, null, null);
        emf.getEntityManager(getDomainName()).delete(Contact.__, null, null);
        emf.getEntityManager(getDomainName()).delete(Email.__, null, null);
        emf.getEntityManager(getDomainName()).delete(DataType.__, null, null);

        if (isSQLServer) {
            try (Connection conn = emf.getDataSourceManager(getDomainName()).getPrimaryDataSource().getConnection()) {
                JdbcUtil.executeUpdate(conn, "SET IDENTITY_INSERT book ON;" + "INSERT INTO book (id, name ) VALUES ( 1, 'book1');" + ""
                        + "INSERT INTO book (id, name ) VALUES ( 2, 'book2');" + "INSERT INTO book (id, name ) VALUES ( 3, 'book3');"
                        + "INSERT INTO book (id, name ) VALUES ( 4, 'book4');" + "INSERT INTO book (id, name ) VALUES ( 5, 'book5');"
                        + "INSERT INTO book (id, name ) VALUES ( 6, 'book6');" + "INSERT INTO book (id, name ) VALUES ( 7, 'book7');"
                        + "SET IDENTITY_INSERT book OFF;" + "SET IDENTITY_INSERT author ON;"
                        + "INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 1, 'author1', 'num1', CONVERT(DATETIME, '2001-01-01', 101));"
                        + "INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 2, 'author2', 'num2', CONVERT(DATETIME, '2002-01-01', 101));"
                        + "INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 3, 'author3', 'num3', CONVERT(DATETIME, '2003-01-01', 101));"
                        + "INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 4, 'author4', 'num4', CONVERT(DATETIME, '2004-01-01', 101));"
                        + "INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 5, 'author5', 'num5', CONVERT(DATETIME, '2005-01-01', 101));"
                        + "SET IDENTITY_INSERT author OFF;" + "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 1 );"
                        + "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 2 );" + "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 3 );"
                        + "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 2, 2 );" + "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 2, 3 );"
                        + "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 3, 3 );" + "SET IDENTITY_INSERT contact ON;"
                        + "INSERT INTO contact (id, hostId, email) VALUES ( 1, 1, 'author1.num1@world.com' );"
                        + "INSERT INTO contact (id, hostId, email) VALUES ( 2, 2 , 'author2.num2@world.com');"
                        + "INSERT INTO contact (id, hostId, email) VALUES ( 3, 3 , 'author3.num4@world.com');"
                        + "INSERT INTO contact (id, hostId, email) VALUES ( 4, 4 , 'author4.num4@world.com');"
                        + "INSERT INTO contact (id, hostId, email) VALUES ( 5, 5 , 'author5.num5@world.com');" + "SET IDENTITY_INSERT contact OFF;");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            Book book = new Book();
            book.setId(1);
            book.setName("book1");
            entityManager.add(book);

            book = new Book();
            book.setId(2);
            book.setName("book2");
            entityManager.add(book);

            book = new Book();
            book.setId(3);
            book.setName("book3");
            entityManager.add(book);

            book = new Book();
            book.setId(4);
            book.setName("book4");
            entityManager.add(book);

            book = new Book();
            book.setId(5);
            book.setName("book5");
            entityManager.add(book);

            book = new Book();
            book.setId(6);
            book.setName("book6");
            entityManager.add(book);

            book = new Book();
            book.setId(7);
            book.setName("book7");
            entityManager.add(book);

            //
            //            String sql  = "INSERT INTO book (id, name ) VALUES ( 1, 'book1' ),  ( 2, 'book2' ),  ( 3, 'book3' ),  ( 4, 'book4' ),  ( 5, 'book5' ), (6, 'book6'), (7, 'book7')";
            //            stmt.execute(sql);
            //            
            Author author = new Author();
            author.setId(1);
            author.setFirstName("author1");
            author.setLastName("num1");
            author.setBirthDay(new Timestamp(Date.valueOf("2001-01-01").getTime()));
            entityManager.add(author);

            author = new Author();
            author.setId(2);
            author.setFirstName("author2");
            author.setLastName("num2");
            author.setBirthDay(new Timestamp(Date.valueOf("2002-01-01").getTime()));
            entityManager.add(author);

            author = new Author();
            author.setId(3);
            author.setFirstName("author3");
            author.setLastName("num3");
            author.setBirthDay(new Timestamp(Date.valueOf("2003-01-01").getTime()));
            entityManager.add(author);

            author = new Author();
            author.setId(4);
            author.setFirstName("author4");
            author.setLastName("num4");
            author.setBirthDay(new Timestamp(Date.valueOf("2004-01-01").getTime()));
            entityManager.add(author);

            author = new Author();
            author.setId(5);
            author.setFirstName("author5");
            author.setLastName("num5");
            author.setBirthDay(new Timestamp(Date.valueOf("2005-01-01").getTime()));
            entityManager.add(author);

            //            sql = "INSERT INTO author (id, firstName , lastName,birthDay) VALUES ( 1, 'author1' , 'num1', '2001-01-01'), ( 2, 'author2', 'num2', '2002-01-01' ), ( 3, 'author3' , 'num3', '2003-01-01'), ( 4, 'author4', 'num4' , '2004-01-01'), ( 5, 'author5' , 'num5', '2005-01-01')";
            //            stmt.execute(sql);
            AuthorBook ab = new AuthorBook(1, 1);
            entityManager.add(ab);

            ab = new AuthorBook(1, 2);
            entityManager.add(ab);

            ab = new AuthorBook(1, 3);
            entityManager.add(ab);

            ab = new AuthorBook(2, 2);
            entityManager.add(ab);

            ab = new AuthorBook(2, 3);
            entityManager.add(ab);

            ab = new AuthorBook(3, 3);
            entityManager.add(ab);

            //            sql = "INSERT INTO AuthorBook (bookId, authorId ) VALUES  ( 1, 1 ), ( 1, 2 ), ( 1, 3 ), ( 2, 2 ), ( 2, 3 ), ( 3, 3 )";
            //            stmt.execute(sql);
            Contact contact = new Contact();
            contact.setId(1);
            contact.setHostId(1);
            contact.setEmail("author1.num1@world.com");
            entityManager.add(contact);

            contact = new Contact();
            contact.setId(2);
            contact.setHostId(2);
            contact.setEmail("author2.num2@world.com");
            entityManager.add(contact);

            contact = new Contact();
            contact.setId(3);
            contact.setHostId(3);
            contact.setEmail("author3.num3@world.com");
            entityManager.add(contact);

            contact = new Contact();
            contact.setId(4);
            contact.setHostId(4);
            contact.setEmail("author4.num4@world.com");
            entityManager.add(contact);

            contact = new Contact();
            contact.setId(5);
            contact.setHostId(5);
            contact.setEmail("author5.num5@world.com");
            entityManager.add(contact);
        }
    }

    @Override
    protected void tearDown() {
    }
}

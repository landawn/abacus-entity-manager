package com.landawn.abacus;

import java.io.File;
import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Test;

import com.landawn.abacus.core.EntityManagerFactory;
import com.landawn.abacus.core.Seid;
import com.landawn.abacus.entity.Account;
import com.landawn.abacus.entity.CodesPNL;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.metadata.sql.SQLDatabase;
import com.landawn.abacus.util.CodeGenerator2;
import com.landawn.abacus.util.CodeGenerator2.EntityMode;
import com.landawn.abacus.util.EntityManagerEx;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.SQLExecutor;

import junit.framework.TestCase;

/**
 * 
 * #1, create db/table.
 * #2, generate entity definition xml by CodeGenerator2.database2EntityDefinitionXml.
 * #3, generate entity classes by entity definition xml.
 * #4,  
 * 
 */
public class HelloCRUD extends TestCase {
    static final EntityManagerFactory emf = EntityManagerFactory.getInstance("./samples/config/abacus-entity-manager.xml");
    static final DBAccess dbAccess = emf.getDBAccess(CodesPNL._DN);
    static final EntityManagerEx<Object> em = emf.getEntityManager(CodesPNL._DN);
    static final DataSource ds;

    static {
        SQLExecutor sqlExecutor = emf.getSQLExecutor(CodesPNL._DN);
        final String sql_user_drop_table = "DROP TABLE IF EXISTS account";

        final String sql_user_creat_table = "CREATE TABLE IF NOT EXISTS account (" //
                + "id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY, " //
                + "first_name varchar(32) NOT NULL, " //
                + "last_name varchar(32) NOT NULL, " //
                + "email_address varchar(64), " //
                + "create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP)";

        sqlExecutor.execute(sql_user_drop_table);
        sqlExecutor.execute(sql_user_creat_table);

        ds = sqlExecutor.dataSource();
    }

    @Test
    public void test_CRUD() {
        Account account = em.gett(Seid.of(Account.ID, 1), N.asList("id"));
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setLastName("lastName...................");
        account.setEmailAddress("abc@email.com");
        em.add(account);

        try {
            em.add(account);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        account = em.gett(Seid.of(Account.ID, account.getId()));

        account = em.gett(Seid.of(Account.ID, account.getId()), N.asList(Account.FIRST_NAME, Account.LAST_NAME));
        N.println(account);
        em.delete(account);
        assertNull(em.gett(Seid.of(Account.ID, account.getId())));
    }

    @Test
    public void test_generateCode() throws Exception {
        SQLDatabase database = null;

        try (Connection conn = ds.getConnection()) {
            database = new SQLDatabase(conn, "test", N.asList("account"));
        }

        File entityDefinitionFile = new File("./samples/config/codes.xml");

        CodeGenerator2.database2EntityDefinitionXml(database, entityDefinitionFile);

        String domainName = "codes";
        String srcPath = "./samples/";

        CodeGenerator2.entityDefinitionXml2Class(domainName, entityDefinitionFile, srcPath, EntityMode.EXTEND_DIRTY_MARKER, null, null, true, null);

        // CodeGenerator2.entityDefinitionXml2ColumnNameTable(domainName, entityDefinitionFile, srcPath, null, N.class);
    }
}

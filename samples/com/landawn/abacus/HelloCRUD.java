package com.landawn.abacus;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;

import com.landawn.abacus.core.EntityManagerEx;
import com.landawn.abacus.core.EntityManagerFactory;
import com.landawn.abacus.core.NewEntityManager;
import com.landawn.abacus.core.NewEntityManager.Mapper;
import com.landawn.abacus.core.SQLTransaction;
import com.landawn.abacus.entity.Account;
import com.landawn.abacus.entity.CodesPNL;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.util.CodeGenerator2;
import com.landawn.abacus.util.CodeGenerator2.EntityMode;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Profiler;

import junit.framework.TestCase;

/**
 * 
 * #1, create db/table.
 * #2, generate entity definition xml by CodeGenerator2.database2EntityDefinitionXml.
 * #3, generate entity classes by entity definition xml.
 * #4, define abacus-entity-manager.xml, Refer to ./schema/abacus-entity-manager.xsd.
 * #5, create instance of EntityManagerFactory/EntityManagerEx by abacus-entity-manager.xml.
 * 
 */
public class HelloCRUD extends TestCase {
    static final EntityManagerFactory emf = EntityManagerFactory.getInstance("./samples/config/abacus-entity-manager.xml");
    static final DBAccess dbAccess = emf.getDBAccess(CodesPNL._DN);
    @SuppressWarnings("deprecation")
    static final EntityManagerEx<Object> em = emf.getEntityManager(CodesPNL._DN);
    static final NewEntityManager nem = emf.getNewEntityManager(CodesPNL._DN);
    static final Mapper<Account, Long> accountMapper = emf.getNewEntityManager(CodesPNL._DN).mapper(Account.class, long.class);
    static final DataSource ds;

    static {
        ds = emf.getDataSourceManager(CodesPNL._DN).getPrimaryDataSource();

        try (Connection conn = ds.getConnection()) {
            final String sql_user_drop_table = "DROP TABLE IF EXISTS account";

            final String sql_user_creat_table = "CREATE TABLE IF NOT EXISTS account (" //
                    + "id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY, " //
                    + "first_name varchar(32) NOT NULL, " //
                    + "last_name varchar(32) NOT NULL, " //
                    + "email_address varchar(64), " //
                    + "create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP)";

            JdbcUtil.execute(conn, sql_user_drop_table);
            JdbcUtil.execute(conn, sql_user_creat_table);
        } catch (SQLException e) {
            // ignore.
            e.printStackTrace();
        }
    }

    @Test
    public void test_crud_by_entity_manager() {
        Account account = em.gett(EntityId.of(Account.ID, 1));
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setLastName("lastName...................");
        account.setEmailAddress("abc@email.com");
        long id = em.add(account).get(Account.ID);

        try {
            em.add(account);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        account = em.gett(EntityId.of(Account.ID, id));

        account = em.gett(EntityId.of(Account.ID, id), N.asList(Account.FIRST_NAME, Account.LAST_NAME));
        N.println(account);
        em.delete(account);
        assertNull(em.gett(EntityId.of(Account.ID, id)));
    }

    @Test
    public void test_crud_by_new_entity_manager() {
        Account account = nem.gett(Account.class, 1);
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setLastName("lastName...................");
        account.setEmailAddress("abc@email.com");
        long id = nem.add(account).get(Account.ID);

        try {
            nem.add(account);
            fail("should throw AbacusSQLException");
        } catch (UncheckedSQLException e) {
        }

        account = nem.gett(Account.class, id);

        account = nem.gett(Account.class, id, N.asList(Account.FIRST_NAME, Account.LAST_NAME));
        N.println(account);
        nem.delete(account);
        assertNull(nem.gett(Account.class, id));
    }

    @Test
    public void test_crud_by_mapper() {
        Account account = accountMapper.gett(1L);
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setLastName("lastName...................");
        account.setEmailAddress("abc@email.com");
        long id = accountMapper.add(account);

        account = accountMapper.gett(id);

        Profiler.run(1, 10000, 3, "load from cache", () -> accountMapper.gett(id)).printResult();

        account = accountMapper.gett(id, N.asList(Account.FIRST_NAME, Account.LAST_NAME));
        N.println(account);
        accountMapper.delete(account);
        assertNull(accountMapper.gett(id));
    }

    @Test
    public void test_transaction() {
        Account account = nem.gett(Account.class, 1);
        N.println(account);
        account = new Account();
        account.setFirstName("firstName...................");
        account.setLastName("lastName...................");
        account.setEmailAddress("abc@email.com");

        long id = 0;
        SQLTransaction tran = nem.beginTransaction(IsolationLevel.DEFAULT);

        try {
            id = nem.add(account).get(Account.ID);
        } finally {
            tran.rollbackIfNotCommitted();
        }

        assertNull(nem.gett(Account.class, id));

        tran = nem.beginTransaction(IsolationLevel.DEFAULT);

        try {
            id = nem.add(account).get(Account.ID);
            tran.commit();
        } finally {
            tran.rollbackIfNotCommitted();
        }

        assertNotNull(account = nem.gett(Account.class, id));

        account = nem.gett(Account.class, id, N.asList(Account.FIRST_NAME, Account.LAST_NAME));
        N.println(account);
        nem.delete(account);
        assertNull(nem.gett(Account.class, id));
    }

    @Test
    public void test_generateCode() throws Exception {
        File entityDefinitionFile = new File("./samples/config/codes.xml");

        //        SQLDatabase database = null;
        //
        //        try (Connection conn = ds.getConnection()) {
        //            database = new SQLDatabase(conn, "test", N.asList("account"));
        //        }
        //
        //
        //        CodeGenerator2.database2EntityDefinitionXml(database, entityDefinitionFile);

        String domainName = "codes";
        String srcPath = "./samples/";

        CodeGenerator2.entityDefinitionXml2Class(domainName, entityDefinitionFile, srcPath, EntityMode.EXTEND_DIRTY_MARKER, null, null, true, null);

        // CodeGenerator2.entityDefinitionXml2ColumnNameTable(domainName, entityDefinitionFile, srcPath, null, N.class);
    }
}

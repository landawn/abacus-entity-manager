/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.landawn.abacus.core.EntityManagerFactory;
import com.landawn.abacus.core.RowDataSet;
import com.landawn.abacus.entity.extendDirty.lvc.Account;
import com.landawn.abacus.entity.extendDirty.lvc.AccountContact;
import com.landawn.abacus.entity.extendDirty.lvc.AclGroup;
import com.landawn.abacus.entity.extendDirty.lvc.AclUser;
import com.landawn.abacus.entity.extendDirty.lvc.AclUserGroupRelationship;
import com.landawn.abacus.entity.extendDirty.lvc.ExtendDirtyLVCPNL;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.util.AsyncSQLExecutor;
import com.landawn.abacus.util.EntityManagerEx;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.SQLExecutor;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractAbacusTest extends AbstractTest {
    protected static final EntityManagerFactory emf = EntityManagerFactory.getInstance();
    protected final String domainName = getDomainName();
    protected final DBAccess dbAccess = emf.getDBAccess(domainName);
    protected final EntityManagerEx<Object> em = emf.getEntityManager(domainName);
    protected final SQLExecutor sqlExecutor = emf.getSQLExecutor(domainName);
    protected final AsyncSQLExecutor asyncSQLExecutor = sqlExecutor.async();
    protected final DataSourceManager dsm = emf.getDataSourceManager(domainName);
    protected final EntityDefinitionFactory entityDefFactory = em.getEntityDefinitionFactory();
    protected final String dbProductName;
    protected final String dbProductVersion;
    protected final boolean isMysql;

    {
        Connection conn = dsm.getPrimaryDataSource().getConnection();

        try {
            dbProductName = conn.getMetaData().getDatabaseProductName();
            dbProductVersion = conn.getMetaData().getDatabaseProductVersion();
            isMysql = dbProductName.toUpperCase().indexOf("MYSQL") >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeQuietly(conn);
        }
    }

    protected String getDomainName() {
        return ExtendDirtyLVCPNL._DN;
    }

    public <T> T addAccount(Class<T> cls) {
        T account = createAccount(cls);
        em.add(account);

        return account;
    }

    public <T> List<T> addAccount(Class<T> cls, int size) {
        if (size == 0) {
            return new ArrayList<>();
        }

        List<T> accounts = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            T account = AbstractAbacusTest.createAccount(cls, FIRST_NAME + i, LAST_NAME + i);
            accounts.add(account);
        }

        em.addAll(accounts, N.asProps(Options.BATCH_SIZE, 3000));

        return accounts;
    }

    public <T> T addAccountWithContact(Class<T> cls) {
        T account = createAccountWithContact(cls);

        em.add(account);

        return account;
    }

    public <T> List<T> addAccountWithContact(Class<T> cls, int size) {
        List<T> accounts = createAccountWithContact(cls, size);

        em.addAll(accounts);

        return accounts;
    }

    public <T> T addAclUser(Class<T> cls) {
        T aclUser = createAclUser(cls);

        em.add(aclUser);

        return N.copy(cls, aclUser);
    }

    public <T> List<T> addAclUser(Class<T> cls, int size) {
        List<T> aclUsers = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            aclUsers.add(createAclUser(cls, ACL_USER_NAME + i));
        }

        em.addAll(aclUsers);

        return aclUsers;
    }

    public <T> T addAclUserWithAclGroup(Class<T> cls) {
        T aclUser = createAclUserWithAclGroup(cls);

        em.add(aclUser);

        return aclUser;
    }

    public <T> List<T> addAclUserWithAclGroup(Class<T> cls, int size) {
        List<T> aclUsers = createAclUserWithAclGroup(cls, size);

        em.addAll(aclUsers);

        return aclUsers;
    }

    protected Collection<String> getCachedPropNames(DataSet resultSet) {
        Collection<String> cachedPropNames = resultSet == null ? null : (Collection<String>) resultSet.properties().get(RowDataSet.CACHED_PROP_NAMES);

        return cachedPropNames == null ? N.<String> emptyList() : cachedPropNames;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        em.delete(AccountContact.__, null, null);
        em.delete(Account.__, null, null);
        em.delete(AclUserGroupRelationship.__, null, null);
        em.delete(AclUser.__, null, null);
        em.delete(AclGroup.__, null, null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        em.delete(AccountContact.__, null, null);
        em.delete(Account.__, null, null);
        em.delete(AclUserGroupRelationship.__, null, null);
        em.delete(AclUser.__, null, null);
        em.delete(AclGroup.__, null, null);
    }
}

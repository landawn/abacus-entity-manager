/*
 * Copyright (c) 2015, Haiyang Li.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.core;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.landawn.abacus.DBAccess;
import com.landawn.abacus.DataSourceManager;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration.CustomizedEntityCacheConfiguration;
import com.landawn.abacus.dataSource.SQLDataSourceManager;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.metadata.sql.SQLEntityDefinitionFactory;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.ObjectPool;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating EntityManager objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class EntityManagerFactory {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerFactory.class);

    private static final String GET_INSTANCE = "getInstance";

    private static final Map<String, EntityManagerFactory> INTANCE_POOL = new LinkedHashMap<>();

    private static volatile EntityManagerFactory DEFAULT_INSTANCE = null;

    private final Map<String, DomainEntityManager> entityManagerPool = new ObjectPool<>(64);

    private final AbacusConfiguration abacusConfig;

    EntityManagerFactory(AbacusConfiguration abacusConfig) {
        this.abacusConfig = abacusConfig;

        String initializerOnStartup = abacusConfig.getInitializerOnStartup();

        if (N.notNullOrEmpty(initializerOnStartup)) {
            ((EntityManagerInitializer) N.newInstance(ClassUtil.forClass(initializerOnStartup))).initialize();
        }

        for (EntityManagerConfiguration entityManagerConfig : abacusConfig.getEntityManagerConfigurationList()) {
            try {
                DomainEntityManager domainEntityManager = new DomainEntityManager(entityManagerConfig);

                entityManagerPool.put(domainEntityManager.getDomainName(), domainEntityManager);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to initialize doamin with file entity definition file: " + entityManagerConfig.getEntityDefinitionFile(), e);
                }
            }
        }
    }

    /**
     * Gets the single instance of EntityManagerFactory.
     *
     * @return single instance of EntityManagerFactory
     */
    public synchronized static EntityManagerFactory getInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = getInstance(AbacusConfiguration.ABACUS_ENTITY_MANAGER_FILE_NAME);
        }

        return DEFAULT_INSTANCE;
    }

    /**
     * Gets the single instance of EntityManagerFactory.
     *
     * @param file
     * @return single instance of EntityManagerFactory
     */
    public synchronized static EntityManagerFactory getInstance(String file) {
        return getInstance(file, false);
    }

    /**
     * Gets the single instance of EntityManagerFactory.
     *
     * @param file
     * @param ignoreFactory
     * @return single instance of EntityManagerFactory
     */
    synchronized static EntityManagerFactory getInstance(String file, boolean ignoreFactory) {
        final long startTime = System.currentTimeMillis();

        File abacusFile = findFile(file);
        String key = abacusFile.getAbsolutePath();
        EntityManagerFactory instance = INTANCE_POOL.get(key);

        if (instance == null) {
            AbacusConfiguration abacusConfig = new AbacusConfiguration(abacusFile);
            String factory = abacusConfig.getFactory();
            Class<?> factoryClass = (N.isNullOrEmpty(factory) || ignoreFactory) ? null : ClassUtil.forClass(factory);

            if ((factoryClass == null) || factoryClass.equals(EntityManagerFactory.class)) {
                instance = new EntityManagerFactory(abacusConfig);
            } else {
                instance = ClassUtil.invokeMethod(ClassUtil.getDeclaredMethod(factoryClass, GET_INSTANCE, File.class), abacusFile);
            }

            INTANCE_POOL.put(key, instance);
        }

        if (logger.isWarnEnabled()) {
            logger.warn("============== It took " + (System.currentTimeMillis() - startTime) + " milliseconds to initialize Abacus with file: " + key);
        }

        return instance;
    }

    /**
     * Gets the entity manager configuration.
     *
     * @param domainName
     * @return
     */
    public EntityManagerConfiguration getEntityManagerConfiguration(String domainName) {
        return getDmainManager(domainName).getEntityManagerConfiguration();
    }

    /**
     * Gets the data source manager.
     *
     * @param domainName
     * @return
     */
    public DataSourceManager getDataSourceManager(String domainName) {
        return getDmainManager(domainName).getDataSourceManager();
    }

    /**
     * Gets the domain names.
     *
     * @return
     */
    public Collection<String> getDomainNames() {
        synchronized (entityManagerPool) {
            return entityManagerPool.keySet();
        }
    }

    /**
     * Gets the DB access.
     *
     * @param domainName
     * @return
     */
    public DBAccess getDBAccess(String domainName) {
        return getDmainManager(domainName).getDBAccess();
    }

    /**
     * Gets the entity manager.
     *
     * @param <T>
     * @param domainName
     * @return
     * @deprecated replaced by {@link #getNewEntityManager(String)}
     */
    @Deprecated
    public <T> EntityManagerEx<T> getEntityManager(String domainName) {
        return getDmainManager(domainName).getEntityManager();
    }

    /**
     * Gets the entity manager.
     *
     * @param domainName
     * @return
     */
    public NewEntityManager getNewEntityManager(String domainName) {
        return getDmainManager(domainName).getNewEntityManager();
    }

    //    @Override
    //    public <T> Session<T> createSession(String domainName) {
    //        return createSession(domainName, IsolationLevel.DEFAULT);
    //    }
    //
    //    public <T> Session<T> createSession(String domainName, IsolationLevel isolationLevel) {
    //        return new SessionImpl<T>((EntityManager<T>) getEntityManager(domainName), isolationLevel);
    //    }

    //    /**
    //     * Gets the SQL executor.
    //     *
    //     * @param domainName the domain name
    //     * @return the SQL executor
    //     */
    //    public SQLExecutor getSQLExecutor(String domainName) {
    //        return getDmainManager(domainName).getSQLExecutor();
    //    }

    /**
     * Creates a new EntityManager object.
     *
     * @param <T>
     * @param domainName
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> AsyncBatchExecutor<T> createAsyncBatchExecutor(String domainName) {
        return new AsyncBatchExecutor<>((EntityManager<T>) getEntityManager(domainName));
    }

    /**
     * Creates a new EntityManager object.
     *
     * @param <T>
     * @param domainName
     * @param batchSize
     * @param evictDelay
     * @param capacity
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> AsyncBatchExecutor<T> createAsyncBatchExecutor(String domainName, int batchSize, long evictDelay, int capacity) {
        return new AsyncBatchExecutor<>((EntityManager<T>) getEntityManager(domainName), capacity, evictDelay, batchSize);
    }

    /**
     *
     * @param file
     * @return
     */
    protected static File findFile(String file) {
        File configurationFile = new File(file);
        configurationFile = Configuration.formatPath(configurationFile);

        if (!configurationFile.exists()) {
            configurationFile = Configuration.findFile(file);
        }

        if (configurationFile == null) {
            throw new RuntimeException("Can't find entity manager configuration file: " + file);
        }

        if (logger.isWarnEnabled()) {
            logger.warn("Found Abacus configuration file: " + configurationFile.getAbsolutePath());
        }

        return configurationFile;
    }

    /**
     * Gets the dmain manager.
     *
     * @param domainName
     * @return
     */
    protected DomainEntityManager getDmainManager(String domainName) {
        synchronized (entityManagerPool) {
            DomainEntityManager result = entityManagerPool.get(domainName);

            if (result == null) {
                for (EntityManagerConfiguration entityManagerConfig : abacusConfig.getEntityManagerConfigurationList()) {
                    boolean isInitialized = false;

                    for (DomainEntityManager dnm : entityManagerPool.values()) {
                        if (dnm.getEntityManagerConfiguration().getDomainName().equals(entityManagerConfig.getDomainName())
                                && dnm.getEntityManagerConfiguration().getEntityDefinitionFile().equals(entityManagerConfig.getEntityDefinitionFile())) {
                            isInitialized = true;

                            break;
                        }
                    }

                    if (!isInitialized) {
                        DomainEntityManager domainEntityManager = new DomainEntityManager(entityManagerConfig);
                        entityManagerPool.put(domainEntityManager.getDomainName(), domainEntityManager);
                    }
                }

                result = entityManagerPool.get(domainName);

                if (result == null) {
                    throw new IllegalArgumentException("There is no entity manager defined for domain[" + domainName + "]. ");
                }
            }

            return result;
        }
    }

    /**
     * The Class DomainEntityManager.
     */
    static final class DomainEntityManager {

        /** The domain name. */
        private final String domainName;

        /** The entity manager config. */
        private final EntityManagerConfiguration entityManagerConfig;

        /** The dsm. */
        private final DataSourceManager dsm;

        /** The db access. */
        private final DBAccess dbAccess;

        /** The ex entity manager. */
        private final EntityManagerEx<Object> entityManagerEx;

        /** The new entity manager. */
        private final NewEntityManager newEntityManager;

        //        /** The sql executor. */
        //        private final SQLExecutor sqlExecutor;

        /**
         * Instantiates a new domain entity manager.
         *
         * @param entityManagerConfig
         */
        @SuppressWarnings("deprecation")
        DomainEntityManager(EntityManagerConfiguration entityManagerConfig) {
            this.domainName = entityManagerConfig.getDomainName();
            this.entityManagerConfig = entityManagerConfig;
            this.dsm = new SQLDataSourceManager(entityManagerConfig.getDataSourceManagerConfiguration());

            final File entityDefinitionFile = entityManagerConfig.getEntityDefinitionFile();
            final EntityDefinitionFactory entityDefFactory = SQLEntityDefinitionFactory.newInstance(domainName, entityDefinitionFile);

            if (entityManagerConfig.getEntityCacheConfiguration() != null) {
                EntityCacheConfiguration entityCacheConfiguration = entityManagerConfig.getEntityCacheConfiguration();
                Collection<String> customizedEntityNames = entityCacheConfiguration.getCustomizedEntityNames();

                if (N.notNullOrEmpty(customizedEntityNames)) {
                    for (String entityName : customizedEntityNames) {
                        CustomizedEntityCacheConfiguration cecConfig = entityCacheConfiguration.getCustomizedEntityCacheConfiguration(entityName);

                        if (N.notNullOrEmpty(cecConfig.getIncludedPropertyNames())) {
                            for (Property idProp : entityDefFactory.getDefinition(entityName).getIdPropertyList()) {
                                if (!(cecConfig.getIncludedPropertyNames().contains(idProp.getName()))) {
                                    cecConfig.getIncludedPropertyNames().add(idProp.getName());
                                }
                            }
                        } else if (N.notNullOrEmpty(cecConfig.getExcludedPropertyNames())) {
                            for (Property idProp : entityDefFactory.getDefinition(entityName).getIdPropertyList()) {
                                if (cecConfig.getExcludedPropertyNames().contains(idProp.getName())) {
                                    throw new RuntimeException("Id property(" + idProp.getName() + ") must not be excluded");
                                }
                            }
                        }
                    }
                }
            }

            final Mode mode = entityManagerConfig.getMode();
            final Executant executant = new Executant(dsm);
            final DBAccessImpl dbAccessImpl = new DBAccessImpl(entityManagerConfig, entityDefFactory, executant);
            EntityManager<Object> entityManager = null;

            if (mode == Mode.LVC) {
                entityManager = new EntityManagerLVC<>(entityManagerConfig, dbAccessImpl);
            } else if (mode == Mode.VC) {
                entityManager = new EntityManagerVC<>(entityManagerConfig, dbAccessImpl);
            } else if (mode == Mode.LV) {
                entityManager = new EntityManagerLV<>(entityManagerConfig, dbAccessImpl);
            } else {
                entityManager = new EntityManagerImpl<>(entityManagerConfig, dbAccessImpl);
            }

            if (mode == Mode.basic) {
                dbAccess = dbAccessImpl;
            } else {
                dbAccess = entityManager;
            }

            entityManagerEx = new EntityManagerEx<>(entityManager);

            newEntityManager = new NewEntityManager(entityManagerEx, dsm);

            //            if (dsm == null) {
            //                sqlExecutor = null;
            //            } else {
            //                final JdbcSettings jdbcSettings = JdbcSettings.create();
            //                jdbcSettings.setBatchSize(entityManagerConfig.getBatchSize());
            //                jdbcSettings.setLogSQL(Boolean.valueOf(dsm.getPrimaryDataSource().getProperties().get(DataSourceConfiguration.SQL_LOG)));
            //
            //                sqlExecutor = new SQLExecutor(dsm, jdbcSettings, entityManagerConfig.getSQLMapper());
            //            }
        }

        /**
         * Gets the domain name.
         *
         * @return
         */
        String getDomainName() {
            return domainName;
        }

        /**
         * Gets the entity manager configuration.
         *
         * @return
         */
        EntityManagerConfiguration getEntityManagerConfiguration() {
            return entityManagerConfig;
        }

        /**
         * Gets the data source manager.
         *
         * @return
         */
        DataSourceManager getDataSourceManager() {
            return dsm;
        }

        /**
         * Gets the DB access.
         *
         * @return
         */
        DBAccess getDBAccess() {
            return dbAccess;
        }

        /**
         * Gets the entity manager.
         *
         * @param <T>
         * @return
         */
        @SuppressWarnings("unchecked")
        <T> EntityManagerEx<T> getEntityManager() {
            return (EntityManagerEx<T>) entityManagerEx;
        }

        /**
         * Gets the new entity manager.
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        NewEntityManager getNewEntityManager() {
            return newEntityManager;
        }

        //        /**
        //         * Gets the SQL executor.
        //         *
        //         * @return the SQL executor
        //         */
        //        SQLExecutor getSQLExecutor() {
        //            return sqlExecutor;
        //        }
    }
}

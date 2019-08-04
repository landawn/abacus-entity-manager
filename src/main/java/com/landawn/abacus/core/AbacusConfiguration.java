/*
 * Copyright (c) 2018, Haiyang Li.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.landawn.abacus.dataSource.DataSourceManagerConfiguration;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.exception.UncheckedIOException;
import com.landawn.abacus.http.ContentFormat;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.SQLMapper;
import com.landawn.abacus.util.XMLUtil;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public final class AbacusConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AbacusConfiguration.class);

    /**
     * Field ABACUS. (value is ""abacus"")
     */
    public static final String ABACUS = "abacus";

    /**
     * Field ABACUS_FILE_NAME. (value is "ABACUS + ".xml"")
     */
    public static final String ABACUS_FILE_NAME = ABACUS + ".xml";

    /**
     * Field VERSION. (value is ""version"")
     */
    public static final String VERSION = "version";

    /**
     * Field PROPERTIES. (value is ""properties"")
     */
    public static final String PROPERTIES = "properties";

    /**
     * Field RESOURCE. (value is ""resource"")
     */
    public static final String RESOURCE = "resource";

    /**
     * Field INITIALIZER_ON_STARTUP. (value is ""initializerOnStartupl"")
     */
    public static final String INITIALIZER_ON_STARTUP = "initializerOnStartup";

    /**
     * Field FACTORY. (value is ""factory"")
     */
    public static final String FACTORY = "factory";

    private final Map<String, String> props = new HashMap<>();

    private final List<EntityManagerConfiguration> entityManagerConfigurationList;
    private final SLogConfiguration slogConfiguration;

    private String initializerOnStartup;
    private String factory;

    public AbacusConfiguration(File abacusFile) throws UncheckedIOException {
        abacusFile = Configuration.formatPath(abacusFile);

        Document doc = Configuration.parse(abacusFile);
        Element abacusEle = doc.getDocumentElement();

        if (!abacusEle.getNodeName().equals(ABACUS)) {
            throw new AbacusException("Wrong configuration file, There is no root element: 'abacus'. ");
        }

        List<Element> propertiesElementList = XMLUtil.getElementsByTagName(abacusEle, PROPERTIES);

        if (N.notNullOrEmpty(propertiesElementList)) {
            for (Element propertiesElement : propertiesElementList) {
                File resourcePropertiesFile = Configuration.findFileByFile(abacusFile, propertiesElement.getAttribute(RESOURCE));
                Properties properties = new Properties();
                InputStream is = null;

                try {
                    is = new FileInputStream(resourcePropertiesFile);

                    if (resourcePropertiesFile.getName().endsWith(".xml")) {
                        properties.loadFromXML(is);
                    } else {
                        properties.load(is);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to load properties: " + resourcePropertiesFile.getPath(), e);
                } finally {
                    IOUtil.close(is);
                }

                for (Object key : properties.keySet()) {
                    props.put((String) key, (String) properties.get(key));
                }
            }
        }

        List<Element> entityManagerElementList = XMLUtil.getElementsByTagName(abacusEle, EntityManagerConfiguration.ENTITY_MANAGER);

        if (N.isNullOrEmpty(entityManagerElementList)) {
            throw new AbacusException("At least configure one 'entityManager' element in 'abacus' element");
        }

        List<EntityManagerConfiguration> tmpEntityManagerConfigurationList = new ArrayList<>();

        for (Element entityManagerElement : entityManagerElementList) {
            tmpEntityManagerConfigurationList.add(new EntityManagerConfiguration(entityManagerElement, abacusFile));
        }

        entityManagerConfigurationList = Collections.unmodifiableList(tmpEntityManagerConfigurationList);

        List<Element> slogElementList = XMLUtil.getElementsByTagName(abacusEle, SLogConfiguration.SLOG);

        if (N.notNullOrEmpty(slogElementList)) {
            if (slogElementList.size() > 1) {
                throw new AbacusException("Only can configure one 'slog' element in 'abacus' element");
            }

            slogConfiguration = new SLogConfiguration(slogElementList.get(0));
        } else {
            slogConfiguration = null;
        }

        List<Element> initializeElementList = XMLUtil.getElementsByTagName(abacusEle, INITIALIZER_ON_STARTUP);

        if (N.notNullOrEmpty(initializeElementList)) {
            if (initializeElementList.size() > 1) {
                throw new AbacusException("Only can configure one 'initializerOnStartup' element in 'abacus' element");
            }

            initializerOnStartup = Configuration.getTextContent(initializeElementList.get(0));
        }

        List<Element> factoryElementList = XMLUtil.getElementsByTagName(abacusEle, FACTORY);

        if (N.notNullOrEmpty(factoryElementList)) {
            if (factoryElementList.size() > 1) {
                throw new AbacusException("Only can configure one 'factory' element in 'abacus' element");
            }

            factory = Configuration.getTextContent(factoryElementList.get(0));
        }
    }

    public String getFactory() {
        return factory;
    }

    public String getInitializerOnStartup() {
        return initializerOnStartup;
    }

    public List<EntityManagerConfiguration> getEntityManagerConfigurationList() {
        return entityManagerConfigurationList;
    }

    public SLogConfiguration getSLogConfiguration() {
        return slogConfiguration;
    }

    public final class EntityManagerConfiguration extends Configuration {
        /**
         * Field ENTITY_MANAGER. (value is ""entityManager"")
         */
        public static final String ENTITY_MANAGER = "entityManager";

        /**
         * Field DOMAIN_NAME. (value is ""domainName"")
         */
        public static final String DOMAIN_NAME = "domainName";

        /**
         * Field MODE. (value is ""mode"")
         */
        public static final String MODE = "mode";

        /**
         * Field BATCH_SIZE. (value is ""batchSize"")
         */
        public static final String BATCH_SIZE = "batchSize";

        /**
         * Field DEFAULT_BATCH_SIZE.
         */
        public static final int DEFAULT_BATCH_SIZE = 200;

        /**
         * Field ENTITY_DEFINITION. (value is ""entityDefinition"")
         */
        public static final String ENTITY_DEFINITION = "entityDefinition";

        /**
         * Field SQL_MAPPER. (value is ""sqlMapper"")
         */
        public static final String SQL_MAPPER = "sqlMapper";

        /**
         * Field HANDLER. (value is ""handler"")
         */
        public static final String HANDLER = "handler";

        private final String domainName;
        private final Mode mode;

        private final int batchSize;
        private final File entityDefinitionFile;
        private final SQLMapper sqlMapper;
        private final List<String> handlerList;

        private LockConfiguration lockConfig;
        private VersionConfiguration versionConfig;
        private EntityCacheConfiguration entityCacheConfig;
        private QueryCacheConfiguration queryCacheConfig;
        private DataSourceManagerConfiguration dataSourceManagerConfig;

        private ServerConfiguration serverConfig;

        EntityManagerConfiguration(Element element, File abacusFile) {
            super(element, AbacusConfiguration.this.props);
            this.domainName = getAttribute(DOMAIN_NAME);

            String attr = getAttribute(MODE);
            mode = (attr == null) ? Mode.basic : Mode.valueOf(attr);

            attr = getAttribute(BATCH_SIZE);
            batchSize = (attr == null) ? DEFAULT_BATCH_SIZE : N.parseInt(attr);

            attr = getAttribute(ENTITY_DEFINITION);

            if (N.notNullOrEmpty(attr)) {
                entityDefinitionFile = findFileByFile(abacusFile, attr);

                if ((entityDefinitionFile == null) || !entityDefinitionFile.exists()) {
                    throw new AbacusException("Can't find entity definition file: " + attr);
                }

                if (logger.isWarnEnabled()) {
                    logger.warn("found entity definition file: " + entityDefinitionFile.getAbsolutePath());
                }
            } else {
                entityDefinitionFile = null;
            }

            sqlMapper = new SQLMapper();

            String[] fileNames = string2Array(getAttribute(SQL_MAPPER));

            for (String fileName : fileNames) {
                final File file = findFileByFile(abacusFile, fileName);
                final SQLMapper tmp = SQLMapper.fromFile(file.getAbsolutePath());

                for (String key : tmp.keySet()) {
                    sqlMapper.add(key, tmp.get(key));
                }
            }

            handlerList = Collections.unmodifiableList(string2List(getAttribute(HANDLER)));
        }

        @Override
        protected void complexElement2Attr(Element element) {
            String eleName = element.getNodeName();

            if (LockConfiguration.LOCK.equals(eleName)) {
                lockConfig = new LockConfiguration(element);
            } else if (VersionConfiguration.VERSION.equals(eleName)) {
                versionConfig = new VersionConfiguration(element);
            } else if (EntityCacheConfiguration.ENTITY_CACHE.equals(eleName)) {
                entityCacheConfig = new EntityCacheConfiguration(element);
            } else if (QueryCacheConfiguration.QUERY_CACHE.equals(eleName)) {
                queryCacheConfig = new QueryCacheConfiguration(element);
            } else if (DataSourceManagerConfiguration.DATA_SOURCE_MANAGER.equals(eleName)) {
                dataSourceManagerConfig = new DataSourceManagerConfiguration(element, AbacusConfiguration.this.props);
            } else if (ServerConfiguration.SERVER.equals(eleName)) {
                serverConfig = new ServerConfiguration(element);
            } else {
                throw new AbacusException("Unknown element: " + eleName);
            }
        }

        public String getDomainName() {
            return domainName;
        }

        public Mode getMode() {
            return mode;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public File getEntityDefinitionFile() {
            return entityDefinitionFile;
        }

        public SQLMapper getSQLMapper() {
            return sqlMapper;
        }

        public List<String> getHandlerList() {
            return handlerList;
        }

        public LockConfiguration getLockConfiguration() {
            return lockConfig;
        }

        public VersionConfiguration getVersionConfiguration() {
            return versionConfig;
        }

        public EntityCacheConfiguration getEntityCacheConfiguration() {
            return entityCacheConfig;
        }

        public QueryCacheConfiguration getQueryCacheConfiguration() {
            return queryCacheConfig;
        }

        public DataSourceManagerConfiguration getDataSourceManagerConfiguration() {
            return dataSourceManagerConfig;
        }

        public ServerConfiguration getServerConfiguration() {
            return serverConfig;
        }

        final class LockConfiguration extends Configuration {
            /**
             * Field LOCK. (value is ""LOCK"")
             */
            public static final String LOCK = "lock";

            /**
             * Field RW_LOCK_PROVIDER. (value is ""rwLockProvider"")
             */
            public static final String RW_LOCK_PROVIDER = "rwLockProvider";

            /**
             * Field RECORD_LOCK_PROVIDER. (value is ""recordLockProvider"")
             */
            public static final String RECORD_LOCK_PROVIDER = "recordLockProvider";

            /**
             * Field RECORD_LOCK_TIMEOUT. (value is ""recordLockTimeout"")
             */
            public static final String RECORD_LOCK_TIMEOUT = "recordLockTimeout";

            /**
             * Field DEFAULT_RECORD_LOCK_TIMEOUT. (value is "3000")
             */
            public static final long DEFAULT_RECORD_LOCK_TIMEOUT = 3000;

            private final String rwLockProvider;
            private final String recordLockProvider;
            private final long recordLockTimeout;

            LockConfiguration(Element element) {
                super(element, AbacusConfiguration.this.props);

                rwLockProvider = getAttribute(RW_LOCK_PROVIDER);

                recordLockProvider = getAttribute(RECORD_LOCK_PROVIDER);

                String attr = getAttribute(RECORD_LOCK_TIMEOUT);

                recordLockTimeout = (attr == null) ? DEFAULT_RECORD_LOCK_TIMEOUT : Configuration.readTimeValue(attr);
            }

            public String getRWLockProvider() {
                return rwLockProvider;
            }

            public String getRecordLockProvider() {
                return recordLockProvider;
            }

            public long getRecordLockTimeout() {
                return recordLockTimeout;
            }
        }

        final class VersionConfiguration extends Configuration {
            public static final String VERSION = "version";
            public static final String PROVIDER = "provider";
            private final String provider;

            VersionConfiguration(Element element) {
                super(element, AbacusConfiguration.this.props);
                provider = getAttribute(PROVIDER);
            }

            public String getProvider() {
                return provider;
            }
        }

        class CacheConfiguration extends Configuration {
            /**
             * Field PROVIDER. (value is ""provider"")
             */
            public static final String PROVIDER = "provider";

            /**
             * Field MAX_ACTIVE. (value is ""capacity"")
             */
            public static final String CAPACITY = "capacity";

            /**
             * Field EVICT_DELAY. (value is ""evictDelay"")
             */
            public static final String EVICT_DELAY = "evictDelay";

            /**
             * Field HANDLE_LIVE_TIME. (value is ""liveTime"")
             */
            public static final String LIVE_TIME = "liveTime";

            /**
             * Field HANDLE_MAX_IDLE_TIME. (value is ""maxIdleTime"")
             */
            public static final String MAX_IDLE_TIME = "maxIdleTime";

            private final String provider;
            private final int capacity;
            private final long evictDelay;
            private final long liveTime;
            private final long maxIdleTime;

            CacheConfiguration(Element element, int defaultCapacity, long defaultEvictDelay, long defaultLiveTime, long defaultMaxIdleTime) {
                super(element, AbacusConfiguration.this.props);

                provider = getAttribute(PROVIDER);

                String attr = getAttribute(CAPACITY);
                this.capacity = (attr == null) ? defaultCapacity : N.parseInt(attr);

                attr = getAttribute(EVICT_DELAY);
                this.evictDelay = (attr == null) ? defaultEvictDelay : Configuration.readTimeValue(attr);

                attr = getAttribute(LIVE_TIME);
                this.liveTime = (attr == null) ? defaultLiveTime : Configuration.readTimeValue(attr);

                attr = getAttribute(MAX_IDLE_TIME);
                this.maxIdleTime = (attr == null) ? defaultMaxIdleTime : Configuration.readTimeValue(attr);
            }

            public String getProvider() {
                return provider;
            }

            public int getCapacity() {
                return capacity;
            }

            public long getEvictDelay() {
                return evictDelay;
            }

            public long getLiveTime() {
                return liveTime;
            }

            public long getMaxIdleTime() {
                return maxIdleTime;
            }
        }

        final class EntityCacheConfiguration extends CacheConfiguration {
            /**
             * Field ENTITY_CACHE. (value is ""entityCache"")
             */
            public static final String ENTITY_CACHE = "entityCache";

            /**
             * Field INCLUDED_ENTITIES. (value is ""includedEntities"")
             */
            public static final String INCLUDED_ENTITIES = "includedEntities";

            /**
             * Field EXCLUDED_ENTITIES. (value is ""excludedEntities"")
             */
            public static final String EXCLUDED_ENTITIES = "excludedEntities";

            /**
             * pool capacity.
             */
            public static final int DEFAULT_CAPACITY = 30000;

            /**
             * Unit is milliseconds.
             */
            public static final long DEFAULT_EVICT_DELAY = 3000;

            /**
             * Field DEFAULT_LIVE_TIME. Unit is milliseconds.
             */
            public static final long DEFAULT_LIVE_TIME = 24 * 60 * 60 * 1000L;

            /**
             * Field DEFAULT_MAX_IDLE_TIME. Unit is milliseconds.
             */
            public static final long DEFAULT_MAX_IDLE_TIME = 60 * 60 * 1000L;

            private final Set<String> includedEntityNames;
            private final Set<String> excludedEntityNames;

            private Map<String, CustomizedEntityCacheConfiguration> customizedEntities;

            EntityCacheConfiguration(Element element) {
                super(element, DEFAULT_CAPACITY, DEFAULT_EVICT_DELAY, DEFAULT_LIVE_TIME, DEFAULT_MAX_IDLE_TIME);

                includedEntityNames = Collections.unmodifiableSet(string2Set(getAttribute(INCLUDED_ENTITIES)));

                excludedEntityNames = Collections.unmodifiableSet(string2Set(getAttribute(EXCLUDED_ENTITIES)));

                if ((includedEntityNames.size() > 0) && (excludedEntityNames.size() > 0)) {
                    throw new AbacusException("Can't set 'includedEntities' and 'excludedEntities' element at the same time. ");
                }
            }

            @Override
            protected void init() {
                customizedEntities = new HashMap<>();
            }

            @Override
            protected void complexElement2Attr(Element element) {
                String eleName = element.getNodeName();

                if (CustomizedEntityCacheConfiguration.CUSTOMIZED_ENTITY.equals(eleName)) {
                    CustomizedEntityCacheConfiguration customizedEntity = new CustomizedEntityCacheConfiguration(element);
                    customizedEntities.put(customizedEntity.getEntityName(), customizedEntity);
                } else {
                    throw new AbacusException("Unknow element: " + eleName);
                }
            }

            Set<String> getIncludedEntityNames() {
                return includedEntityNames;
            }

            Set<String> getExcludedEntityNames() {
                return excludedEntityNames;
            }

            public boolean isExcludedEntity(String entityName) {
                if (excludedEntityNames.size() > 0) {
                    return excludedEntityNames.contains(entityName);
                } else if (includedEntityNames.size() > 0) {
                    return !includedEntityNames.contains(entityName);
                }

                return false;
            }

            public Collection<String> getCustomizedEntityNames() {
                return customizedEntities.keySet();
            }

            public CustomizedEntityCacheConfiguration getCustomizedEntityCacheConfiguration(String entityName) {
                return customizedEntities.get(entityName);
            }

            public final class CustomizedEntityCacheConfiguration extends Configuration {
                /**
                 * Field CUSTOMIZED_ENTITY. (value is ""customizedEntity"")
                 */
                public static final String CUSTOMIZED_ENTITY = "customizedEntity";

                /**
                 * Field NAME
                 */
                public static final String NAME = "name";

                /**
                 * Field INCLUDED_PROPERTIES.
                 */
                public static final String INCLUDED_PROPERTIES = "includedProperties";

                /**
                 * Field EXCLUDED_PROPERTIES.
                 */
                public static final String EXCLUDED_PROPERTIES = "excludedProperties";

                private final String entityName;
                private final long liveTime;
                private final long maxIdleTime;
                private final Set<String> includedPropNames;
                private final Set<String> excludedPropNames;

                CustomizedEntityCacheConfiguration(Element element) {
                    super(element, AbacusConfiguration.this.props);

                    entityName = getAttribute(NAME);

                    String attr = getAttribute(LIVE_TIME);

                    if (attr == null) {
                        Element entityCacheEle = (Element) element.getParentNode();
                        List<Element> commonEntityLiveTimeElementList = XMLUtil.getElementsByTagName(entityCacheEle, LIVE_TIME);

                        if (N.notNullOrEmpty(commonEntityLiveTimeElementList)) {
                            attr = getTextContent(commonEntityLiveTimeElementList.get(0));
                        }
                    }

                    liveTime = (attr == null) ? DEFAULT_LIVE_TIME : Configuration.readTimeValue(attr);

                    attr = getAttribute(MAX_IDLE_TIME);

                    if (attr == null) {
                        Element entityCacheEle = (Element) element.getParentNode();
                        List<Element> commonEntityMaxIdleTimeElementList = XMLUtil.getElementsByTagName(entityCacheEle, MAX_IDLE_TIME);

                        if (N.notNullOrEmpty(commonEntityMaxIdleTimeElementList)) {
                            attr = getTextContent(commonEntityMaxIdleTimeElementList.get(0));
                        }
                    }

                    maxIdleTime = (attr == null) ? DEFAULT_MAX_IDLE_TIME : Configuration.readTimeValue(attr);

                    final String entityNamePrefix = entityName + ".";

                    includedPropNames = new HashSet<>();

                    if (N.notNullOrEmpty(getAttribute(INCLUDED_PROPERTIES))) {
                        Set<String> tmpSet = string2Set(getAttribute(INCLUDED_PROPERTIES));
                        includedPropNames.addAll(tmpSet);

                        for (String propName : tmpSet) {
                            if (propName.startsWith(entityNamePrefix)) {
                                includedPropNames.add(propName.substring(entityNamePrefix.length()));
                            } else {
                                includedPropNames.add(entityNamePrefix + propName);
                            }
                        }
                    }

                    excludedPropNames = new HashSet<>();

                    if (N.notNullOrEmpty(getAttribute(EXCLUDED_PROPERTIES))) {
                        Set<String> tmpSet = string2Set(getAttribute(EXCLUDED_PROPERTIES));
                        excludedPropNames.addAll(tmpSet);

                        for (String propName : tmpSet) {
                            if (propName.startsWith(entityNamePrefix)) {
                                excludedPropNames.add(propName.substring(entityNamePrefix.length()));
                            } else {
                                excludedPropNames.add(entityNamePrefix + propName);
                            }
                        }
                    }

                    if ((includedPropNames.size() > 0) && (excludedPropNames.size() > 0)) {
                        throw new AbacusException("Can't set 'includedProperty' and 'excludedProperty' element at the same time. ");
                    }
                }

                public String getEntityName() {
                    return entityName;
                }

                public long getLiveTime() {
                    return liveTime;
                }

                public long getMaxIdleTime() {
                    return maxIdleTime;
                }

                Set<String> getIncludedPropertyNames() {
                    return includedPropNames;
                }

                Set<String> getExcludedPropertyNames() {
                    return excludedPropNames;
                }

                public boolean isExcludedProperty(String propName) {
                    if (excludedPropNames.size() > 0) {
                        return excludedPropNames.contains(propName);
                    } else if (includedPropNames.size() > 0) {
                        return !includedPropNames.contains(propName);
                    }

                    return false;
                }
            }
        }

        public final class QueryCacheConfiguration extends CacheConfiguration {
            /**
             * Field QUERY_CACHE. (value is ""queryCache"")
             */
            public static final String QUERY_CACHE = "queryCache";

            /**
             * Type is boolean, default is true.
             */
            public static final String AUTO_REFRESH = "autoRefresh";

            /**
             * Type is boolean, default is true.
             */
            public static final boolean DEFAULT_AUTO_REFRESH = true;

            /**
             * Type is boolean, default is false.
             */
            public static final String ZIP_CACHE = "zipCache";

            /**
             * Type is boolean, default is false.
             */
            public static final boolean DEFAULT_ZIP_CACHE = false;

            /**
             * The limited time to update the cache after update data. unit is milliseconds
             */
            public static final String MAX_CHECK_QUERY_CACHE_TIME = "maxCheckQueryCacheTime";

            /**
             * Default value for {@code DEFAULT_MAX_CHECK_QUERY_CACHE_TIME}. Unit is milliseconds
             */
            public static final int DEFAULT_MAX_CHECK_QUERY_CACHE_TIME = 1000;

            /**
             * The condition that a cache must meet when to check if it's need be update. If the cache's size less than
             * this condition, it will be removed without checking if the result in it is updated when database was
             * updated.
             */
            public static final String MIN_CHECK_QUERY_CACHE_SIZE = "minCheckQueryCacheSize";

            /**
             * Default value {@code MIN_CHECK_QUERY_CACHE_SIZE} option.
             */
            public static final int DEFAULT_MIN_CHECK_QUERY_CACHE_SIZE = 100;

            /**
             * Field DEFAULT_CAPACITY. (value is 3000)
             */
            public static final int DEFAULT_CAPACITY = 3000;

            /**
             * Field DEFAULT_EVICT_DELAY. Unit is milliseconds.
             */
            public static final long DEFAULT_EVICT_DELAY = 3000;

            /**
             * Default value for {@code DEFAULT_LIVE_TIME}. Unit is milliseconds
             */
            public static final long DEFAULT_LIVE_TIME = 60 * 60 * 1000L;

            /**
             * Default value for {@code DEFAULT_MAX_IDLE_TIME}. Unit is milliseconds
             */
            public static final long DEFAULT_MAX_IDLE_TIME = 15 * 60 * 1000L;

            private final boolean autoRefresh;
            private final boolean zipCache;
            private final long maxCheckCacheTime;
            private final int minCheckCacheSize;

            private CacheResultConditionConfiguration cacheResultConditionConfiguration;

            QueryCacheConfiguration(Element element) {
                super(element, DEFAULT_CAPACITY, DEFAULT_EVICT_DELAY, DEFAULT_LIVE_TIME, DEFAULT_MAX_IDLE_TIME);

                String attr = getAttribute(AUTO_REFRESH);
                autoRefresh = (attr == null) ? DEFAULT_AUTO_REFRESH : Boolean.valueOf(attr);

                attr = getAttribute(ZIP_CACHE);
                zipCache = (attr == null) ? DEFAULT_ZIP_CACHE : Boolean.valueOf(attr);

                attr = getAttribute(MAX_CHECK_QUERY_CACHE_TIME);
                maxCheckCacheTime = (attr == null) ? DEFAULT_MAX_CHECK_QUERY_CACHE_TIME : Configuration.readTimeValue(attr);

                attr = getAttribute(MIN_CHECK_QUERY_CACHE_SIZE);
                minCheckCacheSize = (attr == null) ? DEFAULT_MIN_CHECK_QUERY_CACHE_SIZE : N.parseInt(attr);
            }

            public boolean isAutoRefresh() {
                return autoRefresh;
            }

            public boolean isZipCache() {
                return zipCache;
            }

            public long getMaxCheckCacheTime() {
                return maxCheckCacheTime;
            }

            public int getMinCheckCacheSize() {
                return minCheckCacheSize;
            }

            public CacheResultConditionConfiguration getCacheResultConditionConfiguration() {
                return cacheResultConditionConfiguration;
            }

            @Override
            protected void complexElement2Attr(Element element) {
                String eleName = element.getNodeName();

                if (CacheResultConditionConfiguration.CACHE_RESULT_CONDITION.equals(eleName)) {
                    cacheResultConditionConfiguration = new CacheResultConditionConfiguration(element);
                } else {
                    throw new AbacusException("Unknown element: " + eleName);
                }
            }

            public final class CacheResultConditionConfiguration extends Configuration {
                /**
                 * Field CACHE_RESULT_CONDITION. (value is ""cacheResultCondition"")
                 */
                public static final String CACHE_RESULT_CONDITION = "cacheResultCondition";

                /**
                 * Field MIN_COUNT. (value is ""minCount"")
                 */
                public static final String MIN_COUNT = "minCount";

                /**
                 * Default value for {@code DEFAULT_MIN_COUNT} option
                 */
                public static final int DEFAULT_MIN_COUNT = 100;

                /**
                 * Field MAX_COUNT. (value is ""maxCount"")
                 */
                public static final String MAX_COUNT = "maxCount";

                /**
                 * Default value for {@code CACHE_RESULT_CONDITION} option
                 */
                public static final int DEFAULT_MAX_COUNT = 100000;

                /**
                 * Field MIN_QUERY_TIME. (value is ""minQueryTime"")
                 */
                public static final String MIN_QUERY_TIME = "minQueryTime";

                /**
                 * If the searching database time less the specified searching database time. Don't cache the result.
                 * Unit is milliseconds.
                 */
                public static final long DEFAULT_MIN_QUERY_TIME = 10;

                private final int minCount;
                private final int maxCount;
                private final long minQueryTime;

                public CacheResultConditionConfiguration(Element element) {
                    super(element, AbacusConfiguration.this.props);

                    String attr = getAttribute(MIN_COUNT);
                    minCount = (attr == null) ? DEFAULT_MIN_COUNT : N.parseInt(attr);

                    attr = getAttribute(MAX_COUNT);
                    maxCount = (attr == null) ? DEFAULT_MAX_COUNT : N.parseInt(attr);

                    attr = getAttribute(MIN_QUERY_TIME);
                    minQueryTime = (attr == null) ? DEFAULT_MIN_QUERY_TIME : Configuration.readTimeValue(attr);
                }

                public int getMinCount() {
                    return minCount;
                }

                public int getMaxCount() {
                    return maxCount;
                }

                public long getMinQueryTime() {
                    return minQueryTime;
                }
            }
        }

        public final class ServerConfiguration extends Configuration {
            /**
             * Field SERVER. (value is ""server"")
             */
            public static final String SERVER = "server";

            /**
             * Field URL. (value is ""url"")
             */
            public static final String URL = "url";

            /**
             * Field MAX_CONNECTION. (value is ""maxConnection"")
             */
            public static final String MAX_CONNECTION = "maxConnection";

            /**
             * Field DEFAULT_MAX_CONNECTION.
             */
            public static final int DEFAULT_MAX_CONNECTION = 64;

            /**
             * Field CONNECTION_TIMEOUT. (value is ""connectionTimeout"")
             */
            public static final String CONNECTION_TIMEOUT = "connectionTimeout";

            /**
             * Field DEFAULT_CONNECTION_TIMEOUT.
             */
            public static final int DEFAULT_CONNECTION_TIMEOUT = 8 * 1000;

            /**
             * Field READ_TIMEOUT. (value is ""readTimeout"")
             */
            public static final String READ_TIMEOUT = "readTimeout";

            /**
             * Field DEFAULT_READ_TIMEOUT.
             */
            public static final int DEFAULT_READ_TIMEOUT = 15 * 1000;

            /**
             * Field CONTENT_FORMAT. (value is ""contentFormat"")
             */
            public static final String CONTENT_FORMAT = "contentFormat";

            private final String url;
            private final int maxConnection;
            private final int connTimeout;
            private final int readTimeout;
            private final ContentFormat contentFormat;

            ServerConfiguration(Element element) {
                super(element, AbacusConfiguration.this.props);

                this.url = getAttribute(URL);

                String attr = getAttribute(MAX_CONNECTION);
                maxConnection = N.isNullOrEmpty(attr) ? DEFAULT_MAX_CONNECTION : N.parseInt(attr);

                attr = getAttribute(CONNECTION_TIMEOUT);
                connTimeout = N.isNullOrEmpty(attr) ? DEFAULT_CONNECTION_TIMEOUT : (int) Configuration.readTimeValue(attr);

                attr = getAttribute(READ_TIMEOUT);
                readTimeout = N.isNullOrEmpty(attr) ? DEFAULT_READ_TIMEOUT : (int) Configuration.readTimeValue(attr);

                attr = getAttribute(CONTENT_FORMAT);
                contentFormat = N.isNullOrEmpty(attr) ? ContentFormat.XML : ContentFormat.valueOf(attr.toUpperCase());
            }

            public String getUrl() {
                return url;
            }

            public int getMaxConnection() {
                return maxConnection;
            }

            public int getConnectionTimeout() {
                return connTimeout;
            }

            public int getReadTimeout() {
                return readTimeout;
            }

            public ContentFormat getContentFormat() {
                return contentFormat;
            }
        }
    }

    public final class SLogConfiguration extends Configuration {
        /**
         * Field SLOG. (value is ""slog"")
         */
        public static final String SLOG = "slog";

        /**
         * Field OPEN. (value is ""open"")
         */
        public static final String OPEN = "open";

        /**
         * Field BATCH_SIZE. (value is ""batchSize"")
         */
        public static final String BATCH_SIZE = "batchSize";

        /**
         * Field DEFAULT_BATCH_SIZE. (value is 200)
         */
        public static final int DEFAULT_BATCH_SIZE = 200;

        /**
         * Field DOMAIN. (value is ""domain"")
         */
        public static final String DOMAIN = "domain";

        /**
         * Field TABLE. (value is ""table"")
         */
        public static final String TABLE = "table";

        private final boolean isOpen;
        private final int batchSize;

        private Map<String, DomainConfiguration> domainList;
        private TableConfiguration table;

        SLogConfiguration(Element element) {
            super(element, AbacusConfiguration.this.props);
            isOpen = Boolean.valueOf(getAttribute(OPEN));

            String attr = getAttribute(BATCH_SIZE);

            batchSize = N.isNullOrEmpty(attr) ? DEFAULT_BATCH_SIZE : N.parseInt(attr);

            if (domainList == null) {
                domainList = new HashMap<>();
            }
        }

        public boolean isOpen() {
            return isOpen;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public DomainConfiguration getDomain(String domainName) {
            return domainList.get(domainName);
        }

        public TableConfiguration getTable() {
            return table;
        }

        @Override
        protected void init() {
            domainList = new HashMap<>();
        }

        @Override
        protected void complexElement2Attr(Element element) {
            if (TABLE.equals(element.getNodeName())) {
                table = new TableConfiguration(element);
            } else if (DOMAIN.equals(element.getNodeName())) {
                if (domainList == null) {
                    domainList = new HashMap<>();
                }

                DomainConfiguration domain = new DomainConfiguration(element);
                domainList.put(domain.getAttribute(DomainConfiguration.NAME), domain);
            } else {
                throw new AbacusException("Unknow element: " + element.getNodeName());
            }
        }

        public final class DomainConfiguration extends Configuration {
            /**
             * Field NAME. (value is ""name"")
             */
            public static final String NAME = "name";

            /**
             * Field OPERATION_CODE
             */
            public static final String OPERATION_CODE = "operationCode";

            private final int operationCode;

            DomainConfiguration(Element element) {
                super(element, AbacusConfiguration.this.props);

                String attr = getAttribute(OPERATION_CODE);
                if (N.isNullOrEmpty(attr)) {
                    int all = 0;
                    for (OperationType e : OperationType.values()) {
                        all += e.intValue();
                    }

                    operationCode = all;
                } else {
                    operationCode = N.parseInt(attr);
                }
            }

            public int getOperationCode() {
                return operationCode;
            }
        }

        public final class TableConfiguration extends Configuration {
            /**
             * Field NAME. (value is ""name"")
             */
            public static final String NAME = "name";

            TableConfiguration(Element element) {
                super(element, AbacusConfiguration.this.props);
            }
        }
    }
}

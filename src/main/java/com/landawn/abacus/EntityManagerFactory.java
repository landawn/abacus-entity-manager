/*
 * Copyright (C) 2015 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus;

import java.util.Collection;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating EntityManager objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface EntityManagerFactory {

    /**
     * Gets the domain names.
     *
     * @return the initialized domain name list.
     */
    Collection<String> getDomainNames();

    /**
     * Method getDBAccess.
     *
     * @param domainName the domain name
     * @return DBAccess
     */
    DBAccess getDBAccess(String domainName);

    /**
     * Method getEntityManager.
     *
     * @param <T> the generic type
     * @param domainName the domain name
     * @return the entity manager
     */
    <T> EntityManager<T> getEntityManager(String domainName);

    //    /**
    //     * Method createSession
    //     * 
    //     * @param <T>
    //     * @param domainName
    //     * @return
    //     */
    //    @SuppressWarnings("deprecation")
    //    <T> Session<T> createSession(String domainName);
}

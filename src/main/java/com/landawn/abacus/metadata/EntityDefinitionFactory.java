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

package com.landawn.abacus.metadata;

import java.util.Collection;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating EntityDefinition objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface EntityDefinitionFactory {

    /**
     * Domain name.
     *
     * @return
     */
    String domainName();

    /**
     * Gets the definition list.
     *
     * @return
     */
    Collection<EntityDefinition> getDefinitionList();

    /**
     * Gets the definition.
     *
     * @param entityName
     * @return
     */
    EntityDefinition getDefinition(String entityName);

    /**
     * Gets the entity name list.
     *
     * @return
     */
    Collection<String> getEntityNameList();

    /**
     * Gets the attributes.
     *
     * @return
     */
    Map<String, String> getAttributes();

    /**
     * Gets the attribute.
     *
     * @param attrName
     * @return
     */
    String getAttribute(String attrName);

    /**
     * Export definition.
     *
     * @return
     */
    byte[] exportDefinition();
}

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

import java.util.List;
import java.util.Map;

import com.landawn.abacus.idGenerator.IdGenerator;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface EntityDefinition {

    /**
     * Gets the name.
     *
     * @return
     */
    String getName();

    /**
     * Gets the table name.
     *
     * @return
     */
    String getTableName();

    /**
     * Gets the java type.
     *
     * @return
     */
    String getJavaType();

    /**
     * Gets the type class.
     *
     * @param <T>
     * @return
     */
    <T> Class<T> getTypeClass();

    /**
     * Gets the array type class.
     *
     * @param <T>
     * @return
     */
    <T> Class<T> getArrayTypeClass();

    /**
     * Gets the property list.
     *
     * @return
     */
    List<Property> getPropertyList();

    /**
     * Gets the property name list.
     *
     * @return
     */
    List<String> getPropertyNameList();

    /**
     * Gets the property.
     *
     * @param propName
     * @return
     */
    Property getProperty(String propName);

    /**
     * Gets the id generator list.
     *
     * @return
     */
    List<IdGenerator<Object>> getIdGeneratorList();

    /**
     * Gets the id generator.
     *
     * @param propName
     * @return
     */
    IdGenerator<Object> getIdGenerator(String propName);

    /**
     * Gets the id property list.
     *
     * @return
     */
    List<Property> getIdPropertyList();

    /**
     * Gets the id property name list.
     *
     * @return
     */
    List<String> getIdPropertyNameList();

    /**
     * Gets the UID property list.
     *
     * @return
     */
    List<Property> getUIDPropertyList();

    /**
     * Gets the UID property name list.
     *
     * @return
     */
    List<String> getUIDPropertyNameList();

    /**
     * Gets the entiy property list.
     *
     * @return
     */
    List<Property> getEntiyPropertyList();

    /**
     * Gets the default load property list.
     *
     * @return
     */
    List<Property> getDefaultLoadPropertyList();

    /**
     * Gets the default load property name list.
     *
     * @return
     */
    List<String> getDefaultLoadPropertyNameList();

    /**
     * Gets the default on insert property list.
     *
     * @return
     */
    List<Property> getDefaultOnInsertPropertyList();

    /**
     * Gets the default on update property list.
     *
     * @return
     */
    List<Property> getDefaultOnUpdatePropertyList();

    /**
     * Checks if is id auto generated.
     *
     * @return true, if is id auto generated
     */
    boolean isIdAutoGenerated();

    /**
     * Checks if is slice entity.
     *
     * @return true, if is slice entity
     */
    boolean isSliceEntity();

    /**
     * Gets the slice entity list.
     *
     * @return
     */
    List<EntityDefinition> getSliceEntityList();

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
     * Gets the parent entity.
     *
     * @return
     */
    EntityDefinition getParentEntity();

    /**
     * Gets the factory.
     *
     * @return
     */
    EntityDefinitionFactory getFactory();
}

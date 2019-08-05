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
 * The Interface EntityDefinition.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface EntityDefinition {

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    String getTableName();

    /**
     * Gets the java type.
     *
     * @return the java type
     */
    String getJavaType();

    /**
     * Gets the type class.
     *
     * @param <T> the generic type
     * @return the type class
     */
    <T> Class<T> getTypeClass();

    /**
     * Gets the array type class.
     *
     * @param <T> the generic type
     * @return the array type class
     */
    <T> Class<T> getArrayTypeClass();

    /**
     * Gets the property list.
     *
     * @return the property list
     */
    List<Property> getPropertyList();

    /**
     * Gets the property name list.
     *
     * @return the property name list
     */
    List<String> getPropertyNameList();

    /**
     * Gets the property.
     *
     * @param propName the prop name
     * @return the property
     */
    Property getProperty(String propName);

    /**
     * Gets the id generator list.
     *
     * @return the id generator list
     */
    List<IdGenerator<Object>> getIdGeneratorList();

    /**
     * Gets the id generator.
     *
     * @param propName the prop name
     * @return the id generator
     */
    IdGenerator<Object> getIdGenerator(String propName);

    /**
     * Gets the id property list.
     *
     * @return the id property list
     */
    List<Property> getIdPropertyList();

    /**
     * Gets the id property name list.
     *
     * @return the id property name list
     */
    List<String> getIdPropertyNameList();

    /**
     * Gets the UID property list.
     *
     * @return the UID property list
     */
    List<Property> getUIDPropertyList();

    /**
     * Gets the UID property name list.
     *
     * @return the UID property name list
     */
    List<String> getUIDPropertyNameList();

    /**
     * Gets the entiy property list.
     *
     * @return the entiy property list
     */
    List<Property> getEntiyPropertyList();

    /**
     * Gets the default load property list.
     *
     * @return the default load property list
     */
    List<Property> getDefaultLoadPropertyList();

    /**
     * Gets the default load property name list.
     *
     * @return the default load property name list
     */
    List<String> getDefaultLoadPropertyNameList();

    /**
     * Gets the default on insert property list.
     *
     * @return the default on insert property list
     */
    List<Property> getDefaultOnInsertPropertyList();

    /**
     * Gets the default on update property list.
     *
     * @return the default on update property list
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
     * @return the slice entity list
     */
    List<EntityDefinition> getSliceEntityList();

    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    Map<String, String> getAttributes();

    /**
     * Gets the attribute.
     *
     * @param attrName the attr name
     * @return the attribute
     */
    String getAttribute(String attrName);

    /**
     * Gets the parent entity.
     *
     * @return the parent entity
     */
    EntityDefinition getParentEntity();

    /**
     * Gets the factory.
     *
     * @return the factory
     */
    EntityDefinitionFactory getFactory();
}

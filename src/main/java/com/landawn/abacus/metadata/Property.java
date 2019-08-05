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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.idGenerator.IdGenerator;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.validator.Validator;

// TODO: Auto-generated Javadoc
/**
 * The Interface Property.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Property {

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the canonical name.
     *
     * @return the canonical name
     */
    String getCanonicalName();

    /**
     * Gets the type.
     *
     * @return the type
     */
    Type<Object> getType();

    /**
     * Gets the default on insert.
     *
     * @param <T> the generic type
     * @return the default on insert
     */
    <T> T getDefaultOnInsert();

    /**
     * Gets the default on update.
     *
     * @param <T> the generic type
     * @return the default on update
     */
    <T> T getDefaultOnUpdate();

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    String getColumnName();

    /**
     * Gets the canonical column name.
     *
     * @return the canonical column name
     */
    String getCanonicalColumnName();

    /**
     * Gets the column type.
     *
     * @return the column type
     */
    ColumnType getColumnType();

    /**
     * Gets the column entity def.
     *
     * @return the column entity def
     */
    EntityDefinition getColumnEntityDef();

    /**
     * Gets the on update action.
     *
     * @return the on update action
     */
    OnUpdateAction getOnUpdateAction();

    /**
     * Gets the on delete action.
     *
     * @return the on delete action
     */
    OnDeleteAction getOnDeleteAction();

    /**
     * Gets the order by.
     *
     * @return the order by
     */
    String getOrderBy();

    /**
     * Gets the association.
     *
     * @return the association
     */
    Association getAssociation();

    /**
     * Gets the validator list.
     *
     * @return the validator list
     */
    List<Validator<Object>> getValidatorList();

    /**
     * Gets the sub property list.
     *
     * @return the sub property list
     */
    List<Property> getSubPropertyList();

    /**
     * Gets the sub property name list.
     *
     * @return the sub property name list
     */
    List<String> getSubPropertyNameList();

    /**
     * Gets the sub property.
     *
     * @param propName the prop name
     * @return the sub property
     */
    Property getSubProperty(String propName);

    /**
     * Checks if is id.
     *
     * @return true, if is id
     */
    boolean isId();

    /**
     * Checks if is uid.
     *
     * @return true, if is uid
     */
    boolean isUID();

    /**
     * Checks if is auto increment.
     *
     * @return true, if is auto increment
     */
    boolean isAutoIncrement();

    /**
     * Checks if is readable.
     *
     * @return true, if is readable
     */
    boolean isReadable();

    /**
     * Checks if is updatable.
     *
     * @return true, if is updatable
     */
    boolean isUpdatable();

    /**
     * Checks if is insertable.
     *
     * @return true, if is insertable
     */
    boolean isInsertable();

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    boolean isReadOnly();

    /**
     * Checks if is list.
     *
     * @return true, if is list
     */
    boolean isList();

    /**
     * Checks if is sets the.
     *
     * @return true, if is sets the
     */
    boolean isSet();

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    boolean isCollection();

    /**
     * As collection.
     *
     * @param <T> the generic type
     * @param entities the entities
     * @return the collection
     */
    @SuppressWarnings("unchecked")
    <T> Collection<T> asCollection(T... entities);

    /**
     * As collection.
     *
     * @param <T> the generic type
     * @param entities the entities
     * @return the collection
     */
    <T> Collection<T> asCollection(Collection<T> entities);

    /**
     * Gets the sets the method.
     *
     * @param clazz the clazz
     * @return the sets the method
     */
    Method getSetMethod(Class<?> clazz);

    /**
     * Gets the gets the method.
     *
     * @param clazz the clazz
     * @return the gets the method
     */
    Method getGetMethod(Class<?> clazz);

    /**
     * Value of.
     *
     * @param <T> the generic type
     * @param st the st
     * @return the t
     */
    <T> T valueOf(String st);

    /**
     * String of.
     *
     * @param propvalue the propvalue
     * @return the string
     */
    String stringOf(Object propvalue);

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
     * Gets the id generator.
     *
     * @return the id generator
     */
    @SuppressWarnings("rawtypes")
    IdGenerator getIdGenerator();

    /**
     * Gets the entity definition.
     *
     * @return the entity definition
     */
    EntityDefinition getEntityDefinition();
}

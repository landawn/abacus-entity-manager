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
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Property {

    /**
     * Gets the name.
     *
     * @return
     */
    String getName();

    /**
     * Gets the canonical name.
     *
     * @return
     */
    String getCanonicalName();

    /**
     * Gets the type.
     *
     * @return
     */
    Type<Object> getType();

    /**
     * Gets the default on insert.
     *
     * @param <T>
     * @return
     */
    <T> T getDefaultOnInsert();

    /**
     * Gets the default on update.
     *
     * @param <T>
     * @return
     */
    <T> T getDefaultOnUpdate();

    /**
     * Gets the column name.
     *
     * @return
     */
    String getColumnName();

    /**
     * Gets the canonical column name.
     *
     * @return
     */
    String getCanonicalColumnName();

    /**
     * Gets the column type.
     *
     * @return
     */
    ColumnType getColumnType();

    /**
     * Gets the column entity def.
     *
     * @return
     */
    EntityDefinition getColumnEntityDef();

    /**
     * Gets the on update action.
     *
     * @return
     */
    OnUpdateAction getOnUpdateAction();

    /**
     * Gets the on delete action.
     *
     * @return
     */
    OnDeleteAction getOnDeleteAction();

    /**
     * Gets the order by.
     *
     * @return
     */
    String getOrderBy();

    /**
     * Gets the association.
     *
     * @return
     */
    Association getAssociation();

    /**
     * Gets the validator list.
     *
     * @return
     */
    List<Validator<Object>> getValidatorList();

    /**
     * Gets the sub property list.
     *
     * @return
     */
    List<Property> getSubPropertyList();

    /**
     * Gets the sub property name list.
     *
     * @return
     */
    List<String> getSubPropertyNameList();

    /**
     * Gets the sub property.
     *
     * @param propName
     * @return
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
     *
     * @param <T>
     * @param entities
     * @return
     */
    @SuppressWarnings("unchecked")
    <T> Collection<T> asCollection(T... entities);

    /**
     *
     * @param <T>
     * @param entities
     * @return
     */
    <T> Collection<T> asCollection(Collection<T> entities);

    /**
     * Gets the sets the method.
     *
     * @param clazz
     * @return
     */
    Method getSetMethod(Class<?> clazz);

    /**
     * Gets the gets the method.
     *
     * @param clazz
     * @return
     */
    Method getGetMethod(Class<?> clazz);

    /**
     *
     * @param <T>
     * @param st
     * @return
     */
    <T> T valueOf(String st);

    /**
     *
     * @param propvalue
     * @return
     */
    String stringOf(Object propvalue);

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
     * Gets the id generator.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    IdGenerator getIdGenerator();

    /**
     * Gets the entity definition.
     *
     * @return
     */
    EntityDefinition getEntityDefinition();
}

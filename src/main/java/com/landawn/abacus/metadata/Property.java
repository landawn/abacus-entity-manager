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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public interface Property {
    String getName();

    String getCanonicalName();

    Type<Object> getType();

    <T> T getDefaultOnInsert();

    <T> T getDefaultOnUpdate();

    String getColumnName();

    String getCanonicalColumnName();

    ColumnType getColumnType();

    EntityDefinition getColumnEntityDef();

    OnUpdateAction getOnUpdateAction();

    OnDeleteAction getOnDeleteAction();

    String getOrderBy();

    Association getAssociation();

    List<Validator<Object>> getValidatorList();

    List<Property> getSubPropertyList();

    List<String> getSubPropertyNameList();

    Property getSubProperty(String propName);

    boolean isId();

    boolean isUID();

    boolean isAutoIncrement();

    boolean isReadable();

    boolean isUpdatable();

    boolean isInsertable();

    boolean isReadOnly();

    boolean isList();

    boolean isSet();

    boolean isCollection();

    @SuppressWarnings("unchecked")
    <T> Collection<T> asCollection(T... entities);

    <T> Collection<T> asCollection(Collection<T> entities);

    Method getSetMethod(Class<?> clazz);

    Method getGetMethod(Class<?> clazz);

    <T> T valueOf(String st);

    String stringOf(Object propvalue);

    Map<String, String> getAttributes();

    String getAttribute(String attrName);

    @SuppressWarnings("rawtypes")
    IdGenerator getIdGenerator();

    EntityDefinition getEntityDefinition();
}

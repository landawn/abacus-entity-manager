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

package com.landawn.abacus.metadata.sql;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.idGenerator.AutoIncrementIdGenerator;
import com.landawn.abacus.idGenerator.IdGenerator;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.metadata.Association;
import com.landawn.abacus.metadata.ColumnType;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.PropertyEle;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.OnDeleteAction;
import com.landawn.abacus.metadata.OnUpdateAction;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.type.ObjectType;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.type.TypeFactory;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.ImmutableList;
import com.landawn.abacus.util.ImmutableMap;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.StringUtil;
import com.landawn.abacus.util.WD;
import com.landawn.abacus.validator.Validator;
import com.landawn.abacus.validator.ValidatorFactory;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLProperty implements Property {
    private static final Logger logger = LoggerFactory.getLogger(SQLProperty.class);

    private final String name;
    private final String canonicalName;
    private final Map<String, String> attrs;
    private Type<Object> type;
    private boolean isSysTimeDefaultOnInsert = false;
    private boolean isSysTimeDefaultOnUpdate = false;
    private Object defaultOnInsert;
    private Object defaultOnUpdate;
    private final String columnName;
    private final String canonicalColumnName;
    private final ColumnType columnType;
    private EntityDefinition columnEntityDef;
    private final OnUpdateAction onUpdateAction;
    private final OnDeleteAction onDeleteAction;
    private final String orderBy;
    private List<Validator<Object>> validatorList;
    private Association association;
    private final boolean isId;
    private final boolean isUID;
    private final boolean isReadable;
    private final boolean isUpdatable;
    private final boolean isInsertable;
    private final boolean isList;
    private final boolean isSet;
    private Map<String, Property> subPropMap;
    private List<String> subPropNameList;
    private List<Property> subPropList;
    private final Map<Class<?>, Method> setMethodMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Method> getMethodMap = new ConcurrentHashMap<>();
    private IdGenerator<?> idGenerator;
    private boolean isAutoIncrement = false;
    volatile EntityDefinition entityDef;
    private volatile boolean isInit = false;

    protected SQLProperty(String entityName, String tableName, String name, Map<String, String> attrs, boolean isId, boolean isUID) {
        this.name = NameUtil.getCachedName(name);
        attrs.put(PropertyEle.NAME, this.name);

        this.canonicalName = NameUtil.getCachedName(entityName + "." + name);

        columnType = N.notNullOrEmpty(attrs.get(PropertyEle.JOIN_ON)) ? ColumnType.ENTITY : ColumnType.TABLE_COLUMN;

        if (columnType.isEntity()) {
            columnName = NameUtil.getCachedName(attrs.get(PropertyEle.TYPE));
            canonicalColumnName = columnName;
        } else {
            if (N.isNullOrEmpty(attrs.get(PropertyEle.COLUMN))) {
                columnName = this.name;
            } else {
                columnName = NameUtil.getCachedName(attrs.get(PropertyEle.COLUMN));
            }

            canonicalColumnName = NameUtil.getCachedName(tableName + "." + columnName);
        }

        attrs.put(PropertyEle.COLUMN, columnName);
        this.attrs = ImmutableMap.of(new LinkedHashMap<>(attrs));

        String actionOnUpdateAttr = getAttribute(PropertyEle.ACTION_ON_UPDATE);
        String actionOnDeleteAttr = getAttribute(PropertyEle.ACTION_ON_DELETE);

        if (((actionOnUpdateAttr != null) || (actionOnDeleteAttr != null)) && (columnType != ColumnType.ENTITY)) {
            throw new AbacusException("Can't set 'constraint' attribute for non-entity property(" + getName() + "). ");
        }

        if (actionOnUpdateAttr != null) {
            throw new AbacusException("Constraint on update is not supported currently.");
        }

        onUpdateAction = columnType.isEntity() ? ((actionOnUpdateAttr == null) ? OnUpdateAction.NO_ACTION : OnUpdateAction.get(actionOnUpdateAttr)) : null;

        onDeleteAction = columnType.isEntity() ? ((actionOnDeleteAttr == null) ? OnDeleteAction.NO_ACTION : OnDeleteAction.get(actionOnDeleteAttr)) : null;

        this.isId = isId;
        this.isUID = isUID;

        String attr = this.attrs.get(PropertyEle.READABLE);
        isReadable = (N.isNullOrEmpty(attr)) ? true : Boolean.valueOf(attr);

        boolean isReadOnly = Boolean.valueOf(this.attrs.get(PropertyEle.READ_ONLY));

        attr = this.attrs.get(PropertyEle.INSERTABLE);
        isInsertable = (attr == null) ? (isReadOnly ? false : true) : Boolean.valueOf(attr);

        if (isReadOnly && isInsertable) {
            throw new AbacusException("Can't set both 'insertable=true' and 'readOnly=true' attributes");
        }

        attr = this.attrs.get(PropertyEle.UPDATABLE);
        isUpdatable = (attr == null) ? (isReadOnly ? false : true) : Boolean.valueOf(attr);

        if (isReadOnly && isUpdatable) {
            throw new AbacusException("Can't set both 'isUpdatable=true' and 'readOnly=true' attributes");
        }

        String collection = this.attrs.get(PropertyEle.COLLECTION);

        if (PropertyEle.LIST.equals(collection)) {
            isList = true;
            isSet = false;
        } else if (PropertyEle.SET.equals(collection)) {
            isSet = true;
            isList = false;
        } else {
            isList = false;
            isSet = false;
        }

        orderBy = this.attrs.get(PropertyEle.ORDER_BY);

        if (orderBy != null) {
            if (!isCollection()) {
                throw new AbacusException("Only list or set property supports sort attribute.");
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public Type<Object> getType() {
        init();

        return type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDefaultOnInsert() {
        init();

        if (isSysTimeDefaultOnInsert) {
            return (T) type.valueOf(PropertyEle.SYS_TIME);
        } else {
            return (T) defaultOnInsert;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDefaultOnUpdate() {
        init();

        if (isSysTimeDefaultOnUpdate) {
            return (T) type.valueOf(PropertyEle.SYS_TIME);
        } else {
            return (T) defaultOnUpdate;
        }
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getCanonicalColumnName() {
        return canonicalColumnName;
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }

    @Override
    public EntityDefinition getColumnEntityDef() {
        init();

        return columnEntityDef;
    }

    @Override
    public OnUpdateAction getOnUpdateAction() {
        return onUpdateAction;
    }

    @Override
    public OnDeleteAction getOnDeleteAction() {
        return onDeleteAction;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public Association getAssociation() {
        init();

        return association;
    }

    @Override
    public List<Validator<Object>> getValidatorList() {
        init();

        return validatorList;
    }

    @Override
    public List<Property> getSubPropertyList() {
        init();

        return subPropList;
    }

    @Override
    public List<String> getSubPropertyNameList() {
        init();

        return subPropNameList;
    }

    @Override
    public Property getSubProperty(String propName) {
        init();

        return subPropMap.get(propName);
    }

    @Override
    public boolean isId() {
        return isId;
    }

    @Override
    public boolean isUID() {
        return isUID;
    }

    @Override
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    @Override
    public boolean isReadable() {
        return isReadable;
    }

    @Override
    public boolean isUpdatable() {
        return isUpdatable;
    }

    @Override
    public boolean isInsertable() {
        return isInsertable;
    }

    @Override
    public boolean isReadOnly() {
        return !(isInsertable || isUpdatable);
    }

    @Override
    public boolean isList() {
        return isList;
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public boolean isCollection() {
        return isList || isSet;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SafeVarargs
    public final <T> Collection<T> asCollection(T... entities) {
        if (isSet()) {
            return N.asLinkedHashSet(entities);
        } else {
            return N.asList(entities);
        }
    }

    @Override
    public <T> Collection<T> asCollection(Collection<T> entities) {
        if (isSet()) {
            return new LinkedHashSet<>(entities);
        } else {
            return new ArrayList<>(entities);
        }
    }

    @Override
    public Method getGetMethod(Class<?> clazz) {
        Method getMethod = getMethodMap.get(clazz);

        if (getMethod == null) {
            getMethod = ClassUtil.getPropGetMethod(clazz, name);

            getMethod.setAccessible(true);
            getMethodMap.put(clazz, getMethod);
        }

        return getMethod;
    }

    @Override
    public Method getSetMethod(Class<?> clazz) {
        Method setMethod = setMethodMap.get(clazz);

        if (setMethod == null) {
            setMethod = ClassUtil.getPropSetMethod(clazz, name);

            // Class parameterType = getType().getTypeClass();
            //
            // if (MapEntity.class.isAssignableFrom(parameterType)) {
            // parameterType = getGetMethod(clazz).getReturnType();
            // }
            setMethod.setAccessible(true);
            setMethodMap.put(clazz, setMethod);
        }

        return setMethod;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(String st) {
        return (T) getType().valueOf(st);
    }

    @Override
    public String stringOf(Object propVlaue) {
        return getType().stringOf(propVlaue);
    }

    @Override
    public Map<String, String> getAttributes() {
        return attrs;
    }

    @Override
    public String getAttribute(String attrName) {
        return attrs.get(attrName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    void setIdGenerator(IdGenerator<?> idGenerator) {
        this.idGenerator = idGenerator;
        this.isAutoIncrement = idGenerator instanceof AutoIncrementIdGenerator;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return entityDef;
    }

    void setEntityDefinition(EntityDefinition entityDef) {
        this.entityDef = entityDef;
    }

    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLProperty && N.equals(((SQLProperty) obj).canonicalName, canonicalName));
    }

    @Override
    public String toString() {
        return attrs.toString();
    }

    protected void init() {
        if (!isInit) {
            synchronized (this) {
                if (!isInit) {
                    // initialize type
                    String typeName = attrs.get(PropertyEle.TYPE);

                    if (columnType.isEntity()) {
                        EntityDefinitionFactory entityDefFactory = entityDef.getFactory();

                        if (entityDefFactory != null) {
                            columnEntityDef = entityDefFactory.getDefinition(typeName);
                        }

                        typeName = columnEntityDef.getJavaType();
                    }

                    if (isList) {
                        typeName = List.class.getName() + WD._LESS_THAN + typeName + WD._GREATER_THAN;
                    } else if (isSet) {
                        typeName = Set.class.getName() + WD._LESS_THAN + typeName + WD._GREATER_THAN;
                    }

                    final String pkgName = entityDef.getAttribute(EntityDefEle.PACKAGE);
                    type = getRealType(typeName, pkgName);

                    // initialize defaultOnInsert
                    String defaultOnInsertAttr = attrs.get(PropertyEle.DEFAULT_ON_INSERT);

                    if (defaultOnInsertAttr != null) {
                        if (PropertyEle.SYS_TIME.equals(defaultOnInsertAttr) && Date.class.isAssignableFrom(type.clazz())) {
                            isSysTimeDefaultOnInsert = true;
                            defaultOnInsert = null;
                        } else {
                            defaultOnInsert = type.valueOf(defaultOnInsertAttr);
                        }
                    } else {
                        defaultOnInsert = type.defaultValue();
                    }

                    // initialize defaultOnUpdate
                    String defaultOnUpdateAttr = attrs.get(PropertyEle.DEFAULT_ON_UPDATE);

                    if (defaultOnUpdateAttr != null) {
                        if (PropertyEle.SYS_TIME.equals(defaultOnUpdateAttr) && Date.class.isAssignableFrom(type.clazz())) {
                            isSysTimeDefaultOnUpdate = true;
                            defaultOnUpdate = null;
                        } else {
                            defaultOnUpdate = type.valueOf(defaultOnUpdateAttr);
                        }
                    } else {
                        defaultOnUpdate = type.defaultValue();
                    }

                    // initialize validatorList
                    List<Validator<Object>> validatorList = new ArrayList<>();
                    String validatorAttr = attrs.get(PropertyEle.VALIDATOR);

                    if (validatorAttr != null) {
                        List<String> validatorElementList = parseValidatorAttr(validatorAttr);

                        for (String validatorElement : validatorElementList) {
                            validatorList.add(ValidatorFactory.create(name, type, validatorElement));
                        }
                    }

                    this.validatorList = ImmutableList.of(validatorList);

                    // init association.
                    String joinOnAttr = attrs.get(PropertyEle.JOIN_ON);

                    if (joinOnAttr != null) {
                        association = new Association(this, joinOnAttr);
                    }

                    Map<String, Property> subPropMap = new HashMap<>();
                    List<String> subPropNameList = new ArrayList<>();
                    List<Property> subPropList = new ArrayList<>();

                    if (columnType.isEntity()) {
                        for (Property subProp : columnEntityDef.getDefaultLoadPropertyList()) {
                            if (subProp.getColumnType() != ColumnType.ENTITY) {
                                final String fullName = NameUtil.getCachedName(columnEntityDef.getName() + WD.PERIOD + subProp.getName());

                                subPropMap.put(subProp.getName(), subProp);
                                subPropMap.put(fullName, subProp);
                                subPropNameList.add(fullName);
                                subPropList.add(subProp);
                            }
                        }
                    }

                    this.subPropMap = ImmutableMap.of(subPropMap);
                    this.subPropNameList = ImmutableList.of(subPropNameList);
                    this.subPropList = ImmutableList.of(subPropList);

                    isInit = true;
                }
            }
        }
    }

    private Type<Object> getRealType(String typeName, String pkgName) {
        Type<Object> type = TypeFactory.getType(typeName);

        if (N.isNullOrEmpty(pkgName)) {
            return type;
        }

        if (Object.class.equals(type.clazz()) && !typeName.equals(ObjectType.OBJECT) && typeName.indexOf('.') < 0) {
            String className = typeName;

            try {
                try {
                    ClassUtil.forClass(className);
                } catch (Exception e) {
                    // ignore
                    className = pkgName + "." + typeName;
                }

                Class<?> cls = ClassUtil.forClass(className);

                if (cls != null) {
                    return TypeFactory.getType(cls);
                }
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("No class found by name: " + className + ". Please run it again after the generated codes are compiled");
                }
            }
        } else if (N.notNullOrEmpty(type.getParameterTypes())) {
            final Type<?>[] parameterTypes = type.getParameterTypes();

            for (int i = 0; i < parameterTypes.length; i++) {
                final Type<?> pt = parameterTypes[i];

                if (Object.class.equals(pt.clazz()) || N.notNullOrEmpty(pt.getParameterTypes())) {
                    final int beginIndex = typeName.indexOf('<');
                    final int endIndex = typeName.lastIndexOf('>');

                    String newTypeName = typeName.substring(0, beginIndex + 1);
                    boolean isFirstParameterType = true;
                    int bracketNum = 0;

                    for (int idx = beginIndex + 1, previousIndex = idx; idx < endIndex; idx++) {
                        final char ch = typeName.charAt(idx);

                        if (ch == '<') {
                            bracketNum++;

                            continue;
                        }

                        if (bracketNum > 0) {
                            if (ch == '>') {
                                bracketNum--;
                            }
                        }

                        if (bracketNum == 0 && (ch == ',' || idx == endIndex - 1)) {
                            String paraTypeName = StringUtil
                                    .trim(ch == ',' ? typeName.substring(previousIndex, idx) : typeName.substring(previousIndex, idx + 1));

                            if (isFirstParameterType) {
                                newTypeName += getRealType(paraTypeName, pkgName).name();

                                isFirstParameterType = false;
                            } else {
                                newTypeName += ", " + getRealType(paraTypeName, pkgName).name();
                            }

                            previousIndex = idx + 1;
                        }
                    }

                    newTypeName += ">";

                    return TypeFactory.getType(newTypeName);
                }
            }
        }

        return type;
    }

    private List<String> parseValidatorAttr(String validatorAttr) {
        List<String> validatorElementList = new ArrayList<>();
        boolean inQuotes = false;

        for (int beginIndex = 0, index = 0; index < validatorAttr.length(); index++) {
            char ch = validatorAttr.charAt(index);

            if (ch == WD._PARENTHESES_L) {
                inQuotes = true;
            } else if (ch == WD._PARENTHESES_R) {
                inQuotes = false;

                if (index == (validatorAttr.length() - 1)) {
                    validatorElementList.add(validatorAttr.substring(beginIndex, index + 1).trim());
                }
            } else if ((ch == WD._SEMICOLON) && !inQuotes) {
                validatorElementList.add(validatorAttr.substring(beginIndex, index).trim());
                beginIndex = index + 1;
            } else if (index == (validatorAttr.length() - 1)) {
                validatorElementList.add(validatorAttr.substring(beginIndex, index + 1).trim());
            }
        }

        return validatorElementList;
    }
}

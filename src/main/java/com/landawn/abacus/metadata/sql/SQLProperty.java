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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.core.NameUtil;
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

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLProperty implements Property {
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SQLProperty.class);

    /** The name. */
    private final String name;
    
    /** The canonical name. */
    private final String canonicalName;
    
    /** The attrs. */
    private final Map<String, String> attrs;
    
    /** The type. */
    private Type<Object> type;
    
    /** The is sys time default on insert. */
    private boolean isSysTimeDefaultOnInsert = false;
    
    /** The is sys time default on update. */
    private boolean isSysTimeDefaultOnUpdate = false;
    
    /** The default on insert. */
    private Object defaultOnInsert;
    
    /** The default on update. */
    private Object defaultOnUpdate;
    
    /** The column name. */
    private final String columnName;
    
    /** The canonical column name. */
    private final String canonicalColumnName;
    
    /** The column type. */
    private final ColumnType columnType;
    
    /** The column entity def. */
    private EntityDefinition columnEntityDef;
    
    /** The on update action. */
    private final OnUpdateAction onUpdateAction;
    
    /** The on delete action. */
    private final OnDeleteAction onDeleteAction;
    
    /** The order by. */
    private final String orderBy;
    
    /** The validator list. */
    private List<Validator<Object>> validatorList;
    
    /** The association. */
    private Association association;
    
    /** The is id. */
    private final boolean isId;
    
    /** The is UID. */
    private final boolean isUID;
    
    /** The is readable. */
    private final boolean isReadable;
    
    /** The is updatable. */
    private final boolean isUpdatable;
    
    /** The is insertable. */
    private final boolean isInsertable;
    
    /** The is list. */
    private final boolean isList;
    
    /** The is set. */
    private final boolean isSet;
    
    /** The sub prop map. */
    private Map<String, Property> subPropMap;
    
    /** The sub prop name list. */
    private List<String> subPropNameList;
    
    /** The sub prop list. */
    private List<Property> subPropList;
    
    /** The set method map. */
    private final Map<Class<?>, Method> setMethodMap = new ConcurrentHashMap<>();
    
    /** The get method map. */
    private final Map<Class<?>, Method> getMethodMap = new ConcurrentHashMap<>();
    
    /** The id generator. */
    private IdGenerator<?> idGenerator;
    
    /** The is auto increment. */
    private boolean isAutoIncrement = false;
    
    /** The entity def. */
    volatile EntityDefinition entityDef;
    
    /** The is init. */
    private volatile boolean isInit = false;

    /**
     * Instantiates a new SQL property.
     *
     * @param entityName
     * @param tableName
     * @param name
     * @param attrs
     * @param isId
     * @param isUID
     */
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
            throw new RuntimeException("Can't set 'constraint' attribute for non-entity property(" + getName() + "). ");
        }

        if (actionOnUpdateAttr != null) {
            throw new RuntimeException("Constraint on update is not supported currently.");
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
            throw new RuntimeException("Can't set both 'insertable=true' and 'readOnly=true' attributes");
        }

        attr = this.attrs.get(PropertyEle.UPDATABLE);
        isUpdatable = (attr == null) ? (isReadOnly ? false : true) : Boolean.valueOf(attr);

        if (isReadOnly && isUpdatable) {
            throw new RuntimeException("Can't set both 'isUpdatable=true' and 'readOnly=true' attributes");
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
                throw new RuntimeException("Only list or set property supports sort attribute.");
            }
        }
    }

    /**
     * Gets the name.
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the canonical name.
     *
     * @return
     */
    @Override
    public String getCanonicalName() {
        return canonicalName;
    }

    /**
     * Gets the type.
     *
     * @return
     */
    @Override
    public Type<Object> getType() {
        init();

        return type;
    }

    /**
     * Gets the default on insert.
     *
     * @param <T>
     * @return
     */
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

    /**
     * Gets the default on update.
     *
     * @param <T>
     * @return
     */
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

    /**
     * Gets the column name.
     *
     * @return
     */
    @Override
    public String getColumnName() {
        return columnName;
    }

    /**
     * Gets the canonical column name.
     *
     * @return
     */
    @Override
    public String getCanonicalColumnName() {
        return canonicalColumnName;
    }

    /**
     * Gets the column type.
     *
     * @return
     */
    @Override
    public ColumnType getColumnType() {
        return columnType;
    }

    /**
     * Gets the column entity def.
     *
     * @return
     */
    @Override
    public EntityDefinition getColumnEntityDef() {
        init();

        return columnEntityDef;
    }

    /**
     * Gets the on update action.
     *
     * @return
     */
    @Override
    public OnUpdateAction getOnUpdateAction() {
        return onUpdateAction;
    }

    /**
     * Gets the on delete action.
     *
     * @return
     */
    @Override
    public OnDeleteAction getOnDeleteAction() {
        return onDeleteAction;
    }

    /**
     * Gets the order by.
     *
     * @return
     */
    @Override
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Gets the association.
     *
     * @return
     */
    @Override
    public Association getAssociation() {
        init();

        return association;
    }

    /**
     * Gets the validator list.
     *
     * @return
     */
    @Override
    public List<Validator<Object>> getValidatorList() {
        init();

        return validatorList;
    }

    /**
     * Gets the sub property list.
     *
     * @return
     */
    @Override
    public List<Property> getSubPropertyList() {
        init();

        return subPropList;
    }

    /**
     * Gets the sub property name list.
     *
     * @return
     */
    @Override
    public List<String> getSubPropertyNameList() {
        init();

        return subPropNameList;
    }

    /**
     * Gets the sub property.
     *
     * @param propName
     * @return
     */
    @Override
    public Property getSubProperty(String propName) {
        init();

        return subPropMap.get(propName);
    }

    /**
     * Checks if is id.
     *
     * @return true, if is id
     */
    @Override
    public boolean isId() {
        return isId;
    }

    /**
     * Checks if is uid.
     *
     * @return true, if is uid
     */
    @Override
    public boolean isUID() {
        return isUID;
    }

    /**
     * Checks if is auto increment.
     *
     * @return true, if is auto increment
     */
    @Override
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    /**
     * Checks if is readable.
     *
     * @return true, if is readable
     */
    @Override
    public boolean isReadable() {
        return isReadable;
    }

    /**
     * Checks if is updatable.
     *
     * @return true, if is updatable
     */
    @Override
    public boolean isUpdatable() {
        return isUpdatable;
    }

    /**
     * Checks if is insertable.
     *
     * @return true, if is insertable
     */
    @Override
    public boolean isInsertable() {
        return isInsertable;
    }

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    @Override
    public boolean isReadOnly() {
        return !(isInsertable || isUpdatable);
    }

    /**
     * Checks if is list.
     *
     * @return true, if is list
     */
    @Override
    public boolean isList() {
        return isList;
    }

    /**
     * Checks if is sets the.
     *
     * @return true, if is sets the
     */
    @Override
    public boolean isSet() {
        return isSet;
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    @Override
    public boolean isCollection() {
        return isList || isSet;
    }

    /**
     *
     * @param <T>
     * @param entities
     * @return
     */
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

    /**
     *
     * @param <T>
     * @param entities
     * @return
     */
    @Override
    public <T> Collection<T> asCollection(Collection<T> entities) {
        if (isSet()) {
            return N.newLinkedHashSet(entities);
        } else {
            return new ArrayList<>(entities);
        }
    }

    /**
     * Gets the gets the method.
     *
     * @param clazz
     * @return
     */
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

    /**
     * Gets the sets the method.
     *
     * @param clazz
     * @return
     */
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

    /**
     *
     * @param <T>
     * @param st
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(String st) {
        return (T) getType().valueOf(st);
    }

    /**
     *
     * @param propVlaue
     * @return
     */
    @Override
    public String stringOf(Object propVlaue) {
        return getType().stringOf(propVlaue);
    }

    /**
     * Gets the attributes.
     *
     * @return
     */
    @Override
    public Map<String, String> getAttributes() {
        return attrs;
    }

    /**
     * Gets the attribute.
     *
     * @param attrName
     * @return
     */
    @Override
    public String getAttribute(String attrName) {
        return attrs.get(attrName);
    }

    /**
     * Gets the id generator.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    /**
     * Sets the id generator.
     *
     * @param idGenerator the new id generator
     */
    void setIdGenerator(IdGenerator<?> idGenerator) {
        this.idGenerator = idGenerator;
        this.isAutoIncrement = idGenerator instanceof AutoIncrementIdGenerator;
    }

    /**
     * Gets the entity definition.
     *
     * @return
     */
    @Override
    public EntityDefinition getEntityDefinition() {
        return entityDef;
    }

    /**
     * Sets the entity definition.
     *
     * @param entityDef the new entity definition
     */
    void setEntityDefinition(EntityDefinition entityDef) {
        this.entityDef = entityDef;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }

    /**
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLProperty && N.equals(((SQLProperty) obj).canonicalName, canonicalName));
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return attrs.toString();
    }

    /**
     * Inits the.
     */
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

    /**
     * Gets the real type.
     *
     * @param typeName
     * @param pkgName
     * @return
     */
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

    /**
     * Parses the validator attr.
     *
     * @param validatorAttr
     * @return
     */
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

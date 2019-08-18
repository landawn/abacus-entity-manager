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

import static com.landawn.abacus.util.WD._PERIOD;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.idGenerator.AutoIncrementIdGenerator;
import com.landawn.abacus.idGenerator.IdGenerator;
import com.landawn.abacus.idGenerator.IdGeneratorFactory;
import com.landawn.abacus.metadata.Column;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle.PropertyEle;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.metadata.Table;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.ImmutableList;
import com.landawn.abacus.util.ImmutableMap;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Primitives;
import com.landawn.abacus.util.XMLUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLEntityDefinition.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLEntityDefinition implements EntityDefinition {
    
    /** The java type. */
    private final String javaType;
    
    /** The type clazz. */
    private final Class<?> typeClazz;
    
    /** The array type clazz. */
    private final Class<?> arrayTypeClazz;
    
    /** The name. */
    private final String name;
    
    /** The table name. */
    private final String tableName;
    
    /** The attrs. */
    private final Map<String, String> attrs;
    
    /** The prop pool. */
    private final Map<String, Property> propPool = new ConcurrentHashMap<>();
    
    /** The prop list. */
    private final List<Property> propList;
    
    /** The prop name list. */
    private final List<String> propNameList;
    
    /** The id prop list. */
    private final List<Property> idPropList;
    
    /** The id prop name list. */
    private final List<String> idPropNameList;
    
    /** The uid prop list. */
    private final List<Property> uidPropList;
    
    /** The uid prop name list. */
    private final List<String> uidPropNameList;
    
    /** The entity prop list. */
    private final List<Property> entityPropList;
    
    /** The default load prop list. */
    private final List<Property> defaultLoadPropList;
    
    /** The default load prop name list. */
    private final List<String> defaultLoadPropNameList;
    
    /** The default on insert prop list. */
    private final List<Property> defaultOnInsertPropList;
    
    /** The default on update prop list. */
    private final List<Property> defaultOnUpdatePropList;
    
    /** The id generator map. */
    private final Map<String, IdGenerator<Object>> idGeneratorMap;
    
    /** The id generator list. */
    private final List<IdGenerator<Object>> idGeneratorList;
    
    /** The slice entity def list. */
    private final List<EntityDefinition> sliceEntityDefList;
    
    /** The is id auto generated. */
    private final boolean isIdAutoGenerated;
    
    /** The is slice entity. */
    private final boolean isSliceEntity;
    
    /** The parent entity. */
    private EntityDefinition parentEntity;
    
    /** The factory. */
    private EntityDefinitionFactory factory;

    /**
     * Instantiates a new SQL entity definition.
     *
     * @param name
     * @param table
     * @param columnName2PropName
     */
    public SQLEntityDefinition(String name, Table table, Method columnName2PropName) {
        this(null, Object.class, name, table.getName(), false, parse(name, table, columnName2PropName));
    }

    /**
     * Instantiates a new SQL entity definition.
     *
     * @param factory
     * @param pkgName
     * @param is
     */
    protected SQLEntityDefinition(EntityDefinitionFactory factory, String pkgName, InputStream is) {
        this(factory, pkgName, Configuration.parse(is).getDocumentElement());
    }

    /**
     * Instantiates a new SQL entity definition.
     *
     * @param factory
     * @param pkgName
     * @param entityNode
     */
    protected SQLEntityDefinition(EntityDefinitionFactory factory, String pkgName, Element entityNode) {
        this(factory, initClass(pkgName, entityNode), entityNode.getAttribute(EntityEle.NAME), entityNode.getAttribute(EntityEle.TABLE), false,
                parse(factory, pkgName, entityNode));
    }

    /**
     * Instantiates a new SQL entity definition.
     *
     * @param factory
     * @param cls
     * @param name
     * @param tableName
     * @param isSliceEntity
     * @param propsAndSlices
     */
    SQLEntityDefinition(final EntityDefinitionFactory factory, final Class<?> cls, final String name, final String tableName, boolean isSliceEntity,
            final Object[] propsAndSlices) {
        if (N.isNullOrEmpty(name)) {
            throw new AbacusException("Entity name can't be null or empty.");
        }

        // use entity name if table name is null or empty
        //        if (N.isNullOrEmpty(tableName)) {
        //            throw new AbacusException("Table name can't be null or empty.");
        //        }

        final Map<String, String> attrs = (Map<String, String>) propsAndSlices[0];
        final Map<String, SQLProperty> props = (Map<String, SQLProperty>) propsAndSlices[1];
        final List<EntityDefinition> sliceEntityDefList = (List<EntityDefinition>) propsAndSlices[2];

        this.factory = factory;
        this.javaType = ClassUtil.getCanonicalClassName(cls);
        this.typeClazz = cls;
        this.arrayTypeClazz = Array.newInstance(typeClazz, 0).getClass();

        this.name = NameUtil.getCachedName(name);
        attrs.put(EntityEle.NAME, this.name);

        this.tableName = N.isNullOrEmpty(tableName) ? this.name : NameUtil.getCachedName(tableName);
        attrs.put(EntityEle.TABLE, this.tableName);

        this.attrs = ImmutableMap.of(attrs);

        final List<Property> propList = new ArrayList<>();
        final List<String> propNameList = new ArrayList<>();
        final List<Property> idPropList = new ArrayList<>();
        final List<String> idPropNameList = new ArrayList<>();
        final List<Property> uidPropList = new ArrayList<>();
        final List<String> uidPropNameList = new ArrayList<>();
        final List<Property> entityPropList = new ArrayList<>();
        final List<Property> defaultLoadPropList = new ArrayList<>();
        final List<String> defaultLoadPropNameList = new ArrayList<>();
        final List<Property> defaultOnInsertPropList = new ArrayList<>();
        final List<Property> defaultOnUpdatePropList = new ArrayList<>();
        final Map<String, IdGenerator<Object>> idGeneratorMap = new HashMap<>();
        final List<IdGenerator<Object>> idGeneratorList = new ArrayList<>();

        for (SQLProperty prop : props.values()) {
            prop.setEntityDefinition(this);

            propPool.put(prop.getName(), prop);
            propPool.put(prop.getCanonicalName(), prop);

            propList.add(prop);
            propNameList.add(prop.getName());

            if (prop.isId()) {
                idPropList.add(prop);
                idPropNameList.add(prop.getName());

                String idGeneratorAttr = prop.getAttribute(PropertyEle.ID_GENERATOR);

                if (idGeneratorAttr != null) {
                    IdGenerator<Object> idGenerator = null;

                    if (PropertyEle.DEFAULT.equals(idGeneratorAttr)) {
                        idGenerator = IdGeneratorFactory.getDefaultIdGenerator(prop);
                    } else {
                        idGenerator = IdGeneratorFactory.create(idGeneratorAttr, prop);
                    }

                    idGeneratorMap.put(prop.getName(), idGenerator);
                    idGeneratorList.add(idGenerator);

                    prop.setIdGenerator(idGenerator);
                }
            }

            if (prop.isUID()) {
                uidPropList.add(prop);
                uidPropNameList.add(prop.getName());
            }

            if (prop.getColumnType().isEntity()) {
                entityPropList.add(prop);
            }

            String attr = prop.getAttribute(PropertyEle.LOAD_BY_DEFAULT);
            boolean defaultLoad = (attr == null) ? (prop.getColumnType().isEntity() ? false : true) : Boolean.valueOf(attr);

            if (defaultLoad) {
                defaultLoadPropList.add(prop);
                defaultLoadPropNameList.add(prop.getName());
            }

            if (prop.getAttribute(PropertyEle.DEFAULT_ON_INSERT) != null) {
                defaultOnInsertPropList.add(prop);
            }

            if (prop.getAttribute(PropertyEle.DEFAULT_ON_UPDATE) != null) {
                defaultOnUpdatePropList.add(prop);
            }
        }

        this.propList = ImmutableList.of(propList);
        this.propNameList = ImmutableList.of(propNameList);
        this.idPropList = ImmutableList.of(idPropList);
        this.idPropNameList = ImmutableList.of(idPropNameList);
        this.uidPropList = ImmutableList.of(uidPropList);
        this.uidPropNameList = ImmutableList.of(uidPropNameList);
        this.entityPropList = ImmutableList.of(entityPropList);
        this.defaultLoadPropList = ImmutableList.of(defaultLoadPropList);
        this.defaultLoadPropNameList = ImmutableList.of(defaultLoadPropNameList);
        this.defaultOnInsertPropList = ImmutableList.of(defaultOnInsertPropList);
        this.defaultOnUpdatePropList = ImmutableList.of(defaultOnUpdatePropList);
        this.idGeneratorMap = ImmutableMap.of(idGeneratorMap);
        this.idGeneratorList = ImmutableList.of(idGeneratorList);
        this.sliceEntityDefList = ImmutableList.of(sliceEntityDefList);

        for (EntityDefinition sliceEntityDef : this.sliceEntityDefList) {
            ((SQLEntityDefinition) sliceEntityDef).setParentEntity(this);
        }

        boolean hasIdAutoGenerated = false;
        for (IdGenerator<?> idGenerator : idGeneratorList) {
            if (idGenerator instanceof AutoIncrementIdGenerator) {
                if (idPropNameList.size() > 1) {
                    throw new AbacusException(
                            "One entity only has one auto increament id. But entity[" + name + "] has more than one id propertes:" + idPropNameList);
                } else {
                    hasIdAutoGenerated = true;
                }

                break;
            }
        }

        isIdAutoGenerated = hasIdAutoGenerated;

        this.isSliceEntity = isSliceEntity;
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
     * Gets the table name.
     *
     * @return
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the java type.
     *
     * @return
     */
    @Override
    public String getJavaType() {
        return javaType;
    }

    /**
     * Gets the type class.
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getTypeClass() {
        return (Class<T>) typeClazz;
    }

    /**
     * Gets the array type class.
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getArrayTypeClass() {
        return (Class<T>) arrayTypeClazz;
    }

    /**
     * Gets the property list.
     *
     * @return
     */
    @Override
    public List<Property> getPropertyList() {
        return propList;
    }

    /**
     * Gets the property name list.
     *
     * @return
     */
    @Override
    public List<String> getPropertyNameList() {
        return propNameList;
    }

    /**
     * Gets the property.
     *
     * @param propName
     * @return
     */
    @Override
    public Property getProperty(String propName) {
        Property prop = propPool.get(propName);

        if (prop == null) {
            // property name should be case sensitive
            //            for (Property e : propList) {
            //                if (e.getName().equalsIgnoreCase(propName) || (name + _PERIOD + e.getName()).equalsIgnoreCase(propName)) {
            //                    prop = e;
            //
            //                    break;
            //                }
            //            }

            if (prop == null) {
                int beginIndex = propName.indexOf(_PERIOD);

                if (beginIndex > -1) {
                    String entityName = propName.substring(0, beginIndex);

                    if (!name.equals(entityName) && (getFactory() != null)) {
                        EntityDefinition entityDef = getFactory().getDefinition(entityName);

                        if (entityDef != null) {
                            prop = entityDef.getProperty(propName);
                        }
                    }
                }
            }

            if (prop != null) {
                if (prop.getEntityDefinition() == this.getParentEntity()) {
                    prop = this.getProperty(prop.getName());
                }

                if (prop.getName().equals(propName)) {
                    propPool.put(prop.getName(), prop);
                } else {
                    propPool.put(NameUtil.getCachedName(propName), prop);
                }
            }
        }

        return prop;
    }

    /**
     * Gets the id property list.
     *
     * @return
     */
    @Override
    public List<Property> getIdPropertyList() {
        return idPropList;
    }

    /**
     * Gets the id property name list.
     *
     * @return
     */
    @Override
    public List<String> getIdPropertyNameList() {
        return idPropNameList;
    }

    /**
     * Gets the UID property list.
     *
     * @return
     */
    @Override
    public List<Property> getUIDPropertyList() {
        return uidPropList;
    }

    /**
     * Gets the UID property name list.
     *
     * @return
     */
    @Override
    public List<String> getUIDPropertyNameList() {
        return uidPropNameList;
    }

    /**
     * Gets the entiy property list.
     *
     * @return
     */
    @Override
    public List<Property> getEntiyPropertyList() {
        return entityPropList;
    }

    /**
     * Gets the default load property list.
     *
     * @return
     */
    @Override
    public List<Property> getDefaultLoadPropertyList() {
        return defaultLoadPropList;
    }

    /**
     * Gets the default load property name list.
     *
     * @return
     */
    @Override
    public List<String> getDefaultLoadPropertyNameList() {
        return defaultLoadPropNameList;
    }

    /**
     * Gets the default on insert property list.
     *
     * @return
     */
    @Override
    public List<Property> getDefaultOnInsertPropertyList() {
        return defaultOnInsertPropList;
    }

    /**
     * Gets the default on update property list.
     *
     * @return
     */
    @Override
    public List<Property> getDefaultOnUpdatePropertyList() {
        return defaultOnUpdatePropList;
    }

    /**
     * Checks if is id auto generated.
     *
     * @return true, if is id auto generated
     */
    @Override
    public boolean isIdAutoGenerated() {
        return isIdAutoGenerated;
    }

    /**
     * Gets the id generator list.
     *
     * @return
     */
    @Override
    public List<IdGenerator<Object>> getIdGeneratorList() {
        return idGeneratorList;
    }

    /**
     * Gets the id generator.
     *
     * @param idPropName
     * @return
     */
    @Override
    public IdGenerator<Object> getIdGenerator(String idPropName) {
        return idGeneratorMap.get(idPropName);
    }

    /**
     * Checks if is slice entity.
     *
     * @return true, if is slice entity
     */
    @Override
    public boolean isSliceEntity() {
        return isSliceEntity;
    }

    /**
     * Gets the slice entity list.
     *
     * @return
     */
    @Override
    public List<EntityDefinition> getSliceEntityList() {
        return sliceEntityDefList;
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
        String attr = attrs.get(attrName);

        if ((attr == null) && (factory != null)) {
            attr = factory.getAttribute(attrName);
        }

        return attr;
    }

    /**
     * Gets the parent entity.
     *
     * @return
     */
    @Override
    public EntityDefinition getParentEntity() {
        return parentEntity;
    }

    /**
     * Sets the parent entity.
     *
     * @param entityDef the new parent entity
     */
    void setParentEntity(EntityDefinition entityDef) {
        this.parentEntity = entityDef;
    }

    /**
     * Gets the factory.
     *
     * @return
     */
    @Override
    public EntityDefinitionFactory getFactory() {
        return factory;
    }

    /**
     * Sets the factory.
     *
     * @param factory the new factory
     */
    void setFactory(EntityDefinitionFactory factory) {
        this.factory = factory;
    }

    /**
     * Hash code.
     *
     * @return
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Equals.
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SQLEntityDefinition && N.equals(((SQLEntityDefinition) obj).name, name));
    }

    /**
     * To string.
     *
     * @return
     */
    @Override
    public String toString() {
        return attrs.toString();
    }

    /**
     * Parses the.
     *
     * @param entityName
     * @param table
     * @param columnName2PropName
     * @return
     */
    private static Object[] parse(final String entityName, final Table table, final Method columnName2PropName) {
        if (columnName2PropName != null) {
            try {
                columnName2PropName.setAccessible(true);
            } catch (Exception e) {
                throw N.toRuntimeException(e);
            }
        }

        final Map<String, SQLProperty> props = new LinkedHashMap<>();
        final Collection<Column> columnList = table.getColumnList();

        String propName = null;

        for (Column column : columnList) {
            propName = columnName2PropName == null ? column.getName() : (String) ClassUtil.invokeMethod(columnName2PropName, column.getName());

            Map<String, String> attrs = new HashMap<>();
            attrs.put(PropertyEle.COLUMN, column.getName());

            Type<?> columnJavaType = N.typeOf(column.getJavaType());

            if (columnJavaType.isPrimitiveWrapper()) {
                attrs.put(PropertyEle.TYPE, ClassUtil.getSimpleClassName(Primitives.unwrap(columnJavaType.clazz())));
            } else {
                attrs.put(PropertyEle.TYPE, column.getJavaType());
            }

            //            if (column.isReadOnly()) {
            //                attrs.put(PropertyEle.READ_ONLY, Boolean.TRUE.toString());
            //            } else if (!column.isWritable()) {
            //                attrs.put(PropertyEle.INSERTABLE, Boolean.FALSE.toString());
            //            }

            boolean isId = false;
            boolean isUID = false;

            if (column.isPrimaryKey()) {
                if (column.isAutoIncrement()) {
                    attrs.put(PropertyEle.ID_GENERATOR, AutoIncrementIdGenerator.class.getSimpleName().replaceAll(IdGenerator.ID_GENERATOR, N.EMPTY_STRING));
                } else {
                    attrs.put(PropertyEle.ID_GENERATOR, PropertyEle.DEFAULT);
                }

                isId = true;
            }

            SQLProperty prop = new SQLProperty(entityName, table.getName(), propName, attrs, isId, isUID);
            props.put(prop.getName(), prop);
        }

        return new Object[] { new HashMap<>(), props, new ArrayList<>() };
    }

    /**
     * Parses the.
     *
     * @param factory
     * @param pkgName
     * @param entityElement
     * @return
     */
    private static Object[] parse(EntityDefinitionFactory factory, String pkgName, Element entityElement) {
        return new Object[] { XMLUtil.readAttributes(entityElement),
                parseProperties(entityElement.getAttribute(EntityEle.NAME), entityElement.getAttribute(EntityEle.TABLE), entityElement, false),
                parseSlices(factory, pkgName, entityElement) };
    }

    /**
     * Parses the properties.
     *
     * @param entityName
     * @param tableName
     * @param entityElement
     * @param isSliceEntity
     * @return
     */
    private static Map<String, SQLProperty> parseProperties(String entityName, String tableName, Element entityElement, boolean isSliceEntity) {
        final Map<String, SQLProperty> props = new LinkedHashMap<>();
        final String idAttrs = entityElement.getAttribute(EntityEle.ID);
        final String uidAttrs = entityElement.getAttribute(EntityEle.UID);

        final List<Element> propsElementList = XMLUtil.getElementsByTagName(entityElement, EntityEle.PROPERTIES);
        if (propsElementList.size() > 0) {
            final NodeList propNodeList = propsElementList.get(0).getChildNodes();
            for (int i = 0; i < propNodeList.getLength(); i++) {
                final Node propNode = propNodeList.item(i);

                if (propNode.getNodeType() != Document.ELEMENT_NODE) {
                    continue;
                }

                final Map<String, String> attrs = XMLUtil.readAttributes(propNode);
                final String propName = attrs.get(PropertyEle.NAME);

                if (PropertyEle.LIST.equals(propNode.getNodeName())) {
                    attrs.put(PropertyEle.COLLECTION, PropertyEle.LIST);
                } else if (PropertyEle.SET.equals(propNode.getNodeName())) {
                    attrs.put(PropertyEle.COLLECTION, PropertyEle.SET);
                }

                if (isSliceEntity && N.notNullOrEmpty(attrs.get(PropertyEle.JOIN_ON))) {
                    String joinOn = attrs.get(PropertyEle.JOIN_ON);
                    String mainEntityName = entityElement.getAttribute(EntityEle.NAME);
                    joinOn = joinOn.replace(mainEntityName + ".", entityName + ".");
                    attrs.put(PropertyEle.JOIN_ON, joinOn);
                }

                boolean isId = N.notNullOrEmpty(idAttrs) && idAttrs.indexOf(propName) >= 0;
                boolean isUID = N.notNullOrEmpty(uidAttrs) && uidAttrs.indexOf(propName) >= 0;

                final SQLProperty prop = new SQLProperty(entityName, tableName, propName, attrs, isId, isUID);
                props.put(prop.getName(), prop);
            }
        }

        return props;
    }

    /**
     * Parses the slices.
     *
     * @param factory
     * @param pkgName
     * @param entityElement
     * @return
     */
    private static List<EntityDefinition> parseSlices(EntityDefinitionFactory factory, String pkgName, Element entityElement) {
        final List<EntityDefinition> sliceEntityDefList = new ArrayList<>();
        final List<Element> slicesElementList = XMLUtil.getElementsByTagName(entityElement, EntityEle.SLICES);

        if (slicesElementList.size() > 0) {
            for (Element slicesEntityElement : XMLUtil.getElementsByTagName(slicesElementList.get(0), EntityEle.ENTITY)) {
                final Map<String, String> attrs = XMLUtil.readAttributes(entityElement);
                attrs.putAll(XMLUtil.readAttributes(slicesEntityElement));

                sliceEntityDefList.add(new SQLEntityDefinition(factory, initClass(pkgName, entityElement), slicesEntityElement.getAttribute(EntityEle.NAME),
                        slicesEntityElement.getAttribute(EntityEle.TABLE), true,
                        new Object[] { attrs, parseProperties(slicesEntityElement.getAttribute(EntityEle.NAME),
                                slicesEntityElement.getAttribute(EntityEle.TABLE), entityElement, true), new ArrayList<>() }));
            }
        }

        return sliceEntityDefList;
    }

    /** The Constant cachedFieldInterfaceSet. */
    private static final Set<Class<?>> cachedFieldInterfaceSet = new HashSet<>();

    /**
     * Inits the class.
     *
     * @param pkgName
     * @param entityNode
     * @return
     */
    private static Class<?> initClass(String pkgName, Element entityNode) {
        final String entityName = entityNode.getAttribute(EntityEle.NAME);
        pkgName = N.isNullOrEmpty(pkgName) ? entityNode.getAttribute(EntityDefEle.PACKAGE) : pkgName;
        String clsName = entityNode.getAttribute(EntityEle.CLASS);

        if (N.isNullOrEmpty(clsName)) {
            clsName = (N.isNullOrEmpty(pkgName)) ? entityName : (pkgName + _PERIOD + entityName);
        } else if (clsName.indexOf('.') < 0) {
            clsName = (N.isNullOrEmpty(pkgName)) ? clsName : (pkgName + _PERIOD + clsName);
        }

        Class<?> cls = Object.class;

        try {
            cls = ClassUtil.forClass(clsName);
        } catch (Exception e) {
            // ignore.
        }

        if (cls.equals(Object.class)) {
            return cls;
        }

        try {
            final Class<?>[] interfaceClasses = cls.getInterfaces();

            for (Class<?> interfaceClass : interfaceClasses) {
                if (cachedFieldInterfaceSet.contains(interfaceClass)) {
                    continue;
                }

                for (Field field : interfaceClass.getFields()) {
                    int mod = field.getModifiers();

                    if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && String.class.equals(field.getType())) {
                        NameUtil.cacheName(field.get(null).toString(), true);
                    }
                }

                final Class<?> declaringClass = interfaceClass.getDeclaringClass();

                if (!cachedFieldInterfaceSet.contains(declaringClass)) {
                    cachedFieldInterfaceSet.add(declaringClass);

                    for (Field field : declaringClass.getFields()) {
                        int mod = field.getModifiers();

                        if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && String.class.equals(field.getType())) {
                            NameUtil.cacheName(field.get(null).toString(), true);
                        }
                    }
                }
            }

            if (!cachedFieldInterfaceSet.contains(cls)) {
                for (Field field : cls.getFields()) {
                    int mod = field.getModifiers();

                    if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod) && String.class.equals(field.getType())) {
                        NameUtil.cacheName(field.get(null).toString(), false);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }

        return cls;
    }
}

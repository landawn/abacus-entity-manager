/*
 * Copyright (c) 2015, Haiyang Li.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.core;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.condition.And;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.condition.Equal;
import com.landawn.abacus.condition.IsNull;
import com.landawn.abacus.condition.Or;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.EntityCacheConfiguration.CustomizedEntityCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.LockConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.QueryCacheConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.QueryCacheConfiguration.CacheResultConditionConfiguration;
import com.landawn.abacus.exception.UncheckedSQLException;
import com.landawn.abacus.idGenerator.IdGenerator;
import com.landawn.abacus.metadata.Association;
import com.landawn.abacus.metadata.Association.JoinType;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.parser.ParserUtil;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.JdbcUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.ObjectPool;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.Options.Cache;
import com.landawn.abacus.util.Options.Jdbc;
import com.landawn.abacus.util.Options.Query;
import com.landawn.abacus.util.WD;
import com.landawn.abacus.util.u.Holder;
import com.landawn.abacus.validator.Validator;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityManagerUtil.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
public final class EntityManagerUtil {

    /** The Constant clsEntityName. */
    private static final Map<Class<?>, String> clsEntityName = new ObjectPool<Class<?>, String>(1024);

    /**
     * Instantiates a new entity manager util.
     */
    private EntityManagerUtil() {
        // no instance
    }

    /**
     * Copy options.
     *
     * @param options the options
     * @return the map
     */
    public static Map<String, Object> copyOptions(final Map<String, Object> options) {
        return N.isNullOrEmpty(options) ? ParametersUtil.asOptions() : ParametersUtil.copy(options);
    }

    /**
     * Check if the specified parameter is null or empty.
     *
     * @param <T> the generic type
     * @param parameter the parameter
     * @param msg name of parameter or error message
     * @return the input parameter
     * @throws IllegalArgumentException if the specified parameter is null or empty.
     */
    public static <T extends EntityId> T checkArgNotNullOrEmpty(final T parameter, final String msg) {
        if (parameter == null || parameter.isEmpty()) {
            if (isNullErrorMsg(msg)) {
                throw new IllegalArgumentException(msg);
            } else {
                throw new IllegalArgumentException("'" + msg + "' can not be null or empty");
            }
        }

        return parameter;
    }

    /**
     * Checks if is null error msg.
     *
     * @param msg the msg
     * @return true, if is null error msg
     */
    private static boolean isNullErrorMsg(final String msg) {
        // shortest message: "it is null"
        return msg.length() > 9 && msg.indexOf(WD._SPACE) > 0;
    }

    /**
     * Check entity name.
     *
     * @param entityDefinitionFactory the entity definition factory
     * @param entityName the entity name
     * @return the entity definition
     */
    public static EntityDefinition checkEntityName(final EntityDefinitionFactory entityDefinitionFactory, final String entityName) {
        if (N.isNullOrEmpty(entityName)) {
            throw new IllegalArgumentException("The entity name can not be null or empty");
        }

        EntityDefinition entityDef = entityDefinitionFactory.getDefinition(entityName);

        if (entityDef == null) {
            throw new IllegalArgumentException("No definition found for entity: " + entityName);
        }

        return entityDef;
    }

    /**
     * Check entity id.
     *
     * @param entityDefinitionFactory the entity definition factory
     * @param entityId the entity id
     * @return the entity definition
     */
    public static EntityDefinition checkEntityId(final EntityDefinitionFactory entityDefinitionFactory, final EntityId entityId) {
        if ((entityId == null) || entityId.isEmpty()) {
            throw new IllegalArgumentException("EntityId can not be null or empty");
        }

        EntityDefinition entityDef = checkEntityName(entityDefinitionFactory, entityId.entityName());

        for (String propName : entityId.keySet()) {
            if (!entityDef.getProperty(propName).isId()) {
                throw new IllegalArgumentException("Non-definied entity id is found in entityId: " + N.toString(entityId) + ". The defined id properties are: "
                        + entityDef.getIdPropertyNameList());
            }
        }

        return entityDef;
    }

    /**
     * Check entity id.
     *
     * @param entityDefinitionFactory the entity definition factory
     * @param entityIds the entity ids
     * @return the entity definition
     */
    public static EntityDefinition checkEntityId(final EntityDefinitionFactory entityDefinitionFactory, final List<? extends EntityId> entityIds) {
        if (N.isNullOrEmpty(entityIds)) {
            throw new IllegalArgumentException("EntityIds can not be null or empty");
        }

        final String entityName = entityIds.iterator().next().entityName();
        final EntityDefinition entityDef = checkEntityName(entityDefinitionFactory, entityName);

        for (EntityId entityId : entityIds) {
            if ((entityId == null) || entityId.isEmpty()) {
                throw new IllegalArgumentException("entityId can not be null or empty. entityIds: " + N.toString(entityIds));
            }

            if (!entityId.entityName().equals(entityName)) {
                throw new IllegalArgumentException("Can not input different type entity id. entityIds: " + N.toString(entityIds));
            }

            for (String propName : entityId.keySet()) {
                if (!entityDef.getProperty(propName).isId()) {
                    throw new IllegalArgumentException("Non-definied entity id is found in entityId: " + N.toString(entityId)
                            + ". The defined id properties are: " + entityDef.getIdPropertyNameList());
                }
            }
        }

        return entityDef;
    }

    /**
     * Check entity.
     *
     * @param entityDefinitionFactory the entity definition factory
     * @param entity the entity
     * @return the entity definition
     */
    public static EntityDefinition checkEntity(final EntityDefinitionFactory entityDefinitionFactory, final Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The specified entity can not be null.");
        }

        Class<?> cls = entity.getClass();
        String entityName = clsEntityName.get(cls);

        if (entityName == null) {
            if (entity instanceof MapEntity) {
                entityName = ((MapEntity) entity).entityName();
            } else {
                for (EntityDefinition entityDef : entityDefinitionFactory.getDefinitionList()) {
                    if (entityDef.isSliceEntity()) {
                        continue;
                    }

                    if (cls.equals(entityDef.getTypeClass())) {
                        entityName = entityDef.getName();

                        break;
                    }
                }

                if (entityName == null) {
                    String simpleClassName = ClassUtil.getSimpleClassName(cls);

                    for (EntityDefinition entityDef : entityDefinitionFactory.getDefinitionList()) {
                        if (entityDef.isSliceEntity()) {
                            continue;
                        }

                        if (entityDef.getName().equals(simpleClassName)) {
                            entityName = entityDef.getName();
                        }

                        if (entityName != null) {
                            break;
                        }
                    }
                }

                if (entityName != null) {
                    clsEntityName.put(cls, entityName);
                }
            }
        }

        if (entityName == null) {
            throw new IllegalArgumentException("No entity definition found for class: " + ClassUtil.getCanonicalClassName(cls));
        } else {
            return checkEntityName(entityDefinitionFactory, entityName);
        }
    }

    /**
     * Check entity.
     *
     * @param entityDefinitionFactory the entity definition factory
     * @param entities the entities
     * @return the entity definition
     */
    public static EntityDefinition checkEntity(final EntityDefinitionFactory entityDefinitionFactory, final Collection<?> entities) {
        if (N.isNullOrEmpty(entities)) {
            throw new IllegalArgumentException("Entities can not be null or empty");
        }

        final Iterator<?> iterator = entities.iterator();
        final Object first = iterator.next();
        final Class<?> cls = first.getClass();

        while (iterator.hasNext()) {
            if (!iterator.next().getClass().equals(cls)) {
                throw new IllegalArgumentException("Can not input different type entity. entities: " + N.toString(entities));
            }
        }

        return checkEntity(entityDefinitionFactory, first);
    }

    /**
     * Check select prop names for get.
     *
     * @param entityDef the entity def
     * @param selectPropNames the select prop names
     */
    public static void checkSelectPropNamesForGet(final EntityDefinition entityDef, final Collection<String> selectPropNames) {
        if (N.notNullOrEmpty(selectPropNames)) {
            Property prop = null;

            for (String propName : selectPropNames) {
                if (propName == null) {
                    throw new IllegalArgumentException("Undefined selection property name(" + propName + ") is specified for the target entity "
                            + entityDef.getName() + ": " + entityDef.getPropertyNameList());
                }

                prop = entityDef.getProperty(propName);

                if ((prop == null) || (prop.getEntityDefinition() != entityDef)) {
                    throw new IllegalArgumentException("Undefined selection property name(" + propName + ") is specified for the target entity "
                            + entityDef.getName() + ": " + entityDef.getPropertyNameList());
                }
            }
        }
    }

    /**
     * Check props list.
     *
     * @param propsList the props list
     */
    public static void checkPropsList(final List<Map<String, Object>> propsList) {
        if (N.isNullOrEmpty(propsList)) {
            throw new IllegalArgumentException("propsList can not be null or empty.");
        }

        int size = propsList.get(0).size();

        for (Map<String, Object> props : propsList) {
            if (props.size() != size) {
                throw new IllegalArgumentException("the size of the properties in the list must be same.");
            }
        }
    }

    /**
     * Check conflict options.
     *
     * @param options the options
     */
    public static void checkConflictOptions(final Map<String, Object> options) {
        if (isGetFromCache(options) || isCacheResult(options)) {
            if ((options.get(Jdbc.MAX_FIELD_SIZE) != null) || (options.get(Jdbc.MAX_ROWS) != null) || (options.get(Jdbc.FETCH_DIRECTION) != null)) {
                throw new IllegalArgumentException("'FROM_CACHE' or 'CACHE_RESULT' is conflict with 'MAX_ROWS' or 'FETCH_DIRECTION' or 'MAX_FIELD_SIZE'. ");
            }
        }
    }

    /**
     * Gets the prop value by method.
     *
     * @param entity the entity
     * @param prop the prop
     * @return the prop value by method
     */
    public static Object getPropValueByMethod(final Object entity, final Property prop) {
        // return N.invokeMethod(entity, prop.getGetMethod(entity.getClass()));
        return ParserUtil.getEntityInfo(entity.getClass()).getPropValue(entity, prop.getName());
    }

    /**
     * Sets the prop value by method.
     *
     * @param entity the entity
     * @param prop the prop
     * @param propValue the prop value
     */
    public static void setPropValueByMethod(final Object entity, final Property prop, Object propValue) {
        if (propValue == null) {
            propValue = N.defaultValueOf(prop.getType().clazz());
        }

        // N.setPropValue(entity, prop.getSetMethod(entity.getClass()), propValue);
        ParserUtil.getEntityInfo(entity.getClass()).setPropValue(entity, prop.getName(), propValue);
    }

    /**
     * Gets the prop value.
     *
     * @param entity the entity
     * @param prop the prop
     * @return the prop value
     */
    public static Object getPropValue(final Object entity, final Property prop) {
        if (entity instanceof MapEntity) {
            return ((MapEntity) entity).get(prop.getName());
        } else {
            return getPropValueByMethod(entity, prop);
        }
    }

    /**
     * Sets the prop value.
     *
     * @param entity the entity
     * @param prop the prop
     * @param propValue the prop value
     */
    public static void setPropValue(final Object entity, final Property prop, final Object propValue) {
        if (entity instanceof MapEntity) {
            ((MapEntity) entity).set(prop.getName(), propValue);
        } else {
            setPropValueByMethod(entity, prop, propValue);
        }
    }

    /**
     * Gets the signed prop names.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @return the signed prop names
     */
    public static Collection<String> getSignedPropNames(final EntityDefinition entityDef, final Object entity) {
        Collection<String> propNames = null;

        if (DirtyMarkerUtil.isDirtyMarker(entity.getClass())) {
            propNames = DirtyMarkerUtil.signedPropNames((DirtyMarker) entity);
        } else {
            propNames = entityDef.getPropertyNameList();
        }

        return propNames;
    }

    /**
     * Gets the dirty prop names.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @return the dirty prop names
     */
    public static Collection<String> getDirtyPropNames(final EntityDefinition entityDef, final Object entity) {
        Collection<String> propNames = null;

        if (DirtyMarkerUtil.isDirtyMarker(entity.getClass())) {
            propNames = DirtyMarkerUtil.dirtyPropNames((DirtyMarker) entity);
        } else {
            propNames = entityDef.getPropertyNameList();
        }

        return propNames;
    }

    /**
     * Gets the updated props.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @return the updated props
     */
    public static Map<String, Object> getUpdatedProps(final EntityDefinition entityDef, final Object entity) {
        Collection<String> propNamesToUpdate = getDirtyPropNames(entityDef, entity);
        Map<String, Object> updateProps = new HashMap<>();

        if (entity instanceof MapEntity) {
            MapEntity anEntity = (MapEntity) entity;

            for (String propName : propNamesToUpdate) {
                updateProps.put(propName, anEntity.get(propName));
            }
        } else {
            for (String propName : propNamesToUpdate) {
                Property prop = entityDef.getProperty(propName);
                updateProps.put(propName, getPropValueByMethod(entity, prop));
            }
        }

        for (Property prop : entityDef.getEntiyPropertyList()) {
            if (!updateProps.containsKey(prop.getName())) {
                Object propValue = getPropValue(entity, prop);

                if (propValue != null) {
                    if (prop.isCollection() && propValue instanceof Collection) {
                        @SuppressWarnings("unchecked")
                        Collection<Object> c = (Collection<Object>) propValue;
                        Collection<Object> updateEntityList = null;

                        for (Object e : c) {
                            if (e instanceof DirtyMarker) {
                                if (DirtyMarkerUtil.isDirty((DirtyMarker) e)) {
                                    if (updateEntityList == null) {
                                        updateEntityList = prop.asCollection();
                                    }

                                    updateEntityList.add(e);
                                }
                            } else {
                                if (updateEntityList == null) {
                                    updateEntityList = prop.asCollection();
                                }

                                updateEntityList.add(e);
                            }
                        }

                        if (N.notNullOrEmpty(updateEntityList)) {
                            updateProps.put(prop.getName(), propValue);
                        }
                    } else {
                        if (propValue instanceof DirtyMarker) {
                            if (DirtyMarkerUtil.isDirty((DirtyMarker) propValue)) {
                                updateProps.put(prop.getName(), propValue);
                            }
                        } else {
                            updateProps.put(prop.getName(), propValue);
                        }
                    }
                }
            }
        }

        for (String idPropName : entityDef.getIdPropertyNameList()) {
            updateProps.remove(idPropName);
        }

        return updateProps;
    }

    /**
     * Parses the select prop names in get.
     *
     * @param entityDef the entity def
     * @param selectPropNames the select prop names
     * @return the select prop name view
     */
    public static SelectPropNameView parseSelectPropNamesInGet(final EntityDefinition entityDef, final Collection<String> selectPropNames) {
        return parseSelectPropNames(entityDef, selectPropNames, true);
    }

    /**
     * Parses the select prop names in query.
     *
     * @param entityDef the entity def
     * @param selectPropNames the select prop names
     * @param options the options
     * @return the select prop name view
     */
    public static SelectPropNameView parseSelectPropNamesInQuery(final EntityDefinition entityDef, final Collection<String> selectPropNames,
            final Map<String, Object> options) {
        SelectPropNameView xa = parseSelectPropNames(entityDef, selectPropNames, false);

        for (String entityPropName : xa.entityPropNames) {
            for (String subPropName : entityDef.getProperty(entityPropName).getSubPropertyNameList()) {
                if (!xa.simplePropNames.contains(subPropName)) {
                    xa.simplePropNames.add(subPropName);
                }
            }
        }

        if (isResultCombined(options) && (xa.entityPropNames.size() > 0)) {
            for (String idPropName : entityDef.getIdPropertyNameList()) {
                if (!xa.simplePropNames.contains(idPropName)) {
                    xa.simplePropNames.add(idPropName);
                }
            }
        }

        return xa;
    }

    /**
     * Parses the select prop names.
     *
     * @param entityDef the entity def
     * @param selectPropNames the select prop names
     * @param autoAddId the auto add id
     * @return the select prop name view
     */
    private static SelectPropNameView parseSelectPropNames(final EntityDefinition entityDef, Collection<String> selectPropNames, final boolean autoAddId) {
        boolean isNullSelectPropNames = selectPropNames == null;

        if (isNullSelectPropNames) {
            selectPropNames = new LinkedHashSet<>(entityDef.getDefaultLoadPropertyNameList());
        } else {
            selectPropNames = ParametersUtil.copy(selectPropNames);
        }

        SelectPropNameView xa = null;

        Set<String> simplePropNames = new LinkedHashSet<>();
        Set<String> entityPropNames = new LinkedHashSet<>();

        Property prop = null;

        for (String propName : selectPropNames) {
            prop = entityDef.getProperty(propName);

            if (prop == null) {
                //                int idx = propName.lastIndexOf(' ');
                //                if (idx > 2 && "AS".equalsIgnoreCase(propName.substring(idx - 2, idx))) {
                //                    String tmp = propName.substring(idx + 1).trim();
                //                    if (tmp.startsWith("\"") && tmp.endsWith("\"")) {
                //                        tmp = tmp.substring(1, tmp.length() - 1);
                //                    }
                //                    simplePropNames.add(tmp);
                //                } else {
                //                    simplePropNames.add(propName);
                //                }

                simplePropNames.add(propName);
            } else if (prop.getColumnType().isEntity()) {
                entityPropNames.add(prop.getName());
                simplePropNames.add(prop.getAssociation().getSrcProperty().getName());
            } else if (prop.getEntityDefinition() == entityDef || prop.getEntityDefinition() == entityDef.getParentEntity()) {
                simplePropNames.add(prop.getName());
            } else {
                simplePropNames.add(propName);
            }
        }

        if (autoAddId) {
            for (String idPropName : entityDef.getIdPropertyNameList()) {
                if (!simplePropNames.contains(idPropName)) {
                    simplePropNames.add(idPropName);
                }
            }

            for (String uidPropName : entityDef.getIdPropertyNameList()) {
                if (!simplePropNames.contains(uidPropName)) {
                    simplePropNames.add(uidPropName);
                }
            }
        }

        xa = new SelectPropNameView(entityDef.getName(), isNullSelectPropNames ? null : selectPropNames, simplePropNames, entityPropNames);

        return xa;
    }

    /**
     * Parses the update props.
     *
     * @param entityDef the entity def
     * @param props the props
     * @param isPropEntitySupported the is prop entity supported
     * @return the update props view
     */
    public static UpdatePropsView parseUpdateProps(final EntityDefinition entityDef, final Map<String, Object> props, final boolean isPropEntitySupported) {
        // TODO [performance improvement]. how to improve performance?
        final Map<String, Object> updateProps = new HashMap<>(N.initHashCapacity(props.size()));
        Map<Property, List<Object>> propEntityList = null;
        Property prop = null;
        Object propValue = null;

        for (String propName : props.keySet()) {
            prop = entityDef.getProperty(propName);

            if (prop == null || !(prop.getEntityDefinition() == entityDef || prop.getEntityDefinition() == entityDef.getParentEntity())) {
                throw new IllegalArgumentException("The specified property[" + propName + "] is not found in entity[" + entityDef.getName() + "]. ");
            }

            if (!prop.isUpdatable()) {
                throw new IllegalArgumentException("The specified property[" + propName + "] is not updatable in entity[" + entityDef.getName() + "]. ");
            }

            propValue = props.get(propName);
            propValue = validatePropValue(prop, propValue);

            if (prop.getColumnType().isEntity()) {
                EntityDefinition columnEntityDef = prop.getColumnEntityDef();

                if (!isPropEntitySupported) {
                    throw new IllegalArgumentException("Property entity[" + columnEntityDef.getName() + "] is not supported by this mode entity manager. ");
                }

                if (propValue != null) {
                    if (propEntityList == null) {
                        propEntityList = new HashMap<>();
                    }

                    List<Object> entityList = propEntityList.get(prop);

                    if (entityList == null) {
                        entityList = new ArrayList<>();
                        propEntityList.put(prop, entityList);
                    }

                    if (prop.isCollection() && propValue instanceof Collection) {
                        @SuppressWarnings("unchecked")
                        Collection<Object> c = (Collection<Object>) propValue;

                        for (Object e : c) {
                            if (e instanceof DirtyMarker) {
                                if (DirtyMarkerUtil.isDirty((DirtyMarker) e)) {
                                    entityList.add(e);
                                }
                            } else {
                                entityList.add(e);
                            }
                        }
                    } else {
                        if (propValue instanceof DirtyMarker) {
                            if (DirtyMarkerUtil.isDirty((DirtyMarker) propValue)) {
                                entityList.add(propValue);
                            }
                        } else {
                            entityList.add(propValue);
                        }
                    }
                }
            } else {
                updateProps.put(prop.getName(), propValue);
            }
        }

        if (entityDef.getDefaultOnUpdatePropertyList().size() > 0) {
            for (Property defaultOnUpdateProp : entityDef.getDefaultOnUpdatePropertyList()) {
                if (!(updateProps.containsKey(defaultOnUpdateProp.getName()))) {
                    updateProps.put(defaultOnUpdateProp.getName(), defaultOnUpdateProp.getDefaultOnUpdate());
                }
            }
        }

        return new UpdatePropsView(updateProps, propEntityList);
    }

    /**
     * Method validatePropValue.
     *
     * @param prop the prop
     * @param propValue the prop value
     * @return the object
     */
    private static Object validatePropValue(final Property prop, Object propValue) {
        List<Validator<Object>> validators = prop.getValidatorList();

        if (validators.size() > 0) {
            for (Validator<Object> validator : validators) {
                propValue = validator.validate(propValue);
            }
        }

        return propValue;
    }

    /**
     * Entity 2 map.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @return the map
     */
    public static Map<String, Object> entity2Map(final EntityDefinition entityDef, final Object entity) {
        Collection<String> signedPropNames = getSignedPropNames(entityDef, entity);
        Map<String, Object> insertProps = new HashMap<>();

        if (entity instanceof MapEntity) {
            MapEntity anEntity = (MapEntity) entity;

            for (String propName : signedPropNames) {
                insertProps.put(propName, anEntity.get(propName));
            }
        } else {
            for (String propName : signedPropNames) {
                Property prop = entityDef.getProperty(propName);
                insertProps.put(propName, getPropValueByMethod(entity, prop));
            }
        }

        return insertProps;
    }

    /**
     * Entity 2 map.
     *
     * @param entityDef the entity def
     * @param entities the entities
     * @return the list
     */
    public static List<Map<String, Object>> entity2Map(final EntityDefinition entityDef, final Collection<?> entities) {
        checkEntity(entityDef.getFactory(), entities);

        List<Map<String, Object>> propsList = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            propsList.add(entity2Map(entityDef, entity));
        }

        return propsList;
    }

    /**
     * Gets the prop entity.
     *
     * @param entityDef the entity def
     * @param propsList the props list
     * @return the prop entity
     */
    public static Map<Property, List<Object>> getPropEntity(final EntityDefinition entityDef, final List<Map<String, Object>> propsList) {
        Map<Property, List<Object>> propEntities = new HashMap<>();

        for (Map<String, Object> props : propsList) {
            Property prop = null;
            Object propValue = null;

            for (String propName : props.keySet()) {
                propValue = props.get(propName);
                prop = entityDef.getProperty(propName);

                if ((prop != null) && (prop.getColumnType().isEntity())) {
                    List<Object> entityList = propEntities.get(prop);

                    if (entityList == null) {
                        entityList = new ArrayList<>();
                        propEntities.put(prop, entityList);
                    }

                    if (propValue != null) {
                        entityList.add(propValue);
                    }
                }
            }
        }

        return propEntities;
    }

    /**
     * Parses the insert props list.
     *
     * @param entityDef the entity def
     * @param propsList the props list
     * @param isPropEntitySupported the is prop entity supported
     * @return the insert props list view
     */
    public static InsertPropsListView parseInsertPropsList(final EntityDefinition entityDef, final List<Map<String, Object>> propsList,
            final boolean isPropEntitySupported) {
        List<Map<String, Object>> insertPropsList = new ArrayList<>(propsList.size());
        InsertPropsListView insertPropsListView = null;

        if (isPropEntitySupported) {
            Map<Property, List<PropEntityProps>> propEntityPropsList = new HashMap<>();
            Map<Property, List<BiEntityProps>> propBiEntityPropsList = new HashMap<>();
            insertPropsListView = new InsertPropsListView(insertPropsList, propEntityPropsList, propBiEntityPropsList);
        } else {
            insertPropsListView = new InsertPropsListView(insertPropsList, null, null);
        }

        Collection<String> propNamesToInsert = null;

        for (Map<String, Object> props : propsList) {
            insertPropsList.add(parseInsertProps(entityDef, propNamesToInsert, props, insertPropsListView, isPropEntitySupported));

            if (propNamesToInsert == null) {
                propNamesToInsert = props.keySet();
            }
        }

        return insertPropsListView;
    }

    /**
     * Parses the insert props.
     *
     * @param entityDef the entity def
     * @param propNamesToInsert the prop names to insert
     * @param props the props
     * @param insertPropsListView the insert props list view
     * @param isPropEntitySupported the is prop entity supported
     * @return the map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseInsertProps(final EntityDefinition entityDef, Collection<String> propNamesToInsert, final Map<String, Object> props,
            final InsertPropsListView insertPropsListView, final boolean isPropEntitySupported) {
        // TODO [performance improvement]. how to improve performance?

        final Map<String, Object> insertProps = new HashMap<>(N.initHashCapacity(props.size()));
        final boolean isInputPropNames = propNamesToInsert != null;
        propNamesToInsert = (propNamesToInsert == null) ? props.keySet() : propNamesToInsert;

        Object propValue = null;
        for (String propName : propNamesToInsert) {
            Property prop = entityDef.getProperty(propName);

            if (prop == null || !(prop.getEntityDefinition() == entityDef || prop.getEntityDefinition() == entityDef.getParentEntity())) {
                throw new IllegalArgumentException("The specified property[" + propName + "] is not found in entity[" + entityDef.getName() + "]. ");
            }

            if (!prop.isInsertable()) {
                throw new IllegalArgumentException("The specified property[" + propName + "] is not insertable in entity[" + entityDef.getName() + "]. ");
            }

            propValue = props.get(propName);

            if (isInputPropNames && (propValue == null) && !props.containsKey(propName)) {
                throw new IllegalArgumentException(
                        "The properties in the list have different property elements. The properties in the first element in in the list are: "
                                + propNamesToInsert + ". But another element in the list are: " + props + ".");
            }

            propValue = validatePropValue(prop, propValue);

            insertProps.put(prop.getName(), propValue);
        }

        // check id prop value.
        for (IdGenerator<Object> idGenerator : entityDef.getIdGeneratorList()) {
            Object idValue = insertProps.get(idGenerator.getProperty().getName());

            if (isNullOrEmptyIdValue(idValue)) {
                if (entityDef.isIdAutoGenerated()) {
                    insertProps.remove(idGenerator.getProperty().getName());

                    break;
                } else {
                    idValue = idGenerator.allocate();
                    insertProps.put(idGenerator.getProperty().getName(), idValue);
                }
            } else {
                idGenerator.reserve(idValue);
            }
        }

        for (Property prop : entityDef.getEntiyPropertyList()) {
            propValue = insertProps.remove(prop.getName());

            if (propValue == null) {
                continue;
            }

            final EntityDefinition columnEntityDef = prop.getColumnEntityDef();

            if (!isPropEntitySupported) {
                throw new IllegalArgumentException("Property entity[" + columnEntityDef.getName() + "] is not supported by this mode entity manager. ");
            }

            Association association = prop.getAssociation();
            Property srcProp = association.getSrcProperty();
            Property targetProp = association.getTargetProperty();

            if (association.getJoinType() == JoinType.OUTER) {
                Object targetPropValue = getPropValue(propValue, targetProp);
                insertProps.put(srcProp.getName(), targetPropValue);
            } else {
                Object srcPropValue = props.get(srcProp.getName());

                if (isNullOrEmptyIdValue(srcPropValue) && !srcProp.isAutoIncrement()) {
                    throw new IllegalArgumentException("Can not add the assoication. There is no value for the property [" + srcProp.getName() + "]. ");
                }

                List<PropEntityProps> entityPropsList = insertPropsListView.propEntityPropsList.get(prop);

                if (entityPropsList == null) {
                    entityPropsList = new ArrayList<>();
                    insertPropsListView.propEntityPropsList.put(prop, entityPropsList);
                }

                Collection<Object> propEntities = (prop.isCollection() && propValue instanceof Collection) ? (Collection<Object>) propValue
                        : N.asList(propValue);
                EntityDefinition biAssociationEntityDef = association.getBiEntityDef();

                if (biAssociationEntityDef == null) {
                    for (Object propEntity : propEntities) {
                        entityPropsList.add(new PropEntityProps(prop, propEntity, srcPropValue, insertProps));
                    }
                } else {
                    List<BiEntityProps> biEntityPropsList = insertPropsListView.propBiEntityPropsList.get(prop);

                    if (biEntityPropsList == null) {
                        biEntityPropsList = new ArrayList<>();
                        insertPropsListView.propBiEntityPropsList.put(prop, biEntityPropsList);
                    }

                    Property targetProperty = association.getTargetProperty();
                    EntityDefinition propEntityDef = targetProperty.getEntityDefinition();
                    Property autoIncrementIdTargetProp = propEntityDef.isIdAutoGenerated() ? propEntityDef.getIdPropertyList().get(0) : null;

                    Object targetPropValue = null;

                    for (Object propEntity : propEntities) {
                        entityPropsList.add(new PropEntityProps(prop, propEntity));

                        targetPropValue = getPropValue(propEntity, targetProperty);

                        if (isNullOrEmptyIdValue(targetPropValue)) {
                            IdGenerator<Object> idGenerate = propEntityDef.getIdGenerator(targetProperty.getName());

                            if ((idGenerate != null) && !propEntityDef.isIdAutoGenerated()) {
                                targetPropValue = idGenerate.allocate();
                                setPropValue(propEntity, targetProperty, targetPropValue);
                            }

                            if (isNullOrEmptyIdValue(targetPropValue) && !targetProperty.equals(autoIncrementIdTargetProp)) {
                                throw new IllegalArgumentException(
                                        "Can not add the association. There is no value for the property [" + targetProperty.getName() + "]. ");
                            }
                        }

                        biEntityPropsList.add(new BiEntityProps(prop, insertProps, propEntity));
                    }
                }
            }

        }

        if (entityDef.getDefaultOnInsertPropertyList().size() > 0) {
            for (Property defaultOnInsertProp : entityDef.getDefaultOnInsertPropertyList()) {
                if (!(insertProps.containsKey(defaultOnInsertProp.getName()))) {
                    insertProps.put(defaultOnInsertProp.getName(), defaultOnInsertProp.getDefaultOnInsert());
                }
            }
        }

        return insertProps;
    }

    /**
     * Clean up dirty prop names.
     *
     * @param entity the entity
     */
    public static void cleanUpDirtyPropNames(final Object entity) {
        if (entity instanceof DirtyMarker) {
            DirtyMarkerUtil.markDirty((DirtyMarker) entity, false);
        }
    }

    /**
     * Clean up dirty prop names.
     *
     * @param entities the entities
     */
    public static void cleanUpDirtyPropNames(final Collection<?> entities) {
        if (N.notNullOrEmpty(entities)) {
            if (entities.iterator().next() instanceof DirtyMarker) {
                for (Object entity : entities) {
                    cleanUpDirtyPropNames(entity);
                }
            }
        }
    }

    /**
     * Sets the dirty marker.
     *
     * @param entity the entity
     * @param isDirty the is dirty
     */
    public static void setDirtyMarker(final Object entity, final boolean isDirty) {
        if (entity instanceof DirtyMarker) {
            DirtyMarkerUtil.markDirty((DirtyMarker) entity, isDirty);
        }
    }

    /**
     * Sets the dirty marker.
     *
     * @param entities the entities
     * @param isDirty the is dirty
     */
    public static void setDirtyMarker(final Collection<?> entities, final boolean isDirty) {
        if (N.notNullOrEmpty(entities)) {
            if (entities.iterator().next() instanceof DirtyMarker) {
                for (Object entity : entities) {
                    DirtyMarkerUtil.markDirty((DirtyMarker) entity, isDirty);
                }
            }
        }
    }

    /**
     * Checks if is null or empty id value.
     *
     * @param value the value
     * @return true, if is null or empty id value
     */
    public static boolean isNullOrEmptyIdValue(final Object value) {
        return (value == null) || (value instanceof Number && (0 == ((Number) value).longValue()));
    }

    /**
     * Sets the id value.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @param entityId the entity id
     */
    @SuppressWarnings("deprecation")
    public static void setIdValue(final EntityDefinition entityDef, final Object entity, final EntityId entityId) {
        Object propVal = null;

        for (String idPropName : entityId.keySet()) {
            propVal = ClassUtil.getPropValue(entity, idPropName);

            if (JdbcUtil.isDefaultIdPropValue(propVal)) {
                Property prop = entityDef.getProperty(idPropName);
                setPropValue(entity, prop, entityId.get(idPropName));
            }
        }
    }

    /**
     * Sets the id value.
     *
     * @param entityDef the entity def
     * @param entities the entities
     * @param entityIds the entity ids
     */
    public static void setIdValue(final EntityDefinition entityDef, final List<?> entities, final List<EntityId> entityIds) {
        for (int i = 0, len = entityIds.size(); i < len; i++) {
            EntityManagerUtil.setIdValue(entityDef, entities.get(i), entityIds.get(i));
        }
    }

    /**
     * Gets the entity id by entity.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @return the entity id by entity
     */
    public static EntityId getEntityIdByEntity(final EntityDefinition entityDef, final Object entity) {
        return getEntityIdByEntity(entityDef, entity, true);
    }

    /**
     * Gets the entity id by entity.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @param checkEmptyId the check empty id
     * @return the entity id by entity
     */
    public static EntityId getEntityIdByEntity(final EntityDefinition entityDef, final Object entity, final boolean checkEmptyId) {
        Collection<String> idPropNames = entityDef.getIdPropertyNameList();

        if (0 == idPropNames.size()) {
            throw new IllegalArgumentException("Entity[" + entityDef.getName() + "] doesn't have id property.");
        }

        Collection<String> signedPropNames = getSignedPropNames(entityDef, entity);

        if (!signedPropNames.containsAll(idPropNames)) {
            if (checkEmptyId) {
                throw new IllegalArgumentException("The signed properties in the entity are: " + signedPropNames + ". Some id properties(" + idPropNames
                        + ") are not signed in the entity: " + N.toString(entity));
            } else {
                return null;
            }
        }

        EntityId entityId = Seid.of(entityDef.getName());

        if (entity instanceof MapEntity) {
            MapEntity anEntity = (MapEntity) entity;

            for (String propName : idPropNames) {
                entityId.set(propName, anEntity.get(propName));
            }
        } else {
            for (String propName : idPropNames) {
                Property prop = entityDef.getProperty(propName);
                entityId.set(propName, getPropValueByMethod(entity, prop));
            }
        }

        return entityId;
    }

    /**
     * Gets the entity id by entity.
     *
     * @param entityDef the entity def
     * @param entities the entities
     * @return the entity id by entity
     */
    public static List<EntityId> getEntityIdByEntity(final EntityDefinition entityDef, final Collection<?> entities) {
        return getEntityIdByEntity(entityDef, entities, true);
    }

    /**
     * Gets the entity id by entity.
     *
     * @param entityDef the entity def
     * @param entities the entities
     * @param checkEmptyId the check empty id
     * @return the entity id by entity
     */
    public static List<EntityId> getEntityIdByEntity(final EntityDefinition entityDef, final Collection<?> entities, final boolean checkEmptyId) {
        List<EntityId> entityIds = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            entityIds.add(getEntityIdByEntity(entityDef, entity, checkEmptyId));
        }

        return entityIds;
    }

    /**
     * Result set 2 entity id.
     *
     * @param entityDef the entity def
     * @param dataSet the data set
     * @return the list
     */
    public static List<EntityId> resultSet2EntityId(final EntityDefinition entityDef, final DataSet dataSet) {
        final String entityName = entityDef.getName();
        final List<EntityId> entityIds = new ArrayList<>(dataSet.size());
        final Collection<String> idPropNames = entityDef.getIdPropertyNameList();
        EntityId entityId = null;

        for (int i = 0, len = dataSet.size(); i < len; i++) {
            dataSet.absolute(i);

            entityId = Seid.of(entityName);

            for (String idPropName : idPropNames) {
                Object id = dataSet.get(idPropName);

                if (id != null) {
                    entityId.set(idPropName, id);
                }
            }

            entityIds.add(entityId);
        }

        return entityIds;
    }

    /**
     * Gets the cache range.
     *
     * @param options the options
     * @return the cache range
     */
    public static Cache.Range getCacheRange(final Map<String, Object> options) {
        Cache.Range range = null;

        if ((options != null) && (options.get(Cache.CACHE_RESULT_RANGE) != null)) {
            range = (Cache.Range) options.get(Cache.CACHE_RESULT_RANGE);
        } else {
            range = Cache.range(Cache.Range.DEFAULT_FROM, Cache.Range.DEFAULT_TO);
        }

        return range;
    }

    /**
     * Gets the cache condition.
     *
     * @param queryCacheConfig the query cache config
     * @param options the options
     * @return the cache condition
     */
    public static Cache.Condition getCacheCondition(final QueryCacheConfiguration queryCacheConfig, final Map<String, Object> options) {
        Cache.Condition cacheCond = null;

        if ((options != null) && (options.get(Cache.CACHE_RESULT_CONDITION) != null)) {
            cacheCond = (Cache.Condition) options.get(Cache.CACHE_RESULT_CONDITION);
        } else if ((queryCacheConfig != null) && (queryCacheConfig.getCacheResultConditionConfiguration() != null)) {
            CacheResultConditionConfiguration config = queryCacheConfig.getCacheResultConditionConfiguration();
            cacheCond = Cache.condition(config.getMinQueryTime(), config.getMinCount(), config.getMaxCount());
        } else {
            cacheCond = Cache.condition(Cache.Condition.DEFAULT_MIN_QUERY_TIME, Cache.Condition.DEFAULT_MIN_COUNT, Cache.Condition.DEFAULT_MAX_COUNT);
        }

        return cacheCond;
    }

    /**
     * Gets the uncache prop names.
     *
     * @param options the options
     * @return the uncache prop names
     */
    public static Object getUncachePropNames(final Map<String, Object> options) {
        return (options == null) ? null : options.get(Cache.UNCACHED_PROP_NAMES);
    }

    /**
     * Gets the entity cache live time.
     *
     * @param entityName the entity name
     * @param entityCacheConfig the entity cache config
     * @param options the options
     * @return the entity cache live time
     */
    public static long getEntityCacheLiveTime(final String entityName, final EntityCacheConfiguration entityCacheConfig, final Map<String, Object> options) {
        long liveTime = EntityCacheConfiguration.DEFAULT_LIVE_TIME;

        if ((options != null) && (options.get(Cache.LIVE_TIME) != null)) {
            liveTime = ((Number) options.get(Cache.LIVE_TIME)).longValue();
        } else if (entityCacheConfig != null) {
            CustomizedEntityCacheConfiguration customizedEntityCacheConfiguration = entityCacheConfig.getCustomizedEntityCacheConfiguration(entityName);

            if (customizedEntityCacheConfiguration != null) {
                liveTime = customizedEntityCacheConfiguration.getLiveTime();
            } else {
                liveTime = entityCacheConfig.getLiveTime();
            }
        }

        if (liveTime < 0) {
            throw new IllegalArgumentException("Invalid entity cache live time[" + liveTime + "]. It must not be negative. ");
        }

        return liveTime;
    }

    /**
     * Gets the entity cache max idle time.
     *
     * @param entityName the entity name
     * @param entityCacheConfig the entity cache config
     * @param options the options
     * @return the entity cache max idle time
     */
    public static long getEntityCacheMaxIdleTime(final String entityName, final EntityCacheConfiguration entityCacheConfig, final Map<String, Object> options) {
        long maxIdleTime = EntityCacheConfiguration.DEFAULT_MAX_IDLE_TIME;

        if ((options != null) && (options.get(Cache.MAX_IDLE_TIME) != null)) {
            maxIdleTime = ((Number) options.get(Cache.MAX_IDLE_TIME)).longValue();
        } else if (entityCacheConfig != null) {
            CustomizedEntityCacheConfiguration customizedEntityCacheConfiguration = entityCacheConfig.getCustomizedEntityCacheConfiguration(entityName);

            if (customizedEntityCacheConfiguration != null) {
                maxIdleTime = customizedEntityCacheConfiguration.getMaxIdleTime();
            } else {
                maxIdleTime = entityCacheConfig.getMaxIdleTime();
            }
        }

        if (maxIdleTime < 0) {
            throw new IllegalArgumentException("Invalid entity cache max idle time[" + maxIdleTime + "]. It must not be negative. ");
        }

        return maxIdleTime;
    }

    /**
     * Gets the query cache live time.
     *
     * @param queryCacheConfig the query cache config
     * @param options the options
     * @return the query cache live time
     */
    public static long getQueryCacheLiveTime(final QueryCacheConfiguration queryCacheConfig, final Map<String, Object> options) {
        long liveTime = QueryCacheConfiguration.DEFAULT_LIVE_TIME;

        if ((options != null) && (options.get(Cache.LIVE_TIME) != null)) {
            liveTime = ((Number) options.get(Cache.LIVE_TIME)).longValue();
        } else if (queryCacheConfig != null) {
            liveTime = queryCacheConfig.getLiveTime();
        }

        if (liveTime < 0) {
            throw new IllegalArgumentException("Invalid query cache live time[" + liveTime + "]. It must not be negative. ");
        }

        return liveTime;
    }

    /**
     * Gets the query cache max idle time.
     *
     * @param queryCacheConfig the query cache config
     * @param options the options
     * @return the query cache max idle time
     */
    public static long getQueryCacheMaxIdleTime(final QueryCacheConfiguration queryCacheConfig, final Map<String, Object> options) {
        long maxIdleTime = QueryCacheConfiguration.DEFAULT_MAX_IDLE_TIME;

        if ((options != null) && (options.get(Cache.MAX_IDLE_TIME) != null)) {
            maxIdleTime = ((Number) options.get(Cache.MAX_IDLE_TIME)).longValue();
        } else if (queryCacheConfig != null) {
            maxIdleTime = queryCacheConfig.getMaxIdleTime();
        }

        if (maxIdleTime < 0) {
            throw new IllegalArgumentException("Invalid query cache max idle time[" + maxIdleTime + "]. It must not be negative. ");
        }

        return maxIdleTime;
    }

    /**
     * Gets the min check query cache size.
     *
     * @param queryCacheConfig the query cache config
     * @param options the options
     * @return the min check query cache size
     */
    public static int getMinCheckQueryCacheSize(final QueryCacheConfiguration queryCacheConfig, final Map<String, Object> options) {
        int minCheckQueryCacheSize = QueryCacheConfiguration.DEFAULT_MIN_CHECK_QUERY_CACHE_SIZE;

        // get and check update condition from options.
        if ((options != null) && (options.get(Cache.MIN_CHECK_QUERY_CACHE_SIZE) != null)) {
            minCheckQueryCacheSize = (Integer) options.get(Cache.MIN_CHECK_QUERY_CACHE_SIZE);
        } else if (queryCacheConfig != null) {
            minCheckQueryCacheSize = queryCacheConfig.getMinCheckCacheSize();
        }

        if (minCheckQueryCacheSize < 0) {
            throw new IllegalArgumentException("Invalid minCheckQueryCacheSize[" + minCheckQueryCacheSize + "]. It must not be negative. ");
        }

        return minCheckQueryCacheSize;
    }

    /**
     * Gets the max check query cache time.
     *
     * @param queryCacheConfig the query cache config
     * @param options the options
     * @return the max check query cache time
     */
    public static long getMaxCheckQueryCacheTime(final QueryCacheConfiguration queryCacheConfig, final Map<String, Object> options) {
        // get and check update condition from options.
        long maxCheckQueryCacheTime = QueryCacheConfiguration.DEFAULT_MAX_CHECK_QUERY_CACHE_TIME;

        if ((options != null) && (options.get(Cache.MAX_CHECK_QUERY_CACHE_TIME) != null)) {
            maxCheckQueryCacheTime = ((Number) options.get(Cache.MAX_CHECK_QUERY_CACHE_TIME)).longValue();
        } else if (queryCacheConfig != null) {
            maxCheckQueryCacheTime = queryCacheConfig.getMaxCheckCacheTime();
        }

        if (maxCheckQueryCacheTime < 0) {
            throw new IllegalArgumentException("Invalid maxCheckQueryCacheTime[" + maxCheckQueryCacheTime + "]. it must not be negative. ");
        }

        return maxCheckQueryCacheTime;
    }

    /**
     * Gets the offset.
     *
     * @param options the options
     * @return the offset
     */
    public static int getOffset(final Map<String, Object> options) {
        int offset = Query.DEFAULT_OFFSET;

        if ((options != null) && (options.get(Query.OFFSET) != null)) {
            offset = (Integer) options.get(Query.OFFSET);

            if (offset < 0) {
                throw new IllegalArgumentException("Invalid offset[" + offset + "]. It must not be negative. ");
            }
        }

        return offset;
    }

    /**
     * Gets the count.
     *
     * @param options the options
     * @return the count
     */
    public static int getCount(final Map<String, Object> options) {
        int count = Query.DEFAULT_COUNT;

        if ((options != null) && (options.get(Query.COUNT) != null)) {
            count = (Integer) options.get(Query.COUNT);

            if (count < 0) {
                throw new IllegalArgumentException("Invalid count[" + count + "]. It must not be negative. ");
            }
        }

        return count;
    }

    /**
     * Gets the lock code.
     *
     * @param options the options
     * @return the lock code
     */
    public static String getLockCode(final Map<String, Object> options) {
        return (options == null) ? null : (String) options.get(Options.RECORD_LOCK_CODE);
    }

    /**
     * Gets the record lock timeout.
     *
     * @param options the options
     * @param lockConfig the lock config
     * @return the record lock timeout
     */
    public static long getRecordLockTimeout(final Map<String, Object> options, final LockConfiguration lockConfig) {
        long lockTimeout = LockConfiguration.DEFAULT_RECORD_LOCK_TIMEOUT;

        if ((options != null) && (options.get(Options.RECORD_LOCK_TIMEOUT) != null)) {
            lockTimeout = ((Number) options.get(Options.RECORD_LOCK_TIMEOUT)).longValue();

            if (lockTimeout < 0) {
                throw new IllegalArgumentException("Invalid lock timeout[" + lockTimeout + "]. It must not be negative. ");
            }
        } else if (lockConfig != null) {
            lockTimeout = lockConfig.getRecordLockTimeout();
        }

        return lockTimeout;
    }

    /**
     * Gets the transaction id.
     *
     * @param options the options
     * @return the transaction id
     */
    public static String getTransactionId(final Map<String, Object> options) {
        return (options == null) ? null : (String) options.get(Options.TRANSACTION_ID);
    }

    /**
     * Gets the batch size.
     *
     * @param options the options
     * @return the batch size
     */
    public static int getBatchSize(final Map<String, Object> options) {
        int batchSize = Options.DEFAULT_BATCH_SIZE;

        if ((options != null) && (options.get(Options.BATCH_SIZE) != null)) {
            batchSize = (Integer) (options.get(Options.BATCH_SIZE));

            if (batchSize <= 0) {
                throw new IllegalArgumentException("Invalid batch size[" + batchSize + "]. It must greater than zero. ");
            }
        }

        return batchSize;
    }

    /**
     * Check result handle.
     *
     * @param resultHandle the result handle
     */
    public static void checkResultHandle(final Holder<String> resultHandle) {
        if ((resultHandle == null) || N.isNullOrEmpty(resultHandle.value())) {
            throw new IllegalArgumentException("The resultHandle or its value is null or empty.");
        }
    }

    /**
     * Check result handle.
     *
     * @param resultHandle the result handle
     */
    public static void checkResultHandle(final String resultHandle) {
        if (N.isNullOrEmpty(resultHandle)) {
            throw new IllegalArgumentException("The resultHandle or its value is null or empty.");
        }
    }

    /**
     * Gets the handle live time.
     *
     * @param options the options
     * @return the handle live time
     */
    public static long getHandleLiveTime(final Map<String, Object> options) {
        long liveTime = Query.HANDLE_DEFAULT_LIVE_TIME;

        if ((options != null) && (options.get(Query.HANDLE_LIVE_TIME) != null)) {
            liveTime = ((Number) options.get(Query.HANDLE_LIVE_TIME)).longValue();
        }

        return liveTime;
    }

    /**
     * Gets the handle max idle time.
     *
     * @param options the options
     * @return the handle max idle time
     */
    public static long getHandleMaxIdleTime(final Map<String, Object> options) {
        long maxIdleTime = Query.HANDLE_DEFAULT_MAX_IDLE_TIME;

        if ((options != null) && (options.get(Query.HANDLE_MAX_IDLE_TIME) != null)) {
            maxIdleTime = ((Number) options.get(Query.HANDLE_MAX_IDLE_TIME)).longValue();
        }

        return maxIdleTime;
    }

    /**
     * Checks if is enable my SQL batch add.
     *
     * @param options the options
     * @return true, if is enable my SQL batch add
     */
    public static boolean isEnableMySQLBatchAdd(final Map<String, Object> options) {
        return isYes(Options.ENABLE_MYSQL_BATCH_ADD, options);
    }

    /**
     * Checks if is gets the by result handle.
     *
     * @param resultHandle the result handle
     * @return true, if is gets the by result handle
     */
    public static boolean isGetByResultHandle(Holder<String> resultHandle)

    {
        return (resultHandle != null) && (N.notNullOrEmpty(resultHandle.value()));
    }

    /**
     * Checks if is in transaction.
     *
     * @param options the options
     * @return true, if is in transaction
     */
    public static boolean isInTransaction(final Map<String, Object> options) {
        return (options != null) && (options.get(Options.TRANSACTION_ID) != null);
    }

    /**
     * Not in transaction.
     *
     * @param options the options
     * @return true, if successful
     */
    public static boolean notInTransaction(final Map<String, Object> options) {
        return (options == null) || (options.get(Options.TRANSACTION_ID) == null);
    }

    /**
     * Checks if is auto rollback transaction.
     *
     * @param options the options
     * @return true, if is auto rollback transaction
     */
    public static boolean isAutoRollbackTransaction(final Map<String, Object> options) {
        return !isNot(Options.AUTO_ROLLBACK_TRANSACTION, options);
    }

    /**
     * Checks if is result combined.
     *
     * @param options the options
     * @return true, if is result combined
     */
    public static boolean isResultCombined(final Map<String, Object> options) {
        return isYes(Query.COMBINE_PROPERTIES, options);
    }

    /**
     * Checks if is yes.
     *
     * @param optionName the option name
     * @param options the options
     * @return true, if is yes
     */
    private static boolean isYes(final String optionName, final Map<String, Object> options) {
        if (N.isNullOrEmpty(options)) {
            return false;
        }

        Object obj = options.get(optionName);

        return (obj == null) ? false : (Boolean) obj;
    }

    /**
     * Checks if is query in parallel.
     *
     * @param options the options
     * @return true, if is query in parallel
     */
    public static boolean isQueryInParallel(final Map<String, Object> options) {
        return N.isNullOrEmpty(options) || options.get(Query.QUERY_IN_PARALLEL) == null || Boolean.TRUE.equals(options.get(Query.QUERY_IN_PARALLEL));
    }

    /**
     * Not query in parallel.
     *
     * @param options the options
     * @return true, if successful
     */
    public static boolean notQueryInParallel(final Map<String, Object> options) {
        return N.notNullOrEmpty(options) && Boolean.FALSE.equals(options.get(Query.QUERY_IN_PARALLEL));
    }

    /**
     * Checks if is query with read only conection.
     *
     * @param options the options
     * @return true, if is query with read only conection
     */
    public static boolean isQueryWithReadOnlyConection(final Map<String, Object> options) {
        return isYes(Query.QUERY_WITH_READ_ONLY_CONNECTION, options);
    }

    /**
     * Not query with read only conection.
     *
     * @param options the options
     * @return true, if successful
     */
    public static boolean notQueryWithReadOnlyConection(final Map<String, Object> options) {
        return isNot(Query.QUERY_WITH_READ_ONLY_CONNECTION, options);
    }

    /**
     * Checks if is not.
     *
     * @param optionName the option name
     * @param options the options
     * @return true, if is not
     */
    private static boolean isNot(final String optionName, final Map<String, Object> options) {
        if (N.isNullOrEmpty(options)) {
            return false;
        }

        Object value = options.get(optionName);

        return (value == null) ? false : Boolean.FALSE.equals(value);
    }

    /**
     * Checks if is cache result.
     *
     * @param options the options
     * @return true, if is cache result
     */
    public static boolean isCacheResult(final Map<String, Object> options) {
        if (options != null) {
            Object cacheResult = options.get(Query.CACHE_RESULT);

            if (cacheResult != null) {
                if (Query.CACHE_RESULT_SYN.equals(cacheResult) || Query.CACHE_RESULT_ASY.equals(cacheResult)) {
                    return true;
                } else if (Boolean.FALSE.equals(cacheResult)) {
                    return false;
                } else {
                    throw new IllegalArgumentException("Invalid CACHE_RESULT[" + options.get(Query.CACHE_RESULT)
                            + "] option . It must be 'CACHE_RESULT_SYN', 'CACHE_RESULT_ASY' or 'false'. ");
                }
            }
        }

        return false;
    }

    /**
     * Not cache result.
     *
     * @param options the options
     * @return true, if successful
     */
    public static boolean notCacheResult(final Map<String, Object> options) {
        return isNot(Query.CACHE_RESULT, options);
    }

    /**
     * Checks if is gets the from cache.
     *
     * @param options the options
     * @return true, if is gets the from cache
     */
    public static boolean isGetFromCache(final Map<String, Object> options) {
        return isYes(Query.QUERY_FROM_CACHE, options);
    }

    /**
     * Not get from cache.
     *
     * @param options the options
     * @return true, if successful
     */
    public static boolean notGetFromCache(final Map<String, Object> options) {
        return isNot(Query.QUERY_FROM_CACHE, options);
    }

    /**
     * Checks if is refresh cache.
     *
     * @param options the options
     * @return true, if is refresh cache
     */
    public static boolean isRefreshCache(final Map<String, Object> options) {
        return isYes(Query.REFRESH_CACHE, options);
    }

    /**
     * Removes the offset count.
     *
     * @param options the options
     * @return the map
     */
    public static Map<String, Object> removeOffsetCount(Map<String, Object> options) {
        if ((options != null) && ((options.get(Query.OFFSET) != null) || (options.get(Query.COUNT) != null))) {
            options = ParametersUtil.copy(options);
            options.remove(Query.OFFSET);
            options.remove(Query.COUNT);
        }

        return options;
    }

    /**
     * Checks for offset count.
     *
     * @param options the options
     * @return true, if successful
     */
    public static boolean hasOffsetCount(final Map<String, Object> options) {
        return (options != null) && ((options.get(Query.OFFSET) != null) || (options.get(Query.COUNT) != null));
    }

    /**
     * Requires auto generated keys.
     *
     * @param entityDef the entity def
     * @param propsList the props list
     * @return true, if successful
     */
    public static boolean requiresAutoGeneratedKeys(final EntityDefinition entityDef, final List<Map<String, Object>> propsList) {
        if (entityDef.isIdAutoGenerated() && (propsList.size() > 0)) {
            for (String idPropName : entityDef.getIdPropertyNameList()) {
                for (Map<String, Object> props : propsList) {
                    Object idPropValue = props.get(idPropName);

                    if (EntityManagerUtil.isNullOrEmptyIdValue(idPropValue)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Entity id 2 condition.
     *
     * @param entityId the entity id
     * @return the condition
     */
    public static Condition entityId2Condition(final EntityId entityId) {
        if (entityId.isEmpty()) {
            throw new IllegalArgumentException("Empty EntityId is found: " + N.toString(entityId));
        }

        if (entityId instanceof Condition) {
            return (Condition) entityId;
        } else {
            Condition cond = null;
            Collection<String> propNames = entityId.keySet();

            if (propNames.size() == 1) {
                String propName = propNames.iterator().next();
                Object propValue = entityId.get(propName);

                if (propValue != null) {
                    cond = new Equal(propName, propValue);
                } else {
                    cond = new IsNull(propName);
                }
            } else {
                And and = new And();

                for (String propName : entityId.keySet()) {
                    Object propValue = entityId.get(propName);

                    if (propValue != null) {
                        and.add(new Equal(propName, propValue));
                    } else {
                        and.add(new IsNull(propName));
                    }
                }

                cond = and;
            }

            return cond;
        }
    }

    /**
     * Entity id 2 condition.
     *
     * @param entityIds the entity ids
     * @return the condition
     */
    public static Condition entityId2Condition(final List<? extends EntityId> entityIds) {
        return entityId2Condition(entityIds, 0, entityIds.size());
    }

    /**
     * Entity id 2 condition.
     *
     * @param entityIds the entity ids
     * @param fromIndex the from index
     * @param toIndex the to index
     * @return the condition
     */
    public static Condition entityId2Condition(final List<? extends EntityId> entityIds, final int fromIndex, final int toIndex) {
        if (toIndex - fromIndex == 1) {
            return entityId2Condition(entityIds.get(fromIndex));
        }

        final String entityName = entityIds.get(0).entityName();
        final Or cond = new Or();

        for (int i = fromIndex; i < toIndex; i++) {
            if (!entityIds.get(i).entityName().equals(entityName)) {
                throw new IllegalArgumentException("The entityIds: " + N.toString(entityIds) + " must be the same entity.");
            }

            cond.add(entityId2Condition(entityIds.get(i)));
        }

        return cond;
    }

    /** The Constant NAME_OF_ENTITY_NAME_FIELD. */
    private static final String NAME_OF_ENTITY_NAME_FIELD = "__";

    /** The Constant entityNameMap. */
    private static final Map<Class<?>, String> entityNameMap = new ConcurrentHashMap<>();

    /**
     * Gets the entity name.
     *
     * @param entityClass the entity class
     * @return the entity name
     */
    static String getEntityName(final Class<?> entityClass) {
        String entityName = entityNameMap.get(entityClass);

        if (entityName == null) {
            entityName = ClassUtil.getSimpleClassName(entityClass);

            final Set<Class<?>> classes = ClassUtil.getAllSuperTypes(entityClass);
            classes.add(entityClass);

            for (Class<?> cls : classes) {
                try {
                    final Field field = cls.getDeclaredField(NAME_OF_ENTITY_NAME_FIELD);

                    if (field != null) {
                        entityName = (String) field.get(null);
                    }

                    break;
                } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                    e.printStackTrace();
                    // ignore.
                }
            }

            entityNameMap.put(entityClass, entityName);
        }

        return entityName;
    }

    /** The Constant sqlStateForTableNotExists. */
    private static final Set<String> sqlStateForTableNotExists = new HashSet<>();

    static {
        sqlStateForTableNotExists.add("42S02"); // for MySQCF.
        sqlStateForTableNotExists.add("42P01"); // for PostgreSQCF.
        sqlStateForTableNotExists.add("42501"); // for HSQLDB.
    }

    /**
     * Checks if is table not exists exception.
     *
     * @param e the e
     * @return true, if is table not exists exception
     */
    public static boolean isTableNotExistsException(final Throwable e) {
        if (e instanceof SQLException) {
            SQLException sqlException = (SQLException) e;

            if (sqlException.getSQLState() != null && sqlStateForTableNotExists.contains(sqlException.getSQLState())) {
                return true;
            }

            final String msg = N.defaultIfNull(e.getMessage(), "").toLowerCase();
            return N.notNullOrEmpty(msg) && (msg.contains("not exist") || msg.contains("doesn't exist") || msg.contains("not found"));
        } else if (e instanceof UncheckedSQLException) {
            UncheckedSQLException sqlException = (UncheckedSQLException) e;

            if (sqlException.getSQLState() != null && sqlStateForTableNotExists.contains(sqlException.getSQLState())) {
                return true;
            }

            final String msg = N.defaultIfNull(e.getMessage(), "").toLowerCase();
            return N.notNullOrEmpty(msg) && (msg.contains("not exist") || msg.contains("doesn't exist") || msg.contains("not found"));
        }

        return false;
    }
}

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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityUtil.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
public final class EntityUtil {

    /** The Constant DOUBLE_BRACKET. */
    private static final String DOUBLE_BRACKET = "{}";

    /**
     * Instantiates a new entity util.
     */
    private EntityUtil() {
        // no instance.
    }

    /**
     * Clone.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param entity the entity
     * @return
     */
    public static <T> T clone(EntityDefinition entityDef, T entity) {
        if (entity == null) {
            return null;
        }

        Collection<String> propNames = EntityManagerUtil.getSignedPropNames(entityDef, entity);

        return copy(entityDef, entity, propNames, true);
    }

    /**
     * Copy.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param entity the entity
     * @return
     */
    public static <T> T copy(EntityDefinition entityDef, T entity) {
        return copy(entityDef, entity, null);
    }

    /**
     * Copy.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param entity the entity
     * @param propNames the prop names
     * @return
     */
    public static <T> T copy(EntityDefinition entityDef, T entity, Collection<String> propNames) {
        if (entity == null) {
            return null;
        }

        if (propNames == null) {
            propNames = EntityManagerUtil.getSignedPropNames(entityDef, entity);
        }

        return copy(entityDef, entity, propNames, false);
    }

    /**
     * Copy.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param entity the entity
     * @param propNames the prop names
     * @param isDeepCopy the is deep copy
     * @return
     */
    private static <T> T copy(EntityDefinition entityDef, T entity, Collection<String> propNames, boolean isDeepCopy) {
        @SuppressWarnings("unchecked")
        T copy = (T) N.newEntity(entity.getClass(), entityDef.getName());
        MapEntity mapEntity = (entity instanceof MapEntity) ? (MapEntity) entity : null;
        MapEntity copyOfMapEnity = (mapEntity == null) ? null : (MapEntity) copy;
        Property prop = null;
        Object propValue = null;
        EntityDefinition columnEntityDef = null;

        for (String propName : propNames) {
            prop = entityDef.getProperty(propName);

            if (mapEntity == null) {
                propValue = EntityManagerUtil.getPropValueByMethod(entity, prop);
            } else {
                propValue = mapEntity.get(propName);
            }

            if (isDeepCopy && (propValue != null) && prop.getColumnType().isEntity()) {
                columnEntityDef = prop.getColumnEntityDef();

                if (prop.isCollection() && propValue instanceof Collection) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> c = (Collection<Object>) propValue;
                    @SuppressWarnings("unchecked")
                    Collection<Object> newC = N.newInstance(c.getClass());

                    for (Object e : c) {
                        if (e == null) {
                            newC.add(e);
                        } else {
                            newC.add(clone(columnEntityDef, e));
                        }
                    }

                    propValue = newC;
                } else {
                    propValue = clone(columnEntityDef, propValue);
                }
            }

            if (copyOfMapEnity == null) {
                EntityManagerUtil.setPropValueByMethod(copy, prop, propValue);
            } else {
                copyOfMapEnity.set(propName, propValue);
            }
        }

        if (entity instanceof DirtyMarker) {
            DirtyMarker anEntity = (DirtyMarker) entity;
            DirtyMarker anCopy = (DirtyMarker) copy;

            DirtyMarkerUtil.dirtyPropNames(anCopy).clear();
            DirtyMarkerUtil.markDirty(anCopy, DirtyMarkerUtil.dirtyPropNames(anEntity), true);

            DirtyMarkerUtil.setVersion(anCopy, anEntity.version());
        }

        return copy;
    }

    /**
     * Transfer.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param sourceEntity the source entity
     * @param targetClass the target class
     * @return
     */
    public static <T> T transfer(EntityDefinition entityDef, Object sourceEntity, Class<T> targetClass) {
        return transfer(entityDef, sourceEntity, null, targetClass);
    }

    /**
     * Transfer.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param sourceEntity the source entity
     * @param propNames the prop names
     * @param targetClass the target class
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T transfer(EntityDefinition entityDef, Object sourceEntity, Collection<String> propNames, Class<T> targetClass) {
        if (sourceEntity == null) {
            return null;
        }

        T targetEntity = N.newEntity(targetClass, entityDef.getName());

        if (propNames == null) {
            propNames = EntityManagerUtil.getSignedPropNames(entityDef, sourceEntity);
        }

        Class<T> targetEntityClass = (Class<T>) targetEntity.getClass();

        MapEntity sourceMapEntity = (sourceEntity instanceof MapEntity) ? (MapEntity) sourceEntity : null;
        MapEntity targetMapEntity = (targetEntity instanceof MapEntity) ? (MapEntity) targetEntity : null;

        if (targetMapEntity != null) {
            if (sourceMapEntity != null) {
                for (String propName : propNames) {
                    targetMapEntity.set(propName, sourceMapEntity.get(propName));
                }
            } else {
                Property prop = null;
                Object propValue = null;

                for (String propName : propNames) {
                    prop = entityDef.getProperty(propName);
                    propValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
                    targetMapEntity.set(propName, propValue);
                }
            }
        } else {
            Property prop = null;
            Object propValue = null;
            Class<?> propEntityClass = null;
            EntityDefinition columnEntityDef = null;

            for (String propName : propNames) {
                prop = entityDef.getProperty(propName);

                if (sourceMapEntity == null) {
                    propValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
                } else {
                    propValue = sourceMapEntity.get(propName);
                }

                if ((propValue != null) && prop.getColumnType().isEntity()) {
                    columnEntityDef = prop.getColumnEntityDef();

                    if (prop.isCollection() && propValue instanceof Collection) {
                        Class<?> propEleClass = getPropEleClass(targetEntityClass, prop);
                        Collection<Object> c = (Collection<Object>) propValue;
                        Collection<Object> newC = N.newInstance(c.getClass());

                        for (Object e : c) {
                            if (e == null || e.getClass().equals(propEleClass)) {
                                newC.add(e);
                            } else {
                                newC.add(transfer(columnEntityDef, e, propEleClass));
                            }
                        }

                        propValue = newC;
                    } else {
                        propEntityClass = prop.getGetMethod(targetEntityClass).getReturnType();

                        if (!propEntityClass.equals(propValue.getClass())) {
                            propValue = transfer(columnEntityDef, propValue, propEntityClass);
                        }
                    }
                }

                EntityManagerUtil.setPropValueByMethod(targetEntity, prop, propValue);
            }
        }

        if (targetEntity instanceof DirtyMarker) {
            DirtyMarker anTargetDirtyMarker = (DirtyMarker) targetEntity;
            DirtyMarkerUtil.markDirty(anTargetDirtyMarker, false);

            if (sourceEntity instanceof DirtyMarker) {
                DirtyMarker anSourceDirtyMarker = (DirtyMarker) sourceEntity;
                DirtyMarkerUtil.markDirty(anTargetDirtyMarker, DirtyMarkerUtil.dirtyPropNames(anSourceDirtyMarker), true);

                DirtyMarkerUtil.setVersion(anTargetDirtyMarker, anSourceDirtyMarker.version());
            }
        }

        return targetEntity;
    }

    /**
     * Disassemble.
     *
     * @param entityDef the entity def
     * @param sourceEntity the source entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public static MapEntity disassemble(EntityDefinition entityDef, Object sourceEntity) {
        if (sourceEntity == null) {
            return null;
        }

        MapEntity targetEntity = new MapEntity(entityDef.getName());
        MapEntity sourcerMapEntity = (sourceEntity instanceof MapEntity) ? (MapEntity) sourceEntity : null;
        Collection<String> signedPropNames = EntityManagerUtil.getSignedPropNames(entityDef, sourceEntity);
        Property prop = null;
        Object propValue = null;
        EntityDefinition columnEntityDef = null;

        for (String propName : signedPropNames) {
            prop = entityDef.getProperty(propName);

            if (sourcerMapEntity == null) {
                propValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
            } else {
                propValue = sourcerMapEntity.get(propName);
            }

            if ((propValue != null) && !(propValue instanceof MapEntity) && prop.getColumnType().isEntity()) {
                columnEntityDef = prop.getColumnEntityDef();

                if (prop.isCollection() && propValue instanceof Collection) {
                    Collection<Object> propEntityList = (Collection<Object>) N.newInstance(propValue.getClass());

                    for (Object e : (Collection<Object>) propValue) {
                        if (e == null || e instanceof MapEntity) {
                            propEntityList.add(e);
                        } else {
                            propEntityList.add(disassemble(columnEntityDef, e));
                        }
                    }

                    propValue = propEntityList;
                } else {
                    propValue = disassemble(columnEntityDef, propValue);
                }
            }

            targetEntity.set(propName, propValue);
        }

        return targetEntity;
    }

    /**
     * Assemble.
     *
     * @param <T> the generic type
     * @param entityDef the entity def
     * @param sourceEntity the source entity
     * @param targetClass the target class
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T assemble(EntityDefinition entityDef, MapEntity sourceEntity, Class<T> targetClass) {
        if (sourceEntity == null) {
            return null;
        }

        T targetEntity = N.newEntity(targetClass, entityDef.getName());
        Property prop = null;
        Object propValue = null;
        Class<?> propEntityClass = null;
        EntityDefinition columnEntityDef = null;

        for (String propName : DirtyMarkerUtil.signedPropNames(sourceEntity)) {
            prop = entityDef.getProperty(propName);
            propValue = sourceEntity.get(propName);

            if ((propValue != null) && prop.getColumnType().isEntity()) {
                columnEntityDef = prop.getColumnEntityDef();

                if (prop.isCollection() && propValue instanceof Collection) {
                    Class<?> propEleClass = getPropEleClass(targetClass, prop);
                    Collection<Object> c = (Collection<Object>) propValue;
                    Collection<Object> newC = N.newInstance(c.getClass());

                    for (Object e : c) {
                        if (e == null || e.getClass().equals(propEleClass) || !(e instanceof MapEntity)) {
                            newC.add(e);
                        } else {
                            newC.add(assemble(columnEntityDef, (MapEntity) e, propEleClass));
                        }
                    }

                    propValue = newC;
                } else {
                    propEntityClass = prop.getGetMethod(targetClass).getReturnType();

                    if (!propEntityClass.equals(propValue.getClass()) && propValue instanceof MapEntity) {
                        propValue = assemble(columnEntityDef, (MapEntity) propValue, propEntityClass);
                    }
                }
            }

            EntityManagerUtil.setPropValueByMethod(targetEntity, prop, propValue);
        }

        return targetEntity;
    }

    /**
     * Refresh.
     *
     * @param entityDef the entity def
     * @param sourceEntity the source entity
     * @param targetEntity the target entity
     */
    public static void refresh(EntityDefinition entityDef, Object sourceEntity, Object targetEntity) {
        if (sourceEntity == null) {
            return;
        }

        Collection<String> signedPropNames = EntityManagerUtil.getSignedPropNames(entityDef, sourceEntity);

        if (targetEntity instanceof MapEntity) {
            MapEntity targetMapEntity = (MapEntity) targetEntity;

            if (sourceEntity instanceof MapEntity) {
                MapEntity sourceMapEntity = (MapEntity) sourceEntity;

                for (String propName : signedPropNames) {
                    targetMapEntity.set(propName, sourceMapEntity.get(propName));
                }
            } else {
                Property prop = null;
                Object propValue = null;

                for (String propName : signedPropNames) {
                    prop = entityDef.getProperty(propName);
                    propValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
                    targetMapEntity.set(propName, propValue);
                }
            }
        } else if (sourceEntity instanceof MapEntity) {
            MapEntity sourceMapEntity = (MapEntity) sourceEntity;
            Property prop = null;
            Object propValue = null;

            for (String propName : signedPropNames) {
                prop = entityDef.getProperty(propName);
                propValue = sourceMapEntity.get(propName);
                EntityManagerUtil.setPropValueByMethod(targetEntity, prop, propValue);
            }
        } else {
            Property prop = null;
            Object propValue = null;

            for (String propName : signedPropNames) {
                prop = entityDef.getProperty(propName);
                propValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
                EntityManagerUtil.setPropValueByMethod(targetEntity, prop, propValue);
            }
        }

        if (targetEntity instanceof DirtyMarker) {
            DirtyMarker anTargetEntity = (DirtyMarker) targetEntity;
            DirtyMarkerUtil.markDirty(anTargetEntity, signedPropNames, false);

            if (sourceEntity instanceof DirtyMarker) {
                DirtyMarkerUtil.setVersion(anTargetEntity, ((DirtyMarker) sourceEntity).version());
            }
        }
    }

    /**
     * Merge.
     *
     * @param entityDef the entity def
     * @param sourceEntity the source entity
     * @param targetEntity the target entity
     * @param ignoreNullValue the ignore null value
     * @param ignoreEqualValue the ignore equal value
     */
    public static void merge(EntityDefinition entityDef, Object sourceEntity, Object targetEntity, boolean ignoreNullValue, boolean ignoreEqualValue) {
        if (sourceEntity == null) {
            return;
        }

        final Map<String, Object> sourceMap = sourceEntity instanceof Map ? (Map<String, Object>) sourceEntity : null;
        final MapEntity sourceMapEntity = (sourceEntity instanceof MapEntity) ? (MapEntity) sourceEntity : null;
        final MapEntity targetMapEntity = (targetEntity instanceof MapEntity) ? (MapEntity) targetEntity : null;

        Property prop = null;
        Object sourcePropValue = null;
        Object targetPropValue = null;

        Collection<String> idPropNames = entityDef.getIdPropertyNameList();
        Collection<String> signedPropNames = sourceMap != null ? sourceMap.keySet() : EntityManagerUtil.getSignedPropNames(entityDef, sourceEntity);

        if (sourceEntity instanceof DirtyMarker) {
            final Collection<String> dirtyPropNames = DirtyMarkerUtil.dirtyPropNames((DirtyMarker) sourceEntity);

            for (String propName : signedPropNames) {
                if ((idPropNames.size() > 0) && idPropNames.contains(propName)) {
                    continue;
                }

                prop = entityDef.getProperty(propName);

                if (dirtyPropNames.contains(propName)) {
                    if (sourceMapEntity == null) {
                        sourcePropValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
                    } else {
                        sourcePropValue = sourceMapEntity.get(propName);
                    }

                    if (sourcePropValue == null && ignoreNullValue) {
                        continue;
                    }

                    if (ignoreEqualValue) {
                        if (targetMapEntity == null) {
                            targetPropValue = EntityManagerUtil.getPropValueByMethod(targetEntity, prop);
                        } else {
                            targetPropValue = targetMapEntity.get(propName);
                        }

                        if (N.equals(sourcePropValue, targetPropValue)) {
                            continue;
                        }
                    }

                    if (targetMapEntity == null) {
                        EntityManagerUtil.setPropValueByMethod(targetEntity, prop, sourcePropValue);
                    } else {
                        targetMapEntity.set(propName, sourcePropValue);
                    }
                } else if (prop.getColumnType().isEntity()) {
                    if (sourceMapEntity == null) {
                        sourcePropValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);
                    } else {
                        sourcePropValue = sourceMapEntity.get(propName);
                    }

                    if (sourcePropValue == null) {
                        continue;
                    }

                    if (ignoreEqualValue) {
                        if (targetMapEntity == null) {
                            targetPropValue = EntityManagerUtil.getPropValueByMethod(targetEntity, prop);
                        } else {
                            targetPropValue = targetMapEntity.get(propName);
                        }

                        if (N.equals(sourcePropValue, targetPropValue)) {
                            continue;
                        }
                    }

                    boolean isUpdatedProp = false;

                    if (prop.isCollection() && sourcePropValue instanceof Collection) {
                        @SuppressWarnings("unchecked")
                        Collection<Object> c = (Collection<Object>) sourcePropValue;

                        for (Object e : c) {
                            if (e instanceof DirtyMarker) {
                                isUpdatedProp = DirtyMarkerUtil.isDirty((DirtyMarker) e);
                            } else {
                                isUpdatedProp = e != null;
                            }

                            if (isUpdatedProp) {
                                break;
                            }
                        }
                    } else {
                        if (sourcePropValue instanceof DirtyMarker) {
                            isUpdatedProp = DirtyMarkerUtil.isDirty((DirtyMarker) sourcePropValue);
                        } else {
                            isUpdatedProp = true;
                        }
                    }

                    if (isUpdatedProp) {
                        if (targetMapEntity == null) {
                            EntityManagerUtil.setPropValueByMethod(targetEntity, prop, sourcePropValue);
                        } else {
                            targetMapEntity.set(propName, sourcePropValue);
                        }
                    }
                }
            }
        } else if (sourceMap != null) {
            for (String propName : signedPropNames) {
                if ((idPropNames.size() > 0) && idPropNames.contains(propName)) {
                    continue;
                }

                prop = entityDef.getProperty(propName);
                sourcePropValue = sourceMap.get(propName);

                if (ignoreEqualValue) {
                    if (targetMapEntity == null) {
                        targetPropValue = EntityManagerUtil.getPropValueByMethod(targetEntity, prop);
                    } else {
                        targetPropValue = targetMapEntity.get(propName);
                    }

                    if (N.equals(sourcePropValue, targetPropValue)) {
                        continue;
                    }
                }

                if (targetMapEntity == null) {
                    EntityManagerUtil.setPropValueByMethod(targetEntity, prop, sourcePropValue);
                } else {
                    targetMapEntity.set(propName, sourcePropValue);
                }
            }
        } else {
            for (String propName : signedPropNames) {
                if ((idPropNames.size() > 0) && idPropNames.contains(propName)) {
                    continue;
                }

                prop = entityDef.getProperty(propName);
                sourcePropValue = EntityManagerUtil.getPropValueByMethod(sourceEntity, prop);

                if (sourcePropValue == null) {
                    continue;
                }

                if (ignoreEqualValue) {
                    if (targetMapEntity == null) {
                        targetPropValue = EntityManagerUtil.getPropValueByMethod(targetEntity, prop);
                    } else {
                        targetPropValue = targetMapEntity.get(propName);
                    }

                    if (N.equals(sourcePropValue, targetPropValue)) {
                        continue;
                    }
                }

                if (targetMapEntity == null) {
                    EntityManagerUtil.setPropValueByMethod(targetEntity, prop, sourcePropValue);
                } else {
                    targetMapEntity.set(propName, sourcePropValue);
                }
            }
        }
    }

    /**
     * Hash code.
     *
     * @param entityDef the entity def
     * @param thisEntity the this entity
     * @return
     */
    public static int hashCode(EntityDefinition entityDef, Object thisEntity) {
        int h = 17;
        h = (h * 31) + entityDef.getName().hashCode();

        Collection<String> signedPropNames = EntityManagerUtil.getSignedPropNames(entityDef, thisEntity);
        List<String> idPropNames = entityDef.getIdPropertyNameList();

        Property prop = null;
        Object propValue = null;
        for (String propName : idPropNames) {
            if (signedPropNames.contains(propName)) {
                prop = entityDef.getProperty(propName);
                propValue = EntityManagerUtil.getPropValue(thisEntity, prop);

                h = (h * 31) + N.hashCode(propValue);
            }
        }

        for (String propName : signedPropNames) {
            if (!idPropNames.contains(propName)) {
                prop = entityDef.getProperty(propName);
                propValue = EntityManagerUtil.getPropValue(thisEntity, prop);

                h = (h * 31) + N.hashCode(propValue);
            }
        }

        return h;
    }

    /**
     * Equals.
     *
     * @param entityDef the entity def
     * @param entity the entity
     * @param anObject the an object
     * @return true, if successful
     */
    public static boolean equals(EntityDefinition entityDef, Object entity, Object anObject) {
        if (entity == anObject) {
            return true;
        }

        if (((entity == null) && (anObject != null)) || ((entity != null) && (anObject == null))) {
            return false;
        }

        if (!entity.getClass().equals(anObject.getClass())) {
            return false;
        }

        if (entity instanceof MapEntity) {
            final MapEntity anMapEntity = (MapEntity) entity;
            final MapEntity anotherMapEntity = (MapEntity) anObject;
            final Set<String> signedPropNames = new HashSet<>(DirtyMarkerUtil.signedPropNames(anMapEntity));
            signedPropNames.addAll(DirtyMarkerUtil.signedPropNames(anotherMapEntity));

            Collection<String> idPropNames = entityDef.getIdPropertyNameList();
            Object propValue = null;
            Object anotherPropValue = null;

            for (String propName : idPropNames) {
                if (signedPropNames.contains(propName)) {
                    propValue = anMapEntity.get(propName);
                    anotherPropValue = anotherMapEntity.get(propName);

                    if (!(N.equals(propValue, anotherPropValue))) {
                        return false;
                    }
                }
            }

            for (String propName : signedPropNames) {
                propValue = anMapEntity.get(propName);
                anotherPropValue = anotherMapEntity.get(propName);

                if (!(N.equals(propValue, anotherPropValue))) {
                    return false;
                }
            }
        } else {
            Set<String> signedPropNames = new HashSet<>(EntityManagerUtil.getSignedPropNames(entityDef, entity));
            signedPropNames.addAll(EntityManagerUtil.getSignedPropNames(entityDef, anObject));

            Collection<String> idPropNames = entityDef.getIdPropertyNameList();
            Property prop = null;
            Object propValue = null;
            Object anotherPropValue = null;

            for (String propName : idPropNames) {
                if (signedPropNames.contains(propName)) {
                    prop = entityDef.getProperty(propName);
                    propValue = EntityManagerUtil.getPropValueByMethod(entity, prop);
                    anotherPropValue = EntityManagerUtil.getPropValueByMethod(anObject, prop);

                    if (!(N.equals(propValue, anotherPropValue))) {
                        return false;
                    }
                }
            }

            for (String propName : signedPropNames) {
                prop = entityDef.getProperty(propName);
                propValue = EntityManagerUtil.getPropValueByMethod(entity, prop);
                anotherPropValue = EntityManagerUtil.getPropValueByMethod(anObject, prop);

                if (!(N.equals(propValue, anotherPropValue))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * To string.
     *
     * @param entityDef the entity def
     * @param thisEntity the this entity
     * @return
     */
    public static String toString(EntityDefinition entityDef, Object thisEntity) {
        Collection<String> signedPropNames = EntityManagerUtil.getSignedPropNames(entityDef, thisEntity);

        if (signedPropNames.size() == 0) {
            return DOUBLE_BRACKET;
        }

        final StringBuilder sb = Objectory.createStringBuilder();

        final boolean hasIdProperty = entityDef.getIdPropertyList().size() > 0;
        Property prop = null;
        Object propValue = null;
        int index = 0;

        sb.append('{');
        if (hasIdProperty) {
            for (String propName : signedPropNames) {
                prop = entityDef.getProperty(propName);
                if (prop.isId()) {
                    if (index++ > 0) {
                        sb.append(", ");
                    }

                    propValue = EntityManagerUtil.getPropValue(thisEntity, prop);

                    sb.append(prop.getName());
                    sb.append('=');
                    sb.append(N.toString(propValue));
                }
            }
        }

        for (String propName : signedPropNames) {
            prop = entityDef.getProperty(propName);

            if (!prop.isId()) {
                if (index++ > 0) {
                    sb.append(", ");
                }

                propValue = EntityManagerUtil.getPropValue(thisEntity, prop);
                sb.append(prop.getName());
                sb.append('=');
                sb.append(N.toString(propValue));
            }
        }

        sb.append('}');

        String st = sb.toString();

        Objectory.recycle(sb);

        return st;
    }

    /**
     * Gets the prop ele class.
     *
     * @param <T> the generic type
     * @param entityClass the entity class
     * @param prop the prop
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getPropEleClass(Class<?> entityClass, Property prop) {
        Method method = prop.getSetMethod(entityClass);
        Class<?> propValueClass = null;

        if (prop.isCollection()) {
            propValueClass = (Class<?>) ((ParameterizedType) (method.getGenericParameterTypes()[0])).getActualTypeArguments()[0];
        } else {
            propValueClass = method.getParameterTypes()[0];
        }

        return (T) propValueClass;

        /*
         * String propClassName = prop.getColumnEntityDefinition().getName(); propClassName =
         * propClassName.replace(PERIOD, DOLLAR); propClassName = (clazz.getPackage() == null) ? propClassName :
         * (clazz.getPackage() .getName() + PERIOD + propClassName);
         * 
         * try { return (T) N.forClass(propClassName); } catch (ClassNotFoundException e) { throw new
         * AbacusException(e); }
         */
    }
}

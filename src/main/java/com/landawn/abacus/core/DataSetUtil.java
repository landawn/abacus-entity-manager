/*
 * Copyright (c) 2019, Haiyang Li.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.parser.ParserUtil;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
final class DataSetUtil {
    private DataSetUtil() {
        // no instance.
    }

    static <T> T row2Entity(final Class<T> targetType, final RowDataSet dataSet, final EntityDefinition entityDef, final int rowNum) {
        final List<T> entities = row2Entity(targetType, dataSet, entityDef, rowNum, rowNum + 1);

        return N.isNullOrEmpty(entities) ? null : entities.get(0);
    }

    static <T> List<T> row2Entity(final Class<T> targetType, final RowDataSet dataSet, final EntityDefinition entityDef, final int fromRowIndex,
            final int toRowIndex) {
        // TODO [performance improvement]. how to improve performance? 

        final List<T> entities = new ArrayList<>(toRowIndex - fromRowIndex);

        if (toRowIndex == fromRowIndex) {
            return entities;
        }

        final List<String> _columnNameList = dataSet._columnNameList;
        final List<List<Object>> _columnList = dataSet._columnList;

        final String entityName = entityDef.getName();
        final int[] entityPropIndexes = new int[_columnNameList.size()];
        final List<Property> propList = new ArrayList<>(entityPropIndexes.length);

        int columnIndex = -1;
        Property prop = null;

        for (String propName : _columnNameList) {
            prop = entityDef.getProperty(propName);

            if ((prop != null) && (prop.getEntityDefinition() == entityDef)) {
                columnIndex = dataSet.getColumnIndex(propName);
                entityPropIndexes[propList.size()] = columnIndex;
                propList.add(prop);
            }
        }

        final boolean isMapEntity = MapEntity.class.isAssignableFrom(targetType);
        final boolean isDirtyMarker = DirtyMarkerUtil.isDirtyMarker(targetType);
        MapEntity mapEntity = null;

        Object entity = null;

        for (int rowIndex = fromRowIndex; rowIndex < toRowIndex; rowIndex++) {
            entity = N.newEntity(targetType, entityName);
            mapEntity = isMapEntity ? (MapEntity) entity : null;

            if (propList.size() > 0) {
                boolean hasNonNullPropValue = false;

                Object propValue = null;

                for (int i = 0, size = propList.size(); i < size; i++) {
                    prop = propList.get(i);
                    propValue = _columnList.get(entityPropIndexes[i]).get(rowIndex);

                    if (propValue == null) {
                        propValue = N.defaultValueOf(prop.getType().clazz());
                    } else {
                        if (prop.isCollection() && !(propValue instanceof Collection)) {
                            propValue = prop.asCollection(propValue);
                        }

                        hasNonNullPropValue = true;
                    }

                    if (isMapEntity) {
                        mapEntity.set(prop.getName(), propValue);
                    } else {
                        setPropValueByMethod(entity, prop, propValue);
                    }
                }

                if (hasNonNullPropValue) {
                    if (isDirtyMarker) {
                        DirtyMarkerUtil.markDirty((DirtyMarker) entity, false);
                    }
                } else {
                    entity = null;
                }
            }

            entities.add((T) entity);
        }

        return entities;
    }

    static void combine(final RowDataSet dataSet, final Property prop, final String... idPropNames) {
        // TODO [performance improvement]. How to improve performance? 

        final String byPropName = prop.getName();
        final int columnIndex = dataSet.checkColumnName(byPropName);

        if (idPropNames.length == 0) {
            throw new IllegalArgumentException("'idPropNames' can't be empty");
        }

        final int[] idPropIndexes = new int[idPropNames.length];

        for (int i = 0; i < idPropIndexes.length; i++) {
            idPropIndexes[i] = dataSet.checkColumnName(idPropNames[i]);
        }

        final int size = dataSet.size();

        if (size == 0) {
            return;
        }

        final List<String> _columnNameList = dataSet._columnNameList;
        final List<List<Object>> _columnList = dataSet._columnList;
        final int columnCount = _columnNameList.size();
        final List<List<Object>> newColumnList = new ArrayList<>(columnCount);

        for (int i = 0; i < columnCount; i++) {
            newColumnList.add(new ArrayList<>(size));
        }

        final Map<Object, Set<Object>> idPropValueSetMap = new HashMap<>();

        Object id = null;
        List<Object> idList = null;
        Set<Object> propValueSet = null;
        Object propValue = null;

        for (int i = 0; i < size; i++) {
            if (idPropIndexes.length == 1) {
                id = _columnList.get(idPropIndexes[0]).get(i);
            } else {
                idList = new ArrayList<>();

                for (int index : idPropIndexes) {
                    idList.add(_columnList.get(index).get(i));
                }

                id = idList;
            }

            propValueSet = idPropValueSetMap.get(id);

            if (propValueSet == null) {
                propValueSet = new LinkedHashSet<>();
                idPropValueSetMap.put(id, propValueSet);

                for (int k = 0; k < columnCount; k++) {
                    newColumnList.get(k).add(_columnList.get(k).get(i));
                }

                newColumnList.get(columnIndex).set(newColumnList.get(columnIndex).size() - 1, propValueSet);
            }

            propValue = _columnList.get(columnIndex).get(i);

            if (propValue != null) {
                propValueSet.add(propValue);
            }
        }

        final int newSize = newColumnList.get(0).size();

        for (int i = 0; i < newSize; i++) {
            newColumnList.get(columnIndex).set(i, prop.asCollection((Collection<?>) newColumnList.get(columnIndex).get(i)));
        }

        dataSet._columnList = newColumnList;
        dataSet._currentRowNum = 0;
        dataSet.modCount++;
    }

    private static void setPropValueByMethod(final Object entity, final Property prop, Object propValue) {
        if (propValue == null) {
            propValue = N.defaultValueOf(prop.getType().clazz());
        }

        // N.setPropValue(entity, prop.getSetMethod(entity.getClass()), propValue);
        ParserUtil.getEntityInfo(entity.getClass()).setPropValue(entity, prop.getName(), propValue);
    }
}

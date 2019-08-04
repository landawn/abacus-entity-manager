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

package com.landawn.abacus.idGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.TypeAttrParser;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating IdGenerator objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class IdGeneratorFactory {
    /**
     * Field registeredIdGenerator.
     */
    private static final Map<String, Class<? extends IdGenerator<?>>> registeredIdGenerator = new ConcurrentHashMap<String, Class<? extends IdGenerator<?>>>();

    static {
        initBuiltinIdGeneratorName(LocalIdGenerator.class);
        initBuiltinIdGeneratorName(SequenceIdGenerator.class);
        initBuiltinIdGeneratorName(AutoIncrementIdGenerator.class);
        initBuiltinIdGeneratorName(UUIDIdGenerator.class);
    }

    /**
     * Constructor for IdGeneratorFactory.
     */
    private IdGeneratorFactory() {
        // singleton.
    }

    /**
     * Method create.
     *
     * @param <T> the generic type
     * @param idGeneratorAttr the id generator attr
     * @param prop the prop
     * @return IdGenerator
     */
    public static <T> IdGenerator<T> create(String idGeneratorAttr, Property prop) {
        TypeAttrParser attrResult = TypeAttrParser.parse(idGeneratorAttr);
        String clsName = attrResult.getClassName();

        Class<? extends IdGenerator<?>> clazz = registeredIdGenerator.get(clsName);

        if (clazz == null) {
            clazz = registeredIdGenerator.get(clsName.toUpperCase());
        }

        if (clazz == null) {
            clazz = registeredIdGenerator.get(clsName.toLowerCase());
        }

        if (clazz == null) {
            clazz = ClassUtil.forClass(clsName);
            registerIdGenerator(clsName, clazz);
        }

        return TypeAttrParser.newInstance(clazz, idGeneratorAttr, Property.class, prop);
    }

    /**
     * Gets the default id generator.
     *
     * @param <T> the generic type
     * @param prop the prop
     * @return the default id generator
     */
    public static <T> IdGenerator<T> getDefaultIdGenerator(Property prop) {
        if (prop.getType().clazz().equals(String.class)) {
            return create(UUIDIdGenerator.class.getSimpleName(), prop);
        } else {
            return create(LocalIdGenerator.class.getSimpleName(), prop);
        }
    }

    /**
     * Method initBuiltinIdGeneratorName.
     *
     * @param clazz the clazz
     */
    private static void initBuiltinIdGeneratorName(Class<? extends IdGenerator<?>> clazz) {
        registerIdGenerator(clazz.getSimpleName().replaceAll(IdGenerator.ID_GENERATOR, N.EMPTY_STRING), clazz);
        registerIdGenerator(clazz.getSimpleName(), clazz);
    }

    /**
     * Register id generator.
     *
     * @param idGenerator the id generator
     * @param clazz the clazz
     */
    public static void registerIdGenerator(String idGenerator, Class<? extends IdGenerator<?>> clazz) {
        registeredIdGenerator.put(idGenerator, clazz);
        registeredIdGenerator.put(idGenerator.toUpperCase(), clazz);
        registeredIdGenerator.put(idGenerator.toLowerCase(), clazz);
    }
}

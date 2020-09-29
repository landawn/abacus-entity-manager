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

package com.landawn.abacus.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.EntityManager;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.TypeAttrParser;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Handler objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class HandlerFactory {
    /**
     * Field registeredHandler.
     */
    private static final Map<String, Class<? extends Handler<?>>> registeredHandler = new ConcurrentHashMap<>();

    /**
     * Constructor for HandlerFactory.
     */
    private HandlerFactory() {
        // singleton.
    }

    /**
     *
     * @param <T>
     * @param entityManager
     * @param handlerAttr
     * @return Handler
     */
    public static <T> Handler<T> create(EntityManager<T> entityManager, String handlerAttr) {
        Class<?> cls = null;

        TypeAttrParser attrResult = TypeAttrParser.parse(handlerAttr);
        String className = attrResult.getClassName();

        if (registeredHandler.containsKey(className)) {
            cls = registeredHandler.get(className);
        } else {
            cls = ClassUtil.forClass(className);
        }

        return TypeAttrParser.newInstance(cls, handlerAttr, EntityManager.class, entityManager);
    }

    /**
     *
     * @param handler
     * @param clazz
     */
    @SuppressWarnings("unchecked")
    public static void registerHandler(String handler, @SuppressWarnings("rawtypes") Class<? extends Handler> clazz) {
        registeredHandler.put(handler, (Class<? extends Handler<?>>) clazz);
    }
}

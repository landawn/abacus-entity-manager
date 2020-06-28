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

package com.landawn.abacus.core.interpreter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.core.command.Command;
import com.landawn.abacus.metadata.EntityDefinition;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Interpreter {
    //    Command interpretAdd(EntityDefinition entityDef, Map<String, Object> props, Map<String, Object> options);

    /**
     *
     * @param entityDef
     * @param propsList
     * @param options
     * @return
     */
    Command interpretAdd(EntityDefinition entityDef, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     *
     * @param entityDef
     * @param props
     * @param condition
     * @param options
     * @return
     */
    Command interpretUpdate(EntityDefinition entityDef, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     *
     * @param entityDef
     * @param condition
     * @param options
     * @return
     */
    Command interpretDelete(EntityDefinition entityDef, Condition condition, Map<String, Object> options);

    /**
     *
     * @param entityDef
     * @param propNames
     * @param condition
     * @param options
     * @return
     */
    Command interpretQuery(EntityDefinition entityDef, Collection<String> propNames, Condition condition, Map<String, Object> options);

    /**
     *
     * @param entityDef
     * @param query
     * @param parameters
     * @param options
     * @return
     */
    Command interpretQuery(EntityDefinition entityDef, String query, List<?> parameters, Map<String, Object> options);

    /**
     *
     * @param entityDef
     * @param condition
     * @return
     */
    Command interpretCondition(EntityDefinition entityDef, Condition condition);
}

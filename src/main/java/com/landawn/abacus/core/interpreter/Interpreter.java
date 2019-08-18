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
 * The Interface Interpreter.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Interpreter {
    //    Command interpretAdd(EntityDefinition entityDef, Map<String, Object> props, Map<String, Object> options);

    /**
     * Interpret add.
     *
     * @param entityDef the entity def
     * @param propsList the props list
     * @param options the options
     * @return
     */
    Command interpretAdd(EntityDefinition entityDef, List<Map<String, Object>> propsList, Map<String, Object> options);

    /**
     * Interpret update.
     *
     * @param entityDef the entity def
     * @param props the props
     * @param condition the condition
     * @param options the options
     * @return
     */
    Command interpretUpdate(EntityDefinition entityDef, Map<String, Object> props, Condition condition, Map<String, Object> options);

    /**
     * Interpret delete.
     *
     * @param entityDef the entity def
     * @param condition the condition
     * @param options the options
     * @return
     */
    Command interpretDelete(EntityDefinition entityDef, Condition condition, Map<String, Object> options);

    /**
     * Interpret query.
     *
     * @param entityDef the entity def
     * @param propNames the prop names
     * @param condition the condition
     * @param options the options
     * @return
     */
    Command interpretQuery(EntityDefinition entityDef, Collection<String> propNames, Condition condition, Map<String, Object> options);

    /**
     * Interpret query.
     *
     * @param entityDef the entity def
     * @param query the query
     * @param parameters the parameters
     * @param options the options
     * @return
     */
    Command interpretQuery(EntityDefinition entityDef, String query, List<?> parameters, Map<String, Object> options);

    /**
     * Interpret condition.
     *
     * @param entityDef the entity def
     * @param condition the condition
     * @return
     */
    Command interpretCondition(EntityDefinition entityDef, Condition condition);
}

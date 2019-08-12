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

package com.landawn.abacus.core.command;

import java.util.Map;

import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.OperationType;

// TODO: Auto-generated Javadoc
/**
 * The Interface Command.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public interface Command {

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    OperationType getOperationType();

    /**
     * Gets the entity def.
     *
     * @return the entity def
     */
    EntityDefinition getEntityDef();

    /**
     * Gets the options.
     *
     * @return the options
     */
    Map<String, Object> getOptions();

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    Object[] getParameters();

    /**
     * Gets the parameter types.
     *
     * @return the parameter types
     */
    Type<Object>[] getParameterTypes();

    /**
     * Gets the parameter.
     *
     * @param index the index
     * @return the parameter
     */
    Object getParameter(int index);

    /**
     * Gets the parameter type.
     *
     * @param index the index
     * @return the parameter type
     */
    Type<Object> getParameterType(int index);

    /**
     * Sets the parameter.
     *
     * @param index the index
     * @param value the value
     * @param type the type
     */
    void setParameter(int index, Object value, Type<Object> type);

    /**
     * Gets the parameter.
     *
     * @param parameterName the parameter name
     * @return the parameter
     */
    Object getParameter(String parameterName);

    /**
     * Gets the parameter type.
     *
     * @param parameterName the parameter name
     * @return the parameter type
     */
    Type<Object> getParameterType(String parameterName);

    /**
     * Sets the parameter.
     *
     * @param parameterName the parameter name
     * @param value the value
     * @param type the type
     */
    void setParameter(String parameterName, Object value, Type<Object> type);

    /**
     * Gets the parameter count.
     *
     * @return the parameter count
     */
    int getParameterCount();

    /**
     * Clear parameters.
     */
    void clearParameters();

    /**
     * Copy.
     *
     * @param <T> the generic type
     * @return the t
     */
    <T extends Command> T copy();

    /**
     * Clear.
     */
    void clear();
}
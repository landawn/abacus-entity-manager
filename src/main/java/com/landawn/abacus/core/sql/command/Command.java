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

package com.landawn.abacus.core.sql.command;

import java.util.Map;

import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.OperationType;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public interface Command {

    OperationType getOperationType();

    EntityDefinition getEntityDef();

    Map<String, Object> getOptions();

    Object[] getParameters();

    Type<Object>[] getParameterTypes();

    Object getParameter(int index);

    Type<Object> getParameterType(int index);

    void setParameter(int index, Object value, Type<Object> type);

    Object getParameter(String parameterName);

    Type<Object> getParameterType(String parameterName);

    void setParameter(String parameterName, Object value, Type<Object> type);

    int getParameterCount();

    void clearParameters();

    <T extends Command> T copy();

    void clear();
}

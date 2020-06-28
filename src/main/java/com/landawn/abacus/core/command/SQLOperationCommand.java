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

import static com.landawn.abacus.util.WD.COMMA_SPACE;
import static com.landawn.abacus.util.WD._BRACE_L;
import static com.landawn.abacus.util.WD._BRACE_R;
import static com.landawn.abacus.util.WD._EQUAL;
import static com.landawn.abacus.util.WD._SPACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Objectory;
import com.landawn.abacus.util.OperationType;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLOperationCommand extends SQLCondCommand {
    
    /** The operation type. */
    private final OperationType operationType;
    
    /** The entity def. */
    private final EntityDefinition entityDef;
    
    /** The options. */
    private final Map<String, Object> options;

    /** The target prop names. */
    private Collection<String> targetPropNames;
    
    /** The batch parameter values. */
    private List<Object[]> batchParameterValues;
    
    /** The is batch. */
    private boolean isBatch = false;

    /**
     * Instantiates a new SQL operation command.
     *
     * @param operationType
     * @param entityDef
     * @param options
     */
    public SQLOperationCommand(OperationType operationType, EntityDefinition entityDef, Map<String, Object> options) {
        super();

        this.entityDef = entityDef;
        this.operationType = operationType;
        this.options = options;
    }

    /**
     * Gets the operation type.
     *
     * @return
     */
    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    /**
     * Gets the entity def.
     *
     * @return
     */
    @Override
    public EntityDefinition getEntityDef() {
        return entityDef;
    }

    /**
     * Gets the options.
     *
     * @return
     */
    @Override
    public Map<String, Object> getOptions() {
        return options;
    }

    /**
     * Gets the target prop names.
     *
     * @return
     */
    public Collection<String> getTargetPropNames() {
        return targetPropNames;
    }

    /**
     * Sets the target prop names.
     *
     * @param propNames the new target prop names
     */
    public void setTargetPropNames(Collection<String> propNames) {
        targetPropNames = propNames;
    }

    /**
     * Adds the batch.
     */
    public void addBatch() {
        if (batchParameterValues == null) {
            batchParameterValues = new ArrayList<>();
        }

        batchParameterValues.add(N.copyOfRange(parameterValues, 0, parameterCount));

        Arrays.fill(parameterValues, null);
        parameterCount = 0;

        isBatch = true;
    }

    /**
     * Gets the batch parameters.
     *
     * @return
     */
    public List<Object[]> getBatchParameters() {
        return batchParameterValues;
    }

    /**
     * Clear batch parameters.
     */
    public void clearBatchParameters() {
        if (batchParameterValues != null) {
            batchParameterValues.clear();

            //
            // if (parameterCount == 0) {
            // Arrays.fill(parameterTypes, null);
            // }
            isBatch = false;
        }
    }

    /**
     * Checks if is batch.
     *
     * @return true, if is batch
     */
    public boolean isBatch() {
        return isBatch;
    }

    /**
     *
     * @param sqlCmd
     */
    @Override
    public void combine(SQLCommand sqlCmd) {
        super.combine(sqlCmd);

        if (sqlCmd instanceof SQLOperationCommand && ((SQLOperationCommand) sqlCmd).isBatch()) {
            if (batchParameterValues == null) {
                batchParameterValues = new ArrayList<>();
            }

            batchParameterValues.addAll(((SQLOperationCommand) sqlCmd).batchParameterValues);
        }
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        SQLOperationCommand copy = (SQLOperationCommand) super.clone();

        if (batchParameterValues != null) {
            copy.batchParameterValues = new ArrayList<>(batchParameterValues);
        }

        if (targetPropNames != null) {
            copy.targetPropNames = N.newInstance(targetPropNames.getClass());
            copy.targetPropNames.addAll(targetPropNames);
        }

        return copy;
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        super.clear();

        targetPropNames = null;
        batchParameterValues = null;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int h = 17;
        h = (h * 31) + sql.hashCode();

        h = (h * 31) + (isBatch() ? N.hashCode(batchParameterValues) : N.hashCode(parameterValues));

        return h;
    }

    /**
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SQLOperationCommand) {
            SQLOperationCommand other = (SQLOperationCommand) obj;

            if (sql.equals(other.sql) && (parameterCount == other.parameterCount)) {
                if (isBatch()) {
                    if (!other.isBatch) {
                        return false;
                    }

                    if (batchParameterValues == other.batchParameterValues) {
                        return true;
                    }

                    if (batchParameterValues == null || other.batchParameterValues == null
                            || batchParameterValues.size() != other.batchParameterValues.size()) {
                        return false;
                    }

                    for (int i = 0, size = batchParameterValues.size(); i < size; i++) {
                        if (!N.equals(batchParameterValues.get(i), other.batchParameterValues.get(i))) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return N.equals(parameterValues, other.parameterValues);
                }
            }
        }

        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        if (!isBatch() && (parameterCount == 0)) {
            return sql;
        } else {
            final StringBuilder sb = Objectory.createStringBuilder();

            sb.append(sql);
            sb.append(_SPACE);
            sb.append(_BRACE_L);

            if (isBatch()) {
                for (int k = 0, batchCount = batchParameterValues == null ? 0 : batchParameterValues.size(); k < batchCount; k++) {
                    if (k > 0) {
                        sb.append(COMMA_SPACE);
                    }

                    sb.append(_BRACE_L);

                    Object[] params = batchParameterValues.get(k);

                    for (int i = 0; i < params.length; i++) {
                        if (i > 0) {
                            sb.append(COMMA_SPACE);
                        }

                        sb.append(i + 1);
                        sb.append(_EQUAL);
                        sb.append(params[i]);
                    }

                    sb.append(_BRACE_R);
                }
            } else {
                for (int i = 0; i < parameterCount; i++) {
                    if (i > 0) {
                        sb.append(COMMA_SPACE);
                    }

                    sb.append(i + 1);
                    sb.append(_EQUAL);
                    sb.append(parameterValues[i]);
                }
            }

            sb.append(_BRACE_R);

            String st = sb.toString();

            Objectory.recycle(sb);

            return st;
        }
    }
}

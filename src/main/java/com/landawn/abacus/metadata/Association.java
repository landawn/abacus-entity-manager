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

package com.landawn.abacus.metadata;

import static com.landawn.abacus.condition.ConditionFactory.leftJoin;
import static com.landawn.abacus.condition.ConditionFactory.on;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.landawn.abacus.condition.Join;
import com.landawn.abacus.exception.AbacusException;
import com.landawn.abacus.util.WD;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * The Class Association.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class Association implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3451344350605572248L;

    /** The join on. */
    private final String joinOn;

    /** The prop. */
    private final Property prop;

    /** The src prop. */
    private final Property srcProp;

    /** The target prop. */
    private final Property targetProp;

    /** The bi entity props. */
    private final Property[] biEntityProps;

    /** The bi entity def. */
    private final EntityDefinition biEntityDef;

    /** The join type. */
    private final JoinType joinType;

    /** The joins. */
    private final List<Join> joins;

    /**
     * Instantiates a new association.
     *
     * @param prop
     * @param joinOn
     */
    public Association(Property prop, String joinOn) {
        this.joinOn = joinOn;
        this.prop = prop;
        this.joins = new ArrayList<>();

        String spliter = WD.SPACE + WD.AND + WD.SPACE;
        int index = joinOn.toUpperCase().indexOf(spliter);
        String[] lr = null;

        if (index > 0) {
            lr = N.asArray(joinOn.substring(0, index), joinOn.substring(index + spliter.length()));
        } else {
            lr = N.asArray(joinOn);
        }

        EntityDefinition entityDef = prop.getEntityDefinition();
        String[] left = lr[0].split(WD.EQUAL);
        Property left1 = checkProperty(entityDef, left[0].trim());
        Property left2 = checkProperty(entityDef, left[1].trim());

        if (1 == lr.length) {
            if (left1.getEntityDefinition().equals(entityDef)) {
                srcProp = left1;
                targetProp = left2;
            } else {
                srcProp = left2;
                targetProp = left1;
            }

            EntityDefinition targetEntityDef = targetProp.getEntityDefinition();

            if (targetProp.isId()) {
                throw new IllegalArgumentException("Unsupported joinOn: '" + joinOn + "'. '" + targetEntityDef.getName() + "' can joinOn '"
                        + entityDef.getName() + "'. but '" + entityDef.getName() + "' can't joinOn '" + targetEntityDef.getName() + "'.");
            }

            joins.add(leftJoin(targetProp.getEntityDefinition().getName(), on(lr[0])));
            biEntityProps = null;
            biEntityDef = null;
        } else {
            String[] right = lr[1].split(WD.EQUAL);
            Property right1 = checkProperty(entityDef, right[0].trim());
            Property right2 = checkProperty(entityDef, right[1].trim());
            biEntityProps = new Property[2];

            if (left1.getEntityDefinition().equals(entityDef)) {
                srcProp = left1;
                biEntityProps[0] = left2;

                if (right1.getEntityDefinition().equals(biEntityProps[0].getEntityDefinition())) {
                    biEntityProps[1] = right1;
                    targetProp = right2;
                } else {
                    biEntityProps[1] = right2;
                    targetProp = right1;
                }
            } else if (left2.getEntityDefinition().equals(entityDef)) {
                srcProp = left2;
                biEntityProps[0] = left1;

                if (right1.getEntityDefinition().equals(biEntityProps[0].getEntityDefinition())) {
                    biEntityProps[1] = right1;
                    targetProp = right2;
                } else {
                    biEntityProps[1] = right2;
                    targetProp = right1;
                }
            } else if (right1.getEntityDefinition().equals(entityDef)) {
                srcProp = right1;
                biEntityProps[0] = right2;

                if (left1.getEntityDefinition().equals(biEntityProps[0].getEntityDefinition())) {
                    biEntityProps[1] = left1;
                    targetProp = left2;
                } else {
                    biEntityProps[1] = left2;
                    targetProp = left1;
                }
            } else {
                srcProp = right2;
                biEntityProps[0] = right1;

                if (left1.getEntityDefinition().equals(biEntityProps[0].getEntityDefinition())) {
                    biEntityProps[1] = left1;
                    targetProp = left2;
                } else {
                    biEntityProps[1] = left2;
                    targetProp = left1;
                }
            }

            biEntityDef = biEntityProps[0].getEntityDefinition();

            if (targetProp.equals(right1) || targetProp.equals(right2)) {
                joins.add(leftJoin(biEntityDef.getName(), on(lr[0])));
                joins.add(leftJoin(targetProp.getEntityDefinition().getName(), on(lr[1])));
            } else {
                joins.add(leftJoin(biEntityDef.getName(), on(lr[1])));
                joins.add(leftJoin(targetProp.getEntityDefinition().getName(), on(lr[0])));
            }
        }

        joinType = (targetProp.isId() && (biEntityDef == null)) ? JoinType.OUTER : JoinType.INNER;

        if ((biEntityDef == null) && (srcProp.isId() && targetProp.isId())) {
            throw new AbacusException("Not supported assocation. The foreigin and reference properties can't both be id.");
        }
    }

    /**
     * Check property.
     *
     * @param ed
     * @param propName
     * @return
     */
    private Property checkProperty(EntityDefinition ed, String propName) {
        propName = propName.trim();

        Property prop = ed.getProperty(propName);

        if ((prop == null) && (propName.indexOf(".") < 0)) {
            propName = ed.getName() + "." + propName;
            prop = ed.getProperty(propName);
        }

        if (prop == null) {
            throw new IllegalArgumentException("Invalid joinOn property name[" + propName + "]. ");
        }

        return prop;
    }

    /**
     * Gets the property.
     *
     * @return
     */
    public Property getProperty() {
        return prop;
    }

    /**
     * Gets the src property.
     *
     * @return
     */
    public Property getSrcProperty() {
        return srcProp;
    }

    /**
     * Gets the target property.
     *
     * @return
     */
    public Property getTargetProperty() {
        return targetProp;
    }

    /**
     * Gets the bi entity properties.
     *
     * @return
     */
    public Property[] getBiEntityProperties() {
        return biEntityProps;
    }

    /**
     * Gets the bi entity def.
     *
     * @return
     */
    public EntityDefinition getBiEntityDef() {
        return biEntityDef;
    }

    /**
     * Gets the join type.
     *
     * @return
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * Gets the join condition.
     *
     * @return
     */
    public Collection<Join> getJoinCondition() {
        return joins;
    }

    /**
     * Equals.
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Association && N.equals(((Association) obj).joinOn, joinOn));
    }

    /**
     * Hash code.
     *
     * @return
     */
    @Override
    public int hashCode() {
        return joinOn.hashCode();
    }

    /**
     * To string.
     *
     * @return
     */
    @Override
    public String toString() {
        return joinOn;
    }

    /**
     * The Enum JoinType.
     */
    public enum JoinType {
        /**
         * Filed INNER.
         */
        INNER,
        /**
         * Field OUTER.
         */
        OUTER;
    }
}

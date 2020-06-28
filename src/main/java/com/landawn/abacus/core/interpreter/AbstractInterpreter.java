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

import com.landawn.abacus.util.WD;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public abstract class AbstractInterpreter implements Interpreter {
    
    /** The Constant _DISTINCT. */
    protected static final char[] _DISTINCT = WD.DISTINCT.toCharArray();

    /** The Constant _INSERT. */
    protected static final char[] _INSERT = WD.INSERT.toCharArray();
    
    /** The Constant _INTO. */
    protected static final char[] _INTO = WD.INTO.toCharArray();
    
    /** The Constant _VALUES. */
    protected static final char[] _VALUES = WD.VALUES.toCharArray();

    /** The Constant _SELECT. */
    protected static final char[] _SELECT = WD.SELECT.toCharArray();
    
    /** The Constant _FROM. */
    protected static final char[] _FROM = WD.FROM.toCharArray();

    /** The Constant _UPDATE. */
    protected static final char[] _UPDATE = WD.UPDATE.toCharArray();
    
    /** The Constant _SET. */
    protected static final char[] _SET = WD.SET.toCharArray();

    /** The Constant _DELETE. */
    protected static final char[] _DELETE = WD.DELETE.toCharArray();

    /** The Constant _JOIN. */
    protected static final char[] _JOIN = WD.JOIN.toCharArray();
    
    /** The Constant _LEFT_JOIN. */
    protected static final char[] _LEFT_JOIN = WD.LEFT_JOIN.toCharArray();
    
    /** The Constant _RIGHT_JOIN. */
    protected static final char[] _RIGHT_JOIN = WD.RIGHT_JOIN.toCharArray();
    
    /** The Constant _ON. */
    protected static final char[] _ON = WD.ON.toCharArray();

    /** The Constant _WHERE. */
    protected static final char[] _WHERE = WD.WHERE.toCharArray();
    
    /** The Constant _GROUP_BY. */
    protected static final char[] _GROUP_BY = WD.GROUP_BY.toCharArray();
    
    /** The Constant _HAVING. */
    protected static final char[] _HAVING = WD.HAVING.toCharArray();
    
    /** The Constant _ORDER_BY. */
    protected static final char[] _ORDER_BY = WD.ORDER_BY.toCharArray();
    
    /** The Constant _LIMIT. */
    protected static final char[] _LIMIT = WD.LIMIT.toCharArray();
    
    /** The Constant _OFFSET. */
    protected static final char[] _OFFSET = WD.OFFSET.toCharArray();

    /** The Constant _UNION. */
    protected static final char[] _UNION = WD.UNION.toCharArray();
    
    /** The Constant _UNION_ALL. */
    protected static final char[] _UNION_ALL = WD.UNION_ALL.toCharArray();
    
    /** The Constant _INTERSECT. */
    protected static final char[] _INTERSECT = WD.INTERSECT.toCharArray();
    
    /** The Constant _EXCEPT. */
    protected static final char[] _EXCEPT = WD.EXCEPT.toCharArray();
    
    /** The Constant _EXCEPT2. */
    protected static final char[] _EXCEPT2 = WD.EXCEPT2.toCharArray();

    /** The Constant _AND. */
    protected static final char[] _AND = WD.AND.toCharArray();
    
    /** The Constant _OR. */
    protected static final char[] _OR = WD.OR.toCharArray();

    /** The Constant _COMMA_SPACE. */
    protected static final char[] _COMMA_SPACE = WD.COMMA_SPACE.toCharArray();
    
    /** The Constant _SPACE_PARENTHESES_L. */
    protected static final char[] _SPACE_PARENTHESES_L = WD.SPACE_PARENTHESES_L.toCharArray();
}

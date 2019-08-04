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

package com.landawn.abacus.core.sql.interpreter;

import com.landawn.abacus.util.WD;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public abstract class AbstractInterpreter implements Interpreter {
    protected static final char[] _DISTINCT = WD.DISTINCT.toCharArray();

    protected static final char[] _INSERT = WD.INSERT.toCharArray();
    protected static final char[] _INTO = WD.INTO.toCharArray();
    protected static final char[] _VALUES = WD.VALUES.toCharArray();

    protected static final char[] _SELECT = WD.SELECT.toCharArray();
    protected static final char[] _FROM = WD.FROM.toCharArray();

    protected static final char[] _UPDATE = WD.UPDATE.toCharArray();
    protected static final char[] _SET = WD.SET.toCharArray();

    protected static final char[] _DELETE = WD.DELETE.toCharArray();

    protected static final char[] _JOIN = WD.JOIN.toCharArray();
    protected static final char[] _LEFT_JOIN = WD.LEFT_JOIN.toCharArray();
    protected static final char[] _RIGHT_JOIN = WD.RIGHT_JOIN.toCharArray();
    protected static final char[] _ON = WD.ON.toCharArray();

    protected static final char[] _WHERE = WD.WHERE.toCharArray();
    protected static final char[] _GROUP_BY = WD.GROUP_BY.toCharArray();
    protected static final char[] _HAVING = WD.HAVING.toCharArray();
    protected static final char[] _ORDER_BY = WD.ORDER_BY.toCharArray();
    protected static final char[] _LIMIT = WD.LIMIT.toCharArray();
    protected static final char[] _OFFSET = WD.OFFSET.toCharArray();

    protected static final char[] _UNION = WD.UNION.toCharArray();
    protected static final char[] _UNION_ALL = WD.UNION_ALL.toCharArray();
    protected static final char[] _INTERSECT = WD.INTERSECT.toCharArray();
    protected static final char[] _EXCEPT = WD.EXCEPT.toCharArray();
    protected static final char[] _EXCEPT2 = WD.EXCEPT2.toCharArray();

    protected static final char[] _AND = WD.AND.toCharArray();
    protected static final char[] _OR = WD.OR.toCharArray();

    protected static final char[] _COMMA_SPACE = WD.COMMA_SPACE.toCharArray();
    protected static final char[] _SPACE_PARENTHESES_L = WD.SPACE_PARENTHESES_L.toCharArray();
}

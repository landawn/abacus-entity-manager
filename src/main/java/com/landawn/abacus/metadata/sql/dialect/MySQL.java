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

package com.landawn.abacus.metadata.sql.dialect;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class MySQL implements Dialect {
    public static final String MYSQL = "MySQL";

    /**
     * Field PRI. (value is ""PRI"")
     */
    public static final String PRI = "PRI";

    /**
     * Field UNI. (value is ""UNI"")
     */
    public static final String UNI = "UNI";

    /**
     * Field YES. (value is ""YES"")
     */
    public static final String YES = "YES";

    /**
     * Field NO. (value is ""NO"")
     */
    public static final String NO = "NO";

    /**
     * Field NULL. (value is ""null"")
     */
    public static final String NULL = "null";

    /**
     * Field KEY. (value is ""Key"")
     */
    public static final String KEY = "Key";

    /**
     * Field DEFAULT. (value is ""Default"")
     */
    public static final String DEFAULT = "Default";

    /**
     * Field FIELD. (value is ""Field"")
     */
    public static final String FIELD = "Field";

    private MySQL() {
        // no instance
    }
}

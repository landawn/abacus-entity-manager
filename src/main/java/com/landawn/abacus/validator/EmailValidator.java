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

package com.landawn.abacus.validator;

import com.landawn.abacus.type.Type;

// TODO: Auto-generated Javadoc
/**
 * The Class EmailValidator.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class EmailValidator extends PatternValidator {

    /** The Constant EMAIL_PATTERN. */
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$";

    /**
     * Instantiates a new email validator.
     *
     * @param propName the prop name
     * @param type the type
     */
    protected EmailValidator(String propName, Type<String> type) {
        super(propName, type, EMAIL_PATTERN);
    }
}

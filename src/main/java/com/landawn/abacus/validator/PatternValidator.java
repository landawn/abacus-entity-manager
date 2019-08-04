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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.landawn.abacus.type.Type;

/**
 *
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class PatternValidator extends AbstractValidator<String> {
    private final Pattern pattern;

    public PatternValidator(String propName, Type<String> type, String pattern) {
        super(propName, type);
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean isValid(String propValue) {
        if (propValue == null) {
            return true;
        }

        Matcher m = pattern.matcher(propValue);

        return m.matches();
    }
}

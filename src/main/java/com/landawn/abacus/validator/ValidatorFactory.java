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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.type.Type;
import com.landawn.abacus.util.WD;
import com.landawn.abacus.util.ObjectPool;
import com.landawn.abacus.util.ClassUtil;
import com.landawn.abacus.util.TypeAttrParser;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Validator objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class ValidatorFactory {

    /** The Constant builtinValidatorClsNamePool. */
    @SuppressWarnings("rawtypes")
    private static final Map<String, Class<? extends Validator>> builtinValidatorClsNamePool = new ConcurrentHashMap<>(100);

    static {
        //        String pkgName = Validator.class.getPackage().getName();
        //        @SuppressWarnings({ "unchecked", "rawtypes" })
        //        List<Class<? extends Validator<?>>> classes = (List) PackageUtil.getClassesByPackage(pkgName, true, false);

        // for Android.
        @SuppressWarnings("rawtypes")
        final List<Class<? extends Validator>> classes = new ArrayList<>();
        {
            classes.add(com.landawn.abacus.validator.AbstractComparableValidator.class);
            classes.add(com.landawn.abacus.validator.AbstractDateValidator.class);
            classes.add(com.landawn.abacus.validator.AbstractValidator.class);
            classes.add(com.landawn.abacus.validator.AfterValidator.class);
            classes.add(com.landawn.abacus.validator.BeforeValidator.class);
            classes.add(com.landawn.abacus.validator.EmailValidator.class);
            classes.add(com.landawn.abacus.validator.EqualValidator.class);
            classes.add(com.landawn.abacus.validator.GreaterEqualValidator.class);
            classes.add(com.landawn.abacus.validator.GreaterThanValidator.class);
            classes.add(com.landawn.abacus.validator.InValidator.class);
            classes.add(com.landawn.abacus.validator.LessEqualValidator.class);
            classes.add(com.landawn.abacus.validator.LessThanValidator.class);
            classes.add(com.landawn.abacus.validator.NameValidator.class);
            classes.add(com.landawn.abacus.validator.NotEqualValidator.class);
            classes.add(com.landawn.abacus.validator.NotNullValidator.class);
            classes.add(com.landawn.abacus.validator.NotValidator.class);
            classes.add(com.landawn.abacus.validator.NullValidator.class);
            classes.add(com.landawn.abacus.validator.PatternValidator.class);
            classes.add(com.landawn.abacus.validator.RangeValidator.class);
            classes.add(com.landawn.abacus.validator.SizeValidator.class);
            classes.add(com.landawn.abacus.validator.Validator.class);
        }

        for (@SuppressWarnings("rawtypes")
        Class<? extends Validator> cls : classes) {
            int mod = cls.getModifiers();

            if (Validator.class.isAssignableFrom(cls) && Modifier.isPublic(mod) && !Modifier.isAbstract(mod) && !Modifier.isInterface(mod)) {
                builtinValidatorClsNamePool.put(ClassUtil.getSimpleClassName(cls), cls);
                builtinValidatorClsNamePool.put(getBuiltinValidatorName(cls), cls);
            }
        }
    }

    /** The Constant validatorPool. */
    private static final Map<String, Validator<?>> validatorPool = new ObjectPool<String, Validator<?>>(1024);

    /**
     * Instantiates a new validator factory.
     */
    private ValidatorFactory() {
        // singleton.
    }

    /**
     * Method create.
     *
     * @param <T>
     * @param propName
     * @param type
     * @param validatorAttr
     * @return Validator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Validator<T> create(String propName, Type<?> type, String validatorAttr) {
        synchronized (validatorPool) {
            String key = createValidatorKey(propName, validatorAttr);
            Validator validator = validatorPool.get(key);

            if (validator == null) {
                boolean isNot = false;

                if (validatorAttr.trim().startsWith("!")) {
                    isNot = true;
                    validatorAttr = validatorAttr.trim().substring(1);
                }

                TypeAttrParser attrResult = TypeAttrParser.parse(validatorAttr);
                String clsName = attrResult.getClassName();
                Class<? extends Validator> cls = builtinValidatorClsNamePool.get(clsName);

                if (cls == null) {
                    cls = ClassUtil.forClass(clsName);
                }

                validator = (Validator) TypeAttrParser.newInstance(cls, validatorAttr, String.class, propName, Type.class, type);

                if (isNot) {
                    validator = new NotValidator(validator);
                }

                validatorPool.put(key, validator);
            }

            return validator;
        }
    }

    /**
     * Register validator.
     *
     * @param validator
     * @param clazz
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void registerValidator(String validator, Class<? extends Validator> clazz) {
        builtinValidatorClsNamePool.put(validator, clazz);
    }

    /**
     * Method getBuiltinValidatorName.
     *
     * @param clazz
     * @return String
     */
    private static String getBuiltinValidatorName(Class<?> clazz) {
        return clazz.getSimpleName().replaceAll("Validator", "");
    }

    /**
     * Method createValidatorKey.
     *
     * @param propName
     * @param validatorAttr
     * @return String
     */
    private static String createValidatorKey(String propName, String validatorAttr) {
        return propName + WD._VERTICALBAR + validatorAttr;
    }
}

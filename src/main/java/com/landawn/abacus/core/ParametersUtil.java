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

package com.landawn.abacus.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.landawn.abacus.annotation.Internal;
import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 * please don't use it in your application.
 *
 * @author Haiyang Li
 * @since 0.8
 */
@Internal
final class ParametersUtil {

    /**
     *
     * @param <T>
     * @param a
     * @return
     */
    @SafeVarargs
    public static <T> List<T> asList(T... a) {
        List<T> list = (a.length == 0) ? new ArrayList<T>() : new ArrayList<T>(a.length);

        for (int i = 0; i < a.length; i++) {
            list.add(a[i]);
        }

        return list;
    }

    /**
     *
     * @param <T>
     * @param a
     * @return
     */
    @SafeVarargs
    public static <T> Set<T> asSet(T... a) {
        Set<T> set = (a.length == 0) ? N.newHashSet() : N.newHashSet(a.length + 3);

        for (int i = 0; i < a.length; i++) {
            set.add(a[i]);
        }

        return set;
    }

    /**
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param a
     * @return
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <K, V> Map<K, V> asMap(Object... a) {
        if (0 != (a.length % 2)) {
            throw new IllegalArgumentException("The property name and value are not paired. ");
        }

        Map<K, V> map = (a.length == 0) ? new HashMap<K, V>() : new HashMap<K, V>(a.length / 2 + 3);

        for (int i = 0; i < a.length; i++) {
            map.put((K) a[i], (V) a[++i]);
        }

        return map;
    }

    /**
     *
     * @param propNameValues
     * @return
     */
    @SafeVarargs
    public static Map<String, Object> asProps(Object... propNameValues) {
        return asMap(propNameValues);
    }

    /**
     *
     * @param a
     * @return
     */
    @SafeVarargs
    public static Map<String, Object> asOptions(Object... a) {
        return asMap(a);
    }

    /**
     *
     * @param <T>
     * @param c
     * @return
     */
    public static <T> Collection<T> copy(Collection<T> c) {
        if (c instanceof List) {
            return copy((List<T>) c);
        } else if (c instanceof Set) {
            return copy((Set<T>) c);
        } else {
            @SuppressWarnings("unchecked")
            Collection<T> copy = N.newInstance(c.getClass());
            copy.addAll(c);

            return copy;
        }
    }

    /**
     *
     * @param <T>
     * @param c
     * @return
     */
    public static <T> List<T> copy(List<T> c) {
        if (c == null) {
            return new ArrayList<T>();
        } else {
            return (c instanceof Local) ? c : new ArrayList<T>(c);
        }
    }

    /**
     *
     * @param <T>
     * @param c
     * @return
     */
    public static <T> Set<T> copy(Set<T> c) {
        if (c == null) {
            return N.newLinkedHashSet();
        } else {
            return (c instanceof Local) ? c : N.newLinkedHashSet(c);
        }
    }

    /**
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param m
     * @return
     */
    public static <K, V> Map<K, V> copy(Map<K, V> m) {
        if (m == null) {
            return new LinkedHashMap<K, V>();
        } else {
            return (m instanceof Local) ? m : new LinkedHashMap<K, V>(m);
        }
    }

    /**
     * The Interface Local.
     */
    static interface Local {
    }

    /**
     * The Class HashMap.
     *
     * @param <K> the key type
     * @param <V> the value type
     */
    static final class HashMap<K, V> extends java.util.HashMap<K, V> implements Local {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -8221642996729977229L;

        /**
         * Instantiates a new hash map.
         */
        public HashMap() {
            super();
        }

        /**
         * Instantiates a new hash map.
         *
         * @param initialCapacity
         */
        public HashMap(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new hash map.
         *
         * @param initialCapacity
         * @param loadFactor
         */
        public HashMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new hash map.
         *
         * @param m
         */
        public HashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }

    /**
     * The Class LinkedHashMap.
     *
     * @param <K> the key type
     * @param <V> the value type
     */
    static final class LinkedHashMap<K, V> extends java.util.HashMap<K, V> implements Local {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 3744016416795543777L;

        /**
         * Instantiates a new linked hash map.
         */
        public LinkedHashMap() {
            super();
        }

        /**
         * Instantiates a new linked hash map.
         *
         * @param initialCapacity
         */
        public LinkedHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new linked hash map.
         *
         * @param initialCapacity
         * @param loadFactor
         */
        public LinkedHashMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new linked hash map.
         *
         * @param m
         */
        public LinkedHashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }

    /**
     * The Class HashSet.
     *
     * @param <E>
     */
    static final class HashSet<E> extends java.util.HashSet<E> implements Local {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 9081027352330623445L;

        /**
         * Instantiates a new hash set.
         */
        public HashSet() {
            super();
        }

        /**
         * Instantiates a new hash set.
         *
         * @param initialCapacity
         */
        public HashSet(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new hash set.
         *
         * @param initialCapacity
         * @param loadFactor
         */
        public HashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new hash set.
         *
         * @param c
         */
        public HashSet(Collection<? extends E> c) {
            super(c);
        }
    }

    /**
     * The Class LinkedHashSet.
     *
     * @param <E>
     */
    static final class LinkedHashSet<E> extends java.util.LinkedHashSet<E> implements Local {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -8427957578044492143L;

        /**
         * Instantiates a new linked hash set.
         */
        public LinkedHashSet() {
            super();
        }

        /**
         * Instantiates a new linked hash set.
         *
         * @param initialCapacity
         */
        public LinkedHashSet(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new linked hash set.
         *
         * @param initialCapacity
         * @param loadFactor
         */
        public LinkedHashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new linked hash set.
         *
         * @param c
         */
        public LinkedHashSet(Collection<? extends E> c) {
            super(c);
        }
    }

    /**
     * The Class ArrayList.
     *
     * @param <E>
     */
    static final class ArrayList<E> extends java.util.ArrayList<E> implements Local {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -6177282621875675144L;

        /**
         * Instantiates a new array list.
         */
        public ArrayList() {
            super();
        }

        /**
         * Instantiates a new array list.
         *
         * @param initialCapacity
         */
        public ArrayList(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new array list.
         *
         * @param c
         */
        public ArrayList(Collection<? extends E> c) {
            super(c);
        }
    }

}

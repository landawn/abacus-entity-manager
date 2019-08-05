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
     * As list.
     *
     * @param <T> the generic type
     * @param a the a
     * @return the list
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
     * As set.
     *
     * @param <T> the generic type
     * @param a the a
     * @return the sets the
     */
    @SafeVarargs
    public static <T> Set<T> asSet(T... a) {
        Set<T> set = (a.length == 0) ? new HashSet<T>() : new HashSet<T>(a.length + 3);

        for (int i = 0; i < a.length; i++) {
            set.add(a[i]);
        }

        return set;
    }

    /**
     * As map.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param a the a
     * @return the map
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
     * As props.
     *
     * @param propNameValues the prop name values
     * @return the map
     */
    @SafeVarargs
    public static Map<String, Object> asProps(Object... propNameValues) {
        return asMap(propNameValues);
    }

    /**
     * As options.
     *
     * @param a the a
     * @return the map
     */
    @SafeVarargs
    public static Map<String, Object> asOptions(Object... a) {
        return asMap(a);
    }

    /**
     * Copy.
     *
     * @param <T> the generic type
     * @param c the c
     * @return the collection
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
     * Copy.
     *
     * @param <T> the generic type
     * @param c the c
     * @return the list
     */
    public static <T> List<T> copy(List<T> c) {
        if (c == null) {
            return new ArrayList<T>();
        } else {
            return (c instanceof Local) ? c : new ArrayList<T>(c);
        }
    }

    /**
     * Copy.
     *
     * @param <T> the generic type
     * @param c the c
     * @return the sets the
     */
    public static <T> Set<T> copy(Set<T> c) {
        if (c == null) {
            return new LinkedHashSet<T>();
        } else {
            return (c instanceof Local) ? c : new LinkedHashSet<T>(c);
        }
    }

    /**
     * Copy.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param m the m
     * @return the map
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
         * @param initialCapacity the initial capacity
         */
        public HashMap(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new hash map.
         *
         * @param initialCapacity the initial capacity
         * @param loadFactor the load factor
         */
        public HashMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new hash map.
         *
         * @param m the m
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
         * @param initialCapacity the initial capacity
         */
        public LinkedHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new linked hash map.
         *
         * @param initialCapacity the initial capacity
         * @param loadFactor the load factor
         */
        public LinkedHashMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new linked hash map.
         *
         * @param m the m
         */
        public LinkedHashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }

    /**
     * The Class HashSet.
     *
     * @param <E> the element type
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
         * @param initialCapacity the initial capacity
         */
        public HashSet(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new hash set.
         *
         * @param initialCapacity the initial capacity
         * @param loadFactor the load factor
         */
        public HashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new hash set.
         *
         * @param c the c
         */
        public HashSet(Collection<? extends E> c) {
            super(c);
        }
    }

    /**
     * The Class LinkedHashSet.
     *
     * @param <E> the element type
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
         * @param initialCapacity the initial capacity
         */
        public LinkedHashSet(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new linked hash set.
         *
         * @param initialCapacity the initial capacity
         * @param loadFactor the load factor
         */
        public LinkedHashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        /**
         * Instantiates a new linked hash set.
         *
         * @param c the c
         */
        public LinkedHashSet(Collection<? extends E> c) {
            super(c);
        }
    }

    /**
     * The Class ArrayList.
     *
     * @param <E> the element type
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
         * @param initialCapacity the initial capacity
         */
        public ArrayList(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Instantiates a new array list.
         *
         * @param c the c
         */
        public ArrayList(Collection<? extends E> c) {
            super(c);
        }
    }

}

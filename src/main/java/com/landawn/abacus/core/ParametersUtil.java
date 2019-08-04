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

/**
 * please don't use it in your application.
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
@Internal
final class ParametersUtil {
    @SafeVarargs
    public static <T> List<T> asList(T... a) {
        List<T> list = (a.length == 0) ? new ArrayList<T>() : new ArrayList<T>(a.length);

        for (int i = 0; i < a.length; i++) {
            list.add(a[i]);
        }

        return list;
    }

    @SafeVarargs
    public static <T> Set<T> asSet(T... a) {
        Set<T> set = (a.length == 0) ? new HashSet<T>() : new HashSet<T>(a.length + 3);

        for (int i = 0; i < a.length; i++) {
            set.add(a[i]);
        }

        return set;
    }

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

    @SafeVarargs
    public static Map<String, Object> asProps(Object... propNameValues) {
        return asMap(propNameValues);
    }

    @SafeVarargs
    public static Map<String, Object> asOptions(Object... a) {
        return asMap(a);
    }

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

    public static <T> List<T> copy(List<T> c) {
        if (c == null) {
            return new ArrayList<T>();
        } else {
            return (c instanceof Local) ? c : new ArrayList<T>(c);
        }
    }

    public static <T> Set<T> copy(Set<T> c) {
        if (c == null) {
            return new LinkedHashSet<T>();
        } else {
            return (c instanceof Local) ? c : new LinkedHashSet<T>(c);
        }
    }

    public static <K, V> Map<K, V> copy(Map<K, V> m) {
        if (m == null) {
            return new LinkedHashMap<K, V>();
        } else {
            return (m instanceof Local) ? m : new LinkedHashMap<K, V>(m);
        }
    }

    static interface Local {
    }

    static final class HashMap<K, V> extends java.util.HashMap<K, V> implements Local {
        private static final long serialVersionUID = -8221642996729977229L;

        public HashMap() {
            super();
        }

        public HashMap(int initialCapacity) {
            super(initialCapacity);
        }

        public HashMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public HashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }

    static final class LinkedHashMap<K, V> extends java.util.HashMap<K, V> implements Local {
        private static final long serialVersionUID = 3744016416795543777L;

        public LinkedHashMap() {
            super();
        }

        public LinkedHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        public LinkedHashMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public LinkedHashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }

    static final class HashSet<E> extends java.util.HashSet<E> implements Local {
        private static final long serialVersionUID = 9081027352330623445L;

        public HashSet() {
            super();
        }

        public HashSet(int initialCapacity) {
            super(initialCapacity);
        }

        public HashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public HashSet(Collection<? extends E> c) {
            super(c);
        }
    }

    static final class LinkedHashSet<E> extends java.util.LinkedHashSet<E> implements Local {
        private static final long serialVersionUID = -8427957578044492143L;

        public LinkedHashSet() {
            super();
        }

        public LinkedHashSet(int initialCapacity) {
            super(initialCapacity);
        }

        public LinkedHashSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public LinkedHashSet(Collection<? extends E> c) {
            super(c);
        }
    }

    static final class ArrayList<E> extends java.util.ArrayList<E> implements Local {
        private static final long serialVersionUID = -6177282621875675144L;

        public ArrayList() {
            super();
        }

        public ArrayList(int initialCapacity) {
            super(initialCapacity);
        }

        public ArrayList(Collection<? extends E> c) {
            super(c);
        }
    }

}

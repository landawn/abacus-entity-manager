/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.landawn.abacus.util.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Haiyang Li
 * 
 * @since 0.8
 */
public final class _N {

    private _N() {
        //singleton
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final int MAX_HASH_LENGTH = (int) (MAX_ARRAY_SIZE / 1.25) - 1;

    private static final String NULL_STRING = "null";

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final boolean a, final boolean b) {
        return a == b;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final char a, final char b) {
        return a == b;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final byte a, final byte b) {
        return a == b;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final short a, final short b) {
        return a == b;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final int a, final int b) {
        return a == b;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final long a, final long b) {
        return a == b;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final float a, final float b) {
        return Float.compare(a, b) == 0;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final double a, final double b) {
        return Double.compare(a, b) == 0;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final String a, final String b) {
        return (a == null) ? b == null : (b == null ? false : a.length() == b.length() && a.equals(b));
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final Object a, final Object b) {
        return (a == null) ? b == null : (b == null ? false : a.equals(b));
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final boolean[] a, final boolean[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final boolean[] a, final int fromIndex, final int toIndex, final boolean[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final char[] a, final char[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final char[] a, final int fromIndex, final int toIndex, final char[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final byte[] a, final byte[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final byte[] a, final int fromIndex, final int toIndex, final byte[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final short[] a, final short[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final short[] a, final int fromIndex, final int toIndex, final short[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final int[] a, final int[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final int[] a, final int fromIndex, final int toIndex, final int[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final long[] a, final long[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final long[] a, final int fromIndex, final int toIndex, final long[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final float[] a, final float[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final float[] a, final int fromIndex, final int toIndex, final float[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (Float.compare(a[i], b[i]) != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final double[] a, final double[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final double[] a, final int fromIndex, final int toIndex, final double[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (Double.compare(a[i], b[i]) != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param a
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final Object[] a, final Object[] b) {
        return (a == null || b == null) ? a == b : (a.length == b.length && equals(a, 0, a.length, b));
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param b
     * @return true, if successful
     */
    public static boolean equals(final Object[] a, final int fromIndex, final int toIndex, final Object[] b) {
        if (a == b) {
            return true;
        }

        if ((a == null && b != null) || (a != null && b == null) || a.length < toIndex || b.length < toIndex) {
            return false;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (!(a[i] == null ? b[i] == null : a[i].equals(b[i]))) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final boolean value) {
        return value ? 1231 : 1237;
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final char value) {
        return value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final byte value) {
        return value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final short value) {
        return value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final int value) {
        return value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final long value) {
        return (int) (value ^ (value >>> 32));
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final float value) {
        return Float.floatToIntBits(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static int hashCode(final double value) {
        long bits = Double.doubleToLongBits(value);

        return (int) (bits ^ (bits >>> 32));
    }

    /**
     *
     * @param obj
     * @return
     */
    public static int hashCode(final Object obj) {
        if (obj == null) {
            return 0;
        }

        return obj.hashCode();
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final boolean[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final boolean[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + (a[i] ? 1231 : 1237);
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final char[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final char[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + a[i];
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final byte[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final byte[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + a[i];
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final short[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final short[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + a[i];
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final int[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final int[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + a[i];
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final long[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final long[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + (int) (a[i] ^ (a[i] >>> 32));
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final float[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final float[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Float.floatToIntBits(a[i]);
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final double[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final double[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            long bits = Double.doubleToLongBits(a[i]);
            result = 31 * result + (int) (bits ^ (bits >>> 32));
        }

        return result;
    }

    /**
     *
     * @param a
     * @return
     */
    public static int hashCode(final Object[] a) {
        return a == null ? 0 : hashCode(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int hashCode(final Object[] a, final int fromIndex, final int toIndex) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + (a[i] == null ? 0 : a[i].hashCode());
        }

        return result;
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final boolean value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final char value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final byte value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final short value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final int value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final long value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final float value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String toString(final double value) {
        return String.valueOf(value);
    }

    /**
     *
     * @param obj
     * @return
     */
    public static String toString(final Object obj) {
        if (obj == null) {
            return NULL_STRING;
        }

        return obj.toString();
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final boolean[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final boolean[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final boolean[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final boolean[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final char[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final char[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final char[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final char[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final byte[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final byte[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final byte[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final byte[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final short[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final short[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final short[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final short[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final int[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final int[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final int[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final int[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final long[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final long[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final long[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final long[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final float[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final float[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final float[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final float[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final double[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final double[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final double[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final double[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(a[i]);
        }

        sb.append(']');
    }

    /**
     *
     * @param a
     * @return
     */
    public static String toString(final Object[] a) {
        if (a == null) {
            return NULL_STRING;
        }

        if (a.length == 0) {
            return "[]";
        }

        return toString(a, 0, a.length);
    }

    /**
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String toString(final Object[] a, final int fromIndex, final int toIndex) {
        final StringBuilder sb = new StringBuilder();

        toString(sb, a, fromIndex, toIndex);

        return sb.toString();
    }

    /**
     *
     * @param sb
     * @param a
     */
    static void toString(final StringBuilder sb, final Object[] a) {
        if (a == null) {
            sb.append(NULL_STRING);
        } else if (a.length == 0) {
            sb.append("[]");
        } else {
            toString(sb, a, 0, a.length);
        }
    }

    /**
     *
     * @param sb
     * @param a
     * @param fromIndex
     * @param toIndex
     */
    static void toString(final StringBuilder sb, final Object[] a, final int fromIndex, final int toIndex) {
        sb.append('[');

        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) {
                sb.append(", ");
            }

            sb.append(toString(a[i]));
        }

        sb.append(']');
    }

    /**
     *
     * @param <T>
     * @param a
     * @return
     */
    @SafeVarargs
    public static <T> List<T> asList(final T... a) {
        if (a == null) {
            return new ArrayList<>();
        }

        final List<T> list = new ArrayList<T>(a.length);

        for (T e : a) {
            list.add(e);
        }

        return list;
    }

    /**
     * As linked list.
     *
     * @param <T>
     * @param a
     * @return
     */
    @SafeVarargs
    public static <T> LinkedList<T> asLinkedList(final T... a) {
        if (a == null) {
            return new LinkedList<>();
        }

        LinkedList<T> list = new LinkedList<T>();

        for (T e : a) {
            list.add(e);
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
    public static <T> Set<T> asSet(final T... a) {
        if (a == null) {
            return new HashSet<>();
        }

        final Set<T> set = new HashSet<T>(initHashCapacity(a.length));

        for (T e : a) {
            set.add(e);
        }

        return set;
    }

    /**
     * As linked hash set.
     *
     * @param <T>
     * @param a
     * @return
     */
    @SafeVarargs
    public static <T> LinkedHashSet<T> asLinkedHashSet(final T... a) {
        if (a == null) {
            return new LinkedHashSet<>();
        }

        final LinkedHashSet<T> set = new LinkedHashSet<T>(initHashCapacity(a.length));

        for (T e : a) {
            set.add(e);
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
    public static <K, V> Map<K, V> asMap(final Object... a) {
        if (a == null) {
            return new HashMap<>();
        }

        final Map<K, V> m = new HashMap<K, V>(initHashCapacity(a.length / 2));

        for (int i = 0; i < a.length; i++) {
            m.put((K) a[i], (V) a[++i]);
        }

        return m;
    }

    /**
     * As linked hash map.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param a
     * @return
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <K, V> LinkedHashMap<K, V> asLinkedHashMap(final Object... a) {
        if (a == null) {
            return new LinkedHashMap<>();
        }

        final LinkedHashMap<K, V> m = new LinkedHashMap<K, V>(initHashCapacity(a.length / 2));

        for (int i = 0; i < a.length; i++) {
            m.put((K) a[i], (V) a[++i]);
        }

        return m;
    }

    /**
     * Inits the hash capacity.
     *
     * @param size
     * @return
     */
    private static int initHashCapacity(final int size) {
        return size < MAX_HASH_LENGTH ? (int) (size * 1.25) + 1 : MAX_ARRAY_SIZE;
    }
}

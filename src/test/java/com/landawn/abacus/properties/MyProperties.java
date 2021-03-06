
package com.landawn.abacus.properties;

import java.util.Map;

import com.landawn.abacus.util.Properties;

/**
 * Auto-generated by Abacus.
 */
public class MyProperties extends Properties<String, Object> {
    private String strProp;
    private String strProp2;
    private String intProp0;
    private int intProp2;
    private MProps3 mProps3;
    private MProps4 mProps4;
    private int intProp1;
    private Properties<String, Object> mProps2;
    private Properties<String, Object> mProps1;

    public String getStrProp() {
        return strProp;
    }

    public void setStrProp(String strProp) {
        super.put("strProp", strProp);
        this.strProp = strProp;
    }

    public void removeStrProp() {
        super.remove("strProp");
        this.strProp = null;
    }

    public String getStrProp2() {
        return strProp2;
    }

    public void setStrProp2(String strProp2) {
        super.put("strProp2", strProp2);
        this.strProp2 = strProp2;
    }

    public void removeStrProp2() {
        super.remove("strProp2");
        this.strProp2 = null;
    }

    public String getIntProp0() {
        return intProp0;
    }

    public void setIntProp0(String intProp0) {
        super.put("intProp0", intProp0);
        this.intProp0 = intProp0;
    }

    public void removeIntProp0() {
        super.remove("intProp0");
        this.intProp0 = null;
    }

    public int getIntProp2() {
        return intProp2;
    }

    public void setIntProp2(int intProp2) {
        super.put("intProp2", intProp2);
        this.intProp2 = intProp2;
    }

    public void removeIntProp2() {
        super.remove("intProp2");
        this.intProp2 = 0;
    }

    public MProps3 getMProps3() {
        return mProps3;
    }

    public void setMProps3(MProps3 mProps3) {
        super.put("mProps3", mProps3);
        this.mProps3 = mProps3;
    }

    public void removeMProps3() {
        super.remove("mProps3");
        this.mProps3 = null;
    }

    public MProps4 getMProps4() {
        return mProps4;
    }

    public void setMProps4(MProps4 mProps4) {
        super.put("mProps4", mProps4);
        this.mProps4 = mProps4;
    }

    public void removeMProps4() {
        super.remove("mProps4");
        this.mProps4 = null;
    }

    public int getIntProp1() {
        return intProp1;
    }

    public void setIntProp1(int intProp1) {
        super.put("intProp1", intProp1);
        this.intProp1 = intProp1;
    }

    public void removeIntProp1() {
        super.remove("intProp1");
        this.intProp1 = 0;
    }

    public Properties<String, Object> getMProps2() {
        return mProps2;
    }

    public void setMProps2(Properties<String, Object> mProps2) {
        super.put("mProps2", mProps2);
        this.mProps2 = mProps2;
    }

    public void removeMProps2() {
        super.remove("mProps2");
        this.mProps2 = null;
    }

    public Properties<String, Object> getMProps1() {
        return mProps1;
    }

    public void setMProps1(Properties<String, Object> mProps1) {
        super.put("mProps1", mProps1);
        this.mProps1 = mProps1;
    }

    public void removeMProps1() {
        super.remove("mProps1");
        this.mProps1 = null;
    }

    @Deprecated
    @Override
    public MyProperties set(String propName, Object propValue) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Object put(String propName, Object propValue) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Object remove(Object propName) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public static class MProps3 extends Properties<String, Object> {
        private String strProp;
        private String strProp2;
        private String intProp0;
        private int intProp2;
        private int intProp1;

        public String getStrProp() {
            return strProp;
        }

        public void setStrProp(String strProp) {
            super.put("strProp", strProp);
            this.strProp = strProp;
        }

        public void removeStrProp() {
            super.remove("strProp");
            this.strProp = null;
        }

        public String getStrProp2() {
            return strProp2;
        }

        public void setStrProp2(String strProp2) {
            super.put("strProp2", strProp2);
            this.strProp2 = strProp2;
        }

        public void removeStrProp2() {
            super.remove("strProp2");
            this.strProp2 = null;
        }

        public String getIntProp0() {
            return intProp0;
        }

        public void setIntProp0(String intProp0) {
            super.put("intProp0", intProp0);
            this.intProp0 = intProp0;
        }

        public void removeIntProp0() {
            super.remove("intProp0");
            this.intProp0 = null;
        }

        public int getIntProp2() {
            return intProp2;
        }

        public void setIntProp2(int intProp2) {
            super.put("intProp2", intProp2);
            this.intProp2 = intProp2;
        }

        public void removeIntProp2() {
            super.remove("intProp2");
            this.intProp2 = 0;
        }

        public int getIntProp1() {
            return intProp1;
        }

        public void setIntProp1(int intProp1) {
            super.put("intProp1", intProp1);
            this.intProp1 = intProp1;
        }

        public void removeIntProp1() {
            super.remove("intProp1");
            this.intProp1 = 0;
        }

        @Deprecated
        @Override
        public MProps3 set(String propName, Object propValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Object put(String propName, Object propValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Object remove(Object propName) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    public static class MProps4 extends Properties<String, Object> {
        private String strProp;
        private String strProp2;
        private String intProp0;
        private int intProp2;
        private int intProp1;

        public String getStrProp() {
            return strProp;
        }

        public void setStrProp(String strProp) {
            super.put("strProp", strProp);
            this.strProp = strProp;
        }

        public void removeStrProp() {
            super.remove("strProp");
            this.strProp = null;
        }

        public String getStrProp2() {
            return strProp2;
        }

        public void setStrProp2(String strProp2) {
            super.put("strProp2", strProp2);
            this.strProp2 = strProp2;
        }

        public void removeStrProp2() {
            super.remove("strProp2");
            this.strProp2 = null;
        }

        public String getIntProp0() {
            return intProp0;
        }

        public void setIntProp0(String intProp0) {
            super.put("intProp0", intProp0);
            this.intProp0 = intProp0;
        }

        public void removeIntProp0() {
            super.remove("intProp0");
            this.intProp0 = null;
        }

        public int getIntProp2() {
            return intProp2;
        }

        public void setIntProp2(int intProp2) {
            super.put("intProp2", intProp2);
            this.intProp2 = intProp2;
        }

        public void removeIntProp2() {
            super.remove("intProp2");
            this.intProp2 = 0;
        }

        public int getIntProp1() {
            return intProp1;
        }

        public void setIntProp1(int intProp1) {
            super.put("intProp1", intProp1);
            this.intProp1 = intProp1;
        }

        public void removeIntProp1() {
            super.remove("intProp1");
            this.intProp1 = 0;
        }

        @Deprecated
        @Override
        public MProps4 set(String propName, Object propValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Object put(String propName, Object propValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Object remove(Object propName) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}

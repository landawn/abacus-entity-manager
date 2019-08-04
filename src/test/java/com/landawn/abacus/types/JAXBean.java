/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.types;

import java.util.ArrayList;
import java.util.List;

import com.landawn.abacus.core.AbstractDirtyMarker;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class JAXBean extends AbstractDirtyMarker {
    public JAXBean() {
        super(JAXBean.class.getSimpleName());
    }

    private static final long serialVersionUID = 2750135823774900597L;
    private List<String> cityList;

    public List<String> getCityList() {
        if (cityList == null) {
            cityList = new ArrayList<>();
            this.setUpdatedPropName("cityList");
        }

        return cityList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((cityList == null) ? 0 : cityList.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        JAXBean other = (JAXBean) obj;

        if (cityList == null) {
            if (other.cityList != null) {
                return false;
            }
        } else if (!cityList.equals(other.cityList)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "{cityList=" + cityList + "}";
    }
}

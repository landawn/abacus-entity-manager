/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.core;

import org.junit.Test;

import com.landawn.abacus.AbstractTest;
import com.landawn.abacus.cache.DataGrid;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class DataGridTest extends AbstractTest {
    @Test
    public void testDataGrid1() {
        DataGrid<String> dataGrid = new DataGrid<String>(99, 87);
        for (int i = 0; i < dataGrid.getX(); i++) {
            for (int j = 0; j < dataGrid.getY(); j++) {
                dataGrid.put(i, j, String.valueOf(i + j));
            }
        }

        N.println(dataGrid);

        DataGrid<String> dataGrid2 = new DataGrid<String>(99, 87);
        for (int i = 0; i < dataGrid2.getX(); i++) {
            for (int j = 0; j < dataGrid2.getY(); j++) {
                dataGrid2.put(i, j, String.valueOf(i + j));
            }
        }

        dataGrid2.zip();

        assertEquals(dataGrid, dataGrid2);
    }
}

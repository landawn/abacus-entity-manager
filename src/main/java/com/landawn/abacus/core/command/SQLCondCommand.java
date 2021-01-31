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

package com.landawn.abacus.core.command;

import java.util.Collection;
import java.util.Set;

import com.landawn.abacus.util.N;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLCondCommand extends SQLCommand {

    private Set<String> joinTables = N.newHashSet();

    private int whereBeginIndex = -1;

    private int whereEndIdnex = -1;

    /**
     * Gets the where begin index.
     *
     * @return
     */
    public int getWhereBeginIndex() {
        return whereBeginIndex;
    }

    /**
     * Sets the where begin index.
     *
     * @param beginIndex the new where begin index
     */
    public void setWhereBeginIndex(int beginIndex) {
        whereBeginIndex = beginIndex;
    }

    /**
     * Gets the where end index.
     *
     * @return
     */
    public int getWhereEndIndex() {
        return whereEndIdnex;
    }

    /**
     * Sets the where end index.
     *
     * @param endIndex the new where end index
     */
    public void setWhereEndIndex(int endIndex) {
        whereEndIdnex = endIndex;
    }

    /**
     * Gets the join tables.
     *
     * @return
     */
    public Set<String> getJoinTables() {
        return joinTables;
    }

    /**
     * Adds the join table.
     *
     * @param tableName
     */
    public void addJoinTable(String tableName) {
        joinTables.add(tableName);
    }

    /**
     * Adds the join tables.
     *
     * @param tableNames
     */
    public void addJoinTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            joinTables.addAll(tableNames);
        }
    }

    /**
     * Removes the join table.
     *
     * @param tableName
     */
    public void removeJoinTable(String tableName) {
        joinTables.remove(tableName);
    }

    /**
     * Removes the join tables.
     *
     * @param tableNames
     */
    public void removeJoinTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            N.removeAll(joinTables, tableNames);
        }
    }

    /**
     * Clear join table.
     */
    public void clearJoinTable() {
        joinTables.clear();
    }

    /**
     *
     * @param sqlCondCmd
     */
    @Override
    public void combine(SQLCommand sqlCondCmd) {
        super.combine(sqlCondCmd);

        if (sqlCondCmd instanceof SQLCondCommand) {
            addJoinTables(((SQLCondCommand) sqlCondCmd).joinTables);
        }
    }

    @Override
    public Object clone() {
        SQLCondCommand copy = (SQLCondCommand) super.clone();

        copy.joinTables = N.newHashSet(joinTables);

        return copy;
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        super.clear();

        joinTables.clear();

        whereBeginIndex = -1;
        whereEndIdnex = -1;
    }
}

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

package com.landawn.abacus.core.sql.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.landawn.abacus.util.Iterables;
import com.landawn.abacus.util.N;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLCondCommand extends SQLCommand {
    private Set<String> joinTables = new HashSet<>();

    private int whereBeginIndex = -1;
    private int whereEndIdnex = -1;

    public int getWhereBeginIndex() {
        return whereBeginIndex;
    }

    public void setWhereBeginIndex(int beginIndex) {
        whereBeginIndex = beginIndex;
    }

    public int getWhereEndIndex() {
        return whereEndIdnex;
    }

    public void setWhereEndIndex(int endIndex) {
        whereEndIdnex = endIndex;
    }

    public Set<String> getJoinTables() {
        return joinTables;
    }

    public void addJoinTable(String tableName) {
        joinTables.add(tableName);
    }

    public void addJoinTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            joinTables.addAll(tableNames);
        }
    }

    public void removeJoinTable(String tableName) {
        joinTables.remove(tableName);
    }

    public void removeJoinTables(Collection<String> tableNames) {
        if (N.notNullOrEmpty(tableNames)) {
            Iterables.removeAll(joinTables, tableNames);
        }
    }

    public void clearJoinTable() {
        joinTables.clear();
    }

    @Override
    public void combine(SQLCommand sqlCondCmd) {
        super.combine(sqlCondCmd);

        if (sqlCondCmd instanceof SQLCommand) {
            addJoinTables(((SQLCondCommand) sqlCondCmd).joinTables);
        }
    }

    @Override
    public Object clone() {
        SQLCondCommand copy = (SQLCondCommand) super.clone();

        copy.joinTables = new HashSet<>(joinTables);

        return copy;
    }

    @Override
    public void clear() {
        super.clear();

        joinTables.clear();

        whereBeginIndex = -1;
        whereEndIdnex = -1;
    }
}

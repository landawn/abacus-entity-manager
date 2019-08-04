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

import java.util.List;
import java.util.Map;

import com.landawn.abacus.EntityId;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityManagerVC.
 *
 * @author Haiyang Li
 * @param <T> the generic type
 * @since 0.8
 */
class EntityManagerVC<T> extends EntityManagerLVC<T> {
    
    /**
     * Instantiates a new entity manager VC.
     *
     * @param entityManagerConfig the entity manager config
     * @param dbAccess the db access
     */
    protected EntityManagerVC(EntityManagerConfiguration entityManagerConfig, DBAccessImpl dbAccess) {
        super(entityManagerConfig, dbAccess);
    }

    /**
     * Internal lock record.
     *
     * @param entityId the entity id
     * @param lockMode the lock mode
     * @param options the options
     * @return the string
     */
    @Override
    protected String internalLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options) {
        throw new UnsupportedOperationException();
    }

    /**
     * Internal unlock record.
     *
     * @param entityId the entity id
     * @param lockCode the lock code
     * @param options the options
     * @return true, if successful
     */
    @Override
    protected boolean internalUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options) {
        throw new UnsupportedOperationException();
    }

    /**
     * Check lock.
     *
     * @param entityIds the entity ids
     * @param requiredLockMode the required lock mode
     * @param options the options
     */
    @Override
    protected void checkLock(List<? extends EntityId> entityIds, LockMode requiredLockMode, Map<String, Object> options) {
        // ignore
    }
}

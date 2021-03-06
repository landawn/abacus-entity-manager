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

import static com.landawn.abacus.core.EntityManagerUtil.getEntityIdByEntity;
import static com.landawn.abacus.core.EntityManagerUtil.getLockCode;
import static com.landawn.abacus.core.EntityManagerUtil.getRecordLockTimeout;
import static com.landawn.abacus.core.EntityManagerUtil.getTransactionId;
import static com.landawn.abacus.core.EntityManagerUtil.removeOffsetCount;
import static com.landawn.abacus.core.EntityManagerUtil.resultSet2EntityId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.landawn.abacus.DataSet;
import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.LockMode;
import com.landawn.abacus.Transaction;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.LockConfiguration;
import com.landawn.abacus.core.AbacusConfiguration.EntityManagerConfiguration.VersionConfiguration;
import com.landawn.abacus.exception.RecordLockedException;
import com.landawn.abacus.lock.XLock;
import com.landawn.abacus.lock.XLockFactory;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.version.Version;
import com.landawn.abacus.version.VersionFactory;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @param <E>
 * @since 0.8
 */
class EntityManagerLV<E> extends EntityManagerImpl<E> {

    static final int VERSION_DELTA_FOR_DELETE = -1;

    static final int VERSION_DELTA_FOR_UPDATE = 1;

    private final Map<String, List<EntityIdMemo>> tranIdEntityIdsMap = new ConcurrentHashMap<>();

    private final XLock<EntityId> xRecordLock;

    private final Version<EntityId> recordVersion;

    protected EntityManagerLV(EntityManagerConfiguration entityManagerConfig, DBAccessImpl dbAccess) {
        super(entityManagerConfig, dbAccess);

        VersionConfiguration versionConfig = entityManagerConfig.getVersionConfiguration();

        if ((versionConfig == null) || (versionConfig.getProvider() == null)) {
            recordVersion = VersionFactory.createLocalVersion();
        } else {
            recordVersion = VersionFactory.createVersion(versionConfig.getProvider());
        }

        LockConfiguration lockConfiguration = entityManagerConfig.getLockConfiguration();

        if ((lockConfiguration == null) || (lockConfiguration.getRecordLockProvider() == null)) {
            xRecordLock = XLockFactory
                    .createLocalXLock(lockConfiguration == null ? LockConfiguration.DEFAULT_RECORD_LOCK_TIMEOUT : lockConfiguration.getRecordLockTimeout());
        } else {
            xRecordLock = XLockFactory.createLock(lockConfiguration.getRecordLockProvider());
        }
    }

    /**
     *
     * @param <T>
     * @param targetClass
     * @param entityName
     * @param selectPropNames
     * @param condition
     * @param options
     * @return
     */
    @Override
    protected <T> List<T> internalList(Class<T> targetClass, String entityName, Collection<String> selectPropNames, Condition condition,
            Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (N.isNullOrEmpty(entityDef.getIdPropertyList())) {
            return super.internalList(targetClass, entityName, selectPropNames, condition, options);
        } else {
            List<EntityId> entityIds = getEntityIdByCondition(entityName, condition, options);

            if ((targetClass == null) && N.isNullOrEmpty(entityIds)) {
                targetClass = entityDef.getTypeClass();
            }

            options = removeOffsetCount(options);

            List<T> result = getEntities(targetClass, entityDef, entityIds, selectPropNames, options);

            // can't reback for cache and version.
            // RecycableEntityId.reback(entityIds);
            return result;
        }
    }

    /**
     * Gets the entities.
     *
     * @param <T>
     * @param targetClass
     * @param entityDef
     * @param entityIds
     * @param selectPropNames
     * @param options
     * @return
     */
    @Override
    protected <T> List<T> getEntities(Class<T> targetClass, EntityDefinition entityDef, List<? extends EntityId> entityIds, Collection<String> selectPropNames,
            Map<String, Object> options) {
        if (targetClass == null) {
            targetClass = entityDef.getTypeClass();
        }

        if (entityIds.size() == 0) {
            return new ArrayList<>();
        }

        checkLock(entityIds, LockMode.R, options);

        final boolean isDirtyMarker = DirtyMarkerUtil.isDirtyMarker(targetClass);

        Map<EntityId, Long> entitiesVersion = null;

        if (isDirtyMarker) {
            entitiesVersion = new HashMap<>();

            for (EntityId entityId : entityIds) {
                entitiesVersion.put(entityId, recordVersion.get(entityId));
            }
        }

        List<T> entities = super.getEntities(targetClass, entityDef, selectPropNames, EntityManagerUtil.entityId2Condition(entityIds), options);

        if ((entities.size() > 0) && isDirtyMarker) {
            EntityId entityId = null;

            for (T entity : entities) {
                entityId = getEntityIdByEntity(entityDef, entity);

                DirtyMarkerUtil.setVersion((DirtyMarker) entity, entitiesVersion.get(entityId));
            }
        }

        return entities;
    }

    /**
     * Adds the entities.
     *
     * @param entityDef
     * @param propsList
     * @param options
     * @return
     */
    @Override
    protected List<EntityId> addEntities(EntityDefinition entityDef, List<Map<String, Object>> propsList, Map<String, Object> options) {
        if (propsList.size() == 0) {
            return new ArrayList<>();
        }

        String transactionId = getTransactionId(options);

        List<EntityId> entityIds = super.addEntities(entityDef, propsList, options);

        if (transactionId == null) {
            updateRecordVersion(entityIds, 1);
        } else {
            addEntityIdsByTran(transactionId, entityIds, OperationType.ADD);
        }

        return entityIds;
    }

    /**
     *
     * @param entityName
     * @param props
     * @param condition
     * @param options
     * @return
     */
    @Override
    protected int internalUpdate(String entityName, Map<String, Object> props, Condition condition, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (N.isNullOrEmpty(entityDef.getIdPropertyList())) {
            return super.internalUpdate(entityName, props, condition, options);
        } else {
            // TODO should call: update(entityId, props, options); ?
            List<EntityId> entityIds = getEntityIdByCondition(entityName, condition, options);

            return updateEntities(entityDef, entityIds, props, options);
        }
    }

    /**
     *
     * @param entityDef
     * @param entityIds
     * @param props
     * @param options
     * @return
     */
    @Override
    protected int updateEntities(EntityDefinition entityDef, List<? extends EntityId> entityIds, Map<String, Object> props, Map<String, Object> options) {
        if (entityIds.size() == 0) {
            return 0;
        }

        checkLock(entityIds, LockMode.U, options);

        String transactionId = getTransactionId(options);

        int result = super.updateEntities(entityDef, entityIds, props, options);

        if (transactionId == null) {
            // TODO how if id properties is updated?
            updateRecordVersion(entityIds, VERSION_DELTA_FOR_UPDATE);

            // // can't recycle for cache and version.
            // RecycableEntityId.reback(entityIds);
        } else {
            addEntityIdsByTran(transactionId, entityIds, OperationType.UPDATE);
        }

        return result;
    }

    /**
     *
     * @param entityName
     * @param cond
     * @param options
     * @return
     */
    @Override
    protected int internalDelete(String entityName, Condition cond, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);

        if (N.isNullOrEmpty(entityDef.getIdPropertyList())) {
            return super.internalDelete(entityName, cond, options);
        } else {
            List<EntityId> entityIds = getEntityIdByCondition(entityName, cond, options);

            // TODO should call: delete(entityIds, options); ?
            return deleteEntities(entityDef, entityIds, options);
        }
    }

    /**
     *
     * @param entityDef
     * @param entityIds
     * @param options
     * @return
     */
    @Override
    protected int deleteEntities(EntityDefinition entityDef, List<? extends EntityId> entityIds, Map<String, Object> options) {
        if (entityIds.size() == 0) {
            return 0;
        }

        checkLock(entityIds, LockMode.D, options);

        String transactionId = getTransactionId(options);

        int result = super.deleteEntities(entityDef, entityIds, options);

        if (transactionId == null) {
            updateRecordVersion(entityIds, VERSION_DELTA_FOR_DELETE);
        } else {
            addEntityIdsByTran(transactionId, entityIds, OperationType.DELETE);
        }

        return result;
    }

    /**
     * Internal get record version.
     *
     * @param entityId
     * @param options
     * @return
     */
    @Override
    protected long internalGetRecordVersion(EntityId entityId, Map<String, Object> options) {
        checkEntityId(entityId);

        return recordVersion.get(entityId);
    }

    /**
     * Internal lock record.
     *
     * @param entityId
     * @param lockMode
     * @param options
     * @return
     */
    @SuppressWarnings("deprecation")
    @Override
    protected String internalLockRecord(EntityId entityId, LockMode lockMode, Map<String, Object> options) {
        checkEntityId(entityId);

        if (LockMode.A.isXLockOf(lockMode)) {
            throw new IllegalArgumentException("Not supported lock mode[" + lockMode + "]. Can't lock a record on lock mode '" + LockMode.A + "'. ");
        }

        String lockCode = getLockCode(options);
        long timeout = getRecordLockTimeout(options, entityManagerConfig.getLockConfiguration());

        return xRecordLock.lock(entityId, lockMode, lockCode, timeout);
    }

    /**
     * Internal unlock record.
     *
     * @param entityId
     * @param lockCode
     * @param options
     * @return true, if successful
     */
    @Override
    protected boolean internalUnlockRecord(EntityId entityId, String lockCode, Map<String, Object> options) {
        checkEntityId(entityId);

        return xRecordLock.unlock(entityId, lockCode);
    }

    /**
     *
     * @param entityIds
     * @param requiredLockMode
     * @param options
     */
    protected void checkLock(List<? extends EntityId> entityIds, LockMode requiredLockMode, Map<String, Object> options) {
        String lockCode = getLockCode(options);

        for (EntityId entityId : entityIds) {
            if (xRecordLock.isLocked(entityId, requiredLockMode, lockCode)) {
                throw new RecordLockedException("[" + entityId + "] has been locked. ");
            }
        }
    }

    /**
     * Internal end transaction.
     *
     * @param transactionId
     * @param transactionAction
     * @param options
     */
    @Override
    protected void internalEndTransaction(String transactionId, Action transactionAction, Map<String, Object> options) {
        Transaction tran = dbAccess.getExecutant().getTransaction(transactionId);

        try {
            dbAccess.endTransaction(transactionId, transactionAction, options);
        } finally {
            updateRecordVersionByTran(transactionId, tran);
        }
    }

    /**
     * Update record version by tran.
     *
     * @param transactionId
     * @param tran
     */
    protected void updateRecordVersionByTran(String transactionId, Transaction tran) {
        if (N.isNullOrEmpty(transactionId) || (tran == null)) {
            return;
        }

        List<EntityIdMemo> entityIdMemoList = tranIdEntityIdsMap.remove(transactionId);

        if (N.isNullOrEmpty(entityIdMemoList)) {
            return;
        }

        List<? extends EntityId> entityIds = null;
        for (EntityIdMemo entityIdMemo : entityIdMemoList) {
            entityIds = entityIdMemo.entityIds;

            if (N.isNullOrEmpty(entityIds)) {
                continue;
            }

            if (entityIdMemo.operationType == OperationType.DELETE) {
                // if (tran.getStatus() == Status.COMMITTED_STATUS) {
                // updateRecordVersion(entityIds, DELETE_VERSION_DELTA,
                // options);
                // releaseId(entityDef, entityIds);
                // } else if (tran.getStatus() ==
                // Status.ROLLBACK_FAILED_STATUS) {
                // updateRecordVersion(entityIds, DELETE_VERSION_DELTA,
                // options);
                // }
                updateRecordVersion(entityIds, VERSION_DELTA_FOR_DELETE);
            } else if ((entityIdMemo.operationType == OperationType.UPDATE) || (entityIdMemo.operationType == OperationType.ADD)) {
                // if ((tran.getStatus() == Status.COMMITTED_STATUS)
                // || (tran.getStatus() ==
                // Status.ROLLBACK_FAILED_STATUS)) {
                // updateRecordVersion(entityIds, 1, options);
                // }
                updateRecordVersion(entityIds, 1);
            }
        }
    }

    /**
     * Adds the entity ids by tran.
     *
     * @param transactionId
     * @param entityIds
     * @param op
     */
    protected void addEntityIdsByTran(String transactionId, List<? extends EntityId> entityIds, OperationType op) {
        synchronized (tranIdEntityIdsMap) {
            List<EntityIdMemo> entityIdMemoList = tranIdEntityIdsMap.get(transactionId);

            if (entityIdMemoList == null) {
                entityIdMemoList = new ArrayList<>();
                tranIdEntityIdsMap.put(transactionId, entityIdMemoList);
            }

            entityIdMemoList.add(new EntityIdMemo(entityIds, op));
        }
    }

    /**
     * Gets the entity id by condition.
     *
     * @param entityName
     * @param cond
     * @param options
     * @return
     */
    protected List<EntityId> getEntityIdByCondition(String entityName, Condition cond, Map<String, Object> options) {
        final EntityDefinition entityDef = checkEntityName(entityName);
        final Collection<String> idPropNames = entityDef.getIdPropertyNameList();

        DataSet resultSet = internalQuery(entityName, idPropNames, cond, null, options);

        return resultSet2EntityId(entityDef, resultSet);
    }

    /**
     * Update record version.
     *
     * @param entityId
     * @param delta
     */
    private void updateRecordVersion(EntityId entityId, int delta) {
        if (delta == VERSION_DELTA_FOR_DELETE) {
            recordVersion.remove(entityId);
        } else {
            recordVersion.update(entityId, delta);
        }
    }

    /**
     * Update record version.
     *
     * @param entityIds
     * @param delta
     */
    private void updateRecordVersion(List<? extends EntityId> entityIds, int delta) {
        for (EntityId entityId : entityIds) {
            updateRecordVersion(entityId, delta);
        }
    }
}

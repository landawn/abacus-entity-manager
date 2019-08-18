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

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.landawn.abacus.DirtyMarker;
import com.landawn.abacus.EntityId;
import com.landawn.abacus.EntityManager;
import com.landawn.abacus.IsolationLevel;
import com.landawn.abacus.Session;
import com.landawn.abacus.Transaction;
import com.landawn.abacus.Transaction.Action;
import com.landawn.abacus.Transaction.Status;
import com.landawn.abacus.condition.Condition;
import com.landawn.abacus.logging.Logger;
import com.landawn.abacus.logging.LoggerFactory;
import com.landawn.abacus.util.ExceptionUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OperationType;
import com.landawn.abacus.util.Options;
import com.landawn.abacus.util.u.Optional;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionImpl.
 *
 * @author Haiyang Li
 * @param <E> the element type
 * @since 0.8
 */
@SuppressWarnings("deprecation")
final class SessionImpl<E> implements Session<E> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SessionImpl.class);

    /** The attached entities. */
    private final Map<E, OperationType> attachedEntities = new IdentityHashMap<>();

    /** The attached entity list. */
    private final List<E> attachedEntityList = new LinkedList<>();

    /** The entity manager. */
    private final EntityManager<E> entityManager;

    /** The isolation level. */
    private final IsolationLevel isolationLevel;

    /** The options. */
    private Map<String, Object> options;

    /** The transaction. */
    private TransactionProxy transaction;

    /** The is closed. */
    private boolean isClosed = false;

    /**
     * Instantiates a new session impl.
     *
     * @param entityManager the entity manager
     * @param isolationLevel the isolation level
     */
    SessionImpl(EntityManager<E> entityManager, IsolationLevel isolationLevel) {
        this.entityManager = entityManager;
        this.isolationLevel = isolationLevel;
    }

    /**
     * Begin transaction.
     *
     * @param isolationLevel the isolation level
     * @return
     */
    @Override
    public Transaction beginTransaction(IsolationLevel isolationLevel) {
        assertNotClosed();

        if (isInTransaction()) {
            throw new IllegalStateException("Transaction is already started. ");
        }

        transaction = new TransactionProxy(entityManager, isolationLevel);

        return transaction;
    }

    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return
     */
    @Override
    public <T> Optional<T> get(EntityId entityId, Collection<String> selectPropNames) {
        return Optional.ofNullable((T) gett(entityId, selectPropNames));
    }

    /**
     * Gets the t.
     *
     * @param <T> the generic type
     * @param entityId the entity id
     * @param selectPropNames the select prop names
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T gett(EntityId entityId, Collection<String> selectPropNames) {
        assertNotClosed();

        Map<String, Object> options = setTransaction();

        T entity = (T) entityManager.gett(entityId, selectPropNames, options);

        if (entity != null) {
            attach(OperationType.UPDATE, (E) entity);
        }

        return entity;
    }

    /**
     * List.
     *
     * @param <T> the generic type
     * @param entityName the entity name
     * @param selectPropNames the select prop names
     * @param condition the condition
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(String entityName, Collection<String> selectPropNames, Condition condition) {
        assertNotClosed();

        Map<String, Object> options = setTransaction();

        List<T> entities = entityManager.list(entityName, selectPropNames, condition, options);

        if (N.notNullOrEmpty(entities)) {
            attach(OperationType.UPDATE, (List<E>) entities);

            return entities;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Adds the.
     *
     * @param entities the entities
     */
    @SuppressWarnings("unchecked")
    @Override
    @SafeVarargs
    public final void add(E... entities) {
        assertNotClosed();

        attach(OperationType.ADD, entities);
    }

    /**
     * Adds the.
     *
     * @param entities the entities
     */
    @Override
    public void add(Collection<? extends E> entities) {
        E[] arrayOfEntity = collection2Array(entities);

        add(arrayOfEntity);
    }

    /**
     * Update.
     *
     * @param entities the entities
     */
    @SuppressWarnings("unchecked")
    @Override
    @SafeVarargs
    public final void update(E... entities) {
        attach(OperationType.UPDATE, entities);
    }

    /**
     * Update.
     *
     * @param entities the entities
     */
    @Override
    public void update(Collection<? extends E> entities) {
        E[] arrayOfEntity = collection2Array(entities);

        update(arrayOfEntity);
    }

    /**
     * Delete.
     *
     * @param entities the entities
     */
    @SuppressWarnings("unchecked")
    @Override
    @SafeVarargs
    public final void delete(E... entities) {
        assertNotClosed();

        attach(OperationType.DELETE, entities);
    }

    /**
     * Delete.
     *
     * @param entities the entities
     */
    @Override
    public void delete(Collection<? extends E> entities) {
        E[] arrayOfEntity = collection2Array(entities);

        delete(arrayOfEntity);
    }

    /**
     * Attach.
     *
     * @param operationType the operation type
     * @param entities the entities
     */
    @SuppressWarnings("unchecked")
    protected void attach(OperationType operationType, E... entities) {
        assertNotClosed();

        if (N.isNullOrEmpty(entities)) {
            return;
        }

        for (E entity : entities) {
            if (attachedEntities.put(entity, operationType) == null) {
                attachedEntityList.add(entity);
            }
        }
    }

    /**
     * Attach.
     *
     * @param operationType the operation type
     * @param entities the entities
     */
    @SuppressWarnings("unchecked")
    protected void attach(OperationType operationType, List<E> entities) {
        assertNotClosed();

        if (N.isNullOrEmpty(entities)) {
            return;
        }

        for (E entity : entities) {
            if (attachedEntities.put(entity, operationType) == null) {
                attachedEntityList.add(entity);
            }
        }
    }

    /**
     * Detach.
     *
     * @param entities the entities
     */
    @SuppressWarnings("unchecked")
    @Override
    @SafeVarargs
    public final void detach(E... entities) {
        assertNotClosed();

        if (N.isNullOrEmpty(entities)) {
            return;
        }

        for (E entity : entities) {
            if (attachedEntities.remove(entity) != null) {
                for (int i = 0; i < attachedEntityList.size(); i++) {
                    if (attachedEntityList.get(i) == entity) {
                        attachedEntityList.remove(i);

                        break;
                    }
                }
            }
        }
    }

    /**
     * Detach.
     *
     * @param entities the entities
     */
    @Override
    public void detach(Collection<? extends E> entities) {
        E[] arrayOfEntity = collection2Array(entities);

        detach(arrayOfEntity);
    }

    /**
     * Contains.
     *
     * @param entity the entity
     * @return true, if successful
     */
    @Override
    public boolean contains(E entity) {
        assertNotClosed();

        return attachedEntities.containsKey(entity);
    }

    /**
     * Flush.
     */
    @Override
    public void flush() {
        assertNotClosed();

        if (N.isNullOrEmpty(attachedEntityList)) {
            return;
        }

        Map<String, Object> options = setTransaction();
        String transactionId = null;

        if (!isInTransaction() && (attachedEntityList.size() > 1)) {
            transactionId = entityManager.beginTransaction(isolationLevel, null);

            if (options == null) {
                options = ParametersUtil.asOptions();
            }

            options.put(Options.TRANSACTION_ID, transactionId);
        }

        OperationType preOperationType = null;
        OperationType operationType = null;
        List<E> preEntities = new ArrayList<>();
        boolean isOk = false;

        try {
            for (E entity : attachedEntityList) {
                operationType = attachedEntities.get(entity);

                if ((preEntities.size() > 0) && ((operationType != preOperationType) || (!entity.getClass().equals(preEntities.get(0).getClass())))) {
                    commit(preEntities, preOperationType, options);

                    preEntities.clear();
                }

                if (operationType == OperationType.ADD) {
                    preEntities.add(entity);
                } else if (operationType == OperationType.DELETE) {
                    preEntities.add(entity);
                } else {
                    if (entity instanceof DirtyMarker) {
                        if (DirtyMarkerUtil.isDirty((DirtyMarker) entity)) {
                            entityManager.update(entity, options);
                        }
                    } else {
                        entityManager.update(entity, options);
                    }
                }

                preOperationType = operationType;
            }

            if (preEntities.size() > 0) {
                commit(preEntities, preOperationType, options);

                preEntities.clear();
            }

            isOk = true;
        } finally {
            if (transactionId != null) {
                options.remove(Options.TRANSACTION_ID);

                if (isOk) {
                    entityManager.endTransaction(transactionId, Action.COMMIT, null);
                } else {
                    try {
                        entityManager.endTransaction(transactionId, Action.ROLLBACK, null);
                    } catch (Exception e) {
                        // ignore
                        logger.error("Failed to roll back with transaction id: " + transactionId + ". " + ExceptionUtil.getMessage(e), e);
                    }
                }
            }

            if (isOk) {
                refreshStatus();
            }
        }
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        assertNotClosed();

        attachedEntities.clear();
        attachedEntityList.clear();
    }

    /**
     * Checks if is dirty.
     *
     * @return true, if is dirty
     */
    @Override
    public boolean isDirty() {
        assertNotClosed();

        OperationType operationType = null;

        for (E entity : attachedEntities.keySet()) {
            operationType = attachedEntities.get(entity);

            if ((operationType == OperationType.ADD) || (operationType == OperationType.DELETE)) {
                return true;
            } else {
                if (entity instanceof DirtyMarker) {
                    if (DirtyMarkerUtil.isDirty((DirtyMarker) entity)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        assertNotClosed();

        boolean isOk = false;

        try {
            flush();

            isOk = true;
        } finally {
            if (isInTransaction()) {
                if (isOk) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            }
        }

        clear();

        isClosed = true;
    }

    /**
     * Checks if is closed.
     *
     * @return true, if is closed
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Commit.
     *
     * @param entities the entities
     * @param operationType the operation type
     * @param options the options
     */
    @SuppressWarnings("unchecked")
    private void commit(List<E> entities, OperationType operationType, Map<String, Object> options) {
        if (N.isNullOrEmpty(entities)) {
            return;
        }

        if (operationType == OperationType.DELETE) {
            if (entities.size() == 1) {
                entityManager.delete(entities.get(0), options);
            } else {
                entityManager.deleteAll(entities, options);
            }
        } else if (operationType == OperationType.ADD) {
            if (entities.size() == 1) {
                entityManager.add(entities.get(0), options);
            } else {
                entityManager.addAll(entities, options);
            }
        }
    }

    /**
     * Collection 2 array.
     *
     * @param entities the entities
     * @return
     */
    @SuppressWarnings("unchecked")
    private E[] collection2Array(Collection<?> entities) {
        E[] arrayOfEntity = null;

        if (entities != null) {
            arrayOfEntity = (E[]) entities.toArray();
        }

        return arrayOfEntity;
    }

    /**
     * Refresh status.
     */
    @SuppressWarnings("unchecked")
    private void refreshStatus() {
        List<E> keys = new ArrayList<>(attachedEntities.keySet());
        OperationType op = null;

        for (E e : keys) {
            op = attachedEntities.get(e);

            if (op == OperationType.DELETE) {
                detach(e);
            } else if (op == OperationType.ADD) {
                attachedEntities.put(e, OperationType.UPDATE);
            }
        }
    }

    /**
     * Sets the transaction.
     *
     * @return
     */
    private Map<String, Object> setTransaction() {
        if (isInTransaction()) {
            if (options == null) {
                options = ParametersUtil.asOptions();
            }

            options.put(Options.TRANSACTION_ID, transaction.id);
        } else {
            if (options != null) {
                options.remove(Options.TRANSACTION_ID);
            }
        }

        return options;
    }

    /**
     * Checks if is in transaction.
     *
     * @return true, if is in transaction
     */
    private boolean isInTransaction() {
        if (transaction == null) {
            return false;
        } else if (transaction.status() == Status.ACTIVE) {
            return true;
        } else {
            transaction = null;

            return false;
        }
    }

    /**
     * Assert not closed.
     */
    private void assertNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("The session has been closed. ");
        }
    }

    /**
     * To string.
     *
     * @return
     */
    @Override
    public String toString() {
        return "{isClosed=" + isClosed + ", transactionId=" + ((transaction == null) ? "null" : transaction.id) + ", attached entities: "
                + attachedEntities.toString() + "}";
    }

    /**
     * The Class TransactionProxy.
     *
     * @author Haiyang Li
     * @version $Revision: 0.8 $ 07/03/05
     */
    static final class TransactionProxy implements Transaction {

        /** The entity manager. */
        private final EntityManager<?> entityManager;

        /** The id. */
        private final String id;

        /** The isolation level. */
        private final IsolationLevel isolationLevel;

        /** The status. */
        private Status status;

        /**
         * Instantiates a new transaction proxy.
         *
         * @param entityManager the entity manager
         * @param isolationLevel the isolation level
         */
        TransactionProxy(EntityManager<?> entityManager, IsolationLevel isolationLevel) {
            this.entityManager = entityManager;
            this.id = entityManager.beginTransaction(isolationLevel, null);
            this.isolationLevel = isolationLevel;
            this.status = Status.ACTIVE;
        }

        /**
         * Id.
         *
         * @return
         */
        @Override
        public String id() {
            return id;
        }

        /**
         * Isolation level.
         *
         * @return
         */
        @Override
        public IsolationLevel isolationLevel() {
            return isolationLevel;
        }

        /**
         * Status.
         *
         * @return
         */
        @Override
        public Status status() {
            return status;
        }

        /**
         * Checks if is active.
         *
         * @return true, if is active
         */
        @Override
        public boolean isActive() {
            return status == Status.ACTIVE;
        }

        /**
         * Commit.
         */
        @Override
        public void commit() {
            if (!status.equals(Status.ACTIVE)) {
                throw new IllegalStateException("transaction is already " + status);
            }

            status = Status.FAILED_COMMIT;

            entityManager.endTransaction(id, Action.COMMIT, null);

            status = Status.COMMITTED;
        }

        /**
         * Rollback.
         */
        @Override
        public void rollback() {
            if (!status.equals(Status.ACTIVE)) {
                throw new IllegalStateException("transaction is already " + status);
            }

            status = Status.FAILED_ROLLBACK;

            entityManager.endTransaction(id, Action.ROLLBACK, null);

            status = Status.ROLLED_BACK;
        }
    }
}

/*
 * Copyright (C) 2015 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus.metadata;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityDefXmlEle.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public final class EntityDefXmlEle {
    /**
     * Constructor for EntityDefXmlEle.
     */
    private EntityDefXmlEle() {
        // no instance
    }

    /**
     * The Class EntityDefEle.
     *
     * @author Haiyang Li
     * @version $Revision: 0.8 $ 07/01/15
     */
    public static final class EntityDefEle {
        /**
         * Field ENTITY_DEF. (value is ""entityDef"")
         */
        public static final String ENTITY_DEF = "entityDef";

        /**
         * Field PACKAGE. (value is ""package"")
         */
        public static final String PACKAGE = "package";

        /**
         * Constructor for DomainEle.
         */
        private EntityDefEle() {
        }

        /**
         * The Class EntityEle.
         *
         * @author Haiyang Li
         * @version $Revision: 0.8 $ 07/01/15
         */
        public static final class EntityEle {
            /**
             * Field ENTITY. (value is ""entity"")
             */
            public static final String ENTITY = "entity";

            /**
             * Field NAME. (value is ""name"")
             */
            public static final String NAME = "name";

            /**
             * Field TABLE. (value is ""table"")
             */
            public static final String TABLE = "table";

            /**
             * Composite ids are split by ','. for example <br>
             * {@code id="id1, id2, id3..."} <br>
             * (value is ""id"")
             */
            public static final String ID = "id";

            /**
             * Only single unique property is supported. composite unique properties are not supports. Multiple single
             * unique properties are split by ','. <br>
             * {@code uid="uid1, uid2, uid3..."} <br>
             * (value is ""id"")
             */
            public static final String UID = "uid";

            /**
             * Field CLASS. (value is ""class"")
             */
            public static final String CLASS = "class";

            /**
             * Field SLICES. (value is ""slices"")
             */
            public static final String SLICES = "slices";

            /**
             * Field PROPERTIES. (value is ""properties"")
             */
            public static final String PROPERTIES = "properties";

            /**
             * Constructor for EntityEle.
             */
            private EntityEle() {
                // no instance
            }

            /**
             * The Class PropertyEle.
             *
             * @author Haiyang Li
             * @version $Revision: 0.8 $ 07/01/15
             */
            public static final class PropertyEle {
                /**
                 * Not supported at present. (value is ""property"")
                 */
                public static final String PROPERTY = "property";

                /**
                 * Field LIST. (value is ""list"")
                 */
                public static final String LIST = "list";

                /**
                 * Field SET. (value is ""set"")
                 */
                public static final String SET = "set";

                /**
                 * Field NAME. (value is ""name"")
                 */
                public static final String NAME = "name";

                /**
                 * 
                 * you can set 'ID_GENERATOR=default', Then Abacus will manage id automatically according to its type.
                 * You also can set 'ID_GENERATOR=yourself id generator which must implements the interface
                 * com.landawn.abacus.idGenerator.IdGenerator' and have the 'constructor(com.landawn.abacus.Property
                 * prop)'. <br>
                 * 
                 * @see com.landawn.abacus.idGenerator.AbstractIdGenerator
                 */
                public static final String ID_GENERATOR = "idGenerator";

                /**
                 * Field COLUMN. (value is ""column"")
                 */
                public static final String COLUMN = "column";

                /**
                 * Field TYPE. (value is ""type"")
                 */
                public static final String TYPE = "type";

                /**
                 * Field VALIDATOR. (value is ""validator"")
                 */
                public static final String VALIDATOR = "validator";

                /**
                 * Field ORDER_BY. (value is ""orderBy"")
                 */
                public static final String ORDER_BY = "orderBy";

                /**
                 * Field READABLE. (value is ""readable"")
                 */
                public static final String READABLE = "readable";

                /**
                 * Field INSERTABLE. (value is ""insertable"")
                 */
                public static final String INSERTABLE = "insertable";

                /**
                 * Field UPDATABLE. (value is ""updatable"")
                 */
                public static final String UPDATABLE = "updatable";

                /**
                 * Field READ_ONLY. (value is ""readOnly"")
                 */
                public static final String READ_ONLY = "readOnly";

                /**
                 * Field DEFAULT_ON_INSERT. (value is ""defaultOnInsert"")
                 */
                public static final String DEFAULT_ON_INSERT = "defaultOnInsert";

                /**
                 * Field DEFAULT_ON_UPDATE. (value is ""defaultOnUpdate"")
                 */
                public static final String DEFAULT_ON_UPDATE = "defaultOnUpdate";

                /**
                 * TODO, undecided.
                 */
                public static final String JOIN_ON = "joinOn";

                /**
                 * TODO, undecided.
                 */
                public static final String ACTION_ON_UPDATE = "actionOnUpdate";

                /**
                 * TODO, undecided.
                 */
                public static final String ACTION_ON_DELETE = "actionOnDelete";

                /**
                 * Field DEFAULT. (value is ""default"")
                 */
                public static final String DEFAULT = "default";

                /**
                 * Field SYS_TIME. (value is ""sysTime"")
                 */
                public static final String SYS_TIME = "sysTime";

                /**
                 * TODO, undecided.
                 */
                public static final String COLLECTION = "collection";

                /**
                 * TODO, undecided.
                 */
                public static final String LOAD_BY_DEFAULT = "loadByDefault";

                /**
                 * Constructor for PropertyEle.
                 */
                private PropertyEle() {
                    // no instance
                }
            }

            /**
             * The Class TableEle.
             *
             * @author Haiyang Li
             * @version $Revision: 0.8 $ 07/01/15
             */
            public static final class TableEle {
                /**
                 * Field TABLE. (value is ""table"")
                 */
                public static final String TABLE = "table";

                /**
                 * Field NAME. (value is ""name"")
                 */
                public static final String NAME = "name";

                /**
                 * Constructor for TableEle.
                 */
                private TableEle() {
                    // no instance
                }

                /**
                 * The Class ColumnEle.
                 *
                 * @author Haiyang Li
                 * @version $Revision: 0.8 $ 07/01/15
                 */
                public static final class ColumnEle {
                    /**
                     * Field COLUMN. (value is ""column"")
                     */
                    public static final String COLUMN = "column";

                    /**
                     * Field NAME. (value is ""name"")
                     */
                    public static final String NAME = "name";

                    /**
                     * Field JAVA_TYPE. (value is ""javaType"")
                     */
                    public static final String JAVA_TYPE = "javaType";

                    /**
                     * Field JDBC_TYPE. (value is ""jdbcType"")
                     */
                    public static final String JDBC_TYPE = "jdbcType";

                    /**
                     * Field SQL_TYPE. (value is ""sqlType"")
                     */
                    public static final String SQL_TYPE = "sqlType";

                    /**
                     * Field DEFAULT_VALUE. (value is ""defaultValue"")
                     */
                    public static final String DEFAULT_VALUE = "defaultValue";

                    /**
                     * Field IS_PRIMARY_KEY. (value is ""isPrimaryKey"")
                     */
                    public static final String IS_PRIMARY_KEY = "isPrimaryKey";

                    /**
                     * Field IS_UNIQUE. (value is ""isUnique"")
                     */
                    public static final String IS_UNIQUE = "isUnique";

                    /**
                     * Field IS_NULLABLE. (value is ""isNullable"")
                     */
                    public static final String IS_NULLABLE = "isNullable";

                    /**
                     * Field IS_READ_ONLY. (value is ""isReadOnly"")
                     */
                    public static final String IS_READ_ONLY = "isReadOnly";

                    /**
                     * Field IS_WRITABLE. (value is ""isWritable"")
                     */
                    public static final String IS_WRITABLE = "isWritable";

                    /**
                     * Field IS_SEARCHABLE. (value is ""isSearchable"")
                     */
                    public static final String IS_SEARCHABLE = "isSearchable";

                    /**
                     * Field IS_CASE_SENSITIVE. (value is ""isCaseSensitive"")
                     */
                    public static final String IS_CASE_SENSITIVE = "isCaseSensitive";

                    /**
                     * Field IS_AUTO_INCREMENT. (value is ""isAutoIncrement"")
                     */
                    public static final String IS_AUTO_INCREMENT = "isAutoIncrement";

                    /**
                     * Constructor for ColumnEle.
                     */
                    private ColumnEle() {
                        // no instance
                    }
                }
            }
        }
    }

    /**
     * The Class DatabaseEle.
     *
     * @author Haiyang Li
     * @version $Revision: 0.8 $ 07/01/15
     */
    public static final class DatabaseEle {
        /**
         * Field DATABASE. (value is ""database"")
         */
        public static final String DATABASE = "database";

        /**
         * Field NAME. (value is ""name"")
         */
        public static final String NAME = "name";

        /**
         * Field PRODUCT_NAME. (value is ""productName"")
         */
        public static final String PRODUCT_NAME = "productName";

        /**
         * Field PRODUCT_NAME. (value is ""productName"")
         */
        public static final String PRODUCT_VERSTION = "productVersion";

        /**
         * Constructor for EntityEle.
         */
        private DatabaseEle() {
            // no instance
        }
    }
}

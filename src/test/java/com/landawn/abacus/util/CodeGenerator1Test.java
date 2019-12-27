/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.landawn.abacus.AbstractEntityManager0Test;
import com.landawn.abacus.impl.MyAbstractDirtyMarker;
import com.landawn.abacus.impl.MyDirtyMarker;
import com.landawn.abacus.impl.MyDirtyMarkerImpl;
import com.landawn.abacus.metadata.sql.SQLDatabase;
import com.landawn.abacus.util.CodeGenerator2.EntityMode;

import junit.framework.TestCase;

/**
 *
 * @since 0.8
 *
 * @author Haiyang Li
 */
public class CodeGenerator1Test extends TestCase {
    static final DataSource ds;
    static {
        Properties<String, String> props = PropertiesUtil.load(new File("./src/test/resources/config/abacus-entity-manager.properties"));
        Properties<String, String> jdbcProperties = new Properties<>();
        for (Map.Entry<String, String> entry : props.entrySet()) {
            if (entry.getKey().startsWith("jdbc.")) {
                jdbcProperties.put(entry.getKey().substring(5), entry.getValue());
            }
        }

        ds = JdbcUtil.createDataSource(jdbcProperties);
    }

    protected static final String srcPath = "./src/test/java/";
    static final List<String> selectedTables = new ArrayList<>();

    static {
        selectedTables.add("author");
        selectedTables.add("email");
        selectedTables.add("book");
        selectedTables.add("data_type");
        selectedTables.add("contact");
        selectedTables.add("AuthorBook");

        //        selectedTables.add("login");
        //        selectedTables.add("account");
    }

    boolean reinitEntityDefinition = true;
    boolean reinitClass = true;
    boolean testMyImpl = false;

    public void testForExtendDirtyMarker() {
        generate("ExtendDirty", EntityMode.EXTEND_DIRTY_MARKER, MyAbstractDirtyMarker.class, MyDirtyMarker.class);
    }

    public void testForImplDirtyMarker() {
        generate("ImplDirty", EntityMode.IMPL_DIRTY_MARKER, MyDirtyMarkerImpl.class, MyDirtyMarker.class);
    }

    public void testForPOJO() {
        generate("NoInherit", EntityMode.POJO, null);
    }

    public void testForStringId() {
        generate("StringId", EntityMode.EXTEND_DIRTY_MARKER, null);
    }

    public void testSQLMapperIdTableGeneration() {
        String sqlMapperFile = "./src/test/resources/config/sqlMapper.xml";
        String packageName = "com.landawn.abacus.entity.sqlMapper";
        String className = "SQLMapperIdTable";
        Method id2VarName = null;
        CodeGenerator2.generateSQLMapperIdTable(sqlMapperFile, srcPath, packageName, className, id2VarName);
    }

    public void generate(String domainName, EntityMode entityMode, Class<?> extendedClass, Class<?>... implInterface) {
        Connection conn = JdbcUtil.getConnection(ds);

        SQLDatabase database = new SQLDatabase(conn, AbstractEntityManager0Test.databaseName, selectedTables);
        JdbcUtil.closeQuietly(null, conn);

        File entityDefinitionFile = getEntityDefinitionXmlFile(domainName);

        if (reinitEntityDefinition) {
            CodeGenerator2.database2EntityDefinitionXml(database, entityDefinitionFile);
        }

        if (reinitClass) {
            if (testMyImpl) {
                CodeGenerator2.entityDefinitionXml2Class(domainName, entityDefinitionFile, srcPath, entityMode, null, null, false, extendedClass,
                        implInterface);
            } else {
                CodeGenerator2.entityDefinitionXml2Class(domainName, entityDefinitionFile, srcPath, entityMode);
            }

            CodeGenerator2.entityDefinitionXml2ColumnNameTable(domainName, entityDefinitionFile, srcPath);
        }
    }

    protected File getEntityDefinitionXmlFile(String domainName) {
        String directoryPath = srcPath + AbstractEntityManager0Test.class.getPackage().getName().replace('.', '/') + "/entity/"
                + domainName.substring(0, 1).toLowerCase() + domainName.substring(1) + "/";

        // create entity definition from database.
        File entityDefinitionFile = null;

        if (domainName.endsWith("Entity")) {
            entityDefinitionFile = new File(directoryPath + domainName + "Definition.xml");
        } else {
            entityDefinitionFile = new File(directoryPath + domainName + "EntityDefinition.xml");
        }

        return entityDefinitionFile;
    }

    static String tableName2EntityName1(String tableName) {
        return CodeGenerator2.tableName2EntityName(tableName);
    }

    static String columnName2PropName2(String columnName) {
        return CodeGenerator2.columnName2PropName(columnName);
    }
}

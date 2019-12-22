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

package com.landawn.abacus.metadata.sql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.exception.UncheckedIOException;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle;
import com.landawn.abacus.metadata.EntityDefXmlEle.EntityDefEle.EntityEle;
import com.landawn.abacus.metadata.EntityDefinition;
import com.landawn.abacus.metadata.EntityDefinitionFactory;
import com.landawn.abacus.metadata.Property;
import com.landawn.abacus.util.Configuration;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.ImmutableMap;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.XMLUtil;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating SQLEntityDefinition objects.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class SQLEntityDefinitionFactory implements EntityDefinitionFactory {

    /** The domain name. */
    private final String domainName;

    /** The domain definition. */
    private final byte[] domainDefinition;

    /** The attrs. */
    private final Map<String, String> attrs;

    /** The entity definition pool. */
    private final Map<String, SQLEntityDefinition> entityDefinitionPool;

    /**
     * Instantiates a new SQL entity definition factory.
     *
     * @param domainName
     * @param byteDef
     */
    protected SQLEntityDefinitionFactory(String domainName, byte[] byteDef) {
        this.domainName = NameUtil.getCachedName(domainName);
        this.domainDefinition = byteDef;

        final Object[] results = parse(Configuration.parse(new ByteArrayInputStream(byteDef)).getDocumentElement());
        this.attrs = ImmutableMap.of((Map<String, String>) results[0]);
        this.entityDefinitionPool = ImmutableMap.of((Map<String, SQLEntityDefinition>) results[1]);

        // temporary solution: initialize ahead to avoid the Double-checked
        // locking issue
        // http://en.wikipedia.org/wiki/Double-checked_locking
        // http://www.ibm.com/developerworks/java/library/j-dcl/index.html

        final Set<Class<?>> entityTypeClass = N.newHashSet();

        for (SQLEntityDefinition entityDef : entityDefinitionPool.values()) {
            entityDef.setFactory(this);

            if (!entityDef.isSliceEntity()) {
                Class<?> typeClass = entityDef.getTypeClass();

                if (entityTypeClass.contains(typeClass) && !Object.class.equals(typeClass)) {
                    throw new RuntimeException("Two entity can't have same type class: " + typeClass.getCanonicalName());
                }

                entityTypeClass.add(typeClass);
            }

            for (Property prop : entityDef.getPropertyList()) {
                prop.getType();
                prop.getSubPropertyList();
            }
        }
    }

    /**
     *
     * @param domainName
     * @param entityDefinitionXmlFile
     * @return
     * @throws UncheckedIOException the unchecked IO exception
     */
    public static synchronized SQLEntityDefinitionFactory newInstance(String domainName, File entityDefinitionXmlFile) throws UncheckedIOException {
        entityDefinitionXmlFile = Configuration.formatPath(entityDefinitionXmlFile);

        InputStream is = null;

        try {
            is = new FileInputStream(entityDefinitionXmlFile);

            return newInstance(domainName, is);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        } finally {
            IOUtil.close(is);
        }
    }

    /**
     *
     * @param domainName
     * @param is
     * @return
     */
    public static synchronized SQLEntityDefinitionFactory newInstance(String domainName, InputStream is) {
        return new SQLEntityDefinitionFactory(domainName, IOUtil.readAllBytes(is));
    }

    /**
     *
     * @return
     */
    @Override
    public String domainName() {
        return domainName;
    }

    /**
     * Gets the entity name list.
     *
     * @return
     */
    @Override
    public Collection<String> getEntityNameList() {
        return entityDefinitionPool.keySet();
    }

    /**
     * Gets the definition.
     *
     * @param entityName
     * @return
     */
    @Override
    public EntityDefinition getDefinition(String entityName) {
        return entityDefinitionPool.get(entityName);
    }

    /**
     * Gets the definition list.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Collection<EntityDefinition> getDefinitionList() {
        return (Collection) entityDefinitionPool.values();
    }

    /**
     * Gets the attributes.
     *
     * @return
     */
    @Override
    public Map<String, String> getAttributes() {
        return attrs;
    }

    /**
     * Gets the attribute.
     *
     * @param attrName
     * @return
     */
    @Override
    public String getAttribute(String attrName) {
        return attrs.get(attrName);
    }

    /**
     *
     * @return
     */
    @Override
    public byte[] exportDefinition() {
        return domainDefinition;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return entityDefinitionPool.hashCode();
    }

    /**
     *
     * @param obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof SQLEntityDefinitionFactory && N.equals(((SQLEntityDefinitionFactory) obj).entityDefinitionPool, entityDefinitionPool));

    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return attrs.toString();
    }

    /**
     *
     * @param entityDefElement
     * @return
     */
    private static Object[] parse(Element entityDefElement) {
        final String pkgName = entityDefElement.getAttribute(EntityDefEle.PACKAGE);
        final Map<String, String> attrs = XMLUtil.readAttributes(entityDefElement);
        final Map<String, SQLEntityDefinition> entityDefinitionPool = new LinkedHashMap<>();

        final List<Element> entityElementList = XMLUtil.getElementsByTagName(entityDefElement, EntityEle.ENTITY);
        for (Element entityElement : entityElementList) {
            if (entityElement.getParentNode() instanceof Element && (((Element) entityElement.getParentNode()).getTagName().equals(EntityEle.SLICES))) {
                continue;
            }

            SQLEntityDefinition ed = new SQLEntityDefinition(null, pkgName, entityElement);
            entityDefinitionPool.put(ed.getName(), ed);

            for (EntityDefinition sliceEntityDef : ed.getSliceEntityList()) {
                entityDefinitionPool.put(sliceEntityDef.getName(), (SQLEntityDefinition) sliceEntityDef);
            }
        }

        return new Object[] { attrs, entityDefinitionPool };
    }
}

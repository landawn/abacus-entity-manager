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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.landawn.abacus.core.NameUtil;
import com.landawn.abacus.exception.AbacusException;
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

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class SQLEntityDefinitionFactory implements EntityDefinitionFactory {
    private final String domainName;
    private final byte[] domainDefinition;
    private final Map<String, String> attrs;
    private final Map<String, SQLEntityDefinition> entityDefinitionPool;

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

        final Set<Class<?>> entityTypeClass = new HashSet<>();

        for (SQLEntityDefinition entityDef : entityDefinitionPool.values()) {
            entityDef.setFactory(this);

            if (!entityDef.isSliceEntity()) {
                Class<?> typeClass = entityDef.getTypeClass();

                if (entityTypeClass.contains(typeClass) && !Object.class.equals(typeClass)) {
                    throw new AbacusException("Two entity can't have same type class: " + typeClass.getCanonicalName());
                }

                entityTypeClass.add(typeClass);
            }

            for (Property prop : entityDef.getPropertyList()) {
                prop.getType();
                prop.getSubPropertyList();
            }
        }
    }

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

    public static synchronized SQLEntityDefinitionFactory newInstance(String domainName, InputStream is) {
        return new SQLEntityDefinitionFactory(domainName, IOUtil.readBytes(is));
    }

    @Override
    public String domainName() {
        return domainName;
    }

    @Override
    public Collection<String> getEntityNameList() {
        return entityDefinitionPool.keySet();
    }

    @Override
    public EntityDefinition getDefinition(String entityName) {
        return entityDefinitionPool.get(entityName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Collection<EntityDefinition> getDefinitionList() {
        return (Collection) entityDefinitionPool.values();
    }

    @Override
    public Map<String, String> getAttributes() {
        return attrs;
    }

    @Override
    public String getAttribute(String attrName) {
        return attrs.get(attrName);
    }

    @Override
    public byte[] exportDefinition() {
        return domainDefinition;
    }

    @Override
    public int hashCode() {
        return entityDefinitionPool.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof SQLEntityDefinitionFactory && N.equals(((SQLEntityDefinitionFactory) obj).entityDefinitionPool, entityDefinitionPool));

    }

    @Override
    public String toString() {
        return attrs.toString();
    }

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

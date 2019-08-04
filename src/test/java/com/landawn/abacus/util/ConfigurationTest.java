/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.landawn.abacus.AbstractTest;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public class ConfigurationTest extends AbstractTest {
    public void testFindFile() {
        assertNotNull(Configuration.findFile("Configuration.java"));
    }

    public void testReadWriteAttr() throws IOException {
        File databaseXml = new File("./config/abacus.xml");
        Document doc = Configuration.parse(databaseXml);

        N.println(Configuration.readElement(doc.getDocumentElement()));

        InputStream is = new FileInputStream(databaseXml);
        doc = Configuration.parse(is);
        is.close();

        Configuration config = new Configuration(doc.getDocumentElement(), null) {
            @Override
            protected void complexElement2Attr(Element element) {
                N.println(element.getTagName());
            }
        };

        N.println(config.toString());
        assertEquals(config, config);
    }

    public void testGetCommonConfigPath() {
        String st = "..\\..\\abc\\abc.txt";
        N.println(st.replaceAll("\\.\\.\\" + File.separatorChar, ""));
        N.println(st.replaceAll("\\.\\.\\" + '\\', ""));
        N.println(st.replaceAll("\\.\\.\\" + '/', ""));

        st = "../../abc/abc.txt";
        N.println(st.replaceAll("\\.\\.\\" + File.separatorChar, ""));
        N.println(st.replaceAll("\\.\\.\\" + '\\', ""));
        N.println(st.replaceAll("\\.\\.\\" + '/', ""));

        N.println(Configuration.getCommonConfigPath());

        String path = "classes/com/landawn/abacus/EntityId.class";
        N.println(Configuration.findFile(path));

        path = "./abacus/EntityId.class";
        N.println(Configuration.findFile(path));

        path = "./../../abacus/EntityId.class";
        N.println(Configuration.findFile(path));

        path = "classes\\com\\landawn\\abacus\\EntityId.class";
        N.println(Configuration.findFile(path));

        path = ".\\abacus\\EntityId.class";
        N.println(Configuration.findFile(path));

        path = ".\\..\\..\\abacus\\EntityId.class";
        N.println(Configuration.findFile(path));
    }

    public void testReadTimeValue() {
        N.println(Configuration.readTimeValue(null));
        N.println(Configuration.readTimeValue("9032802349890"));

        assertEquals(24 * 3600 * 1000, Configuration.readTimeValue("24 * 3600 * 1000"));
        assertEquals(30 * 7 * 24 * 3600 * 1000L, Configuration.readTimeValue("30* 7 * 24 * 3600 * 1000L"));
    }
}

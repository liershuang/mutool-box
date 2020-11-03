package com.mutool.javafx.core.xml;

import java.io.File;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

public class XmlDocumentTest {

    @Test
    public void testSetElement() throws Exception {
        File xmlFile = new File("src/test/resources/xml-document-test.xml");
        XmlDocument xmlDocument = XmlDocument.readFile(xmlFile);

        Element encoder = xmlDocument.selectSingleElement("/configuration/appender/encoder");
        Assert.assertNotNull(encoder);

        xmlDocument.addOrReplaceChildElement(encoder, "charset", "UTF8");
        xmlDocument.save();
    }
}
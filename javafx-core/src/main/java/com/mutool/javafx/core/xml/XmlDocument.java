package com.mutool.javafx.core.xml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 对 dom4j 的常用操作做进一步封装
 */
public class XmlDocument {

    public static XmlDocument readFile(File file) throws DocumentException {
        return new XmlDocument(file);
    }

    public static XmlDocument newDocument() throws DocumentException {
        return new XmlDocument(null);
    }

    public static Element element(String name, String content) {
        Element element = DocumentFactory.getInstance().createElement(name);
        element.setText(content);
        return element;
    }

    //////////////////////////////////////////////////////////////

    private File currentFile;

    private Document document;

    private XmlDocument(File file) throws DocumentException {
        this.currentFile = file;
        this.document = new SAXReader().read(file);
    }

    public Document getDocument() {
        return document;
    }

    /**
     * 通过 XPATH 表达式在文档中查询 Element 列表
     */
    public List<Element> selectElements(String xpath) {
        return this.document.selectNodes(xpath).stream()
            .filter(node -> node instanceof Element)
            .map(node -> (Element) node)
            .collect(Collectors.toList());
    }

    /**
     * 通过 XPATH 表达式在文档中查询符合表达式的第一个 Element
     */
    public Element selectSingleElement(String xpath) {
        return this.document.selectNodes(xpath).stream()
            .filter(node -> node instanceof Element)
            .map(node -> (Element) node)
            .findFirst().orElse(null);
    }

    /**
     * 在指定元素中修改子元素内容，如果不存在则自动添加子元素
     *
     * @param parent       父元素
     * @param childName    子元素名称
     * @param childContent 子元素的（新）内容
     */
    public void addOrReplaceChildElement(Element parent, String childName, String childContent) {
        List<Node> nodes = parent.content();
        int found = -1;

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (Objects.equals(node.getName(), childName)) {
                found = i;
                break;
            }
        }

        Element element = element(childName, childContent);

        if (found > -1) {
            nodes.remove(found);
            nodes.add(found, element);
        } else {
            nodes.add(element);
        }
    }

    public void save() throws IOException {
        save(currentFile);
    }

    public void save(File file) throws IOException {
        if (file != null) {
            Charset charset = Charset.forName(StrUtil.blankToDefault(document.getXMLEncoding(), "UTF-8"));
            Files.write(file.toPath(), document.asXML().getBytes(charset));
        }
    }
}

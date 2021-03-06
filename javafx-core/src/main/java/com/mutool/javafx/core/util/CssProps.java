package com.mutool.javafx.core.util;

import cn.hutool.core.util.StrUtil;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 用于解析和更新 node 的 CSS 样式字符串
 */
public class CssProps {

    private Map<String, String> props = new HashMap<>();

    public CssProps put(String propName, String propValue) {
        if (StrUtil.isNotBlank(propName) && StrUtil.isNotBlank(propValue)) {
            this.props.put(propName, propValue);
        }
        return this;
    }

    public CssProps concat(CssProps cssProps) {
        CssProps result = new CssProps();
        result.props.putAll(this.props);
        result.props.putAll(cssProps.props);
        return result;
    }

    public void applyTo(Node node) {
        CssProps currentCssProps = CssProps.parse(node.getStyle());
        CssProps resultProps = currentCssProps.concat(this);
        node.setStyle(resultProps.toStyleString());
    }

    private String toStyleString() {
        return this.props.entrySet().stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.joining(";"));
    }

    public boolean isEmpty() {
        return this.props.isEmpty();
    }

    private static CssProps parse(String style) {
        CssProps cssProps = new CssProps();
        if (StrUtil.isNotBlank(style)) {
            Stream.of(style.split(";"))
                .filter(StrUtil::isNotBlank)
                .forEach(cssInstr -> {
                    String[] split = cssInstr.split(":");
                    cssProps.put(split[0].trim(), split[1].trim());
                });
        }
        return cssProps;
    }
}

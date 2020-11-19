package com.mutool.javafx.core.util.javafx;

import cn.hutool.core.collection.CollectionUtil;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.Map;

/**
 * 描述：html页面工具类<br>
 * 作者：les<br>
 * 日期：2020/11/19 14:02<br>
 */
public class HtmlPageUtil {

    /**
     * 创建webview并添加对象到页面
     * @param url 本地html路径
     * @param members 要放到页面的java对象map
     * @return WebView
     */
    public static WebView createWebView(String url, Map<String, Object> members){
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        System.out.println("文件绝对路径："+HtmlPageUtil.class.getResource(url).toExternalForm());
        webEngine.load(HtmlPageUtil.class.getResource(url).toExternalForm());
        if(CollectionUtil.isEmpty(members)){
            return browser;
        }
        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        members.forEach((k, v) -> {
                            win.setMember(k, v);
                        });
                    }
                });
        return browser;
    }

}

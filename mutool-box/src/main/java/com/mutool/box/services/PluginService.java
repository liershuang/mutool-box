package com.mutool.box.services;

import cn.hutool.json.JSONUtil;
import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.plugin.PluginManager;
import com.mutool.javafx.core.util.javafx.HtmlPageUtil;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/11/21 22:39<br>
 */
@Slf4j
@Service
public class PluginService {

    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private IndexService indexService;

    /**
     * 插件数据初始化加载
     */
    @PostConstruct
    public void initService(){
        //添加jar到系统中
        pluginManager.addJarByLibs();
        //加载本地插件信息到内存
        pluginManager.loadLocalPlugins();
        //加载远程插件信息到内存
        pluginManager.loadServerPlugins();
    }

    public void testnew(){
        Map<String, Object> data = new HashMap<>();
        data.put("pageService", this);
        indexService.openLocalWebView("测试", "/static/testpage.html", "", data);
    }

    /**
     * 打开插件管理页面
     */
    public void openPluginManagerPage(){
        Map<String, Object> data = new HashMap<>();
        data.put("pageService", this);
        indexService.openLocalWebView("插件管理", "/static/html/pluginManage.html", "", data);
    }

    public List<PluginJarInfo> getPluginList(){
        return pluginManager.getPluginList();
    }

    /**
     * 打开新插件列表
     * @param menuId
     */
    public void openPluginMenu(String menuId){
        try{
            PluginJarInfo pluginInfo = pluginManager.getPluginInfo(menuId);
            //todo 加载jar包时将jar对应的service类获取并加入
            Map<String, Object> data = new HashMap<>();
            WebView browser = HtmlPageUtil.createWebView(pluginInfo.getPagePath(), data);

            Tab tab = new Tab(pluginInfo.getTitle());
            tab.setContent(browser);
            indexService.addTab(tab);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 下载插件并添加插件菜单
     * @param pluginJarName 插件jar名称
     * @throws Exception
     */
    public void downloadPlugin(String pluginJarName) throws IOException {
        File file = pluginManager.downloadPlugin(pluginJarName);
        indexService.addMenu(file);
    }

    /**
     * 更新插件
     * @param pluginJarName 插件jar名称
     * @throws IOException
     */
    public void updatePlugin(String pluginJarName) throws IOException {
        pluginManager.downloadPlugin(pluginJarName);
    }

    public void deletePlugin(String pluginJarName){
        PluginJarInfo plugin = pluginManager.getPlugin(pluginJarName);
        if (plugin == null) {
            throw new IllegalStateException("没有找到插件 " + pluginJarName);
        }
        String jarFilePath = "libs/"+pluginJarName + "-" + plugin.getVersion() + ".jar";
        File pluginFile = new File(jarFilePath);
        indexService.removeMenu(pluginFile);
        pluginManager.deletePlugin(pluginJarName);
    }


    public List<PluginJarInfo> searchPlugin(String keyword) {
        return pluginManager.getPluginList().stream()
                .filter(i -> i.getName().toLowerCase().contains(keyword) || i.getSynopsis().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }


}

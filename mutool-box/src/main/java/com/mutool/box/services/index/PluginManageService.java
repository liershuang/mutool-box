package com.mutool.box.services.index;

import cn.hutool.json.JSONUtil;
import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.plugin.PluginManager;
import com.mutool.box.services.IndexService;
import com.mutool.javafx.core.util.javafx.HtmlPageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

/**
 * @ClassName: PluginManageService
 * @Description: 插件管理
 * @author: xufeng
 * @date: 2020/1/19 17:41
 */

@Slf4j
//@Service
public class PluginManageService {

    @Getter
    private ObservableList<Map<String, String>> originPluginData = FXCollections.observableArrayList();
    @Getter
    private FilteredList<Map<String, String>> pluginDataTableData = new FilteredList<>(originPluginData, m -> true);
    @Autowired
    private IndexService indexService;

    private PluginManager pluginManager = PluginManager.getInstance();

    @PostConstruct
    private void loadService(){
        System.out.println("加载 PluginManageService ******************");
    }

    /**
     * 远程插件信息加载到内存并组织表格信息
     */
    public void initPluginList() {
        //加载远程插件信息到内存，点击下载时将内存插件信息保存到本地
        pluginManager.loadServerPlugins();
        //插件信息组装页面表格信息
        pluginManager.getPluginList().forEach(this::addDataRow);
    }

    public String getPluginList(){
        return JSONUtil.toJsonStr(pluginManager.getPluginList());
    }

    public void openNewPluginPage(){
        try{
            Map<String, Object> data = new HashMap<>();
            WebView browser = HtmlPageUtil.createWebView("/static/plugin.html", data);

            Tab tab = new Tab("插件页面");
            tab.setContent(browser);
            indexService.getTabPaneMain().getTabs().add(tab);
            indexService.getTabPaneMain().getSelectionModel().select(tab);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 组织表格信息
     * @param plugin
     */
    private void addDataRow(PluginJarInfo plugin) {
        Map<String, String> dataRow = new HashMap<>();
        dataRow.put("nameTableColumn", plugin.getName());
        dataRow.put("synopsisTableColumn", plugin.getSynopsis());
        dataRow.put("versionTableColumn", plugin.getVersion());
        dataRow.put("jarName", plugin.getJarName());
        dataRow.put("downloadUrl", plugin.getDownloadUrl());
        dataRow.put("versionNumber", String.valueOf(plugin.getVersionNumber()));

        if (plugin.getIsDownload() == null || !plugin.getIsDownload()) {
            dataRow.put("isDownloadTableColumn", "下载");
            dataRow.put("isEnableTableColumn", "false");
        } else {
            if (plugin.getLocalVersionNumber() != null &&
                plugin.getVersionNumber() > plugin.getLocalVersionNumber()) {
                dataRow.put("isDownloadTableColumn", "更新");
            } else {
                dataRow.put("isDownloadTableColumn", "已下载");
            }
            dataRow.put("isEnableTableColumn", plugin.getIsEnable().toString());
        }

        originPluginData.add(dataRow);
    }

    /**
     * 下载插件
     * @param dataRow
     * @throws Exception
     */
    @Deprecated
    public void downloadPluginJar(Map<String, String> dataRow) throws Exception {
        PluginJarInfo pluginJarInfo = new PluginJarInfo();
        pluginJarInfo.setName(dataRow.get("nameTableColumn"));
        pluginJarInfo.setSynopsis(dataRow.get("synopsisTableColumn"));
        pluginJarInfo.setVersion(dataRow.get("versionTableColumn"));
        pluginJarInfo.setVersionNumber(Integer.parseInt(dataRow.get("versionNumber")));
        pluginJarInfo.setDownloadUrl(dataRow.get("downloadUrl"));
        pluginJarInfo.setJarName(dataRow.get("jarName"));
        pluginJarInfo.setIsDownload(true);
        pluginJarInfo.setIsEnable(true);

        File file = pluginManager.downloadPlugin(pluginJarInfo);
//        indexService.addToolMenu(file);
        indexService.addMenu(file);
    }

    /**
     * 设置是否启用
     * @param index
     */
    public void setIsEnableTableColumn(Integer index) {
        Map<String, String> dataRow = originPluginData.get(index);
        String jarName = dataRow.get("jarName");
        PluginJarInfo pluginJarInfo = this.pluginManager.getPlugin(jarName);
        if (pluginJarInfo != null) {
            pluginJarInfo.setIsEnable(Boolean.parseBoolean(dataRow.get("isEnableTableColumn")));
        }
    }

    /**
     * 插件搜索
     * @param keyword
     */
    public void searchPlugin(String keyword) {
        pluginDataTableData.setPredicate(map -> {
            if (StringUtils.isBlank(keyword)) {
                return true;
            } else {
                return isPluginDataMatch(map, keyword);
            }
        });
    }

    private boolean isPluginDataMatch(Map<String, String> map, String keyword) {
        return map.entrySet().stream().anyMatch(
            entry ->
                !entry.getKey().equals("downloadUrl") &&
                    entry.getValue().toLowerCase().contains(keyword.toLowerCase())
        );
    }

}

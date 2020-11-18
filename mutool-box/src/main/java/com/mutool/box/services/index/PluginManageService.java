package com.mutool.box.services.index;

import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.plugin.PluginManager;
import com.mutool.box.services.IndexService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class PluginManageService {

    @Getter
    private ObservableList<Map<String, String>> originPluginData = FXCollections.observableArrayList();
    @Getter
    private FilteredList<Map<String, String>> pluginDataTableData = new FilteredList<>(originPluginData, m -> true);
    @Autowired
    private IndexService indexService;

    private PluginManager pluginManager = PluginManager.getInstance();


    public void initPluginList() {
        pluginManager.loadServerPlugins();
        pluginManager.getPluginList().forEach(this::addDataRow);
    }

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
        indexService.addToolMenu(file);
    }

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

    /**
     * 判断插件是否启用
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isPluginEnabled(String fileName) {
        String jarName = substringBeforeLast(fileName, "-");
        PluginJarInfo pluginJarInfo = PluginManager.getInstance().getPlugin(jarName);
        if (pluginJarInfo == null) {
            return false;
        }
        Boolean isEnable = pluginJarInfo.getIsEnable();
        return isEnable != null && isEnable;
    }

}

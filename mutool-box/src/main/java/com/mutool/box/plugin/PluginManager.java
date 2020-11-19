package com.mutool.box.plugin;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.mutool.box.constant.UrlConstant;
import com.mutool.box.model.PluginJarInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
public class PluginManager {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String localPluginsPath = UrlConstant.LOCAL_PLUGINS_PATH;
    /** 插件列表 */
    private final List<PluginJarInfo> pluginList = new ArrayList<>();

    public static PluginManager getInstance() {
        return new PluginManager();
    }

    public PluginManager() {
        //加载本地配置文件信息到内存
        loadLocalPlugins();
    }

    /**
     * 获取插件列表
     * @return
     */
    public List<PluginJarInfo> getPluginList() {
        return Collections.unmodifiableList(this.pluginList);
    }

    /**
     * 通过插件名获取插件完整信息
     * @param jarName
     * @return
     */
    public PluginJarInfo getPlugin(String jarName) {
        return this.pluginList.stream()
            .filter(plugin -> Objects.equals(plugin.getJarName(), jarName))
            .findFirst().orElse(null);
    }

    /**
     * 加载本地配置文件中插件信息列表到内存
     */
    public void loadLocalPlugins() {
        try {
            Path path = Paths.get(this.localPluginsPath);
            if (!Files.exists(path)) {
                return;
            }
            String json = new String(Files.readAllBytes(path), DEFAULT_CHARSET);
            JSON.parseArray(json, PluginJarInfo.class).forEach(plugin -> {
                //如果不存在则添加，否则更新本地版本号、是否下载、是否启用标志
                this.addOrUpdatePlugin(plugin, exist -> {
                    exist.setLocalVersionNumber(plugin.getLocalVersionNumber());
                    exist.setIsDownload(plugin.getIsDownload());
                    exist.setIsEnable(plugin.getIsEnable());
                });
            });
        } catch (IOException e) {
            log.error("读取插件配置失败", e);
        }
    }

    /**
     * 下载或更新服务起插件列表信息
     */
    public void loadServerPlugins() {
        try {
            String json = HttpUtil.get(UrlConstant.SERVER_PLUGINS_URL);
            JSON.parseArray(json, PluginJarInfo.class).forEach(plugin -> {
                //如果不存在则添加，否则更新远程名称、简介、版本、下载地址
                this.addOrUpdatePlugin(plugin, exist -> {
                    exist.setName(plugin.getName());
                    exist.setSynopsis(plugin.getSynopsis());
                    exist.setVersion(plugin.getVersion());
                    exist.setVersionNumber(plugin.getVersionNumber());
                    exist.setDownloadUrl(plugin.getDownloadUrl());
                });
            });
        } catch (Exception e) {
            log.error("下载插件列表失败", e);
        }
    }

    /**
     * 异步下载或更新服务器插件列表信息，便于界面上展示 loading 动画
     * @return
     */
    public CompletableFuture<Void> loadServerPluginsAsync() {
        return CompletableFuture.runAsync(this::loadServerPlugins);
    }

    /**
     * 新增或更新插件信息到内存
     * @param pluginJarInfo 插件信息
     * @param ifExists 插件信息处理逻辑
     */
    private void addOrUpdatePlugin(PluginJarInfo pluginJarInfo,  Consumer<PluginJarInfo> ifExists) {
        PluginJarInfo exists = getPlugin(pluginJarInfo.getJarName());
        if (exists == null) {
            this.pluginList.add(pluginJarInfo);
        } else {
            ifExists.accept(exists);
        }
    }

    ////////////////////////////////////////////////////////////// 下载插件

    /**
     * 下载插件到本地
     * @param pluginJarInfo
     * @return
     * @throws IOException
     */
    public File downloadPlugin(PluginJarInfo pluginJarInfo) throws IOException {
        PluginJarInfo plugin = getPlugin(pluginJarInfo.getJarName());
        if (plugin == null) {
            throw new IllegalStateException("没有找到插件 " + pluginJarInfo.getJarName());
        }

        File file = new File("libs/", pluginJarInfo.getJarName() + "-" + pluginJarInfo.getVersion() + ".jar");
        HttpUtil.downloadFile(pluginJarInfo.getDownloadUrl(), file);

        plugin.setIsDownload(true);
        plugin.setIsEnable(true);
        plugin.setLocalVersionNumber(plugin.getVersionNumber());
        this.saveToFile();

        return file;
    }

    /**
     * 保存插件配置到本地文件
     * @throws IOException
     */
    public void saveToFile() throws IOException {
        String json = JSON.toJSONString(this.pluginList, true);
        Files.write(Paths.get(this.localPluginsPath), json.getBytes(DEFAULT_CHARSET));
    }
}

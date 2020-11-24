package com.mutool.box.controller;

import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.services.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * 描述：插件管理<br>
 * 作者：les<br>
 * 日期：2020/11/24 13:14<br>
 */
@Controller
@RequestMapping("plugin")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    @ResponseBody
    @PostMapping("getPluginList")
    public List<PluginJarInfo> getPluginList(){
        return pluginService.getPluginList();
    }

    @ResponseBody
    @PostMapping("searchPlugin")
    public List<PluginJarInfo> searchPlugin(String keyword) {
        return pluginService.searchPlugin(keyword);
    }

    @ResponseBody
    @PostMapping("downloadPlugin")
    public void downloadPlugin(String jarName) throws IOException {
        pluginService.downloadPlugin(jarName);
    }

    @ResponseBody
    @PostMapping("updatePlugin")
    public void updatePlugin(String jarName) throws IOException {
        pluginService.updatePlugin(jarName);
    }

    @ResponseBody
    @PostMapping("deletePlugin")
    public void deletePlugin(String jarName) {
        pluginService.deletePlugin(jarName);
    }




}

package com.mutool.box.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.mutool.box.constant.UrlConstant;
import com.mutool.box.services.IndexService;
import com.mutool.box.services.PluginService;
import com.mutool.box.services.index.SystemSettingService;
import com.mutool.box.utils.Config;
import com.mutool.box.view.IndexView;
import com.mutool.javafx.core.util.ConfigureUtil;
import com.mutool.javafx.core.util.HttpClientUtil;
import com.mutool.javafx.core.util.javafx.AlertUtil;
import com.mutool.javafx.core.util.javafx.JavaFxSystemUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.mutool.box.utils.Config.Keys.NotepadEnabled;

/**
 * @ClassName: IndexController
 * @Description: 主页
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
@Slf4j
@FXMLController
public class JavafxIndexController extends IndexView {

    @Autowired
    private IndexService indexService;
    @Autowired
    private PluginService pluginService;

    private ContextMenu contextMenu = new ContextMenu();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();

        //加载记事本页面
        initNotepad();
        this.indexService.addWebView("交流吐槽", UrlConstant.FEEDBACK_URL, null);
        //加载插件管理页面（html实现）
        pluginService.openPluginManagerPage();
    }


    private void initNotepad() {
        if (Config.getBoolean(NotepadEnabled, true)) {
            addNodepadAction(null);
        }
    }

    /**
     * 初始化页面（菜单）
     */
    private void initView() {
        //传递给service使用
        indexService.setTabPaneMain(tabPaneMain);
        indexService.setSingleWindowBootCheckBox(singleWindowBootCheckBox);

        indexService.addMenu("moreToolsMenu", moreToolsMenu);
        // 获取所有的.jar和.zip文件
        List<File> jarFiles = pluginService.getPluginJarFileList();
        if(CollectionUtil.isEmpty(jarFiles)){
            return;
        }
        jarFiles.forEach(i -> indexService.addMenu(i));
    }

    /**
     * 初始化事件（菜单搜索）
     */
    private void initEvent() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> selectAction(newValue));
        searchButton.setOnAction(arg0 -> {
            selectAction(searchTextField.getText());
        });
    }

    private void initService() {
    }


    public void selectAction(String selectText) {
        if (contextMenu.isShowing()) {
            contextMenu.hide();
        }
        contextMenu = indexService.getSelectContextMenu(selectText);
        contextMenu.show(searchTextField, null, 0, searchTextField.getHeight());
    }

    @FXML
    private void exitAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void closeAllTabAction(ActionEvent event) {
        tabPaneMain.getTabs().clear();
    }

    @FXML
    private void openAllTabAction(ActionEvent event) {
        for (MenuItem value : indexService.getJavafxMenuItemMap().values()) {
            value.fire();
        }
    }

    @FXML
    private void addNodepadAction(ActionEvent event) {
        indexService.addNodepadAction(event);
    }

    @FXML
    private void addLogConsoleAction(ActionEvent event) {
        indexService.addLogConsoleAction(event);
    }

    @FXML
    /** 点击插件管理打开插件列表页面 */
    private void pluginManageAction() {
        pluginService.openPluginManagerPage();
    }

    @FXML
    private void SettingAction() {
        SystemSettingService.openSystemSettings("设置");
    }

    @FXML
    private void aboutAction(ActionEvent event) throws Exception {
        String aboutText = "欢迎使用JavaFx工具集合。\\ngit地址：https://github.com/liershuang/mutool-view\\n作者：小木\\n博客：https://liershuang.gitee.io\\n欢迎前来提出意见，一起完善该工具，谢谢！！\\n当前版本：";
        AlertUtil.showInfoAlert(aboutText + Config.xJavaFxToolVersions);
    }

    @FXML
    private void setLanguageAction(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        indexService.setLanguageAction(menuItem.getText());
    }

    @FXML
    private void openLogFileAction() {
        String filePath = System.getProperty("user.home")+"/mutool/logs/logFile." + DateFormatUtils.format(new Date(), "yyyy-MM-dd") + ".log";
        JavaFxSystemUtil.openDirectory(filePath);
    }

    @FXML
    private void openLogFolderAction() {
        JavaFxSystemUtil.openDirectory(System.getProperty("user.home")+"/mutool/logs/");
    }

    @FXML
    private void openConfigFolderAction() {
        JavaFxSystemUtil.openDirectory(ConfigureUtil.getConfigurePath());
    }

    @FXML
    private void openPluginFolderAction() {
        JavaFxSystemUtil.openDirectory(pluginService.getLocalPluginJarDir());
    }

    @FXML
    private void xwintopLinkOnAction() throws Exception {
        HttpClientUtil.openBrowseURLThrowsException("https://github.com/liershuang/mutool-view");
    }

    @FXML
    private void userSupportAction() throws Exception {
        HttpClientUtil.openBrowseURLThrowsException("https://support.qq.com/product/291829");
    }
}

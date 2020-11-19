package com.mutool.box.controller;

import com.mutool.box.constant.UrlConstant;
import com.mutool.box.controller.index.PluginManageController;
import com.mutool.box.services.IndexService;
import com.mutool.box.services.index.PluginManageService;
import com.mutool.box.services.index.SystemSettingService;
import com.mutool.box.utils.Config;
import com.mutool.box.view.IndexView;
import com.mutool.javafx.core.util.ConfigureUtil;
import com.mutool.javafx.core.util.HttpClientUtil;
import com.mutool.javafx.core.util.javafx.AlertUtil;
import com.mutool.javafx.core.util.javafx.JavaFxSystemUtil;
import com.mutool.javafx.core.util.javafx.JavaFxViewUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
public class IndexController extends IndexView {

    @Autowired
    private IndexService indexService;

    private ContextMenu contextMenu = new ContextMenu();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        indexService.setBundle(resources);

        this.bundle = resources;
        initView();
        initEvent();
        initService();

        //加载记事本页面
        initNotepad();
        this.indexService.addWebView("交流吐槽", UrlConstant.FEEDBACK_URL, null);
        this.tongjiWebView.getEngine().load(UrlConstant.STATISTICS_URL);
        //加载插件管理页面
        initPluginManager();
    }

    private void initNotepad() {
        if (Config.getBoolean(NotepadEnabled, true)) {
            addNodepadAction(null);
        }
    }

    private void initPluginManager() {
        indexService.addContent("插件管理", PluginManageController.FXML, "", "");
    }

    /**
     * 初始化页面（菜单）
     */
    private void initView() {
        //传递给service使用
        indexService.setTabPaneMain(tabPaneMain);
        indexService.setSingleWindowBootCheckBox(singleWindowBootCheckBox);

        indexService.addMenu("toolsMenu", toolsMenu);
        indexService.addMenu("moreToolsMenu", moreToolsMenu);
        File libPath = new File("libs/");
        // 获取所有的.jar和.zip文件
        File[] jarFiles = libPath.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return;
        }
        for (File jarFile : jarFiles) {
            if (!PluginManageService.isPluginEnabled(jarFile.getName())) {
                continue;
            }
            try {
                indexService.addToolMenu(jarFile);
            } catch (Exception e) {
                log.error("加载工具出错：", e);
            }
        }
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
        for (MenuItem value : indexService.getMenuItemMap().values()) {
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
    private void pluginManageAction() throws Exception {
        FXMLLoader fXMLLoader = PluginManageController.getFXMLLoader();
        Parent root = fXMLLoader.load();
        String pluginManage = bundle.getString("plugin_manage");
        JavaFxViewUtil.openNewWindow(pluginManage, root);
    }

    @FXML
    private void SettingAction() {
        SystemSettingService.openSystemSettings(bundle.getString("Setting"));
    }

    @FXML
    private void aboutAction(ActionEvent event) throws Exception {
        AlertUtil.showInfoAlert(bundle.getString("aboutText") + Config.xJavaFxToolVersions);
    }

    @FXML
    private void setLanguageAction(ActionEvent event) throws Exception {
        MenuItem menuItem = (MenuItem) event.getSource();
        indexService.setLanguageAction(menuItem.getText());
    }

    @FXML
    private void openLogFileAction() {
        String filePath = "logs/logFile." + DateFormatUtils.format(new Date(), "yyyy-MM-dd") + ".log";
        JavaFxSystemUtil.openDirectory(filePath);
    }

    @FXML
    private void openLogFolderAction() {
        JavaFxSystemUtil.openDirectory("logs/");
    }

    @FXML
    private void openConfigFolderAction() {
        JavaFxSystemUtil.openDirectory(ConfigureUtil.getConfigurePath());
    }

    @FXML
    private void openPluginFolderAction() {
        JavaFxSystemUtil.openDirectory("libs/");
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

package com.mutool.box.plugin;

import cn.hutool.core.util.StrUtil;
import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.utils.Config;
import com.mutool.javafx.core.util.javafx.JavaFxViewUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.util.ResourceBundle;

@Slf4j
public class PluginLoader {

    /**
     * 添加插件作为新窗口展示
     * @param plugin
     */
    public static void loadPluginAsWindow(PluginJarInfo plugin) {
        try {
            FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(PluginLoader.class.getResource(plugin.getPagePath()));

            if (StrUtil.isNotEmpty(plugin.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(plugin.getBundleName(), Config.defaultLocale);
                generatingCodeFXMLLoader.setResources(resourceBundle);
            }

            JavaFxViewUtil.getNewStage(plugin.getTitle(), plugin.getIconPath(), generatingCodeFXMLLoader);
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }

    /**
     * 加载插件作为新tab显示
     * @param plugin
     * @param tabPane
     */
    public static void loadPluginAsTab(PluginJarInfo plugin, TabPane tabPane) {
        try {
            FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(PluginLoader.class.getResource(plugin.getPagePath()));

            if (StrUtil.isNotEmpty(plugin.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(plugin.getBundleName(), Config.defaultLocale);
                generatingCodeFXMLLoader.setResources(resourceBundle);
            }

            Tab tab = new Tab(plugin.getTitle());

            if (StrUtil.isNotEmpty(plugin.getIconPath())) {
                ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }

            tab.setContent(generatingCodeFXMLLoader.load());
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            tab.setOnCloseRequest(
                event -> JavaFxViewUtil.setControllerOnCloseRequest(generatingCodeFXMLLoader.getController(), event)
            );
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }

}

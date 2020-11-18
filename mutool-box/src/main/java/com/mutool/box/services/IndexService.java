package com.mutool.box.services;

import com.mutool.box.common.logback.ConsoleLogAppender;
import com.mutool.box.controller.IndexController;
import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.model.ToolFxmlLoaderConfiguration;
import com.mutool.box.plugin.PluginLoader;
import com.mutool.box.utils.Config;
import com.mutool.box.utils.SpringUtil;
import com.mutool.box.utils.XJavaFxSystemUtil;
import com.mutool.javafx.core.javafx.dialog.FxAlerts;
import com.mutool.javafx.core.util.javafx.JavaFxViewUtil;
import de.felixroske.jfxsupport.AbstractFxmlView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.mutool.javafx.core.util.javafx.JavaFxViewUtil.setControllerOnCloseRequest;

@Slf4j
@Setter
@Service
public class IndexService {

    private Map<String, Menu> menuMap = new HashMap<String, Menu>();
    private Map<String, MenuItem> menuItemMap = new HashMap<String, MenuItem>();

    private IndexController indexController;
    private ResourceBundle bundle;

    public IndexService(IndexController indexController) {
        this.indexController = indexController;
    }

    public void setLanguageAction(String languageType) throws Exception {
        if ("简体中文".equals(languageType)) {
            Config.set(Config.Keys.Locale, Locale.SIMPLIFIED_CHINESE);
        } else if ("English".equals(languageType)) {
            Config.set(Config.Keys.Locale, Locale.US);
        }
        FxAlerts.info("", indexController.getBundle().getString("SetLanguageText"));
    }

    public ContextMenu getSelectContextMenu(String selectText) {
        selectText = selectText.toLowerCase();
        ContextMenu contextMenu = new ContextMenu();
        for (MenuItem menuItem : indexController.getMenuItemMap().values()) {
            if (menuItem.getText().toLowerCase().contains(selectText)) {
                MenuItem menu_tab = new MenuItem(menuItem.getText(), menuItem.getGraphic());
                menu_tab.setOnAction(event1 -> {
                    menuItem.fire();
                });
                contextMenu.getItems().add(menu_tab);
            }
        }
        return contextMenu;
    }

    public void addNodepadAction(ActionEvent event) {
        TextArea notepad = new TextArea();
        notepad.setFocusTraversable(true);
        if (indexController.getSingleWindowBootCheckBox().isSelected()) {
            JavaFxViewUtil.getNewStage(indexController.getBundle().getString("addNodepad"), null, notepad);
        } else {
            Tab tab = new Tab(indexController.getBundle().getString("addNodepad"));
            tab.setContent(notepad);
            indexController.getTabPaneMain().getTabs().add(tab);
            if (event != null) {
                indexController.getTabPaneMain().getSelectionModel().select(tab);
            }
        }
    }

    public void addLogConsoleAction(ActionEvent event) {
        TextArea textArea = new TextArea();
        textArea.setFocusTraversable(true);
        ConsoleLogAppender.textAreaList.add(textArea);
        if (indexController.getSingleWindowBootCheckBox().isSelected()) {
            Stage newStage = JavaFxViewUtil.getNewStage(indexController.getBundle().getString("addLogConsole"), null, textArea);
            newStage.setOnCloseRequest(event1 -> {
                ConsoleLogAppender.textAreaList.remove(textArea);
            });
        } else {
            Tab tab = new Tab(indexController.getBundle().getString("addLogConsole"));
            tab.setContent(textArea);
            indexController.getTabPaneMain().getTabs().add(tab);
            if (event != null) {
                indexController.getTabPaneMain().getSelectionModel().select(tab);
            }
            tab.setOnCloseRequest((Event event1) -> {
                ConsoleLogAppender.textAreaList.remove(textArea);
            });
        }
    }

    /**
     * @Title: addContent
     * @Description: 添加Content内容
     */
    public void addContent(String title, String url, String resourceBundleName, String iconPath) {

        PluginJarInfo plugin = new PluginJarInfo();
        plugin.setTitle(title);
        plugin.setFxmlPath(url);
        plugin.setBundleName(resourceBundleName);
        plugin.setIconPath(iconPath);

        if (indexController.getSingleWindowBootCheckBox().isSelected()) {
            PluginLoader.loadPluginAsWindow(plugin);
        } else {
            PluginLoader.loadPluginAsTab(plugin, indexController.getTabPaneMain());
        }
    }

    /**
     * @Title: addContent
     * @Description: 添加Content内容
     */
    public void addContent(String title, String className, String iconPath) {
        try {
//			Class<AbstractFxmlView> viewClass = (Class<AbstractFxmlView>) ClassLoader.getSystemClassLoader().loadClass(className);
            Class<AbstractFxmlView> viewClass = (Class<AbstractFxmlView>) Thread.currentThread().getContextClassLoader().loadClass(className);
            AbstractFxmlView fxmlView = SpringUtil.getBean(viewClass);
            if (indexController.getSingleWindowBootCheckBox().isSelected()) {
//				Main.showView(viewClass, Modality.NONE);
                Stage newStage = JavaFxViewUtil.getNewStage(title, iconPath, fxmlView.getView());
                newStage.setOnCloseRequest((WindowEvent event) -> {
                    setControllerOnCloseRequest(fxmlView.getPresenter(), event);
                });
                return;
            }
            Tab tab = new Tab(title);
            tab.setContent(fxmlView.getView());

            if (org.apache.commons.lang.StringUtils.isNotEmpty(iconPath)) {
                ImageView imageView = new ImageView(new Image(iconPath));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }
            TabPane indexPaneMain = indexController.getTabPaneMain();
            indexPaneMain.getTabs().add(tab);
            indexPaneMain.getSelectionModel().select(tab);
            tab.setOnCloseRequest((Event event) -> {
                setControllerOnCloseRequest(fxmlView.getPresenter(), event);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title: addWebView
     * @Description: 添加WebView视图
     */
    public void addWebView(String title, String url, String iconPath) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        if (url.startsWith("http")) {
            webEngine.load(url);
        } else {
            webEngine.load(IndexController.class.getResource(url).toExternalForm());
        }
        if (indexController.getSingleWindowBootCheckBox().isSelected()) {
            JavaFxViewUtil.getNewStage(title, iconPath, new BorderPane(browser));
            return;
        }
        Tab tab = new Tab(title);
        if (StringUtils.isNotEmpty(iconPath)) {
            ImageView imageView = new ImageView(new Image(iconPath));
            imageView.setFitHeight(18);
            imageView.setFitWidth(18);
            tab.setGraphic(imageView);
        }
        tab.setContent(browser);
        indexController.getTabPaneMain().getTabs().add(tab);
        indexController.getTabPaneMain().getSelectionModel().select(tab);
    }

    public void addToolMenu(File file) throws Exception {
        XJavaFxSystemUtil.addJarClass(file);
        Map<String, ToolFxmlLoaderConfiguration> toolMap = new HashMap<>();
        List<ToolFxmlLoaderConfiguration> toolList = new ArrayList<>();

        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry("config/toolFxmlLoaderConfiguration.xml");
            if (entry == null) {
                return;
            }
            InputStream input = jarFile.getInputStream(entry);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(input);
            Element root = document.getRootElement();
            List<Element> elements = root.elements("ToolFxmlLoaderConfiguration");
            for (Element configurationNode : elements) {
                ToolFxmlLoaderConfiguration toolFxmlLoaderConfiguration = new ToolFxmlLoaderConfiguration();
                List<Attribute> attributes = configurationNode.attributes();
                for (Attribute configuration : attributes) {
                    BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(), configuration.getValue());
                }
                List<Element> childrenList = configurationNode.elements();
                for (Element configuration : childrenList) {
                    BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(), configuration.getStringValue());
                }
                if (org.apache.commons.lang.StringUtils.isEmpty(toolFxmlLoaderConfiguration.getMenuParentId())) {
                    toolFxmlLoaderConfiguration.setMenuParentId("moreToolsMenu");
                }
                if (toolFxmlLoaderConfiguration.getIsMenu()) {
                    if (menuMap.get(toolFxmlLoaderConfiguration.getMenuId()) == null) {
                        toolMap.putIfAbsent(toolFxmlLoaderConfiguration.getMenuId(), toolFxmlLoaderConfiguration);
                    }
                } else {
                    toolList.add(toolFxmlLoaderConfiguration);
                }
            }
        }
        toolList.addAll(toolMap.values());
        this.addMenu(toolList);
    }

    public void addMenu(String menuId, Menu menu){
        menuMap.put(menuId, menu);
    }

    private void addMenu(List<ToolFxmlLoaderConfiguration> toolList) {
        for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
            try {
                if (StringUtils.isEmpty(toolConfig.getResourceBundleName())) {
                    if (StringUtils.isNotEmpty(bundle.getString(toolConfig.getTitle()))) {
                        toolConfig.setTitle(bundle.getString(toolConfig.getTitle()));
                    }
                } else {
                    ResourceBundle resourceBundle = ResourceBundle.getBundle(toolConfig.getResourceBundleName(), Config.defaultLocale);
                    if (org.apache.commons.lang.StringUtils.isNotEmpty(resourceBundle.getString(toolConfig.getTitle()))) {
                        toolConfig.setTitle(resourceBundle.getString(toolConfig.getTitle()));
                    }
                }
            } catch (Exception e) {
                log.error("加载菜单失败", e);
            }
            if (toolConfig.getIsMenu()) {
                Menu menu = new Menu(toolConfig.getTitle());
                if (org.apache.commons.lang.StringUtils.isNotEmpty(toolConfig.getIconPath())) {
                    ImageView imageView = new ImageView(new Image(toolConfig.getIconPath()));
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);
                    menu.setGraphic(imageView);
                }
                menuMap.put(toolConfig.getMenuId(), menu);
            }
        }

        for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
            if (toolConfig.getIsMenu()) {
                menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuMap.get(toolConfig.getMenuId()));
            }
        }

        for (ToolFxmlLoaderConfiguration toolConfig : toolList) {
            if (toolConfig.getIsMenu()) {
                continue;
            }
            MenuItem menuItem = new MenuItem(toolConfig.getTitle());
            if (org.apache.commons.lang.StringUtils.isNotEmpty(toolConfig.getIconPath())) {
                ImageView imageView = new ImageView(new Image(toolConfig.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                menuItem.setGraphic(imageView);
            }
            if ("Node".equals(toolConfig.getControllerType())) {
                menuItem.setOnAction((ActionEvent event) -> {
                    addContent(menuItem.getText(), toolConfig.getUrl(), toolConfig.getResourceBundleName(), toolConfig.getIconPath());
                });
                if (toolConfig.getIsDefaultShow()) {
                    addContent(menuItem.getText(), toolConfig.getUrl(), toolConfig.getResourceBundleName(), toolConfig.getIconPath());
                }
            } else if ("WebView".equals(toolConfig.getControllerType())) {
                menuItem.setOnAction((ActionEvent event) -> {
                    addWebView(menuItem.getText(), toolConfig.getUrl(), toolConfig.getIconPath());
                });
                if (toolConfig.getIsDefaultShow()) {
                    addWebView(menuItem.getText(), toolConfig.getUrl(), toolConfig.getIconPath());
                }
            }
            menuMap.get(toolConfig.getMenuParentId()).getItems().add(menuItem);
            menuItemMap.put(menuItem.getText(), menuItem);
        }
    }
}

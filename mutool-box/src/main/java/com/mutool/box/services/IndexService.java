package com.mutool.box.services;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.mutool.box.common.logback.ConsoleLogAppender;
import com.mutool.box.model.MenuConfig;
import com.mutool.box.model.PluginJarInfo;
import com.mutool.box.plugin.PluginLoader;
import com.mutool.box.utils.Config;
import com.mutool.box.utils.SpringUtil;
import com.mutool.box.utils.XJavaFxSystemUtil;
import com.mutool.javafx.core.javafx.dialog.FxAlerts;
import com.mutool.javafx.core.util.javafx.HtmlPageUtil;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.mutool.javafx.core.util.javafx.JavaFxViewUtil.setControllerOnCloseRequest;

@Slf4j
@Service
public class IndexService {

    private Map<String, Menu> menuMap = new HashMap<String, Menu>();

    /** 页面菜单（子节点功能菜单）map */
    private Map<String, MenuConfig> menuItems = new HashMap<>();
    /** 所有菜单数据 */
    private Map<String, MenuConfig> allMenuMap = new HashMap<>();
    /** javafx菜单map */
    private Map<String, Menu> javafxMenuMap = new HashMap<String, Menu>();
    /** javafx子节点菜单map */
    @Getter
    private Map<String, MenuItem> javafxMenuItemMap = new HashMap<String, MenuItem>();

    @Getter
    @Setter
    /** 首页面板tabPane */
    private TabPane tabPaneMain;
    @Setter
    /** 首页是否新窗口打开复选框 */
    private CheckBox singleWindowBootCheckBox;
    @Getter
    private Map<String, MenuItem> menuItemMap = new HashMap<String, MenuItem>();


    /**
     * 添加首页tab
     * @param tab
     */
    public void addTab(Tab tab){
        tabPaneMain.getTabs().add(tab);
        tabPaneMain.getSelectionModel().select(tab);
    }

    public void setLanguageAction(String languageType) {
        if ("简体中文".equals(languageType)) {
            Config.set(Config.Keys.Locale, Locale.SIMPLIFIED_CHINESE);
        } else if ("English".equals(languageType)) {
            Config.set(Config.Keys.Locale, Locale.US);
        }
        FxAlerts.info("", "语言选择设置成功，重启后生效。");
    }

    public ContextMenu getSelectContextMenu(String selectText) {
        selectText = selectText.toLowerCase();
        ContextMenu contextMenu = new ContextMenu();
        for (MenuItem menuItem : javafxMenuItemMap.values()) {
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

    /**
     * 添加记事本事件
     * @param event
     */
    public void addNodepadAction(ActionEvent event) {
        TextArea notepad = new TextArea();
        notepad.setFocusTraversable(true);
        if (singleWindowBootCheckBox.isSelected()) {
            JavaFxViewUtil.getNewStage("临时记事本", null, notepad);
        } else {
            Tab tab = new Tab("临时记事本");
            tab.setContent(notepad);
            tabPaneMain.getTabs().add(tab);
            if (event != null) {
                tabPaneMain.getSelectionModel().select(tab);
            }
        }
    }

    public void addLogConsoleAction(ActionEvent event) {
        TextArea textArea = new TextArea();
        textArea.setFocusTraversable(true);
        ConsoleLogAppender.textAreaList.add(textArea);
        if (singleWindowBootCheckBox.isSelected()) {
            Stage newStage = JavaFxViewUtil.getNewStage("日志控制台", null, textArea);
            newStage.setOnCloseRequest(event1 -> {
                ConsoleLogAppender.textAreaList.remove(textArea);
            });
        } else {
            Tab tab = new Tab("日志控制台");
            tab.setContent(textArea);
            tabPaneMain.getTabs().add(tab);
            if (event != null) {
                tabPaneMain.getSelectionModel().select(tab);
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
    public void openPluginMenuByUrl(String title, String url, String iconPath) {
        PluginJarInfo plugin = new PluginJarInfo();
        plugin.setTitle(title);
        plugin.setPagePath(url);
        plugin.setIconPath(iconPath);

        if (singleWindowBootCheckBox.isSelected()) {
            PluginLoader.loadPluginAsWindow(plugin);
        } else {
            PluginLoader.loadPluginAsTab(plugin, tabPaneMain);
        }
    }

    /**
     * @Title: addContent
     * @Description: 添加Content内容
     */
    public void openPluginMenuByViewClass(String title, String viewClassName, String iconPath) {
        try {
//			Class<AbstractFxmlView> viewClass = (Class<AbstractFxmlView>) ClassLoader.getSystemClassLoader().loadClass(className);
            Class<AbstractFxmlView> viewClass = (Class<AbstractFxmlView>) Thread.currentThread()
                    .getContextClassLoader().loadClass(viewClassName);
            AbstractFxmlView fxmlView = SpringUtil.getBean(viewClass);
            if (singleWindowBootCheckBox.isSelected()) {
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
            tabPaneMain.getTabs().add(tab);
            tabPaneMain.getSelectionModel().select(tab);
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
            webEngine.load(this.getClass().getResource(url).toExternalForm());
        }
        if (singleWindowBootCheckBox.isSelected()) {
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
        addTab(tab);
    }

    /**
     * 打开远程网页地址
     * @param title tab标题
     * @param webUrl 网络url
     * @param iconPath 图标路径
     */
    public void openRemoteWebView(String title, String webUrl, String iconPath){
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load(webUrl);

        if (singleWindowBootCheckBox.isSelected()) {
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
        addTab(tab);
    }

    /**
     * 打开本地页面
     * @param title tab页标题
     * @param localPagePath 本地页面地址
     * @param iconPath 图标路径
     * @param pageData 页面服务对象
     */
    public void openLocalWebView(String title, String localPagePath, String iconPath, Map<String, Object> pageData){
        WebView browser = HtmlPageUtil.createWebView(localPagePath, pageData);
        if (singleWindowBootCheckBox.isSelected()) {
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
        addTab(tab);
    }

    /**
     * 解析jar包添加菜单
     * @param file
     * @throws Exception
     */
    public void addMenu(File file) {
        //加载jar到系统
        XJavaFxSystemUtil.addJarClass(file);

        List<MenuConfig> menuList = analyseMenuList(file);
        menuList.forEach(i -> addMenu(i));
    }

    public void addMenu(String menuId, Menu menu){
        javafxMenuMap.put(menuId, menu);
    }

    public void removeMenu(File file){
        List<MenuConfig> menuList = analyseMenuList(file);
        menuList.forEach(i -> removeMenu(i));
    }

    /**
     * 解析jar文件中配置的菜单数据
     * @param file
     * @return
     */
    private List<MenuConfig> analyseMenuList(File file){
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry("config/toolFxmlLoaderConfiguration.xml");
            if (entry == null) {
                return ListUtil.empty();
            }
            InputStream input = jarFile.getInputStream(entry);

            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(input);
            Element root = document.getRootElement();
            List<Element> elements = root.elements("menuTree");

            //todo 菜单id相同处理办法，已存在的不重复添加
            List<MenuConfig> resultMenuList = new ArrayList<>();
            for (Element menuTreeNode : elements) {
                MenuConfig menuConfig = new MenuConfig();
                List<Element> childNodeList = menuTreeNode.elements();
                Element childMenuNode = null;
                for (Element childNode : childNodeList) {
                    if("childMenu".equals(childNode.getName())){
                        childMenuNode = childNode;
                    }else{
                        BeanUtils.copyProperty(menuConfig, childNode.getName(), childNode.getStringValue());
                    }
                }
                if(childMenuNode != null){
                    setChildMenu(menuConfig, childMenuNode);
                }
                resultMenuList.add(menuConfig);
            }
            return resultMenuList;
        }catch(Exception e){
            log.error("解析jar包菜单配置错误，jar文件地址：{}", file.getAbsolutePath());
            e.printStackTrace();
        }
        return ListUtil.empty();
    }

    /**
     * 设置子菜单
     * @param menuConfig
     * @param childNode childMenu节点
     */
    private void setChildMenu(MenuConfig menuConfig, Element childNode) {
        List<MenuConfig> childMenuList = new ArrayList<>();
        for(Element childMenuTreeNode : childNode.elements("menuTree")){
            MenuConfig childMenu = new MenuConfig();
            List<Element> childMenuTreeEleList = childMenuTreeNode.elements();

            Element childMenuNode = null;
            //先获取当前菜单属性
            for(Element childMenuTreeEle : childMenuTreeEleList){
                if("childMenu".equals(childMenuTreeEle.getName())){
                    childMenuNode = childMenuTreeEle;
                }else{
                    try {
                        BeanUtils.copyProperty(childMenu, childMenuTreeEle.getName(), childMenuTreeEle.getStringValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //有子节点的话递归设置子节点
            if(childMenuNode != null){
                setChildMenu(childMenu, childMenuNode);
            }
            childMenu.setParentMenuId(menuConfig.getMenuId());
            childMenuList.add(childMenu);
        }
        menuConfig.setChildMenuList(childMenuList);
    }


    /*public void addToolMenu(File file) throws Exception {
        //加载jar到系统
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
                //无归属时添加插件菜单到更多工具菜单下
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
    }*/

    /*public void addMenu(String menuId, Menu menu){
        menuMap.put(menuId, menu);
    }*/


    private void addMenu(MenuConfig menuConfig){
        allMenuMap.put(menuConfig.getMenuId(), menuConfig);

        //如果是菜单则设置菜单menu，否者设置menuItem
        if(CollectionUtil.isNotEmpty(menuConfig.getChildMenuList())){
            Menu menu = new Menu(menuConfig.getMenuName());
            if (StrUtil.isNotBlank(menuConfig.getIconPath())) {
                ImageView imageView = new ImageView(new Image(menuConfig.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                menu.setGraphic(imageView);
            }
            javafxMenuMap.put(menuConfig.getMenuId(), menu);
            //将菜单添加到父菜单下
            javafxMenuMap.get(menuConfig.getParentMenuId()).getItems().add(javafxMenuMap.get(menuConfig.getMenuId()));

            for(MenuConfig childMenu : menuConfig.getChildMenuList()){
                addMenu(childMenu);
            }
        }else{
            MenuItem menuItem = new MenuItem(menuConfig.getMenuName());
            if (StrUtil.isNotBlank(menuConfig.getIconPath())) {
                ImageView imageView = new ImageView(new Image(menuConfig.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                menuItem.setGraphic(imageView);
            }
            if ("Node".equals(menuConfig.getMenuType())) {
                //设置菜单点击事件：菜单点击打开页面
                menuItem.setOnAction((ActionEvent event) -> {
                    openPluginMenuByUrl(menuItem.getText(), menuConfig.getUrl(), menuConfig.getIconPath());
                });
                //是否默认展示
                if (menuConfig.getIsDefaultShow()) {
                    openPluginMenuByUrl(menuItem.getText(), menuConfig.getUrl(), menuConfig.getIconPath());
                }
            } else if ("WebView".equals(menuConfig.getMenuType())) {
                menuItem.setOnAction((ActionEvent event) -> {
                    addWebView(menuItem.getText(), menuConfig.getUrl(), menuConfig.getIconPath());
                });
                if (menuConfig.getIsDefaultShow()) {
                    addWebView(menuItem.getText(), menuConfig.getUrl(), menuConfig.getIconPath());
                }
            }
            //将menuItem添加到父菜单下
            javafxMenuMap.get(menuConfig.getParentMenuId()).getItems().add(menuItem);
            javafxMenuItemMap.put(menuItem.getText(), menuItem);

            menuItems.put(menuConfig.getMenuId(), menuConfig);
        }
    }

    private void removeMenu(MenuConfig menuConfig){
        allMenuMap.remove(menuConfig.getMenuId());

        if(CollectionUtil.isNotEmpty(menuConfig.getChildMenuList())){
            for(MenuConfig childMenu : menuConfig.getChildMenuList()){
                removeMenu(childMenu);
            }
        }else{
            javafxMenuItemMap.remove(menuConfig.getMenuName());
            menuItems.remove(menuConfig.getMenuId(), menuConfig);
        }
        //将菜单从父菜单下删除
        javafxMenuMap.get(menuConfig.getParentMenuId()).getItems().remove(javafxMenuMap.get(menuConfig.getMenuId()));
    }

    /**
     * 添加菜单，为菜单添加响应事件，设置菜单logo
     * @param toolList
     */
    /*private void addMenu(List<ToolFxmlLoaderConfiguration> toolList) {
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
                //菜单点击打开页面
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
    }*/

}

package com.mutool.box.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.mutool.javafx.core.util.javafx.JavaFxViewUtil.setControllerOnCloseRequest;

@Slf4j
@Service
public class IndexService {

    /** 页面菜单（叶子节点功能菜单）map */
    @Getter
    private Map<String, MenuConfig> menuItemMap = new HashMap<>();
    /** 所有菜单数据 */
    private Map<String, MenuConfig> allMenuMap = new HashMap<>();
    /** javafx菜单map */
    private Map<String, Menu> javafxMenuMap = new HashMap<>();
    /** javafx子节点菜单map */
    @Getter
    private Map<String, MenuItem> javafxMenuItemMap = new HashMap<>();

    @Getter
    @Setter
    /** 首页面板tabPane */
    private TabPane tabPaneMain;
    @Setter
    /** 首页是否新窗口打开复选框 */
    private CheckBox singleWindowBootCheckBox;


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

            if (StrUtil.isNotEmpty(iconPath)) {
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
        if (StrUtil.isNotEmpty(iconPath)) {
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
        if (StrUtil.isNotEmpty(iconPath)) {
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
        if (StrUtil.isNotEmpty(iconPath)) {
            ImageView imageView = new ImageView(new Image(iconPath));
            imageView.setFitHeight(18);
            imageView.setFitWidth(18);
            tab.setGraphic(imageView);
        }
        tab.setContent(browser);
        addTab(tab);
    }

    /**
     * 获取树形结构菜单
     * @return
     */
    public List<MenuConfig> getMenuTree(){
        ArrayList<MenuConfig> allMenuTree = new ArrayList<>(allMenuMap.values());
        //平铺去重
        Map<String, MenuConfig> distinctMenuMap = new HashMap<>();
        explanMenu(allMenuTree, distinctMenuMap);
        //重新组织为树形结构
        List<MenuConfig> topLevelMenu = distinctMenuMap.values().stream()
                .filter(i -> "moreToolsMenu".equals(i.getParentMenuId())).collect(Collectors.toList());
        setChildMenu(topLevelMenu, distinctMenuMap);
        return topLevelMenu;
    }

    /**
     * 平铺去重菜单为list
     * @param menuConfigList
     * @param distinctMenuMap
     */
    private void explanMenu(List<MenuConfig> menuConfigList, Map<String, MenuConfig> distinctMenuMap){
        for(MenuConfig menuConfig : menuConfigList){
            List<MenuConfig> childMenuList = menuConfig.getChildMenuList();
            if(CollectionUtil.isNotEmpty(childMenuList)){
                explanMenu(childMenuList, distinctMenuMap);
                //子节点不为空的话解析完子节点后设置子节点为空
                menuConfig.setChildMenuList(Collections.EMPTY_LIST);
            }
            //不判断是否已存在，因上级菜单可能重复，直接覆盖即可
            distinctMenuMap.put(menuConfig.getMenuId(), menuConfig);
        }
    }

    /**
     * 递归设置子菜单
     * @param menuConfigList
     * @param distinctMenuMap
     */
    private void setChildMenu(List<MenuConfig> menuConfigList, Map<String, MenuConfig> distinctMenuMap){
        if(CollectionUtil.isEmpty(menuConfigList)){
            return;
        }
        menuConfigList.forEach(i -> {
            List<MenuConfig> childList = distinctMenuMap.values().stream()
                    .filter(menu -> menu.getParentMenuId().equals(i.getMenuId())).collect(Collectors.toList());
            i.setChildMenuList(childList);
            setChildMenu(childList, distinctMenuMap);
        });
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
        if(!FileUtil.exist(file)){
            return;
        }
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
                        BeanUtil.setFieldValue(menuConfig, childNode.getName(), childNode.getStringValue());
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
                    BeanUtil.setFieldValue(childMenu, childMenuTreeEle.getName(), childMenuTreeEle.getStringValue());
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


    private void addMenu(MenuConfig menuConfig){
        allMenuMap.put(menuConfig.getMenuId(), menuConfig);

        //如果是菜单则设置菜单menu，否者设置menuItem
        if(CollectionUtil.isNotEmpty(menuConfig.getChildMenuList())){
            boolean isExist = false;
            Menu menu = javafxMenuMap.get(menuConfig.getMenuId());
            if(menu == null){
                menu = new Menu(menuConfig.getMenuName());
            }else{
                isExist = true;
            }

            if(!isExist){
                if (StrUtil.isNotBlank(menuConfig.getIconPath())) {
                    ImageView imageView = new ImageView(new Image(menuConfig.getIconPath()));
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);
                    menu.setGraphic(imageView);
                }

                javafxMenuMap.put(menuConfig.getMenuId(), menu);
                //将菜单添加到父菜单下
                javafxMenuMap.get(menuConfig.getParentMenuId()).getItems().add(javafxMenuMap.get(menuConfig.getMenuId()));
            }
            //若菜单已存在则递归子菜单添加到现有菜单中
            for(MenuConfig childMenu : menuConfig.getChildMenuList()){
                addMenu(childMenu);
            }
        }else{
            //叶子节点菜单已存在不再添加
            if(javafxMenuItemMap.get(menuConfig.getMenuId()) != null){
                return;
            }
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
            javafxMenuItemMap.put(menuConfig.getMenuId(), menuItem);

            menuItemMap.put(menuConfig.getMenuId(), menuConfig);
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
            menuItemMap.remove(menuConfig.getMenuId(), menuConfig);
        }
        //将菜单从父菜单下删除
        javafxMenuMap.get(menuConfig.getParentMenuId()).getItems().remove(javafxMenuMap.get(menuConfig.getMenuId()));
    }

}

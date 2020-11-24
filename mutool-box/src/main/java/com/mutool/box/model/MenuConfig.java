package com.mutool.box.model;

import lombok.Data;

import java.util.List;

/**
 * 菜单配置，对应插件中config/toolFxmlLoaderConfiguration.xml文件内容
 */
@Data
public class MenuConfig {

	private String menuId;

	private String menuName;
	/** 父节点菜单，默认插件列表菜单 */
	private String parentMenuId = "moreToolsMenu";
	/** 资源url，fxml文件地址或网页地址 */
	private String url;
	/** 菜单图标路径 */
	private String iconPath;
	/** 是否默认展示到tab页 */
	private Boolean isDefaultShow;
	/** 菜单类型，Node:本地fxml页面，WebView：html页面 */
	private String menuType;

	private List<MenuConfig> childMenuList;

}

package com.mutool.box.controller.index;

import com.mutool.box.plugin.PluginManager;
import com.mutool.box.services.index.PluginManageService;
import com.mutool.box.view.index.PluginManageView;
import com.mutool.javafx.core.util.javafx.JavaFxViewUtil;
import com.mutool.javafx.core.util.javafx.TooltipUtil;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName: PluginManageController
 * @Description: 插件管理
 * @author: xufeng
 * @date: 2020/1/19 17:41
 */

@Slf4j
@Controller
public class PluginManageController extends PluginManageView {

    public static final String FXML = "/fxmlView/index/PluginManage.fxml";

    //    @Autowired //todo 注入失败，研究原因
    private PluginManageService pluginManageService = new PluginManageService();

    public static FXMLLoader getFXMLLoader() {
        return new FXMLLoader(PluginManageController.class.getResource(FXML));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        FilteredList<Map<String, String>> pluginDataTableData = pluginManageService.getPluginDataTableData();

        JavaFxViewUtil.setTableColumnMapValueFactory(nameTableColumn, "nameTableColumn");
        JavaFxViewUtil.setTableColumnMapValueFactory(synopsisTableColumn, "synopsisTableColumn");
        JavaFxViewUtil.setTableColumnMapValueFactory(versionTableColumn, "versionTableColumn");
        JavaFxViewUtil.setTableColumnMapValueFactory(isDownloadTableColumn, "isDownloadTableColumn");
        JavaFxViewUtil.setTableColumnMapAsCheckBoxValueFactory(isEnableTableColumn, "isEnableTableColumn",
            (mouseEvent, index) -> {
                pluginManageService.setIsEnableTableColumn(index);
            });

        // TODO 实现插件的启用禁用

        downloadTableColumn.setCellFactory(
            new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>() {
                @Override
                public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param) {
                    return new TableCell<Map<String, String>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            this.setText(null);
                            this.setGraphic(null);
                            if (empty) {
                                return;
                            }
                            Map<String, String> dataRow = pluginDataTableData.get(this.getIndex());
                            Button downloadButton = new Button(dataRow.get("isDownloadTableColumn"));
                            if ("已下载".equals(dataRow.get("isDownloadTableColumn"))) {
                                downloadButton.setDisable(true);
                            }
                            this.setContentDisplay(ContentDisplay.CENTER);
                            downloadButton.setOnMouseClicked((me) -> {
                                try {
                                    pluginManageService.downloadPluginJar(dataRow);
                                    dataRow.put("isEnableTableColumn", "true");
                                    dataRow.put("isDownloadTableColumn", "已下载");
                                    downloadButton.setText("已下载");
                                    downloadButton.setDisable(true);
                                    pluginDataTableView.refresh();
                                    TooltipUtil.showToast("插件 " + dataRow.get("nameTableColumn") + " 下载完成");
                                } catch (Exception e) {
                                    log.error("下载插件失败：", e);
                                    TooltipUtil.showToast("下载插件失败：" + e.getMessage());
                                }
                            });
                            this.setGraphic(downloadButton);
                        }
                    };
                }
            });

        pluginDataTableView.setItems(pluginDataTableData);
    }

    private void initEvent() {
        // 插件菜单右键选择保存配置执行，将内存插件配置保存到本地
        MenuItem mnuSavePluginConfig = new MenuItem("保存配置");
        mnuSavePluginConfig.setOnAction(ev -> {
            try {
                PluginManager.getInstance().saveToFile();
                TooltipUtil.showToast("保存配置成功");
            } catch (Exception ex) {
                log.error("保存插件配置失败", ex);
            }
        });

        ContextMenu contextMenu = new ContextMenu(mnuSavePluginConfig);
        pluginDataTableView.setContextMenu(contextMenu);
        // 搜索
        selectPluginTextField.textProperty().addListener((_ob, _old, _new) -> {
            pluginManageService.searchPlugin(_new);
        });
    }

    private void initService() {
        pluginManageService.initPluginList();
    }

    @FXML
    private void selectPluginAction(ActionEvent event) {
        pluginManageService.searchPlugin(selectPluginTextField.getText());
    }
}
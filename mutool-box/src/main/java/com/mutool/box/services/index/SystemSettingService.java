package com.mutool.box.services.index;

import com.mutool.box.controller.index.SystemSettingController;
import com.mutool.javafx.core.javafx.FxApp;
import com.mutool.javafx.core.javafx.dialog.FxDialog;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: SystemSettingService
 * @Description: 设置页面
 * @author: xufeng
 * @date: 2020/2/25 0025 16:44
 */

@Getter
@Setter
@Slf4j
public class SystemSettingService {

    public static void openSystemSettings(String title) {

        FxDialog<SystemSettingController> dialog = new FxDialog<SystemSettingController>()
            .setTitle(title)
            .setBodyFxml("/fxmlView/index/SystemSetting.fxml")
            .setOwner(FxApp.primaryStage)
            .setButtonTypes(ButtonType.OK, ButtonType.CANCEL);

        SystemSettingController controller = dialog.show();

        dialog
            .setButtonHandler(ButtonType.OK, (actionEvent, stage) -> {
                controller.applySettings();
                stage.close();
            })
            .setButtonHandler(ButtonType.CANCEL, (actionEvent, stage) -> stage.close());
    }
}

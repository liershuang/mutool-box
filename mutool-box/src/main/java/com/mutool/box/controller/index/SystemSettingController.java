package com.mutool.box.controller.index;

import com.mutool.box.utils.Config;
import com.mutool.box.utils.Config.Keys;
import com.mutool.box.view.index.SystemSettingView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName: SystemSettingController
 * @Description: 设置页面
 * @author: xufeng
 * @date: 2020/2/25 0025 16:44
 */

@Getter
@Setter
@Slf4j
@Controller
public class SystemSettingController extends SystemSettingView {

    private Stage newStage = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
    }

    private void initView() {
        try {
            exitShowAlertCheckBox.setSelected(Config.getBoolean(Keys.ConfirmExit, true));
            addNotepadCheckBox.setSelected(Config.getBoolean(Keys.NotepadEnabled, true));
            saveStageBoundCheckBox.setSelected(Config.getBoolean(Keys.RememberWindowLocation, true));
            chkNewLauncher.setSelected(Config.getBoolean(Keys.NewLauncher, false));
        } catch (Exception e) {
            log.error("加载配置失败：", e);
        }
    }

    public void applySettings() {
        try {
            Config.set(Keys.ConfirmExit, exitShowAlertCheckBox.isSelected());
            Config.set(Keys.NotepadEnabled, addNotepadCheckBox.isSelected());
            Config.set(Keys.RememberWindowLocation, saveStageBoundCheckBox.isSelected());
            Config.set(Keys.NewLauncher, chkNewLauncher.isSelected());

            if (newStage != null) {
                newStage.close();
            }
        } catch (Exception e) {
            log.error("保存配置失败：", e);
        }
    }
}

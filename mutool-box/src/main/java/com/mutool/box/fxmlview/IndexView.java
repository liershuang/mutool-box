package com.mutool.box.fxmlview;

import com.jfoenix.controls.JFXDecorator;
import com.mutool.box.utils.Config;
import com.mutool.javafx.core.javafx.dialog.FxAlerts;
import com.mutool.javafx.core.util.javafx.JavaFxViewUtil;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.annotation.Scope;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @ClassName: IndexView
 * @Description:
 * @author: xufeng
 * @date: 2017/11/22 17:38
 */
@Scope("prototype")
@FXMLView(value = "/fxmlView/Index.fxml", bundle = "locale.Menu")
public class IndexView extends AbstractFxmlView {
    public IndexView() throws Exception {
        //反射修改默认语言
        ResourceBundle bundle = ResourceBundle.getBundle(this.getResourceBundle().get().getBaseBundleName(), Config.defaultLocale);
        FieldUtils.writeField(this,"bundle",Optional.ofNullable(bundle),true);
        //修改标题国际化
        GUIState.getStage().setTitle(bundle.getString("Title"));
    }

    @Override
    public Parent getView() {
        Stage stage = GUIState.getStage();
        JFXDecorator decorator = JavaFxViewUtil.getJFXDecorator(stage,
                stage.getTitle() + Config.xJavaFxToolVersions,"/images/icon.jpg",super.getView());
        decorator.setOnCloseButtonAction(() -> {
            if (FxAlerts.confirmOkCancel("提示", "确定要退出吗？")) {
                System.exit(0);
            }
        });
        return decorator;
    }
}

package com.mutool.box;

import com.mutool.box.fxmlview.IndexView;
import com.mutool.box.utils.StageUtils;
import com.mutool.box.utils.XJavaFxSystemUtil;
import com.mutool.javafx.core.exception.BizException;
import com.mutool.javafx.core.exception.ErrorCodeEnum;
import com.mutool.javafx.core.javafx.dialog.FxAlerts;
import com.mutool.javafx.core.util.javafx.JavaFxViewUtil;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.GUIState;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @ClassName: Main
 * @Description: 启动类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:34:11
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"com.mutool.box"})
public class Main extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        try{
            //初始化本地语言
            XJavaFxSystemUtil.initSystemLocal();
            //启动图设置
            SplashScreen splashScreen =  new SplashScreen() {
                @Override
                public String getImagePath() {
                    return "/static/images/start_page.jpg";
                }
            };
            launch(Main.class, IndexView.class, splashScreen, args);
        }catch (Throwable cause){
            log.error("启动异常，异常信息：{}", cause);
            throw new BizException(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getErrorCode(), "启动异常", cause);
        }
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        super.beforeInitialView(stage, ctx);
        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(stage, "", null, new AnchorPane());
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            if (FxAlerts.confirmOkCancel("提示", "确定要退出吗？")) {
                System.exit(0);
            } else {
                event.consume();
            }
        });
        GUIState.setScene(scene);
        Platform.runLater(() -> {
            StageUtils.updateStageStyle(GUIState.getStage());
        });
    }

    @Override
    public void beforeShowingSplash(Stage splashStage) {
        //添加bootstrapfx样式支持
//        splashStage.getScene().getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
    }

}

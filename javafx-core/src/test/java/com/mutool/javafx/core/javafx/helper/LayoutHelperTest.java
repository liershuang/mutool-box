package com.mutool.javafx.core.javafx.helper;

import com.mutool.javafx.core.javafx.wrapper.BorderWrapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LayoutHelperTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderWrapper blueBorder = BorderWrapper.of("#44AAFF", BorderWrapper.BorderStyle.solid, 2);

        primaryStage.setScene(new Scene(new BorderPane(
            blueBorder.wrap(new Label("Hello!"))
        )));

        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
    }
}
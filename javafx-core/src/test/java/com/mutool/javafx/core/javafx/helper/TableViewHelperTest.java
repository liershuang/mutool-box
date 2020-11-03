package com.mutool.javafx.core.javafx.helper;

import com.mutool.javafx.core.User;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class TableViewHelperTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<User> tableView = new TableView<>();

        TableViewHelper.of(tableView)
            .addStrColumn("编号", user -> String.valueOf(user.getId()))
            .addStrColumn("姓名", user -> String.valueOf(user.getName()))
            .addStrColumn("身高", user -> String.valueOf(user.getLength()))
            .addStrColumn("上次登录时间", user -> String.valueOf(user.getLastLogin()))
        ;

        tableView.getItems().addAll(User.createUsers());

        primaryStage.setScene(new Scene(LayoutHelper.vbox(30, 0, Pos.CENTER, tableView)));
        primaryStage.show();
    }
}
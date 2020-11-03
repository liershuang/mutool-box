package com.mutool.javafx.core.util.javafx;

import com.mutool.javafx.core.XCoreException;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;

public class FxmlUtil {

    public static FXMLLoader loadFxmlFromResource(String resourcePath) {
        return loadFxmlFromResource(resourcePath, null);
    }

    public static FXMLLoader loadFxmlFromResource(String resourcePath, ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(FxmlUtil.class.getResource(resourcePath));
            fxmlLoader.setResources(resourceBundle);
            fxmlLoader.load();
            return fxmlLoader;
        } catch (IOException e) {
            throw new XCoreException(e);
        }
    }
}

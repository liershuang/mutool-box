package com.mutool.javafx.core.util;

import java.io.File;

public class ConfigureUtil {
    public static String getConfigurePath() {
        return System.getProperty("user.home") + "/mutool/configure/";
    }

    public static String getConfigurePath(String fileName) {
        return getConfigurePath() + fileName;
    }

    public static File getConfigureFile(String fileName) {
        return new File(getConfigurePath(fileName));
    }

    public static String getPluginPath(){
        return System.getProperty("user.home") + "/mutool/plugin/";
    }
}

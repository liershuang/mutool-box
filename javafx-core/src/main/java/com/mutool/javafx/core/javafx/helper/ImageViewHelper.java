package com.mutool.javafx.core.javafx.helper;

import static com.mutool.javafx.core.javafx.helper.ImageHelper.image;

import javafx.scene.image.ImageView;

public class ImageViewHelper {

    public static ImageView imageView(String path, double size) {
        return imageView(path, size, size);
    }

    public static ImageView imageView(String path, double width, double height) {
        ImageView imageView = new ImageView(image(path));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

}

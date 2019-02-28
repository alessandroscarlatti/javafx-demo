package com.scarlatti.javafxdemo;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 2/14/2019
 */
public class JavaFxUtils {

    public static Image imageFromResource(String fullResourcePath) {
        return new Image(JavaFxUtils.class.getResourceAsStream(fullResourcePath));
    }

    public static List<Image> scaledImages(InputStream inputStream) {
        return Arrays.asList(
            new Image(inputStream, 127, 127, true, true),
            new Image(inputStream, 63, 63, true, true),
            new Image(inputStream, 31, 31, true, true),
            new Image(inputStream, 15, 15, true, true)
        );
    }
}

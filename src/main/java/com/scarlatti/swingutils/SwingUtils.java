package com.scarlatti.swingutils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 */
public class SwingUtils {

    public static void display(Container uiComponent) {
        display(() -> uiComponent);
    }

    public static void display(Supplier<Container> uiComponentSupplier) {
        // create a new frame
        JFrame frame = new JFrame("Demo");


        // add the ui component to the frame
        Container uiComponent = uiComponentSupplier.get();
        frame.setContentPane(uiComponent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.revalidate();
        frame.setVisible(true);

        CountDownLatch latch = new CountDownLatch(1);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error awaiting window close.", e);
        }
    }

    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException("Error setting system look and feel", e);
        }
    }

    public static Icon createScaledIconFromResource(String resourcePath, IconScale iconScale) {
        try {
            Image image = ImageIO.read(SwingUtils.class.getResourceAsStream(resourcePath));
            Image scaledImage = image.getScaledInstance(iconScale.size, iconScale.size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            throw new RuntimeException("Error reading image from resource " + resourcePath);
        }
    }

    public static Icon createScaledIcon(String base64ImageString, IconScale iconScale) {
        Image image = createImageFromBase64String(base64ImageString);
        Image scaledImage = image.getScaledInstance(iconScale.size, iconScale.size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static Map<IconScale, Image> createScaledImages(String base64ImageString) {
        Image image = createImageFromBase64String(base64ImageString);
        Map<IconScale, Image> imagesMap = new HashMap<>();

        imagesMap.put(IconScale.ICON_SCALE_127, image.getScaledInstance(127, 127, Image.SCALE_SMOOTH));
        imagesMap.put(IconScale.ICON_SCALE_63, image.getScaledInstance(63, 63, Image.SCALE_SMOOTH));
        imagesMap.put(IconScale.ICON_SCALE_31, image.getScaledInstance(31, 31, Image.SCALE_SMOOTH));
        imagesMap.put(IconScale.ICON_SCALE_15, image.getScaledInstance(15, 15, Image.SCALE_SMOOTH));

        return imagesMap;
    }

    public static Image createImageFromBase64String(String base64ImageString) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes;
        bytes = decoder.decode(base64ImageString);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            return ImageIO.read(bis);
        } catch (Exception e) {
            throw new RuntimeException("Error reading image.", e);
        }
    }

    public enum IconScale {
        ICON_SCALE_15(15),
        ICON_SCALE_31(31),
        ICON_SCALE_63(63),
        ICON_SCALE_127(127);

        final int size;

        IconScale(int size) {
            this.size = size;
        }
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Font makeBold(Font font) {
        return font.deriveFont(Font.BOLD, font.getSize());
    }
}

package com.scarlatti.swingutils;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 */
public class SwingUtilsTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void displayJPanel() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.setBackground(Color.BLUE);
            return jPanel;
        });
    }

    @Test
    public void displayJButton() {
        SwingUtils.display(() -> {
            JButton jButton = new JButton();
            jButton.setText("stuff");
            return jButton;
        });
    }
}

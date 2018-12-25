package com.scarlatti.swingutils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 */
public class TestUtils {

    public static JPanel redJPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.RED);
        jPanel.setBorder(new LineBorder(Color.BLACK, 2));
        return jPanel;
    }

    public static JPanel blueJPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.BLUE);
        jPanel.setBorder(new LineBorder(Color.BLACK, 2));
        return jPanel;
    }
}

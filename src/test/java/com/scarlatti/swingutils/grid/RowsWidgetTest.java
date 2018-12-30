package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.filechooser.FileWidget;
import com.scarlatti.swingutils.filechooser.FileChoosers;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 */
public class RowsWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void createSeveralRows() {
        SwingUtils.display(() -> {

            FileWidget fileWidget1 = FileChoosers.openFileWidget();
            FileWidget fileWidget2 = FileChoosers.saveFileWidget();
            JButton jButton = new JButton("OK");

            RowsWidget rowsWidget = new RowsWidget(_rowsWidget -> {
                _rowsWidget.addRow(fileWidget1.getUi());
                _rowsWidget.addRow(fileWidget2.getUi());
                _rowsWidget.addRow(
                    new MultilineTextWidget("lots of text.").getUi()
                );
                _rowsWidget.addRow(jButton);
            });

            return rowsWidget.getUi();
        });
    }
}

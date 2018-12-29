package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.filechooser.FileChooserWidget;
import com.scarlatti.swingutils.filechooser.FileChoosers;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class ColumnsWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void createSeveralColumns() {
        SwingUtils.display(() -> {

            FileChooserWidget fileChooserWidget1 = FileChoosers.openFileWidget();
            FileChooserWidget fileChooserWidget2 = FileChoosers.saveFileWidget();
            MarginWidget buttonWidget = new MarginWidget(new JButton("OK"));

            ColumnsWidget rowsWidget = new ColumnsWidget(_rowsWidget -> {

//                fileChooserWidget1.getUi().setMinimumSize(new Dimension(500, 200));

                _rowsWidget.addRelativeColumn(fileChooserWidget1.getUi());
                _rowsWidget.addFixedColumn(
                    new MultilineTextWidget("lots of text.").getUi()
                );
                _rowsWidget.addRelativeColumn(fileChooserWidget2.getUi());
                _rowsWidget.addFixedColumn(buttonWidget.getUi());
            });

            return rowsWidget.getUi();
        });
    }
}

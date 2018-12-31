package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.file.FileWidget;
import com.scarlatti.swingutils.file.FileChoosers;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

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

            FileWidget fileWidget1 = FileChoosers.openFileWidget();
            FileWidget fileWidget2 = FileChoosers.saveFileWidget();
            MarginWidget buttonWidget = new MarginWidget(new JButton("OK"));

            ColumnsWidget rowsWidget = new ColumnsWidget(_rowsWidget -> {

//                fileWidget1.getUi().setMinimumSize(new Dimension(500, 200));

                _rowsWidget.addRelativeColumn(fileWidget1.getUi());
                _rowsWidget.addFixedColumn(
                    new MultilineTextWidget("lots of text.").getUi()
                );
                _rowsWidget.addRelativeColumn(fileWidget2.getUi());
                _rowsWidget.addFixedColumn(buttonWidget.getUi());
            });

            return rowsWidget.getUi();
        });
    }
}

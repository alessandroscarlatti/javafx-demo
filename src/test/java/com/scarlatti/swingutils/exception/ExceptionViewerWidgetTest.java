package com.scarlatti.swingutils.exception;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 12/29/2018
 */
public class ExceptionViewerWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }


    @Test
    public void showExceptionViewer() {
        Exception e = new RuntimeException();
        JOptionPane.showMessageDialog(
            null,
            new ExceptionViewerWidget(e).getUi(),
            "Error Getting Default Key Stores",
            JOptionPane.ERROR_MESSAGE);
    }

}
package com.scarlatti.swingutils.exception;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 12/29/2018
 */
public class ExceptionViewerWidget implements Widget {

    private JComponent widgetComponent;
    private Exception exception;

    public ExceptionViewerWidget() {
    }

    public ExceptionViewerWidget(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Container getUi() {
        if (widgetComponent == null)
            initUi();

        return widgetComponent;
    }

    private void initUi() {
        StringBuilder sb = new StringBuilder("");

        if (exception != null) {
            sb.append(exception.getMessage());
            sb.append("\n");
            StringWriter errors = new StringWriter();
            exception.printStackTrace(new PrintWriter(errors));
            sb.append(errors.toString());
        }

        JTextArea jta = new JTextArea(sb.toString());
        widgetComponent = new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };
    }

    public static void displayModal(Exception e) {
        JOptionPane.showMessageDialog(
            null,
            new ExceptionViewerWidget(e).getUi(),
            "Exception Details",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void displayModal(Exception e, Component component) {
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(component),
            new ExceptionViewerWidget(e).getUi(),
            "Exception Details",
            JOptionPane.ERROR_MESSAGE);
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

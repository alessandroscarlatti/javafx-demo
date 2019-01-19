package com.scarlatti.swingutils.text;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/19/2019
 */
public class HtmlTextWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void htmlTextWidgetTest() {
        SwingUtils.display(
            HtmlTextWidget.ui(htmlTextWidget -> {
                htmlTextWidget.setHtml(
                    "Click <a href='shortDescription'>here</a> for a short description. <br />" +
                        "Click <a href='detailedDescription'>here</a> for a detailed description. <br />"
                );
                htmlTextWidget.onClick("shortDescription", widget -> {
                    JOptionPane.showMessageDialog(widget, "short description");
                });
                htmlTextWidget.onClick("detailedDescription", widget -> {
                    JOptionPane.showMessageDialog(widget, "detailedDescription");
                });
            })
        );
    }
}

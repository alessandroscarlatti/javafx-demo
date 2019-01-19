package com.scarlatti.swingutils.text;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.grid.RowsWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

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

    @Test
    public void htmlTextDoesNotExpand() {
        // todo this is not working yet...

        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                JPanel blackJPanel = new JPanel();
                blackJPanel.setBackground(Color.BLACK);
                JPanel blueJPanel = new JPanel();
                blueJPanel.setBackground(Color.BLUE);
                rowsWidget.addRow(blackJPanel);

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BorderLayout());  //give your JPanel a BorderLayout

                Container textContainer = HtmlTextWidget.ui(htmlTextWidget -> {
                    htmlTextWidget.setHtml("stuff stuff stuff");
                });

                JScrollPane scroll = new JScrollPane(textContainer); //place the JTextArea in a scroll pane
                textPanel.add(scroll, BorderLayout.CENTER); //add the JScrollPane to the panel
                // CENTER will use up all available space

                rowsWidget.addRow(textPanel);
                rowsWidget.addRow(blueJPanel);
            })
        );
    }
}

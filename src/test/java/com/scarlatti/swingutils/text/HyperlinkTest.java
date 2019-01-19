package com.scarlatti.swingutils.text;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 1/7/2019
 */
public class HyperlinkTest {


    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void hyperlinkTest() {
        JEditorPane content = new JEditorPane();
        content.setContentType("text/html");
        content.setEditable(false);
        content.setOpaque(false);
        content.setText("<html><h1>Stuff and Things</h1><a style='color: red;' href='http://stackoverflow.com'>Link</a><a href=\"stuff\">Link2</a></html>");
        content.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception e1) {
                        throw new RuntimeException(e1);
                    }
                }
            }
        });

        SwingUtils.display(content);
    }
}

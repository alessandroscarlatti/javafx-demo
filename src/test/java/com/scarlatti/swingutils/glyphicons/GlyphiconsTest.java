package com.scarlatti.swingutils.glyphicons;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.glyphicon.Glyphicons;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class GlyphiconsTest {


    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }


    @Test
    public void showGlyphicons() {

        java.util.List<String> glyphicons = Arrays.asList(
            Glyphicons.Send,
            Glyphicons.Ok,
            Glyphicons.Remove,
            Glyphicons.Time,
            Glyphicons.Refresh
        );

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            for (String glyphicon : glyphicons) {
                JButton label = new JButton(glyphicon);
                label.setFont(Glyphicons.getFont().deriveFont(Font.PLAIN, 48f));
                label.setForeground(Color.decode("#4286f4"));
                jPanel.add(label);
            }

            JButton ok = new JButton(Glyphicons.Ok);
            ok.setFont(Glyphicons.getFont().deriveFont(Font.PLAIN, 48f));
            ok.setForeground(Color.decode("#20e21d"));
            jPanel.add(ok);

            JButton cancel = new JButton(Glyphicons.Remove);
            cancel.setFont(Glyphicons.getFont().deriveFont(Font.PLAIN, 48f));
            cancel.setForeground(Color.decode("#e21d1d"));
            jPanel.add(cancel);

            return jPanel;
        });
    }
}

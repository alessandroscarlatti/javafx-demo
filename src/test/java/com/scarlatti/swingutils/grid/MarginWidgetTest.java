package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.filechooser.FileChooserWidget;
import com.scarlatti.swingutils.filechooser.FileChoosers;
import com.scarlatti.swingutils.layout.RelativeLayout;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class MarginWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void wrapJButton() {
        SwingUtils.display(() -> {

            JPanel jPanel = new JPanel(new RelativeLayout());

            MarginWidget jButton = new MarginWidget(new JButton("what"));

            jPanel.add(jButton.getUi());

            return jPanel;
        });
    }
}

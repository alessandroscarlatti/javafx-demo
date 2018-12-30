package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.layout.RelativeLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

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

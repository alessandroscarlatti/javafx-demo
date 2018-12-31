package com.scarlatti.swingutils.flow;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 12/31/2018
 */
public class FlowWidgetTest {
    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void testFlowWidget() {
        SwingUtils.display(
            FlowWidget.ui()
        );
    }
}
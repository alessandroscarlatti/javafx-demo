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
    public void flowWidget() {
        SwingUtils.display(
            FlowWidget.ui()
        );
    }


    @Test
    public void widgetWithBackAndFinishEnabled() {
        SwingUtils.display(
            FlowWidget.ui(flowWidget -> {
                flowWidget.addStep(
                    FlowStepWidget.step(flowStepWidget -> {
                        flowStepWidget.setCommandName("Do the Thing 1");
                    })
                );
                flowWidget.addStep(
                    FlowStepWidget.step(flowStepWidget -> {
                        flowStepWidget.setCommandName("Do the Thing 2");
                    })
                );
                flowWidget.start();
            })
        );
    }
}
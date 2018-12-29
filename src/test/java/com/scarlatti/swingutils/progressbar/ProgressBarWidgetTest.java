package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class ProgressBarWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void showWaitingProgressBar() {
        SwingUtils.display(() -> {

            ProgressBarWidget widget = new ProgressBarWidget();

            return widget.getUi();
        });
    }

    @Test
    public void progressBarWithReallyLongTask() {

        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
            widget.getProgressBarTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(progressBarWidget::getUi);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

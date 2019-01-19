package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.scarlatti.swingutils.SwingUtils.sleep;

/**
 * @author Alessandro Scarlatti
 * @since Sunday, 1/6/2019
 */
public class TaskTemplateTest {
    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void showTaskProgressBarTemplate() {
        SwingUtils.display(
            TaskProgressBarWidget.ui(taskProgressBarWidget -> {
                taskProgressBarWidget.getTaskTemplate().setWork(() -> {
                    sleep(2000);
                });
                taskProgressBarWidget.setTitle("Task 1");
                taskProgressBarWidget.setMessage("This is a long task.");
            })
        );
    }
}
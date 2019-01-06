package com.scarlatti.swingutils.messaging;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.button.ButtonWidget;
import com.scarlatti.swingutils.button.ButtonWidget.ButtonWidgetApi;
import com.scarlatti.swingutils.button.ButtonWidget.ButtonWidgetEvents;
import com.scarlatti.swingutils.grid.RowsWidget;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import com.scarlatti.swingutils.progressbar.ProgressBarWidgetOld;
import com.scarlatti.swingutils.progressbar.TaskTemplate;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateApi;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateEvents;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.scarlatti.swingutils.SwingUtils.sleep;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 */
public class Example1Attempt2 {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void twoProgressBarTasksOnPage() {

        MessageBus bus = new MessageBus();

        // button
        Topic<ButtonWidgetEvents> buttonEventsTopic = Topic.create("button.events", ButtonWidgetEvents.class);
        Topic<ButtonWidgetApi> buttonApiTopic = Topic.create("button.api", ButtonWidgetApi.class);
        // task 1
        Topic<TaskTemplateApi> task1ApiTopic = Topic.create("task1.api", TaskTemplateApi.class);
        Topic<TaskTemplateEvents> task1EventsTopic = Topic.create("task1.events", TaskTemplateEvents.class);
        // task 2
        Topic<TaskTemplateApi> task2ApiTopic = Topic.create("task2.api", TaskTemplateApi.class);
        Topic<TaskTemplateEvents> task2EventsTopic = Topic.create("task2.events", TaskTemplateEvents.class);

        TaskTemplate task1 = TaskTemplate.task(task -> {
            task.setWork(() -> {
                sleep(3000);
            });
        });

        TaskTemplate task2 = TaskTemplate.task(task -> {
            task.setWork(() -> {
                sleep(3000);
            });
        });

        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    ProgressBarWidgetOld.ui2(progressBarWidget -> {
                        progressBarWidget.setSelfStartable(false);
                        progressBarWidget.setTaskTemplate(task1);
                    })
                );
                rowsWidget.addRow(
                    ProgressBarWidgetOld.ui2(progressBarWidget -> {
                        progressBarWidget.setSelfStartable(false);
                        progressBarWidget.setTaskTemplate(task2);
                    })
                );
                rowsWidget.addRow(
                    ButtonWidget.ui(buttonWidget -> {
                        buttonWidget.setText("Go");
                    })
                );
            })
        );
    }
}

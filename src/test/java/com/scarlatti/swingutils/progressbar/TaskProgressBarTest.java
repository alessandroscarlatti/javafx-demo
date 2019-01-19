package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.grid.RowsWidget;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateEvents;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.scarlatti.swingutils.SwingUtils.sleep;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 1/7/2019
 */
public class TaskProgressBarTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void taskProgressBar() {

        MessageBus messageBus = new MessageBus();
        Topic<TaskTemplateEvents> taskTemplateEventsTopic = Topic.create("taskProgressBarWidget.taskTemplate.events", TaskTemplateEvents.class);

        messageBus.connect().subscribe(taskTemplateEventsTopic, new TaskTemplateEvents() {
            @Override
            public void started() {
                System.out.println("started");
            }

            @Override
            public void completed(Object result) {
                System.out.println("completed");
            }
        });

        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    TaskProgressBarWidget.ui(taskProgressBarWidget -> {
                        taskProgressBarWidget.setRepeatable(true);
                        taskProgressBarWidget.setCancelable(false);
                        taskProgressBarWidget.connectAs("taskProgressBarWidget", messageBus);
                        taskProgressBarWidget.setTaskTemplate(
                            TaskTemplate.task(taskTemplate -> {
                                taskTemplate.setWork(() -> {
                                    System.out.println("start");
                                    sleep(3000);
                                    System.out.println("end");
                                });
                            })
                        );
                    })
                );
            })
        );
    }

    @Test
    public void taskProgressBarModified() {

        MessageBus messageBus = new MessageBus();
        Topic<TaskTemplateEvents> taskTemplateEventsTopic = Topic.create("taskProgressBarWidget.taskTemplate.events", TaskTemplateEvents.class);

        messageBus.connect().subscribe(taskTemplateEventsTopic, new TaskTemplateEvents() {
            @Override
            public void started() {
                System.out.println("started");
            }

            @Override
            public void completed(Object result) {
                System.out.println("completed");
            }
        });

        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    TaskProgressBarWidget.ui(taskProgressBarWidget -> {
                        taskProgressBarWidget.setRepeatable(true);
                        taskProgressBarWidget.setCancelable(true);
                        taskProgressBarWidget.setTitle("Big, Long Task");
                        taskProgressBarWidget.setMessage("Run this big, long task.");
                        taskProgressBarWidget.connectAs("taskProgressBarWidget", messageBus);
                        taskProgressBarWidget.getTaskTemplate().setWork(() -> {
                            System.out.println("start");
                            sleep(3000);
                            System.out.println("end");
                        });
                    })
                );
            })
        );
    }

    @Test
    public void cancelableTaskProgressBar() {
        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    TaskProgressBarWidget.ui(taskProgressBarWidget -> {
                        taskProgressBarWidget.setRepeatable(true);
                        taskProgressBarWidget.setCancelable(true);
                        taskProgressBarWidget.getTaskTemplate().setWork(() -> {
                            System.out.println("start");
                            sleep(3000);
                            System.out.println("end");
                        });
                    })
                );
            })
        );
    }
}

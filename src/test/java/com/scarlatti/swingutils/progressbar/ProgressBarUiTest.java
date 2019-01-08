package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.messaging.MessageBus;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.util.concurrent.Executors;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class ProgressBarUiTest {

    private static MessageBus bus = new MessageBus();

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void showWaitingProgressBar() {
        SwingUtils.display(() -> {

            ProgressBarUi widget = new ProgressBarUi();

            return widget.getUi();
        });
    }

    @Test
    public void progressBarWithShortTask() {

        // the specific runnable or callable will be defined in the
        // business area of the code...
        TaskTemplate taskTemplate = TaskTemplate.task(task -> {
            task.setWork(this::longTask);
            task.connectAs("task", bus);
        });

        SwingUtils.display(
            ProgressBarUi.ui(progressBarUi -> {
                progressBarUi.connectEvents(bus, taskTemplate.getEventsTopic());
                progressBarUi.connectApi(bus, taskTemplate.getApiTopic());
            })
        );
    }

    @Test
    public void brieferProgressBarTask() {
        // the specific runnable or callable will be defined in the
        // business area of the code...
        SwingUtils.display(
            ProgressBarWidgetOld.ui2(progressBarWidget -> {
                progressBarWidget.getTaskTemplate().setWork(() -> {
                    sleep(2000);
                });
            })
        );
    }

    @Test
    public void injectTaskTemplate() {

        TaskTemplate taskTemplate = TaskTemplate.task(task -> {
            task.setWork(() -> {
                sleep(2000);
            });
        });

        bus.connect().subscribe(taskTemplate.getEventsTopic(), new TaskTemplate.TaskTemplateEvents() {
            @Override
            public void started() {
                System.out.println("ProgressBarUiTest.started() ");
            }
        });

        // the specific runnable or callable will be defined in the
        // business area of the code...
        SwingUtils.display(
            ProgressBarWidgetOld.ui2(progressBarWidget -> {
                progressBarWidget.setMessageBus(bus);
                progressBarWidget.setTaskTemplate(taskTemplate);
            })
        );
    }

    private String longTask() {
        sleep(5000);
        return "asdf";
    }

    @Test
    public void progressBarWithLongTask() {
        ProgressBarUi progressBarUi = new ProgressBarUi(() -> {
            sleep(4000);
        });

        SwingUtils.display(progressBarUi::getUi);
    }

    @Test
    public void progressBarWithFailingTask() {
        ProgressBarUi progressBarUi = new ProgressBarUi(() -> {
            sleep(1000);
            throw new RuntimeException("Something failed.");
        });

        SwingUtils.display(progressBarUi::getUi);
    }

    @Test
    public void progressBarWithTaskStartedExternally() {

        ProgressBarWidgetOld progressBarWidget = new ProgressBarWidgetOld();
        progressBarWidget.connect();

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }

    @Test
    public void progressBarWithTaskStartedExternallyCanPickUpReturnValueLater() {

        ProgressBarWidgetOld progressBarWidget = new ProgressBarWidgetOld(widget -> {
            widget.getTaskTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            JButton jButton = new JButton("Retrieve Result");
            jButton.addActionListener((e -> {
                Executors.newFixedThreadPool(1).execute(() -> {
                    System.out.println(progressBarWidget.getTaskTemplate().getResult());
                });
            }));
            jPanel.add(jButton);
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }

    @Test
    public void progressBarWithNonSelfStartableTask() {
        ProgressBarWidgetOld progressBarWidget = new ProgressBarWidgetOld(widget -> {
            widget.setSelfStartable(false);
            widget.getTaskTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }

    @Test
    public void progressBarWithRepeatableTask() {
        ProgressBarWidgetOld progressBarWidget = new ProgressBarWidgetOld(widget -> {
            widget.setSelfStartable(false);
            widget.setRepeatable(true);
            widget.getTaskTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }


    @Test
    public void progressBarWithNonCancelableTask() {
        ProgressBarWidgetOld progressBarWidget = new ProgressBarWidgetOld(widget -> {
            widget.setSelfStartable(true);
            widget.setRepeatable(true);
            widget.setCancelable(false);
            widget.getTaskTemplate().setMaxDurationMs(1000);
            widget.getTaskTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }

    @Test
    public void progressBarWithTitleAndMessage() {
        SwingUtils.display(
            ProgressBarWidgetOld.ui2(progressBarWidget -> {
                progressBarWidget.getTaskTemplate().setWork(() -> {
                    sleep(1000);
                });

                progressBarWidget.setTitle("Task 1");
                progressBarWidget.setMessage("This task should run for 1 second.");
            })
        );
    }

    @Test
    public void progressBarWithJLabel() {
        // does not work!
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            JProgressBar jProgressBar = new JProgressBar();
            jProgressBar.add(new JLabel("stuff and things"));
            jPanel.add(jProgressBar);
            return jPanel;
        });
    }

    private JButton buttonForTriggeringProgressBarTask(ProgressBarWidgetOld progressBarWidget) {
        JButton jButton = new JButton("Execute Task");
        jButton.addActionListener((e -> {
            Executors.newFixedThreadPool(1).execute(() -> {
                progressBarWidget.getTaskTemplate().execute(() -> {
                    sleep(2000);
                });
                System.out.println("done");
            });
        }));

        return jButton;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

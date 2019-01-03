package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.messaging.MessageBus;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class ProgressBarWidgetTest {

    private static MessageBus bus = new MessageBus();

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
    public void progressBarWithShortTask() {

        // the specific runnable or callable will be defined in the
        // business area of the code...
        TaskTemplate taskTemplate = TaskTemplate.task(task -> {
            task.setWork(this::longTask);
            task.connect(bus);
        });

        SwingUtils.display(
            ProgressBarWidget.ui(progressBarWidget -> {
                progressBarWidget.connectEvents(bus, taskTemplate.getEventsTopic());
                progressBarWidget.connectApi(bus, taskTemplate.getApiTopic());
            })
        );
    }

    private String longTask() {
        sleep(5000);
        return "asdf";
    }

    @Test
    public void progressBarWithLongTask() {
        ProgressBarWidget progressBarWidget = new ProgressBarWidget(() -> {
            sleep(4000);
        });

        SwingUtils.display(progressBarWidget::getUi);
    }

    @Test
    public void progressBarWithFailingTask() {
        ProgressBarWidget progressBarWidget = new ProgressBarWidget(() -> {
            sleep(1000);
            throw new RuntimeException("Something failed.");
        });

        SwingUtils.display(progressBarWidget::getUi);
    }

//    @Test
//    public void progressBarWithTaskStartedExternally() {
//
//        ProgressBarWidget progressBarWidget = new ProgressBarWidget();
//
//        SwingUtils.display(() -> {
//            JPanel jPanel = new JPanel();
//            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
//            jPanel.add(progressBarWidget.getUi());
//            return jPanel;
//        });
//    }

//    @Test
//    public void progressBarWithTaskStartedExternallyCanPickUpReturnValueLater() {
//
//        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
//            widget.getProgressBarTemplate().setWork(() -> {
//                sleep(2000);
//                return "asdf";
//            });
//        });
//
//        SwingUtils.display(() -> {
//            JPanel jPanel = new JPanel();
//
//            JButton jButton = new JButton("Retrieve Result");
//            jButton.addActionListener((e -> {
//                Executors.newFixedThreadPool(1).execute(() -> {
//                    System.out.println(progressBarWidget.getProgressBarTemplate().getResult());
//                });
//            }));
//            jPanel.add(jButton);
//            jPanel.add(progressBarWidget.getUi());
//            return jPanel;
//        });
//    }

//    @Test
//    public void progressBarWithNonSelfStartableTask() {
//        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
//            widget.setSelfStartable(false);
//            widget.getProgressBarTemplate().setWork(() -> {
//                sleep(2000);
//                return "asdf";
//            });
//        });
//
//        SwingUtils.display(() -> {
//            JPanel jPanel = new JPanel();
//            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
//            jPanel.add(progressBarWidget.getUi());
//            return jPanel;
//        });
//    }

//    @Test
//    public void progressBarWithRepeatableTask() {
//        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
//            widget.setSelfStartable(false);
//            widget.setRepeatable(true);
//            widget.getProgressBarTemplate().setWork(() -> {
//                sleep(2000);
//                return "asdf";
//            });
//        });
//
//        SwingUtils.display(() -> {
//            JPanel jPanel = new JPanel();
//            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
//            jPanel.add(progressBarWidget.getUi());
//            return jPanel;
//        });
//    }


//    @Test
//    public void progressBarWithNonCancelableTask() {
//        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
//            widget.setSelfStartable(true);
//            widget.setRepeatable(true);
//            widget.setCancelable(false);
//            widget.getProgressBarTemplate().setMaxDurationMs(1000);
//            widget.getProgressBarTemplate().setWork(() -> {
//                sleep(2000);
//                return "asdf";
//            });
//        });
//
//        SwingUtils.display(() -> {
//            JPanel jPanel = new JPanel();
//            jPanel.add(progressBarWidget.getUi());
//            return jPanel;
//        });
//    }

//    @Test
//    public void progressBarWithTitleAndMessage() {
//        SwingUtils.display(
//            ProgressBarWidget.ui(progressBarWidget -> {
//                progressBarWidget.getProgressBarTemplate().setWork(() -> {
//                    sleep(1000);
//                });
//
//                progressBarWidget.setTitle("Task 1");
//                progressBarWidget.setMessage("This task should run for 1 second.");
//            })
//        );
//    }

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

//    private JButton buttonForTriggeringProgressBarTask(ProgressBarWidget progressBarWidget) {
//        JButton jButton = new JButton("Execute Task");
//        jButton.addActionListener((e -> {
//            Executors.newFixedThreadPool(1).execute(() -> {
//                progressBarWidget.getProgressBarTemplate().execute(() -> {
//                    sleep(2000);
//                });
//                System.out.println("done");
//            });
//        }));
//
//        return jButton;
//    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

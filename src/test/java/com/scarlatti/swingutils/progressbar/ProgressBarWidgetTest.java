package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.layout.RelativeLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
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
    public void progressBarWithShortTask() {

        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
            widget.getProgressBarTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(progressBarWidget::getUi);
    }

    @Test
    public void progressBarWithTaskStartedExternally() {

        ProgressBarWidget progressBarWidget = new ProgressBarWidget();

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.add(buttonForTriggeringProgressBarTask(progressBarWidget));
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }

    @Test
    public void progressBarWithTaskStartedExternallyCanPickUpReturnValueLater() {

        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
            widget.getProgressBarTemplate().setWork(() -> {
                sleep(2000);
                return "asdf";
            });
        });

        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            JButton jButton = new JButton("Retrieve Result");
            jButton.addActionListener((e -> {
                Executors.newFixedThreadPool(1).execute(() -> {
                    System.out.println(progressBarWidget.getProgressBarTemplate().getResult());
                });
            }));
            jPanel.add(jButton);
            jPanel.add(progressBarWidget.getUi());
            return jPanel;
        });
    }

    @Test
    public void progressBarWithNonSelfStartableTask() {
        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
            widget.setSelfStartable(false);
            widget.getProgressBarTemplate().setWork(() -> {
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
        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
            widget.setSelfStartable(false);
            widget.setRepeatable(true);
            widget.getProgressBarTemplate().setWork(() -> {
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
        ProgressBarWidget progressBarWidget = new ProgressBarWidget(widget -> {
            widget.setSelfStartable(true);
            widget.setRepeatable(true);
            widget.setCancelable(false);
            widget.getProgressBarTemplate().setMaxDurationMs(1000);
            widget.getProgressBarTemplate().setWork(() -> {
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

    private JButton buttonForTriggeringProgressBarTask(ProgressBarWidget progressBarWidget) {
        JButton jButton = new JButton("Execute Task");
        jButton.addActionListener((e -> {
            Executors.newFixedThreadPool(1).execute(() -> {
                progressBarWidget.getProgressBarTemplate().execute(() -> {
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

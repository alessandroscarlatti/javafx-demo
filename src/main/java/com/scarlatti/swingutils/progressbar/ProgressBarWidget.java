package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.exception.ExceptionViewerWidget;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateCommandsNotifier;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateEventsNotifier;
import com.scarlatti.swingutils.text.MultilineTextWidget;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.scarlatti.swingutils.SwingUtils.makeBold;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class ProgressBarWidget implements Widget {

    private JProgressBar progressBar;
    private ProgressBarUI normalProgressBarUi;
    private ProgressBarUI errorProgressBarUi;
    private ProgressBarUI successProgressBarUi;
    private JPanel widgetPanel;
    private JButton statusButton;
    private TaskTemplateCommandsNotifier taskCommandsNotifier = new TaskTemplateCommandsNotifier() {};
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private boolean selfStartable = true;
    private boolean retryable = true;
    private boolean repeatable = false;
    private boolean cancelable = true;
    private String title;
    private String message;
    private Exception taskException;

    private Future paintTask;
    private long expectedDurationMs = 5000;

    private TaskState taskState = TaskState.PENDING;

    private ActionListener startActionListener = e -> {
        executor.execute(() -> {
            taskCommandsNotifier.start();
        });
    };

    private ActionListener cancelActionListener = e -> {
        executor.execute(() -> {
            taskCommandsNotifier.stop();
        });
    };

    private MouseListener errorActionListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            ExceptionViewerWidget.displayModal(taskException, widgetPanel);
        }
    };

    public ProgressBarWidget() {
    }

    public static Container ui() {
        return new ProgressBarWidget().getUi();
    }

    public ProgressBarWidget(Runnable runnableWork) {
        throw new UnsupportedOperationException();
    }

    public static Container ui(Runnable runnableWork) {
        return new ProgressBarWidget(runnableWork).getUi();
    }

    public ProgressBarWidget(Callable<?> callableWork) {
        throw new UnsupportedOperationException();
    }

    public static Container ui(Callable<?> callableWork) {
        return new ProgressBarWidget(callableWork).getUi();
    }

    public ProgressBarWidget(Consumer<ProgressBarWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<ProgressBarWidget> config) {
        return new ProgressBarWidget(config).getUi();
    }

    public void connect(MessageBus bus, TaskTemplate taskTemplate) {
        createPublisher(bus, taskTemplate.getCommandsTopic());
        subscribeToEvents(bus, taskTemplate.getEventsTopic());
    }

    private void createPublisher(MessageBus bus, Topic<TaskTemplateCommandsNotifier> commandsTopic) {
        taskCommandsNotifier = bus.syncPublisher(commandsTopic);
    }

    private void subscribeToEvents(MessageBus bus, Topic<TaskTemplateEventsNotifier> eventsTopic) {
        bus.connect().subscribe(eventsTopic, new TaskTemplateEventsNotifier() {
            @Override
            public void started() {
                taskState = TaskState.IN_PROGRESS;
                taskException = null;
                SwingUtilities.invokeLater(() -> {
                    paintTask = executor.submit(ProgressBarWidget.this::paintProgressBar);  // start the paint task
                    configureByState();
                });
            }

            @Override
            public void cancelled(Exception e) {
                taskState = TaskState.CANCELLED;
                taskException = e;
                SwingUtilities.invokeLater(() -> {
                    paintTask.cancel(true);
                    setProgress(1f);
                    configureByState();
                });
            }

            @Override
            public void interrupted(Exception e) {
                taskState = TaskState.FAILED;
                taskException = e;
                SwingUtilities.invokeLater(() -> {
                    paintTask.cancel(true);
                    setProgress(1f);
                    configureByState();
                });

            }

            @Override
            public void timedOut(Exception e) {
                taskState = TaskState.TIMEOUT;
                taskException = e;
                SwingUtilities.invokeLater(() -> {
                    paintTask.cancel(true);
                    setProgress(1f);
                    configureByState();
                });
            }

            @Override
            public void error(Exception e) {
                taskState = TaskState.FAILED;
                taskException = e;
                SwingUtilities.invokeLater(() -> {
                    paintTask.cancel(true);
                    setProgress(1f);
                    configureByState();
                });
            }

            @Override
            public void completed(Object result) {
                taskState = TaskState.COMPLETE;
                taskException = null;
                SwingUtilities.invokeLater(() -> {
                    paintTask.cancel(true);
                    setProgress(1f);
                    configureByState();
                });
            }
        });
    }

    /**
     * Set the progress level
     *
     * @param progressPercent percent of progress, 0.0f - 1.0f
     */
    public void setProgress(float progressPercent) {
        progressBar.setValue((int) (progressPercent * 10000));
    }

    @Override
    public Container getUi() {
        if (widgetPanel == null) {
            initUi();
            configureByState();
        }

        return widgetPanel;
    }

    private void initUi() {
        widgetPanel = new JPanel();
        progressBar = new JProgressBar(0, 10000);
        progressBar.setValue(4);

        normalProgressBarUi = progressBar.getUI();
        errorProgressBarUi = new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.RED;
            }

            @Override
            protected Color getSelectionBackground() {
                return Color.RED;
            }


        };
        successProgressBarUi = new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.decode("#4DB146");  // success green
            }

            @Override
            protected Color getSelectionBackground() {
                return Color.decode("#4DB146");  // success green
            }
        };

        statusButton = new JButton();

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(makeBold(titleLabel.getFont()));
        Container messageComponent = MultilineTextWidget.ui(message);

        GroupLayout gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup widgetHGroup = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup widgetVGroup = gl.createSequentialGroup();
        GroupLayout.SequentialGroup progressBarHGroup = gl.createSequentialGroup();
        GroupLayout.ParallelGroup progressBarVGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER);

        if (title != null) {
            widgetHGroup.addComponent(titleLabel);
            widgetVGroup.addComponent(titleLabel);
        }
        if (message != null) {
            widgetHGroup.addComponent(messageComponent);
            widgetVGroup.addComponent(messageComponent);
        }

        progressBarHGroup
            .addComponent(statusButton)
            .addComponent(progressBar);

        progressBarVGroup
            .addComponent(statusButton)
            .addComponent(progressBar);

        widgetHGroup.addGroup(progressBarHGroup);
        widgetVGroup.addGroup(progressBarVGroup);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }

    private void configureByState() {
        statusButton.removeActionListener(startActionListener);
        statusButton.removeActionListener(cancelActionListener);
        progressBar.removeMouseListener(errorActionListener);
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(false);
        progressBar.setString("");
        progressBar.setFont(null);
        progressBar.setForeground(null);  // this is actually the background.
        progressBar.setUI(normalProgressBarUi);
        progressBar.setCursor(Cursor.getDefaultCursor());

        switch (taskState) {
            case PENDING:
                statusButton.setText("Start");
                statusButton.setEnabled(true);
                statusButton.addActionListener(startActionListener);
                statusButton.setVisible(selfStartable);
                break;
            case IN_PROGRESS:
                statusButton.setVisible(true);
                if (cancelable) {
                    statusButton.setText("Cancel");
                    statusButton.setEnabled(true);
                    statusButton.addActionListener(cancelActionListener);
                } else {
                    statusButton.setText("Working...");
                    statusButton.setEnabled(false);
                }
                break;
            case TIMEOUT:
                statusButton.setVisible(true);
                if (retryable) {
                    statusButton.setText("Try Again");
                    statusButton.setEnabled(true);
                    statusButton.addActionListener(startActionListener);
                    progressBar.setStringPainted(true);
                    progressBar.setString("Timed out.");
                    setUnderlineFont(progressBar);
                    progressBar.setUI(errorProgressBarUi);
                    progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));
                    progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    progressBar.addMouseListener(errorActionListener);
                } else {
                    statusButton.setText("Timed out");
                    statusButton.setEnabled(false);
                }
                break;
            case CANCELLED:
                statusButton.setVisible(true);
                if (retryable) {
                    statusButton.setText("Try Again");
                    statusButton.setEnabled(true);
                    statusButton.addActionListener(startActionListener);
                    progressBar.setStringPainted(true);
                    progressBar.setString("Cancelled.");
                    setUnderlineFont(progressBar);
                    progressBar.setUI(errorProgressBarUi);
                    progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));
                    progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    progressBar.addMouseListener(errorActionListener);
                } else {
                    statusButton.setText("Cancelled");
                    statusButton.setEnabled(false);
                }
                break;
            case FAILED:
                statusButton.setVisible(true);
                if (retryable) {
                    statusButton.setText("Try Again");
                    statusButton.setEnabled(true);
                    statusButton.addActionListener(startActionListener);
                    progressBar.setStringPainted(true);
                    progressBar.setString("Failed.");
                    setUnderlineFont(progressBar);
                    progressBar.setUI(errorProgressBarUi);
                    progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));
                    progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    progressBar.addMouseListener(errorActionListener);
                } else {
                    statusButton.setText("Failed");
                    statusButton.setEnabled(false);
                }
                break;
            case COMPLETE:
                progressBar.setStringPainted(true);
                progressBar.setString("Complete.");
                progressBar.setUI(successProgressBarUi);
                progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));

                if (repeatable) {
                    statusButton.setVisible(true);
                    statusButton.setText("Do Again");
                    statusButton.setEnabled(true);
                    statusButton.addActionListener(startActionListener);
                    statusButton.setVisible(true);
                } else {
                    statusButton.setVisible(false);
                }

                break;
        }
    }

    private void setUnderlineFont(JProgressBar progressBar) {
        Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        progressBar.setFont(progressBar.getFont().deriveFont(fontAttributes));
    }

    /**
     * This is the paint thread.
     */
    void paintProgressBar() {
        long durationMs = expectedDurationMs;
        long refreshRateMs = 30;
        float resolution = 1.0f / (float) (durationMs / refreshRateMs);  // need to count from 0 to 1 in durationMs
        float fallbackPercent = 0.4f;  // if we are still going, go back to the fallback percent.

        SwingUtilities.invokeLater(() -> {
            setProgress(0.0f);
        });

        // this is an intentional infinite loop
        for (float progressPercent = 0.0f; ; progressPercent += resolution) {
            final float progressPercentToPaint = progressPercent;
            SwingUtilities.invokeLater(() -> {
                setProgress(progressPercentToPaint);
            });

            try {
                // if this thread is interrupted, an interrupted exception will be thrown here.
                Thread.sleep(refreshRateMs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // determine if we need to fall back and start over
            if (progressPercent >= 1.0f) {
                fallbackPercent = fallbackPercent * 0.7f;  // go back to the fallback
                progressPercent = fallbackPercent;
                durationMs += durationMs;

                // recalculate how much we will bump the progress each iteration
                resolution = 1.0f / (float) (durationMs / refreshRateMs);
            }
        }
    }

    public void setSelfStartable(boolean selfStartable) {
        this.selfStartable = selfStartable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExpectedDurationMs(long expectedDurationMs) {
        this.expectedDurationMs = expectedDurationMs;
    }

    private enum TaskState {
        PENDING,
        IN_PROGRESS,
        TIMEOUT,
        CANCELLED,
        FAILED,
        COMPLETE
    }
}

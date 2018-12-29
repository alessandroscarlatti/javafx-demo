package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.Widget;
import javafx.scene.control.ProgressBar;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.multi.MultiProgressBarUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
    private ProgressBarTemplate progressBarTemplate = new ProgressBarTemplate(this);
    private ExecutorService executor = Executors.newFixedThreadPool(3);
    private GroupLayout gl;

    private boolean selfStartable = true;
    private boolean retryable = true;
    private boolean repeatable = false;
    private boolean cancelable = true;

    private ProgressBarWidgetState state = new ProgressBarWidgetState();

    private ActionListener startActionListener = e -> {
        executor.execute(() -> {
            progressBarTemplate.invokeWorkWithProgressBar();
        });
    };

    private ActionListener cancelActionListener = e -> {
        executor.execute(() -> {
            progressBarTemplate.cancel();
        });
    };

    public ProgressBarWidget() {
    }

    public ProgressBarWidget(Consumer<ProgressBarWidget> config) {
        config.accept(this);
    }

    /**
     * Set the progress level
     *
     * @param progressPercent percent of progress, 0.0f - 1.0f
     */
    public void setProgress(float progressPercent) {
        state.progressPercent = progressPercent;
        progressBar.setValue((int) (progressPercent * 10000));
    }

    public void inProgress() {
        state.taskState = TaskState.IN_PROGRESS;
        configureByState();
    }

    public void complete() {
        state.taskState = TaskState.COMPLETE;
        configureByState();
    }

    public void error() {
        state.taskState = TaskState.FAILED;
        configureByState();
    }

    /**
     * May be called by the cancel button, or by another source.
     */
    public void cancelled() {
        state.taskState = TaskState.CANCELLED;
        configureByState();
    }

    public void timeout() {
        state.taskState = TaskState.TIMEOUT;
        configureByState();
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
        };
        successProgressBarUi = new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.decode("#4DB146");  // success green
            }
        };

//        JPanel progressBarPanel = new JPanel(new BorderLayout());
//        progressBarPanel.add(progressBar, BorderLayout.CENTER);
//        progressBarPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 31));

//        statusIcon = new JLabel();
//        statusIcon.setIcon(SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/clock.png", ICON_SCALE_31));
//
//        cancelButton = new JLabel();
//        cancelButton.setIcon(SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/cancel-icon.png", ICON_SCALE_15));

        statusButton = new JButton();

        gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup widgetHGroup = gl.createSequentialGroup()
            .addComponent(statusButton)
            .addComponent(progressBar);

        GroupLayout.ParallelGroup widgetVGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(statusButton)
            .addComponent(progressBar);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }

    private void configureByState() {
        statusButton.removeActionListener(startActionListener);
        statusButton.removeActionListener(cancelActionListener);
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(false);
        progressBar.setString("");
        progressBar.setForeground(null);  // this is actually the background.
        progressBar.setUI(normalProgressBarUi);

        switch (state.taskState) {
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
                    progressBar.setUI(errorProgressBarUi);
                    progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));
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
                    progressBar.setUI(errorProgressBarUi);
                    progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));
                } else {
                    statusButton.setText("Cancelled");
                    statusButton.setEnabled(false);
                }
                break;
            case FAILED:
                statusButton.setVisible(true);
                if (retryable) {
                    statusButton.setText("Failed. Try Again");
                    statusButton.setEnabled(true);
                    statusButton.addActionListener(startActionListener);
                    progressBar.setStringPainted(true);
                    progressBar.setString("Failed.");
                    progressBar.setUI(errorProgressBarUi);
                    progressBar.setForeground(UIManager.getColor("ProgressBar.selectionForeground"));
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

    public ProgressBarTemplate getProgressBarTemplate() {
        return progressBarTemplate;
    }

    public void setProgressBarTemplate(ProgressBarTemplate progressBarTemplate) {
        this.progressBarTemplate = progressBarTemplate;
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

    private enum TaskState {
        PENDING,
        IN_PROGRESS,
        TIMEOUT,
        CANCELLED,
        FAILED,
        COMPLETE
    }

    private static class ProgressBarWidgetState {
        TaskState taskState = TaskState.PENDING;
        float progressPercent = 0.0f;
    }

    public static class ProgressBarTemplate {

        private ExecutorService executor = Executors.newFixedThreadPool(4);

        private Future workTask;
        private Future paintTask;
        private ProgressBarWidget progressBarWidget;
        private long expectedDurationMs = 5000;
        private long maxDurationMs = 30000;
        private Runnable runnableWork = this::doRunnableWork;
        private Callable<?> callableWork = this::doCallableWork;
        private Object result = null;

        public void setExpectedDurationMs(long expectedDurationMs) {
            this.expectedDurationMs = expectedDurationMs;
        }

        public void setMaxDurationMs(long maxDurationMs) {
            this.maxDurationMs = maxDurationMs;
        }

        public void setWork(Runnable runnableWork) {
            this.runnableWork = runnableWork;
            this.callableWork = null;
        }

        public void setWork(Callable<?> callableWork) {
            this.callableWork = callableWork;
            this.runnableWork = null;
        }

        public ProgressBarTemplate() {
        }

        public ProgressBarTemplate(ProgressBarWidget progressBarWidget) {
            this.progressBarWidget = progressBarWidget;
        }

        public ProgressBarTemplate(Consumer<ProgressBarTemplate> config) {
            config.accept(this);
        }

        void setProgressBarWidget(ProgressBarWidget progressBarWidget) {
            this.progressBarWidget = progressBarWidget;
        }

        public void cancel() {
            paintTask.cancel(true);
            workTask.cancel(true);

            SwingUtilities.invokeLater(() -> {
                progressBarWidget.cancelled();
            });
        }

        void execute(Runnable runnableWork) {
            setWork(runnableWork);
            invokeWorkWithProgressBar();
        }

        @SuppressWarnings("unchecked")
        <T> T execute(Callable<T> callableWork) {
            setWork(callableWork);
            return (T) invokeWorkWithProgressBar();
        }

        /**
         * Blocking call.
         *
         * @return the result of this work, if any.
         * @throws any exceptions thrown by the underlying invocation of the work.
         */
        public Object invokeWorkWithProgressBar() {
            Objects.requireNonNull(progressBarWidget, "Must be attached to a progress bar.");
            try {
                // update the state so we look like we are working!
                progressBarWidget.inProgress();

                // start the paint task
                paintTask = executor.submit(this::paintProgressBar);

                // start the work task, either runnable or callable
                if (runnableWork != null) {
                    workTask = executor.submit(runnableWork);
                } else if (callableWork != null) {
                    workTask = executor.submit(callableWork);
                }

                // wait for the work task to finish
                result = workTask.get(maxDurationMs, TimeUnit.MILLISECONDS);
                onComplete();
                return result;
            } catch (CancellationException e) {
                onCancelled();
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                onInterrupted();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                onError();
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                onTimeout();
                throw new RuntimeException(e);
            }
        }

        void doRunnableWork() {
            // nothing by default
        }

        Object doCallableWork() {
            return null;
        }

        void onComplete() {
            SwingUtilities.invokeLater(() -> {
                paintTask.cancel(true);
                progressBarWidget.setProgress(1f);
                progressBarWidget.complete();
            });
        }

        void onCancelled() {
            SwingUtilities.invokeLater(() -> {
                paintTask.cancel(true);
                progressBarWidget.setProgress(1f);
                progressBarWidget.cancelled();
            });
        }

        void onInterrupted() {
            SwingUtilities.invokeLater(() -> {
                paintTask.cancel(true);
                progressBarWidget.setProgress(1f);
                progressBarWidget.error();
            });
        }

        void onError() {
            SwingUtilities.invokeLater(() -> {
                paintTask.cancel(true);
                progressBarWidget.setProgress(1f);
                progressBarWidget.error();
            });
        }

        void onTimeout() {
            SwingUtilities.invokeLater(() -> {
                paintTask.cancel(true);
                progressBarWidget.setProgress(1f);
                progressBarWidget.timeout();
            });
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
                progressBarWidget.setProgress(0.0f);
            });

            // this is an intentional infinite loop
            for (float progressPercent = 0.0f; ; progressPercent += resolution) {
                final float progressPercentToPaint = progressPercent;
                SwingUtilities.invokeLater(() -> {
                    progressBarWidget.setProgress(progressPercentToPaint);
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

        public Object getResult() {
            return result;
        }
    }
}

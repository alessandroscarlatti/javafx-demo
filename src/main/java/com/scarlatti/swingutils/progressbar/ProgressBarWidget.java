package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.scarlatti.swingutils.messaging.MessagingUtils.createName;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 * <p>
 * This widget will actually be JUST the progress bar!
 */
public class ProgressBarWidget implements Widget {

    private JProgressBar widgetProgressBar;
    private final int PROGRESS_BAR_LENGTH = 10000;
    private String name = createName(this);
    private MessageBus messageBus = new MessageBus();

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private Future paintTask;
    private long expectedDurationMs = 5000;
    private boolean showExactProgress = true;

    public ProgressBarWidget(Consumer<ProgressBarWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<ProgressBarWidget> config) {
        ProgressBarWidget widget = new ProgressBarWidget(config);
        return widget.getUi();
    }

    @Override
    public Container getUi() {
        if (widgetProgressBar == null)
            initUi();

        return widgetProgressBar;
    }

    private void initUi() {
        widgetProgressBar = new JProgressBar(0, PROGRESS_BAR_LENGTH);
        widgetProgressBar.setIndeterminate(!showExactProgress);

        Topic<ProgressBarWidgetApi> apiTopic = Topic.create(name + ".api", ProgressBarWidgetApi.class);

        messageBus.connect().subscribe(apiTopic, new ProgressBarWidgetApi() {
            @Override
            public void reset() {
                widgetProgressBar.setValue(0);
                if (paintTask != null) {
                    paintTask.cancel(true);
                    paintTask = null;
                }
            }

            @Override
            public void start() {
                // start the paint task
                if (paintTask == null)
                    paintTask = executor.submit(ProgressBarWidget.this::paintProgressBar);
            }

            @Override
            public void stop() {
                if (paintTask != null) {
                    paintTask.cancel(true);
                    paintTask = null;
                }
            }

            @Override
            public void complete() {
                if (paintTask != null) {
                    paintTask.cancel(true);
                    paintTask = null;
                }
                widgetProgressBar.setValue(PROGRESS_BAR_LENGTH);
            }
        });
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public void setMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public void connectAs(String name, MessageBus messageBus) {
        this.name = name;
        this.messageBus = messageBus;
    }

    public Topic<ProgressBarWidgetApi> getApiTopic() {
        return Topic.create(name + ".api", ProgressBarWidgetApi.class);
    }

    /**
     * This is the runnable for the paint task thread.
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

    public String getName() {
        return name;
    }

    /**
     * Set the progress level
     *
     * @param progressPercent percent of progress, 0.0f - 1.0f
     */
    private void setProgress(float progressPercent) {
        widgetProgressBar.setValue((int) (progressPercent * PROGRESS_BAR_LENGTH));
    }

    public void setExpectedDurationMs(long expectedDurationMs) {
        this.expectedDurationMs = expectedDurationMs;
    }

    public void setShowExactProgress(boolean showExactProgress) {
        this.showExactProgress = showExactProgress;
    }

    public interface ProgressBarWidgetApi {

        default void reset() {
        }

        default void start() {
        }

        default void stop() {
        }

        default void complete() {
        }
    }
}

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
    public void progressBarWithRunningTask() {
        SwingUtils.display(() -> {

            ExecutorService executor = Executors.newFixedThreadPool(10);

            ProgressBarWidget widget = new ProgressBarWidget(progressBarWidget -> {

                AtomicReference<Future> workTask = new AtomicReference<>();
                AtomicReference<Future> paintTask = new AtomicReference<>();

                progressBarWidget.props.onCancelled = () -> {
                    executor.execute(() -> {
                        paintTask.get().cancel(true);
                        workTask.get().cancel(true);
                        progressBarWidget.cancelled();
                    });
                };

                progressBarWidget.props.onStarted = () -> {
                    executor.execute(() -> {
                        try {
                            progressBarWidget.inProgress();

                            // start the paint task
                            paintTask.set(executor.submit(() -> {
                                paintProgressBar(progressBarWidget, 3000);
                            }));

                            // start the work task
                            workTask.set(executor.submit(() -> {
                                sleep(60000);  // this is the work that takes a long time
                            }));

                            workTask.get().get(60, TimeUnit.SECONDS);
                            paintTask.get().cancel(true);
                            progressBarWidget.setProgress(1f);
                            progressBarWidget.complete();
                        } catch (CancellationException e) {
                            paintTask.get().cancel(true);
                            progressBarWidget.setProgress(1f);
                            progressBarWidget.cancelled();
                        } catch (InterruptedException | ExecutionException e) {
                            paintTask.get().cancel(true);
                            progressBarWidget.setProgress(1f);
                            progressBarWidget.error();
                        } catch (TimeoutException e) {
                            paintTask.get().cancel(true);
                            progressBarWidget.setProgress(1f);
                            progressBarWidget.timeout();
                        }
                    });
                };
            });

            return widget.getUi();
        });
    }

    private static void paintProgressBar(ProgressBarWidget widget, long durationMs) {
        // this is the paint thread
        long refreshRateMs = 15;
        float resolution = 1.0f / (float) (durationMs / refreshRateMs);  // need to count from 0 to 1 in durationMs
        float fallbackPercent = 0.4f;  // if we are still going, go back to the fallback percent.

        widget.setProgress(0.0f);

        // this is an intentional infinite loop
        for (float progressPercent = 0.0f; ; progressPercent += resolution) {
            widget.setProgress(progressPercent);
            sleep(refreshRateMs);

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

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

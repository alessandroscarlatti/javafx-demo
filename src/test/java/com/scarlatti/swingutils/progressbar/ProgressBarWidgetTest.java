package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.button.ButtonWidget;
import com.scarlatti.swingutils.grid.RowsWidget;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import com.scarlatti.swingutils.progressbar.ProgressBarWidget.ProgressBarWidgetApi;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Executors;

import static com.scarlatti.swingutils.SwingUtils.sleep;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 */
public class ProgressBarWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void paintProgressBar() {

        MessageBus messageBus = new MessageBus();

        // connect to progress bar
        Topic<ProgressBarWidgetApi> progressBarApiTopic = Topic.create("progressBar.api", ProgressBarWidgetApi.class);
        ProgressBarWidgetApi progressBarWidgetApi = messageBus.syncPublisher(progressBarApiTopic);

        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    ButtonWidget.ui(buttonWidget -> {
                        buttonWidget.setText("Paint Progress Bar");
                        buttonWidget.setAction(() -> {
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    progressBarWidgetApi.reset();
                                    progressBarWidgetApi.start();
                                    sleep(2000);
                                    progressBarWidgetApi.stop();
                                });
                            }
                        );
                    })
                );
                rowsWidget.addRow(
                    ProgressBarWidget.ui(progressBarWidget -> {
                        progressBarWidget.connectAs("progressBar", messageBus);
                    })
                );
            })
        );
    }

}
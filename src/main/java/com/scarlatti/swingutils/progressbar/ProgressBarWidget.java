package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class ProgressBarWidget implements Widget {

    private JProgressBar progressBar;
    private JPanel widgetPanel;
    private JButton statusButton;

    ProgressBarWidgetProps props = new ProgressBarWidgetProps();
    private ProgressBarWidgetState state = new ProgressBarWidgetState();

    private ActionListener startActionListener = e -> {
        props.onStarted.run();
    };

    private ActionListener cancelActionListener = e -> {
        props.onCancelled.run();
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
        progressBar.setValue((int) (progressPercent * 1000));
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
        progressBar = new JProgressBar(0, 1000);
        progressBar.setValue(4);

//        JPanel progressBarPanel = new JPanel(new BorderLayout());
//        progressBarPanel.add(progressBar, BorderLayout.CENTER);
//        progressBarPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 31));

//        statusIcon = new JLabel();
//        statusIcon.setIcon(SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/clock.png", ICON_SCALE_31));
//
//        cancelButton = new JLabel();
//        cancelButton.setIcon(SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/cancel-icon.png", ICON_SCALE_15));

        statusButton = new JButton();

        GroupLayout gl = new GroupLayout(widgetPanel);
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

        switch (state.taskState) {
            case PENDING:
                statusButton.setText("Start");
                statusButton.setEnabled(true);
                statusButton.addActionListener(startActionListener);
                break;
            case IN_PROGRESS:
                statusButton.setText("Cancel");
                statusButton.setEnabled(true);
                statusButton.addActionListener(cancelActionListener);
                break;
            case TIMEOUT:
                statusButton.setText("Timed out");
                statusButton.setEnabled(false);
                break;
            case CANCELLED:
                statusButton.setText("Canceled");
                statusButton.setEnabled(false);
                break;
            case FAILED:
                statusButton.setText("Failed");
                statusButton.setEnabled(false);
                break;
            case COMPLETE:
                statusButton.setText("Complete");
                statusButton.setEnabled(false);
                break;
        }
    }

    public static class ProgressBarWidgetProps {
        Runnable onCancelled = () -> {};
        Runnable onStarted = () -> {};
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
}

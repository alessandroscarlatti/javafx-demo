package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.button.ButtonWidget;
import com.scarlatti.swingutils.button.ButtonWidget.ButtonWidgetApi;
import com.scarlatti.swingutils.button.ButtonWidget.ButtonWidgetEvents;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Connection;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import com.scarlatti.swingutils.progressbar.ProgressBarWidget.ProgressBarWidgetApi;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateApi;
import com.scarlatti.swingutils.progressbar.TaskTemplate.TaskTemplateEvents;
import com.scarlatti.swingutils.text.MultilineTextWidget;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static com.scarlatti.swingutils.SwingUtils.makeBold;
import static com.scarlatti.swingutils.messaging.MessagingUtils.createName;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 1/3/2019
 */
public class TaskProgressBarWidget implements Widget {

    private TaskTemplate taskTemplate = defaultTaskTemplate();
    private ProgressBarWidget progressBarWidget = defaultProgressBarWidget();
    private JPanel widgetPanel;
    private ButtonWidget statusButtonWidget;
    private String title;
    private String message;
    private boolean selfStartable = true;
    private boolean retryable = false;
    private boolean repeatable = false;
    private boolean cancelable = true;

    private String name = createName(this);
    private MessageBus messageBus = new MessageBus();

    private TaskState taskState = TaskState.READY;
    private ProgressBarWidgetApi progressBarWidgetApi;
    private TaskTemplateApi taskTemplateWidgetApi;

    public TaskProgressBarWidget() {
    }

    public TaskProgressBarWidget(Consumer<TaskProgressBarWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<TaskProgressBarWidget> config) {
        TaskProgressBarWidget widget = new TaskProgressBarWidget(config);
        return widget.getUi();
    }

    @Override
    public Container getUi() {
        widgetPanel = new JPanel();

        statusButtonWidget = new ButtonWidget(buttonWidget -> {
            buttonWidget.setText("Start");
            buttonWidget.connectAs(name + ".statusButton", messageBus);
        });

        progressBarWidget.connectAs(name + ".progressBar", messageBus);

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
            .addComponent(statusButtonWidget.getUi())
            .addComponent(progressBarWidget.getUi());

        progressBarVGroup
            .addComponent(statusButtonWidget.getUi())
            .addComponent(progressBarWidget.getUi());

        widgetHGroup.addGroup(progressBarHGroup);
        widgetVGroup.addGroup(progressBarVGroup);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);

        configureStatusButtonByState();
        initMessaging();
        return widgetPanel;
    }

    private void configureStatusButtonByState() {

        Topic<ButtonWidgetApi> buttonWidgetApiTopic = Topic.create(statusButtonWidget.getName() + ".api", ButtonWidgetApi.class);
        ButtonWidgetApi buttonWidgetApi = messageBus.syncPublisher(buttonWidgetApiTopic);

        SwingUtilities.invokeLater(() -> {
            switch (taskState) {
                case NOT_READY:
                    buttonWidgetApi.setText("Start");
                    buttonWidgetApi.disable();
                    break;
                case READY:
                    if (selfStartable) {
                        buttonWidgetApi.setText("Start");
                        buttonWidgetApi.enable();
                        break;
                    } else {
                        buttonWidgetApi.setText("Start");
                        buttonWidgetApi.disable();
                    }
                case IN_PROGRESS:
                    if (cancelable) {
                        buttonWidgetApi.setText("Cancel");
                        buttonWidgetApi.enable();
                    } else {
                        buttonWidgetApi.setText("Working...");
                        buttonWidgetApi.disable();
                    }
                    break;
                case CANCELLED:
                    if (retryable) {
                        buttonWidgetApi.setText("Retry");
                        buttonWidgetApi.enable();
                    } else {
                        buttonWidgetApi.setText("Cancelled");
                        buttonWidgetApi.disable();
                    }
                    break;
                case TIMED_OUT:
                    if (retryable) {
                        buttonWidgetApi.setText("Retry");
                        buttonWidgetApi.enable();
                    } else {
                        buttonWidgetApi.setText("Timed Out");
                        buttonWidgetApi.disable();
                    }
                    break;
                case FAILED:
                    if (retryable) {
                        buttonWidgetApi.setText("Retry");
                        buttonWidgetApi.enable();
                    } else {
                        buttonWidgetApi.setText("Failed");
                        buttonWidgetApi.disable();
                    }
                    break;
                case COMPLETE:
                    if (repeatable) {
                        buttonWidgetApi.setText("Do Again");
                        buttonWidgetApi.enable();
                    } else {
                        buttonWidgetApi.setText("Complete");
                        buttonWidgetApi.disable();
                    }
                    break;
            }
        });
    }

    private void initMessaging() {
        // now set up the messaging component...
        // progress bar
        Topic<ProgressBarWidgetApi> progressBarApiTopic = Topic.create(progressBarWidget.getName() + ".api", ProgressBarWidgetApi.class);
        // task template
        Topic<TaskTemplateApi> taskTemplateApiTopic = taskTemplate.getApiTopic();
        Topic<TaskTemplateEvents> taskTemplateEventsTopic = taskTemplate.getEventsTopic();
        // button
        Topic<ButtonWidgetEvents> buttonEventsTopic = Topic.create(statusButtonWidget.getName() + ".events", ButtonWidgetEvents.class);

        // publisher
        progressBarWidgetApi = messageBus.syncPublisher(progressBarApiTopic);
        taskTemplateWidgetApi = messageBus.syncPublisher(taskTemplateApiTopic);

        Connection connection = messageBus.connect();

        // subscribe to the button
        connection.subscribe(buttonEventsTopic, new ButtonWidgetEvents() {
            @Override
            public void clicked() {
                // disable the button, perhaps...
                switch (taskState) {
                    case READY:
                        start();
                        break;
                    case IN_PROGRESS:
                        cancel();
                        break;
                    case CANCELLED:
                    case TIMED_OUT:
                    case FAILED:
                        restart();
                        break;
                    case COMPLETE:
                        if (repeatable) {
                            start();
                        }
                        break;
                }
            }
        });

        connection.subscribe(taskTemplateEventsTopic, new TaskTemplateEvents() {
            @Override
            public void started() {
                progressBarWidgetApi.start();
                taskState = TaskState.IN_PROGRESS;
                configureStatusButtonByState();
            }

            @Override
            public void cancelled(Exception e) {
                progressBarWidgetApi.stop();
                taskState = TaskState.CANCELLED;
                configureStatusButtonByState();
            }

            @Override
            public void interrupted(Exception e) {
                progressBarWidgetApi.stop();
                taskState = TaskState.FAILED;
                configureStatusButtonByState();
            }

            @Override
            public void timedOut(Exception e) {
                progressBarWidgetApi.stop();
                taskState = TaskState.TIMED_OUT;
                configureStatusButtonByState();
            }

            @Override
            public void error(Exception e) {
                progressBarWidgetApi.stop();
                taskState = TaskState.FAILED;
                configureStatusButtonByState();
            }

            @Override
            public void completed(Object result) {
                progressBarWidgetApi.complete();
                taskState = TaskState.COMPLETE;
                configureStatusButtonByState();
            }
        });
    }

    public void connectAs(String name, MessageBus messageBus) {
        this.name = name;
        this.messageBus = messageBus;
    }

    public void setSelfStartable(boolean selfStartable) {
        this.selfStartable = selfStartable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    private enum TaskState {
        NOT_READY,
        READY,
        IN_PROGRESS,
        CANCELLED,
        TIMED_OUT,
        FAILED,
        COMPLETE
    }

    /**
     * Actions
     */
    private void start() {
        taskTemplateWidgetApi.start();
    }

    private void cancel() {
        taskTemplateWidgetApi.stop();
    }

    private void restart() {
        taskTemplateWidgetApi.start();
    }

    private ProgressBarWidget defaultProgressBarWidget() {
        return new ProgressBarWidget(progressBarWidget -> {});
    }

    private TaskTemplate defaultTaskTemplate() {
        return new TaskTemplate();
    }

    public TaskTemplate getTaskTemplate() {
        return taskTemplate;
    }

    public void setTaskTemplate(TaskTemplate taskTemplate) {
        this.taskTemplate = taskTemplate;
    }

    public ProgressBarWidget getProgressBarWidget() {
        return progressBarWidget;
    }

    public void setProgressBarWidget(ProgressBarWidget progressBarWidget) {
        this.progressBarWidget = progressBarWidget;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

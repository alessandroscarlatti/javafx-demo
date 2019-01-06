package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.button.ButtonWidget;
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
    private ProgressBarWidget progressBarWidget = defaultProgressBarUi();
    private JPanel widgetPanel;
    private ButtonWidget statusButtonWidget;
    private String title;
    private String message;

    private String name = createName(this);
    private MessageBus messageBus = new MessageBus();

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
        });

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

        initMessaging();
        return widgetPanel;
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
        ProgressBarWidgetApi progressBarWidgetApi = messageBus.syncPublisher(progressBarApiTopic);
        TaskTemplateApi taskTemplateWidgetApi = messageBus.syncPublisher(taskTemplateApiTopic);

        Connection connection = messageBus.connect();

        // subscribe to the button
        connection.subscribe(buttonEventsTopic, new ButtonWidgetEvents() {
            @Override
            public void clicked() {
                // disable the button, perhaps...
                // or change its status based on the task state...

                taskTemplateWidgetApi.start();
            }
        });

        connection.subscribe(taskTemplateEventsTopic, new TaskTemplateEvents() {
            @Override
            public void started() {
                // todo update the status button...
                progressBarWidgetApi.start();
            }

            @Override
            public void cancelled(Exception e) {
                // todo update the status button...
                progressBarWidgetApi.stop();
            }

            @Override
            public void interrupted(Exception e) {
                // todo update the status button...
                progressBarWidgetApi.stop();
            }

            @Override
            public void timedOut(Exception e) {
                // todo update the status button...
                progressBarWidgetApi.stop();
            }

            @Override
            public void error(Exception e) {
                // todo update the status button...
                progressBarWidgetApi.stop();
            }

            @Override
            public void completed(Object result) {
                // todo update the status button...
                progressBarWidgetApi.complete();
            }
        });
    }

    public void connectAs(String name, MessageBus messageBus) {
        this.name = name;
        this.messageBus = messageBus;
    }

    private ProgressBarWidget defaultProgressBarUi() {
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

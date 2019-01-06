package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.messaging.MessageBus;

import java.awt.*;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 1/3/2019
 */
public class ProgressBarWidgetOld extends ProgressBarUi {

    private TaskTemplate taskTemplate = defaultTaskTemplate();
    private MessageBus messageBus;

    public ProgressBarWidgetOld() {
    }

    public ProgressBarWidgetOld(Consumer<ProgressBarWidgetOld> config) {
        config.accept(this);
        connect();
    }

    public static Container ui2(Consumer<ProgressBarWidgetOld> config) {
        ProgressBarWidgetOld widget = new ProgressBarWidgetOld(config);
        return widget.getUi();
    }
    
    public void connect() {
        // create default message bus
        // and connect the progress bar and the task template if necessary.
        if (messageBus == null) {
            // assume that we will create a default message bus.
            messageBus = new MessageBus();
        }

        getTaskTemplate().connect(messageBus);
        connectApi(messageBus, getTaskTemplate().getApiTopic());
        connectEvents(messageBus, getTaskTemplate().getEventsTopic());
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

    public void setMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
    }
}

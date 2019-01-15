package com.scarlatti.swingutils.button;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 */
public class ButtonWidget implements Widget {

    private JButton widgetButton;
    private String text = "Button";
    private boolean enabled = true;
    private String name = "ButtonWidget@" + Integer.toHexString(System.identityHashCode(this));
    private MessageBus messageBus = new MessageBus();
    private Runnable action = () -> {};

    public ButtonWidget(Consumer<ButtonWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<ButtonWidget> config) {
        return new ButtonWidget(config).getUi();
    }

    @Override
    public Container getUi() {
        if (widgetButton == null)
            initUi();

        return widgetButton;
    }

    private void initUi() {
        widgetButton = new JButton();
        widgetButton.setText(text);
        widgetButton.setEnabled(enabled);

        Topic<ButtonWidgetEvents> eventsTopic = Topic.create(name + ".events", ButtonWidgetEvents.class);
        ButtonWidgetEvents events = messageBus.syncPublisher(eventsTopic);

        widgetButton.addActionListener(e -> {
            action.run();
            events.clicked();
        });

        Topic<ButtonWidgetApi> apiTopic = Topic.create(name + ".api", ButtonWidgetApi.class);
        messageBus.connect().subscribe(apiTopic, new ButtonWidgetApi() {
            @Override
            public void enable() {
                setEnabled(true);
                configureByState();
            }

            @Override
            public void disable() {
                setEnabled(false);
                configureByState();
            }

            @Override
            public void setText(String text) {
                ButtonWidget.this.setText(text);
                configureByState();
            }
        });
    }

    private void configureByState() {
        widgetButton.setText(text);
        widgetButton.setEnabled(enabled);
    }

    public void connectAs(String name, MessageBus messageBus) {
        this.messageBus = messageBus;
        this.name = name;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public void setMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public Topic<ButtonWidgetEvents> getEventsTopic() {
        return Topic.create(name + ".events", ButtonWidgetEvents.class);
    }

    public Topic<ButtonWidgetApi> getApiTopic() {
        return Topic.create(name + ".api", ButtonWidgetApi.class);
    }

    public String getName() {
        return name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public interface ButtonWidgetApi {
        default void enable(){}
        default void disable(){}
        default void setText(String text){}
    }

    public interface ButtonWidgetEvents {
        default void clicked(){}
    }

}

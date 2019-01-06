package com.scarlatti.swingutils.button;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.button.ButtonWidget.ButtonWidgetApi;
import com.scarlatti.swingutils.button.ButtonWidget.ButtonWidgetEvents;
import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.scarlatti.swingutils.messaging.MessageBus.Binding.bind;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 */
public class ButtonWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void testButtonWidget() {
        SwingUtils.display(
            ButtonWidget.ui(buttonWidget -> {
                buttonWidget.setText("Do Something");
                buttonWidget.setAction(() -> {
                    System.out.println("do something");
                });
            })
        );
    }

    @Test
    public void testButtonWidgetListeningFromOutside() {

        MessageBus bus = new MessageBus();
        Topic<ButtonWidgetEvents> buttonEventsTopic = Topic.create("button.events", ButtonWidgetEvents.class);
        Topic<ButtonWidgetApi> buttonApiTopic = Topic.create("button.api", ButtonWidgetApi.class);

        bus.connect().subscribe(buttonEventsTopic, new ButtonWidgetEvents() {
            @Override
            public void clicked() {
                System.out.println("Do Something");
            }
        });

        bind(buttonEventsTopic, buttonApiTopic, bus, buttonApi -> new ButtonWidgetEvents() {
            @Override
            public void clicked() {
                buttonApi.disable();
            }
        });

        SwingUtils.display(
            ButtonWidget.ui(buttonWidget -> {
                buttonWidget.connectAs("button", bus);
                buttonWidget.setText("Do Something");
            })
        );
    }
}
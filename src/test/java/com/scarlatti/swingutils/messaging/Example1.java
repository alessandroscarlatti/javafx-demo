package com.scarlatti.swingutils.messaging;

import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import org.junit.Test;

import static com.scarlatti.swingutils.messaging.MessageBus.Binding.bind;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 * <p>
 * Progress Bar and Play Button in a Page, asserting behaviors in a Test.
 */
public class Example1 {

    @Test
    public void connectAndAssertTheBehavior() {
        MessageBus bus = new MessageBus();
        Topic<LifecycleApi> progressBarApiTopic = Topic.create("progressBar.api", LifecycleApi.class);
        Topic<LifecycleEvents> progressBarEventsTopic = Topic.create("progressBar.events", LifecycleEvents.class);
        Topic<LifecycleApi> taskApiTopic = Topic.create("task.api", LifecycleApi.class);
        Topic<LifecycleEvents> taskEventsTopic = Topic.create("task.events", LifecycleEvents.class);
        Topic<ButtonApi> playButtonApiTopic = Topic.create("playButton.api", ButtonApi.class);
        Topic<ButtonEvents> playButtonEventsTopic = Topic.create("playButton.events", ButtonEvents.class);
        Topic<LifecycleEvents> pageEventsTopic = Topic.create("page.events", LifecycleEvents.class);
        Topic<LifecycleEvents> testEventsTopic = Topic.create("test.events", LifecycleEvents.class);

        // now bind all the things together properly.
        bind(taskEventsTopic, progressBarEventsTopic, bus);
        bind(progressBarApiTopic, taskApiTopic, bus);

        bind(playButtonEventsTopic, taskApiTopic, bus, taskApi -> new ButtonEvents() {
            @Override
            public void clicked() {
                taskApi.start();
            }
        });

        bind(playButtonEventsTopic, playButtonApiTopic, bus, playButtonApi -> new ButtonEvents() {
            @Override
            public void clicked() {
                playButtonApi.disable();
            }
        });

        bind(taskEventsTopic, playButtonApiTopic, bus, buttonApi -> new LifecycleEvents() {
            @Override
            public void started() {
                buttonApi.disable();
            }

            @Override
            public void finishedAny() {
                buttonApi.enable();
            }
        });

        bind(taskEventsTopic, pageEventsTopic, bus);
        bind(pageEventsTopic, testEventsTopic, bus);
    }

    private interface ButtonApi {
        default void enable() {
        }

        default void disable() {
        }
    }

    private interface ButtonEvents {
        default void clicked() {
        }
    }

    private interface LifecycleApi {
        default void start() {
        }

        default void stop() {
        }

        default void pause() {
        }
    }

    private interface LifecycleEvents {

        default void started() {
        }

        default void cancelled(Exception e) {
        }

        default void interrupted(Exception e) {
        }

        default void timedOut(Exception e) {
        }

        default void error(Exception e) {
        }

        default void completed(Object result) {
        }

        default void finishedError() {
        }

        default void finishedAny(){
        }
    }

}

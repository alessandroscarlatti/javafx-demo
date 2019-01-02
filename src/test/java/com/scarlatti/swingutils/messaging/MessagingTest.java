package com.scarlatti.swingutils.messaging;

import com.scarlatti.swingutils.messaging.MessageBus.Connection;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import org.junit.Test;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/1/2019
 */
public class MessagingTest {

    @Test
    public void testMessageBus() {
        MessageBus bus = new MessageBus();

        Topic<LunchNotifier> lunchNotifierTopic = Topic.create("lunch", LunchNotifier.class);

        LunchNotifier lunchNotifier = bus.syncPublisher(lunchNotifierTopic);

        Connection connection1 = bus.connect();
        connection1.subscribe(lunchNotifierTopic, new LunchNotifier() {
            @Override
            public void firstCourseServed() {
                System.out.println("served 1");
                System.out.println("which CALLED secondCourseServed!!!");
                lunchNotifier.secondCourseServed();
            }

            @Override
            public void secondCourseServed() {
                System.out.println("served 2");
            }

            @Override
            public void thirdCourseServed() {
                System.out.println("served 3");
            }
        });

        Connection connection2 = bus.connect();
        connection2.subscribe(lunchNotifierTopic, new LunchNotifier() {
            @Override
            public void firstCourseServed() {
                System.out.println("also served 1");
            }

            @Override
            public void secondCourseServed() {
                System.out.println("also served 2");
            }

            @Override
            public void thirdCourseServed() {
                System.out.println("also served 3");
            }
        });

        lunchNotifier.firstCourseServed();
        lunchNotifier.secondCourseServed();
        lunchNotifier.thirdCourseServed();

        connection1.unsubscribe(lunchNotifierTopic);

        lunchNotifier.firstCourseServed();
        lunchNotifier.secondCourseServed();
        lunchNotifier.thirdCourseServed();

        connection1.disconnect();
        connection2.disconnect();
    }

    private interface LunchNotifier {
        void firstCourseServed();
        void secondCourseServed();
        void thirdCourseServed();
    }

}

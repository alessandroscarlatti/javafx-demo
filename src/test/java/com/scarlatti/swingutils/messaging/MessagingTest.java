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

    @Test
    public void testDinnerNotifier() {
        MessageBus bus = new MessageBus();
        Connection connection = bus.connect();

        Topic<LunchNotifier> lunchTopic = Topic.create("lunch", LunchNotifier.class);
        Topic<DinnerNotifier> dinnerTopic = Topic.create("dinner", DinnerNotifier.class);

        connection.subscribe(dinnerTopic, new DinnerNotifier() {
            @Override
            public void mainCourse(String name) {
                System.out.println("Main course: " + name);
            }
        });

        connection.subscribe(lunchTopic, new LunchNotifier() {
            @Override
            public void firstCourseServed() {
                System.out.println("first course served");
            }

            @Override
            public void secondCourseServed() {
                System.out.println("second course served");

            }

            @Override
            public void thirdCourseServed() {
                System.out.println("third course served");

            }
        });

        LunchNotifier lunchNotifier = bus.syncPublisher(lunchTopic);
        DinnerNotifier dinnerNotifier = bus.syncPublisher(dinnerTopic);

        lunchNotifier.firstCourseServed();
        lunchNotifier.secondCourseServed();
        lunchNotifier.thirdCourseServed();

        dinnerNotifier.mainCourse("Turkey");

        connection.disconnect();
    }

    private interface DinnerNotifier {
        void mainCourse(String name);
    }

    private interface LunchNotifier {
        void firstCourseServed();
        void secondCourseServed();
        void thirdCourseServed();
    }

}

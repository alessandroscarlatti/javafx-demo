package com.scarlatti.swingutils.messaging;

import com.scarlatti.swingutils.messaging.MessageBus.Binding;
import com.scarlatti.swingutils.messaging.MessageBus.Connection;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testBindingsManual() {
        MessageBus bus = new MessageBus();

        Topic<TopicA> topicA = Topic.create("topicA", TopicA.class);
        Topic<TopicB> topicB = Topic.create("topicB", TopicB.class);

        List<String> responses = new ArrayList<>();

        // set up a publisher on topicA
        TopicA topicAPublisher = bus.syncPublisher(topicA);

        // set up a subscriber on topicB
        bus.connect().subscribe(topicB, new TopicB() {
            @Override
            public void sayB() {
                System.out.println("B");
                responses.add("B");
            }
        });

        // bind the two together
        bus.connect().subscribe(topicA, new TopicA() {

            private TopicB topicBPublisher = bus.syncPublisher(topicB);

            @Override
            public void sayA() {
                topicBPublisher.sayB();
            }
        });

        // publish to topicA
        topicAPublisher.sayA();

        // verify received on topicB
        assertEquals(1, responses.size());
        assertEquals("B", responses.get(0));
    }

    @Test
    public void testBindingsAuto() {
        MessageBus bus = new MessageBus();

        // create two topics of the same interface, but different topic names.
        // this means they are directly compatible, but not connected to one another.
        Topic<TopicA> topicA = Topic.create("topicA", TopicA.class);
        Topic<TopicA> topicA2 = Topic.create("topicA2", TopicA.class);

        List<String> responses = new ArrayList<>();

        // set up a publisher on topicA
        TopicA topicAPublisher = bus.syncPublisher(topicA);

        // set up a subscriber on topicB
        bus.connect().subscribe(topicA2, new TopicA() {
            @Override
            public void sayA() {
                System.out.println("A");
                responses.add("A");
            }
        });

        // set up a subscriber on topicA2
        bindTopicsAtoB(topicA, topicA2, bus);

        // publish to topicA
        topicAPublisher.sayA();

        // verify received on topicB
        assertEquals(1, responses.size());
        assertEquals("A", responses.get(0));
    }

    @Test
    public void bindingSyntax() {

        MessageBus bus = new MessageBus();
        MessageBus bus2 = new MessageBus();

        // create two topics of the same interface, but different topic names.
        // this means they are directly compatible, but not connected to one another.
        Topic<TopicA> topicA = Topic.create("topicA", TopicA.class);
        Topic<TopicA> topicA2 = Topic.create("topicA2", TopicA.class);
        Topic<TopicB> topicB = Topic.create("topicB", TopicB.class);

        Binding topicATopicBBinding = Binding.bind(topicA, topicB, bus, publisher -> new TopicA() {
            @Override
            public void sayA() {
                publisher.sayB();
            }

            @Override
            public void sayAgain() {
                publisher.sayB();
            }
        });

        List<String> responses = new ArrayList<>();

        bus.connect().subscribe(topicB, new TopicB() {
            @Override
            public void sayB() {
                System.out.println("B");
                responses.add("B");
            }
        });

        bus.connect().subscribe(topicA2, new TopicA() {
            @Override
            public void sayA() {
                System.out.println("A");
            }
        });

        bus.syncPublisher(topicA).sayA();
        assertEquals(Arrays.asList("B"), responses);

        topicATopicBBinding.unbind();
        bus.syncPublisher(topicA).sayA();
        assertEquals(Arrays.asList("B"), responses);

        topicATopicBBinding.bind();
        bus.syncPublisher(topicA).sayA();
        assertEquals(Arrays.asList("B", "B"), responses);
    }

    private static <E, F extends E> void bindTopicsAtoB(Topic<E> topic1, Topic<F> topic2, MessageBus bus) {
        // bind the two topics together
        E topic2Publisher = bus.syncPublisher(topic2);

        @SuppressWarnings("unchecked")
        E topic1SubscriberProxy = (E) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[]{topic1.getMessageClazz()},
            (proxy, method, args) -> {
                return method.invoke(topic2Publisher, args);
            }
        );

        bus.connect().subscribe(topic1, topic1SubscriberProxy);
    }

    private interface TopicA {
        default void sayA() { }

        default void sayAgain() { }
    }

    private interface TopicB {
        void sayB();
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

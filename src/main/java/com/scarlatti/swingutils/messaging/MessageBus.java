package com.scarlatti.swingutils.messaging;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 1/1/2019
 */
public class MessageBus {

    private Map<String, Publisher> publishersMap = new HashMap<>();
    private List<Connection> connections = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T> T syncPublisher(Topic<T> topic) {

        // if we don't have a publisher, we need to create one.
        if (!publishersMap.containsKey(topic.name)) {
            Publisher<T> publisher = new Publisher<>(topic);
            publishersMap.put(topic.name, publisher);
        }

        // now we definitely have a publisher
        Publisher<T> publisher = (Publisher<T>) publishersMap.get(topic.name);

        // return the proxy object.
        return publisher.publisherProxyInstance;
    }

    public Connection connect() {
        Connection connection = new Connection(this::disconnect);
        connections.add(connection);
        return connection;
    }

    private void disconnect(Connection connection) {
        connections.remove(connection);
    }

    private class Publisher<T> {
        private Topic<T> topic;
        private T publisherProxyInstance;
        private Queue<Invocation> queue = new ArrayDeque<>();

        @SuppressWarnings("unchecked")
        private Publisher(Topic<T> topic) {
            this.topic = topic;
            publisherProxyInstance = (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), new Class[]{topic.messageClazz}, this::invoke);
        }

        private Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // a nested call within a topic would probably still be on the same thread...
            // but either way, just accept the args into a queue for this particular method.
            // at the end of this method empty the queue.

            if (queue.isEmpty()) {
                // this is the initial call
                queue.add(new Invocation(method, args));
            } else {
                // this is a nested call
                // store the args and move on
                queue.add(new Invocation(method, args));

                // don't try to actually perform anything.
                // another stack frame is already doing it.
                return null;
            }

            // invoke this method on each of the subscribed objects...
            // ask the connection list if there is a connection that is listening for this topic.
            performQueuedInvocationsOnAllSubscribers();
            return null;
        }

        @SuppressWarnings("unchecked")
        private void performQueuedInvocationsOnAllSubscribers() throws Throwable {
            while (queue.size() > 0) {
                Invocation invocation = queue.peek();
                boolean foundAnySubscriber = false;
                for (Connection connection : connections) {
                    if (connection.hasSubscriptionForTopic(topic)) {
                        foundAnySubscriber = true;
                        T subscriptionObject = (T) connection.getTopicSubject(topic);
                        invocation.method.invoke(subscriptionObject, invocation.args);
                    }
                }

                if (!foundAnySubscriber) {
//                    System.out.println("No subscribers for invocation on bus " + MessageBus.this + ": " + invocation);
                }

                // poll after we have notified subscribers so that new invocations
                // added to the queue do not accidentally think it is empty before
                // we have actually notified all subscribers.
                queue.poll();
            }
        }

        private class Invocation {
            Method method;
            Object[] args;

            public Invocation(Method method, Object[] args) {
                this.method = method;
                this.args = args;
            }

            @Override
            public String toString() {
                return "Invocation{" +
                    "method=" + method +
                    ", args=" + Arrays.toString(args) +
                    '}';
            }
        }
    }

    public static class Connection {

        private Map<String, Object> subjects = new HashMap<>();
        private Consumer<Connection> disconnect;

        public Connection(Consumer<Connection> disconnect) {
            this.disconnect = disconnect;
        }

        public Object getTopicSubject(Topic topic) {
            return subjects.get(topic.name);
        }

        public boolean hasSubscriptionForTopic(Topic topic) {
            return subjects.containsKey(topic.name);
        }

        public <T> void subscribe(Topic<T> topic, T subject) {
            subjects.put(topic.name, subject);
        }

        public void unsubscribe(Topic topic) {
            subjects.remove(topic.name);
        }

        public void disconnect() {
            disconnect.accept(this);
        }
    }

    public static class Topic<MessageClazz> {
        private String name;
        private Class<MessageClazz> messageClazz;

        public static <T> Topic<T> create(String name, Class<T> messageClazz) {
            Topic<T> topic = new Topic<>();
            topic.name = name;
            topic.messageClazz = messageClazz;
            return topic;
        }

        public String getName() {
            return name;
        }

        public Class<MessageClazz> getMessageClazz() {
            return messageClazz;
        }
    }

    public static class Binding<FromTopicT, ToTopicT> {
        private MessageBus fromMessageBus;
        private MessageBus toMessageBus;
        private Topic<FromTopicT> fromTopic;
        private Topic<ToTopicT> toTopic;
        private FromTopicT fromTopicSubscriber;
        private ToTopicT toTopicPublisher;
        private Function<ToTopicT, FromTopicT> fromTopicSubscriberFactoryFunc;
        private Connection connection;
        private boolean bound;

        public static <FromTopicT, ToTopicT extends FromTopicT> Binding<FromTopicT, ToTopicT> bind(
            Topic<FromTopicT> fromTopic,
            Topic<ToTopicT> toTopic,
            MessageBus messageBus
        ) {
            return Binding.bind(
                fromTopic,
                toTopic,
                messageBus,
                messageBus
            );
        }

        public static <FromTopicT, ToTopicT extends FromTopicT> Binding<FromTopicT, ToTopicT> bind(
            Topic<FromTopicT> fromTopic,
            Topic<ToTopicT> toTopic,
            MessageBus fromMessageBus,
            MessageBus toMessageBus
        ) {

            // use default binding function for binding like types
            return Binding.bind(
                fromTopic,
                toTopic,
                fromMessageBus,
                toMessageBus,
                bindLikeTopicsAtoB(fromTopic, toTopic)
            );
        }

        public static <FromTopicT, ToTopicT> Binding<FromTopicT, ToTopicT> bind(
            Topic<FromTopicT> fromTopic,
            Topic<ToTopicT> toTopic,
            MessageBus messageBus,
            Function<ToTopicT, FromTopicT> fromTopicSubscriberFactoryFunc
        ) {
            return Binding.bind(
                fromTopic,
                toTopic,
                messageBus,
                messageBus,
                fromTopicSubscriberFactoryFunc
            );
        }

        public static <FromTopicT, ToTopicT> Binding<FromTopicT, ToTopicT> bind(
            Topic<FromTopicT> fromTopic,
            Topic<ToTopicT> toTopic,
            MessageBus fromMessageBus,
            MessageBus toMessageBus,
            Function<ToTopicT, FromTopicT> fromTopicSubscriberFactoryFunc
        ) {
            Binding<FromTopicT, ToTopicT> binding = new Binding<>();
            binding.fromTopic = fromTopic;
            binding.toTopic = toTopic;
            binding.fromMessageBus = fromMessageBus;
            binding.toMessageBus = toMessageBus;
            binding.fromTopicSubscriberFactoryFunc = fromTopicSubscriberFactoryFunc;
            binding.bind();
            return binding;
        }

        @SuppressWarnings("unchecked")
        private static <FromTopicT, ToTopicT extends FromTopicT> Function<ToTopicT, FromTopicT> bindLikeTopicsAtoB(
            Topic<FromTopicT> topic1,
            Topic<ToTopicT> topic2
        ) {
            return publisher ->
                (FromTopicT) Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{topic1.getMessageClazz()},
                    (proxy, method, args) -> {
                        return method.invoke(publisher, args);
                    }
                );
        }

        @Override
        public String toString() {
            return "Binding{" +
                "fromMessageBus=" + fromMessageBus +
                ", toMessageBus=" + toMessageBus +
                ", fromTopic=" + fromTopic +
                ", toTopic=" + toTopic +
                ", fromTopicSubscriber=" + fromTopicSubscriber +
                ", toTopicPublisher=" + toTopicPublisher +
                '}';
        }

        public void unbind() {
            if (bound) {
                connection.disconnect();
                bound = false;
            }
        }

        public void bind() {
            if (!bound) {
                toTopicPublisher = toMessageBus.syncPublisher(toTopic);
                fromTopicSubscriber = fromTopicSubscriberFactoryFunc.apply(toTopicPublisher);
                connection = fromMessageBus.connect();
                connection.subscribe(fromTopic, fromTopicSubscriber);
                bound = true;
            }
        }
    }
}

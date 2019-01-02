package com.scarlatti.swingutils.messaging;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Consumer;

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

        @SuppressWarnings("unchecked")
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

            while (queue.size() > 0) {
                Invocation invocation = queue.peek();

                for (Connection connection : connections) {
                    if (connection.hasSubscriptionForTopic(topic)) {
                        T subscriptionObject = (T) connection.getTopicSubject(topic);
                        invocation.method.invoke(subscriptionObject, args);
                    }
                }

                queue.poll();
            }

            return null;
        }

        private class Invocation {
            Method method;
            Object[] args;

            public Invocation(Method method, Object[] args) {
                this.method = method;
                this.args = args;
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
    }
}

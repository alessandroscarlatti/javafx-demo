package com.scarlatti.swingutils.progressbar;

import com.scarlatti.swingutils.messaging.MessageBus;
import com.scarlatti.swingutils.messaging.MessageBus.Topic;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 1/2/2019
 */
public class TaskTemplate {
    private Topic<TaskTemplateEvents> eventsTopic;
    private Topic<TaskTemplateApi> commandsTopic;
    private ExecutorService executor = Executors.newFixedThreadPool(4);

    private String name = "task@" + Integer.toHexString(System.identityHashCode(this));
    private Future workTask;
    private long maxDurationMs = 30000;
    private Runnable runnableWork = this::doRunnableWork;
    private Callable<?> callableWork = this::doCallableWork;
    private Object result = null;
    private Exception exception = null;
    private TaskTemplateEvents eventsNotifier = new TaskTemplateEvents() {};

    public void setMaxDurationMs(long maxDurationMs) {
        this.maxDurationMs = maxDurationMs;
    }

    public void setWork(Runnable runnableWork) {
        this.runnableWork = runnableWork;
        this.callableWork = null;
    }

    public void setWork(Callable<?> callableWork) {
        this.callableWork = callableWork;
        this.runnableWork = null;
    }

    public TaskTemplate() {
    }

    public static TaskTemplate task(Consumer<TaskTemplate> config) {
        TaskTemplate taskTemplate = new TaskTemplate();
        config.accept(taskTemplate);
        return taskTemplate;
    }

    public static TaskTemplate task(String name, Consumer<TaskTemplate> config) {
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setName(name);
        config.accept(taskTemplate);
        return taskTemplate;
    }

    public Topic<TaskTemplateEvents> getEventsTopic() {
        if (eventsTopic == null)
            eventsTopic = Topic.create(name + ".events", TaskTemplateEvents.class);

        return eventsTopic;
    }

    public Topic<TaskTemplateApi> getApiTopic() {
        if (commandsTopic == null)
            commandsTopic = Topic.create(name + ".api", TaskTemplateApi.class);

        return commandsTopic;
    }

    public void connect(MessageBus bus) {
        eventsNotifier = bus.syncPublisher(getEventsTopic());
        subscribeToEvents(bus, getApiTopic());
    }

    public void subscribeToEvents(MessageBus bus, Topic<TaskTemplateApi> commandsNotifierTopic) {
        bus.connect().subscribe(commandsNotifierTopic, new TaskTemplateApi() {
            @Override
            public void start() {
                executor.execute(() -> {
                    invokeWorkAndBlock();
                });
            }

            @Override
            public void stop() {
                cancel();
            }
        });
    }

    public void cancel() {
        workTask.cancel(true);
    }

    void execute(Runnable runnableWork) {
        setWork(runnableWork);
        invokeWorkAndBlock();
    }

    @SuppressWarnings("unchecked")
    <T> T execute(Callable<T> callableWork) {
        setWork(callableWork);
        return (T) invokeWorkAndBlock();
    }

    /**
     * Blocking call.
     *
     * @return the result of this work, if any.
     * @throws any exceptions thrown by the underlying invocation of the work.
     */
    public Object invokeWorkAndBlock() {
        result = null;
        exception = null;
        try {
            eventsNotifier.started();

            // start the work task, either runnable or callable
            if (runnableWork != null) {
                workTask = executor.submit(runnableWork);
            } else if (callableWork != null) {
                workTask = executor.submit(callableWork);
            }

            // wait for the work task to finish
            result = workTask.get(maxDurationMs, TimeUnit.MILLISECONDS);
            eventsNotifier.completed(result);
            return result;
        } catch (CancellationException e) {
            exception = e;
            eventsNotifier.cancelled(e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            exception = e;
            eventsNotifier.interrupted(e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            exception = e;
            eventsNotifier.error(e);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            exception = e;
            eventsNotifier.timedOut(e);
            throw new RuntimeException(e);
        }
    }

    void doRunnableWork() {
        // nothing by default
    }

    Object doCallableWork() {
        return null;
    }

    public Object getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

    public void setEventsNotifier(TaskTemplateEvents eventsNotifier) {
        this.eventsNotifier = eventsNotifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public interface TaskTemplateApi {
        default void start() {
        }

        default void stop() {
        }
    }

    public interface TaskTemplateEvents {
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
    }
}
package com.scarlatti.swingutils.flow;

public interface ActionNotifier {
    default void start() {
    }

    default void stop() {
    }

    default void pause() {
    }

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
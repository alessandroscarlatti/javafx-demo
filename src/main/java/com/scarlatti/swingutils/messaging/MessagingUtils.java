package com.scarlatti.swingutils.messaging;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/5/2019
 */
public class MessagingUtils {

    public static String createName(Object instance) {
        return instance.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(instance));
    }
}

package org.gradle.playframework.tools.internal.reflection;

/**
 * Thrown when a requested method cannot be found.
 */
public class NoSuchMethodException extends RuntimeException {
    public NoSuchMethodException(String message) {
        super(message);
    }
}

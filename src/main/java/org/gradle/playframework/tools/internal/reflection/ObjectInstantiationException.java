package org.gradle.playframework.tools.internal.reflection;

/**
 * Thrown when an object cannot be instantiated.
 */
public class ObjectInstantiationException extends RuntimeException {
    public ObjectInstantiationException(Class<?> targetType, Throwable throwable) {
        super(String.format("Could not create an instance of type %s.", targetType.getName()), throwable);
    }
}

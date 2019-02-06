package org.gradle.playframework.tools.internal.reflection;

public interface PropertyAccessor<T, F> {
    String getName();

    Class<F> getType();

    F getValue(T target);
}

package org.gradle.playframework.tools.internal.reflection;

/**
 * An object that can create new instances of a given type, which may be decorated in some fashion.
 */
public interface Instantiator {

    <T> T newInstance(Class<? extends T> type, Object... parameters) throws ObjectInstantiationException;

}

package org.gradle.playframework.tools.internal.scala;

import java.lang.reflect.Field;

public class ScalaObject {
    private final Object instance;
    private final Class<?> type;
    private final String className;

    public ScalaObject(ClassLoader classLoader, String className) {
        this.className = className;
        Class<?> baseClass = getClass(classLoader, className);
        final Field scalaObject = getModule(baseClass);
        instance = getInstance(scalaObject);
        type = scalaObject.getType();
    }

    private Object getInstance(Field scalaObject) {
        try {
            return scalaObject.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getClass(ClassLoader classLoader, String typeName) {
        try {
            return classLoader.loadClass(typeName + "$");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getModule(Class<?> baseClass) {
        try {
            return baseClass.getField("MODULE$");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getInstance() {
        return instance;
    }

    public Class<?> getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }
}
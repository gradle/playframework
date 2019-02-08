package org.gradle.playframework.tools.internal.scala;

import org.gradle.api.GradleException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScalaMethod {
    private final String description;
    private final Method method;
    private final Object instance;

    public ScalaMethod(ScalaObject scalaObject, String methodName, Class<?>... typeParameters) {
        description = scalaObject.getClassName() + "." + methodName + "()";
        instance = scalaObject.getInstance();
        method = getMethod(scalaObject.getType(), methodName, typeParameters);
    }

    public ScalaMethod(ClassLoader classLoader, String className, String methodName, Class<?>... typeParameters) {
        this(new ScalaObject(classLoader, className), methodName, typeParameters);
    }

    private Method getMethod(Class<?> type, String methodName, Class<?>[] typeParameters) {
        try {
            return type.getMethod(methodName, typeParameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object invoke(Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new GradleException(String.format("Could not invoke Scala method %s.", description), e);
        }
    }

}


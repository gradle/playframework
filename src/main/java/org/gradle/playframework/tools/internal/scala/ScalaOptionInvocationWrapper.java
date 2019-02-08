package org.gradle.playframework.tools.internal.scala;

import org.gradle.internal.Cast;

import java.lang.reflect.Method;

public class ScalaOptionInvocationWrapper<T> {
    private final Object obj;


    public ScalaOptionInvocationWrapper(Object obj) {
        this.obj = obj;
    }

    public boolean isDefined() {
        try {
            Method resultIsDefined = obj.getClass().getMethod("isDefined");
            return (Boolean) resultIsDefined.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T get() {
        try {
            return Cast.uncheckedCast(obj.getClass().getMethod("get").invoke(obj));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

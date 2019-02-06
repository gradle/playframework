package org.gradle.playframework.tools.internal.reflection;

import org.gradle.api.GradleException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JavaMethod<T, R> {
    private final Method method;
    private final Class<R> returnType;

    public JavaMethod(Class<T> target, Class<R> returnType, String name, boolean allowStatic, Class<?>... paramTypes) {
        this(returnType, findMethod(target, target, name, allowStatic, paramTypes));
    }

    public JavaMethod(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        this(target, returnType, name, false, paramTypes);
    }

    public JavaMethod(Class<R> returnType, Method method) {
        this.returnType = returnType;
        this.method = method;
        method.setAccessible(true);
    }

    private static Method findMethod(Class origTarget, Class target, String name, boolean allowStatic, Class<?>[] paramTypes) {
        for (Method method : target.getDeclaredMethods()) {
            if (!allowStatic && Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), paramTypes)) {
                return method;
            }
        }

        Class<?> parent = target.getSuperclass();
        if (parent == null) {
            String concatenatedParamTypes = Arrays.asList(paramTypes).stream().map(x -> x.toString()).collect(Collectors.joining(", "));
            throw new NoSuchMethodException(String.format("Could not find method %s(%s) on %s.", name, concatenatedParamTypes, origTarget.getSimpleName()));
        } else {
            return findMethod(origTarget, parent, name, allowStatic, paramTypes);
        }
    }

    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    public R invokeStatic(Object... args) {
        return invoke(null, args);
    }

    public R invoke(T target, Object... args) {
        try {
            Object result = method.invoke(target, args);
            return returnType.cast(result);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (Exception e) {
            throw new GradleException(String.format("Could not call %s.%s() on %s", method.getDeclaringClass().getSimpleName(), method.getName(), target), e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes(){
        return method.getParameterTypes();
    }
}
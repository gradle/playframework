package org.gradle.playframework.tools.internal.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DirectInstantiator implements Instantiator {

    public static final Instantiator INSTANCE = new DirectInstantiator();

    private final ConstructorCache constructorCache = new ConstructorCache();

    public static <T> T instantiate(Class<? extends T> type, Object... params) {
        return INSTANCE.newInstance(type, params);
    }

    private DirectInstantiator() {
    }

    public <T> T newInstance(Class<? extends T> type, Object... params) {
        try {
            Class<?>[] argTypes = wrapArgs(params);
            Constructor<?> match = null;
            while (match == null) {
                // we need to wrap this into a loop, because there's always a risk
                // that the method, which is weakly referenced, has been collected
                // in between the creation time and now
                match = constructorCache.get(type, argTypes).getMethod();
            }
            return type.cast(match.newInstance(params));
        } catch (InvocationTargetException e) {
            throw new ObjectInstantiationException(type, e.getCause());
        } catch (Throwable t) {
            throw new ObjectInstantiationException(type, t);
        }
    }

    private Class<?>[] wrapArgs(Object[] params) {
        Class<?>[] result = new Class<?>[params.length];
        for (int i = 0; i < result.length; i++) {
            Object param = params[i];
            if (param == null) {
                continue;
            }
            Class<?> pType = param.getClass();
            if (pType.isPrimitive()) {
                pType = JavaReflectionUtil.getWrapperTypeForPrimitiveType(pType);
            }
            result[i] = pType;
        }
        return result;
    }

    public static class ConstructorCache extends ReflectionCache<JavaReflectionUtil.CachedConstructor> {

        @Override
        protected JavaReflectionUtil.CachedConstructor create(Class<?> receiver, Class<?>[] argumentTypes) {
            Constructor<?>[] constructors = receiver.getConstructors();
            Constructor<?> match = null;
            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == argumentTypes.length) {
                    if (isMatch(argumentTypes, parameterTypes)) {
                        if (match != null) {
                            throw new IllegalArgumentException(String.format("Found multiple public constructors for %s which accept parameters [%s].", receiver, prettify(argumentTypes)));
                        }
                        match = constructor;
                    }
                }
            }
            if (match == null) {
                throw new IllegalArgumentException(String.format("Could not find any public constructor for %s which accepts parameters [%s].", receiver, prettify(argumentTypes)));
            }
            return new JavaReflectionUtil.CachedConstructor(match);
        }

        private String prettify(Class<?>[] argumentTypes) {
            return Arrays.asList(argumentTypes)
                    .stream()
                    .map(input -> {
                        if (input == null) {
                            return "null";
                        }
                        return input.getName();
                    })
                    .collect(Collectors.joining(", "));
        }

        private boolean isMatch(Class<?>[] argumentTypes, Class[] parameterTypes) {
            for (int i = 0; i < argumentTypes.length; i++) {
                Class<?> argumentType = argumentTypes[i];
                Class<?> parameterType = parameterTypes[i];
                boolean primitive = parameterType.isPrimitive();
                if (primitive) {
                    if (argumentType == null) {
                        return false;
                    }
                    parameterType = JavaReflectionUtil.getWrapperTypeForPrimitiveType(parameterType);
                }
                if (argumentType != null && !parameterType.isAssignableFrom(argumentType)) {
                    return false;
                }
            }
            return true;
        }
    }
}

package org.gradle.playframework.tools.internal.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class JavaReflectionUtil {

    private JavaReflectionUtil() {}

    public static <T, R> JavaMethod<T, R> method(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        return new JavaMethod<>(target, returnType, name, paramTypes);
    }

    public static <T, R> JavaMethod<T, R> method(T target, Class<R> returnType, String name, Class<?>... paramTypes) {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) target.getClass();
        return method(targetClass, returnType, name, paramTypes);
    }

    public static <T, F> PropertyAccessor<T, F> readableField(Class<T> target, Class<F> fieldType, String fieldName) {
        Field field = findField(target, fieldName);
        if (field == null) {
            throw new NoSuchPropertyException(String.format("Could not find field '%s' on class %s.", fieldName, target.getSimpleName()));
        }

        return new FieldBackedPropertyAccessor<>(fieldName, fieldType, field);
    }

    public static <T, F> PropertyAccessor<T, F> readableField(T target, Class<F> fieldType, String fieldName) {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) target.getClass();
        return readableField(targetClass, fieldType, fieldName);
    }

    public static <T, R> JavaMethod<T, R> staticMethod(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        return new JavaMethod<>(target, returnType, name, true, paramTypes);
    }

    public static Class<?> getWrapperTypeForPrimitiveType(Class<?> type) {
        if (type == Character.TYPE) {
            return Character.class;
        } else if (type == Boolean.TYPE) {
            return Boolean.class;
        } else if (type == Long.TYPE) {
            return Long.class;
        } else if (type == Integer.TYPE) {
            return Integer.class;
        } else if (type == Short.TYPE) {
            return Short.class;
        } else if (type == Byte.TYPE) {
            return Byte.class;
        } else if (type == Float.TYPE) {
            return Float.class;
        } else if (type == Double.TYPE) {
            return Double.class;
        }
        throw new IllegalArgumentException(String.format("Don't know the wrapper type for primitive type %s.", type));
    }

    public static <T> T newInstance(Class<T> c) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Field findField(Class<?> target, String fieldName) {
        Field[] fields = target.getFields();
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    private static class FieldBackedPropertyAccessor<T, F> implements PropertyAccessor<T, F> {
        private final String property;
        private final Field field;
        private final Class<F> fieldType;

        FieldBackedPropertyAccessor(String property, Class<F> fieldType, Field field) {
            this.property = property;
            this.field = field;
            this.fieldType = fieldType;
        }

        @Override
        public String getName() {
            return property;
        }

        @Override
        public Class<F> getType() {
            return fieldType;
        }

        @Override
        public F getValue(T target) {
            try {
                return fieldType.cast(field.get(target));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class CachedConstructor extends ReflectionCache.CachedInvokable<Constructor<?>> {
        public CachedConstructor(Constructor<?> ctor) {
            super(ctor);
        }
    }
}
package org.gradle.playframework.tools.internal.reflection;

import java.lang.reflect.Field;

public final class JavaReflectionUtil {

    private JavaReflectionUtil() {}

    /**
     * Locates the given method. Searches all methods, including private methods.
     *
     * @param target The target class
     * @param returnType The return type
     * @param paramTypes The parameter types
     * @return The Java method representation
     */
    public static <T, R> JavaMethod<T, R> method(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        return new JavaMethod<>(target, returnType, name, paramTypes);
    }

    /**
     * Locates the given method. Searches all methods, including private methods.
     *
     * @param target The target type
     * @param returnType The return type
     * @param paramTypes The Parameter types
     * @return The Java method representation
     */
    public static <T, R> JavaMethod<T, R> method(T target, Class<R> returnType, String name, Class<?>... paramTypes) {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) target.getClass();
        return method(targetClass, returnType, name, paramTypes);
    }

    /**
     * Locates the field with the given name as a readable property.  Searches only public fields.
     *
     * @param target The target class
     * @param fieldType The field type
     * @param fieldName The field name
     * @return The property accessor
     */
    public static <T, F> PropertyAccessor<T, F> readableField(Class<T> target, Class<F> fieldType, String fieldName) {
        Field field = findField(target, fieldName);
        if (field == null) {
            throw new NoSuchPropertyException(String.format("Could not find field '%s' on class %s.", fieldName, target.getSimpleName()));
        }

        return new FieldBackedPropertyAccessor<>(fieldName, fieldType, field);
    }

    /**
     * Locates the field with the given name as a readable property.  Searches only public fields.
     *
     * @param target The target type
     * @param fieldType The field type
     * @param fieldName The field name
     * @return The property accessor
     */
    public static <T, F> PropertyAccessor<T, F> readableField(T target, Class<F> fieldType, String fieldName) {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) target.getClass();
        return readableField(targetClass, fieldType, fieldName);
    }

    /**
     * Locates the given static method. Searches all methods, including private methods.
     *
     * @param target The target class
     * @param returnType The return type
     * @param name The method name
     * @param paramTypes The parameter types
     * @return The Java method representation
     */
    public static <T, R> JavaMethod<T, R> staticMethod(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        return new JavaMethod<>(target, returnType, name, true, paramTypes);
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
}
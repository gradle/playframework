package org.gradle.playframework.tools.internal.scala;

import org.gradle.playframework.tools.internal.reflection.JavaReflectionUtil;

import java.lang.reflect.Method;
import java.util.List;

public class ScalaListBuffer {
    public static <T> Object fromList(ClassLoader cl, List<T> list) {
        try {
            Class<?> bufferClass = cl.loadClass("scala.collection.mutable.ListBuffer");
            Object buffer = JavaReflectionUtil.newInstance(bufferClass);
            Method bufferPlusEq = bufferClass.getMethod("$plus$eq", Object.class);

            for (T elem : list) {
                bufferPlusEq.invoke(buffer, elem);
            }
            return buffer;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }
}

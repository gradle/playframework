package org.gradle.playframework.tools.internal.scala;

import org.gradle.playframework.tools.internal.reflection.JavaReflectionUtil;

import java.lang.reflect.Method;
import java.util.Collection;

public class ScalaSeq {
    public static <T> Object fromList(ClassLoader cl, Collection<T> list) {
        try {
            Class<?> bufferClass = cl.loadClass("scala.collection.mutable.ListBuffer");
            Object buffer = JavaReflectionUtil.newInstance(bufferClass);
            Method bufferPlusEq = bufferClass.getMethod("$plus$eq", Object.class);
            for (T elem : list) {
                bufferPlusEq.invoke(buffer, elem);
            }
            Method toList = bufferClass.getMethod("toList");
            return toList.invoke(buffer);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}

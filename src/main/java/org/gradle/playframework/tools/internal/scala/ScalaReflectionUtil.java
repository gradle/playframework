package org.gradle.playframework.tools.internal.scala;

public class ScalaReflectionUtil {
    public static ScalaMethod scalaMethod(ClassLoader classLoader, String typeName, String methodName, Class<?>... typeParameters) {
        return new ScalaMethod(classLoader, typeName, methodName, typeParameters);
    }
}

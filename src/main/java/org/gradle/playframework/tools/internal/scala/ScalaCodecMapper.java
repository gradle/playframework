package org.gradle.playframework.tools.internal.scala;

import java.io.Serializable;

public class ScalaCodecMapper implements Serializable {
    public static String getClassName() {
        return "scala.io.Codec";
    }

    public static Object create(ClassLoader cl, String codec) {
        ScalaMethod method = ScalaReflectionUtil.scalaMethod(cl, getClassName(), "apply", String.class);
        return method.invoke(codec);
    }
}

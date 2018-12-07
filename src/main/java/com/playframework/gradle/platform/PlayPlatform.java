package com.playframework.gradle.platform;

public interface PlayPlatform extends Platform {

    /**
     * Version of Play Framework to use
     * @return version of the Play Framework
     */
    String getPlayVersion();

    /**
     * Version of Scala Runtime to use.
     * @return version of the Scala runtime
     */
    ScalaPlatform getScalaPlatform();

    /**
     * Version of Java Runtime to use.
     * @return version of the Java runtime
     */
    JavaPlatform getJavaPlatform();
}


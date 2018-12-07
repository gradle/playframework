package com.playframework.gradle.platform;

import org.gradle.api.JavaVersion;

public class DefaultJavaPlatform implements JavaPlatform {
    private final String name;
    private JavaVersion targetCompatibility;

    public DefaultJavaPlatform(JavaVersion javaVersion) {
        this.name = generateName(javaVersion);
        this.targetCompatibility = javaVersion;
    }

    public static JavaPlatform current() {
        return new DefaultJavaPlatform(JavaVersion.current());
    }

    @Override
    public JavaVersion getTargetCompatibility() {
        return targetCompatibility;
    }

    @Override
    public String getDisplayName() {
        return "Java SE " + targetCompatibility.getMajorVersion();
    }

    @Override
    public String getName() {
        return name;
    }

    public String toString() {
        return getDisplayName();
    }

    @Override
    public void setTargetCompatibility(JavaVersion targetCompatibility) {
        this.targetCompatibility = targetCompatibility;
    }

    private static String generateName(JavaVersion javaVersion) {
        return "java" + javaVersion.getMajorVersion();
    }
}


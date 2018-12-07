package com.playframework.gradle.platform;

import org.gradle.api.JavaVersion;

public interface JavaPlatform extends Platform {
    JavaVersion getTargetCompatibility();
    void setTargetCompatibility(JavaVersion targetCompatibility);
}

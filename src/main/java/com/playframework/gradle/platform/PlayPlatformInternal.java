package com.playframework.gradle.platform;

public interface PlayPlatformInternal extends PlayPlatform {
    String getDependencyNotation(String playModule);
}

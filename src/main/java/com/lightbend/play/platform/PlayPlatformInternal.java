package com.lightbend.play.platform;

public interface PlayPlatformInternal extends PlayPlatform {
    String getDependencyNotation(String playModule);
}

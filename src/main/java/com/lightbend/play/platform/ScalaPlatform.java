package com.lightbend.play.platform;

import org.gradle.api.tasks.Input;

public interface ScalaPlatform extends Platform {
    @Input
    String getScalaVersion();

    @Input
    String getScalaCompatibilityVersion();
}

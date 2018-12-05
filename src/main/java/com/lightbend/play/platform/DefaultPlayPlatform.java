package com.lightbend.play.platform;

public class DefaultPlayPlatform implements PlayPlatformInternal {
    public final static String DEFAULT_PLAY_VERSION = "2.6.15";
    private final String playVersion;
    private final ScalaPlatform scalaPlatform;
    private final JavaPlatform javaPlatform;
    private final String name;

    public DefaultPlayPlatform(String name, String playVersion, ScalaPlatform scalaPlatform, JavaPlatform javaPlatform) {
        this.name = name;
        this.playVersion = playVersion;
        this.scalaPlatform = scalaPlatform;
        this.javaPlatform = javaPlatform;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return "Play Platform (Play " + playVersion + ", Scala: " + scalaPlatform.getScalaCompatibilityVersion() + ", Java: " + javaPlatform.getDisplayName() + ")";
    }

    @Override
    public String getPlayVersion() {
        return playVersion;
    }

    @Override
    public ScalaPlatform getScalaPlatform() {
        return scalaPlatform;
    }

    @Override
    public JavaPlatform getJavaPlatform() {
        return javaPlatform;
    }

    @Override
    public String getDependencyNotation(String playModule) {
        return "com.typesafe.play:" + playModule + "_" + scalaPlatform.getScalaCompatibilityVersion() + ":" + playVersion;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}


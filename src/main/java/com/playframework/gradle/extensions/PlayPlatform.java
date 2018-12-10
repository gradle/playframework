package com.playframework.gradle.extensions;

import org.gradle.api.JavaVersion;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.util.VersionNumber;

import javax.inject.Inject;

public class PlayPlatform {

    public static final String DEFAULT_PLAY_VERSION = "2.6.15";
    public static final String DEFAULT_SCALA_VERSION = "2.12";
    private final Property<String> playVersion;
    private final Property<String> scalaVersion;
    private final Property<JavaVersion> javaVersion;

    @Inject
    public PlayPlatform(ObjectFactory objectFactory) {
        playVersion = objectFactory.property(String.class);
        scalaVersion = objectFactory.property(String.class);
        javaVersion = objectFactory.property(JavaVersion.class);
    }

    public Property<String> getPlayVersion() {
        return playVersion;
    }

    public Property<String> getScalaVersion() {
        return scalaVersion;
    }

    public Provider<String> getScalaCompatibilityVersion() {
        return scalaVersion.map(s -> {
            VersionNumber versionNumber = VersionNumber.parse(s);
            return versionNumber.getMajor() + "." + versionNumber.getMinor();
        });
    }

    public Property<JavaVersion> getJavaVersion() {
        return javaVersion;
    }

    public Provider<String> getDependencyNotation(String playModule) {
        return getScalaCompatibilityVersion().map(s -> "com.typesafe.play:" + playModule + "_" + s + ":" + playVersion.get());
    }
}

package com.lightbend.play.extensions;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.jvm.platform.JavaPlatform;
import org.gradle.jvm.platform.internal.DefaultJavaPlatform;
import org.gradle.language.scala.ScalaPlatform;
import org.gradle.language.scala.internal.DefaultScalaPlatform;
import org.gradle.play.internal.DefaultPlayPlatform;
import org.gradle.play.platform.PlayPlatform;

public class Platform {

    private final Property<String> playVersion;
    private final Property<String> scalaVersion;
    private final Property<JavaVersion> javaVersion;

    public Platform(Project project) {
        playVersion = project.getObjects().property(String.class);
        scalaVersion = project.getObjects().property(String.class);
        javaVersion = project.getObjects().property(JavaVersion.class);
    }

    public Property<String> getPlayVersion() {
        return playVersion;
    }

    public Property<String> getScalaVersion() {
        return scalaVersion;
    }

    public Property<JavaVersion> getJavaVersion() {
        return javaVersion;
    }

    public PlayPlatform asPlayPlatform() {
        ScalaPlatform scalaPlatform = new DefaultScalaPlatform(scalaVersion.get());
        JavaPlatform javaPlatform = new DefaultJavaPlatform(javaVersion.get());
        String name = createName(playVersion.get(), scalaVersion.get(), javaPlatform.getDisplayName());
        return new DefaultPlayPlatform(name, playVersion.get(), scalaPlatform, javaPlatform);
    }

    private String createName(String playVersion, String scalaVersion, String javaVersion) {
        StringBuilder builder = new StringBuilder("play-");
        builder.append(playVersion);
        if (scalaVersion != null) {
            builder.append("-");
            builder.append(scalaVersion);
        }
        if (javaVersion != null) {
            builder.append("_");
            builder.append(javaVersion);
        }
        return builder.toString();
    }
}

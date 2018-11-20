package com.lightbend.play.plugins;

import org.gradle.api.JavaVersion;
import org.gradle.jvm.platform.JavaPlatform;
import org.gradle.jvm.platform.internal.DefaultJavaPlatform;
import org.gradle.language.scala.ScalaPlatform;
import org.gradle.language.scala.internal.DefaultScalaPlatform;
import org.gradle.play.internal.DefaultPlayPlatform;
import org.gradle.play.platform.PlayPlatform;

/**
 * Temporary class to resolve the Play platform configuration before exposing an extension to make it configurable.
 */
public final class PlayPlatformHelper {

    private PlayPlatformHelper() {}

    public static PlayPlatform createDefaultPlayPlatform() {
        String playVersion = DefaultPlayPlatform.DEFAULT_PLAY_VERSION;
        ScalaPlatform scalaPlatform = new DefaultScalaPlatform("2.11");
        JavaPlatform javaPlatform = new DefaultJavaPlatform(JavaVersion.current());
        return new DefaultPlayPlatform("play", playVersion, scalaPlatform, javaPlatform);
    }
}

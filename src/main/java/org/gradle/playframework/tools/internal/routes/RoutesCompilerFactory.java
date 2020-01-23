package org.gradle.playframework.tools.internal.routes;

import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;
import org.gradle.util.VersionNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutesCompilerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesCompilerFactory.class);

    public static RoutesCompiler create(PlayPlatform playPlatform) {
        return new RoutesCompiler(createAdapter(playPlatform));
    }

    public static VersionedRoutesCompilerAdapter createAdapter(PlayPlatform playPlatform) {
        String playVersion = playPlatform.getPlayVersion().get();
        String scalaVersion = playPlatform.getScalaCompatibilityVersion().get();
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
                return new RoutesCompilerAdapterV23X(playVersion);
            case PLAY_2_4_X:
                if (VersionNumber.parse(playVersion).getMicro() < 6 && !"2.10".equals(scalaVersion)) {
                    scalaVersion = "2.10";
                }
                return new RoutesCompilerAdapterV24X(playVersion, scalaVersion);
            case PLAY_2_5_X:
            case PLAY_2_6_X:
            case PLAY_2_7_X:
                return new RoutesCompilerAdapterV24X(playVersion, scalaVersion);
            default:
                throw new RuntimeException("Could not create routes compile spec for Play version: " + playVersion);
        }
    }
}

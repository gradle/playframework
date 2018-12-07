package com.playframework.gradle.tools.routes;

import com.playframework.gradle.extensions.PlayPlatform;
import com.playframework.gradle.extensions.internal.PlayMajorVersion;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.util.VersionNumber;

public class RoutesCompilerFactory {
    private static final Logger LOGGER = Logging.getLogger(RoutesCompilerFactory.class);

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
                    LOGGER.warn("Asked to use scala version " + scalaVersion
                            + " on play < 2.4.6. Will have to use the 2.10 routes compiler");
                    scalaVersion = "2.10";
                }
                return new RoutesCompilerAdapterV24X(playVersion, scalaVersion);
            case PLAY_2_5_X:
            case PLAY_2_6_X:
                return new RoutesCompilerAdapterV24X(playVersion, scalaVersion);
            default:
                throw new RuntimeException("Could not create routes compile spec for Play version: " + playVersion);
        }
    }
}

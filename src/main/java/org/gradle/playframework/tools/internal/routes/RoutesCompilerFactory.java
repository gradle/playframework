package org.gradle.playframework.tools.internal.routes;

import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;
import org.gradle.util.VersionNumber;

public class RoutesCompilerFactory {
    public static RoutesCompiler create(PlayPlatform playPlatform, String projectDir) {
        return new RoutesCompiler(createAdapter(playPlatform, projectDir));
    }

    public static VersionedRoutesCompilerAdapter createAdapter(PlayPlatform playPlatform, String projectDir) {
        String playVersion = playPlatform.getPlayVersion().get();
        String scalaVersion = playPlatform.getScalaCompatibilityVersion().get();
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
                return new RoutesCompilerAdapterV23X(playVersion, projectDir);
            case PLAY_2_4_X:
                if (VersionNumber.parse(playVersion).getMicro() < 6 && !"2.10".equals(scalaVersion)) {
                    scalaVersion = "2.10";
                }
                return new RoutesCompilerAdapterV24X(playVersion, scalaVersion);
            case PLAY_2_5_X:
            case PLAY_2_6_X:
                return new RoutesCompilerAdapterV24X(playVersion, scalaVersion);
            case PLAY_2_7_X:
            case PLAY_2_8_X:
            default:
                return new RoutesCompilerAdapterV27X(playVersion, scalaVersion, projectDir);
        }
    }
}

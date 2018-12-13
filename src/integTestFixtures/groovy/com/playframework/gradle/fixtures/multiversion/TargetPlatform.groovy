package com.playframework.gradle.fixtures.multiversion

import org.gradle.util.VersionNumber

import static com.playframework.gradle.extensions.PlayPlatform.DEFAULT_PLAY_VERSION
import static com.playframework.gradle.extensions.PlayPlatform.DEFAULT_SCALA_VERSION

enum TargetPlatform {

    PLAY_2_6(DEFAULT_PLAY_VERSION, DEFAULT_SCALA_VERSION),
    PLAY_2_5('2.5.18', '2.11'),
    PLAY_2_4('2.4.11', '2.11')

    private final VersionNumber playVersion
    private final VersionNumber scalaVersion

    private TargetPlatform(String playVersion, String scalaVersion) {
        this.playVersion = VersionNumber.parse(playVersion)
        this.scalaVersion = VersionNumber.parse(scalaVersion)
    }

    VersionNumber getPlayVersion() {
        playVersion
    }

    VersionNumber getScalaVersion() {
        scalaVersion
    }

    static TargetPlatform forPlayVersion(String playVersion) {
        VersionNumber parsedPlayVersion = VersionNumber.parse(playVersion)

        for (TargetPlatform targetPlatform : values()) {
            if (targetPlatform.playVersion.major == parsedPlayVersion.major
                && targetPlatform.playVersion.minor == parsedPlayVersion.minor) {
                return targetPlatform
            }
        }

        throw new IllegalArgumentException("Unknown Play version: $playVersion")
    }

    @Override
    String toString() {
        "[Play version = '${playVersion.toString()}', Scala version = '${scalaVersion.toString()}']"
    }
}

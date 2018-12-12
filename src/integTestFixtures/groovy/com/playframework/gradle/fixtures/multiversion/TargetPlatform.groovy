package com.playframework.gradle.fixtures.multiversion

import org.gradle.util.VersionNumber

import static com.playframework.gradle.extensions.PlayPlatform.DEFAULT_PLAY_VERSION
import static com.playframework.gradle.extensions.PlayPlatform.DEFAULT_SCALA_VERSION

final class TargetPlatform {

    public static final TargetPlatform PLAY_2_6 = new TargetPlatform(DEFAULT_PLAY_VERSION, DEFAULT_SCALA_VERSION)
    public static final TargetPlatform PLAY_2_5 = new TargetPlatform('2.5.18', '2.11')
    public static final TargetPlatform PLAY_2_4 = new TargetPlatform('2.4.11', '2.11')

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

    @Override
    String toString() {
        "[Play version = '${playVersion.toString()}', Scala version = '${scalaVersion.toString()}']"
    }
}

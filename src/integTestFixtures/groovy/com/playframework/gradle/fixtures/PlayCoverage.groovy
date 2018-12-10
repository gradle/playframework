package com.playframework.gradle.fixtures

import org.gradle.play.internal.DefaultPlayPlatform
import org.gradle.util.VersionNumber

final class PlayCoverage {

    private PlayCoverage() {}

    public static final List<VersionNumber> ALL_VERSIONS = ["2.5.18", DefaultPlayPlatform.DEFAULT_PLAY_VERSION].collect { VersionNumber.parse(it) }
    public static final VersionNumber DEFAULT_PLAY_VERSION = VersionNumber.parse(DefaultPlayPlatform.DEFAULT_PLAY_VERSION)
}

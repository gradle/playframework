package com.playframework.gradle.fixtures

import com.playframework.gradle.extensions.PlayPlatform
import org.gradle.util.VersionNumber

final class PlayCoverage {

    private PlayCoverage() {}

    public static final List<VersionNumber> ALL_VERSIONS = ["2.5.18", PlayPlatform.DEFAULT_PLAY_VERSION].collect { VersionNumber.parse(it) }
    public static final VersionNumber DEFAULT_PLAY_VERSION = VersionNumber.parse(PlayPlatform.DEFAULT_PLAY_VERSION)
}

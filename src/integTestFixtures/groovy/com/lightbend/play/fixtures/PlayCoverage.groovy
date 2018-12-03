package com.lightbend.play.fixtures

import org.gradle.play.internal.DefaultPlayPlatform
import org.gradle.util.VersionNumber

final class PlayCoverage {
    private PlayCoverage() {}

    public static final VersionNumber DEFAULT_PLAY_VERSION = VersionNumber.parse(DefaultPlayPlatform.DEFAULT_PLAY_VERSION)
}

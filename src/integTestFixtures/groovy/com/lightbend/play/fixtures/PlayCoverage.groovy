package com.lightbend.play.fixtures

import org.gradle.play.internal.DefaultPlayPlatform
import org.gradle.util.VersionNumber

class PlayCoverage {
    static final VersionNumber DEFAULT_PLAY_VERSION = VersionNumber.parse(DefaultPlayPlatform.DEFAULT_PLAY_VERSION)
}

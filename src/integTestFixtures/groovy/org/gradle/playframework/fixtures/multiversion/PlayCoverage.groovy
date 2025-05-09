package org.gradle.playframework.fixtures.multiversion

import org.gradle.playframework.util.VersionNumber

import static org.gradle.playframework.extensions.PlayPlatform.DEFAULT_PLAY_VERSION

final class PlayCoverage {

    private PlayCoverage() {}

    public static final VersionNumber DEFAULT = VersionNumber.parse(DEFAULT_PLAY_VERSION)
    public static final List<VersionNumber> ALL = [
            VersionNumber.parse('2.4.11'),
            VersionNumber.parse('2.5.19'),
            VersionNumber.parse('2.6.25'),
            VersionNumber.parse('2.7.5'),
            VersionNumber.parse('2.8.22'),
    ]
}

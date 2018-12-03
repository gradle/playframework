package com.lightbend.play

import org.gradle.util.VersionNumber

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

abstract class PlayMultiVersionIntegrationTest extends AbstractIntegrationTest {

    VersionNumber versionNumber = DEFAULT_PLAY_VERSION

    void configurePlayVersionInBuildScript() {
        buildFile << """
            play {
                platform {
                    playVersion = '${versionNumber.toString()}'
                }
            }
        """
    }
}

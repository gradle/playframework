package org.gradle.playframework

import org.gradle.playframework.fixtures.multiversion.PlayCoverage
import org.gradle.playframework.fixtures.multiversion.PlayMultiVersionRunner
import org.gradle.playframework.fixtures.multiversion.TargetCoverage
import org.gradle.playframework.fixtures.multiversion.TargetPlatform
import org.gradle.util.VersionNumber
import org.junit.runner.RunWith

@RunWith(PlayMultiVersionRunner)
@TargetCoverage({ PlayCoverage.ALL })
abstract class PlayMultiVersionIntegrationTest extends AbstractIntegrationTest {

    static TargetPlatform targetPlatform

    protected VersionNumber getPlayVersion() {
        targetPlatform.playVersion
    }

    protected VersionNumber getScalaVersion() {
        targetPlatform.scalaVersion
    }

    void configurePlayVersionInBuildScript() {
        buildFile << """
            play {
                platform {
                    playVersion = '${playVersion.toString()}'
                    scalaVersion = '${scalaVersion.toString()}'
                }
            }
        """
    }
}

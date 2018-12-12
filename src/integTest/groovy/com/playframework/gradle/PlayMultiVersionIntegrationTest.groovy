package com.playframework.gradle

import com.playframework.gradle.fixtures.multiversion.PlayCoverage
import com.playframework.gradle.fixtures.multiversion.PlayMultiVersionRunner
import com.playframework.gradle.fixtures.multiversion.TargetCoverage
import com.playframework.gradle.fixtures.multiversion.TargetPlatform
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

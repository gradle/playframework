package org.gradle.playframework.application.basic

import org.gradle.playframework.PlayMultiVersionApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.testkit.runner.BuildResult

class PlayBasicAppIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(playVersion)
    }

    def "does not emit deprecation warnings"() {
        expect:
        build("build", "--warning-mode=fail")
    }
}

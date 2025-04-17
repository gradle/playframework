package org.gradle.playframework.application.basic

import org.gradle.api.JavaVersion
import org.gradle.playframework.PlayMultiVersionApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.playframework.fixtures.app.PlayApp
import org.junit.jupiter.api.Assumptions

class PlayBasicAppIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(playVersion)
    }

    def "does not emit deprecation warnings"() {
        given:
        Assumptions.assumeTrue(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17))
        configurePlayApplication(version)

        expect:
        build("build", "--warning-mode=fail")

        where:
        version << getVersionsToTest()
    }
}

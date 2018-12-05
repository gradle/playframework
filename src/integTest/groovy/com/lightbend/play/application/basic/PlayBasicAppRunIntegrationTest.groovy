package com.lightbend.play.application.basic

import com.lightbend.play.application.PlayMultiVersionRunApplicationIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp

class PlayBasicAppRunIntegrationTest extends PlayMultiVersionRunApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(versionNumber)
    }

    def "can run play app"() {
        given:
        build("assemble")
        buildFile << """
            runPlay {
                httpPort = 0
            }
        """

        when:
        buildAndWait("runPlay")

        then:
        runningApp.verifyStarted()

        and:
        runningApp.verifyContent()

        // and:
        // stop build
    }
}

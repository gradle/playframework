package com.lightbend.play.application.basic

import com.lightbend.play.PlayMultiVersionApplicationIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp

class PlayBasicAppIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(versionNumber)
    }
}

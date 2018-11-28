package com.lightbend.play.application.basic

import com.lightbend.play.application.PlayApplicationPluginIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

class PlayBasicAppIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(DEFAULT_PLAY_VERSION)
    }
}

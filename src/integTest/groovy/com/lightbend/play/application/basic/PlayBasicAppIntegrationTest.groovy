package com.lightbend.play.application.basic

import com.lightbend.play.application.PlayApplicationPluginIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp

class PlayBasicAppIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp()
    }
}

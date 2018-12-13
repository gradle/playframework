package org.gradle.playframework.application.basic

import org.gradle.playframework.PlayMultiVersionApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.playframework.fixtures.app.PlayApp

class PlayBasicAppIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(playVersion)
    }
}

package com.playframework.gradle.application.basic

import com.playframework.gradle.PlayMultiVersionApplicationIntegrationTest
import com.playframework.gradle.fixtures.app.BasicPlayApp
import com.playframework.gradle.fixtures.app.PlayApp

class PlayBasicAppIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(versionNumber)
    }
}

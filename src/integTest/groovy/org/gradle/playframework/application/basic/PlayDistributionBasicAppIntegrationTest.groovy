package org.gradle.playframework.application.basic

import org.gradle.playframework.application.PlayDistributionApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.playframework.fixtures.app.PlayApp

class PlayDistributionBasicAppIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        return new BasicPlayApp(playVersion)
    }
}

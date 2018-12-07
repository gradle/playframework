package com.playframework.gradle.application.basic

import com.playframework.gradle.application.PlayDistributionApplicationIntegrationTest
import com.playframework.gradle.fixtures.app.BasicPlayApp
import com.playframework.gradle.fixtures.app.PlayApp

class PlayDistributionBasicAppIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        return new BasicPlayApp(versionNumber)
    }
}

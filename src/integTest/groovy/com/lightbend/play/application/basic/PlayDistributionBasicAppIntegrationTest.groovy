package com.lightbend.play.application.basic

import com.lightbend.play.application.PlayDistributionApplicationIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp

class PlayDistributionBasicAppIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        return new BasicPlayApp(versionNumber)
    }
}

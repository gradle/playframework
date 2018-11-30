package com.lightbend.play.application.dependencies

import com.lightbend.play.application.PlayDistributionApplicationIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayAppWithDependencies

class PlayDistributionAppWithDependenciesIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(versionNumber)
    }
}

package com.lightbend.play.application.dependencies

import com.lightbend.play.application.PlayApplicationPluginIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayAppWithDependencies

class PlayBinaryAppWithDependenciesIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(versionNumber)
    }
}

package com.lightbend.play.application.dependencies

import com.lightbend.play.application.PlayApplicationPluginIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayAppWithDependencies

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

class PlayBinaryAppWithDependenciesIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(DEFAULT_PLAY_VERSION)
    }
}

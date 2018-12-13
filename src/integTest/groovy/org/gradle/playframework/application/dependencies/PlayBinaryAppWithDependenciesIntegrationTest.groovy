package org.gradle.playframework.application.dependencies

import org.gradle.playframework.application.PlayApplicationPluginIntegrationTest
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayAppWithDependencies

class PlayBinaryAppWithDependenciesIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(playVersion)
    }
}

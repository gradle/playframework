package com.playframework.gradle.application.dependencies

import com.playframework.gradle.application.PlayApplicationPluginIntegrationTest
import com.playframework.gradle.fixtures.app.PlayApp
import com.playframework.gradle.fixtures.app.PlayAppWithDependencies

class PlayBinaryAppWithDependenciesIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(playVersion)
    }
}

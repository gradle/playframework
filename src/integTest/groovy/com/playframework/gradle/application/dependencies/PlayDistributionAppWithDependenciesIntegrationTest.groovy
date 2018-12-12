package com.playframework.gradle.application.dependencies

import com.playframework.gradle.application.PlayDistributionApplicationIntegrationTest
import com.playframework.gradle.fixtures.app.PlayApp
import com.playframework.gradle.fixtures.app.PlayAppWithDependencies

class PlayDistributionAppWithDependenciesIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(playVersion)
    }
}

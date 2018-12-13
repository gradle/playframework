package org.gradle.playframework.application.dependencies

import org.gradle.playframework.application.PlayDistributionApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayAppWithDependencies

class PlayDistributionAppWithDependenciesIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(playVersion)
    }
}

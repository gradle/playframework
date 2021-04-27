package org.gradle.playframework.application.dependencies

import org.gradle.playframework.application.PlayDistributionApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayAppWithDependenciesNoPrefix

class PlayDistributionAppWithDependenciesNoPrefixIntegrationTest extends PlayDistributionApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependenciesNoPrefix(playVersion)
    }

    @Override
    protected boolean shouldPrefixDependencies() {
        return false
    }
}

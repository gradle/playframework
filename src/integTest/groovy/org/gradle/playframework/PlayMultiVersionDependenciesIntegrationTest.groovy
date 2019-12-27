package org.gradle.playframework

import static org.gradle.playframework.fixtures.Repositories.playRepositories

abstract class PlayMultiVersionDependenciesIntegrationTest extends PlayMultiVersionIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
            }
            
            ${playRepositories()}
        """
        configurePlayVersionInBuildScript()
    }
}

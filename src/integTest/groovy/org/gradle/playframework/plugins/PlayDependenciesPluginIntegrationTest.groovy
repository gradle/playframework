package org.gradle.playframework.plugins

import org.gradle.playframework.PlayMultiVersionDependenciesIntegrationTest

import static org.gradle.playframework.fixtures.Repositories.playRepositories

class PlayDependenciesPluginIntegrationTest extends PlayMultiVersionDependenciesIntegrationTest {

    def "will configure dependencies"() {
        given:
        buildFile << """
            dependencies {
                implementation playDep.json()
            }
        """
        when:
        build("dependencies")
        then:
        true
    }

}

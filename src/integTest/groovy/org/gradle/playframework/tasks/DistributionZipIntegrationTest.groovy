package org.gradle.playframework.tasks

import org.gradle.playframework.AbstractIntegrationTest

import static org.gradle.playframework.fixtures.Repositories.playRepositories

class DistributionZipIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        settingsFile << """ rootProject.name = 'dist-play-app' """
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
            }

            ${playRepositories()}
        """
    }

    def "can add to default distribution" () {
        buildFile << """
            distributions {
                main {
                    contents {
                        from "additionalFile.txt"
                    }
                }
            }
        """
        file("additionalFile.txt").createNewFile()

        when:
        build "dist"

        then:
        zip("build/distributions/main.zip").containsDescendants("main/additionalFile.txt")
    }
}

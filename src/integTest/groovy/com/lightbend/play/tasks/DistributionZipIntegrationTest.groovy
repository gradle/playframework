package com.lightbend.play.tasks

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.fixtures.Repositories.playRepositories

class DistributionZipIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        settingsFile << """ rootProject.name = 'dist-play-app' """
        buildFile << """
            plugins {
                id 'com.lightbend.play'
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

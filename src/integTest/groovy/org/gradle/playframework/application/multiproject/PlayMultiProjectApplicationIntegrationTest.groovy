package org.gradle.playframework.application.multiproject

import org.gradle.playframework.AbstractIntegrationTest
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayMultiProject
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

class PlayMultiProjectApplicationIntegrationTest extends AbstractIntegrationTest {

    PlayApp playApp = new PlayMultiProject()

    def setup() {
        playApp.writeSources(projectDir)
    }

    def "can build play app binary"() {
        when:
        BuildResult result = build(":primary:assemble")

        then:
        result.task(":javalibrary:jar").outcome == TaskOutcome.SUCCESS
        result.task(":primary:assemble").outcome == TaskOutcome.SUCCESS

        and:
        jar("primary/build/libs/primary.jar").containsDescendants(
                "router/Routes.class",
                "controllers/Application.class")
        jar("primary/build/libs/primary-assets.jar").hasDescendants(
                "public/primary.txt")
        jar("submodule/build/libs/submodule.jar").containsDescendants(
                "controllers/submodule/Application.class")
        jar("submodule/build/libs/submodule-assets.jar").hasDescendants(
                "public/submodule.txt")
    }
}

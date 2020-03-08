package org.gradle.playframework.tasks

import org.gradle.playframework.fixtures.archive.JarTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.plugins.PlayWebJarsPlugin.WEBJARS_EXTRACT_TASK_NAME

class WebJarsExtractIntegrationTest extends AbstractAssetsTaskIntegrationTest {
    private static final WEBJARS_EXTRACT_TASK_PATH = ":$WEBJARS_EXTRACT_TASK_NAME".toString()

    def setup() {
        settingsFile << """ rootProject.name = 'webjars-play-app' """

        buildFile << """
            plugins {
                id 'org.gradle.playframework'
                id 'org.gradle.playframework-webjars'
            }

            ${playRepositories()}

            dependencies {
                webJar 'org.webjars.bower:css-reset:2.5.1'
            }
        """
    }

    def "extracts WebJars as part of Play application build"() {
        when:
        BuildResult result = build "assemble"

        then:
        result.task(WEBJARS_EXTRACT_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        assetsJar.containsDescendants(
                "public/lib/css-reset/reset.css"
        )
    }

    def "does not reextract when outputs are unchanged"() {
        given:
        build "assemble"

        when:
        BuildResult result = build "assemble"

        then:
        result.task(WEBJARS_EXTRACT_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    def "reextracts when an output is removed" () {
        given:
        build "assemble"

        when:
        extractedWebJar("lib/css-reset/reset.css").delete()
        assetsJar.file.delete()
        BuildResult result = build "assemble"

        then:
        result.task(WEBJARS_EXTRACT_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
    }

    JarTestFixture getAssetsJar() {
        jar("build/libs/webjars-play-app-assets.jar")
    }

    File extractedWebJar(String fileName) {
        new File(file("build/src/play/webJars"), fileName)
    }
}

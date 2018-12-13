package org.gradle.playframework.tasks

import org.gradle.playframework.AbstractIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME

class PlayAssetsJarIntegrationTest extends AbstractIntegrationTest {

    private static final JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()
    private static final ASSETS_JAR_FILE_PATH = 'build/libs/play-app-assets.jar'

    def setup() {
        new BasicPlayApp().writeSources(projectDir)
        settingsFile << "rootProject.name = 'play-app'"

        when:
        BuildResult result = build('assemble')

        then:
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        file(ASSETS_JAR_FILE_PATH).isFile()
        jar(ASSETS_JAR_FILE_PATH).containsDescendants(
                'public/images/favicon.svg',
                'public/stylesheets/main.css',
                'public/javascripts/hello.js')
    }

    def "does not rebuild when public assets remain unchanged"() {
        when:
        BuildResult result = build('assemble')

        then:
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    def "rebuilds when public assets change"() {
        when:
        file('public/stylesheets/main.css') << '\n'
        BuildResult result = build('assemble')

        then:
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS

        and:
        jar(ASSETS_JAR_FILE_PATH).assertFileContent('public/stylesheets/main.css', file('public/stylesheets/main.css').text)
    }

    def "rebuilds when public assets are removed" () {
        when:
        file('public/stylesheets/main.css').delete()
        BuildResult result = build('assemble')

        then:
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS

        and:
        jar(ASSETS_JAR_FILE_PATH).countFiles('public/stylesheets/main.css') == 0
    }
}

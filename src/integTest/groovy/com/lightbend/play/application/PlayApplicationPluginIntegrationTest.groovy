package com.lightbend.play.application

import com.lightbend.play.AbstractIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.lightbend.play.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayApplicationPlugin.JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

abstract class PlayApplicationPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        getPlayApp().writeSources(projectDir)
        settingsFile << "rootProject.name = 'play-app'"
    }

    def "can build application binaries"() {
        when:
        BuildResult result = build('assemble')

        then:
        buildTasks.each { taskPath ->
            assert result.task(taskPath).outcome == TaskOutcome.SUCCESS
        }

        and:
        verifyJars()

        when:
        result = build('assemble')

        then:
        result.task(":$TWIRL_COMPILE_TASK_NAME".toString()).outcome == TaskOutcome.UP_TO_DATE
        result.task(":$JAR_TASK_NAME".toString()).outcome == TaskOutcome.UP_TO_DATE
        result.task(":$ASSETS_JAR_TASK_NAME".toString()).outcome == TaskOutcome.UP_TO_DATE
    }

    static String[] getBuildTasks() {
        [
            ':compilePlayRoutes',
            ':compilePlayTwirlTemplates',
            ':compileScala',
            ':createPlayJar',
            ':createPlayAssetsJar',
            ':assemble'
        ]
    }

    void verifyJars() {
        jar('build/libs/play-app.jar').containsDescendants(
                'router/Routes.class',
                'views/html/index.class',
                'views/html/main.class',
                'controllers/Application.class',
                'application.conf',
                'logback.xml')
        jar('build/libs/play-app-assets.jar').containsDescendants(
                'public/images/favicon.svg',
                'public/stylesheets/main.css',
                'public/javascripts/hello.js')
    }

    abstract PlayApp getPlayApp()
}

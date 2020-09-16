package org.gradle.playframework.application

import org.gradle.playframework.PlayMultiVersionApplicationIntegrationTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static org.gradle.playframework.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static org.gradle.playframework.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME
import static org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME

abstract class PlayApplicationPluginIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    private static final String SCALA_COMPILE_TASK_PATH = ':compileScala'
    private static final String JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final String ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()
    private static final String ASSEMBLE_TASK_PATH = ":$ASSEMBLE_TASK_NAME".toString()

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
        result.task(TWIRL_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    String[] getBuildTasks() {
        [
            ROUTES_COMPILE_TASK_PATH,
            TWIRL_COMPILE_TASK_PATH,
            SCALA_COMPILE_TASK_PATH,
            JAR_TASK_PATH,
            ASSETS_JAR_TASK_PATH,
            ASSEMBLE_TASK_PATH
        ]
    }

    void verifyJars() {
        if (playVersion.major == 2 && playVersion.minor == 3) {
            jar("build/libs/${playApp.name}.jar").containsDescendants(
                    'Routes.class',
                    'views/html/index.class',
                    'views/html/main.class',
                    'controllers/Application.class',
                    'application.conf',
                    'logback.xml')
        } else {
            jar("build/libs/${playApp.name}.jar").containsDescendants(
                    'router/Routes.class',
                    'views/html/index.class',
                    'views/html/main.class',
                    'controllers/Application.class',
                    'application.conf',
                    'logback.xml')
        }
        jar("build/libs/${playApp.name}-assets.jar").containsDescendants(
                'public/images/favicon.svg',
                'public/stylesheets/main.css',
                'public/javascripts/hello.js')
    }
}

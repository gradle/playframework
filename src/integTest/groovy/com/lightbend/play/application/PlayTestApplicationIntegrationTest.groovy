package com.lightbend.play.application

import com.lightbend.play.AbstractIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.test.JUnitXmlTestExecutionResult
import com.lightbend.play.fixtures.test.TestExecutionResult
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.lightbend.play.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayApplicationPlugin.JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME
import static org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME

abstract class PlayTestApplicationIntegrationTest extends AbstractIntegrationTest {

    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    private static final SCALA_COMPILE_TASK_NAME = 'compileScala'
    private static final SCALA_COMPILE_TASK_PATH = ":$SCALA_COMPILE_TASK_NAME".toString()
    private static final JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()
    private static final SCALA_TEST_COMPILE_TASK_NAME = 'compileTestScala'
    private static final SCALA_TEST_COMPILE_TASK_PATH = ":$SCALA_TEST_COMPILE_TASK_NAME".toString()
    private static final TEST_TASK_PATH = ":$TEST_TASK_NAME".toString()

    def setup() {
        getPlayApp().writeSources(projectDir)
        settingsFile << "rootProject.name = 'play-app'"
    }

    def "can run play app tests"() {
        when:
        BuildResult result = build("check")
        then:
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(TWIRL_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(SCALA_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        // TODO: Identify why the main JAR and the assets JAR is needed
        //result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        //result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(SCALA_TEST_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(TEST_TASK_PATH).outcome == TaskOutcome.SUCCESS

        then:
        verifyTestOutput(new JUnitXmlTestExecutionResult(projectDir))

        when:
        result = build("check")
        then:
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(TWIRL_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(SCALA_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        //result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        //result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(SCALA_TEST_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(TEST_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    void verifyTestOutput(TestExecutionResult result) { }
    abstract PlayApp getPlayApp()
}

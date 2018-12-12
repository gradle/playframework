package com.playframework.gradle.application

import com.playframework.gradle.PlayMultiVersionApplicationIntegrationTest
import com.playframework.gradle.fixtures.test.JUnitXmlTestExecutionResult
import com.playframework.gradle.fixtures.test.TestExecutionResult
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.playframework.gradle.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static com.playframework.gradle.plugins.PlayTestPlugin.TEST_SCALA_COMPILE_TASK_NAME
import static com.playframework.gradle.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME
import static org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME

abstract class PlayTestApplicationIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    private static final SCALA_COMPILE_TASK_NAME = 'compileScala'
    private static final SCALA_COMPILE_TASK_PATH = ":$SCALA_COMPILE_TASK_NAME".toString()
    private static final SCALA_TEST_COMPILE_TASK_PATH = ":$TEST_SCALA_COMPILE_TASK_NAME".toString()
    private static final TEST_TASK_PATH = ":$TEST_TASK_NAME".toString()

    def "can run play app tests"() {
        when:
        BuildResult result = build("check")
        then:
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(TWIRL_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(SCALA_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
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
        result.task(SCALA_TEST_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(TEST_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    void verifyTestOutput(TestExecutionResult result) { }
}

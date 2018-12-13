package org.gradle.playframework.application.basic

import org.gradle.playframework.application.PlayTestApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.test.TestExecutionResult

class PlayTestBasicAppIntegrationTest extends PlayTestApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(playVersion)
    }

    @Override
    void verifyTestOutput(TestExecutionResult result) {
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
    }
}
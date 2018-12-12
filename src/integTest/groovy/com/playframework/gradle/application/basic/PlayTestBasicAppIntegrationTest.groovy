package com.playframework.gradle.application.basic

import com.playframework.gradle.application.PlayTestApplicationIntegrationTest
import com.playframework.gradle.fixtures.app.BasicPlayApp
import com.playframework.gradle.fixtures.app.PlayApp
import com.playframework.gradle.fixtures.test.TestExecutionResult

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
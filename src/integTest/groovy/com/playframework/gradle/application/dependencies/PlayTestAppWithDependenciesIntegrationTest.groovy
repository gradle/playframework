package com.playframework.gradle.application.dependencies

import com.playframework.gradle.application.PlayTestApplicationIntegrationTest
import com.playframework.gradle.fixtures.app.PlayApp
import com.playframework.gradle.fixtures.app.PlayAppWithDependencies
import com.playframework.gradle.fixtures.test.TestExecutionResult

class PlayTestAppWithDependenciesIntegrationTest extends PlayTestApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(playVersion)
    }

    @Override
    void verifyTestOutput(TestExecutionResult result) {
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
    }
}
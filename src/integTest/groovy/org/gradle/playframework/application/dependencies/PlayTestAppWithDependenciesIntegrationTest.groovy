package org.gradle.playframework.application.dependencies

import org.gradle.playframework.application.PlayTestApplicationIntegrationTest
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayAppWithDependencies
import org.gradle.playframework.fixtures.test.TestExecutionResult

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
package com.lightbend.play.application.dependencies

import com.lightbend.play.application.PlayTestApplicationIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayAppWithDependencies
import com.lightbend.play.fixtures.test.TestExecutionResult

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

class PlayTestAppWithDependenciesIntegrationTest extends PlayTestApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(DEFAULT_PLAY_VERSION)
    }

    @Override
    void verifyTestOutput(TestExecutionResult result) {
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
    }
}
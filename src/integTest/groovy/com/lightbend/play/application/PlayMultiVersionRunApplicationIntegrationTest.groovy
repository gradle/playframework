package com.lightbend.play.application

import com.lightbend.play.PlayMultiVersionApplicationIntegrationTest
import com.lightbend.play.fixtures.app.RunningPlayApp
import com.lightbend.play.fixtures.wait.ConcurrentTestUtil
import com.lightbend.play.fixtures.wait.ExecutionOutput
import com.lightbend.play.fixtures.wait.TestOutputStream
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.DefaultGradleRunner
import org.hamcrest.Matcher

import static org.hamcrest.Matchers.containsString

abstract class PlayMultiVersionRunApplicationIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    int buildTimeout = 20
    Writer stderr = new OutputStreamWriter(new TestOutputStream())
    Writer stdout = new OutputStreamWriter(new TestOutputStream())
    RunningPlayApp runningApp

    def setup() {
        runningApp = new RunningPlayApp(projectDir)
        buildFile << """
            play {
                platform {
                    scalaVersion.set('2.12')
                }
            }
        """
    }

    protected BuildResult buildAndWait(String... arguments) {
        BuildResult result = (DefaultGradleRunner)runWithCapturedOutput(arguments).build()
        waitForBuild("Running Play App")
        result
    }

    private GradleRunner runWithCapturedOutput(String... arguments) {
        createAndConfigureGradleRunner(arguments).forwardStdOutput(stdout).forwardStdError(stderr)
    }

    private ExecutionOutput waitForBuild(String message) {
        waitUntilOutputContains(containsString(message))
    }

    private ExecutionOutput waitUntilOutputContains(Matcher<String> expectedMatcher) {
        boolean success = false
        long pollingStartNanos = System.nanoTime()
        try {
            ConcurrentTestUtil.poll(buildTimeout, 0.5) {
                def out = stdout.toString()
                assert expectedMatcher.matches(out)
            }
            success = true
        } catch (Throwable t) {
            throw new RuntimeException("Timeout waiting for build to complete.", t)
        } finally {
            if (!success) {
                println "Polling lasted ${(long) ((System.nanoTime() - pollingStartNanos) / 1000000L)} ms measured with monotonic clock"
                println "Output"
                println "-----------"
                println stdout.toString()
                println stderr.toString()
                println "-----------"
            }
        }

        def executionOutput = new ExecutionOutput(stdout.toString(), stderr.toString())
        stdout.reset()
        stderr.reset()
        return executionOutput
    }
}

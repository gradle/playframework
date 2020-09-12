package org.gradle.playframework

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.withType


class TestSetupPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        tasks.withType<Test>().configureEach {
            // Log test execution so that Travis CI doesn't time out
            if (System.getenv("CI") != null) {
                // Enable detailed logging when running in Travis CI,
                // so that we can understand why a build failed
                // From: https://stackoverflow.com/a/47458666
                testLogging {
                    events("started", "passed", "skipped", "failed")
                    showStackTraces = true
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }

            maxParallelForks = determineMaxParallelForks()
        }
    }

    private
    fun determineMaxParallelForks(): Int {
        return if ((Runtime.getRuntime().availableProcessors() / 2) < 1) 1 else (Runtime.getRuntime().availableProcessors() / 2)
    }
}

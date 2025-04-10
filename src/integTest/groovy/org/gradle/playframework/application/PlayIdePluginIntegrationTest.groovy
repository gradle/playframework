package org.gradle.playframework.application

import org.gradle.playframework.PlayMultiVersionApplicationIntegrationTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

abstract class PlayIdePluginIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    abstract String getIdePlugin()
    abstract String getIdeTask()
    abstract List<File> getIdeFiles()
    abstract String[] getBuildTasks()
    abstract String[] getUnexecutedTasks()

    def "generates IDE configuration"() {given:
        configurePlayApplication(version)

        applyIdePlugin()
        when:
        BuildResult result = build(ideTask)
        then:
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.SUCCESS
        }
        unexecutedTasks.each {
            assert !result.task(it)
        }
        ideFiles.each {
            assert it.exists()
        }

        where:
        version << getVersionsToTest()
    }

    def "does not blow up when no IDE plugin is applied"() {
        given:
        configurePlayApplication(version)

        expect:
        build("tasks")

        where:
        version << getVersionsToTest()
    }

    protected void applyIdePlugin() {
        buildFile << """
    allprojects {
        apply plugin: "${idePlugin}"
    }
"""
    }
}

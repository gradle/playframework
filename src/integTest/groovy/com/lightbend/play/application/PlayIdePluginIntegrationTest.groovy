package com.lightbend.play.application

import com.lightbend.play.AbstractIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.util.VersionNumber

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

abstract class PlayIdePluginIntegrationTest extends AbstractIntegrationTest {

    abstract String getIdePlugin()
    abstract String getIdeTask()
    abstract List<File> getIdeFiles()
    abstract String[] getBuildTasks()
    abstract PlayApp getPlayApp()

    VersionNumber versionNumber = DEFAULT_PLAY_VERSION

    def setup() {
        playApp.writeSources(projectDir)
        settingsFile << """
            rootProject.name = '${playApp.name}'
        """
        buildFile << """
            play {
                platform {
                    playVersion = '${versionNumber.toString()}'
                }
            }
        """
    }

    def "generates IDE configuration"() {
        applyIdePlugin()
        when:
        BuildResult result = build(ideTask)
        then:
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.SUCCESS
        }
        ideFiles.each {
            assert it.exists()
        }
    }

    def "does not blow up when no IDE plugin is applied"() {
        expect:
        build("tasks")
    }

    protected void applyIdePlugin() {
        buildFile << """
    allprojects {
        apply plugin: "${idePlugin}"
    }
"""
    }
}

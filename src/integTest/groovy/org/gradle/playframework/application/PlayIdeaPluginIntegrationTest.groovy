package org.gradle.playframework.application


import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.fixtures.ide.IdeaFixtures.parseIml

abstract class PlayIdeaPluginIntegrationTest extends PlayIdePluginIntegrationTest {

    String getIdePlugin() {
        "idea"
    }

    String getIdeTask() {
        "idea"
    }

    File getModuleFile() {
        file("${playApp.name}.iml")
    }

    File getProjectFile() {
        file("${playApp.name}.ipr")
    }

    File getWorkspaceFile() {
        file("${playApp.name}.iws")
    }

    List<File> getIdeFiles() {
        [moduleFile,
         projectFile,
         workspaceFile]
    }

    abstract String[] getSourcePaths()

    def "IML contains path to Play app sources"() {
        given:
        playVersion = version
        setupBuildFile()
        configurePlayVersionInBuildScript()

        applyIdePlugin()

        when:
        build(ideTask)
        then:
        def content = parseIml(moduleFile).content
        content.assertContainsSourcePaths(sourcePaths)
        content.assertContainsResourcePaths("conf")
        content.assertContainsExcludes("build", ".gradle")

        where:
        version << getVersionsToTest()
    }

    def "IDEA metadata contains correct Java version"() {
        given:
        playVersion = version
        setupBuildFile()
        configurePlayVersionInBuildScript()

        applyIdePlugin()
        buildFile << """
    allprojects {
        pluginManager.withPlugin("org.gradle.playframework") {
            tasks.idea {
                doLast {
                    assert ideaModule.module.targetBytecodeVersion == play.platform.javaVersion.get()
                    assert ideaModule.module.languageLevel == new org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(play.platform.javaVersion.get().toString())
                    println "Validated Java Version"
                }
            }
        }
    }
"""
        when:
        BuildResult result = build(ideTask)
        then:
        result.output.contains("Validated Java Version")

        where:
        version << getVersionsToTest()
    }

    def "IDEA metadata contains correct dependencies for RUNTIME, COMPILE, TEST"() {
        given:
        playVersion = version
        setupBuildFile()
        configurePlayVersionInBuildScript()

        applyIdePlugin()

        when:
        build(ideTask)
        then:
        def externalLibs = parseIml(moduleFile).dependencies.libraries
        def compileDeps = externalLibs.findAll({ it.scope == "COMPILE" }).collect { it.url }
        !compileDeps.empty

        def runtimeDeps = externalLibs.findAll({ it.scope == "RUNTIME" })
        !runtimeDeps.empty

        def testDeps = externalLibs.findAll({ it.scope == "TEST" })
        !testDeps.empty

        where:
        version << getVersionsToTest()
    }

    def "IDEA plugin depends on source generation tasks"() {
        given:
        playVersion = version
        setupBuildFile()
        configurePlayVersionInBuildScript()

        applyIdePlugin()

        when:
        BuildResult result = build(ideTask)
        then:
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.SUCCESS
        }

        where:
        version << getVersionsToTest()
    }

    def "can modify source directories"() {
        given:
        playVersion = version
        setupBuildFile()
        configurePlayVersionInBuildScript()

        applyIdePlugin()
        buildFile << """
            allprojects {
                File customDir = file('custom')
                Set<File> allSourceDirs = ideaModule.module.sourceDirs
                allSourceDirs.add(customDir)
                ideaModule.module.sourceDirs = allSourceDirs

                tasks.idea {
                    doLast {
                        assert ideaModule.module.sourceDirs.contains(customDir)
                    }
                }
            }
        """

        when:
        BuildResult result = build(ideTask)

        then:
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.SUCCESS
        }

        where:
        version << getVersionsToTest()
    }
}

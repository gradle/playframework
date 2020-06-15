package org.gradle.playframework.application

import org.gradle.playframework.fixtures.ide.IdeaModuleFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Ignore

import static org.gradle.playframework.fixtures.ide.IdeaFixtures.parseIml
import static org.gradle.playframework.fixtures.ide.IdeaFixtures.parseIpr

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
    abstract int getExpectedScalaClasspathSize()

    def "IML contains path to Play app sources"() {
        applyIdePlugin()

        when:
        build(ideTask)
        then:
        def content = parseIml(moduleFile).content
        content.assertContainsSourcePaths(sourcePaths)
        content.assertContainsResourcePaths("conf")
        content.assertContainsExcludes("build", ".gradle")
    }

    @Ignore
    def "IDEA metadata contains correct Scala version"() {
        applyIdePlugin()
        buildFile << """
    allprojects {
        pluginManager.withPlugin("org.gradle.playframework") {
            tasks.idea {
                doLast {
                    assert ideaModule.module.scalaPlatform.getScalaCompatibilityVersion() == play.platform.scalaVersion.get()
                    println "Validated Scala Version"
                }
            }
        }
    }
"""
        when:
        BuildResult result = build(ideTask)
        then:
        result.output.contains("Validated Scala Version")

        parseIml(moduleFile).dependencies.dependencies.any {
            if (it instanceof IdeaModuleFixture.ImlLibrary) {
                return it.name.startsWith("scala-sdk") && it.level == "project"
            }
            false
        }

        def libraryTable = parseIpr(projectFile).libraryTable
        def scalaSdk = libraryTable.library.find { it.@name.toString().startsWith("scala-sdk") && it.@type == "Scala" }
        def scalaClasspath = scalaSdk.properties."compiler-classpath".root."@url"
        scalaClasspath.size() == expectedScalaClasspathSize
    }

    def "IDEA metadata contains correct Java version"() {
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
    }

    def "IDEA metadata contains correct dependencies for RUNTIME, COMPILE, TEST"() {
        applyIdePlugin()
        build("assemble") // Need generated directories to exist
        when:
        build(ideTask)
        then:

        def externalLibs = parseIml(moduleFile).dependencies.libraries
        def compileDeps = externalLibs.findAll({ it.scope == "COMPILE" }).collect { it.url }
        compileDeps.any {
            it.endsWith("build/classes/scala/main")
        }

        def runtimeDeps = externalLibs.findAll({ it.scope == "RUNTIME" })
        !runtimeDeps.empty

        def testDeps = externalLibs.findAll({ it.scope == "TEST" })
        !testDeps.empty
    }

    def "IDEA plugin depends on source generation tasks"() {
        applyIdePlugin()

        when:
        BuildResult result = build(ideTask)
        then:
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.SUCCESS
        }
    }

    def "can modify source directories"() {
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
    }
}

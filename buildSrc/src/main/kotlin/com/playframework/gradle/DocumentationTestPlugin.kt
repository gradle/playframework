package com.playframework.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*

class DocumentationTestPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val sourceSets = the<SourceSetContainer>()
        val testRuntimeClasspath by configurations

        val docTestSourceSet = sourceSets.create("docTest") {
            withConvention(GroovySourceSet::class) {
                groovy.srcDir("src/docTest/groovy")
            }
            resources.srcDir("src/docTest/resources")
            compileClasspath += sourceSets["main"]!!.output + configurations["runtimeClasspath"] + testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }

        val docTest by tasks.creating(Test::class) {
            description = "Runs the documentation tests"
            group = "verification"
            testClassesDirs = docTestSourceSet.output.classesDirs
            classpath = docTestSourceSet.runtimeClasspath
            mustRunAfter("test", "integrationTest")

            reports {
                html.destination = file("${html.destination}/doc")
                junitXml.destination = file("${junitXml.destination}/doc")
            }
        }

        tasks["check"].dependsOn(docTest)
    }
}
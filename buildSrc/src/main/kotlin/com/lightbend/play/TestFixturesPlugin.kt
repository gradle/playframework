package com.lightbend.play

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*

class TestFixturesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val sourceSets = the<SourceSetContainer>()
        val testRuntimeClasspath by configurations

        sourceSets.create("testFixtures") {
            withConvention(GroovySourceSet::class) {
                groovy.srcDir("src/testFixtures/groovy")
            }
            resources.srcDir("src/testFixtures/resources")
            compileClasspath += sourceSets["main"]!!.output + configurations["runtimeClasspath"] + testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}
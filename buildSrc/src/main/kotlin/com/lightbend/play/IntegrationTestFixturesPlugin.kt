package com.lightbend.play

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*

class IntegrationTestFixturesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val sourceSets = the<SourceSetContainer>()
        val testRuntimeClasspath by configurations

        sourceSets.create("integTestFixtures") {
            withConvention(GroovySourceSet::class) {
                groovy.srcDir("src/integTestFixtures/groovy")
            }
            resources.srcDir("src/integTestFixtures/resources")
            compileClasspath += sourceSets["main"]!!.output + configurations["runtimeClasspath"] + testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}
package com.playframework.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*

class IntegrationTestPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val sourceSets = the<SourceSetContainer>()
        val testRuntimeClasspath by configurations
        val integTestFixturesRuntimeClasspath by configurations

        val integrationTestSourceSet = sourceSets.create("integTest") {
            withConvention(GroovySourceSet::class) {
                groovy.srcDir("src/integTest/groovy")
            }
            resources.srcDir("src/integTest/resources")
            compileClasspath += sourceSets["main"]!!.output + sourceSets["integTestFixtures"]!!.output + testRuntimeClasspath + integTestFixturesRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }

        val integrationTest by tasks.creating(Test::class) {
            description = "Runs the integration tests"
            group = "verification"
            testClassesDirs = integrationTestSourceSet.output.classesDirs
            classpath = integrationTestSourceSet.runtimeClasspath
            mustRunAfter("test")
        }

        tasks["check"].dependsOn(integrationTest)
    }
}
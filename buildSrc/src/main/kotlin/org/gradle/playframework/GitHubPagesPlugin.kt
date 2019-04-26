package org.gradle.playframework

import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.ajoberstar.gradle.git.publish.GitPublishPlugin
import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.*


class GitHubPagesPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyGitPublishPlugin()
        addLinks()
        configureGitPublishExtension()
        configureTaskDependencies()
    }

    private
    fun Project.applyGitPublishPlugin() {
        apply<GitPublishPlugin>()
    }

    private
    fun Project.addLinks() {
        val javaApiUrl = "https://docs.oracle.com/javase/8/docs/api/"
        // TODO: This should be https, but the Groovy site has an invalid certificate right now.
        val groovyApiUrl = "http://docs.groovy-lang.org/2.5.4/html/gapi/"
        val gradleApiUrl = "https://docs.gradle.org/${project.gradle.gradleVersion}/javadoc/"

        tasks.withType<Javadoc>().configureEach {
            (options as StandardJavadocDocletOptions).links(javaApiUrl, groovyApiUrl, gradleApiUrl)
        }
    }

    private
    fun Project.configureGitPublishExtension() {
        val javadoc by tasks.existing(Javadoc::class)
        val asciidoctor by tasks.existing(AsciidoctorTask::class)

        configure<GitPublishExtension> {
            repoUri = "https://github.com/gradle/playframework.git"
            branch = "gh-pages"

            contents {
                from(javadoc) {
                    into("api")
                }
                from("${asciidoctor.get().outputDir}/html5")
            }
        }
    }

    private
    fun Project.configureTaskDependencies() {
        val javadoc by tasks.existing(Javadoc::class)
        val asciidoctor by tasks.existing(AsciidoctorTask::class)
        tasks.named("gitPublishCopy") {
            dependsOn(javadoc, asciidoctor)
        }
    }
}

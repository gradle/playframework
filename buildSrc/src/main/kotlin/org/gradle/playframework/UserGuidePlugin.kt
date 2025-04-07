package org.gradle.playframework

import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.util.PatternSet
import org.gradle.kotlin.dsl.*


class UserGuidePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyAsciidocPlugin()
        configureAsciidoctorTask()
    }

    private
    fun Project.applyAsciidocPlugin() {
        apply<AsciidoctorJPlugin>()
    }

    private
    fun Project.configureAsciidoctorTask() {
        val asciidoctor by tasks.existing(AsciidoctorTask::class) {
            setSourceDir(file("src/docs/asciidoc"))
            setBaseDir(file("src/docs/asciidoc"))

            sources(
                delegateClosureOf<PatternSet> {
                    include("index.adoc")
                }
            )

            attributes(
                mapOf(
                    "toc" to "left",
                    "source-highlighter" to "prettify",
                    "icons" to "font",
                    "numbered" to "",
                    "idprefix" to "",
                    "docinfo1" to "true",
                    "sectanchors" to "true",
                    "samplesCodeDir" to file("src/docs/samples")
                )
            )

            forkOptions {
                jvmArgs(
                    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
                    "--add-opens", "java.base/java.io=ALL-UNNAMED",
                )
            }
        }

        afterEvaluate {
            asciidoctor {
                // Replace hard-coded version in samples with project version
                doLast {
                    val htmlUserGuideFile = file("$outputDir/index.html")
                    var text = htmlUserGuideFile.readText()
                    text = text.replace(Regex("id 'org.gradle.playframework' version '.+'"), "id 'org.gradle.playframework' version '${project.version}'")
                    htmlUserGuideFile.writeText(text)
                }
            }
        }
    }
}

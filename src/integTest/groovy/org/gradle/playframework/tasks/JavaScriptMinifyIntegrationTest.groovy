package org.gradle.playframework.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static org.gradle.playframework.plugins.PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME

class JavaScriptMinifyIntegrationTest extends AbstractJavaScriptMinifyIntegrationTest {

    private static final JS_MINIFY_TASK_PATH = ":$JS_MINIFY_TASK_NAME".toString()
    private static final JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()

    File getProcessedJavaScriptDir() {
        file("build/src/play/javaScript")
    }

    void hasProcessedJavaScript(String fileName) {
        hasExpectedJavaScript(processedJavaScript("${fileName}.js" ))
        hasMinifiedJavaScript(processedJavaScript("${fileName}.min.js" ))
    }

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
            }

            ${playRepositories()}
        """
    }

    def "minifies default javascript source set as part of play application build"() {
        given:
        withJavaScriptSource(assets("test.js"))

        when:
        BuildResult result = build "assemble"

        then:
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        assetsJar.containsDescendants(
                "public/test.min.js",
                "public/test.js"
        )

        and:
        hasProcessedJavaScript("test")
    }


    def "does not re-minify when inputs and outputs are unchanged"() {
        given:
        withJavaScriptSource(assets("test.js"))
        build "assemble"

        when:
        BuildResult result = build "assemble"

        then:
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    def "re-minifies when an output is removed" () {
        given:
        withJavaScriptSource(assets("test.js"))
        build "assemble"

        // Detects missing output
        when:
        processedJavaScript("test.min.js").delete()
        assetsJar.file.delete()
        BuildResult result = build "assemble"

        then:
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        hasProcessedJavaScript("test")
    }

    def "re-minifies when an input is changed" () {
        given:
        withJavaScriptSource(assets("test.js"))
        build "assemble"

        // Detects changed input
        when:
        file("app/assets/test.js") << "alert('this is a change!');"
        BuildResult result = build "assemble"

        then:
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
    }

    def "cleans removed source file on minify" () {
        given:
        withJavaScriptSource(assets("test1.js"))
        def source2 = withJavaScriptSource(assets("test2.js"))

        when:
        build "assemble"

        then:
        hasProcessedJavaScript("test1")
        hasProcessedJavaScript("test2")
        assetsJar.containsDescendants(
                "public/test1.min.js",
                "public/test2.min.js",
                "public/test1.js",
                "public/test2.js"
        )

        when:
        source2.delete()
        build "assemble"

        then:
        ! processedJavaScript("test2.min.js").exists()
        ! processedJavaScript("test2.js").exists()
        assetsJar.countFiles("public/test2.min.js") == 0
        assetsJar.countFiles("public/test2.js") == 0
    }

    def "produces sensible error on minify failure"() {
        given:
        temporaryFolder.newFolder('app', 'assets', 'javascripts')
        file("app/assets/javascripts/test1.js") << "BAD SOURCE"
        file("app/assets/javascripts/test2.js") << "BAD SOURCE"
        withJavaScriptSource("app/assets/javascripts/hello.js")

        when:
        BuildResult result = buildAndFail "assemble"

        then:
        hasProcessedJavaScript("javascripts/hello")
        result.output.contains("Execution failed for task ':minifyPlayJavaScript'.")
        result.output.contains("Minification failed with the following errors:")
        String slash = File.separator
        result.output.contains("app${slash}assets${slash}javascripts${slash}test1.js line 1 : 4")
        result.output.contains("app${slash}assets${slash}javascripts${slash}test2.js line 1 : 4")
    }
}
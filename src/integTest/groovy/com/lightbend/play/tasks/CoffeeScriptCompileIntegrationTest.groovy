package com.lightbend.play.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.lightbend.play.fixtures.Repositories.gradleJavascriptRepository
import static com.lightbend.play.fixtures.Repositories.playRepositories
import static com.lightbend.play.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayApplicationPlugin.JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME
import static com.lightbend.play.plugins.PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME

class CoffeeScriptCompileIntegrationTest extends AbstractCoffeeScriptCompileIntegrationTest {

    private static final COFFEESCRIPT_COMPILE_TASK_PATH = ":$COFFEESCRIPT_COMPILE_TASK_NAME".toString()
    private static final JS_MINIFY_TASK_PATH = ":$JS_MINIFY_TASK_NAME".toString()
    private static final JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play'
            }

            ${playRepositories()}
            ${gradleJavascriptRepository()}
        """
    }

    def "compiles default coffeescript source set as part of play application build" () {
        when:
        withCoffeeScriptSource(assets("test.coffee"))
        BuildResult result = build "assemble"

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        hasProcessedCoffeeScript("test")
        assetsJar.containsDescendants(
                "public/test.js",
                "public/test.min.js"
        )
    }

    def "minify task depends on compile task" () {
        when:
        withCoffeeScriptSource(assets("test.coffee"))
        BuildResult result = build COFFEESCRIPT_COMPILE_TASK_NAME

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
    }

    def "does not recompile when inputs and outputs are unchanged" () {
        given:
        withCoffeeScriptSource(assets("test.coffee"))
        build "assemble"

        when:
        BuildResult result = build "assemble"

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    def "recompiles when inputs are changed" () {
        given:
        withCoffeeScriptSource(assets("test.coffee"))
        build "assemble"

        when:
        assets("test.coffee") << '\nalert "this is a change!"'
        BuildResult result = build "assemble"

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
    }

    def "recompiles when outputs are removed" () {
        given:
        withCoffeeScriptSource(assets("test.coffee"))
        build "assemble"

        when:
        hasProcessedCoffeeScript("test")
        compiledCoffeeScript("test.js").delete()
        processedJavaScript("test.js").delete()
        processedJavaScript("test.min.js").delete()
        assetsJar.file.delete()
        BuildResult result = build "assemble"

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JS_MINIFY_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS

        hasProcessedCoffeeScript("test")
    }

    def "cleans removed source file on compile" () {
        given:
        withCoffeeScriptSource(assets("test1.coffee"))
        def source2 = withCoffeeScriptSource(assets("test2.coffee"))

        when:
        build "assemble"

        then:
        assetsJar.containsDescendants(
                "public/test1.js",
                "public/test2.js",
                "public/test1.min.js",
                "public/test2.min.js"
        )

        when:
        source2.delete()
        build "assemble"

        then:
        ! compiledCoffeeScript("test2.js").exists()
        ! processedJavaScript("test2.js").exists()
        ! processedJavaScript("test2.min.js").exists()
        assetsJar.countFiles("public/test2.js") == 0
        assetsJar.countFiles("public/test2.min.js") == 0
    }

    def "produces sensible error on compile failure" () {
        given:
        assets("test1.coffee") << "if"

        when:
        BuildResult result = buildAndFail "assemble"

        then:
        result.output.contains("Execution failed for task '$COFFEESCRIPT_COMPILE_TASK_PATH'.")
        result.output.contains("Failed to compile coffeescript file: test1.coffee")
        result.output.contains("SyntaxError: unexpected if (coffee-script-js-1.8.0.js#10)")
    }
}

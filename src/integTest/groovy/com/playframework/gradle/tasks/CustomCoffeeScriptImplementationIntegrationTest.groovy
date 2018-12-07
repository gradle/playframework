package com.playframework.gradle.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.playframework.gradle.fixtures.Repositories.gradleJavascriptRepository
import static com.playframework.gradle.fixtures.Repositories.playRepositories
import static com.playframework.gradle.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME

class CustomCoffeeScriptImplementationIntegrationTest extends AbstractCoffeeScriptCompileIntegrationTest {

    private static final COFFEESCRIPT_COMPILE_TASK_PATH = ":$COFFEESCRIPT_COMPILE_TASK_NAME".toString()
    def customCoffeeScriptImplFileName

    def setup() {
        temporaryFolder.newFolder('coffeescript')
        temporaryFolder.newFolder('app', 'assets')
        customCoffeeScriptImplFileName = 'coffeescript/coffee-script.min.js'
        file(customCoffeeScriptImplFileName) << getClass().getResource("/coffee-script.min.js").text

        withCoffeeScriptSource('app/assets/test.coffee')
        buildFile << """
            plugins {
                id 'com.playframework.play'
            }

            ${playRepositories()}
            ${gradleJavascriptRepository()}
        """
    }

    def "can compile coffeescript with a custom implementation from file"() {
        buildFile << """
            tasks.withType(PlayCoffeeScriptCompile) {
                coffeeScriptJs = files("${customCoffeeScriptImplFileName}")
            }
        """

        when:
        BuildResult result = build "assemble"

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        hasProcessedCoffeeScript("test")
    }

    def "can compile coffeescript with a custom implementation from configuration"() {
        buildFile << """
            configurations {
                coffeeScript
            }

            dependencies {
                coffeeScript files("${customCoffeeScriptImplFileName}")
            }
            
            tasks.withType(PlayCoffeeScriptCompile) {
                coffeeScriptJs = configurations.coffeeScript
            }
        """

        when:
        BuildResult result = build "assemble"

        then:
        result.task(COFFEESCRIPT_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        hasProcessedCoffeeScript("test")
    }
}

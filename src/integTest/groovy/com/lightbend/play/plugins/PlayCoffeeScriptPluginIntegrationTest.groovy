package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.fixtures.AssertionHelper.findFile
import static com.lightbend.play.fixtures.Repositories.gradleJavascriptRepository
import static com.lightbend.play.fixtures.Repositories.playRepositories
import static com.lightbend.play.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME

class PlayCoffeeScriptPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
                id 'com.lightbend.play-coffeescript'
            }
            
            ${playRepositories()}
            ${gradleJavascriptRepository()}
        """
    }

    def "can compile CoffeeScript files"() {
        given:
        File appAssetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(appAssetsDir, 'test.coffee') << coffeeScriptSource()

        when:
        build(COFFEESCRIPT_COMPILE_TASK_NAME)

        then:
        File outputDir = file('build/src/coffeescript')
        outputDir.isDirectory()
        File[] compiledCoffeeScriptFiles = outputDir.listFiles()
        compiledCoffeeScriptFiles.length == 1
        findFile(compiledCoffeeScriptFiles, 'test.js')
    }

    def "can add source directories to default source set"() {
        given:
        File appAssetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(appAssetsDir, 'test.coffee') << coffeeScriptSource()
        File extraCoffeeScriptDir = temporaryFolder.newFolder('extra', 'coffeescript')
        new File(extraCoffeeScriptDir, 'extra.coffee') << coffeeScriptSource()

        buildFile << """
            sourceSets {
                main {
                    coffeeScript {
                        srcDir 'extra/coffeescript'
                    }
                }
            }
        """

        when:
        build(COFFEESCRIPT_COMPILE_TASK_NAME)

        then:
        then:
        File outputDir = file('build/src/coffeescript')
        outputDir.isDirectory()
        File[] compiledCoffeeScriptFiles = outputDir.listFiles()
        compiledCoffeeScriptFiles.length == 2
        findFile(compiledCoffeeScriptFiles, 'test.js')
        findFile(compiledCoffeeScriptFiles, 'extra.js')
    }

    static String coffeeScriptSource() {
        """
            # Assignment:
            number   = 42
            opposite = true
            
            # Conditions:
            number = -42 if opposite
            
            # Functions:
            square = (x) -> x * x
            
            # Arrays:
            list = [1, 2, 3, 4, 5]
            
            # Objects:
            math =
              root:   Math.sqrt
              square: square
              cube:   (x) -> x * square x
            
            # Splats:
            race = (winner, runners...) ->
              print winner, runners
            
            # Existence:
            alert "I knew it!" if elvis?
            
            # Array comprehensions:
            cubes = (math.cube num for num in list)
        """
    }
}

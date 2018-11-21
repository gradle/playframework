package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.PlayFixtures.findFile
import static com.lightbend.play.PlayFixtures.playRepositories
import static com.lightbend.play.PlayFixtures.javascriptRepository
import static com.lightbend.play.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME

class PlayCoffeeScriptPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
                id 'com.lightbend.play-coffeescript'
            }
            
            ${playRepositories()}
            ${javascriptRepository()}
        """
    }

    def "can compile CoffeeScript files"() {
        given:
        File appAssetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(appAssetsDir, 'test.coffee') << coffeeScriptSource()

        when:
        build(COFFEESCRIPT_COMPILE_TASK_NAME)

        then:
        then:
        File outputDir = new File(projectDir, 'build/src/coffeescript')
        outputDir.isDirectory()
        File[] compiledCoffeeScriptFiles = outputDir.listFiles()
        compiledCoffeeScriptFiles.length == 1
        findFile(compiledCoffeeScriptFiles, 'test.js')
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

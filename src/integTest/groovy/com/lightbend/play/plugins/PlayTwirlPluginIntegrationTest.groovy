package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.PlayFixtures.findFile
import static com.lightbend.play.PlayFixtures.playRepositories
import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayTwirlPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
                id 'com.lightbend.play-twirl'
            }
            
            ${playRepositories()}
        """
    }

    def "can compile Twirl files"() {
        given:
        File appViewDir = temporaryFolder.newFolder('app', 'views')
        new File(appViewDir, 'test.scala.js') << """
            @(username: String) alert(@helper.json(username));
        """

        when:
        build(TWIRL_COMPILE_TASK_NAME)

        then:
        File outputDir = new File(projectDir, "build/src/twirl/js")
        outputDir.isDirectory()
        File[] compiledTwirlFiles = outputDir.listFiles()
        compiledTwirlFiles.length == 1
        findFile(compiledTwirlFiles, 'test.template.scala')
    }
}

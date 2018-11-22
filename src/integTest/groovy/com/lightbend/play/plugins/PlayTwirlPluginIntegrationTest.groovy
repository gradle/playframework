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
        new File(appViewDir, 'test.scala.js') << jsTwirlTemplate()

        when:
        build(TWIRL_COMPILE_TASK_NAME)

        then:
        File outputDir = new File(projectDir, 'build/src/twirl/js')
        outputDir.isDirectory()
        File[] compiledTwirlFiles = outputDir.listFiles()
        compiledTwirlFiles.length == 1
        findFile(compiledTwirlFiles, 'test.template.scala')
    }

    def "can add source directories to default source set"() {
        given:
        File appViewDir = temporaryFolder.newFolder('app', 'views')
        new File(appViewDir, 'test.scala.js') << jsTwirlTemplate()
        File extraTwirlDir = temporaryFolder.newFolder('extra', 'twirl')
        new File(extraTwirlDir, 'extra.scala.js') << jsTwirlTemplate()

        buildFile << """
            sourceSets {
                main {
                    twirl {
                        srcDir 'extra/twirl'
                    }
                }
            }
        """

        when:
        build(TWIRL_COMPILE_TASK_NAME)

        then:
        File outputDir = new File(projectDir, 'build/src/twirl/js')
        outputDir.isDirectory()
        File[] compiledTwirlFiles = outputDir.listFiles()
        compiledTwirlFiles.length == 2
        findFile(compiledTwirlFiles, 'test.template.scala')
        findFile(compiledTwirlFiles, 'extra.template.scala')
    }

    static String jsTwirlTemplate() {
        """
            @(username: String) alert(@helper.json(username));
        """
    }
}

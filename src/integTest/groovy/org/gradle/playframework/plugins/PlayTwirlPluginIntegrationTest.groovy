package org.gradle.playframework.plugins

import org.gradle.playframework.AbstractIntegrationTest

import static org.gradle.playframework.fixtures.file.FileFixtures.findFile
import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayTwirlPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
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
        File outputDir = file('build/src/play/twirl/views/js')
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
        File outputDir = file('build/src/play/twirl/views/js')
        outputDir.isDirectory()
        File[] compiledTwirlFiles = outputDir.listFiles()
        compiledTwirlFiles.length == 1
        findFile(compiledTwirlFiles, 'test.template.scala')
        File extraOutputDir = file('build/src/play/twirl/js')
        extraOutputDir.isDirectory()
        File[] extraCompiledTwirlFiles = extraOutputDir.listFiles()
        extraCompiledTwirlFiles.length == 1
        findFile(extraCompiledTwirlFiles, 'extra.template.scala')
    }

    static String jsTwirlTemplate() {
        """
            @(username: String) alert(@helper.json(username));
        """
    }
}

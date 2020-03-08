package org.gradle.playframework.plugins

import org.gradle.playframework.AbstractIntegrationTest

import static org.gradle.playframework.fixtures.file.FileFixtures.findFile
import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.plugins.PlayLessPlugin.LESS_COMPILE_TASK_NAME

class PlayLessPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
                id 'org.gradle.playframework-less'
            }
            
            ${playRepositories()}
        """
    }

    def "can compile LESS files"() {
        given:
        File lessDir = temporaryFolder.newFolder('app', 'assets', 'stylesheets')
        new File(lessDir, 'main.less') << lessSource()

        when:
        build(LESS_COMPILE_TASK_NAME)

        then:
        File outputDir = file('build/src/play/less')
        outputDir.isDirectory()

        File[] cssFiles = new File(outputDir, 'stylesheets').listFiles()
        cssFiles.length == 1
        findFile(cssFiles, 'main.css')
    }

    def "can add source directories to default source set"() {
        given:
        File lessDir = temporaryFolder.newFolder('app', 'assets', 'stylesheets')
        new File(lessDir, 'main.less') << lessSource()

        File extraLessDir = temporaryFolder.newFolder('extra', 'less', 'stylesheets')
        new File(extraLessDir, 'extra.less') << lessSource()

        buildFile << """
            sourceSets {
                main {
                    less {
                        srcDir 'extra/less'
                    }
                }
            }
        """

        when:
        build(LESS_COMPILE_TASK_NAME)

        then:
        File outputDir = file('build/src/play/less')
        outputDir.isDirectory()

        File[] cssFiles = new File(outputDir, 'stylesheets').listFiles()
        cssFiles.length == 2
        findFile(cssFiles, 'main.css')
        findFile(cssFiles, 'extra.css')
    }

    static String lessSource() {
        """
            .some-class {
                float: left;
            }
        """
    }
}

package org.gradle.playframework.plugins

import org.gradle.playframework.AbstractIntegrationTest

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.fixtures.file.FileFixtures.findFile
import static org.gradle.playframework.plugins.PlayLessPlugin.LESS_COMPILE_TASK_NAME

class PlayLessWithWebJarsPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
                id 'org.gradle.playframework-less'
                id 'org.gradle.playframework-webjars'
            }
            
            ${playRepositories()}

            dependencies {
                webJar 'org.webjars.bower:css-reset:2.5.1'
            }
        """
    }

    def "can compile LESS files which import files in WebJars"() {
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

    static String lessSource() {
        """
            @import (inline) "lib/css-reset/reset.css";

            .some-class {
                float: left;
            }
        """
    }
}

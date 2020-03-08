package org.gradle.playframework.plugins

import org.gradle.playframework.AbstractIntegrationTest

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.fixtures.file.FileFixtures.findFile
import static org.gradle.playframework.plugins.PlayWebJarsPlugin.WEBJARS_EXTRACT_TASK_NAME

class PlayWebJarsPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
                id 'org.gradle.playframework-webjars'
            }
            
            ${playRepositories()}
            
            dependencies {
                webJar 'org.webjars:requirejs:2.3.6'
                webJar 'org.webjars.npm:inherits:2.0.4'
            }
        """
    }

    def "can extract WebJars"() {
        when:
        build(WEBJARS_EXTRACT_TASK_NAME)

        then:
        File outputDir = file('build/src/play/webJars/lib')
        outputDir.isDirectory()

        File[] libs = outputDir.listFiles()
        libs.length == 2
        findFile(libs, 'requirejs')
        findFile(libs, 'inherits')
    }
}

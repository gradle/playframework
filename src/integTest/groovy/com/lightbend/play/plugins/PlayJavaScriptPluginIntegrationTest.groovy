package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.PlayFixtures.playRepositories

class PlayJavaScriptPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
                id 'com.lightbend.play-javascript'
            }
            
            ${playRepositories()}
        """
    }

    def "can minify JavaScript files"() {
        given:
        File assetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(assetsDir, 'test.js') << 'test'

        when:
        build('minifyJavascript')

        then:
        File outputDir = new File(projectDir, "build/src/javaScript")
        outputDir.isDirectory()
        File[] jsFles = outputDir.listFiles()
        jsFles.length == 2
        jsFles.find { it.name == "test.js" }
        jsFles.find { it.name == "test.min.js" }
    }
}

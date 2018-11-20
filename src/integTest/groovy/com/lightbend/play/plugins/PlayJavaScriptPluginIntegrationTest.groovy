package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest
import spock.lang.Ignore

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

    @Ignore
    def "can add source directories to default source set"() {
        given:
        File assetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(assetsDir, 'test.js') << 'test'
        File extraJavascriptDir = temporaryFolder.newFolder('extra', 'javascript')
        new File(extraJavascriptDir, 'extra.js') << 'extra'
        buildFile << """
            // modify existing source set
        """

        when:
        build('minifyJavascript')

        then:
        File outputDir = new File(projectDir, "build/src/javaScript")
        outputDir.isDirectory()
        File[] jsFles = outputDir.listFiles()
        jsFles.length == 4
        jsFles.find { it.name == "test.js" }
        jsFles.find { it.name == "test.min.js" }
        jsFles.find { it.name == "extra.js" }
        jsFles.find { it.name == "extra.min.js" }
    }
}

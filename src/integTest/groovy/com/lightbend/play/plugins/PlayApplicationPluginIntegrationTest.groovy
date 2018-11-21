package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest
import org.gradle.testkit.runner.BuildResult

import static com.lightbend.play.PlayFixtures.playRepositories

class PlayApplicationPluginIntegrationTest  extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
            }
            
            ${playRepositories()}
        """
    }

    def "can resolve default dependencies for Play platform"() {
        when:
        BuildResult result = build('dependencies')

        then:
        result.output.contains("""play
+--- com.typesafe.play:play_2.11:2.6.15""")
        result.output.contains("""playRun
+--- com.typesafe.play:play_2.11:2.6.15""")
        result.output.contains("""playTest
+--- com.typesafe.play:play_2.11:2.6.15""")
    }

    def "can resolve dependencies for Play platform configured by extension"() {
        buildFile << """
            play {
                playVersion = '2.6.14'
                scalaVersion = '2.12'
                javaVersion = JavaVersion.VERSION_1_8
            }
        """

        when:
        BuildResult result = build('dependencies')

        then:
        result.output.contains("""play
+--- com.typesafe.play:play_2.12:2.6.14""")
        result.output.contains("""playRun
+--- com.typesafe.play:play_2.12:2.6.14""")
        result.output.contains("""playTest
+--- com.typesafe.play:play_2.12:2.6.14""")
    }
}

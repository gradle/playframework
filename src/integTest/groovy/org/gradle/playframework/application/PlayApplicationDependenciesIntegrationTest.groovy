package org.gradle.playframework.application

import org.gradle.playframework.AbstractIntegrationTest
import org.gradle.testkit.runner.BuildResult

import static org.gradle.playframework.fixtures.Repositories.playRepositories

class PlayApplicationDependenciesIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework-application'
            }
            
            ${playRepositories()}
        """
    }

    def "can resolve default dependencies for Play platform"() {
        when:
        BuildResult result = build('dependencies')

        then:
        result.output.contains("""play
+--- com.typesafe.play:play_2.12:2.6.25""")
    }

    def "can resolve dependencies for Play platform configured by extension"() {
        buildFile << """
            play {
                platform {
                    playVersion = '2.6.14'
                    scalaVersion = '2.12'
                    javaVersion = JavaVersion.VERSION_1_8
                }
            }
        """

        when:
        BuildResult result = build('dependencies')

        then:
        result.output.contains("""play
+--- com.typesafe.play:play_2.12:2.6.14""")
    }

    def "can add dependencies to Play application"() {
        given:
        buildFile << """
            dependencies {
                play 'commons-lang:commons-lang:2.6'
                play 'ch.qos.logback:logback-classic:1.2.3'
            }
            
            task checkDeps {
                doLast {
                    assert configurations.play.findAll { it.name == 'commons-lang-2.6.jar' || it.name == 'logback-classic-1.2.3.jar' }.size() == 2
                }
            }
        """

        expect:
        build('checkDeps')
    }
}

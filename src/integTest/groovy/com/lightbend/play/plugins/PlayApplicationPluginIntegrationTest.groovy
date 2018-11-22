package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest
import org.gradle.testkit.runner.BuildResult

import static com.lightbend.play.fixtures.Repositories.playRepositories

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
                platform {
                    playVersion.set('2.6.14')
                    scalaVersion.set('2.12')
                    javaVersion.set(JavaVersion.VERSION_1_8)
                }
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

    def "can compile Java code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'com', 'lightbend')
        new File(appDir, 'JavaHelloWorld.java') << """
            package com.lightbend;

            public class JavaHelloWorld {}
        """

        when:
        build('classes')

        then:
        File classesOutputDir = new File(projectDir, 'build/classes')
        new File(classesOutputDir, 'java/main/com/lightbend/JavaHelloWorld.class').isFile()
    }

    def "can compile Scala code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'com', 'lightbend')

        new File(appDir, 'ScalaHelloWorld.scala') << """
            package com.lightbend

            object ScalaHelloWorld {}
        """

        buildFile << """
            dependencies {
                implementation 'org.scala-lang:scala-library:2.11.12'
            }
        """

        when:
        build('classes')

        then:
        File classesOutputDir = new File(projectDir, 'build/classes')
        new File(classesOutputDir, 'scala/main/com/lightbend/ScalaHelloWorld.class').isFile()
    }
}

package com.playframework.gradle.application

import com.playframework.gradle.AbstractIntegrationTest

import static com.playframework.gradle.fixtures.Repositories.playRepositories

class PlayApplicationCompilationIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.playframework.play-application'
            }
            
            ${playRepositories()}
        """
    }

    def "can compile Java code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'com', 'playframework')
        new File(appDir, 'JavaHelloWorld.java') << """
            package com.playframework;

            public class JavaHelloWorld {}
        """

        when:
        build('classes')

        then:
        File classesOutputDir = file('build/classes')
        new File(classesOutputDir, 'scala/main/com/playframework/JavaHelloWorld.class').isFile()
    }

    def "can compile Scala code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'com', 'playframework')

        new File(appDir, 'ScalaHelloWorld.scala') << """
            package com.playframework

            object ScalaHelloWorld {}
        """

        when:
        build('classes')

        then:
        File classesOutputDir = file('build/classes')
        new File(classesOutputDir, 'scala/main/com/playframework/ScalaHelloWorld.class').isFile()
    }
}

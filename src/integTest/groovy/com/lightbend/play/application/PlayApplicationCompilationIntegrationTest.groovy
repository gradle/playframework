package com.lightbend.play.application

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.fixtures.Repositories.playRepositories

class PlayApplicationCompilationIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
            }
            
            ${playRepositories()}
        """
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
        File classesOutputDir = file('build/classes')
        new File(classesOutputDir, 'java/main/com/lightbend/JavaHelloWorld.class').isFile()
    }

    def "can compile Scala code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'com', 'lightbend')

        new File(appDir, 'ScalaHelloWorld.scala') << """
            package com.lightbend

            object ScalaHelloWorld {}
        """

        when:
        build('classes')

        then:
        File classesOutputDir = file('build/classes')
        new File(classesOutputDir, 'scala/main/com/lightbend/ScalaHelloWorld.class').isFile()
    }
}

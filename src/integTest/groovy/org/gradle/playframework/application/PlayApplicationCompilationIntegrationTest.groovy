package org.gradle.playframework.application

import org.gradle.playframework.AbstractIntegrationTest

import static org.gradle.playframework.fixtures.Repositories.playRepositories

class PlayApplicationCompilationIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework-application'
            }
            
            ${playRepositories()}
        """
    }

    def "can compile Java code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'org', 'gradle', 'playframework')
        new File(appDir, 'JavaHelloWorld.java') << """
            package org.gradle.playframework;

            public class JavaHelloWorld {}
        """

        when:
        build('classes')

        then:
        File classesOutputDir = file('build/classes')
        new File(classesOutputDir, 'scala/main/org/gradle/playframework/JavaHelloWorld.class').isFile()
    }

    def "can compile Scala code"() {
        given:
        File appDir = temporaryFolder.newFolder('app', 'org', 'gradle', 'playframework')

        new File(appDir, 'ScalaHelloWorld.scala') << """
            package org.gradle.playframework

            object ScalaHelloWorld {}
        """

        when:
        build('classes')

        then:
        File classesOutputDir = file('build/classes')
        new File(classesOutputDir, 'scala/main/org/gradle/playframework/ScalaHelloWorld.class').isFile()
    }
}

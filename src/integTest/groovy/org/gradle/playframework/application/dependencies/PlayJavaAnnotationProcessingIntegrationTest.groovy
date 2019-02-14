package org.gradle.playframework.application.dependencies

import org.gradle.playframework.AbstractIntegrationTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.fixtures.Repositories.playRepositories

class PlayJavaAnnotationProcessingIntegrationTest extends AbstractIntegrationTest {

    def "can compile Java class incorporating annotation processing"() {
        given:
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
            }
            
            ${playRepositories()}

            configurations {
                annotationProcessor.extendsFrom configurations.compileOnly
            }

            dependencies {
                compileOnly 'org.projectlombok:lombok:1.16.22'
            }
        """

        File appControllerDir = temporaryFolder.newFolder('app', 'controller')
        new File(appControllerDir, "GetterSetterExample.java") << """
            package controller;

            import lombok.AccessLevel;
            import lombok.Getter;
            import lombok.Setter;
            
            public class GetterSetterExample {
                @Getter
                @Setter
                private int age = 10;
                
                @Setter(AccessLevel.PROTECTED)
                private String name;
            
                @Override 
                public String toString() {
                    return String.format("%s (age: %d)", name, getAge());
                }
            }
        """

        when:
        BuildResult result = build('compileScala')

        then:
        result.task(':compileScala').outcome == TaskOutcome.SUCCESS
    }
}

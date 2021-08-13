package org.gradle.playframework.fixtures

final class Repositories {

    private Repositories() {}

    static String playRepositories() {
        """
            repositories {
                mavenCentral()
                maven {
                    name "lightbend-maven-release"
                    url "https://repo.lightbend.com/lightbend/maven-releases"
                }
                ivy {
                    name "lightbend-ivy-release"
                    url "https://repo.lightbend.com/lightbend/ivy-releases"
                    layout "ivy"
                }
            }
        """
    }
}

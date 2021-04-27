package org.gradle.playframework.fixtures

final class Repositories {

    private Repositories() {}

    static String playRepositories() {
        """
            repositories {
                mavenCentral()
                // TODO Remove once lightbend migrates everything to repo.lightbend.com
                maven {
                    name "lightbend-bintray-releases"
                    url "https://dl.bintray.com/typesafe/maven-releases"
                }
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

    static String gradleJavascriptRepository() {
        """
            repositories {
                maven {
                    name = "gradle-js"
                    url = "https://repo.gradle.org/gradle/javascript-public"
                }
            }
        """
    }
}

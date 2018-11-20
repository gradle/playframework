package com.lightbend.play

final class PlayFixtures {

    private PlayFixtures() {}

    static String playRepositories() {
        """
            repositories {
                jcenter()
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

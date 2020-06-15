package org.gradle.playframework.application

import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayCompositeBuild

class PlayCompositeBuildIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayCompositeBuild(playVersion)
    }

    @Override
    String[] getBuildTasks() {
        return super.buildTasks + [
                ":javalibrary:compileJava",
                ":javalibrary:classes",
                ":javalibrary:jar",
        ]
    }

    @Override
    void verifyJars() {
        super.verifyJars()
        jar("javalibrary/build/libs/java-lib.jar").hasDescendants("org/test/Util.class")
    }
}

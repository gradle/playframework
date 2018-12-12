package com.playframework.gradle.application

import com.playframework.gradle.fixtures.app.PlayApp
import com.playframework.gradle.fixtures.app.PlayCompositeBuild

class PlayCompositeBuildIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayCompositeBuild(playVersion)
    }

    @Override
    String[] getBuildTasks() {
        return super.buildTasks + [
                ":java-lib:compileJava",
                ":java-lib:classes",
                ":java-lib:jar",
        ]
    }

    @Override
    void verifyJars() {
        super.verifyJars()
        jar("javalibrary/build/libs/java-lib.jar").hasDescendants("org/test/Util.class")
    }
}

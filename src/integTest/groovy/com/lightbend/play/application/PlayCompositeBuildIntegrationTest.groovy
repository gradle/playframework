package com.lightbend.play.application

import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayCompositeBuild

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

class PlayCompositeBuildIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayCompositeBuild(DEFAULT_PLAY_VERSION)
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

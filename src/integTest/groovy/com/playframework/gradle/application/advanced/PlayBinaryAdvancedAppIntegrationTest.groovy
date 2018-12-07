package com.playframework.gradle.application.advanced

import com.playframework.gradle.application.PlayApplicationPluginIntegrationTest
import com.playframework.gradle.fixtures.app.AdvancedPlayApp
import com.playframework.gradle.fixtures.app.PlayApp

import static com.playframework.gradle.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayBinaryAdvancedAppIntegrationTest extends PlayApplicationPluginIntegrationTest {

    private static final TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()

    @Override
    PlayApp getPlayApp() {
        new AdvancedPlayApp(versionNumber)
    }

    @Override
    void verifyJars() {
        super.verifyJars()

        jar("build/libs/${playApp.name}.jar").containsDescendants(
                "views/html/awesome/index.class",
                "jva/html/index.class",
                "special/strangename/Application.class",
                "models/DataType.class",
                "models/ScalaClass.class",
                "controllers/scla/MixedJava.class",
                "controllers/jva/PureJava.class",
                "evolutions/default/1.sql"
        )

        jar("build/libs/${playApp.name}-assets.jar").containsDescendants(
                "public/javascripts/sample.js",
                "public/javascripts/sample.min.js"
        )
    }

    @Override
    String[] getBuildTasks() {
        return super.getBuildTasks() + TWIRL_COMPILE_TASK_PATH
    }
}

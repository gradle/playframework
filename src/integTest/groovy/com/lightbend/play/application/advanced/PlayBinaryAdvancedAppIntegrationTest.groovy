package com.lightbend.play.application.advanced

import com.lightbend.play.application.PlayApplicationPluginIntegrationTest
import com.lightbend.play.fixtures.app.AdvancedPlayApp
import com.lightbend.play.fixtures.app.PlayApp

import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

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
                "public/javascripts/sample.min.js",
                "public/javascripts/test.js",
                "public/javascripts/test.min.js"
        )
    }

    @Override
    String[] getBuildTasks() {
        return super.getBuildTasks() + TWIRL_COMPILE_TASK_PATH
    }
}

package org.gradle.playframework.application.advanced

import org.gradle.playframework.application.PlayApplicationPluginIntegrationTest
import org.gradle.playframework.fixtures.app.AdvancedPlayApp
import org.gradle.playframework.fixtures.app.PlayApp

class PlayBinaryAdvancedAppIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new AdvancedPlayApp(playVersion)
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
                "public/stylesheets/main.css",
                "public/stylesheets/extra.css",
                "public/lib/css-reset/reset.css",
        )
    }
}

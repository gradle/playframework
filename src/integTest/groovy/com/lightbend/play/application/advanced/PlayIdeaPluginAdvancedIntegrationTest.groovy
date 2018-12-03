package com.lightbend.play.application.advanced

import com.lightbend.play.application.PlayIdeaPluginIntegrationTest
import com.lightbend.play.fixtures.app.AdvancedPlayApp
import com.lightbend.play.fixtures.app.PlayApp
import org.gradle.play.internal.platform.PlayMajorVersion

import static com.lightbend.play.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME
import static com.lightbend.play.plugins.PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME
import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayIdeaPluginAdvancedIntegrationTest extends PlayIdeaPluginIntegrationTest {
    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    private static final String COFFEESCRIPT_COMPILE_TASK_PATH = ":$COFFEESCRIPT_COMPILE_TASK_NAME".toString()
    private static final String JS_MINIFY_TASK_PATH = ":$JS_MINIFY_TASK_NAME".toString()
    static final Map PLAY_VERSION_TO_CLASSPATH_SIZE = [(PlayMajorVersion.PLAY_2_4_X): 108,
                                                       (PlayMajorVersion.PLAY_2_5_X): 121,
                                                       (PlayMajorVersion.PLAY_2_6_X): 111]

    @Override
    PlayApp getPlayApp() {
        new AdvancedPlayApp(versionNumber)
    }

    String[] getSourcePaths() {
        ["public", "conf", "app",
         "templates", "app/assets",
         "build/src/twirl", "build/src/coffeeScript", "build/src/javaScript",
         "build/src/routes"]
    }

    String[] getBuildTasks() {
        [ROUTES_COMPILE_TASK_PATH, TWIRL_COMPILE_TASK_PATH, COFFEESCRIPT_COMPILE_TASK_PATH, JS_MINIFY_TASK_PATH, ":ideaModule", ":ideaWorkspace", ":idea"]
    }

    int getExpectedScalaClasspathSize() {
        PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(versionNumber.toString())]
    }
}

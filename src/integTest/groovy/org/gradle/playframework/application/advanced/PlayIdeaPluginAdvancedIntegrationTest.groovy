package org.gradle.playframework.application.advanced

import org.gradle.play.internal.platform.PlayMajorVersion
import org.gradle.playframework.application.PlayIdeaPluginIntegrationTest
import org.gradle.playframework.fixtures.app.AdvancedPlayApp
import org.gradle.playframework.fixtures.app.PlayApp

import static org.gradle.api.plugins.JavaPlugin.CLASSES_TASK_NAME
import static org.gradle.playframework.plugins.PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME
import static org.gradle.playframework.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static org.gradle.playframework.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayIdeaPluginAdvancedIntegrationTest extends PlayIdeaPluginIntegrationTest {
    private static final String CLASSES_TASK_PATH = ":$CLASSES_TASK_NAME".toString()
    private static final String SCALA_COMPILE_TASK_NAME = 'compileScala'
    private static final String SCALA_COMPILE_TASK_PATH = ":$SCALA_COMPILE_TASK_NAME".toString()
    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    private static final String JS_MINIFY_TASK_PATH = ":$JS_MINIFY_TASK_NAME".toString()
    static final Map PLAY_VERSION_TO_CLASSPATH_SIZE = [(PlayMajorVersion.PLAY_2_4_X): 108,
                                                       (PlayMajorVersion.PLAY_2_5_X): 121,
                                                       (PlayMajorVersion.PLAY_2_6_X): 111]

    @Override
    PlayApp getPlayApp() {
        new AdvancedPlayApp(playVersion)
    }

    String[] getSourcePaths() {
        [
            "public",
            "conf",
            "app",
            // TODO: It's unclear why those directories should be available as source paths ("templates" is a Twirl directory which we add later, "app/assets" sits below "app" so no need to add explictly)
            //"templates",
            //"app/assets",
            "build/src/play/twirl",
            "build/src/play/javaScript",
            "build/src/play/routes"
        ]
    }

    String[] getBuildTasks() {
        [
            ROUTES_COMPILE_TASK_PATH,
            TWIRL_COMPILE_TASK_PATH,
            JS_MINIFY_TASK_PATH,
            ":ideaModule",
            ":ideaWorkspace",
            ":idea"
        ]
    }

    String[] getUnexecutedTasks() {
        [
            CLASSES_TASK_PATH,
            SCALA_COMPILE_TASK_PATH
        ]
    }

    int getExpectedScalaClasspathSize() {
        PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(playVersion.toString())]
    }
}

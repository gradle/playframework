package com.lightbend.play.application.basic

import com.lightbend.play.application.PlayIdeaPluginIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp
import org.gradle.play.internal.platform.PlayMajorVersion

import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayIdeaPluginBasicIntegrationTest extends PlayIdeaPluginIntegrationTest {
    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    static final Map PLAY_VERSION_TO_CLASSPATH_SIZE = [(PlayMajorVersion.PLAY_2_4_X): 96,
                                                       (PlayMajorVersion.PLAY_2_5_X): 108,
                                                       (PlayMajorVersion.PLAY_2_6_X): 111]

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(versionNumber)
    }

    String[] getSourcePaths() {
        [
            "public",
            "conf",
            "app",
            "test",
            "build/src/routes",
            "build/src/twirl"
        ]
    }

    String[] getBuildTasks() {
        [
            ROUTES_COMPILE_TASK_PATH,
            TWIRL_COMPILE_TASK_PATH,
            ":ideaProject",
            ":ideaModule",
            ":ideaWorkspace",
            ":idea"
        ]
    }

    int getExpectedScalaClasspathSize() {
        return PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(versionNumber.toString())]
    }
}

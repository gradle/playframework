package org.gradle.playframework.application.multiproject

import org.gradle.play.internal.platform.PlayMajorVersion
import org.gradle.playframework.application.PlayIdeaPluginIntegrationTest
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.fixtures.app.PlayMultiProject

import static org.gradle.api.plugins.JavaPlugin.CLASSES_TASK_NAME
import static org.gradle.playframework.application.basic.PlayIdeaPluginBasicIntegrationTest.PLAY_VERSION_TO_CLASSPATH_SIZE
import static org.gradle.playframework.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME

class PlayIdeaPluginMultiprojectIntegrationTest extends PlayIdeaPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayMultiProject(playVersion)
    }

    File getModuleFile() {
        file("primary/primary.iml")
    }

    @Override
    List<File> getIdeFiles() {
        super.getIdeFiles() + [file("${playApp.name}.iml"), file('submodule/submodule.iml'), file('javalibrary/javalibrary.iml')]
    }

    String[] getSourcePaths() {
        [
            "public",
            "conf",
            "app",
            "build/src/play/routes"
        ]
    }

    String[] getBuildTasks() {
        [
            ":ideaModule",
            ":ideaProject",
            ":ideaWorkspace",
            ":idea",
            ":javalibrary:ideaModule",
            ":javalibrary:idea",
            ":primary:$ROUTES_COMPILE_TASK_NAME".toString(),
            ":primary:ideaModule",
            ":primary:idea",
            ":submodule:ideaModule",
            ":submodule:idea"
        ]
    }

    String[] getUnexecutedTasks() {
        [
            ":primary:$CLASSES_TASK_NAME".toString(),
            ":primary:compileScala".toString()
        ]
    }

    int getExpectedScalaClasspathSize() {
        PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(playVersion.toString())]
    }
}

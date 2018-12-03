package com.lightbend.play.application.multiproject

import com.lightbend.play.application.PlayIdeaPluginIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayMultiProject
import org.gradle.play.internal.platform.PlayMajorVersion

import static com.lightbend.play.application.basic.PlayIdeaPluginBasicIntegrationTest.PLAY_VERSION_TO_CLASSPATH_SIZE
import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME

class PlayIdeaPluginMultiprojectIntegrationTest extends PlayIdeaPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayMultiProject(versionNumber)
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
            "build/src/routes"
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

    int getExpectedScalaClasspathSize() {
        PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(versionNumber.toString())]
    }
}

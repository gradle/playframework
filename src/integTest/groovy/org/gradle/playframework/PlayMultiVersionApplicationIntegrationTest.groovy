package org.gradle.playframework

import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.playframework.util.VersionNumber

abstract class PlayMultiVersionApplicationIntegrationTest extends PlayMultiVersionIntegrationTest {

    def configurePlayApplication(VersionNumber playVersion) {
        PlayMultiVersionIntegrationTest.playVersion = playVersion

        getPlayApp().writeBuildFile(projectDir, playVersion)

        getPlayApp().writeSources(projectDir)

        settingsFile << """
            rootProject.name = '${playApp.name}'
        """
    }

    abstract PlayApp getPlayApp()
}

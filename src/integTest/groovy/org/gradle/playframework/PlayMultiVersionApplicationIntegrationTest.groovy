package org.gradle.playframework

import org.gradle.playframework.fixtures.app.PlayApp

abstract class PlayMultiVersionApplicationIntegrationTest extends PlayMultiVersionIntegrationTest {

    def setup() {
        getPlayApp().writeSources(projectDir)
        configurePlayVersionInBuildScript()
        settingsFile << """
            rootProject.name = '${playApp.name}'
        """
    }

    abstract PlayApp getPlayApp()
}

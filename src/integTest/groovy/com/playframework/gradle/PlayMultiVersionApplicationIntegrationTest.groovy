package com.playframework.gradle

import com.playframework.gradle.fixtures.app.PlayApp

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

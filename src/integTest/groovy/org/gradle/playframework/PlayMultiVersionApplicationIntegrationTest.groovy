package org.gradle.playframework

import org.gradle.playframework.fixtures.app.PlayApp

abstract class PlayMultiVersionApplicationIntegrationTest extends PlayMultiVersionIntegrationTest {

    def configurePlayApplication() {
        getPlayApp().writeSources(projectDir)
        settingsFile << """
            rootProject.name = '${playApp.name}'
        """
    }

    abstract PlayApp getPlayApp()
}

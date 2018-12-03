package com.lightbend.play

import com.lightbend.play.fixtures.app.PlayApp

abstract class PlayMultiVersionApplicationIntegrationTest extends PlayMultiVersionIntegrationTest {

    def setup() {
        getPlayApp().writeSources(projectDir)
        configurePlayVersionInBuildScript()
    }

    abstract PlayApp getPlayApp()
}

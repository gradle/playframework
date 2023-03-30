package org.gradle.playframework.fixtures.app

import org.gradle.playframework.util.VersionNumber

class BasicPlayApp extends PlayApp {
    BasicPlayApp() {
        super()
    }

    BasicPlayApp(VersionNumber version) {
        super(version)
    }
}
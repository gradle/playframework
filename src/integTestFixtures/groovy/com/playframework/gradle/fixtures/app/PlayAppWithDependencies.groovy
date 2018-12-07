package com.playframework.gradle.fixtures.app

import org.gradle.util.VersionNumber

class PlayAppWithDependencies extends PlayApp {
    PlayAppWithDependencies(VersionNumber version) {
        super(version)
    }
}
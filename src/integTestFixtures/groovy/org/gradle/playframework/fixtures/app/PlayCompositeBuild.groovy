package org.gradle.playframework.fixtures.app

import org.gradle.playframework.util.VersionNumber

class PlayCompositeBuild extends PlayApp {
    PlayCompositeBuild(VersionNumber version) {
        super(version)
    }

    @Override
    List<SourceFile> getAllFiles() {
        return super.getAllFiles() + sourceFiles("javalibrary") + sourceFile("", "settings.gradle")
    }
}


package org.gradle.playframework.fixtures.app

import org.gradle.util.VersionNumber

class PlayMultiProject extends PlayApp {

    PlayMultiProject() {
        super()
    }

    PlayMultiProject(VersionNumber version) {
        super(version)
    }

    @Override
    List<SourceFile> getAllFiles() {
        return sourceFiles("primary") + sourceFiles("submodule") + sourceFiles("javalibrary") + sourceFile("", "settings.gradle")
    }
}

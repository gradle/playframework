package org.gradle.playframework.fixtures.app

import org.gradle.playframework.util.VersionNumber

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

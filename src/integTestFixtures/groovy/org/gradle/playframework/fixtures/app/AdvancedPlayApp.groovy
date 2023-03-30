package org.gradle.playframework.fixtures.app

import org.gradle.playframework.util.VersionNumber

class AdvancedPlayApp extends PlayApp {
    AdvancedPlayApp(VersionNumber version) {
        super(version)
    }
    @Override
    List<SourceFile> getViewSources() {
        return super.getViewSources() + sourceFiles("templates")
    }
}

package com.playframework.gradle.fixtures.app

import org.gradle.util.VersionNumber

import static com.playframework.gradle.fixtures.Repositories.gradleJavascriptRepository

class AdvancedPlayApp extends PlayApp {
    AdvancedPlayApp(VersionNumber version) {
        super(version)
    }
    @Override
    SourceFile getGradleBuild() {
        def gradleBuild = super.getGradleBuild()
        def gradleBuildWithRepositories = gradleBuild.content.concat """
            allprojects {
                ${gradleJavascriptRepository()}
            }
        """
        return new SourceFile(gradleBuild.path, gradleBuild.name, gradleBuildWithRepositories)
    }

    @Override
    List<SourceFile> getViewSources() {
        return super.getViewSources() + sourceFiles("templates")
    }
}

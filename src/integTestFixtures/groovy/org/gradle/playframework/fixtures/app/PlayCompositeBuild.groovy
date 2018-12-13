package org.gradle.playframework.fixtures.app

import org.gradle.util.VersionNumber

import static org.gradle.playframework.fixtures.Repositories.gradleJavascriptRepository

class PlayCompositeBuild extends PlayApp {
    PlayCompositeBuild(VersionNumber version) {
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
    List<SourceFile> getAllFiles() {
        return super.getAllFiles() + sourceFiles("javalibrary") + sourceFile("", "settings.gradle")
    }
}


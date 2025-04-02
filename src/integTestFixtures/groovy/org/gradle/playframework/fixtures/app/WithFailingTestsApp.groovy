package org.gradle.playframework.fixtures.app

import org.gradle.playframework.util.VersionNumber

import static org.gradle.playframework.fixtures.Repositories.playRepositories

class WithFailingTestsApp extends PlayApp {
    List<SourceFile> appSources
    List<SourceFile> viewSources
    List<SourceFile> confSources
    List<SourceFile> testSources

    @Override
    SourceFile getGradleBuild(VersionNumber playVersion) {
        def gradleBuild = sourceFile("", "build.gradle.ftl", "basicplayapp")
        def buildFileContent = gradleBuild.content.concat """
            allprojects {
                ${playRepositories()}
            }
        """
        if (playVersion != null) {
            buildFileContent = buildFileContent.concat """
                play {
                    platform {
                        playVersion = '${playVersion.toString()}'
                    }
                }
            """.stripIndent()
        }
        return new SourceFile(gradleBuild.path, gradleBuild.name, buildFileContent)
    }

    WithFailingTestsApp(VersionNumber version){
        super(version)
        appSources = sourceFiles("app", "basicplayapp");
        viewSources = sourceFiles("app/views", "basicplayapp");
        confSources = sourceFiles("conf", "shared") + sourceFiles("conf", "basicplayapp")
        testSources = sourceFiles("test") + sourceFiles("test", "basicplayapp")
    }
}

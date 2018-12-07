package com.playframework.gradle.application

import com.playframework.gradle.PlayMultiVersionApplicationIntegrationTest
import com.playframework.gradle.fixtures.archive.ArchiveTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.util.VersionNumber

import static com.playframework.gradle.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static com.playframework.gradle.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static com.playframework.gradle.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME

abstract class PlayDistributionApplicationIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {

    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    private static final String JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final String ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()
    private static final String MAIN_ZIP_DIST_TASK_PATH = ":createMainZipDist"
    private static final String MAIN_TAR_DIST_TASK_PATH = ":createMainTarDist"
    private static final String STAGE_MAIN_DIST_TASK_PATH = ":stageMainDist"
    private static final String MAIN_START_SCRIPTS_TASK_PATH = ":createMainStartScripts"
    private static final String MAIN_DIST_JAR_TASK_PATH = ":createMainDistributionJar"

    def "can build play app distribution"() {
        when:
        BuildResult result = build("stage")

        then:
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.SUCCESS
        }

        and:
        verifyJars()
        verifyStagedFiles()

        when:
        result = build("dist")

        then:
        result.task(MAIN_ZIP_DIST_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(MAIN_TAR_DIST_TASK_PATH).outcome == TaskOutcome.SUCCESS
        buildTasks.each {
            assert result.task(it).outcome == TaskOutcome.UP_TO_DATE
        }

        and:
        verifyArchives()
    }

    List<ArchiveTestFixture> archives() {
        [ zip("build/distributions/main.zip"), tar("build/distributions/main.tar") ]
    }
    void verifyArchives() {
        archives()*.containsDescendants(
                "main/lib/${playApp.name}.jar",
                "main/lib/${playApp.name}-assets.jar",
                "main/bin/main",
                "main/bin/main.bat",
                "main/conf/application.conf",
                "main/README")
    }

    void verifyStagedFiles() {
        File stageMainDir = file("build/stage/main")
        [
            "lib/${playApp.name}.jar",
            "lib/${playApp.name}-assets.jar",
            "bin/main",
            "bin/main.bat",
            "conf/application.conf",
            "README"
        ].each {
            assert new File(stageMainDir, it).isFile()
        }
    }

    void verifyJars() {
        jar("build/distributionJars/main/${playApp.name}.jar").containsDescendants(
                determineRoutesClassName(),
                "views/html/index.class",
                "views/html/main.class",
                "controllers/Application.class",
                "application.conf")

        jar("build/distributionJars/main/${playApp.name}.jar").isManifestPresentAndFirstEntry()
    }

    String[] getBuildTasks() {
        [
                ROUTES_COMPILE_TASK_PATH,
                TWIRL_COMPILE_TASK_PATH,
                JAR_TASK_PATH,
                MAIN_DIST_JAR_TASK_PATH,
                ASSETS_JAR_TASK_PATH,
                MAIN_START_SCRIPTS_TASK_PATH,
                STAGE_MAIN_DIST_TASK_PATH
        ]
    }

    String determineRoutesClassName() {
        return versionNumber >= VersionNumber.parse('2.4.0') ? "router/Routes.class" : "Routes.class"
    }
}

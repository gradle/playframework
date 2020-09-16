package org.gradle.playframework.tasks

import org.gradle.playframework.PlayMultiVersionIntegrationTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.util.VersionNumber
import org.junit.Assume

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.fixtures.file.FileFixtures.assertContentsHaveChangedSince
import static org.gradle.playframework.fixtures.file.FileFixtures.snapshot
import static org.gradle.playframework.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME

abstract class AbstractRoutesCompileIntegrationTest extends PlayMultiVersionIntegrationTest {

    protected static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    String destinationDirPath = "build/src/play/routes"
    File destinationDir

    abstract getScalaRoutesFileName(String packageName, String namespace)

    abstract getJavaRoutesFileName(String packageName, String namespace)

    abstract getReverseRoutesFileName(String packageName, String namespace)

    abstract getOtherRoutesFileNames()

    def setup() {
        destinationDir = file(destinationDirPath)
        settingsFile << """ rootProject.name = 'routes-play-app' """
        buildFile << """
plugins {
    id 'org.gradle.playframework-application'
}

${playRepositories()}
"""
        configurePlayVersionInBuildScript()
    }

    protected String controllers() {
        if (playVersion >= VersionNumber.parse('2.6.0')) {
            return "@controllers"
        } else {
            return "controllers"
        }
    }

    protected String controller(String packageId) {
        if (playVersion >= VersionNumber.parse('2.6.0')) {
            return """
package controllers${packageId}

import javax.inject._
import play.api._
import play.api.mvc._
import models._

@Singleton
class Application @Inject() extends InjectedController {
  def index = Action {
    Ok("Your new application is ready.")
  }
}
"""
        } else {
            return """
package controllers${packageId}


import play.api._
import play.api.mvc._
import models._

object Application extends Controller {
  def index = Action {
    Ok("Your new application is ready.")
  }
}
"""
        }
    }

    def "can run RoutesCompile"() {
        given:
        withRoutesTemplate()
        expect:
        build(ROUTES_COMPILE_TASK_NAME)
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
    }

    def "recompiles on changed routes file input"() {
        given:
        File templateFile = withRoutesTemplate()
        build(ROUTES_COMPILE_TASK_NAME)

        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
        def scalaRoutesFileSnapshot = snapshot(getScalaRoutesFile())
        def javaRoutesFileSnapshot = snapshot(getJavaRoutesFile())
        def reverseRoutesFileSnapshot = snapshot(getReverseRoutesFile())

        when:
        // Wait to ensure timestamp on input file is different from previous compilation
        // I suspect that the Play routes compiler has some incremental check based on timestamp
        sleep(1000)
        templateFile << """
GET     /newroute                          ${controllers()}.Application.index()
"""

        and:
        BuildResult result = build ROUTES_COMPILE_TASK_NAME

        then:
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS

        and:
        assertContentsHaveChangedSince(scalaRoutesFileSnapshot, getScalaRoutesFile())
        assertContentsHaveChangedSince(javaRoutesFileSnapshot, getJavaRoutesFile())
        assertContentsHaveChangedSince(reverseRoutesFileSnapshot, getReverseRoutesFile())

        when:
        result = build ROUTES_COMPILE_TASK_NAME

        then:
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    private File getScalaRoutesFile() {
        new File(destinationDir, getScalaRoutesFileName('', ''))
    }

    private File getJavaRoutesFile() {
        new File(destinationDir, getJavaRoutesFileName('', ''))
    }

    private File getReverseRoutesFile() {
        new File(destinationDir, getReverseRoutesFileName('', ''))
    }

    def "compiles additional routes file and cleans up output on removal"() {
        when:
        withRoutesTemplate()
        then:
        build(ROUTES_COMPILE_TASK_NAME)
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }

        when:
        withRoutesTemplate("foo")
        and:
        build(ROUTES_COMPILE_TASK_NAME)
        then:
        (createRouteFileList() + createRouteFileList('foo')).each {
            assert new File(destinationDir, it).isFile()
        }

        when:
        file("conf/foo.routes").delete()
        then:
        build(ROUTES_COMPILE_TASK_NAME)
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
        createRouteFileList('foo').each { assert !new File(destinationDir, it).isFile() }
    }

    def "can run RoutesCompile with namespaceReverseRouter set"() {
        given:
        withRoutesTemplate("org.gradle.test")
        buildFile << """
            $ROUTES_COMPILE_TASK_NAME {
                namespaceReverseRouter.set(true)
            }
        """
        expect:
        build(ROUTES_COMPILE_TASK_NAME)
        and:
        createRouteFileList("org/gradle/test", "org/gradle/test").each {
            assert new File(destinationDir, it).isFile()
        }
    }

    def withRoutesSource(File routesFile, String packageId) {
        routesFile.createNewFile()
        routesFile << """
# Routes
# This file defines all application routes (Higher priority routes first)
# ~~~~

# Home page
GET     /                          ${controllers()}${packageId}.Application.index()
"""
        File controllersDir = file("app/controllers")

        if (!controllersDir.isDirectory()) {
            temporaryFolder.newFolder('app', 'controllers')
        }

        File packageIdDir = controllersDir

        if (packageId != '') {
            packageIdDir = new File(controllersDir, packageId)

            if (!packageIdDir.isDirectory()) {
                packageIdDir.mkdirs()
            }
        }

        withControllerSource(new File(packageIdDir, "Application.scala"), packageId)
        return routesFile
    }

    def withControllerSource(File file, String packageId) {
        file.createNewFile()
        file << controller(packageId)
    }

    def withRoutesTemplate(String packageName = "") {
        def routesDir = file('conf')

        if (!routesDir.isDirectory()) {
            temporaryFolder.newFolder('conf')
        }

        def routesFile = packageName.isEmpty() ? new File(routesDir, "routes") : new File(routesDir, packageName + ".routes")
        def packageId = packageName.isEmpty() ? "" : ".$packageName"
        withRoutesSource(routesFile, packageId)
    }

    def createRouteFileList(String packageName = '', String namespace = '') {
        [getJavaRoutesFileName(packageName, namespace), getReverseRoutesFileName(packageName, namespace), getScalaRoutesFileName(packageName, namespace)] + otherRoutesFileNames.collect {
            it(packageName, namespace)
        }
    }

    def "can add additional imports"() {
        // Play version 2.3 not supported
        Assume.assumeTrue(playVersion > VersionNumber.parse("2.3.99"))
        given:
        withRoutesTemplate()
        and:
        buildFile << """
$ROUTES_COMPILE_TASK_NAME {
    additionalImports.add("extra.package")
}
"""
        expect:
        build(ROUTES_COMPILE_TASK_NAME)
        and:
        new File(destinationDir, getReverseRoutesFileName('', '')).text.contains("extra.package")
        new File(destinationDir, getScalaRoutesFileName('', '')).text.contains("extra.package")
    }
}

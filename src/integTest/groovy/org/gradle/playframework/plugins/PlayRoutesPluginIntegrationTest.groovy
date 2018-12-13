package org.gradle.playframework.plugins

import org.gradle.playframework.AbstractIntegrationTest
import org.gradle.testkit.runner.BuildResult

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.fixtures.file.FileFixtures.findFile
import static org.gradle.playframework.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME

class PlayRoutesPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework'
            }
            
            ${playRepositories()}
        """
    }

    def "can compile routes files"() {
        given:
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, 'routes') << getRoutes()

        when:
        build(ROUTES_COMPILE_TASK_NAME)

        then:
        File controllersOutputDir = file('build/src/play/routes/controllers')
        controllersOutputDir.isDirectory()
        File[] compiledControllersFiles = controllersOutputDir.listFiles()
        compiledControllersFiles.length == 3
        findFile(compiledControllersFiles, 'routes.java')
        findFile(compiledControllersFiles, 'ReverseRoutes.scala')
        File controllersJavascriptOutputDir = new File(controllersOutputDir, 'javascript')
        controllersJavascriptOutputDir.isDirectory()
        File[] compiledControllersJavascriptFiles = controllersJavascriptOutputDir.listFiles()
        compiledControllersJavascriptFiles.length == 1
        findFile(compiledControllersJavascriptFiles, 'JavaScriptReverseRoutes.scala')
        File routerOutputDir = file('build/src/play/routes/router')
        routerOutputDir.isDirectory()
        File[] compiledRouterFiles = routerOutputDir.listFiles()
        compiledRouterFiles.length == 2
        findFile(compiledRouterFiles, 'RoutesPrefix.scala')
        findFile(compiledRouterFiles, 'Routes.scala')
    }

    def "can add source directories to default source set"() {
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, 'routes') << getRoutes()
        File extraRoutesDir = temporaryFolder.newFolder('extra', 'more')
        new File(extraRoutesDir, 'more') << getRoutes()

        buildFile << """
            sourceSets {
                main {
                    routes {
                        srcDir 'extra/more'
                    }
                }
            }
        """

        when:
        build(ROUTES_COMPILE_TASK_NAME)

        then:
        File controllersOutputDir = file('build/src/play/routes/controllers')
        controllersOutputDir.isDirectory()
        File[] compiledControllersFiles = controllersOutputDir.listFiles()
        compiledControllersFiles.length == 3
        findFile(compiledControllersFiles, 'routes.java')
        findFile(compiledControllersFiles, 'ReverseRoutes.scala')
        File controllersJavascriptOutputDir = new File(controllersOutputDir, 'javascript')
        controllersJavascriptOutputDir.isDirectory()
        File[] compiledControllersJavascriptFiles = controllersJavascriptOutputDir.listFiles()
        compiledControllersJavascriptFiles.length == 1
        findFile(compiledControllersJavascriptFiles, 'JavaScriptReverseRoutes.scala')
        File routerOutputDir = file('build/src/play/routes/router')
        routerOutputDir.isDirectory()
        File[] compiledRouterFiles = routerOutputDir.listFiles()
        compiledRouterFiles.length == 2
        findFile(compiledRouterFiles, 'RoutesPrefix.scala')
        findFile(compiledRouterFiles, 'Routes.scala')
    }

    def "can configure static routes generator"() {
        given:
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, 'routes') << getRoutes()

        buildFile << """
            play {
                injectedRoutesGenerator = true
            }
        """

        when:
        build(ROUTES_COMPILE_TASK_NAME)

        then:
        File controllersOutputDir = file('build/src/play/routes/controllers')
        controllersOutputDir.isDirectory()
        File[] compiledControllersFiles = controllersOutputDir.listFiles()
        compiledControllersFiles.length == 3
        findFile(compiledControllersFiles, 'routes.java')
        findFile(compiledControllersFiles, 'ReverseRoutes.scala')
        File controllersJavascriptOutputDir = new File(controllersOutputDir, 'javascript')
        controllersJavascriptOutputDir.isDirectory()
        File[] compiledControllersJavascriptFiles = controllersJavascriptOutputDir.listFiles()
        compiledControllersJavascriptFiles.length == 1
        findFile(compiledControllersJavascriptFiles, 'JavaScriptReverseRoutes.scala')
        File routerOutputDir = file('build/src/play/routes/router')
        routerOutputDir.isDirectory()
        File[] compiledRouterFiles = routerOutputDir.listFiles()
        compiledRouterFiles.length == 2
        findFile(compiledRouterFiles, 'RoutesPrefix.scala')
        findFile(compiledRouterFiles, 'Routes.scala')
    }

    def "fails build if injected routes generator was configured for Play version < 2.4.0"() {
        given:
        buildFile << """
            play {
                platform {
                    playVersion = '2.3.0'
                }
                injectedRoutesGenerator = true
            }
        """

        when:
        BuildResult result = buildAndFail(ROUTES_COMPILE_TASK_NAME)

        then:
        result.output.contains('Injected routers are only supported in Play 2.4 or newer.')
    }

    static String getRoutes() {
        """
            GET     /                           controllers.HomeController.index
        """
    }
}

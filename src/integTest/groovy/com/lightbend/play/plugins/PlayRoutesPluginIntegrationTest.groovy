package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest
import org.gradle.testkit.runner.BuildResult

import static com.lightbend.play.PlayFixtures.findFile
import static com.lightbend.play.PlayFixtures.playRepositories
import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME

class PlayRoutesPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
                id 'com.lightbend.play-routes'
            }
            
            ${playRepositories()}
        """
    }

    def "can compile routes files"() {
        given:
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, 'routes') << """
            GET     /                           controllers.HomeController.index
        """

        when:
        build(ROUTES_COMPILE_TASK_NAME)

        then:
        File controllersOutputDir = new File(projectDir, 'build/src/routes/controllers')
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
        File routerOutputDir = new File(projectDir, 'build/src/routes/router')
        routerOutputDir.isDirectory()
        File[] compiledRouterFiles = routerOutputDir.listFiles()
        compiledRouterFiles.length == 2
        findFile(compiledRouterFiles, 'RoutesPrefix.scala')
        findFile(compiledRouterFiles, 'Routes.scala')
    }

    def "can configure static routes generator"() {
        given:
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, 'routes') << """
            GET     /                           controllers.HomeController.index
        """
        buildFile << """
            play {
                platform {
                    injectedRoutesGenerator.set(true)
                }
            }
        """

        when:
        build(ROUTES_COMPILE_TASK_NAME)

        then:
        File controllersOutputDir = new File(projectDir, 'build/src/routes/controllers')
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
        File routerOutputDir = new File(projectDir, 'build/src/routes/router')
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
                    playVersion.set('2.3.0')
                    injectedRoutesGenerator.set(true)
                }
            }
        """

        when:
        BuildResult result = buildAndFail(ROUTES_COMPILE_TASK_NAME)

        then:
        result.output.contains('Injected routers are only supported in Play 2.4 or newer.')
    }
}

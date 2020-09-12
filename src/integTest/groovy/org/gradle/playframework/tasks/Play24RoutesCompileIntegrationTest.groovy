package org.gradle.playframework.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.util.VersionNumber
import org.junit.Assume

class Play24RoutesCompileIntegrationTest extends AbstractRoutesCompileIntegrationTest {

    @Override
    def getJavaRoutesFileName(String packageName, String namespace) {
        if (playVersion < VersionNumber.parse("2.4")) {
            return "${namespace ? namespace + '/' : ''}controllers/${packageName ? packageName + "/" : ''}/routes.java"
        } else {
            return "${namespace ? namespace + '/' : ''}controllers/${packageName ? packageName + '/' : ''}routes.java"
        }
    }

    @Override
    def getReverseRoutesFileName(String packageName, String namespace) {
        if (playVersion < VersionNumber.parse("2.4")) {
            if (namespace) {
                return "${namespace ? namespace + '/' : ''}routes_reverseRouting.scala"
            } else {
                return "${packageName ? packageName + '/' : ''}routes_reverseRouting.scala"
            }
        } else {
            return "${namespace ? namespace + '/' : ''}controllers/${packageName ? packageName + '/' : ''}ReverseRoutes.scala"
        }
    }

    @Override
    def getScalaRoutesFileName(String packageName, String namespace) {
        if (playVersion < VersionNumber.parse("2.4")) {
            if (namespace) {
                return "${namespace ? namespace + '/' : ''}routes_routing.scala"
            } else {
                return "${packageName ? packageName + '/' : ''}routes_routing.scala"
            }
        } else {
            return "${packageName ?: 'router'}/Routes.scala"
        }
    }

    @Override
    def getOtherRoutesFileNames() {
        if (playVersion < VersionNumber.parse("2.4")) {
            return []
        } else {
            return [
                    { packageName, namespace -> "${namespace ? namespace + '/' : ''}controllers/${packageName ? packageName + '/' : ''}javascript/JavaScriptReverseRoutes.scala" },
                    { packageName, namespace -> "${packageName ?: 'router'}/RoutesPrefix.scala" }
            ]
        }
    }

    def "can specify route compiler type as injected"() {
        // Play version 2.3 not supported
        Assume.assumeTrue(playVersion > VersionNumber.parse("2.3.99"))
        given:
        withRoutesTemplate()
        withInjectedRoutesController()
        buildFile << """
play {
    injectedRoutesGenerator = true
}
"""
        expect:
        build('compileScala')
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
    }

    def "recompiles when route compiler type is changed"() {
        // Play version 2.3 not supported
        Assume.assumeTrue(playVersion > VersionNumber.parse("2.3.99"))
        // Play 2.7+ only has a single route compiler type.
        Assume.assumeTrue(playVersion < VersionNumber.parse("2.7"))
        when:
        withRoutesTemplate()
        then:
        build('compileScala')

        when:
        withInjectedRoutesController()
        buildFile << """
play {
    injectedRoutesGenerator = true
}
"""
        then:
        BuildResult result = build('compileScala')
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
    }

    private withInjectedRoutesController() {
        file("app/controllers/Application.scala").with {
            // change Scala companion object into a regular class
            text = text.replaceFirst(/object/, "class")
        }
    }

    def "failure to generate routes fails the build with useful message"() {
        given:
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, "routes") << """
# This will cause route compilation failure since overload is not supported.
GET        /        com.foobar.HelloController.index()
GET        /*path   com.foobar.HelloController.index(path)
        """
        expect:
        BuildResult result = buildAndFail(ROUTES_COMPILE_TASK_PATH)
        result.output.contains("Using different overloaded methods is not allowed. If you are using a single method in combination with default parameters, make sure you declare them all explicitly.")
    }
}

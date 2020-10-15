package org.gradle.playframework

import org.gradle.samples.test.rule.UsesSample
import spock.lang.Unroll

@Unroll
class InjectedRoutesUserGuideIntegrationTest extends InDepthUserGuideSamplesIntegrationTest {
    @UsesSample("play-2.4/groovy")
    def "injected routes sample is buildable for Play 2.4 #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("build")

        then:
        assertRoutesCompilationOutput()

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }

    @UsesSample("play-2.6/groovy")
    def "injected routes sample is buildable for Play 2.6 #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("build")

        then:
        assertRoutesCompilationOutput()

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }

    private void assertRoutesCompilationOutput() {
        File routesCompilationOutputDir = new File(sample.dir, "build/src/play/routes")
        [
                "controllers/routes.java",
                "controllers/ReverseRoutes.scala",
                "router/Routes.scala",
                "controllers/javascript/JavaScriptReverseRoutes.scala",
                "router/RoutesPrefix.scala"
        ].each {
            assert new File(routesCompilationOutputDir, it).isFile()
        }
    }
}

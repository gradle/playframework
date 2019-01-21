package org.gradle.playframework

import org.gradle.playframework.fixtures.archive.ArchiveTestFixture
import org.gradle.playframework.fixtures.archive.JarTestFixture
import org.gradle.playframework.fixtures.archive.TarTestFixture
import org.gradle.playframework.fixtures.archive.ZipTestFixture
import org.gradle.playframework.fixtures.test.JUnitXmlTestExecutionResult
import org.gradle.playframework.fixtures.test.TestExecutionResult
import org.gradle.samples.test.rule.Sample
import org.gradle.samples.test.rule.UsesSample
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import spock.lang.Specification

/**
 * In-depth testing of user guide samples.
 */
class InDepthUserGuideSamplesIntegrationTest extends Specification {

    @Rule
    Sample sample = Sample.from("src/docs/samples")

    @UsesSample("basic/groovy")
    def "basic sample is buildable"() {
        when:
        build("build")

        then:
        new File(sample.dir, "build/libs/basic.jar").isFile()
        TestExecutionResult result = new JUnitXmlTestExecutionResult(sample.dir)
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(4, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
    }

    @UsesSample("advanced/groovy")
    def "advanced sample is buildable"() {
        when:
        build("build")

        then:
        new File(sample.dir, "build/libs/advanced.jar").isFile()
        TestExecutionResult result = new JUnitXmlTestExecutionResult(sample.dir)
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
    }

    @UsesSample("multi-project/groovy")
    def "multi-project sample is buildable"() {
        when:
        build("assemble")

        then:
        new File(sample.dir, "modules/admin/build/libs/admin.jar").isFile()
        new File(sample.dir, "modules/user/build/libs/user.jar").isFile()
        new File(sample.dir, "modules/util/build/libs/util.jar").isFile()
    }

    @UsesSample("source-sets/groovy")
    def "source-sets sample is buildable"() {
        when:
        build("build")

        then:
        applicationJar(sample.dir, "source-sets").containsDescendants(
            "controllers/hello/HelloController.class",
            "controllers/date/DateController.class",
            "controllers/hello/routes.class",
            "controllers/date/routes.class",
            "html/main.class"
        )
        assetsJar(sample.dir, "source-sets").with {
            containsDescendants(
                "public/sample.js"
            )
            doesNotContainDescendants(
                "public/old_sample.js"
            )
        }
    }

    @UsesSample("configure-compiler/groovy")
    def "compiler sample is buildable"() {
        when:
        build("build")

        then:
        applicationJar(sample.dir, "configure-compiler").containsDescendants(
            "controllers/Application.class"
        )
    }

    @UsesSample("custom-distribution/groovy")
    def "distribution sample is buildable"() {
        when:
        build("dist")

        then:
        distributionArchives(sample.dir)*.containsDescendants(
            "main/README.md",
            "main/bin/runPlayBinaryAsUser.sh"
        )
    }

    @UsesSample("play-2.4/groovy")
    def "injected routes sample is buildable for Play 2.4"() {
        when:
        build("build")

        then:
        assertRoutesCompilationOutput()
    }

    @UsesSample("play-2.6/groovy")
    def "injected routes sample is buildable for Play 2.6"() {
        when:
        build("build")

        then:
        assertRoutesCompilationOutput()
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

    private BuildResult build(String... arguments) {
        GradleRunner.create()
                .withProjectDir(sample.dir)
                .withArguments(arguments + '-s' as List<String>)
                .withPluginClasspath()
                .forwardOutput()
                .build()
    }

    JarTestFixture applicationJar(File sampleDir, String projectName) {
        new JarTestFixture(new File(sampleDir, "build/libs/${projectName}.jar"))
    }

    JarTestFixture assetsJar(File sampleDir, String projectName) {
        new JarTestFixture(new File(sampleDir,"build/libs/${projectName}-assets.jar"))
    }

    List<ArchiveTestFixture> distributionArchives(File sampleDir) {
        [new ZipTestFixture(new File(sampleDir, "build/distributions/main.zip")),
         new TarTestFixture(new File(sampleDir, "build/distributions/main.tar"))]
    }
}

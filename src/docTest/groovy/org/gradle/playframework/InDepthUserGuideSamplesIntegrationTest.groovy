package org.gradle.playframework

import org.gradle.exemplar.test.rule.Sample
import org.gradle.playframework.fixtures.archive.ArchiveTestFixture
import org.gradle.playframework.fixtures.archive.JarTestFixture
import org.gradle.playframework.fixtures.archive.TarTestFixture
import org.gradle.playframework.fixtures.archive.ZipTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import spock.lang.Specification

/**
 * In-depth testing of user guide samples.
 */
abstract class InDepthUserGuideSamplesIntegrationTest extends Specification {
    protected static final String[] VERSIONS_UNDER_TEST = ["5.1.1", "5.2.1", "5.5.1", "5.6.4", "6.0.1", "6.6.1", "7.0.1"]

    @Rule
    Sample sample = Sample.from("src/docs/samples")
    private GradleRunner runner

    BuildResult build(String... arguments) {
        runner.withArguments(arguments + '-s' as List<String>).build()
    }

    GradleRunner setupRunner(String gradleVersion) {
        runner = GradleRunner.create()
            .withProjectDir(sample.dir)
            .withPluginClasspath()
            .withGradleVersion(gradleVersion)
            .forwardOutput()
        return runner
    }

    JarTestFixture applicationJar(File sampleDir, String projectName) {
        new JarTestFixture(new File(sampleDir, "build/libs/${projectName}.jar"))
    }

    JarTestFixture assetsJar(File sampleDir, String projectName) {
        new JarTestFixture(new File(sampleDir, "build/libs/${projectName}-assets.jar"))
    }

    List<ArchiveTestFixture> distributionArchives(File sampleDir) {
        [new ZipTestFixture(new File(sampleDir, "build/distributions/main.zip")),
         new TarTestFixture(new File(sampleDir, "build/distributions/main.tar"))]
    }
}

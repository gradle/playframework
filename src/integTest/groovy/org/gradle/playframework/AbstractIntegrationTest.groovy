package org.gradle.playframework

import org.gradle.playframework.fixtures.archive.JarTestFixture
import org.gradle.playframework.fixtures.archive.TarTestFixture
import org.gradle.playframework.fixtures.archive.ZipTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class AbstractIntegrationTest extends Specification {

    boolean enableCC = true

    @TempDir
    File temporaryFolder

    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = temporaryFolder
        buildFile = new File(temporaryFolder, 'build.gradle')
        settingsFile = new File(temporaryFolder, 'settings.gradle')
    }

    protected BuildResult build(String... arguments) {
        createAndConfigureGradleRunner(arguments).build()
    }

    protected BuildResult buildAndFail(String... arguments) {
        createAndConfigureGradleRunner(arguments).buildAndFail()
    }

    private GradleRunner createAndConfigureGradleRunner(String... arguments) {
        def args = (arguments + ['-s']) as List<String>
        if (enableCC) {
            args += '--configuration-cache'
        }
        GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(args)
                .withPluginClasspath().forwardOutput()
    }

    protected File file(String path) {
        new File(projectDir, path)
    }

    protected JarTestFixture jar(String fileName) {
        new JarTestFixture(file(fileName))
    }

    protected ZipTestFixture zip(String fileName) {
        new ZipTestFixture(file(fileName))
    }

    protected TarTestFixture tar(String fileName) {
        new TarTestFixture(file(fileName))
    }
}

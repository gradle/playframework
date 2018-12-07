package com.playframework.gradle

import com.playframework.gradle.fixtures.archive.JarTestFixture
import com.playframework.gradle.fixtures.archive.TarTestFixture
import com.playframework.gradle.fixtures.archive.ZipTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class AbstractIntegrationTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = temporaryFolder.root
        buildFile = temporaryFolder.newFile('build.gradle')
        settingsFile = temporaryFolder.newFile('settings.gradle')
    }

    protected BuildResult build(String... arguments) {
        createAndConfigureGradleRunner(arguments).build()
    }

    protected BuildResult buildAndFail(String... arguments) {
        createAndConfigureGradleRunner(arguments).buildAndFail()
    }

    private GradleRunner createAndConfigureGradleRunner(String... arguments) {
        GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(arguments + '-s' as List<String>)
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

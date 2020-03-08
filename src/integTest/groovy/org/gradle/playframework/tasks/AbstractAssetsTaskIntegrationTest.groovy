package org.gradle.playframework.tasks

import org.gradle.playframework.AbstractIntegrationTest
import org.gradle.playframework.fixtures.archive.JarTestFixture

import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME
import static org.gradle.playframework.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME

abstract class AbstractAssetsTaskIntegrationTest extends AbstractIntegrationTest {
    static final JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    static final ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()

    JarTestFixture jar(String fileName) {
        new JarTestFixture(file(fileName))
    }

    File assets(String fileName) {
        File assetsDir = file('app/assets')

        if (!assetsDir.isDirectory()) {
            temporaryFolder.newFolder('app', 'assets')
        }

        new File(assetsDir, fileName)
    }

    boolean compareWithoutWhiteSpace(String string1, String string2) {
        return withoutWhiteSpace(string1) == withoutWhiteSpace(string2)
    }

    def withoutWhiteSpace(String string) {
        return string.replaceAll("\\s+", " ");
    }
}

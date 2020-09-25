package org.gradle.playframework.application.basic

import org.gradle.play.internal.platform.PlayMajorVersion
import org.gradle.playframework.application.PlayIdeaPluginIntegrationTest
import org.gradle.playframework.fixtures.app.BasicPlayApp
import org.gradle.playframework.fixtures.app.PlayApp
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.api.plugins.JavaPlugin.CLASSES_TASK_NAME
import static org.gradle.playframework.fixtures.ide.IdeaFixtures.parseIml
import static org.gradle.playframework.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static org.gradle.playframework.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME

class PlayIdeaPluginBasicIntegrationTest extends PlayIdeaPluginIntegrationTest {
    private static final String CLASSES_TASK_PATH = ":$CLASSES_TASK_NAME".toString()
    private static final String SCALA_COMPILE_TASK_NAME = 'compileScala'
    private static final String SCALA_COMPILE_TASK_PATH = ":$SCALA_COMPILE_TASK_NAME".toString()
    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    static final Map PLAY_VERSION_TO_CLASSPATH_SIZE = [(PlayMajorVersion.PLAY_2_4_X): 96,
                                                       (PlayMajorVersion.PLAY_2_5_X): 108,
                                                       (PlayMajorVersion.PLAY_2_6_X): 111]

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(playVersion)
    }

    String[] getSourcePaths() {
        [
            "public",
            "conf",
            "app",
            "test",
            "build/src/play/routes",
            "build/src/play/twirl"
        ]
    }

    String[] getBuildTasks() {
        [
            ROUTES_COMPILE_TASK_PATH,
            TWIRL_COMPILE_TASK_PATH,
            ":ideaProject",
            ":ideaModule",
            ":ideaWorkspace",
            ":idea"
        ]
    }

    String[] getUnexecutedTasks() {
        [
            SCALA_COMPILE_TASK_PATH,
            CLASSES_TASK_PATH
        ]
    }

    int getExpectedScalaClasspathSize() {
        return PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(playVersion.toString())]
    }

    def "when model configuration changes, IDEA metadata can be rebuilt"() {
        applyIdePlugin()
        build(ideTask)
        when:
        file("extra/java").mkdirs()
        buildFile << """
sourceSets {
    main {
        scala {
            srcDir "extra/java"
        }
    }
}
"""
        and:
        BuildResult result = build(ideTask)
        then:
        result.task(':ideaModule').outcome == TaskOutcome.SUCCESS
        def content = parseIml(moduleFile).content
        content.assertContainsSourcePaths("extra/java", "public", "conf", "app", "test", "build/src/play/routes", "build/src/play/twirl")
    }

    def "IDEA metadata contains custom source set"() {
        applyIdePlugin()
        file("extra/java").mkdirs()
        buildFile << """
sourceSets {
    main {
        scala {
            srcDir "extra/java"
        }
    }
}
"""
        when:
        build(ideTask)
        then:
        def content = parseIml(moduleFile).content
        content.assertContainsSourcePaths("extra/java", "public", "conf", "app", "test", "build/src/play/routes", "build/src/play/twirl")
    }
}

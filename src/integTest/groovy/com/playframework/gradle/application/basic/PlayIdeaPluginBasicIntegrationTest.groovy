package com.playframework.gradle.application.basic

import com.playframework.gradle.application.PlayIdeaPluginIntegrationTest
import com.playframework.gradle.fixtures.app.BasicPlayApp
import com.playframework.gradle.fixtures.app.PlayApp
import org.gradle.play.internal.platform.PlayMajorVersion
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.playframework.gradle.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME
import static com.playframework.gradle.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME
import static com.playframework.gradle.fixtures.ide.IdeaFixtures.parseIml

class PlayIdeaPluginBasicIntegrationTest extends PlayIdeaPluginIntegrationTest {
    private static final String ROUTES_COMPILE_TASK_PATH = ":$ROUTES_COMPILE_TASK_NAME".toString()
    private static final String TWIRL_COMPILE_TASK_PATH = ":$TWIRL_COMPILE_TASK_NAME".toString()
    static final Map PLAY_VERSION_TO_CLASSPATH_SIZE = [(PlayMajorVersion.PLAY_2_4_X): 96,
                                                       (PlayMajorVersion.PLAY_2_5_X): 108,
                                                       (PlayMajorVersion.PLAY_2_6_X): 111]

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(versionNumber)
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

    int getExpectedScalaClasspathSize() {
        return PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(versionNumber.toString())]
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

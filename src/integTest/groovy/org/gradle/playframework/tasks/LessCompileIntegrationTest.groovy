package org.gradle.playframework.tasks

import org.gradle.playframework.fixtures.archive.JarTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static org.gradle.playframework.fixtures.Repositories.playRepositories
import static org.gradle.playframework.plugins.PlayLessPlugin.LESS_COMPILE_TASK_NAME

class LessCompileIntegrationTest extends AbstractAssetsTaskIntegrationTest {
    private static final LESS_COMPILE_TASK_PATH = ":$LESS_COMPILE_TASK_NAME".toString()

    def setup() {
        settingsFile << """ rootProject.name = 'less-play-app' """

        buildFile << """
            plugins {
                id 'org.gradle.playframework'
                id 'org.gradle.playframework-less'
            }

            ${playRepositories()}
        """
    }

    def "compiles default less source set as part of Play application build"() {
        given:
        withMainLessSource(assets("main.less"))
        withLessSource(assets("_partial.less"))

        when:
        BuildResult result = build "assemble"

        then:
        result.task(LESS_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        assetsJar.containsDescendants(
                "public/main.css"
        )

        and:
        hasProcessedMainCss("main")
    }

    def "does not recompile when inputs and outputs are unchanged"() {
        given:
        withMainLessSource(assets("main.less"))
        withLessSource(assets("_partial.less"))
        build "assemble"

        when:
        BuildResult result = build "assemble"

        then:
        result.task(LESS_COMPILE_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    def "recompiles when an output is removed" () {
        given:
        withMainLessSource(assets("main.less"))
        withLessSource(assets("_partial.less"))
        build "assemble"

        when:
        processedCss("main").delete()
        assetsJar.file.delete()
        BuildResult result = build "assemble"

        then:
        result.task(LESS_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        hasProcessedMainCss("main")
    }

    def "recompiles when an input is changed" () {
        given:
        withMainLessSource(assets("main.less"))
        withLessSource(assets("_partial.less"))
        build "assemble"

        when:
        file("app/assets/main.less") << ".other-class { margin: auto; }"
        BuildResult result = build "assemble"

        then:
        result.task(LESS_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
    }

    def "cleans removed source file on compile" () {
        given:
        withMainLessSource(assets("main.less"))
        withLessSource(assets("_partial.less"))
        def source2 = withLessSource(assets("extra.less"))

        when:
        build "assemble"

        then:
        hasProcessedMainCss("main")
        hasProcessedCss("extra")
        assetsJar.containsDescendants(
                "public/main.css",
                "public/extra.css",
        )

        when:
        source2.delete()
        build "assemble"

        then:
        ! processedCss("extra").exists()
        assetsJar.countFiles("public/extra.css") == 0
    }

    def "produces sensible error on compile failure"() {
        given:
        assets("main.less") << "BAD SOURCE"

        when:
        BuildResult result = buildAndFail "assemble"

        then:
        result.output.contains("Execution failed for task ':compilePlayLess'.")
        result.output.contains("Could not compile less.")
        String slash = File.separator
        result.output.contains("app${slash}assets${slash}main.less 1:11")
    }

    def withMainLessSource(File file) {
        file << """
            @import _partial;
            
            .class1 {
                float: left;
                
                .class2 {
                    float: right;
                }
            }
        """
    }

    def withLessSource(File file) {
        file << """
            .class3 {
                margin: auto;
            }
        """
    }

    JarTestFixture getAssetsJar() {
        jar("build/libs/less-play-app-assets.jar")
    }

    File processedCss(String fileName) {
        new File(file("build/src/play/less"), "${fileName}.css")
    }

    void hasProcessedMainCss(String fileName) {
        hasExpectedMainCss(processedCss(fileName))
    }

    void hasProcessedCss(String fileName) {
        hasExpectedCss(processedCss(fileName))
    }

    void hasExpectedMainCss(File file) {
        assert file.exists()
        assert compareWithoutWhiteSpace(file.text.readLines().get(0), expectedMainCss())
    }

    void hasExpectedCss(File file) {
        assert file.exists()
        assert compareWithoutWhiteSpace(file.text.readLines().get(0), expectedCss())
    }

    String expectedMainCss() {
        """.class3{margin:auto;} .class1{float:left;} .class1 .class2{float:right;}"""
    }

    String expectedCss() {
        """.class3{margin:auto;}"""
    }
}

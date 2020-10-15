package org.gradle.playframework

import org.gradle.playframework.fixtures.test.JUnitXmlTestExecutionResult
import org.gradle.playframework.fixtures.test.TestExecutionResult
import org.gradle.samples.test.rule.UsesSample
import spock.lang.Unroll

@Unroll
class AdvancedGroovyUserGuideIntegrationTest extends InDepthUserGuideSamplesIntegrationTest {

    @UsesSample("advanced/groovy")
    def "advanced sample is buildable #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("build")

        then:
        new File(sample.dir, "build/libs/advanced.jar").isFile()
        TestExecutionResult result = new JUnitXmlTestExecutionResult(sample.dir)
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }
}

package com.playframework.gradle

import org.gradle.samples.test.rule.Sample
import org.gradle.samples.test.rule.UsesSample
import org.junit.Rule
import spock.lang.Specification

/**
 * In-depth testing of user guide samples.
 */
class InDepthUserGuideSamplesIntegrationTest extends Specification {
    @Rule
    Sample sample = Sample.from("src/docs/samples")

    @UsesSample("apply-plugin")
    def "can apply plugin"() {
        expect:
        true
    }
}

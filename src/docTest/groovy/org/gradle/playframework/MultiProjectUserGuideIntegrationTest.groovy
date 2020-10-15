package org.gradle.playframework

import org.gradle.samples.test.rule.UsesSample
import spock.lang.Unroll

@Unroll
class MultiProjectUserGuideIntegrationTest extends InDepthUserGuideSamplesIntegrationTest {
    @UsesSample("multi-project/groovy")
    def "multi-project sample is buildable #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("assemble")

        then:
        new File(sample.dir, "modules/admin/build/libs/admin.jar").isFile()
        new File(sample.dir, "modules/user/build/libs/user.jar").isFile()
        new File(sample.dir, "modules/util/build/libs/util.jar").isFile()

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }
}

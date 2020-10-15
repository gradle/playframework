package org.gradle.playframework

import org.gradle.samples.test.rule.UsesSample
import spock.lang.Unroll

@Unroll
class MiscUserGuideIntegrationTest extends InDepthUserGuideSamplesIntegrationTest {
    @UsesSample("source-sets/groovy")
    def "source-sets sample is buildable #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("build")

        then:
        applicationJar(sample.dir, "source-sets").containsDescendants(
                "controllers/hello/HelloController.class",
                "controllers/date/DateController.class",
                "controllers/hello/routes.class",
                "controllers/date/routes.class",
                "html/main.class"
        )
        assetsJar(sample.dir, "source-sets").with {
            containsDescendants(
                    "public/sample.js"
            )
            doesNotContainDescendants(
                    "public/old_sample.js"
            )
        }

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }

    @UsesSample("configure-compiler/groovy")
    def "compiler sample is buildable #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("build")

        then:
        applicationJar(sample.dir, "configure-compiler").containsDescendants(
                "controllers/Application.class"
        )

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }

    @UsesSample("custom-distribution/groovy")
    def "distribution sample is buildable #gradleVersion"(String gradleVersion) {
        setupRunner(gradleVersion)

        when:
        build("dist")

        then:
        distributionArchives(sample.dir)*.containsDescendants(
                "main/README.md",
                "main/bin/runPlayBinaryAsUser.sh"
        )

        where:
        gradleVersion << VERSIONS_UNDER_TEST
    }
}

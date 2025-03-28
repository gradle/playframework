//file:noinspection GrMethodMayBeStatic
package org.gradle.playframework

import org.gradle.playframework.fixtures.multiversion.PlayCoverage
import org.gradle.playframework.fixtures.multiversion.TargetCoverage
import org.gradle.playframework.util.VersionNumber

import java.lang.annotation.Annotation

import static org.gradle.playframework.fixtures.Repositories.playRepositories

@TargetCoverage({ PlayCoverage.ALL })
abstract class PlayMultiVersionIntegrationTest extends AbstractIntegrationTest {
    private static final String DEFAULT_PLAY_VERSION_SYS_PROP_VALUE = 'default'
    private static final String ALL_PLAY_VERSIONS_SYS_PROP_VALUE = 'all'
    private static final String USER_PROVIDED_PLAY_VERSION_SYS_PROP = System.getProperty('playframework.int-test.target.version')

    static VersionNumber playVersion

    protected setupBuildFile() {
        buildFile << """
            plugins {
                id 'org.gradle.playframework-application'
            }

            ${playRepositories()}

            play {
                platform {
                    playVersion = '${playVersion.toString()}'
                }
            }
        """
    }

    protected List<VersionNumber> getVersionsToTest() {
        final Annotation targetCoverage = this.getClass().getAnnotation(TargetCoverage)

        // All multi-version tests need to declare the annotation
        if (!targetCoverage) {
            throw new IllegalStateException("All multi-version tests need to declare the @TargetCoverage annotation")
        }

        // Allow target platform configuration for CI environments or if user provides explicit version
        if (System.getenv("CI") || USER_PROVIDED_PLAY_VERSION_SYS_PROP) {
            List<VersionNumber> userProvidedPlayVersions = determineUserProvidedVersions()

            // Apply user-provided Play versions if available
            // If no value was provided then use assigned annotation value
            if (userProvidedPlayVersions) {
                return userProvidedPlayVersions
            } else {
                return targetCoverage.value().newInstance(target, target).call() as List
            }
        } else {
            return [PlayCoverage.DEFAULT]
        }
    }

    private List<VersionNumber> determineUserProvidedVersions() {
        if (USER_PROVIDED_PLAY_VERSION_SYS_PROP) {
            if (USER_PROVIDED_PLAY_VERSION_SYS_PROP == DEFAULT_PLAY_VERSION_SYS_PROP_VALUE) {
                return [PlayCoverage.DEFAULT]
            } else if (USER_PROVIDED_PLAY_VERSION_SYS_PROP == ALL_PLAY_VERSIONS_SYS_PROP_VALUE) {
                return PlayCoverage.ALL
            }

            return [VersionNumber.parse(USER_PROVIDED_PLAY_VERSION_SYS_PROP)]
        } else {
            return []
        }
    }
}

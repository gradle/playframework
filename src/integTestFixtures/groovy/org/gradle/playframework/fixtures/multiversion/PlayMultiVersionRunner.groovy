package org.gradle.playframework.fixtures.multiversion

import org.gradle.playframework.extensions.internal.PlayMajorVersion
import org.gradle.util.VersionNumber

import java.lang.annotation.Annotation

class PlayMultiVersionRunner extends AbstractMultiTestRunner {

    public static final String DEFAULT_PLAY_VERSION_SYS_PROP_VALUE = 'default'
    public static final String ALL_PLAY_VERSIONS_SYS_PROP_VALUE = 'all'
    private static final String USER_PROVIDED_PLAY_VERSION_SYS_PROP = System.getProperty('playframework.int-test.target.version')
    private final Annotation targetCoverage

    PlayMultiVersionRunner(Class<?> target) {
        super(target)
        targetCoverage = target.getAnnotation(TargetCoverage)
    }

    @Override
    protected void createExecutions() {
        // All multi-version tests need to declare the annotation
        if (!targetCoverage) {
            throw new IllegalStateException("All multi-version tests need to declare the @TargetCoverage annotation")
        }

        // Allow target platform configuration for CI environments or if user provides explicit version
        if (System.getenv("CI") || USER_PROVIDED_PLAY_VERSION_SYS_PROP) {
            List<VersionNumber> userProvidedPlayVersions = determineUserProvidedPlayVersions()

            // Apply user-provided Play versions if available
            // If no value was provided then use assigned annotation value
            if (userProvidedPlayVersions) {
                createConfiguredVersionExecutions(userProvidedPlayVersions)
            } else {
                List<VersionNumber> playVersions = targetCoverage.value().newInstance(target, target).call() as List
                createConfiguredVersionExecutions(playVersions)
            }
        } else {
            createDefaultVersionExecution()
        }
    }

    private List<VersionNumber> determineUserProvidedPlayVersions() {
        if (USER_PROVIDED_PLAY_VERSION_SYS_PROP) {
            if (USER_PROVIDED_PLAY_VERSION_SYS_PROP == DEFAULT_PLAY_VERSION_SYS_PROP_VALUE) {
                return [PlayCoverage.DEFAULT]
            } else if (USER_PROVIDED_PLAY_VERSION_SYS_PROP == ALL_PLAY_VERSIONS_SYS_PROP_VALUE) {
                return PlayCoverage.ALL
            }

            return [VersionNumber.parse(USER_PROVIDED_PLAY_VERSION_SYS_PROP)]
        }
    }

    private void createConfiguredVersionExecutions(List<VersionNumber> playVersions) {
        List<VersionExecution> playVersionExecutions = playVersions.collect { new VersionExecution(it) }
        playVersionExecutions.each { add(it) }
    }

    private void createDefaultVersionExecution() {
        add(new VersionExecution(PlayCoverage.DEFAULT))
    }

    private static class VersionExecution extends AbstractMultiTestRunner.Execution {
        private final VersionNumber playVersion

        VersionExecution(VersionNumber playVersion) {
            this.playVersion = playVersion
        }

        @Override
        protected String getDisplayName() {
            return playVersion.toString()
        }

        @Override
        protected void before() {
            target.playVersion = playVersion
        }
    }
}

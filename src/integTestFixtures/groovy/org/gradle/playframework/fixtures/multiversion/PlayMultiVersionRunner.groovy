package org.gradle.playframework.fixtures.multiversion

import java.lang.annotation.Annotation

class PlayMultiVersionRunner extends AbstractMultiTestRunner {

    public static final String DEFAULT_TARGET_PLATFORM_SYS_PROP_VALUE = 'default'
    public static final String ALL_TARGET_PLATFORM_SYS_PROP_VALUE = 'all'
    private static final String USER_PROVIDED_TARGET_PLATFORM_SYS_PROP = System.getProperty('play.int-test.target.platform')
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
        if (System.getenv("CI") || USER_PROVIDED_TARGET_PLATFORM_SYS_PROP) {
            List<TargetPlatform> userProvidedTargetPlatforms = determineUserProvidedTargetPlatforms()

            // Apply user-provided target platforms if available
            // If no value was provided then use assigned annotation value
            if (userProvidedTargetPlatforms) {
                createConfiguredVersionExecutions(userProvidedTargetPlatforms)
            } else {
                List<TargetPlatform> targetPlatforms = targetCoverage.value().newInstance(target, target).call() as List
                createConfiguredVersionExecutions(targetPlatforms)
            }
        }

        createDefaultVersionExecution()
    }

    private List<TargetPlatform> determineUserProvidedTargetPlatforms() {
        if (USER_PROVIDED_TARGET_PLATFORM_SYS_PROP) {
            if (USER_PROVIDED_TARGET_PLATFORM_SYS_PROP == DEFAULT_TARGET_PLATFORM_SYS_PROP_VALUE) {
                return [PlayCoverage.DEFAULT]
            } else if (USER_PROVIDED_TARGET_PLATFORM_SYS_PROP == ALL_TARGET_PLATFORM_SYS_PROP_VALUE) {
                return PlayCoverage.ALL
            }

            return [TargetPlatform.forPlayVersion(USER_PROVIDED_TARGET_PLATFORM_SYS_PROP)]
        }
    }

    private void createConfiguredVersionExecutions(List<TargetPlatform> targetPlatforms) {
        List<VersionExecution> playVersionExecutions = targetPlatforms.collect { new VersionExecution(it) }
        playVersionExecutions.each { add(it) }
    }

    private void createDefaultVersionExecution() {
        add(new VersionExecution(PlayCoverage.DEFAULT))
    }

    private static class VersionExecution extends AbstractMultiTestRunner.Execution {
        private final TargetPlatform targetPlatform

        VersionExecution(TargetPlatform targetPlatform) {
            this.targetPlatform = targetPlatform
        }

        @Override
        protected String getDisplayName() {
            return targetPlatform.playVersion.toString()
        }

        @Override
        protected void before() {
            target.targetPlatform = targetPlatform
        }
    }
}

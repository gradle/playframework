package com.playframework.gradle.fixtures.multiversion

import java.lang.annotation.Annotation

class PlayMultiVersionRunner extends AbstractMultiTestRunner {

    public static final String ALL_COVERAGE_SYS_PROP = 'play.inttest.coverage.all'
    private final Annotation targetCoverage

    PlayMultiVersionRunner(Class<?> target) {
        super(target)
        targetCoverage = target.getAnnotation(TargetCoverage)
    }

    @Override
    protected void createExecutions() {
        Boolean allTargetPlatformsEnabled = Boolean.getBoolean(ALL_COVERAGE_SYS_PROP)

        if (targetCoverage && allTargetPlatformsEnabled) {
            List<TargetPlatform> targetPlatforms = targetCoverage.value().newInstance(target, target).call() as List
            createConfiguredVersionExecutions(targetPlatforms)
        } else {
            createDefaultVersionExecution()
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

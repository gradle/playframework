package com.playframework.gradle.fixtures.multiversion

import com.playframework.gradle.fixtures.PlayCoverage
import org.gradle.util.VersionNumber

import java.lang.annotation.Annotation

class PlayMultiVersionRunner extends AbstractMultiTestRunner {

    private final Annotation targetCoverage

    PlayMultiVersionRunner(Class<?> target) {
        super(target)
        targetCoverage = target.getAnnotation(TargetCoverage)
    }

    @Override
    protected void createExecutions() {
        if (targetCoverage) {
            List<VersionNumber> playVersions = targetCoverage.value().newInstance(target, target).call() as List
            createConfiguredVersionExecutions(playVersions)
        } else {
            createDefaultVersionExecution()
        }
    }

    private void createConfiguredVersionExecutions(List<VersionNumber> playVersions) {
        List<VersionExecution> playVersionExecutions = playVersions.collect { new VersionExecution(it) }
        playVersionExecutions.each { add(it) }
    }

    private void createDefaultVersionExecution() {
        add(new VersionExecution(PlayCoverage.DEFAULT_PLAY_VERSION))
    }

    private static class VersionExecution extends AbstractMultiTestRunner.Execution {
        private final VersionNumber versionNumber

        VersionExecution(VersionNumber versionNumber) {
            this.versionNumber = versionNumber
        }

        @Override
        protected String getDisplayName() {
            return versionNumber.toString()
        }

        @Override
        protected void before() {
            target.versionNumber = versionNumber
        }
    }
}

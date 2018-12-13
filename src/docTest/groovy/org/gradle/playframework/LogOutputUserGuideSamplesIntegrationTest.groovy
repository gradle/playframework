package org.gradle.playframework

import org.gradle.playframework.runner.GradlePluginSamplesRunner
import org.gradle.samples.test.runner.SamplesRoot
import org.junit.runner.RunWith

/**
 * Scans user guide samples root directory, executes task in discovered {@code sample.conf} file and inspects output.
 *
 * <b>Note:</b> The intention of this test suite is to inspect output. More elaborate testing is performed in {@link InDepthUserGuideSamplesIntegrationTest}.
 */
@RunWith(GradlePluginSamplesRunner)
@SamplesRoot("src/docs/samples")
class LogOutputUserGuideSamplesIntegrationTest {
}

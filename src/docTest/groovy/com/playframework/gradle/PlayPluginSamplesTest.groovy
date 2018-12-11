package com.playframework.gradle

import com.playframework.gradle.runner.GradlePluginSamplesRunner
import org.gradle.samples.test.runner.SamplesRoot
import org.junit.runner.RunWith

@RunWith(GradlePluginSamplesRunner)
@SamplesRoot("src/docs/samples")
class PlayPluginSamplesTest {
}

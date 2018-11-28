package com.lightbend.play.application.dependencies

import com.lightbend.play.application.PlayApplicationPluginIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.PlayAppWithDependencies
import org.gradle.play.internal.DefaultPlayPlatform
import org.gradle.util.VersionNumber

class PlayBinaryAppWithDependenciesIntegrationTest extends PlayApplicationPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayAppWithDependencies(VersionNumber.parse(DefaultPlayPlatform.DEFAULT_PLAY_VERSION))
    }
}

package com.lightbend.play.toolchain;

import org.gradle.play.platform.PlayPlatform;

public interface PlayToolChainInternal extends PlayToolChain, ToolChainInternal<PlayPlatform> {
    @Override
    PlayToolProvider select(PlayPlatform targetPlatform);

}

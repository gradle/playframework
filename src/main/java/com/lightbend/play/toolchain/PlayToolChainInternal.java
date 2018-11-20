package com.lightbend.play.toolchain;

import org.gradle.platform.base.internal.toolchain.ToolChainInternal;
import org.gradle.play.platform.PlayPlatform;

public interface PlayToolChainInternal extends PlayToolChain, ToolChainInternal<PlayPlatform> {
    @Override
    PlayToolProvider select(PlayPlatform targetPlatform);

}

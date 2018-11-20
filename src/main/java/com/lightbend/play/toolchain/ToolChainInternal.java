package com.lightbend.play.toolchain;

import org.gradle.platform.base.Platform;
import org.gradle.platform.base.ToolChain;

public interface ToolChainInternal<T extends Platform> extends ToolChain {
    /**
     * Locates the tools that can target the given platform.
     */
    ToolProvider select(T targetPlatform);
}

package com.lightbend.play.platform;

import org.gradle.api.Named;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import java.io.Serializable;

/**
 * The platform or runtime that a binary is designed to run on.
 *
 * Examples: the JvmPlatform defines a java runtime, while the NativePlatform defines the Operating System and Architecture for a native app.
 */
public interface Platform extends Named, Serializable {
    @Override
    @Input
    String getName();

    /**
     * Returns a human consumable name for this platform.
     *
     */
    @Internal
    String getDisplayName();
}

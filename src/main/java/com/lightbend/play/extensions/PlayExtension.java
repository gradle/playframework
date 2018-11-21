package com.lightbend.play.extensions;

import org.gradle.api.Action;
import org.gradle.api.Project;

public class PlayExtension {

    private final Platform platform;

    public PlayExtension(Project project) {
        platform = new Platform(project);
    }

    public void platform(Action<Platform> action) {
        action.execute(platform);
    }

    public Platform getPlatform() {
        return platform;
    }
}

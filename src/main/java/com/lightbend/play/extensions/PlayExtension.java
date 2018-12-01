package com.lightbend.play.extensions;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

public class PlayExtension {

    private final Platform platform;

    public PlayExtension(ObjectFactory objectFactory) {
        platform = objectFactory.newInstance(Platform.class, objectFactory);
    }

    public void platform(Action<? super Platform> action) {
        action.execute(platform);
    }

    public Platform getPlatform() {
        return platform;
    }
}

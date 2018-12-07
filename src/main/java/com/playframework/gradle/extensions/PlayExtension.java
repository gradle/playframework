package com.playframework.gradle.extensions;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class PlayExtension {

    private final PlayPlatform platform;
    private final Property<Boolean> injectedRoutesGenerator;

    public PlayExtension(ObjectFactory objectFactory) {
        platform = objectFactory.newInstance(PlayPlatform.class, objectFactory);
        injectedRoutesGenerator = objectFactory.property(Boolean.class);
    }

    public void platform(Action<? super PlayPlatform> action) {
        action.execute(platform);
    }

    public PlayPlatform getPlatform() {
        return platform;
    }

    public Property<Boolean> getInjectedRoutesGenerator() {
        return injectedRoutesGenerator;
    }
}

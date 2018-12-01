package com.lightbend.play.extensions;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class PlayExtension {

    private final Platform platform;
    private final Property<Boolean> injectedRoutesGenerator;

    public PlayExtension(ObjectFactory objectFactory) {
        platform = objectFactory.newInstance(Platform.class, objectFactory);
        injectedRoutesGenerator = objectFactory.property(Boolean.class);
    }

    public void platform(Action<? super Platform> action) {
        action.execute(platform);
    }

    public Platform getPlatform() {
        return platform;
    }

    public Property<Boolean> getInjectedRoutesGenerator() {
        return injectedRoutesGenerator;
    }
}

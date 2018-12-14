package org.gradle.playframework.extensions;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;

import static org.gradle.playframework.extensions.PlayPlatform.DEFAULT_PLAY_VERSION;

public class PlayExtension {

    private final PlayPlatform platform;
    private final Property<Boolean> injectedRoutesGenerator;

    public PlayExtension(ObjectFactory objectFactory) {
        platform = objectFactory.newInstance(PlayPlatform.class, objectFactory);
        platform.getPlayVersion().set(DEFAULT_PLAY_VERSION);
        platform.getScalaVersion().set(platform.getPlayVersion().map(playVersion -> PlayMajorVersion.forPlayVersion(playVersion).getDefaultScalaPlatform()));
        platform.getJavaVersion().set(JavaVersion.current());
        injectedRoutesGenerator = objectFactory.property(Boolean.class);
        injectedRoutesGenerator.set(false);
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

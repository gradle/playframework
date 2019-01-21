package org.gradle.playframework.extensions;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;

import static org.gradle.playframework.extensions.PlayPlatform.DEFAULT_PLAY_VERSION;

/**
 * The extension of the plugin allowing for configuring the target Play version used for the application.
 * <p>
 * It is not strictly necessary to configure the Scala and Java version. The plugin picks sensible default versions based on the provided Play version.
 * <p>
 * The following example demonstrate the use of the extension in a build script using the Groovy DSL:
 * <pre>
 * play {
 *     platform {
 *         playVersion = '2.6.14'
 *         scalaVersion = '2.11'
 *         javaVersion = JavaVersion.VERSION_1_9
 *     }
 *     injectedRoutesGenerator = true
 * }
 * </pre>
 */
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

    /**
     * Configures the target Play platform used for the application.
     *
     * @param action Action configuring the Play platform.
     */
    public void platform(Action<? super PlayPlatform> action) {
        action.execute(platform);
    }

    /**
     * Returns the Play platform.
     *
     * @return The Play platform
     * @see #platform(Action)
     */
    public PlayPlatform getPlatform() {
        return platform;
    }

    /**
     * Returns the property configuring if the default static router should be used.
     *
     * @return Property configuring the router
     */
    public Property<Boolean> getInjectedRoutesGenerator() {
        return injectedRoutesGenerator;
    }
}

package org.gradle.playframework.extensions;

import org.gradle.api.JavaVersion;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.util.VersionNumber;

import javax.inject.Inject;

/**
 * Configures the Play platform used for the application including the Play version, Scala version and the Java version.
 */
public class PlayPlatform {

    /**
     * The default Play version used if no Play version was configured.
     */
    public static final String DEFAULT_PLAY_VERSION = "2.6.25";

    private final Property<String> playVersion;
    private final Property<String> scalaVersion;
    private final Property<JavaVersion> javaVersion;

    @Inject
    public PlayPlatform(ObjectFactory objectFactory) {
        playVersion = objectFactory.property(String.class);
        scalaVersion = objectFactory.property(String.class);
        javaVersion = objectFactory.property(JavaVersion.class);
    }

    /**
     * Returns the Play version.
     * <p>
     * Used the default Play version if it wasn't configured through the extension.
     *
     * @return The Play version
     */
    @Input
    public Property<String> getPlayVersion() {
        return playVersion;
    }

    /**
     * Returns the Scala version.
     * <p>
     * Uses a sensible default version based on the configured Play version if no value was provided.
     *
     * @return The Scala version
     */
    @Input
    public Property<String> getScalaVersion() {
        return scalaVersion;
    }

    /**
     * Returns the Scala compatibility version.
     * <p>
     * The Scala compatibility version is comprised of the major and minor version of the configured Scala version.
     * For example, the compatibility version of Scala 2.12.2 is 2.12.
     *
     * @return The Scala compatibility version
     * @see #getScalaVersion()
     */
    @Internal
    public Provider<String> getScalaCompatibilityVersion() {
        return scalaVersion.map(s -> {
            VersionNumber versionNumber = VersionNumber.parse(s);
            return versionNumber.getMajor() + "." + versionNumber.getMinor();
        });
    }

    /**
     * Returns the Java version.
     * <p>
     * Uses a sensible default version based on the configured Play version if no value was provided.
     *
     * @return The Java version
     */
    @Input
    public Property<JavaVersion> getJavaVersion() {
        return javaVersion;
    }

    /**
     * Returns the dependency notation (group/name/version) for a given Play module including its compatible Scala version.
     * <p>
     * For example, the dependency notation for the Play module "play-docs" for Play 2.6.15 and Scala 2.12.2 would evaluate to "com.typesafe.play:play-docs_2.12:2.6.15".
     *
     * @param playModule The Play module
     * @return The dependency notation
     * @see #getScalaCompatibilityVersion()
     */
    public Provider<String> getDependencyNotation(String playModule) {
        return getScalaCompatibilityVersion().map(s -> "com.typesafe.play:" + playModule + "_" + s + ":" + playVersion.get());
    }
}

package org.gradle.playframework.tools.internal.routes;

import java.util.Arrays;
import org.gradle.playframework.util.VersionNumber;

abstract class DefaultVersionedRoutesCompilerAdapter implements VersionedRoutesCompilerAdapter {
    private static final Iterable<String> SHARED_PACKAGES = Arrays.asList("play.router", "scala.collection", "scala.collection.mutable", "scala.util.matching", "play.routes.compiler");
    private final VersionNumber playVersion;
    private final String scalaVersion;

    public DefaultVersionedRoutesCompilerAdapter(String playVersion, String scalaVersion) {
        this.playVersion = VersionNumber.parse(playVersion);
        this.scalaVersion = scalaVersion;
    }

    protected boolean isGenerateRefReverseRouter() {
        return false;
    }

    @Override
    public String getDependencyNotation() {
        if (playVersion.equals(VersionNumber.parse("2.7.4")) ||
            playVersion.equals(VersionNumber.parse("2.7.5"))) {
            // this is a hack because of a Play Framework issue
            // See: https://github.com/playframework/playframework/issues/10333
            return "com.typesafe.play:routes-compiler_" + scalaVersion + ":2.7.3";
        }

        if (playVersion.isLowerThan(VersionNumber.parse("2.9.0"))) {
            return "com.typesafe.play:routes-compiler_" + scalaVersion + ":" + playVersion;
        }
        // https://www.playframework.com/documentation/3.0.x/Migration29#Changed-artifact-names
        return "com.typesafe.play:play-routes-compiler_" + scalaVersion + ":" + playVersion;
    }

    @Override
    public Iterable<String> getClassLoaderPackages() {
        return SHARED_PACKAGES;
    }
}

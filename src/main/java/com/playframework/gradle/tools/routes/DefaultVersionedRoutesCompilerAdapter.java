package com.playframework.gradle.tools.routes;

import java.util.Arrays;

abstract class DefaultVersionedRoutesCompilerAdapter implements VersionedRoutesCompilerAdapter {
    private static final Iterable<String> SHARED_PACKAGES = Arrays.asList("play.router", "scala.collection", "scala.collection.mutable", "scala.util.matching", "play.routes.compiler");
    private final String playVersion;
    private final String scalaVersion;

    public DefaultVersionedRoutesCompilerAdapter(String playVersion, String scalaVersion) {
        this.playVersion = playVersion;
        this.scalaVersion = scalaVersion;
    }

    protected boolean isGenerateRefReverseRouter() {
        return false;
    }

    @Override
    public String getDependencyNotation() {
        return "com.typesafe.play:routes-compiler_" + scalaVersion + ":" + playVersion;
    }

    @Override
    public Iterable<String> getClassLoaderPackages() {
        return SHARED_PACKAGES;
    }
}

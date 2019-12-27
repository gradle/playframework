package org.gradle.playframework.extensions;

import org.apache.groovy.util.Maps;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;

public class PlayDependencies {
    private final Project project;
    private final PlayExtension playExtension;

    @Inject
    public PlayDependencies(Project project, PlayExtension playExtension) {
        this.project = project;
        this.playExtension = playExtension;
    }

    private static String extractMajorMinorVersion(String version) {
        return String.join(".", version.split("\\.", 2));
    }

    private String lazyGetScalaMajorMinorVersion() {
        return playExtension.getPlatform().getScalaVersion().map(PlayDependencies::extractMajorMinorVersion).get();
    }

    public Provider<ExternalModuleDependency> evolutions() {
        return playComponent("play-jdbc-evolutions");
    }

    public Provider<ExternalModuleDependency> jdbc() {
        return playComponent("play-jdbc");
    }

    public Provider<ExternalModuleDependency> javaCore() {
        return playComponent("play-java");
    }

    public Provider<ExternalModuleDependency> javaJdbc() {
        return playComponent("play-java-jdbc");
    }

    public Provider<ExternalModuleDependency> javaJpa() {
        return playComponent("play-java-jpa");
    }

    public Provider<ExternalModuleDependency> filters() {
        return playComponent("filters-helpers");
    }

    public Provider<ExternalModuleDependency> cache() {
        return playComponent("play-cache");
    }

    public Provider<ExternalModuleDependency> json() {
        return playComponent("play-json");
    }

    public Provider<ExternalModuleDependency> ws() {
        return playComponent("play-ws");
    }

    public Provider<ExternalModuleDependency> javaWs() {
        return playComponent("play-java-ws");
    }

    public Provider<ExternalModuleDependency> specs2() {
        return playComponent("play-specs2");
    }

    public Provider<ExternalModuleDependency> playComponent(String id) {
        return scalaVersionedInternal("com.typesafe.play", id, playExtension.getPlatform().getPlayVersion());
    }

    public Provider<ExternalModuleDependency> scalaVersioned(String group, String name, String version) {
        return scalaVersionedInternal(group, name, project.provider(() -> version));
    }

    private Provider<ExternalModuleDependency> scalaVersionedInternal(String group, String name, Provider<String> version) {
        return project.provider(() -> (ExternalModuleDependency) project.getDependencies().create(
            Maps.of(
                "group", group,
                "name", name + "_" + lazyGetScalaMajorMinorVersion(),
                "version", version
            )
        ));
    }
}

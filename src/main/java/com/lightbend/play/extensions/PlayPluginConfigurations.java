package com.lightbend.play.extensions;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.internal.file.collections.ImmutableFileCollection;
import org.gradle.api.internal.file.collections.LazilyInitializedFileCollection;
import org.gradle.api.internal.tasks.TaskDependencyResolveContext;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayPluginConfigurations {

    public static final String PLATFORM_CONFIGURATION = "playPlatform";
    public static final String COMPILE_CONFIGURATION = "play";
    public static final String RUN_CONFIGURATION = "playRun";
    public static final String TEST_COMPILE_CONFIGURATION = "playTest";

    private final ConfigurationContainer configurations;
    private final DependencyHandler dependencyHandler;

    public PlayPluginConfigurations(ConfigurationContainer configurations, DependencyHandler dependencyHandler) {
        this.configurations = configurations;
        this.dependencyHandler = dependencyHandler;
        Configuration playPlatform = configurations.create(PLATFORM_CONFIGURATION);

        Configuration playCompile = configurations.create(COMPILE_CONFIGURATION);
        playCompile.extendsFrom(playPlatform);

        Configuration playRun = configurations.create(RUN_CONFIGURATION);
        playRun.extendsFrom(playCompile);

        Configuration playTestCompile = configurations.create(TEST_COMPILE_CONFIGURATION);
        playTestCompile.extendsFrom(playCompile);

        configurations.maybeCreate(Dependency.DEFAULT_CONFIGURATION).extendsFrom(playCompile);
    }

    public PlayConfiguration getPlayPlatform() {
        return new PlayConfiguration(PLATFORM_CONFIGURATION);
    }

    public PlayConfiguration getPlay() {
        return new PlayConfiguration(COMPILE_CONFIGURATION);
    }

    public PlayConfiguration getPlayRun() {
        return new PlayConfiguration(RUN_CONFIGURATION);
    }

    public PlayConfiguration getPlayTest() {
        return new PlayConfiguration(TEST_COMPILE_CONFIGURATION);
    }

    /**
     * Wrapper around a Configuration instance used by the PlayApplicationPlugin.
     */
    public class PlayConfiguration {
        private final String name;

        PlayConfiguration(String name) {
            this.name = name;
        }

        public Configuration getConfiguration() {
            return configurations.getByName(name);
        }

        public FileCollection getAllArtifacts() {
            return getConfiguration();
        }

        public FileCollection getChangingArtifacts() {
            return new FilterByProjectComponentTypeFileCollection(getConfiguration(), true);
        }

        public FileCollection getNonChangingArtifacts() {
            return new FilterByProjectComponentTypeFileCollection(getConfiguration(), false);
        }

        public void addDependency(Object notation) {
            dependencyHandler.add(name, notation);
        }

        public void addArtifact(PublishArtifact artifact) {
            configurations.getByName(name).getArtifacts().add(artifact);
        }
    }

    private static class FilterByProjectComponentTypeFileCollection extends LazilyInitializedFileCollection {
        private final Configuration configuration;
        private final boolean matchProjectComponents;

        private FilterByProjectComponentTypeFileCollection(Configuration configuration, boolean matchProjectComponents) {
            this.configuration = configuration;
            this.matchProjectComponents = matchProjectComponents;
        }

        @Override
        public String getDisplayName() {
            return configuration.toString();
        }

        @Override
        public FileCollectionInternal createDelegate() {
            Set<File> files = new HashSet<>();
            for (ResolvedArtifact artifact : configuration.getResolvedConfiguration().getResolvedArtifacts()) {
                if ((artifact.getId().getComponentIdentifier() instanceof ProjectComponentIdentifier) == matchProjectComponents) {
                    files.add(artifact.getFile());
                }
            }
            return ImmutableFileCollection.of(Collections.unmodifiableSet(files));
        }

        @Override
        public void visitDependencies(TaskDependencyResolveContext context) {
            context.add(configuration);
        }
    }
}

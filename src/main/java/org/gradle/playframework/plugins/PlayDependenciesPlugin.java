package org.gradle.playframework.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.playframework.extensions.PlayDependencies;
import org.gradle.playframework.extensions.PlayExtension;

/**
 * Plugin for Play dependencies support.
 */
public class PlayDependenciesPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(PlayConventionPlugin.class);

        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PlayApplicationPlugin.PLAY_EXTENSION_NAME);
        project.getDependencies().getExtensions().create("playDep", PlayDependencies.class, project, playExtension);
    }
}

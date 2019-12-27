package org.gradle.playframework.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.playframework.extensions.PlayExtension;

import static org.gradle.playframework.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;

public class PlayConventionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create(PLAY_EXTENSION_NAME, PlayExtension.class, project.getObjects());
    }
}

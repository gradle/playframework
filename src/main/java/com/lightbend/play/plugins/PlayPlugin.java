package com.lightbend.play.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PlayPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(PlayApplicationPlugin.class);
    }
}

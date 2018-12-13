package org.gradle.playframework.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Plugin for configuring IDE plugins when the project uses the Play Framework support.
 *
 * <p>NOTE: This currently supports configuring the 'idea' plugin only.</p>
 */
public class PlayIdePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("idea", appliedPlugin -> project.getPluginManager().apply(PlayIdeaPlugin.class));
    }
}

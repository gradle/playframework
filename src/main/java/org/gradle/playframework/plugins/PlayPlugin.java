package org.gradle.playframework.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.playframework.tasks.JavaScriptMinify;

import static org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME;

public class PlayPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        applyPlugins(project);
        configureJavaScriptTasks(project);
    }

    private static void applyPlugins(Project project) {
        project.getPluginManager().apply(PlayApplicationPlugin.class);
        project.getPluginManager().apply(PlayJavaScriptPlugin.class);
        project.getPluginManager().apply(PlayDistributionPlugin.class);
        project.getPluginManager().apply(PlayIdePlugin.class);
    }

    private static void configureJavaScriptTasks(Project project) {
        TaskProvider<JavaScriptMinify> javaScriptMinifyTask = project.getTasks().named(PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME, JavaScriptMinify.class);

        project.getTasks().named(ASSEMBLE_TASK_NAME, task -> task.dependsOn(javaScriptMinifyTask));

        project.getTasks().named(PlayApplicationPlugin.ASSETS_JAR_TASK_NAME, Jar.class, task -> {
            task.dependsOn(javaScriptMinifyTask);
            task.from(javaScriptMinifyTask.get().getDestinationDir(), copySpec -> copySpec.into("public"));
        });
    }
}

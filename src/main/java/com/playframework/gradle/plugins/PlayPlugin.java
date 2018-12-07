package com.playframework.gradle.plugins;

import com.playframework.gradle.tasks.JavaScriptMinify;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.play.tasks.PlayCoffeeScriptCompile;

import static com.playframework.gradle.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME;
import static com.playframework.gradle.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME;
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
        project.getPluginManager().apply(PlayCoffeeScriptPlugin.class);
        project.getPluginManager().apply(PlayTestPlugin.class);
        project.getPluginManager().apply(PlayDistributionPlugin.class);
        project.getPluginManager().apply(PlayIdePlugin.class);
    }

    private static void configureJavaScriptTasks(Project project) {
        TaskProvider<PlayCoffeeScriptCompile> playCoffeeScriptCompileTask = project.getTasks().named(COFFEESCRIPT_COMPILE_TASK_NAME, PlayCoffeeScriptCompile.class);

        TaskProvider<JavaScriptMinify> javaScriptMinifyTask = project.getTasks().named(PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME, JavaScriptMinify.class, task -> {
            task.mustRunAfter(playCoffeeScriptCompileTask);
            FileTree combinedJavaScriptSources = task.getSource().plus(project.fileTree(playCoffeeScriptCompileTask.get().getDestinationDir()));
            task.setSource(combinedJavaScriptSources);
        });

        project.getTasks().named(ASSEMBLE_TASK_NAME, task -> {
            task.dependsOn(playCoffeeScriptCompileTask);
            task.dependsOn(javaScriptMinifyTask);
        });

        project.getTasks().named(ASSETS_JAR_TASK_NAME, Jar.class, task -> {
            task.dependsOn(javaScriptMinifyTask);
            task.from(javaScriptMinifyTask.get().getDestinationDir(), copySpec -> copySpec.into("public"));
        });
    }
}

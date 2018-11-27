package com.lightbend.play.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.play.tasks.JavaScriptMinify;
import org.gradle.play.tasks.PlayCoffeeScriptCompile;

import static com.lightbend.play.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME;
import static com.lightbend.play.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME;
import static com.lightbend.play.plugins.PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME;
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
    }

    private static void configureJavaScriptTasks(Project project) {
        Task assembleTask = project.getTasks().getByName(ASSEMBLE_TASK_NAME);
        JavaScriptMinify javaScriptMinifyTask = getJavaScriptMinifyTask(project);
        assembleTask.dependsOn(javaScriptMinifyTask);
        assembleTask.dependsOn(getCoffeeScriptCompileTask(project));

        Jar assetsJarTask = (Jar) project.getTasks().getByName(ASSETS_JAR_TASK_NAME);
        assetsJarTask.dependsOn(javaScriptMinifyTask);
        assetsJarTask.from(javaScriptMinifyTask.getDestinationDir(), copySpec -> copySpec.into("public"));
    }

    private static JavaScriptMinify getJavaScriptMinifyTask(Project project) {
        return (JavaScriptMinify) project.getTasks().getByName(JS_MINIFY_TASK_NAME);
    }

    private static PlayCoffeeScriptCompile getCoffeeScriptCompileTask(Project project) {
        return (PlayCoffeeScriptCompile) project.getTasks().getByName(COFFEESCRIPT_COMPILE_TASK_NAME);
    }
}

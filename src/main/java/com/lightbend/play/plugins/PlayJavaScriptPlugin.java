package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.JavaScriptMinify;

import java.io.File;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;

/**
 * Plugin for adding javascript processing to a Play application.
 */
public class PlayJavaScriptPlugin implements Plugin<Project> {

    public static final String JS_MINIFY_TASK_NAME = "minifyJavaScript";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(BasePlugin.class);

        SourceDirectorySet sourceDirectory = createDefaultSourceDirectorySet(project);
        createDefaultJavaScriptMinifyTask(project, sourceDirectory);
    }

    private SourceDirectorySet createDefaultSourceDirectorySet(Project project) {
        SourceDirectorySet sourceDirectory = project.getObjects().sourceDirectorySet("javaScript", "JavaScript source files");
        sourceDirectory.srcDir("app/assets");
        sourceDirectory.include("**/*.js");
        return sourceDirectory;
    }

    private JavaScriptMinify createDefaultJavaScriptMinifyTask(Project project, SourceDirectorySet sourceDirectory) {
        PlayPlatform playPlatform = ((PlayExtension)project.getExtensions().getByName(PLAY_EXTENSION_NAME)).getPlatform().asPlayPlatform();

        return project.getTasks().create(JS_MINIFY_TASK_NAME, JavaScriptMinify.class, javaScriptMinify -> {
            javaScriptMinify.setDescription("Minifies javascript for the " + sourceDirectory.getDisplayName() + ".");
            File generatedSourceDir = new File(project.getBuildDir(), "src");
            File outputDirectory = new File(generatedSourceDir, sourceDirectory.getName());
            javaScriptMinify.setDestinationDir(outputDirectory);
            javaScriptMinify.setSource(sourceDirectory.getSrcDirs());
            javaScriptMinify.setPlayPlatform(playPlatform);
            javaScriptMinify.dependsOn(sourceDirectory);
        });
    }
}

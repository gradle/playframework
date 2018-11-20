package com.lightbend.play.plugins;

import com.lightbend.play.tasks.JavaScriptMinify;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.play.platform.PlayPlatform;

import java.io.File;

import static com.lightbend.play.plugins.PlayPlatformHelper.createDefaultPlayPlatform;

public class PlayJavaScriptPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        SourceDirectorySet sourceDirectory = createSourceDirectorySet(project);
        createJavaScriptMinify(project, sourceDirectory);
    }

    private SourceDirectorySet createSourceDirectorySet(Project project) {
        SourceDirectorySet sourceDirectory = project.getObjects().sourceDirectorySet("javaScript", "JavaScript source files");
        sourceDirectory.srcDir("app/assets");
        sourceDirectory.include("**/*.js");
        return sourceDirectory;
    }

    private JavaScriptMinify createJavaScriptMinify(Project project, SourceDirectorySet sourceDirectory) {
        PlayPlatform playPlatform = createDefaultPlayPlatform();

        return project.getTasks().create("minifyJavaScript", JavaScriptMinify.class, javaScriptMinify -> {
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

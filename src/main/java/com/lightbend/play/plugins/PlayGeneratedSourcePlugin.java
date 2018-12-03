package com.lightbend.play.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;

import java.io.File;

public interface PlayGeneratedSourcePlugin extends Plugin<Project> {

    String GENERATED_SOURCE_ROOT_DIR_PATH = "src/play";

    default File getOutputDir(Project project, SourceDirectorySet sourceDirectorySet) {
        File generatedSourceRootDir = new File(project.getBuildDir(), GENERATED_SOURCE_ROOT_DIR_PATH);
        return new File(generatedSourceRootDir, sourceDirectorySet.getName());
    }
}

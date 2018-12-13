package org.gradle.playframework.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Provider;

public interface PlayGeneratedSourcePlugin extends Plugin<Project> {

    String GENERATED_SOURCE_ROOT_DIR_PATH = "src/play";

    default Provider<Directory> getOutputDir(Project project, SourceDirectorySet sourceDirectorySet) {
        DirectoryProperty buildDir = project.getLayout().getBuildDirectory();
        return buildDir.dir(GENERATED_SOURCE_ROOT_DIR_PATH + "/" + sourceDirectorySet.getName());
    }
}

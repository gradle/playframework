package com.lightbend.play.plugins;

import com.lightbend.play.extensions.Platform;
import com.lightbend.play.extensions.PlayExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.RoutesCompile;

import java.io.File;
import java.util.ArrayList;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;

/**
 * Plugin for compiling Play routes sources in a Play application.
 */
public class PlayRoutesPlugin implements Plugin<Project> {

    public static final String ROUTES_COMPILE_TASK_NAME = "compileRoutes";

    @Override
    public void apply(Project project) {
        Platform platform = ((PlayExtension)project.getExtensions().getByName(PLAY_EXTENSION_NAME)).getPlatform();
        PlayPlatform playPlatform = platform.asPlayPlatform();

        SourceDirectorySet sourceDirectory = createDefaultSourceDirectorySet(project);
        RoutesCompile routesCompile = createDefaultRoutesCompileTask(project, sourceDirectory, playPlatform);

        // TODO: RoutesCompile should use Provider types to avoid afterEvaluate
        project.afterEvaluate(project1 -> routesCompile.setInjectedRoutesGenerator(platform.getInjectedRoutesGenerator().get()));
    }

    private SourceDirectorySet createDefaultSourceDirectorySet(Project project) {
        SourceDirectorySet sourceDirectory = project.getObjects().sourceDirectorySet("routes", "Routes source files");
        sourceDirectory.srcDir("conf");
        sourceDirectory.include("routes", "*.routes");
        return sourceDirectory;
    }

    private RoutesCompile createDefaultRoutesCompileTask(Project project, SourceDirectorySet sourceDirectory, PlayPlatform playPlatform) {
        return project.getTasks().create(ROUTES_COMPILE_TASK_NAME, RoutesCompile.class, routesCompile -> {
            routesCompile.setDescription("Generates routes for the '" + sourceDirectory.getDisplayName() + "' source set.");
            File generatedSourceDir = new File(project.getBuildDir(), "src");
            File outputDirectory = new File(generatedSourceDir, sourceDirectory.getName());

            routesCompile.setPlatform(playPlatform);
            routesCompile.setAdditionalImports(new ArrayList<>());
            routesCompile.setSource(sourceDirectory.getSrcDirs());
            routesCompile.setOutputDirectory(outputDirectory);
        });
    }
}

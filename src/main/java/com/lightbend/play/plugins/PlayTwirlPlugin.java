package com.lightbend.play.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.language.twirl.TwirlImports;
import org.gradle.play.internal.platform.PlayPlatformInternal;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.TwirlCompile;

import java.io.File;
import java.util.ArrayList;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_CONFIGURATIONS_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayPlatformHelper.createDefaultPlayPlatform;

/**
 * Plugin for compiling Twirl sources in a Play application.
 */
public class PlayTwirlPlugin implements Plugin<Project> {

    public static final String TWIRL_COMPILE_TASK_NAME = "compileTwirl";

    @Override
    public void apply(Project project) {
        PlayPlatform playPlatform = createDefaultPlayPlatform();
        PlayPluginConfigurations configurations = (PlayPluginConfigurations) project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);

        SourceDirectorySet sourceDirectory = createDefaultSourceDirectorySet(project);
        TwirlCompile twirlCompile = createDefaultTwirlCompileTask(project, sourceDirectory, playPlatform);

        project.afterEvaluate(project1 -> {
            if (hasTwirlSourceSetsWithJavaImports(twirlCompile)) {
                configurations.getPlay().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play-java"));
            }
        });
    }

    private SourceDirectorySet createDefaultSourceDirectorySet(Project project) {
        SourceDirectorySet sourceDirectory = project.getObjects().sourceDirectorySet("twirl", "Twirl source files");
        sourceDirectory.srcDir("app");
        return sourceDirectory;
    }

    private TwirlCompile createDefaultTwirlCompileTask(Project project, SourceDirectorySet sourceDirectory, PlayPlatform playPlatform) {
        return project.getTasks().create(TWIRL_COMPILE_TASK_NAME, TwirlCompile.class, twirlCompile -> {
            twirlCompile.setDescription("Compiles Twirl templates for the '" + sourceDirectory.getDisplayName() + "' source set.");
            File generatedSourceDir = new File(project.getBuildDir(), "src");
            twirlCompile.setPlatform(playPlatform);
            twirlCompile.setSource(sourceDirectory.getSrcDirs());
            File outputDirectory = new File(generatedSourceDir, sourceDirectory.getName());
            twirlCompile.setOutputDirectory(outputDirectory);
            twirlCompile.setDefaultImports(TwirlImports.SCALA);
            twirlCompile.setUserTemplateFormats(new ArrayList<>());
            twirlCompile.setAdditionalImports(new ArrayList<>());
        });
    }

    private boolean hasTwirlSourceSetsWithJavaImports(TwirlCompile twirlCompile) {
        return twirlCompile.getDefaultImports() == TwirlImports.JAVA;
    }
}

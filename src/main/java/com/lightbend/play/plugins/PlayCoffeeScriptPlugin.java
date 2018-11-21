package com.lightbend.play.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.play.tasks.PlayCoffeeScriptCompile;

import java.io.File;

/**
 * Plugin for adding coffeescript compilation to a Play application.
 */
public class PlayCoffeeScriptPlugin implements Plugin<Project> {

    public static final String COFFEESCRIPT_COMPILE_TASK_NAME = "compileCoffeeScript";
    private static final String DEFAULT_COFFEESCRIPT_VERSION = "1.8.0";
    private static final String DEFAULT_RHINO_VERSION = "1.7R4";

    static String getDefaultCoffeeScriptDependencyNotation() {
        return "org.coffeescript:coffee-script-js:" + DEFAULT_COFFEESCRIPT_VERSION + "@js";
    }

    static String getDefaultRhinoDependencyNotation() {
        return "org.mozilla:rhino:" + DEFAULT_RHINO_VERSION;
    }

    @Override
    public void apply(Project project) {
        SourceDirectorySet sourceDirectory = createDefaultSourceDirectorySet(project);

        project.getTasks().withType(PlayCoffeeScriptCompile.class, coffeeScriptCompile -> {
            coffeeScriptCompile.setRhinoClasspathNotation(getDefaultRhinoDependencyNotation());
            coffeeScriptCompile.setCoffeeScriptJsNotation(getDefaultCoffeeScriptDependencyNotation());
        });

        createDefaultCoffeeScriptCompileTask(project, sourceDirectory);
    }

    private SourceDirectorySet createDefaultSourceDirectorySet(Project project) {
        SourceDirectorySet sourceDirectory = project.getObjects().sourceDirectorySet("coffeeScript", "CoffeeScript source files");
        sourceDirectory.srcDir("app/assets");
        sourceDirectory.include("**/*.coffee");
        return sourceDirectory;
    }

    private PlayCoffeeScriptCompile createDefaultCoffeeScriptCompileTask(Project project, SourceDirectorySet sourceDirectory) {
        return project.getTasks().create(COFFEESCRIPT_COMPILE_TASK_NAME, PlayCoffeeScriptCompile.class, coffeeScriptCompile -> {
            coffeeScriptCompile.setDescription("Compiles coffeescript for the '" + sourceDirectory.getDisplayName() + "' source set.");
            File generatedSourceDir = new File(project.getBuildDir(), "src");
            File outputDirectory = new File(generatedSourceDir, sourceDirectory.getName());

            coffeeScriptCompile.setDestinationDir(outputDirectory);
            coffeeScriptCompile.setSource(sourceDirectory.getSrcDirs());
        });
    }
}

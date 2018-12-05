package com.lightbend.play.plugins;

import com.lightbend.play.sourcesets.CoffeeScriptSourceSet;
import com.lightbend.play.sourcesets.DefaultCoffeeScriptSourceSet;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.play.tasks.PlayCoffeeScriptCompile;

import static com.lightbend.play.plugins.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for adding coffeescript compilation to a Play application.
 */
public class PlayCoffeeScriptPlugin implements PlayGeneratedSourcePlugin {

    public static final String COFFEESCRIPT_COMPILE_TASK_NAME = "compilePlayCoffeeScript";
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
        CoffeeScriptSourceSet coffeeScriptSourceSet = createCustomSourceSet(project, DefaultCoffeeScriptSourceSet.class, "coffeeScript");

        project.getTasks().withType(PlayCoffeeScriptCompile.class, coffeeScriptCompile -> {
            coffeeScriptCompile.setRhinoClasspathNotation(getDefaultRhinoDependencyNotation());
            coffeeScriptCompile.setCoffeeScriptJsNotation(getDefaultCoffeeScriptDependencyNotation());
        });

        createDefaultCoffeeScriptCompileTask(project, coffeeScriptSourceSet.getCoffeeScript());
    }

    private PlayCoffeeScriptCompile createDefaultCoffeeScriptCompileTask(Project project, SourceDirectorySet sourceDirectory) {
        return project.getTasks().create(COFFEESCRIPT_COMPILE_TASK_NAME, PlayCoffeeScriptCompile.class, coffeeScriptCompile -> {
            coffeeScriptCompile.setDescription("Compiles coffeescript for the '" + sourceDirectory.getDisplayName() + "' source set.");
            coffeeScriptCompile.setDestinationDir(getOutputDir(project, sourceDirectory).get().getAsFile());
            coffeeScriptCompile.setSource(sourceDirectory);
        });
    }
}

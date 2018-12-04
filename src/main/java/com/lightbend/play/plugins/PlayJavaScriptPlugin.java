package com.lightbend.play.plugins;

import com.lightbend.play.sourcesets.DefaultJavaScriptSourceSet;
import com.lightbend.play.sourcesets.JavaScriptSourceSet;
import com.lightbend.play.tasks.JavaScriptMinify;
import com.lightbend.play.tools.javascript.GoogleClosureCompiler;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;

import static com.lightbend.play.plugins.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for adding javascript processing to a Play application.
 */
public class PlayJavaScriptPlugin implements PlayGeneratedSourcePlugin {

    public static final String COMPILER_CONFIGURATION_NAME = "javaScriptCompiler";
    public static final String JS_MINIFY_TASK_NAME = "minifyPlayJavaScript";

    @Override
    public void apply(Project project) {
        Configuration compilerConfiguration = createCompilerConfiguration(project);
        declareDefaultDependencies(project, compilerConfiguration);
        JavaScriptSourceSet javaScriptSourceSet = createCustomSourceSet(project, DefaultJavaScriptSourceSet.class, "javaScript");
        createDefaultJavaScriptMinifyTask(project, javaScriptSourceSet.getJavaScript(), compilerConfiguration);
    }

    private Configuration createCompilerConfiguration(Project project) {
        Configuration compilerConfiguration = project.getConfigurations().create(COMPILER_CONFIGURATION_NAME);
        compilerConfiguration.setVisible(false);
        compilerConfiguration.setTransitive(true);
        compilerConfiguration.setDescription("The JavaScript compiler library used to minify assets.");
        return compilerConfiguration;
    }

    private void declareDefaultDependencies(Project project, Configuration configuration) {
        configuration.defaultDependencies(dependencies -> dependencies.add(project.getDependencies().create(GoogleClosureCompiler.getDependencyNotation())));
    }

    private JavaScriptMinify createDefaultJavaScriptMinifyTask(Project project, SourceDirectorySet sourceDirectory, Configuration compilerConfiguration) {
        return project.getTasks().create(JS_MINIFY_TASK_NAME, JavaScriptMinify.class, javaScriptMinify -> {
            javaScriptMinify.setDescription("Minifies javascript for the " + sourceDirectory.getDisplayName() + ".");
            javaScriptMinify.setDestinationDir(getOutputDir(project, sourceDirectory));
            javaScriptMinify.setSource(sourceDirectory);
            javaScriptMinify.setCompilerClasspath(compilerConfiguration);
            javaScriptMinify.dependsOn(sourceDirectory);
        });
    }
}

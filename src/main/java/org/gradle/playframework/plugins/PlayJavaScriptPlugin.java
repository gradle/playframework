package org.gradle.playframework.plugins;

import org.gradle.playframework.plugins.internal.PlayPluginHelper;
import org.gradle.playframework.sourcesets.JavaScriptSourceSet;
import org.gradle.playframework.sourcesets.internal.DefaultJavaScriptSourceSet;
import org.gradle.playframework.tasks.JavaScriptMinify;
import org.gradle.playframework.tools.internal.javascript.GoogleClosureCompiler;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;

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
        JavaScriptSourceSet javaScriptSourceSet = PlayPluginHelper.createCustomSourceSet(project, DefaultJavaScriptSourceSet.class, "javaScript");
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

    private void createDefaultJavaScriptMinifyTask(Project project, SourceDirectorySet sourceDirectory, Configuration compilerConfiguration) {
        project.getTasks().register(JS_MINIFY_TASK_NAME, JavaScriptMinify.class, javaScriptMinify -> {
            javaScriptMinify.setDescription("Minifies javascript for the " + sourceDirectory.getDisplayName() + ".");
            javaScriptMinify.getDestinationDir().set(getOutputDir(project, sourceDirectory));
            javaScriptMinify.setSource(sourceDirectory);
            javaScriptMinify.getCompilerClasspath().setFrom(compilerConfiguration);
            javaScriptMinify.dependsOn(sourceDirectory);
        });
    }
}

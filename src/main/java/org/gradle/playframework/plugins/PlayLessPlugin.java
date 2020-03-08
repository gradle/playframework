package org.gradle.playframework.plugins;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.playframework.sourcesets.LessSourceSet;
import org.gradle.playframework.sourcesets.internal.DefaultLessSourceSet;
import org.gradle.playframework.tasks.LessCompile;
import org.gradle.playframework.tasks.PlayRun;
import org.gradle.playframework.tools.internal.less.Less4jCompiler;

import static org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME;
import static org.gradle.playframework.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME;
import static org.gradle.playframework.plugins.PlayApplicationPlugin.PLAY_RUN_TASK_NAME;
import static org.gradle.playframework.plugins.internal.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for compiling LESS stylesheets into CSS stylesheets in a Play application.
 */
public class PlayLessPlugin implements PlayGeneratedSourcePlugin {
    public static final String LESS_COMPILER_CONFIGURATION_NAME = "lessCompiler";
    public static final String LESS_COMPILE_TASK_NAME = "compilePlayLess";

    @Override
    public void apply(Project project) {
        Configuration configuration = createLessCompilerConfiguration(project);
        declareDefaultDependencies(project, configuration);
        LessSourceSet sourceSet = createCustomSourceSet(project, DefaultLessSourceSet.class, "less");
        createDefaultLessCompileTask(project, sourceSet.getLess(), configuration);
        configureLessCompileTask(project);
    }

    private Configuration createLessCompilerConfiguration(Project project) {
        Configuration configuration = project.getConfigurations().create(LESS_COMPILER_CONFIGURATION_NAME);
        configuration.setVisible(false);
        configuration.setTransitive(true);
        configuration.setDescription("The LESS compiler library used to generate CSS stylesheets from LESS stylesheets.");
        return configuration;
    }

    private void declareDefaultDependencies(Project project, Configuration configuration) {
        configuration.defaultDependencies(dependencies -> {
            dependencies.add(project.getDependencies().create(Less4jCompiler.getDependencyNotation()));
        });
    }

    private void createDefaultLessCompileTask(Project project, SourceDirectorySet sourceDirectory, Configuration configuration) {
        project.getTasks().register(LESS_COMPILE_TASK_NAME, LessCompile.class, task -> {
            task.setDescription("Generates CSS stylesheets for the '" + sourceDirectory.getDisplayName() + "' source set.");
            task.setSource(sourceDirectory);
            task.getOutputDirectory().set(getOutputDir(project, sourceDirectory));
            task.getLessCompilerClasspath().setFrom(configuration);
        });
    }

    private void configureLessCompileTask(Project project) {
        TaskProvider<LessCompile> lessCompileTaskProvider = project.getTasks().named(LESS_COMPILE_TASK_NAME, LessCompile.class);

        project.getTasks().named(ASSEMBLE_TASK_NAME, task -> {
            task.dependsOn(lessCompileTaskProvider);
        });
        project.getTasks().named(ASSETS_JAR_TASK_NAME, Jar.class, task -> {
            task.dependsOn(lessCompileTaskProvider);
            task.from(lessCompileTaskProvider.get().getOutputDirectory(), copySpec -> copySpec.into("public"));
        });
        project.getTasks().named(PLAY_RUN_TASK_NAME, PlayRun.class, task -> {
            task.getAssetsDirs().from(lessCompileTaskProvider.get().getOutputDirectory());
        });
    }
}

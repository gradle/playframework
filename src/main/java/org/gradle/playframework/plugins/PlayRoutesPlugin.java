package org.gradle.playframework.plugins;

import org.gradle.playframework.sourcesets.internal.DefaultRoutesSourceSet;
import org.gradle.playframework.extensions.PlayExtension;
import org.gradle.playframework.sourcesets.RoutesSourceSet;
import org.gradle.playframework.tasks.RoutesCompile;
import org.gradle.playframework.tools.internal.routes.RoutesCompilerFactory;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Provider;

import java.util.ArrayList;

import static org.gradle.playframework.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static org.gradle.playframework.plugins.internal.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for compiling Play routes sources in a Play application.
 */
public class PlayRoutesPlugin implements PlayGeneratedSourcePlugin {

    public static final String ROUTES_COMPILER_CONFIGURATION_NAME = "routesCompiler";
    public static final String ROUTES_COMPILE_TASK_NAME = "compilePlayRoutes";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);

        Configuration routesCompilerConfiguration = createRoutesCompilerConfiguration(project);
        declareDefaultDependencies(project, routesCompilerConfiguration, playExtension);
        RoutesSourceSet routesSourceSet = createCustomSourceSet(project, DefaultRoutesSourceSet.class, "routes");
        createDefaultRoutesCompileTask(project, routesSourceSet.getRoutes(), routesCompilerConfiguration, playExtension, playExtension.getInjectedRoutesGenerator());
    }

    private Configuration createRoutesCompilerConfiguration(Project project) {
        Configuration compilerConfiguration = project.getConfigurations().create(ROUTES_COMPILER_CONFIGURATION_NAME);
        compilerConfiguration.setVisible(false);
        compilerConfiguration.setTransitive(true);
        compilerConfiguration.setDescription("The routes compiler library used to generate Scala source from routes templates.");
        return compilerConfiguration;
    }

    private void declareDefaultDependencies(Project project, Configuration configuration, PlayExtension playExtension) {
        configuration.defaultDependencies(dependencies -> {
            String dependencyNotation = RoutesCompilerFactory.createAdapter(playExtension.getPlatform()).getDependencyNotation();
            dependencies.add(project.getDependencies().create(dependencyNotation));
        });
    }

    private void createDefaultRoutesCompileTask(Project project, SourceDirectorySet sourceDirectory, Configuration compilerConfiguration, PlayExtension playExtension, Provider<Boolean> injectedRoutesGenerator) {
        project.getTasks().register(ROUTES_COMPILE_TASK_NAME, RoutesCompile.class, routesCompile -> {
            routesCompile.setDescription("Generates routes for the '" + sourceDirectory.getDisplayName() + "' source set.");
            routesCompile.getPlatform().set(project.provider(() -> playExtension.getPlatform()));
            routesCompile.getAdditionalImports().set(new ArrayList<>());
            routesCompile.setSource(sourceDirectory);
            routesCompile.getOutputDirectory().set(getOutputDir(project, sourceDirectory));
            routesCompile.getInjectedRoutesGenerator().set(injectedRoutesGenerator);
            routesCompile.getRoutesCompilerClasspath().setFrom(compilerConfiguration);
        });
    }
}

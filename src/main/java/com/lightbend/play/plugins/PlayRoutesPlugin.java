package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.sourcesets.DefaultRoutesSourceSet;
import com.lightbend.play.sourcesets.RoutesSourceSet;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.RoutesCompile;

import java.util.ArrayList;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;

/**
 * Plugin for compiling Play routes sources in a Play application.
 */
public class PlayRoutesPlugin implements PlayGeneratedSourcePlugin {

    public static final String ROUTES_COMPILE_TASK_NAME = "compilePlayRoutes";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = ((PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME));
        PlayPlatform playPlatform = playExtension.getPlatform().asPlayPlatform();

        RoutesSourceSet routesSourceSet = createRoutesSourceSet(project);
        RoutesCompile routesCompile = createDefaultRoutesCompileTask(project, routesSourceSet.getRoutes(), playPlatform);

        // TODO: RoutesCompile should use Provider types to avoid afterEvaluate
        project.afterEvaluate(project1 -> routesCompile.setInjectedRoutesGenerator(playExtension.getInjectedRoutesGenerator().get()));
    }

    private RoutesSourceSet createRoutesSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        RoutesSourceSet routesSourceSet = project.getObjects().newInstance(DefaultRoutesSourceSet.class, "routes", ((DefaultSourceSet) mainSourceSet).getDisplayName(), project.getObjects());
        new DslObject(mainSourceSet).getConvention().getPlugins().put("routes", routesSourceSet);
        return routesSourceSet;
    }

    private RoutesCompile createDefaultRoutesCompileTask(Project project, SourceDirectorySet sourceDirectory, PlayPlatform playPlatform) {
        return project.getTasks().create(ROUTES_COMPILE_TASK_NAME, RoutesCompile.class, routesCompile -> {
            routesCompile.setDescription("Generates routes for the '" + sourceDirectory.getDisplayName() + "' source set.");
            routesCompile.setPlatform(playPlatform);
            routesCompile.setAdditionalImports(new ArrayList<>());
            routesCompile.setSource(sourceDirectory);
            routesCompile.setOutputDirectory(getOutputDir(project, sourceDirectory));
        });
    }
}

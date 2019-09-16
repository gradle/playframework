package org.gradle.playframework.plugins;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.playframework.extensions.PlayExtension;
import org.gradle.playframework.plugins.internal.PlayPluginHelper;
import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlSourceSet;
import org.gradle.playframework.sourcesets.internal.DefaultTwirlSourceSet;
import org.gradle.playframework.tasks.TwirlCompile;
import org.gradle.playframework.tools.internal.twirl.TwirlCompilerFactory;

import java.util.List;

/**
 * Plugin for compiling Twirl sources in a Play application.
 */
public class PlayTwirlPlugin implements PlayGeneratedSourcePlugin {

    public static final String TWIRL_COMPILER_CONFIGURATION_NAME = "twirlCompiler";
    public static final String TWIRL_COMPILE_TASK_NAME = "compilePlayTwirlTemplates";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PlayApplicationPlugin.PLAY_EXTENSION_NAME);

        Configuration twirlCompilerConfiguration = createTwirlCompilerConfiguration(project);
        declareDefaultDependencies(project, twirlCompilerConfiguration, playExtension);
        TwirlSourceSet twirlSourceSet = PlayPluginHelper.createCustomSourceSet(project, DefaultTwirlSourceSet.class, "twirl");
        TaskProvider<TwirlCompile> twirlCompile = createDefaultTwirlCompileTask(project, twirlSourceSet, twirlCompilerConfiguration, playExtension);

        project.afterEvaluate(project1 -> {
            if (hasTwirlSourceSetsWithJavaImports(twirlCompile)) {
                project.getDependencies().add(PlayApplicationPlugin.PLATFORM_CONFIGURATION, playExtension.getPlatform().getDependencyNotation("play-java").get());
            }
        });
    }

    private Configuration createTwirlCompilerConfiguration(Project project) {
        Configuration twirlCompilerConfiguration = project.getConfigurations().create(TWIRL_COMPILER_CONFIGURATION_NAME);
        twirlCompilerConfiguration.setVisible(false);
        twirlCompilerConfiguration.setTransitive(true);
        twirlCompilerConfiguration.setDescription("The Twirl compiler library used to generate Scala source from Twirl templates.");
        return twirlCompilerConfiguration;
    }

    private void declareDefaultDependencies(Project project, Configuration configuration, PlayExtension playExtension) {
        configuration.defaultDependencies(dependencies -> {
            List<String> dependencyNotations = TwirlCompilerFactory.createAdapter(playExtension.getPlatform()).getDependencyNotation();

            for (String dependencyNotation : dependencyNotations) {
                dependencies.add(project.getDependencies().create(dependencyNotation));
            }
        });
    }

    private TaskProvider<TwirlCompile> createDefaultTwirlCompileTask(Project project, TwirlSourceSet twirlSourceSet, Configuration compilerConfiguration, PlayExtension playExtension) {
        return project.getTasks().register(TWIRL_COMPILE_TASK_NAME, TwirlCompile.class, twirlCompile -> {
            twirlCompile.setDescription("Compiles Twirl templates for the '" + twirlSourceSet.getTwirl().getDisplayName() + "' source set.");
            twirlCompile.getPlatform().set(project.provider(() -> playExtension.getPlatform()));
            twirlCompile.setSource(twirlSourceSet.getTwirl());
            twirlCompile.getOutputDirectory().set(getOutputDir(project, twirlSourceSet.getTwirl()));
            twirlCompile.getDefaultImports().set(twirlSourceSet.getDefaultImports());
            twirlCompile.getUserTemplateFormats().set(twirlSourceSet.getUserTemplateFormats());
            twirlCompile.getAdditionalImports().set(twirlSourceSet.getAdditionalImports());
            twirlCompile.getTwirlCompilerClasspath().setFrom(compilerConfiguration);
            twirlCompile.getConstructorAnnotations().set(twirlSourceSet.getConstructorAnnotations());
        });
    }

    private boolean hasTwirlSourceSetsWithJavaImports(TaskProvider<TwirlCompile> twirlCompile) {
        return twirlCompile.get().getDefaultImports().get() == TwirlImports.JAVA;
    }
}

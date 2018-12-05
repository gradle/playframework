package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.extensions.PlayPluginConfigurations;
import com.lightbend.play.platform.PlayPlatformInternal;
import com.lightbend.play.sourcesets.DefaultTwirlSourceSet;
import com.lightbend.play.sourcesets.TwirlSourceSet;
import com.lightbend.play.tasks.TwirlCompile;
import com.lightbend.play.tools.twirl.TwirlCompilerFactory;
import com.lightbend.play.tools.twirl.TwirlImports;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;

import java.util.List;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_CONFIGURATIONS_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for compiling Twirl sources in a Play application.
 */
public class PlayTwirlPlugin implements PlayGeneratedSourcePlugin {

    public static final String TWIRL_COMPILER_CONFIGURATION_NAME = "twirlCompiler";
    public static final String TWIRL_COMPILE_TASK_NAME = "compilePlayTwirlTemplates";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);
        PlayPluginConfigurations configurations = (PlayPluginConfigurations) project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);

        Configuration twirlCompilerConfiguration = createTwirlCompilerConfiguration(project);
        declareDefaultDependencies(project, twirlCompilerConfiguration, playExtension);
        TwirlSourceSet twirlSourceSet = createCustomSourceSet(project, DefaultTwirlSourceSet.class, "twirl");
        TaskProvider<TwirlCompile> twirlCompile = createDefaultTwirlCompileTask(project, twirlSourceSet, twirlCompilerConfiguration, playExtension);

        project.afterEvaluate(project1 -> {
            if (hasTwirlSourceSetsWithJavaImports(twirlCompile)) {
                configurations.getPlay().addDependency(((PlayPlatformInternal) playExtension.getPlatform().asPlayPlatform()).getDependencyNotation("play-java"));
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
            List<String> dependencyNotations = TwirlCompilerFactory.createAdapter(playExtension.getPlatform().asPlayPlatform()).getDependencyNotation();

            for (String dependencyNotation : dependencyNotations) {
                dependencies.add(project.getDependencies().create(dependencyNotation));
            }
        });
    }

    private TaskProvider<TwirlCompile> createDefaultTwirlCompileTask(Project project, TwirlSourceSet twirlSourceSet, Configuration compilerConfiguration, PlayExtension playExtension) {
        return project.getTasks().register(TWIRL_COMPILE_TASK_NAME, TwirlCompile.class, twirlCompile -> {
            twirlCompile.setDescription("Compiles Twirl templates for the '" + twirlSourceSet.getTwirl().getDisplayName() + "' source set.");
            twirlCompile.setPlatform(project.provider(() -> playExtension.getPlatform().asPlayPlatform()));
            twirlCompile.setSource(twirlSourceSet.getTwirl());
            twirlCompile.setOutputDirectory(getOutputDir(project, twirlSourceSet.getTwirl()));
            twirlCompile.setDefaultImports(twirlSourceSet.getDefaultImports());
            twirlCompile.setUserTemplateFormats(twirlSourceSet.getUserTemplateFormats());
            twirlCompile.setAdditionalImports(twirlSourceSet.getAdditionalImports());
            twirlCompile.setTwirlCompilerClasspath(compilerConfiguration);
        });
    }

    private boolean hasTwirlSourceSetsWithJavaImports(TaskProvider<TwirlCompile> twirlCompile) {
        return twirlCompile.get().getDefaultImports().get() == TwirlImports.JAVA;
    }
}

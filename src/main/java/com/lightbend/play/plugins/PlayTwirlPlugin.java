package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.extensions.PlayPluginConfigurations;
import com.lightbend.play.sourcesets.DefaultTwirlSourceSet;
import com.lightbend.play.sourcesets.TwirlSourceSet;
import com.lightbend.play.tasks.TwirlCompile;
import com.lightbend.play.tools.twirl.TwirlCompilerFactory;
import com.lightbend.play.tools.twirl.TwirlImports;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.play.internal.platform.PlayPlatformInternal;
import org.gradle.play.platform.PlayPlatform;

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
        project.getPluginManager().apply(BasePlugin.class);

        PlayPlatform playPlatform = ((PlayExtension)project.getExtensions().getByName(PLAY_EXTENSION_NAME)).getPlatform().asPlayPlatform();
        PlayPluginConfigurations configurations = (PlayPluginConfigurations)project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);

        Configuration twirlCompilerConfiguration = createTwirlCompilerConfiguration(project);
        TwirlSourceSet twirlSourceSet = createCustomSourceSet(project, DefaultTwirlSourceSet.class, "twirl");
        TwirlCompile twirlCompile = createDefaultTwirlCompileTask(project, twirlSourceSet, playPlatform);

        project.afterEvaluate(project1 -> {
            declareDefaultDependencies(project, twirlCompilerConfiguration, playPlatform);
            configureTwirlCompileConfiguration(twirlCompile, twirlCompilerConfiguration);
            configureTwirlCompileTaskFromSourceSet(twirlCompile, twirlSourceSet);

            if (hasTwirlSourceSetsWithJavaImports(twirlCompile)) {
                configurations.getPlay().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play-java"));
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

    private void declareDefaultDependencies(Project project, Configuration configuration, PlayPlatform playPlatform) {
        configuration.defaultDependencies(dependencies -> {
            List<String> dependencyNotations = TwirlCompilerFactory.createAdapter(playPlatform).getDependencyNotation();

            for (String dependencyNotation : dependencyNotations) {
                dependencies.add(project.getDependencies().create(dependencyNotation));
            }
        });
    }

    private TwirlCompile createDefaultTwirlCompileTask(Project project, TwirlSourceSet twirlSourceSet, PlayPlatform playPlatform) {
        return project.getTasks().create(TWIRL_COMPILE_TASK_NAME, TwirlCompile.class, twirlCompile -> {
            twirlCompile.setDescription("Compiles Twirl templates for the '" + twirlSourceSet.getTwirl().getDisplayName() + "' source set.");
            twirlCompile.setPlatform(playPlatform);
            twirlCompile.setSource(twirlSourceSet.getTwirl());
            twirlCompile.setOutputDirectory(getOutputDir(project, twirlSourceSet.getTwirl()));
        });
    }

    private void configureTwirlCompileTaskFromSourceSet(TwirlCompile twirlCompile, TwirlSourceSet twirlSourceSet) {
        twirlCompile.setDefaultImports(twirlSourceSet.getDefaultImports());
        twirlCompile.setUserTemplateFormats(twirlSourceSet.getUserTemplateFormats());
        twirlCompile.setAdditionalImports(twirlSourceSet.getAdditionalImports());
    }

    private void configureTwirlCompileConfiguration(TwirlCompile twirlCompile, Configuration twirlCompilerConfiguration) {
        twirlCompile.setTwirlCompilerClasspath(twirlCompilerConfiguration);
    }

    private boolean hasTwirlSourceSetsWithJavaImports(TwirlCompile twirlCompile) {
        return twirlCompile.getDefaultImports() == TwirlImports.JAVA;
    }
}

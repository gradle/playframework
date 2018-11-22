package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.sourcesets.DefaultTwirlSourceSet;
import com.lightbend.play.sourcesets.TwirlSourceSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.language.twirl.TwirlImports;
import org.gradle.play.internal.platform.PlayPlatformInternal;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.TwirlCompile;

import java.io.File;
import java.util.ArrayList;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_CONFIGURATIONS_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;

/**
 * Plugin for compiling Twirl sources in a Play application.
 */
public class PlayTwirlPlugin implements Plugin<Project> {

    public static final String TWIRL_COMPILE_TASK_NAME = "compileTwirl";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(BasePlugin.class);

        PlayPlatform playPlatform = ((PlayExtension)project.getExtensions().getByName(PLAY_EXTENSION_NAME)).getPlatform().asPlayPlatform();
        PlayPluginConfigurations configurations = (PlayPluginConfigurations)project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);

        TwirlSourceSet twirlSourceSet = createTwirlSourceSet(project);
        TwirlCompile twirlCompile = createDefaultTwirlCompileTask(project, twirlSourceSet.getTwirl(), playPlatform);

        project.afterEvaluate(project1 -> {
            // TODO: Revisit later - this likely won't work
            if (hasTwirlSourceSetsWithJavaImports(twirlCompile)) {
                configurations.getPlay().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play-java"));
            }
        });
    }

    private TwirlSourceSet createTwirlSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        TwirlSourceSet twirlSourceSet = new DefaultTwirlSourceSet("twirl", ((DefaultSourceSet) mainSourceSet).getDisplayName(), project.getObjects());
        new DslObject(mainSourceSet).getConvention().getPlugins().put("twirl", twirlSourceSet);
        return twirlSourceSet;
    }

    private TwirlCompile createDefaultTwirlCompileTask(Project project, SourceDirectorySet sourceDirectory, PlayPlatform playPlatform) {
        return project.getTasks().create(TWIRL_COMPILE_TASK_NAME, TwirlCompile.class, twirlCompile -> {
            twirlCompile.setDescription("Compiles Twirl templates for the '" + sourceDirectory.getDisplayName() + "' source set.");
            File generatedSourceDir = new File(project.getBuildDir(), "src");
            twirlCompile.setPlatform(playPlatform);
            twirlCompile.setSource(sourceDirectory);
            File outputDirectory = new File(generatedSourceDir, sourceDirectory.getName());
            twirlCompile.setOutputDirectory(outputDirectory);
            twirlCompile.setDefaultImports(TwirlImports.SCALA);
            twirlCompile.setUserTemplateFormats(new ArrayList<>());
            twirlCompile.setAdditionalImports(new ArrayList<>());
        });
    }

    private boolean hasTwirlSourceSetsWithJavaImports(TwirlCompile twirlCompile) {
        return twirlCompile.getDefaultImports() == TwirlImports.JAVA;
    }
}

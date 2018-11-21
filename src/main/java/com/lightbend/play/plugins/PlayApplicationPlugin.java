package com.lightbend.play.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.scala.ScalaPlugin;
import org.gradle.play.internal.platform.PlayMajorVersion;
import org.gradle.play.internal.platform.PlayPlatformInternal;
import org.gradle.play.platform.PlayPlatform;

import static com.lightbend.play.plugins.PlayPlatformHelper.createDefaultPlayPlatform;

public class PlayApplicationPlugin implements Plugin<Project> {

    public static final String PLAY_CONFIGURATIONS_EXTENSION_NAME = "playConfigurations";

    @Override
    public void apply(Project project) {
        PlayPluginConfigurations playPluginConfigurations = project.getExtensions().create(PLAY_CONFIGURATIONS_EXTENSION_NAME, PlayPluginConfigurations.class, project.getConfigurations(), project.getDependencies());
        PlayPlatform playPlatform = createDefaultPlayPlatform();

        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(ScalaPlugin.class);
        project.getPluginManager().apply(PlayTwirlPlugin.class);
        project.getPluginManager().apply(PlayRoutesPlugin.class);

        initialiseConfigurations(playPluginConfigurations, playPlatform);
    }

    private void initialiseConfigurations(PlayPluginConfigurations configurations, PlayPlatform playPlatform) {
        configurations.getPlayPlatform().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play"));
        configurations.getPlayTest().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play-test"));
        configurations.getPlayRun().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play-docs"));

        PlayMajorVersion playMajorVersion = PlayMajorVersion.forPlatform(playPlatform);
        if (playMajorVersion == PlayMajorVersion.PLAY_2_6_X) {
            // This has the downside of adding play-java-forms for all kind of play projects
            // including Scala based projects. Still, users can exclude the dependency if they
            // want/need. Maybe in the future we can enable users to have some flag to specify
            // if the project is Java or Scala based.
            configurations.getPlayPlatform().addDependency(((PlayPlatformInternal) playPlatform).getDependencyNotation("play-java-forms"));
        }
    }
}

package com.lightbend.play.plugins;

import com.lightbend.play.extensions.Platform;
import com.lightbend.play.extensions.PlayExtension;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.scala.ScalaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.play.internal.DefaultPlayPlatform;
import org.gradle.play.internal.platform.PlayMajorVersion;
import org.gradle.play.internal.platform.PlayPlatformInternal;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.util.VersionNumber;

import java.io.File;
import java.util.Arrays;

import static org.gradle.api.plugins.JavaPlugin.CLASSES_TASK_NAME;

/**
 * Plugin for Play Framework component support.
 */
public class PlayApplicationPlugin implements Plugin<Project> {

    public static final String PLAY_EXTENSION_NAME = "play";
    public static final String PLAY_CONFIGURATIONS_EXTENSION_NAME = "playConfigurations";
    public static final String JAR_TASK_NAME = "createJar";
    public static final String ASSETS_JAR_TASK_NAME = "createAssetsJar";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = createPlayExtension(project);
        PlayPluginConfigurations playPluginConfigurations = project.getExtensions().create(PLAY_CONFIGURATIONS_EXTENSION_NAME, PlayPluginConfigurations.class, project.getConfigurations(), project.getDependencies());

        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(ScalaPlugin.class);
        project.getPluginManager().apply(PlayTwirlPlugin.class);
        project.getPluginManager().apply(PlayRoutesPlugin.class);

        configureJavaAndScalaSourceSet(project);
        createJarTasks(project);

        project.afterEvaluate(project1 -> {
            failIfInjectedRouterIsUsedWithOldVersion(playExtension.getPlatform());
            initialiseConfigurations(playPluginConfigurations, playExtension.getPlatform().asPlayPlatform());
        });
    }

    private PlayExtension createPlayExtension(Project project) {
        PlayExtension playExtension = project.getExtensions().create(PLAY_EXTENSION_NAME, PlayExtension.class, project);
        playExtension.getPlatform().getPlayVersion().set(DefaultPlayPlatform.DEFAULT_PLAY_VERSION);
        playExtension.getPlatform().getScalaVersion().set("2.11");
        playExtension.getPlatform().getJavaVersion().set(JavaVersion.current());
        playExtension.getPlatform().getInjectedRoutesGenerator().set(false);
        return playExtension;
    }

    private void failIfInjectedRouterIsUsedWithOldVersion(Platform platform) {
        if (Boolean.TRUE.equals(platform.getInjectedRoutesGenerator().get())) {
            PlayPlatform playPlatform = platform.asPlayPlatform();
            VersionNumber minSupportedVersion = VersionNumber.parse("2.4.0");
            VersionNumber playVersion = VersionNumber.parse(playPlatform.getPlayVersion());
            if (playVersion.compareTo(minSupportedVersion) < 0) {
                throw new GradleException("Injected routers are only supported in Play 2.4 or newer.");
            }
        }
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

    private void configureJavaAndScalaSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        SourceDirectorySet mainSourceDirectorySet = mainSourceSet.getJava();
        mainSourceDirectorySet.setSrcDirs(Arrays.asList("app"));
        mainSourceDirectorySet.include("**/*.java");

        SourceDirectorySet scalaSourceDirectorySet = ((SourceDirectorySet)InvokerHelper.invokeMethod(mainSourceSet, "getScala", null));
        scalaSourceDirectorySet.setSrcDirs(Arrays.asList("app"));
        scalaSourceDirectorySet.include("**/*.scala");
    }

    private void createJarTasks(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        project.getTasks().create(JAR_TASK_NAME, Jar.class, jar -> {
            jar.setDescription("Assembles the application jar.");
            jar.from(mainSourceSet.getOutput().getClassesDirs());
            jar.from(mainSourceSet.getOutput().getResourcesDir());
            jar.dependsOn(project.getTasks().getByName(CLASSES_TASK_NAME));
        });

        project.getTasks().create(ASSETS_JAR_TASK_NAME, Jar.class, jar -> {
            jar.setDescription("Assembles the assets jar for the application.");
            jar.setClassifier("assets");
            jar.from(new File(project.getProjectDir(), "public"));
            jar.into("public");
        });
    }
}

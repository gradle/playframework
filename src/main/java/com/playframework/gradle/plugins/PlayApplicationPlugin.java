package com.playframework.gradle.plugins;

import com.playframework.gradle.extensions.PlayExtension;
import com.playframework.gradle.extensions.PlayPlatform;
import com.playframework.gradle.extensions.PlayPluginConfigurations;
import com.playframework.gradle.extensions.internal.PlayMajorVersion;
import com.playframework.gradle.tasks.PlayRun;
import com.playframework.gradle.tasks.RoutesCompile;
import com.playframework.gradle.tasks.TwirlCompile;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationPublications;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact;
import org.gradle.api.plugins.scala.ScalaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.scala.ScalaCompile;
import org.gradle.util.VersionNumber;

import java.util.Arrays;
import java.util.HashSet;

import static com.playframework.gradle.extensions.PlayPlatform.DEFAULT_PLAY_VERSION;
import static com.playframework.gradle.extensions.PlayPlatform.DEFAULT_SCALA_VERSION;
import static com.playframework.gradle.plugins.internal.PlayPluginHelper.getMainJavaSourceSet;
import static com.playframework.gradle.plugins.internal.PlayPluginHelper.getScalaSourceDirectorySet;
import static org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME;
import static org.gradle.api.plugins.JavaBasePlugin.BUILD_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME;

/**
 * Plugin for Play Framework component support.
 */
public class PlayApplicationPlugin implements Plugin<Project> {

    public static final String PLAY_EXTENSION_NAME = "play";
    public static final String PLAY_CONFIGURATIONS_EXTENSION_NAME = "playConfigurations";
    public static final String ASSETS_JAR_TASK_NAME = "createPlayAssetsJar";
    public static final String RUN_TASK_NAME = "runPlay";
    public static final int DEFAULT_HTTP_PORT = 9000;
    public static final String RUN_GROUP = "Run";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = createPlayExtension(project);
        PlayPluginConfigurations playPluginConfigurations = project.getExtensions().create(PLAY_CONFIGURATIONS_EXTENSION_NAME, PlayPluginConfigurations.class, project.getConfigurations(), project.getDependencies());

        applyPlugins(project);

        configureJavaAndScalaSourceSet(project);
        TaskProvider<Jar> mainJarTask = project.getTasks().named(JAR_TASK_NAME, Jar.class);
        TaskProvider<Jar> assetsJarTask = createAssetsJarTask(project);
        registerOutgoingArtifact(project, assetsJarTask);
        TaskProvider<PlayRun> playRun = createRunTask(project, playExtension, mainJarTask, assetsJarTask);

        project.afterEvaluate(project1 -> {
            PlayPlatform playPlatform = playExtension.getPlatform();
            failIfInjectedRouterIsUsedWithOldVersion(playExtension.getInjectedRoutesGenerator().get(), playPlatform);
            initialiseConfigurations(playPluginConfigurations, playPlatform);
            configureScalaCompileTask(project, playPluginConfigurations);
            configureRunTask(playRun, playPluginConfigurations);
        });
    }

    private void applyPlugins(Project project) {
        project.getPluginManager().apply(ScalaPlugin.class);
        project.getPluginManager().apply(PlayTwirlPlugin.class);
        project.getPluginManager().apply(PlayRoutesPlugin.class);
    }

    private PlayExtension createPlayExtension(Project project) {
        PlayExtension playExtension = project.getExtensions().create(PLAY_EXTENSION_NAME, PlayExtension.class, project.getObjects());
        playExtension.getPlatform().getPlayVersion().set(DEFAULT_PLAY_VERSION);
        playExtension.getPlatform().getScalaVersion().set(DEFAULT_SCALA_VERSION);
        playExtension.getPlatform().getJavaVersion().set(JavaVersion.current());
        playExtension.getInjectedRoutesGenerator().set(false);
        return playExtension;
    }

    private void failIfInjectedRouterIsUsedWithOldVersion(Boolean injectedRoutesGenerator, PlayPlatform playPlatform) {
        if (Boolean.TRUE.equals(injectedRoutesGenerator)) {
            VersionNumber minSupportedVersion = VersionNumber.parse("2.4.0");
            VersionNumber playVersion = VersionNumber.parse(playPlatform.getPlayVersion().get());
            if (playVersion.compareTo(minSupportedVersion) < 0) {
                throw new GradleException("Injected routers are only supported in Play 2.4 or newer.");
            }
        }
    }

    private void initialiseConfigurations(PlayPluginConfigurations configurations, PlayPlatform playPlatform) {
        configurations.getPlayPlatform().addDependency(playPlatform.getDependencyNotation("play").get());
        configurations.getPlayTest().addDependency(playPlatform.getDependencyNotation("play-test").get());
        configurations.getPlayRun().addDependency(playPlatform.getDependencyNotation("play-docs").get());

        PlayMajorVersion playMajorVersion = PlayMajorVersion.forPlatform(playPlatform);
        if (playMajorVersion == PlayMajorVersion.PLAY_2_6_X) {
            // This has the downside of adding play-java-forms for all kind of play projects
            // including Scala based projects. Still, users can exclude the dependency if they
            // want/need. Maybe in the future we can enable users to have some flag to specify
            // if the project is Java or Scala based.
            configurations.getPlayPlatform().addDependency(playPlatform.getDependencyNotation("play-java-forms").get());
        }
    }

    private void configureJavaAndScalaSourceSet(Project project) {
        SourceSet mainSourceSet = getMainJavaSourceSet(project);
        SourceDirectorySet mainResourcesDirectorySet = mainSourceSet.getResources();
        mainResourcesDirectorySet.setSrcDirs(Arrays.asList("conf"));

        SourceDirectorySet scalaSourceDirectorySet = getScalaSourceDirectorySet(project);
        scalaSourceDirectorySet.setSrcDirs(Arrays.asList("app"));
        scalaSourceDirectorySet.include("**/*.scala", "**/*.java");

        scalaSourceDirectorySet.srcDir(getTwirlCompileTask(project).flatMap(task -> task.getOutputDirectory()));
        scalaSourceDirectorySet.srcDir(getRoutesCompileTask(project).flatMap(task -> task.getOutputDirectory()));
    }

    private TaskProvider<Jar> createAssetsJarTask(Project project) {
        TaskProvider<Jar> assetsJarTask = project.getTasks().register(ASSETS_JAR_TASK_NAME, Jar.class, jar -> {
            jar.setDescription("Assembles the assets jar for the application.");
            jar.setClassifier("assets");
            jar.from(project.file("public"), copySpec -> copySpec.into("public"));
        });

        project.getTasks().named(ASSEMBLE_TASK_NAME, assembleTask -> assembleTask.dependsOn(assetsJarTask));

        return assetsJarTask;
    }

    private void registerOutgoingArtifact(Project project, TaskProvider<Jar> assetsJarTask) {
        Configuration runtimeElementsConfiguration = project.getConfigurations().getByName(RUNTIME_ELEMENTS_CONFIGURATION_NAME);
        PublishArtifact jarArtifact = new LazyPublishArtifact(assetsJarTask);
        ConfigurationPublications publications = runtimeElementsConfiguration.getOutgoing();
        publications.getArtifacts().add(jarArtifact);
        publications.getAttributes().attribute(ArtifactAttributes.ARTIFACT_FORMAT, ArtifactTypeDefinition.JAR_TYPE);
    }

    private void configureScalaCompileTask(Project project, PlayPluginConfigurations configurations) {
        project.getTasks().named("compileScala", ScalaCompile.class, scalaCompile -> {
            FileCollection playArtifacts = configurations.getPlay().getAllArtifacts();
            scalaCompile.setClasspath(playArtifacts);
            scalaCompile.getOptions().setAnnotationProcessorPath(playArtifacts);
            scalaCompile.dependsOn(getTwirlCompileTask(project));
            scalaCompile.dependsOn(getRoutesCompileTask(project));
        });
    }

    private static TaskProvider<TwirlCompile> getTwirlCompileTask(Project project) {
        return project.getTasks().named(PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME, TwirlCompile.class);
    }

    private static TaskProvider<RoutesCompile> getRoutesCompileTask(Project project) {
        return project.getTasks().named(PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME, RoutesCompile.class);
    }

    private TaskProvider<PlayRun> createRunTask(Project project, PlayExtension playExtension, TaskProvider<Jar> mainJarTask, TaskProvider<Jar> assetsJarTask) {
        return project.getTasks().register(RUN_TASK_NAME, PlayRun.class, playRun -> {
            playRun.setDescription("Runs the Play application for local development.");
            playRun.setGroup(RUN_GROUP);
            playRun.setHttpPort(DEFAULT_HTTP_PORT);
            playRun.getWorkingDir().set(project.getProjectDir());
            playRun.getPlatform().set(project.provider(() -> playExtension.getPlatform()));
            playRun.setApplicationJar(mainJarTask.get().getArchivePath());
            playRun.setAssetsJar(assetsJarTask.get().getArchivePath());
            playRun.setAssetsDirs(new HashSet<>(Arrays.asList(project.file("public"))));
            playRun.dependsOn(project.getTasks().named(BUILD_TASK_NAME));
        });
    }

    private void configureRunTask(TaskProvider<PlayRun> playRun, PlayPluginConfigurations configurations) {
        playRun.configure(task -> {
            task.setRuntimeClasspath(configurations.getPlayRun().getNonChangingArtifacts());
            task.setChangingClasspath(configurations.getPlayRun().getChangingArtifacts());
        });
    }
}

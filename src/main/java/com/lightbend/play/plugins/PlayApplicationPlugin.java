package com.lightbend.play.plugins;

import com.lightbend.play.extensions.Platform;
import com.lightbend.play.extensions.PlayExtension;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationPublications;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.scala.ScalaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.scala.ScalaCompile;
import org.gradle.play.internal.DefaultPlayPlatform;
import org.gradle.play.internal.platform.PlayMajorVersion;
import org.gradle.play.internal.platform.PlayPlatformInternal;
import org.gradle.play.internal.toolchain.PlayToolChainInternal;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.PlayRun;
import org.gradle.play.tasks.RoutesCompile;
import org.gradle.play.tasks.TwirlCompile;
import org.gradle.util.VersionNumber;

import java.util.Arrays;
import java.util.HashSet;

import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME;
import static com.lightbend.play.plugins.PlayTwirlPlugin.TWIRL_COMPILE_TASK_NAME;
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
        Jar mainJarTask = (Jar) project.getTasks().getByName(JAR_TASK_NAME);
        Jar assetsJarTask = createAssetsJarTask(project);
        registerOutgoingArtifact(project, assetsJarTask);

        project.afterEvaluate(project1 -> {
            failIfInjectedRouterIsUsedWithOldVersion(playExtension.getPlatform());
            PlayPlatform playPlatform = playExtension.getPlatform().asPlayPlatform();
            initialiseConfigurations(playPluginConfigurations, playPlatform);
            configureScalaCompileTask(project, playPluginConfigurations);
            createRunTask(project, playPluginConfigurations, playPlatform, mainJarTask, assetsJarTask);
        });
    }

    private void applyPlugins(Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(ScalaPlugin.class);
        project.getPluginManager().apply(PlayTwirlPlugin.class);
        project.getPluginManager().apply(PlayRoutesPlugin.class);
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
        SourceSet mainSourceSet = getMainSourceSet(project);
        SourceDirectorySet mainResourcesDirectorySet = mainSourceSet.getResources();
        mainResourcesDirectorySet.setSrcDirs(Arrays.asList("conf"));

        SourceDirectorySet scalaSourceDirectorySet = ((SourceDirectorySet)InvokerHelper.invokeMethod(mainSourceSet, "getScala", null));
        scalaSourceDirectorySet.setSrcDirs(Arrays.asList("app"));
        scalaSourceDirectorySet.include("**/*.scala", "**/*.java");

        scalaSourceDirectorySet.srcDir(getTwirlCompileTask(project).getOutputDirectory());
        scalaSourceDirectorySet.srcDir(getRoutesCompileTask(project).getOutputDirectory());
    }

    private Jar createAssetsJarTask(Project project) {
        Jar assetsJarTask = project.getTasks().create(ASSETS_JAR_TASK_NAME, Jar.class, jar -> {
            jar.setDescription("Assembles the assets jar for the application.");
            jar.setClassifier("assets");
            jar.from(project.file("public"), copySpec -> copySpec.into("public"));
        });

        Task assembleTask = project.getTasks().getByName(ASSEMBLE_TASK_NAME);
        assembleTask.dependsOn(assetsJarTask);

        return assetsJarTask;
    }

    private void registerOutgoingArtifact(Project project, Jar assetsJarTask) {
        Configuration runtimeElementsConfiguration = project.getConfigurations().getByName(RUNTIME_ELEMENTS_CONFIGURATION_NAME);
        PublishArtifact jarArtifact = new ArchivePublishArtifact(assetsJarTask);
        ConfigurationPublications publications = runtimeElementsConfiguration.getOutgoing();
        publications.getArtifacts().add(jarArtifact);
        publications.getAttributes().attribute(ArtifactAttributes.ARTIFACT_FORMAT, ArtifactTypeDefinition.JAR_TYPE);
    }

    private void configureScalaCompileTask(Project project, PlayPluginConfigurations configurations) {
        ScalaCompile scalaCompile = (ScalaCompile) project.getTasks().getByName("compileScala");
        FileCollection playArtifacts = configurations.getPlay().getAllArtifacts();
        scalaCompile.setClasspath(playArtifacts);
        scalaCompile.getOptions().setAnnotationProcessorPath(playArtifacts);
        scalaCompile.dependsOn(getTwirlCompileTask(project));
        scalaCompile.dependsOn(getRoutesCompileTask(project));
    }

    private static TwirlCompile getTwirlCompileTask(Project project) {
        return (TwirlCompile) project.getTasks().getByName(TWIRL_COMPILE_TASK_NAME);
    }

    private static RoutesCompile getRoutesCompileTask(Project project) {
        return (RoutesCompile) project.getTasks().getByName(ROUTES_COMPILE_TASK_NAME);
    }

    private static SourceSet getMainSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        return javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }

    private void createRunTask(Project project, PlayPluginConfigurations configurations, PlayPlatform playPlatform, Jar mainJarTask, Jar assetsJarTask) {
        PlayToolChainInternal playToolChain = ((ProjectInternal) project).getServices().get(PlayToolChainInternal.class);

        project.getTasks().create(RUN_TASK_NAME, PlayRun.class, playRun -> {
            playRun.setDescription("Runs the Play application for local development.");
            playRun.setGroup(RUN_GROUP);
            playRun.setHttpPort(DEFAULT_HTTP_PORT);
            playRun.getWorkingDir().set(project.getProjectDir());
            playRun.setPlayToolProvider(playToolChain.select(playPlatform));
            playRun.setApplicationJar(mainJarTask.getArchivePath());
            playRun.setAssetsJar(assetsJarTask.getArchivePath());
            playRun.setAssetsDirs(new HashSet<>(Arrays.asList(project.file("public"))));
            playRun.setRuntimeClasspath(configurations.getPlayRun().getNonChangingArtifacts());
            playRun.setChangingClasspath(configurations.getPlayRun().getChangingArtifacts());
            playRun.dependsOn(project.getTasks().getByName(BUILD_TASK_NAME));
        });
    }
}

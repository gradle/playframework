package org.gradle.playframework.plugins;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationPublications;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.internal.file.collections.ImmutableFileCollection;
import org.gradle.api.internal.file.collections.LazilyInitializedFileCollection;
import org.gradle.api.internal.tasks.TaskDependencyResolveContext;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.scala.ScalaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.playframework.extensions.PlayExtension;
import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;
import org.gradle.playframework.plugins.internal.PlayPluginHelper;
import org.gradle.playframework.tasks.PlayRun;
import org.gradle.playframework.tasks.RoutesCompile;
import org.gradle.playframework.tasks.TwirlCompile;
import org.gradle.util.VersionNumber;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME;
import static org.gradle.api.plugins.JavaBasePlugin.BUILD_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME;

/**
 * Plugin for Play Framework component support.
 */
public class PlayApplicationPlugin implements Plugin<Project> {

    public static final String PLAY_EXTENSION_NAME = "play";
    public static final String ASSETS_JAR_TASK_NAME = "createPlayAssetsJar";
    public static final String RUN_TASK_NAME = "runPlay";
    public static final int DEFAULT_HTTP_PORT = 9000;
    public static final String RUN_GROUP = "Run";
    public static final String PLATFORM_CONFIGURATION = "play";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(ScalaPlugin.class);

        PlayExtension playExtension = project.getExtensions().create(PLAY_EXTENSION_NAME, PlayExtension.class, project.getObjects());
        Configuration playConfiguration = project.getConfigurations().create(PLATFORM_CONFIGURATION);
        project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME).extendsFrom(playConfiguration);

        applyPlugins(project);

        configureJavaAndScalaSourceSet(project);
        TaskProvider<Jar> mainJarTask = project.getTasks().named(JAR_TASK_NAME, Jar.class);
        TaskProvider<Jar> assetsJarTask = createAssetsJarTask(project);
        registerOutgoingArtifact(project, assetsJarTask);
        TaskProvider<PlayRun> playRun = createRunTask(project, playExtension, mainJarTask, assetsJarTask);

        project.afterEvaluate(project1 -> {
            PlayPlatform playPlatform = playExtension.getPlatform();
            failIfInjectedRouterIsUsedWithOldVersion(playExtension.getInjectedRoutesGenerator().get(), playPlatform);
            addAutomaticDependencies(project.getDependencies(), playPlatform);
            configureRunTask(playRun, filtered(project.getConfigurations().getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)));
        });
    }

    private void applyPlugins(Project project) {
        project.getPluginManager().apply(PlayTwirlPlugin.class);
        project.getPluginManager().apply(PlayRoutesPlugin.class);
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

    private void addAutomaticDependencies(DependencyHandler dependencies, PlayPlatform playPlatform) {
        dependencies.add(PLATFORM_CONFIGURATION, playPlatform.getDependencyNotation("play").get());
        dependencies.add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, playPlatform.getDependencyNotation("play-test").get());
        dependencies.add(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME, playPlatform.getDependencyNotation("play-docs").get());

        PlayMajorVersion playMajorVersion = PlayMajorVersion.forPlatform(playPlatform);
        if (playMajorVersion == PlayMajorVersion.PLAY_2_6_X) {
            // This has the downside of adding play-java-forms for all kind of play projects
            // including Scala based projects. Still, users can exclude the dependency if they
            // want/need. Maybe in the future we can enable users to have some flag to specify
            // if the project is Java or Scala based.
            dependencies.add(PLATFORM_CONFIGURATION, playPlatform.getDependencyNotation("play-java-forms").get());
        }
    }

    private void configureJavaAndScalaSourceSet(Project project) {
        SourceSet mainSourceSet = PlayPluginHelper.getMainJavaSourceSet(project);
        SourceDirectorySet mainResourcesDirectorySet = mainSourceSet.getResources();
        mainResourcesDirectorySet.setSrcDirs(Arrays.asList("conf"));

        SourceDirectorySet scalaSourceDirectorySet = PlayPluginHelper.getScalaSourceDirectorySet(project);
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

    private void configureRunTask(TaskProvider<PlayRun> playRun, PlayConfiguration filteredRuntime) {
        playRun.configure(task -> {
            task.setRuntimeClasspath(filteredRuntime.getNonChangingArtifacts());
            task.setChangingClasspath(filteredRuntime.getChangingArtifacts());
        });
    }

    PlayConfiguration filtered(Configuration configuration) {
        return new PlayConfiguration(configuration);
    }

    /**
     * Wrapper around a Configuration instance used by the PlayApplicationPlugin.
     */
    class PlayConfiguration {
        private final Configuration configuration;

        PlayConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }

        FileCollection getChangingArtifacts() {
            return new FilterByProjectComponentTypeFileCollection(configuration, true);
        }

        FileCollection getNonChangingArtifacts() {
            return new FilterByProjectComponentTypeFileCollection(configuration, false);
        }
    }

    private static class FilterByProjectComponentTypeFileCollection extends LazilyInitializedFileCollection {
        private final Configuration configuration;
        private final boolean matchProjectComponents;

        private FilterByProjectComponentTypeFileCollection(Configuration configuration, boolean matchProjectComponents) {
            this.configuration = configuration;
            this.matchProjectComponents = matchProjectComponents;
        }

        @Override
        public String getDisplayName() {
            return configuration.toString();
        }

        @Override
        public FileCollectionInternal createDelegate() {
            Set<File> files = new HashSet<>();
            for (ResolvedArtifact artifact : configuration.getResolvedConfiguration().getResolvedArtifacts()) {
                if ((artifact.getId().getComponentIdentifier() instanceof ProjectComponentIdentifier) == matchProjectComponents) {
                    files.add(artifact.getFile());
                }
            }
            return ImmutableFileCollection.of(Collections.unmodifiableSet(files));
        }

        @Override
        public void visitDependencies(TaskDependencyResolveContext context) {
            context.add(configuration);
        }
    }
}

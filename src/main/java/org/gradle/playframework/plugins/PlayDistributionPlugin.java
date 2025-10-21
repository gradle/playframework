package org.gradle.playframework.plugins;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.distribution.Distribution;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.application.CreateStartScripts;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.Tar;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.playframework.extensions.PlayExtension;
import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;
import org.gradle.util.GradleVersion;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME;

public class PlayDistributionPlugin implements Plugin<Project> {

    public static final String DISTRIBUTION_GROUP = "distribution";
    public static final String STAGE_LIFECYCLE_TASK_NAME = "stage";
    public static final String DIST_LIFECYCLE_TASK_NAME = "dist";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(PlayApplicationPlugin.class);
        project.getPluginManager().apply(DistributionPlugin.class);

        TaskProvider<Task> stageLifecycleTask = createStageLifecycleTask(project);
        TaskProvider<Task> distLifecycleTask = createDistLifecycleTasks(project);

        DistributionContainer distributionContainer = (DistributionContainer) project.getExtensions().getByName("distributions");

        distributionContainer.all(distribution -> {
            createDistributionContentTasks(project, distribution);
            createDistributionZipTasks(project, distribution, stageLifecycleTask, distLifecycleTask);
        });
    }

    private TaskProvider<Task> createStageLifecycleTask(Project project) {
        return project.getTasks().register(STAGE_LIFECYCLE_TASK_NAME, task -> {
            task.setDescription("Stages all Play distributions.");
            task.setGroup(DISTRIBUTION_GROUP);
        });
    }

    private TaskProvider<Task> createDistLifecycleTasks(Project project) {
        return project.getTasks().register(DIST_LIFECYCLE_TASK_NAME, task -> {
            task.setDescription("Assembles all Play distributions.");
            task.setGroup(DISTRIBUTION_GROUP);
        });
    }

    private void createDistributionContentTasks(Project project, Distribution distribution) {
        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PlayApplicationPlugin.PLAY_EXTENSION_NAME);
        TaskProvider<Jar> mainJarTask = project.getTasks().named(JAR_TASK_NAME, Jar.class);
        TaskProvider<Jar> assetsJarTask = project.getTasks().named(PlayApplicationPlugin.ASSETS_JAR_TASK_NAME, Jar.class);

        final String capitalizedDistName = capitalizeDistributionName(distribution.getName());
        final String jarTaskName = "create" + capitalizedDistName + "DistributionJar";

        // Workaround for gradle/gradle#25372: how to safely use ResolvedArtifactResult as task input.
        Configuration runtimeClasspath = project.getConfigurations().getByName(RUNTIME_CLASSPATH_CONFIGURATION_NAME);
        Provider<Set<ResolvedArtifactResult>> artifactsProvider = runtimeClasspath.getIncoming().getArtifacts().getResolvedArtifacts();
        Provider<Set<ComponentIdAndFile>> componentAndFileProvider = artifactsProvider.map(resultSet ->
            resultSet.stream()
                .map(result -> new ComponentIdAndFile(result.getId().getComponentIdentifier(), result.getFile()))
                .collect(Collectors.toSet())
        );

        TaskProvider<Jar> distributionJarTask = project.getTasks().register(jarTaskName, Jar.class, jar -> {
            jar.setDescription("Assembles an application jar suitable for deployment.");
            jar.dependsOn(mainJarTask, assetsJarTask);
            jar.from(project.zipTree(mainJarTask.flatMap(AbstractArchiveTask::getArchiveFile)));
            jar.getDestinationDirectory().convention(project.getLayout().getBuildDirectory().dir("distributionJars/" + distribution.getName()));
            jar.getArchiveBaseName().convention(mainJarTask.flatMap(AbstractArchiveTask::getArchiveBaseName));

            Map<String, Object> classpath = new HashMap<>();
            classpath.put("Class-Path", new PlayManifestClasspath(runtimeClasspath, componentAndFileProvider, assetsJarTask.get().getArchiveFile().get().getAsFile()));
            jar.getManifest().attributes(classpath);
        });

        final File scriptsDir = new File(project.getBuildDir(), "scripts");
        String createStartScriptsTaskName = "create" + capitalizedDistName + "StartScripts";
        TaskProvider<CreateStartScripts> createStartScriptsTask = project.getTasks().register(createStartScriptsTaskName, CreateStartScripts.class, createStartScripts -> {
            createStartScripts.setDescription("Creates OS specific scripts to run the distribution.");
            createStartScripts.setClasspath(distributionJarTask.get().getOutputs().getFiles());
            if (GradleVersion.current().compareTo(GradleVersion.version("6.4")) >= 0) {
                createStartScripts.getMainClass().set(getMainClass(playExtension.getPlatform()));
            } else {
                createStartScripts.setMainClassName(getMainClass(playExtension.getPlatform()));
            }
            createStartScripts.setApplicationName(distribution.getName());
            createStartScripts.setOutputDir(scriptsDir);
        });

        CopySpec distSpec = distribution.getContents();
        distSpec.into("lib", copySpec -> {
            copySpec.from(distributionJarTask);
            copySpec.from(assetsJarTask.flatMap(AbstractArchiveTask::getArchiveFile));
            copySpec.from(project.getConfigurations().getByName(RUNTIME_CLASSPATH_CONFIGURATION_NAME));
            copySpec.eachFile(new PrefixArtifactFileNames(componentAndFileProvider));
        });

        distSpec.into("bin", copySpec -> {
            copySpec.from(createStartScriptsTask);
            setFileMode(copySpec);
        });

        distSpec.into("conf", copySpec -> copySpec.from("conf").exclude("routes"));
        distSpec.from("README");
    }

    private void setFileMode(CopySpec copySpec) {
        try {
            if (GradleVersion.current().compareTo(GradleVersion.version("8.3")) >= 0) {
                copySpec.getClass().getMethod("filePermissions", Action.class)
                        .invoke(copySpec, (Action<?>) filePermission -> {
                            try {
                                filePermission.getClass().getMethod("unix", String.class).invoke(filePermission, "0755");
                            } catch (Exception e) {
                                throw new GradleException("Failed to set unix file permission", e);
                            }
                        });
            } else {
                copySpec.getClass().getMethod("setFileMode", Integer.class).invoke(copySpec, 0755);
            }
        } catch (Exception e) {
            throw new GradleException("Failed to set file permissions", e);
        }
    }

    private String getMainClass(PlayPlatform playPlatform) {
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
                return "play.core.server.NettyServer";
            case PLAY_2_4_X:
            case PLAY_2_5_X:
            case PLAY_2_6_X:
            case PLAY_2_7_X:
            default:
                return "play.core.server.ProdServerStart";
        }
    }

    private void createDistributionZipTasks(Project project, Distribution distribution, TaskProvider<Task> stageLifecycleTask, TaskProvider<Task> distLifecycleTask) {
        final String capitalizedDistName = capitalizeDistributionName(distribution.getName());
        final String stageTaskName = "stage" + capitalizedDistName + "Dist";
        final File stageDir = new File(project.getBuildDir(), "stage");
        final Provider<String> baseName = getBaseNameForDistribution(project.getProviders(), distribution);

        TaskProvider<Sync> stageSyncTask = project.getTasks().register(stageTaskName, Sync.class, sync -> {
            sync.setDescription("Copies the '" + distribution.getName() + "' distribution to a staging directory.");
            sync.setDestinationDir(stageDir);

            sync.into(baseName.get(), copySpec -> copySpec.with(distribution.getContents()));
        });

        stageLifecycleTask.configure(task -> task.dependsOn(stageSyncTask));

        final String distributionZipTaskName = "create" + capitalizedDistName + "ZipDist";
        TaskProvider<Zip> distZipTask = project.getTasks().register(distributionZipTaskName, Zip.class, zip -> {
            zip.setDescription("Packages the '" + distribution.getName() + "' distribution as a zip file.");
            // TODO: This should be using .convention, but old versions of Gradle did not honor conventions in some cases
            zip.getArchiveBaseName().set(baseName);
            zip.getDestinationDirectory().convention(project.getLayout().getBuildDirectory().dir("distributions"));
            zip.from(stageSyncTask);
        });

        final String distributionTarTaskName = "create" + capitalizedDistName + "TarDist";
        TaskProvider<Tar> distTarTask = project.getTasks().register(distributionTarTaskName, Tar.class, tar -> {
            tar.setDescription("Packages the '" + distribution.getName() + "' distribution as a tar file.");
            // TODO: This should be using .convention, but old versions of Gradle did not honor conventions in some cases
            tar.getArchiveBaseName().set(baseName);
            tar.getDestinationDirectory().convention(project.getLayout().getBuildDirectory().dir("distributions"));
            tar.from(stageSyncTask);
        });

        distLifecycleTask.configure(task -> {
            task.dependsOn(distZipTask);
            task.dependsOn(distTarTask);
        });
    }

    private Provider<String> getBaseNameForDistribution(ProviderFactory providers, Distribution distribution) {
        if (GradleVersion.current().compareTo(GradleVersion.version("6.0")) < 0) {
            return providers.provider(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String baseName = (String) Distribution.class.getMethod("getBaseName").invoke(distribution);
                    return "".equals(baseName) ? "" : distribution.getName();
                }
            });
        } else {
            return distribution.getDistributionBaseName().map(baseName -> baseName.isEmpty() ? "" : distribution.getName()).orElse(distribution.getName());
        }
    }

    private String capitalizeDistributionName(String distributionName) {
        return Character.toUpperCase(distributionName.charAt(0)) + distributionName.substring(1);
    }

    /**
     * Represents a classpath to be defined in a jar manifest
     */
    static class PlayManifestClasspath {
        final File assetsJarFile;
        private final Provider<Set<ComponentIdAndFile>> componentAndFileProvider;
        private final FileCollection configuration;

        public PlayManifestClasspath(FileCollection configuration, Provider<Set<ComponentIdAndFile>> componentAndFileProvider, File assetsJarFile) {
            this.componentAndFileProvider = componentAndFileProvider;
            this.configuration = configuration;
            this.assetsJarFile = assetsJarFile;
        }

        @Override
        public String toString() {
            Stream<File> allFiles = Stream.concat(configuration.getFiles().stream(), Collections.singleton(assetsJarFile).stream());
            Stream<String> transformedFiles = allFiles.map(new PrefixArtifactFileNames(componentAndFileProvider));
            return String.join(" ",
                    transformedFiles.collect(Collectors.toList())
            );
        }
    }

    static class PrefixArtifactFileNames implements Action<FileCopyDetails>, Function<File, String> {
        private final Provider<Set<ComponentIdAndFile>> componentAndFileProvider;
        Map<File, String> renames;

        PrefixArtifactFileNames(Provider<Set<ComponentIdAndFile>> componentAndFileProvider) {
            this.componentAndFileProvider = componentAndFileProvider;
        }

        @Override
        public void execute(FileCopyDetails fileCopyDetails) {
            fileCopyDetails.setName(apply(fileCopyDetails.getFile()));
        }

        @Override
        public String apply(File input) {
            calculateRenames();
            String rename = renames.get(input);
            if (rename != null) {
                return rename;
            }
            return input.getName();
        }

        private void calculateRenames() {
            if (renames == null) {
                renames = calculate();
            }
        }

        private Map<File, String> calculate() {
            Map<File, String> files = new HashMap<>();
            for (ComponentIdAndFile artifact : getResolvedArtifacts()) {
                ComponentIdentifier componentId = artifact.getComponentId();
                if (componentId instanceof ProjectComponentIdentifier) {
                    // rename project dependencies
                    ProjectComponentIdentifier projectComponentIdentifier = (ProjectComponentIdentifier) componentId;
                    files.put(artifact.getArtifactFile(), renameForProject(projectComponentIdentifier, artifact.getArtifactFile()));
                } else if (componentId instanceof ModuleComponentIdentifier) {
                    ModuleComponentIdentifier moduleComponentIdentifier = (ModuleComponentIdentifier) componentId;
                    files.put(artifact.getArtifactFile(), renameForModule(moduleComponentIdentifier, artifact.getArtifactFile()));
                } else {
                    // don't rename other types of dependencies
                    files.put(artifact.getArtifactFile(), artifact.getArtifactFile().getName());
                }
            }
            return Collections.unmodifiableMap(files);
        }

        Set<ComponentIdAndFile> getResolvedArtifacts() {
            return componentAndFileProvider.get();
        }

        static String renameForProject(ProjectComponentIdentifier id, File file) {
            String fileName = file.getName();
            if (shouldBeRenamed(file)) {
                String projectPath = id.getProjectPath();
                projectPath = projectPathToSafeFileName(projectPath);
                return maybePrefix(projectPath, file);
            }
            return fileName;
        }

        static String renameForModule(ModuleComponentIdentifier id, File file) {
            if (shouldBeRenamed(file)) {
                return maybePrefix(id.getGroup(), file);
            }
            return file.getName();
        }

        private static String maybePrefix(String prefix, File file) {
            if (!(prefix != null && prefix.length() > 0)) {
                return file.getName();
            }
            return prefix + "-" + file.getName();
        }

        private static String projectPathToSafeFileName(String projectPath) {
            if (projectPath.equals(":")) {
                return null;
            }
            return projectPath.replaceAll(":", ".").substring(1);
        }

        private static boolean shouldBeRenamed(File file) {
            return hasExtension(file, ".jar");
        }
    }

    private static boolean hasExtension(File file, String extension) {
        return file.getPath().endsWith(extension);
    }
}

class ComponentIdAndFile {
    private final ComponentIdentifier componentId;
    private final File artifactFile;


    ComponentIdAndFile(ComponentIdentifier componentId, File artifactFile) {
        this.componentId = componentId;
        this.artifactFile = artifactFile;
    }

    public ComponentIdentifier getComponentId() {
        return componentId;
    }

    public File getArtifactFile() {
        return artifactFile;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ComponentIdAndFile componentIdAndFile = (ComponentIdAndFile) o;
        return Objects.equals(componentId, componentIdAndFile.componentId) && Objects.equals(artifactFile, componentIdAndFile.artifactFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, artifactFile);
    }
}

package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.extensions.PlayPluginConfigurations;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactCollection;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.distribution.Distribution;
import org.gradle.api.distribution.DistributionContainer;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.application.CreateStartScripts;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.Tar;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.play.internal.platform.PlayMajorVersion;
import org.gradle.play.platform.PlayPlatform;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lightbend.play.plugins.PlayApplicationPlugin.*;
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;

public class PlayDistributionPlugin implements Plugin<Project> {

    public static final String DISTRIBUTION_GROUP = "distribution";
    public static final String STAGE_LIFECYCLE_TASK_NAME = "stage";
    public static final String DIST_LIFECYCLE_TASK_NAME = "dist";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(PlayApplicationPlugin.class);
        project.getPluginManager().apply(DistributionPlugin.class);

        Task stageLifecycleTask = createStageLifecycleTask(project);
        Task distLifecycleTask = createDistLifecycleTasks(project);

        DistributionContainer distributionContainer = (DistributionContainer) project.getExtensions().getByName("distributions");

        distributionContainer.all(distribution -> {
            createDistributionContentTasks(project, distribution);
            createDistributionZipTasks(project, distribution, stageLifecycleTask, distLifecycleTask);
        });
    }

    private Task createStageLifecycleTask(Project project) {
        return project.getTasks().create(STAGE_LIFECYCLE_TASK_NAME, task -> {
            task.setDescription("Stages all Play distributions.");
            task.setGroup(DISTRIBUTION_GROUP);
        });
    }

    private Task createDistLifecycleTasks(Project project) {
        return project.getTasks().create(DIST_LIFECYCLE_TASK_NAME, task -> {
            task.setDescription("Assembles all Play distributions.");
            task.setGroup(DISTRIBUTION_GROUP);
        });
    }

    private void createDistributionContentTasks(Project project, Distribution distribution) {
        PlayPluginConfigurations configurations = (PlayPluginConfigurations) project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);
        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);
        Jar mainJarTask = (Jar) project.getTasks().getByName(JAR_TASK_NAME);
        Jar assetsJarTask = (Jar) project.getTasks().getByName(ASSETS_JAR_TASK_NAME);

        final File distJarDir = new File(project.getBuildDir(), "distributionJars/" + distribution.getName());
        final String capitalizedDistName = capitalizeDistributionName(distribution.getName());
        final String jarTaskName = "create" + capitalizedDistName + "DistributionJar";

        Jar distributionJarTask = project.getTasks().create(jarTaskName, Jar.class, jar -> {
            jar.setDescription("Assembles an application jar suitable for deployment.");
            jar.dependsOn(mainJarTask, assetsJarTask);
            jar.from(project.zipTree(mainJarTask.getArchivePath()));
            jar.setDestinationDir(distJarDir);
            jar.setBaseName(mainJarTask.getBaseName());

            Map<String, Object> classpath = new HashMap<>();
            classpath.put("Class-Path", new PlayManifestClasspath(configurations.getPlayRun(), assetsJarTask.getArchivePath()));
            jar.getManifest().attributes(classpath);
        });

        final File scriptsDir = new File(project.getBuildDir(), "scripts");
        String createStartScriptsTaskName = "create" + capitalizedDistName + "StartScripts";
        CreateStartScripts createStartScriptsTask = project.getTasks().create(createStartScriptsTaskName, CreateStartScripts.class, createStartScripts -> {
            createStartScripts.setDescription("Creates OS specific scripts to run the distribution.");
            createStartScripts.setClasspath(distributionJarTask.getOutputs().getFiles());
            createStartScripts.setMainClassName(getMainClass(playExtension.getPlatform().asPlayPlatform()));
            createStartScripts.setApplicationName(distribution.getName());
            createStartScripts.setOutputDir(scriptsDir);
        });

        CopySpec distSpec = distribution.getContents();
        distSpec.into("lib", copySpec -> {
            copySpec.from(distributionJarTask);
            copySpec.from(assetsJarTask.getArchivePath());
            copySpec.from(configurations.getPlayRun().getAllArtifacts());
            copySpec.eachFile(new PrefixArtifactFileNames(configurations.getPlayRun()));
        });

        distSpec.into("bin", copySpec -> {
            copySpec.from(createStartScriptsTask);
            copySpec.setFileMode(0755);
        });

        distSpec.into("conf", copySpec -> copySpec.from("conf").exclude("routes"));
        distSpec.from("README");
    }

    private String getMainClass(PlayPlatform playPlatform) {
        String playVersion = playPlatform.getPlayVersion();
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
                return "play.core.server.NettyServer";
            case PLAY_2_4_X:
            case PLAY_2_5_X:
            case PLAY_2_6_X:
                return "play.core.server.ProdServerStart";
            default:
                throw new RuntimeException("Could not determine main class for Play version:" + playVersion);
        }
    }

    private void createDistributionZipTasks(Project project, Distribution distribution, Task stageLifecycleTask, Task distLifecycleTask) {
        final String capitalizedDistName = capitalizeDistributionName(distribution.getName());
        final String stageTaskName = "stage" + capitalizedDistName + "Dist";
        final File stageDir = new File(project.getBuildDir(), "stage");
        final String baseName = (distribution.getBaseName() != null && "".equals(distribution.getBaseName())) ? distribution.getBaseName() : distribution.getName();

        Sync stageSyncTask = project.getTasks().create(stageTaskName, Sync.class, sync -> {
            sync.setDescription("Copies the '" + distribution.getName() + "' distribution to a staging directory.");
            sync.setDestinationDir(stageDir);

            sync.into(baseName, copySpec -> copySpec.with(distribution.getContents()));
        });

        stageLifecycleTask.dependsOn(stageSyncTask);

        final String distributionZipTaskName = "create" + capitalizedDistName + "ZipDist";
        Zip distZipTask = project.getTasks().create(distributionZipTaskName, Zip.class, zip -> {
            zip.setDescription("Packages the '" + distribution.getName() + "' distribution as a zip file.");
            zip.setBaseName(baseName);
            zip.setDestinationDir(new File(project.getBuildDir(), "distributions"));
            zip.from(stageSyncTask);
        });

        final String distributionTarTaskName = "create" + capitalizedDistName + "TarDist";
        Tar distTarTask = project.getTasks().create(distributionTarTaskName, Tar.class, tar -> {
            tar.setDescription("Packages the '" + distribution.getName() + "' distribution as a tar file.");
            tar.setBaseName(baseName);
            tar.setDestinationDir(new File(project.getBuildDir(), "distributions"));
            tar.from(stageSyncTask);
        });

        distLifecycleTask.dependsOn(distZipTask);
        distLifecycleTask.dependsOn(distTarTask);
    }

    private String capitalizeDistributionName(String distributionName) {
        return Character.toUpperCase(distributionName.charAt(0)) + distributionName.substring(1);
    }

    /**
     * Represents a classpath to be defined in a jar manifest
     */
    static class PlayManifestClasspath {
        final PlayPluginConfigurations.PlayConfiguration playConfiguration;
        final File assetsJarFile;

        public PlayManifestClasspath(PlayPluginConfigurations.PlayConfiguration playConfiguration, File assetsJarFile) {
            this.playConfiguration = playConfiguration;
            this.assetsJarFile = assetsJarFile;
        }

        @Override
        public String toString() {
            Stream<File> allFiles = Stream.concat(playConfiguration.getAllArtifacts().getFiles().stream(), Collections.singleton(assetsJarFile).stream());
            Stream<String> transformedFiles = allFiles.map(new PrefixArtifactFileNames(playConfiguration));
            return String.join(" ",
                    transformedFiles.collect(Collectors.toList())
            );
        }
    }

    static class PrefixArtifactFileNames implements Action<FileCopyDetails>, Function<File, String> {
        private final PlayPluginConfigurations.PlayConfiguration configuration;
        Map<File, String> renames;

        PrefixArtifactFileNames(PlayPluginConfigurations.PlayConfiguration configuration) {
            this.configuration = configuration;
        }

        @Override
        public void execute(FileCopyDetails fileCopyDetails) {
            fileCopyDetails.setName(apply(fileCopyDetails.getFile()));
        }

        @Override
        public String apply(File input) {
            calculateRenames();
            String rename = renames.get(input);
            if (rename!=null) {
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
            for (ResolvedArtifactResult artifact : getResolvedArtifacts()) {
                ComponentIdentifier componentId = artifact.getId().getComponentIdentifier();
                if (componentId instanceof ProjectComponentIdentifier) {
                    // rename project dependencies
                    ProjectComponentIdentifier projectComponentIdentifier = (ProjectComponentIdentifier) componentId;
                    files.put(artifact.getFile(), renameForProject(projectComponentIdentifier, artifact.getFile()));
                } else if (componentId instanceof ModuleComponentIdentifier) {
                    ModuleComponentIdentifier moduleComponentIdentifier = (ModuleComponentIdentifier) componentId;
                    files.put(artifact.getFile(), renameForModule(moduleComponentIdentifier, artifact.getFile()));
                } else {
                    // don't rename other types of dependencies
                    files.put(artifact.getFile(), artifact.getFile().getName());
                }
            }
            return Collections.unmodifiableMap(files);
        }

        Set<ResolvedArtifactResult> getResolvedArtifacts() {
            ArtifactCollection artifacts = configuration.getConfiguration().getIncoming().getArtifacts();
            return artifacts.getArtifacts();
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

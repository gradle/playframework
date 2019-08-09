package org.gradle.playframework.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.tools.internal.run.DefaultPlayRunSpec;
import org.gradle.playframework.tools.internal.run.PlayApplicationDeploymentHandle;
import org.gradle.playframework.tools.internal.run.PlayApplicationRunner;
import org.gradle.playframework.tools.internal.run.PlayApplicationRunnerFactory;
import org.gradle.playframework.tools.internal.run.PlayRunSpec;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.deployment.internal.DeploymentRegistry;
import org.gradle.internal.fingerprint.classpath.ClasspathFingerprinter;
import org.gradle.process.internal.worker.WorkerProcessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetSocketAddress;

/**
 * Task to run a Play application.
 */
public class PlayRun extends DefaultTask {
    public static final int DEFAULT_HTTP_PORT = 9000;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayRun.class);

    private final BaseForkOptions forkOptions;

    private final Property<Integer> httpPort;
    private final DirectoryProperty workingDir;
    private final RegularFileProperty applicationJar;
    private final RegularFileProperty assetsJar;
    private final ConfigurableFileCollection assetsDirs;
    private final ConfigurableFileCollection runtimeClasspath;
    private final ConfigurableFileCollection changingClasspath;
    private final Property<PlayPlatform> platform;

    public PlayRun() {
        ObjectFactory objects = getProject().getObjects();

        httpPort = objects.property(Integer.class).value(DEFAULT_HTTP_PORT);
        workingDir = objects.directoryProperty();
        applicationJar = objects.fileProperty();
        assetsJar = objects.fileProperty();
        assetsDirs = getProject().files();
        runtimeClasspath = getProject().files();
        changingClasspath = getProject().files();
        platform = objects.property(PlayPlatform.class);
        forkOptions = new BaseForkOptions();
    }

    @TaskAction
    public void run() {
        String deploymentId = getPath();
        DeploymentRegistry deploymentRegistry = getDeploymentRegistry();
        PlayApplicationDeploymentHandle deploymentHandle = deploymentRegistry.get(deploymentId, PlayApplicationDeploymentHandle.class);

        if (deploymentHandle == null) {
            PlayRunSpec spec = new DefaultPlayRunSpec(runtimeClasspath, changingClasspath, applicationJar.getAsFile().get(), assetsJar.getAsFile().get(), assetsDirs, workingDir.get().getAsFile(), getForkOptions(), getHttpPort().get());
            PlayApplicationRunner playApplicationRunner = PlayApplicationRunnerFactory.create(platform.get(), getWorkerProcessFactory(), getClasspathFingerprinter());
            deploymentHandle = deploymentRegistry.start(deploymentId, DeploymentRegistry.ChangeBehavior.BLOCK, PlayApplicationDeploymentHandle.class, spec, playApplicationRunner);

            InetSocketAddress playAppAddress = deploymentHandle.getPlayAppAddress();
            String playUrl = "http://localhost:" + playAppAddress.getPort() + "/";
            LOGGER.warn("Running Play App ({}) at {}", getPath(), playUrl);
        }
    }

    /**
     * fork options for the running a Play application.
     *
     * @return Fork options
     */
    @Nested
    public BaseForkOptions getForkOptions() {
        return forkOptions;
    }

    /**
     * The HTTP port listened to by the Play application.
     *
     * This port should be available.  The Play application will fail to start if the port is already in use.
     *
     * @return HTTP port
     */
    @Internal
    public Property<Integer> getHttpPort() {
        return httpPort;
    }

    /**
     * The working directory.
     *
     * @return The working directory
     */
    @Internal
    public DirectoryProperty getWorkingDir() {
        return workingDir;
    }

    /**
     * The Play application jar to run.
     *
     * @return The Play application jar
     */
    @Classpath
    public RegularFileProperty getApplicationJar() {
        return applicationJar;
    }

    /**
     * The assets jar to run with the Play application.
     *
     * @return The assets jar
     */
    @Classpath
    public RegularFileProperty getAssetsJar() {
        return assetsJar;
    }

    /**
     * The directories of the assets for the Play application (for live reload functionality).
     *
     * @return The directories of the assets
     */
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public ConfigurableFileCollection getAssetsDirs() {
        return assetsDirs;
    }

    /**
     * The runtime classpath for the Play application.
     *
     * @return The runtime classpath
     */
    @Classpath
    public ConfigurableFileCollection getRuntimeClasspath() {
        return runtimeClasspath;
    }

    /**
     * The changing classpath for the Play application.
     *
     * @return The changing classpath
     */
    @Classpath
    public ConfigurableFileCollection getChangingClasspath() {
        return changingClasspath;
    }

    @Nested
    public Property<PlayPlatform> getPlatform() {
        return platform;
    }

    @Inject
    public DeploymentRegistry getDeploymentRegistry() {
        throw new UnsupportedOperationException();
    }

    @Inject
    public WorkerProcessFactory getWorkerProcessFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    public ClasspathFingerprinter getClasspathFingerprinter() {
        throw new UnsupportedOperationException();
    }
}

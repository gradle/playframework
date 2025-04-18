package org.gradle.playframework.tasks;

import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.tasks.internal.RoutesCompileParameters;
import org.gradle.playframework.tasks.internal.RoutesCompileRunnable;
import org.gradle.playframework.tasks.internal.RoutesCompileWorkAction;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.routes.DefaultRoutesCompileSpec;
import org.gradle.playframework.tools.internal.routes.RoutesCompileSpec;
import org.gradle.playframework.tools.internal.routes.RoutesCompilerFactory;
import org.gradle.util.GradleVersion;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.ProcessWorkerSpec;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;

/**
 * Task for compiling routes templates into Scala code.
 */
public class RoutesCompile extends SourceTask {

    private final WorkerExecutor workerExecutor;

    /**
     * Target directory for the compiled route files.
     */
    private final DirectoryProperty outputDirectory;

    /**
     * Additional imports used for by generated files.
     */
    private final ListProperty<String> additionalImports;

    private final Property<Boolean> namespaceReverseRouter;
    private final Property<Boolean> generateReverseRoutes;
    private final Property<PlayPlatform> platform;
    private final Property<Boolean> injectedRoutesGenerator;
    private final ConfigurableFileCollection routesCompilerClasspath;
    private final File projectDir;

    @Inject
    public RoutesCompile(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        this.outputDirectory = getProject().getObjects().directoryProperty();
        this.additionalImports = getProject().getObjects().listProperty(String.class).empty();
        this.namespaceReverseRouter = getProject().getObjects().property(Boolean.class);
        this.namespaceReverseRouter.set(false);
        this.generateReverseRoutes = getProject().getObjects().property(Boolean.class);
        this.generateReverseRoutes.set(true);
        this.platform = getProject().getObjects().property(PlayPlatform.class);
        this.injectedRoutesGenerator = getProject().getObjects().property(Boolean.class);
        this.injectedRoutesGenerator.set(false);
        this.routesCompilerClasspath = getProject().files();
        this.projectDir = getProject().getProjectDir();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PathSensitive(PathSensitivity.RELATIVE)
    public FileTree getSource() {
        return super.getSource();
    }

    /**
     * Returns the directory to generate the parser source files into.
     *
     * @return The output directory.
     */
    @OutputDirectory
    public DirectoryProperty getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Returns the additional imports of the Play Routes compiler.
     *
     * @return The additional imports.
     */
    @Input
    public ListProperty<String> getAdditionalImports() {
        return additionalImports;
    }

    @Classpath
    public ConfigurableFileCollection getRoutesCompilerClasspath() {
        return routesCompilerClasspath;
    }

    @TaskAction
    @SuppressWarnings("Convert2Lambda")
    void compile() {
        RoutesCompileSpec spec = new DefaultRoutesCompileSpec(
            getSource().getFiles(),
            getOutputDirectory().get().getAsFile(),
            isJavaProject(),
            getNamespaceReverseRouter().get(),
            getGenerateReverseRoutes().get(),
            getInjectedRoutesGenerator().get(),
            getAdditionalImports().get(),
            projectDir
        );
        workerExecutor.processIsolation(new Action<ProcessWorkerSpec>() {
            @Override
            public void execute(ProcessWorkerSpec workerSpec) {
                workerSpec.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
                workerSpec.getClasspath().from(routesCompilerClasspath);
            }
        }).submit(RoutesCompileWorkAction.class, new Action<RoutesCompileParameters>() {
            @Override
            public void execute(RoutesCompileParameters parameters) {
                parameters.getCompiler().set(RoutesCompile.this.getCompiler());
                parameters.getSpec().set(spec);
            }
        });
    }

    private Compiler<RoutesCompileSpec> getCompiler() {
        return RoutesCompilerFactory.create(getPlatform().get());
    }

    @Internal
    public boolean isJavaProject() {
        return false;
    }

    @Internal
    public Property<PlayPlatform> getPlatform() {
        return platform;
    }

    /**
     * Whether the reverse router should be namespaced.
     *
     * @return Whether the reverse router should be namespaced
     */
    @Input
    public Property<Boolean> getNamespaceReverseRouter() {
        return namespaceReverseRouter;
    }

    /**
     * Whether a reverse router should be generated.  Default is true.
     *
     * @return Whether a reverse router should be generated
     */
    @Input
    public Property<Boolean> getGenerateReverseRoutes() {
        return generateReverseRoutes;
    }

    /**
     * Is the injected routes generator (<code>play.routes.compiler.InjectedRoutesGenerator</code>) used for
     * generating routes?  Default is false.
     *
     * @return false if StaticRoutesGenerator will be used to generate routes,
     * true if InjectedRoutesGenerator will be used to generate routes.
     */
    @Input
    public Property<Boolean> getInjectedRoutesGenerator() {
        return injectedRoutesGenerator;
    }

}

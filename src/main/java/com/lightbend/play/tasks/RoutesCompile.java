package com.lightbend.play.tasks;

import com.lightbend.play.platform.PlayPlatform;
import com.lightbend.play.tools.Compiler;
import com.lightbend.play.tools.routes.DefaultRoutesCompileSpec;
import com.lightbend.play.tools.routes.RoutesCompileSpec;
import com.lightbend.play.tools.routes.RoutesCompilerFactory;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * Task for compiling routes templates into Scala code.
 */
public class RoutesCompile extends SourceTask {

    private final WorkerExecutor workerExecutor;

    /**
     * Target directory for the compiled route files.
     */
    private File outputDirectory;

    /**
     * Additional imports used for by generated files.
     */
    private final ListProperty<String> additionalImports;

    private final Property<Boolean> namespaceReverseRouter;
    private final Property<Boolean> generateReverseRoutes;
    private final Property<PlayPlatform> platform;
    private BaseForkOptions forkOptions;
    private final Property<Boolean> injectedRoutesGenerator;
    private final ConfigurableFileCollection routesCompilerClasspath;

    @Inject
    public RoutesCompile(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        additionalImports = getProject().getObjects().listProperty(String.class).empty();
        namespaceReverseRouter = getProject().getObjects().property(Boolean.class);
        namespaceReverseRouter.set(false);
        generateReverseRoutes = getProject().getObjects().property(Boolean.class);
        generateReverseRoutes.set(true);
        platform = getProject().getObjects().property(PlayPlatform.class);
        injectedRoutesGenerator = getProject().getObjects().property(Boolean.class);
        injectedRoutesGenerator.set(false);
        routesCompilerClasspath = getProject().getLayout().configurableFiles();
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
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Specifies the directory to generate the parser source files into.
     *
     * @param outputDirectory The output directory. Must not be null.
     */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Returns the additional imports of the Play Routes compiler.
     *
     * @return The additional imports.
     */
    @Input
    public Provider<List<String>> getAdditionalImports() {
        return additionalImports;
    }

    /**
     * Specifies the additional imports of the Play Routes compiler.
     */
    public void setAdditionalImports(List<String> additionalImports) {
        this.additionalImports.addAll(additionalImports);
    }

    @Classpath
    @PathSensitive(PathSensitivity.RELATIVE)
    public FileCollection getRoutesCompilerClasspath() {
        return routesCompilerClasspath;
    }

    public void setRoutesCompilerClasspath(FileCollection routesCompilerClasspath) {
        this.routesCompilerClasspath.setFrom(routesCompilerClasspath);
    }

    @TaskAction
    void compile() {
        RoutesCompileSpec spec = new DefaultRoutesCompileSpec(getSource().getFiles(), getOutputDirectory(), getForkOptions(), isJavaProject(), getNamespaceReverseRouter().get(), getGenerateReverseRoutes().get(), getInjectedRoutesGenerator().get(), getAdditionalImports().get());

        workerExecutor.submit(RoutesCompileRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec, getCompiler());
            workerConfiguration.classpath(routesCompilerClasspath);
            workerConfiguration.setDisplayName("Generating Scala source from routes templates");
        });
        workerExecutor.await();
    }

    private Compiler<RoutesCompileSpec> getCompiler() {
        return RoutesCompilerFactory.create(platform.get());
    }

    @Internal
    public boolean isJavaProject() {
        return false;
    }

    public void setPlatform(Provider<PlayPlatform> platform) {
        this.platform.set(platform);
    }

    /**
     * The fork options to be applied to the Routes compiler.
     *
     * @return The fork options for the Routes compiler.
     */
    @Nested
    public BaseForkOptions getForkOptions() {
        if (forkOptions == null) {
            forkOptions = new BaseForkOptions();
        }
        return forkOptions;
    }

    /**
     * Whether the reverse router should be namespaced.
     */
    @Input
    public Provider<Boolean> getNamespaceReverseRouter() {
        return namespaceReverseRouter;
    }

    /**
     * Sets whether or not the reverse router should be namespaced.
     */
    public void setNamespaceReverseRouter(Provider<Boolean> namespaceReverseRouter) {
        this.namespaceReverseRouter.set(namespaceReverseRouter);
    }

    /**
     * Whether a reverse router should be generated.  Default is true.
     */
    @Input
    public Provider<Boolean> getGenerateReverseRoutes() {
        return generateReverseRoutes;
    }

    /**
     * Sets whether or not a reverse router should be generated.
     */
    public void setGenerateReverseRoutes(Provider<Boolean> generateReverseRoutes) {
        this.generateReverseRoutes.set(generateReverseRoutes);
    }

    /**
     * Is the injected routes generator (<code>play.routes.compiler.InjectedRoutesGenerator</code>) used for
     * generating routes?  Default is false.
     *
     * @return false if StaticRoutesGenerator will be used to generate routes,
     * true if InjectedRoutesGenerator will be used to generate routes.
     */
    @Input
    public Provider<Boolean> getInjectedRoutesGenerator() {
        return injectedRoutesGenerator;
    }

    /**
     * Configure if the injected routes generator should be used to generate routes.
     *
     * @param injectedRoutesGenerator false - use StaticRoutesGenerator
     * true - use InjectedRoutesGenerator
     */
    public void setInjectedRoutesGenerator(Provider<Boolean> injectedRoutesGenerator) {
        this.injectedRoutesGenerator.set(injectedRoutesGenerator);
    }
}

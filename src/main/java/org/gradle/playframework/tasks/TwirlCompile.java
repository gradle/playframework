package org.gradle.playframework.tasks;

import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.tasks.internal.TwirlCompileRunnable;
import org.gradle.playframework.tools.internal.twirl.DefaultTwirlCompileSpec;
import org.gradle.playframework.tools.internal.twirl.TwirlCompileSpec;
import org.gradle.playframework.tools.internal.twirl.TwirlCompilerFactory;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Task for compiling Twirl templates into Scala code.
 */
public class TwirlCompile extends SourceTask {

    private final WorkerExecutor workerExecutor;

    /**
     * Target directory for the compiled template files.
     */
    private final Property<Directory> outputDirectory;

    /**
     * The default imports to use when compiling templates
     */
    private final Property<TwirlImports> defaultImports;

    private final Property<PlayPlatform> platform;
    private final ListProperty<TwirlTemplateFormat> userTemplateFormats;
    private final ListProperty<String> additionalImports;
    private final ConfigurableFileCollection twirlCompilerClasspath;
    private final ListProperty<String> constructorAnnotations;

    @Inject
    public TwirlCompile(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        this.outputDirectory = getProject().getObjects().directoryProperty();
        this.platform = getProject().getObjects().property(PlayPlatform.class);
        this.defaultImports = getProject().getObjects().property(TwirlImports.class);
        this.userTemplateFormats = getProject().getObjects().listProperty(TwirlTemplateFormat.class).empty();
        this.additionalImports = getProject().getObjects().listProperty(String.class);
        this.twirlCompilerClasspath = getProject().files();
        this.constructorAnnotations = getProject().getObjects().listProperty(String.class).empty();
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
    public Property<Directory> getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Returns the default imports that will be used when compiling templates.
     *
     * @return The imports that will be used.
     */
    @Nullable
    @Optional
    @Input
    public Property<TwirlImports> getDefaultImports() {
        return defaultImports;
    }

    @Classpath
    public ConfigurableFileCollection getTwirlCompilerClasspath() {
        return twirlCompilerClasspath;
    }

    @TaskAction
    void compile() {
        RelativeFileCollector relativeFileCollector = new RelativeFileCollector();
        getSource().visit(relativeFileCollector);
        final TwirlCompileSpec spec = new DefaultTwirlCompileSpec(relativeFileCollector.relativeFiles, getOutputDirectory().get().getAsFile(), getDefaultImports().get(), userTemplateFormats.get(), additionalImports.get(), constructorAnnotations.get());

        workerExecutor.submit(TwirlCompileRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec, getCompiler());
            workerConfiguration.classpath(twirlCompilerClasspath);
            workerConfiguration.setDisplayName("Generating Scala source from Twirl templates");
        });
        workerExecutor.await();
    }

    private Compiler<TwirlCompileSpec> getCompiler() {
        return TwirlCompilerFactory.create(platform.get());
    }

    @Internal
    public Property<PlayPlatform> getPlatform() {
        return platform;
    }

    /**
     * Returns the custom template formats configured for this task.
     *
     * @return Custom template formats
     */
    @Input
    public ListProperty<TwirlTemplateFormat> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    /**
     * Returns the list of additional imports to add to the generated Scala code.
     *
     * @return List of additional imports
     */
    @Input
    public ListProperty<String> getAdditionalImports() {
        return additionalImports;
    }

    /**
     * Returns the list of constructor annotations to add to the generated Scala code.
     *
     * @return List of constructor annotations
     **/
    @Input
    public ListProperty<String> getConstructorAnnotations() {
        return constructorAnnotations;
    }

    private static class RelativeFileCollector implements FileVisitor {
        List<RelativeFile> relativeFiles = new ArrayList<>();

        @Override
        public void visitDir(FileVisitDetails dirDetails) {
        }

        @Override
        public void visitFile(FileVisitDetails fileDetails) {
            relativeFiles.add(new RelativeFile(fileDetails.getFile(), fileDetails.getRelativePath()));
        }
    }
}

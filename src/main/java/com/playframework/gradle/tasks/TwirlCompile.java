package com.playframework.gradle.tasks;

import com.playframework.gradle.platform.PlayPlatform;
import com.playframework.gradle.tools.twirl.DefaultTwirlCompileSpec;
import com.playframework.gradle.tools.twirl.DefaultTwirlTemplateFormat;
import com.playframework.gradle.tools.twirl.TwirlCompileSpec;
import com.playframework.gradle.tools.twirl.TwirlImports;
import com.playframework.gradle.tools.twirl.TwirlTemplateFormat;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
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
import java.util.Arrays;
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

    @Inject
    public TwirlCompile(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        outputDirectory = getProject().getObjects().directoryProperty();
        platform = getProject().getObjects().property(PlayPlatform.class);
        defaultImports = getProject().getObjects().property(TwirlImports.class);
        userTemplateFormats = getProject().getObjects().listProperty(TwirlTemplateFormat.class).empty();
        additionalImports = getProject().getObjects().listProperty(String.class);
        twirlCompilerClasspath = getProject().getLayout().configurableFiles();
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
    public Provider<Directory> getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Specifies the directory to generate the parser source files into.
     *
     * @param outputDirectory The output directory. Must not be null.
     */
    public void setOutputDirectory(Provider<Directory> outputDirectory) {
        this.outputDirectory.set(outputDirectory);
    }

    /**
     * Returns the default imports that will be used when compiling templates.
     * @return The imports that will be used.
     */
    @Nullable @Optional @Input
    public Provider<TwirlImports> getDefaultImports() {
        return defaultImports;
    }

    /**
     * Sets the default imports to be used when compiling templates.
     * @param defaultImports The imports to be used.
     */
    public void setDefaultImports(@Nullable Provider<TwirlImports> defaultImports) {
        this.defaultImports.set(defaultImports);
    }

    @Classpath
    @PathSensitive(PathSensitivity.RELATIVE)
    public FileCollection getTwirlCompilerClasspath() {
        return twirlCompilerClasspath;
    }

    public void setTwirlCompilerClasspath(FileCollection twirlCompilerClasspath) {
        this.twirlCompilerClasspath.setFrom(twirlCompilerClasspath);
    }

    @TaskAction
    void compile() {
        RelativeFileCollector relativeFileCollector = new RelativeFileCollector();
        getSource().visit(relativeFileCollector);
        final TwirlCompileSpec spec = new DefaultTwirlCompileSpec(relativeFileCollector.relativeFiles, getOutputDirectory().get().getAsFile(), getDefaultImports().get(), userTemplateFormats.get(), additionalImports.get());

        workerExecutor.submit(TwirlCompileRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec, platform.get());
            workerConfiguration.classpath(twirlCompilerClasspath);
            workerConfiguration.setDisplayName("Generating Scala source from Twirl templates");
        });
        workerExecutor.await();
    }

    public void setPlatform(Provider<PlayPlatform> platform) {
        this.platform.set(platform);
    }

    /**
     * Returns the custom template formats configured for this task.
     *
     * @return Custom template formats
     */
    @Input
    public Provider<List<TwirlTemplateFormat>> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    /**
     * Sets the custom template formats for this task.
     *
     * @param userTemplateFormats Custom template formats
     */
    public void setUserTemplateFormats(Provider<List<TwirlTemplateFormat>> userTemplateFormats) {
        this.userTemplateFormats.set(userTemplateFormats);
    }

    /**
     * Adds a custom template format.
     *
     * @param extension file extension this template applies to (e.g., {@code html}).
     * @param templateType fully-qualified type for this template format.
     * @param imports additional imports to add for the custom template format.
     */
    public void addUserTemplateFormat(final String extension, String templateType, String... imports) {
        userTemplateFormats.add(new DefaultTwirlTemplateFormat(extension, templateType, Arrays.asList(imports)));
    }

    /**
     * Returns the list of additional imports to add to the generated Scala code.
     *
     * @return List of additional imports
     */
    @Input
    public Provider<List<String>> getAdditionalImports() {
        return additionalImports;
    }

    /**
     * Sets the additional imports to add to all generated Scala code.
     *
     * @param additionalImports additional imports
     */
    public void setAdditionalImports(Provider<List<String>> additionalImports) {
        this.additionalImports.set(additionalImports);
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
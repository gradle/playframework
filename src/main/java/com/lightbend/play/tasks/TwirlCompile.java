package com.lightbend.play.tasks;

import com.lightbend.play.tools.Compiler;
import com.lightbend.play.tools.twirl.DefaultTwirlCompileSpec;
import com.lightbend.play.tools.twirl.DefaultTwirlTemplateFormat;
import com.lightbend.play.tools.twirl.TwirlCompileSpec;
import com.lightbend.play.tools.twirl.TwirlCompilerFactory;
import com.lightbend.play.tools.twirl.TwirlImports;
import com.lightbend.play.tools.twirl.TwirlTemplateFormat;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.util.CollectionUtils;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Task for compiling Twirl templates into Scala code.
 */
public class TwirlCompile extends SourceTask {

    private final WorkerExecutor workerExecutor;

    /**
     * Target directory for the compiled template files.
     */
    private File outputDirectory;

    /**
     * The default imports to use when compiling templates
     */
    private TwirlImports defaultImports;

    private BaseForkOptions forkOptions;
    private PlayPlatform platform;
    private List<TwirlTemplateFormat> userTemplateFormats = new ArrayList<>();
    private List<String> additionalImports = new ArrayList<>();

    @Inject
    public TwirlCompile(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
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
     * fork options for the twirl compiler.
     */
    @Nested
    public BaseForkOptions getForkOptions() {
        if (forkOptions == null) {
            forkOptions = new BaseForkOptions();
        }
        return forkOptions;
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

    @Input
    public Object getDependencyNotation() {
        return TwirlCompilerFactory.createAdapter(platform).getDependencyNotation();
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
     * Returns the default imports that will be used when compiling templates.
     * @return The imports that will be used.
     */
    @Nullable @Optional @Input
    public TwirlImports getDefaultImports() {
        return defaultImports;
    }

    /**
     * Sets the default imports to be used when compiling templates.
     * @param defaultImports The imports to be used.
     */
    public void setDefaultImports(@Nullable TwirlImports defaultImports) {
        this.defaultImports = defaultImports;
    }

    @TaskAction
    void compile() {
        RelativeFileCollector relativeFileCollector = new RelativeFileCollector();
        getSource().visit(relativeFileCollector);
        final TwirlCompileSpec spec = new DefaultTwirlCompileSpec(relativeFileCollector.relativeFiles, getOutputDirectory(), getForkOptions(), getDefaultImports(), userTemplateFormats, additionalImports);
        final Set<File> twirlClasspath = resolveToolClasspath(TwirlCompilerFactory.createAdapter(platform).getDependencyNotation().toArray()).resolve();

        workerExecutor.submit(TwirlCompileRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec, getCompiler());
            workerConfiguration.classpath(twirlClasspath);
            workerConfiguration.setDisplayName("Generating Scala source from Twirl templates");
        });
    }

    private Compiler<TwirlCompileSpec> getCompiler() {
        return TwirlCompilerFactory.create(platform);
    }

    private Configuration resolveToolClasspath(Object... dependencyNotations) {
        final DependencyHandler dependencyHandler = getProject().getDependencies();
        List<Dependency> dependencies = CollectionUtils.collect(dependencyNotations, dependencyNotation -> dependencyHandler.create(dependencyNotation));
        Dependency[] dependenciesArray = dependencies.toArray(new Dependency[0]);
        return getProject().getConfigurations().detachedConfiguration(dependenciesArray);
    }

    public void setPlatform(PlayPlatform platform) {
        this.platform = platform;
    }

    /**
     * Returns the custom template formats configured for this task.
     */
    @Input
    public List<TwirlTemplateFormat> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    /**
     * Sets the custom template formats for this task.
     */
    public void setUserTemplateFormats(List<TwirlTemplateFormat> userTemplateFormats) {
        this.userTemplateFormats = userTemplateFormats;
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
     */
    @Input
    public List<String> getAdditionalImports() {
        return additionalImports;
    }

    /**
     * Sets the additional imports to add to all generated Scala code.
     *
     * @param additionalImports additional imports
     */
    public void setAdditionalImports(List<String> additionalImports) {
        this.additionalImports = additionalImports;
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
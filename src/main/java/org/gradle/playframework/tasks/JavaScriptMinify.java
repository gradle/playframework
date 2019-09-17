package org.gradle.playframework.tasks;

import org.gradle.playframework.tasks.internal.JavaScriptMinifyRunnable;
import org.gradle.playframework.tools.internal.javascript.DefaultJavaScriptCompileSpec;
import org.gradle.playframework.tools.internal.javascript.JavaScriptCompileSpec;
import org.gradle.playframework.tools.internal.javascript.SimpleStaleClassCleaner;
import org.gradle.playframework.tools.internal.javascript.StaleClassCleaner;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Task to minify JavaScript assets.
 */
public class JavaScriptMinify extends SourceTask {
    private final WorkerExecutor workerExecutor;
    private final Property<Directory> destinationDir;
    private final ConfigurableFileCollection compilerClasspath;

    @Inject
    public JavaScriptMinify(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        this.include("**/*.js");
        this.destinationDir = getProject().getObjects().directoryProperty();
        this.compilerClasspath = getProject().files();
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
     * Returns the output directory that processed JavaScript is written to.
     *
     * @return The output directory.
     */
    @OutputDirectory
    public Property<Directory> getDestinationDir() {
        return destinationDir;
    }

    @Classpath
    public ConfigurableFileCollection getCompilerClasspath() {
        return compilerClasspath;
    }

    @TaskAction
    void compileJavaScriptSources() {
        StaleClassCleaner cleaner = new SimpleStaleClassCleaner(getOutputs());
        cleaner.addDirToClean(destinationDir.get().getAsFile());
        cleaner.execute();

        MinifyFileVisitor visitor = new MinifyFileVisitor();
        getSource().visit(visitor);

        JavaScriptCompileSpec spec = new DefaultJavaScriptCompileSpec(visitor.relativeFiles, destinationDir.get().getAsFile());

        workerExecutor.submit(JavaScriptMinifyRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec);
            workerConfiguration.classpath(compilerClasspath);
            workerConfiguration.setDisplayName("Minifying JavaScript source files");
        });
        workerExecutor.await();
    }

    /**
     * Copies each file in the source set to the output directory and gathers relative files for compilation
     */
    class MinifyFileVisitor implements FileVisitor {
        List<RelativeFile> relativeFiles = new ArrayList<>();

        @Override
        public void visitDir(FileVisitDetails dirDetails) {
            new File(destinationDir.get().getAsFile(), dirDetails.getRelativePath().getPathString()).mkdirs();
        }

        @Override
        public void visitFile(final FileVisitDetails fileDetails) {
            final File outputFileDir = new File(destinationDir.get().getAsFile(), fileDetails.getRelativePath().getParent().getPathString());

            // Copy the raw form
            FileOperations fileOperations = ((ProjectInternal) getProject()).getFileOperations();
            fileOperations.copy(copySpec -> copySpec.from(fileDetails.getFile()).into(outputFileDir));

            // Capture the relative file
            relativeFiles.add(new RelativeFile(fileDetails.getFile(), fileDetails.getRelativePath()));
        }
    }
}

package org.gradle.playframework.tasks;

import org.gradle.api.Action;
import org.gradle.api.file.*;
import org.gradle.playframework.tasks.internal.JavaScriptMinifyParameters;
import org.gradle.playframework.tasks.internal.JavaScriptMinifyRunnable;
import org.gradle.playframework.tasks.internal.JavaScriptMinifyWorkAction;
import org.gradle.playframework.tools.internal.javascript.DefaultJavaScriptCompileSpec;
import org.gradle.playframework.tools.internal.javascript.JavaScriptCompileSpec;
import org.gradle.playframework.tools.internal.javascript.SimpleStaleClassCleaner;
import org.gradle.playframework.tools.internal.javascript.StaleClassCleaner;
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
import org.gradle.util.GradleVersion;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.ProcessWorkerSpec;
import org.gradle.workers.WorkQueue;
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
    private final FileSystemOperations fileSystemOperations;

    @Inject
    public JavaScriptMinify(WorkerExecutor workerExecutor, FileSystemOperations fileSystemOperations) {
        this.workerExecutor = workerExecutor;
        this.fileSystemOperations = fileSystemOperations;
        this.include("/*.js");
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
    @SuppressWarnings("Convert2Lambda")
    void compileJavaScriptSources() {
        StaleClassCleaner cleaner = new SimpleStaleClassCleaner(getOutputs());
        cleaner.addDirToClean(destinationDir.get().getAsFile());
        cleaner.execute();

        MinifyFileVisitor visitor = new MinifyFileVisitor(fileSystemOperations);
        getSource().visit(visitor);

        JavaScriptCompileSpec spec = new DefaultJavaScriptCompileSpec(visitor.relativeFiles, destinationDir.get().getAsFile());

        WorkQueue workQueue = workerExecutor.processIsolation(new Action<ProcessWorkerSpec>() {
            @Override
            public void execute(ProcessWorkerSpec workerSpec) {
                workerSpec.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
                workerSpec.getClasspath().from(compilerClasspath);
            }
        });
        workQueue.submit(JavaScriptMinifyWorkAction.class, new Action<JavaScriptMinifyParameters>() {
            @Override
            public void execute(JavaScriptMinifyParameters parameters) {
                parameters.getSpec().set(spec);
            }
        });
    }

    /**
     * Copies each file in the source set to the output directory and gathers relative files for compilation
     */
    class MinifyFileVisitor implements FileVisitor {
        private final FileSystemOperations fileSystemOperations;
        List<RelativeFile> relativeFiles = new ArrayList<>();

        MinifyFileVisitor(FileSystemOperations fileSystemOperations) {
            this.fileSystemOperations = fileSystemOperations;
        }

        @Override
        public void visitDir(FileVisitDetails dirDetails) {
            new File(destinationDir.get().getAsFile(), dirDetails.getRelativePath().getPathString()).mkdirs();
        }

        @Override
        public void visitFile(final FileVisitDetails fileDetails) {
            final File outputFileDir = new File(destinationDir.get().getAsFile(), fileDetails.getRelativePath().getParent().getPathString());

            // Use FileSystemOperations to copy
            fileSystemOperations.copy(copySpec -> copySpec.from(fileDetails.getFile()).into(outputFileDir));

            // Capture the relative file
            relativeFiles.add(new RelativeFile(fileDetails.getFile(), fileDetails.getRelativePath()));
        }
    }
}

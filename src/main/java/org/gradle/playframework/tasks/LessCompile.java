package org.gradle.playframework.tasks;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.playframework.tasks.internal.LessCompileRunnable;
import org.gradle.playframework.tools.internal.less.DefaultLessCompileSpec;
import org.gradle.playframework.tools.internal.less.Less4jCompiler;
import org.gradle.playframework.tools.internal.less.LessCompileSpec;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LessCompile extends SourceTask {
    private final WorkerExecutor workerExecutor;
    private final Property<Directory> outputDirectory;
    private final ConfigurableFileCollection lessCompilerClasspath;
    private final List<File> lessCompilerIncludePaths;

    @Inject
    public LessCompile(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        this.outputDirectory = getProject().getObjects().directoryProperty();
        this.lessCompilerClasspath = getProject().files();
        this.lessCompilerIncludePaths = new ArrayList<>();
    }

    @Override
    @PathSensitive(PathSensitivity.RELATIVE)
    public FileTree getSource() {
        return super.getSource();
    }

    @OutputDirectory
    public Property<Directory> getOutputDirectory() {
        return outputDirectory;
    }

    @Classpath
    public ConfigurableFileCollection getLessCompilerClasspath() {
        return lessCompilerClasspath;
    }

    @InputFiles
    public List<File> getIncludePaths() {
        return lessCompilerIncludePaths;
    }

    @TaskAction
    void compile() {
        RelativeFileCollector relativeFileCollector = new RelativeFileCollector();
        getSource().visit(relativeFileCollector);
        final LessCompileSpec spec = new DefaultLessCompileSpec(relativeFileCollector.relativeFiles, getOutputDirectory().get().getAsFile(), getIncludePaths());

        workerExecutor.submit(LessCompileRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec, new Less4jCompiler());
            workerConfiguration.classpath(lessCompilerClasspath);
            workerConfiguration.setDisplayName("Generating CSS stylesheets from LESS stylesheets");
        });
        workerExecutor.await();
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

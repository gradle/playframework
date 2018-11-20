package com.lightbend.play.tasks;

import com.lightbend.play.javascript.DefaultJavaScriptCompileSpec;
import com.lightbend.play.javascript.JavaScriptCompileSpec;
import com.lightbend.play.toolchain.DefaultPlayToolChain;
import com.lightbend.play.toolchain.PlayToolChain;
import com.lightbend.play.toolchain.PlayToolChainInternal;
import com.lightbend.play.toolchain.ToolProvider;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.internal.fingerprint.classpath.ClasspathFingerprinter;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.process.internal.worker.WorkerProcessFactory;
import org.gradle.process.internal.worker.child.WorkerDirectoryProvider;
import org.gradle.workers.internal.WorkerDaemonFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaScriptMinify extends SourceTask {
    private File destinationDir;
    private PlayPlatform playPlatform;
    private BaseForkOptions forkOptions;

    public JavaScriptMinify() {
        this.include("**/*.js");
    }

    @Inject
    protected FileResolver getFileResolver() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected PathToFileResolver getPathToFileResolver() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected WorkerDaemonFactory getWorkerDaemonFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected WorkerProcessFactory getWorkerProcessFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected WorkerDirectoryProvider getWorkerDirectoryProvider() {
        throw new UnsupportedOperationException();
    }

    @Inject
    protected ClasspathFingerprinter getClasspathFingerprinter() {
        throw new UnsupportedOperationException();
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
    public File getDestinationDir() {
        return destinationDir;
    }

    /**
     * Sets the output directory where processed JavaScript should be written.
     *
     * @param destinationDir The output directory.
     */
    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    /**
     * Sets the target Play platform.
     *
     * @param playPlatform The target Play platform.
     */
    public void setPlayPlatform(PlayPlatform playPlatform) {
        this.playPlatform = playPlatform;
    }

    private Compiler<JavaScriptCompileSpec> getCompiler() {
        PlayToolChain playToolChain = new DefaultPlayToolChain(getPathToFileResolver(), getWorkerDaemonFactory(), getProject().getConfigurations(), getProject().getDependencies(), getWorkerProcessFactory(), getWorkerDirectoryProvider(), getClasspathFingerprinter());

        ToolProvider select = ((PlayToolChainInternal) playToolChain).select(playPlatform);
        return select.newCompiler(JavaScriptCompileSpec.class);
    }

    /**
     * The fork options to be applied to the JavaScript compiler.
     *
     * @return The fork options for the JavaScript compiler.
     */
    @Nested
    public BaseForkOptions getForkOptions() {
        if (forkOptions == null) {
            forkOptions = new BaseForkOptions();
        }
        return forkOptions;
    }

    @TaskAction
    void compileJavaScriptSources() {
        StaleClassCleaner cleaner = new SimpleStaleClassCleaner(getOutputs());
        cleaner.addDirToClean(getDestinationDir());
        cleaner.execute();

        MinifyFileVisitor visitor = new MinifyFileVisitor();
        getSource().visit(visitor);

        JavaScriptCompileSpec spec = new DefaultJavaScriptCompileSpec(visitor.relativeFiles, getDestinationDir(), getForkOptions());
        getCompiler().execute(spec);
    }

    /**
     * Copies each file in the source set to the output directory and gathers relative files for compilation
     */
    class MinifyFileVisitor implements FileVisitor {
        List<RelativeFile> relativeFiles = new ArrayList<>();

        @Override
        public void visitDir(FileVisitDetails dirDetails) {
            new File(destinationDir, dirDetails.getRelativePath().getPathString()).mkdirs();
        }

        @Override
        public void visitFile(final FileVisitDetails fileDetails) {
            final File outputFileDir = new File(destinationDir, fileDetails.getRelativePath().getParent().getPathString());

            // Copy the raw form
            FileOperations fileOperations = ((ProjectInternal) getProject()).getFileOperations();
            fileOperations.copy(copySpec -> copySpec.from(fileDetails.getFile()).into(outputFileDir));

            // Capture the relative file
            relativeFiles.add(new RelativeFile(fileDetails.getFile(), fileDetails.getRelativePath()));
        }
    }
}
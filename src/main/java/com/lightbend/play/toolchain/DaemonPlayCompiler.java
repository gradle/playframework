package com.lightbend.play.toolchain;

import com.lightbend.play.javascript.PlayCompileSpec;
import org.gradle.api.internal.tasks.compile.BaseForkOptionsConverter;
import org.gradle.api.internal.tasks.compile.daemon.AbstractDaemonCompiler;
import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.process.JavaForkOptions;
import org.gradle.workers.internal.DaemonForkOptions;
import org.gradle.workers.internal.DaemonForkOptionsBuilder;
import org.gradle.workers.internal.KeepAliveMode;
import org.gradle.workers.internal.WorkerDaemonFactory;

import java.io.File;

public class DaemonPlayCompiler<T extends PlayCompileSpec> extends AbstractDaemonCompiler<T> {
    private final Iterable<File> compilerClasspath;
    private final Iterable<String> classLoaderPackages;
    private final PathToFileResolver fileResolver;
    private final File daemonWorkingDir;

    public DaemonPlayCompiler(File daemonWorkingDir, Compiler<T> compiler, WorkerDaemonFactory workerDaemonFactory, Iterable<File> compilerClasspath, Iterable<String> classLoaderPackages, PathToFileResolver fileResolver) {
        super(compiler, workerDaemonFactory);
        this.compilerClasspath = compilerClasspath;
        this.classLoaderPackages = classLoaderPackages;
        this.fileResolver = fileResolver;
        this.daemonWorkingDir = daemonWorkingDir;
    }

    @Override
    protected DaemonForkOptions toDaemonForkOptions(PlayCompileSpec spec) {
        BaseForkOptions forkOptions = spec.getForkOptions();
        JavaForkOptions javaForkOptions = new BaseForkOptionsConverter(fileResolver).transform(forkOptions);
        javaForkOptions.setWorkingDir(daemonWorkingDir);

        return new DaemonForkOptionsBuilder(fileResolver)
                .javaForkOptions(javaForkOptions)
                .classpath(compilerClasspath)
                .sharedPackages(classLoaderPackages)
                .keepAliveMode(KeepAliveMode.SESSION)
                .build();
    }
}

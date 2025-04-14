package org.gradle.playframework.tasks;

import org.gradle.api.Incubating;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public class TwirlCompileExt extends TwirlCompile {
    @Inject
    public TwirlCompileExt(WorkerExecutor workerExecutor) {
        super(workerExecutor);
        this.javaLauncher = getProject().getObjects().property(JavaLauncher.class);
    }

    private final Property<JavaLauncher> javaLauncher;

    /**
     * The toolchain {@link JavaLauncher} to use for executing the twirl template compiler.
     *
     * @return the java launcher property
     */
    @Internal
    @Incubating
    public Property<JavaLauncher> getJavaLauncher() {
        return this.javaLauncher;
    }

    @Override
    protected String executable() {
        return getJavaLauncher().map(launcher -> launcher.getExecutablePath().getAsFile().getAbsolutePath()).getOrNull();
    }
}

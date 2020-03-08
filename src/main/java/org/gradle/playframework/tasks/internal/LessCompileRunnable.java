package org.gradle.playframework.tasks.internal;

import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.less.LessCompileSpec;
import org.gradle.util.GFileUtils;

import javax.inject.Inject;

public class LessCompileRunnable implements Runnable {

    private final LessCompileSpec lessCompileSpec;
    private final Compiler<LessCompileSpec> compiler;

    @Inject
    public LessCompileRunnable(LessCompileSpec lessCompileSpec, Compiler<LessCompileSpec> compiler) {
        this.lessCompileSpec = lessCompileSpec;
        this.compiler = compiler;
    }

    @Override
    public void run() {
        GFileUtils.forceDelete(lessCompileSpec.getDestinationDir());
        compiler.execute(lessCompileSpec);
    }
}

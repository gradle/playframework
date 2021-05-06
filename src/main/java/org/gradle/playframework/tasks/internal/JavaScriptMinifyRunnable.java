package org.gradle.playframework.tasks.internal;

import org.gradle.playframework.tools.internal.javascript.GoogleClosureCompiler;
import org.gradle.playframework.tools.internal.javascript.JavaScriptCompileSpec;

import javax.inject.Inject;

public class JavaScriptMinifyRunnable implements Runnable {

    private final JavaScriptCompileSpec spec;

    @Inject
    public JavaScriptMinifyRunnable(JavaScriptCompileSpec spec) {
        this.spec = spec;
    }

    @Override
    public void run() {
        new GoogleClosureCompiler().execute(spec);
    }
}

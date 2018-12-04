package com.lightbend.play.tasks;

import com.lightbend.play.tools.javascript.GoogleClosureCompiler;
import com.lightbend.play.tools.javascript.JavaScriptCompileSpec;

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

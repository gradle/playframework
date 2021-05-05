package org.gradle.playframework.tasks.internal;

import org.gradle.playframework.tools.internal.javascript.GoogleClosureCompiler;
import org.gradle.workers.WorkAction;


public abstract class JavaScriptMinifyWorkAction implements WorkAction<JavaScriptMinifyParameters> {

    @Override
    public void execute() {
        new GoogleClosureCompiler().execute(getParameters().getSpec().get());
    }
}

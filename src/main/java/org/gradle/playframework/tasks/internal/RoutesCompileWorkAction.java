package org.gradle.playframework.tasks.internal;

import org.gradle.workers.WorkAction;

public abstract class RoutesCompileWorkAction implements WorkAction<RoutesCompileParameters> {

    @Override
    public void execute() {
        // TODO: When dropping support for <5.6, remove the Runnable and fold its functionality into here
        new RoutesCompileRunnable(getParameters().getSpec().get(), getParameters().getCompiler().get()).run();
    }
}

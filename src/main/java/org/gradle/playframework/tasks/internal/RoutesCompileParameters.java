package org.gradle.playframework.tasks.internal;

import org.gradle.api.provider.Property;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.routes.RoutesCompileSpec;
import org.gradle.workers.WorkParameters;

public interface RoutesCompileParameters extends WorkParameters {
    Property<Compiler<RoutesCompileSpec>> getCompiler();
    Property<RoutesCompileSpec> getSpec();
}


package org.gradle.playframework.tasks.internal;

import org.gradle.api.provider.Property;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.twirl.TwirlCompileSpec;
import org.gradle.workers.WorkParameters;

public interface TwirlCompileParameters extends WorkParameters {
    Property<TwirlCompileSpec> getTwirlCompileSpec();
    Property<Compiler<TwirlCompileSpec>> getCompiler();
}

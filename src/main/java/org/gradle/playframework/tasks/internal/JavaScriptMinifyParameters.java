package org.gradle.playframework.tasks.internal;

import org.gradle.api.provider.Property;
import org.gradle.playframework.tools.internal.javascript.JavaScriptCompileSpec;
import org.gradle.workers.WorkParameters;

public interface JavaScriptMinifyParameters extends WorkParameters {
     Property<JavaScriptCompileSpec> getSpec();
}
